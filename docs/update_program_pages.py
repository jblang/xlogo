#!/usr/bin/env python3
"""
Update program pages to fix paragraph indentation and convert remaining HTML.
"""

import os
import re
from pathlib import Path
from bs4 import BeautifulSoup, NavigableString
from html import unescape

def extract_paragraph_text(elem):
    """Extract text from paragraph-like elements, preserving formatting."""
    result = []

    def process_child(child):
        if isinstance(child, NavigableString):
            # Clean up excessive whitespace - replace newlines, tabs, and multiple spaces
            text = str(child)
            text = re.sub(r'\s+', ' ', text).strip()
            if text:
                result.append(text)
        elif child.name == 'strong' or child.name == 'b':
            # Get text and clean up whitespace
            text = ' '.join(child.stripped_strings)
            text = re.sub(r'\s+', ' ', text).strip()
            if text:
                result.append(f'**{text}**')
        elif child.name == 'em' or child.name == 'i':
            # Get text and clean up whitespace
            text = ' '.join(child.stripped_strings)
            text = re.sub(r'\s+', ' ', text).strip()
            if text:
                result.append(f'*{text}*')
        elif child.name == 'code':
            # Get text and clean up whitespace
            text = ' '.join(child.stripped_strings)
            text = re.sub(r'\s+', ' ', text).strip()
            if text:
                result.append(f'`{text}`')
        elif child.name == 'a':
            href = child.get('href', '')
            content = ''.join(child.stripped_strings)
            if href and content:
                if href.endswith('.html'):
                    href = href.replace('.html', '.md')
                result.append(f'[{content}]({href})')
            elif content:
                result.append(content)
        elif child.name == 'br':
            result.append('\n')
        else:
            # Recursively process other elements
            if hasattr(child, 'children'):
                for grandchild in child.children:
                    process_child(grandchild)

    for child in elem.children:
        process_child(child)

    return ''.join(result)

def extract_cell_lines(cell):
    """Extract lines from a table cell, splitting by <br> tags."""
    lines = []
    current_line = []

    def process_node(node, in_formatting=None):
        if isinstance(node, NavigableString):
            # Clean up whitespace - replace newlines, tabs, and multiple spaces with single space
            text = str(node)
            text = re.sub(r'\s+', ' ', text).strip()
            if text:
                if in_formatting:
                    current_line.append(f'{in_formatting[0]}{text}{in_formatting[1]}')
                else:
                    current_line.append(text)
        elif node.name == 'br':
            # Finish current line and start new one
            line_text = ' '.join(current_line).strip()
            if line_text or lines:  # Keep empty lines if not the first
                lines.append(line_text)
            current_line.clear()
        elif node.name == 'strong' or node.name == 'b':
            # Process children with bold formatting
            for child in node.children:
                process_node(child, in_formatting=('**', '**'))
        elif node.name == 'em' or node.name == 'i':
            # Process children with italic formatting
            for child in node.children:
                process_node(child, in_formatting=('*', '*'))
        elif node.name == 'code':
            # Process children with code formatting
            for child in node.children:
                process_node(child, in_formatting=('`', '`'))
        elif node.name == 'a':
            href = node.get('href', '')
            content = ''.join(node.stripped_strings)
            if href and content:
                if href.endswith('.html'):
                    href = href.replace('.html', '.md')
                current_line.append(f'[{content}]({href})')
            elif content:
                current_line.append(content)
        else:
            # Process children
            for child in node.children:
                process_node(child, in_formatting)

    # Process all children of the cell
    for child in cell.children:
        process_node(child)

    # Add any remaining content as the last line
    if current_line:
        lines.append(' '.join(current_line).strip())

    return lines if lines else ['']

def table_to_markdown(table):
    """Convert HTML table to Markdown, splitting <br> into separate rows."""
    markdown_rows = []
    has_header = False

    for tr_index, tr in enumerate(table.find_all('tr', recursive=False)):
        cells = tr.find_all(['td', 'th'], recursive=False)
        if not cells:
            continue

        # Check if this is a header row
        is_header = cells[0].name == 'th'

        # Extract lines from each cell
        cell_lines_list = []
        max_lines = 0

        for cell in cells:
            lines = extract_cell_lines(cell)
            cell_lines_list.append(lines)
            max_lines = max(max_lines, len(lines))

        # Pad all cells to have the same number of lines
        for cell_lines in cell_lines_list:
            while len(cell_lines) < max_lines:
                cell_lines.append('')

        # Create markdown rows
        for line_index in range(max_lines):
            row_cells = []
            for cell_lines in cell_lines_list:
                content = cell_lines[line_index]
                # Escape pipes in content
                content = content.replace('|', '\\|')
                row_cells.append(content)

            markdown_rows.append('| ' + ' | '.join(row_cells) + ' |')

        # Add separator after header
        if is_header and not has_header:
            markdown_rows.append('| ' + ' | '.join(['---'] * len(cells)) + ' |')
            has_header = True

    return '\n'.join(markdown_rows) if markdown_rows else ''

def update_program_file(md_path):
    """Update a program markdown file to fix formatting issues."""
    with open(md_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Split into sections
    lines = content.split('\n')
    updated_lines = []
    in_code_block = False

    for line in lines:
        # Track code blocks
        if line.strip().startswith('```'):
            in_code_block = not in_code_block
            updated_lines.append(line)
            continue

        # Don't process lines in code blocks
        if in_code_block:
            updated_lines.append(line)
            continue

        # Convert remaining <br> tags to newlines
        if '<br>' in line:
            line = line.replace('<br>', '\n')

        # Remove excessive leading whitespace from non-code lines
        if line.strip() and not line.startswith('#') and not line.startswith('!') and not line.startswith('|'):
            # Clean up excessive indentation
            line = re.sub(r'^\s+', '', line)

        updated_lines.append(line)

    # Write back
    updated_content = '\n'.join(updated_lines)
    with open(md_path, 'w', encoding='utf-8') as f:
        f.write(updated_content)

    return md_path.name

def main():
    """Update all program files."""
    md_dir = Path('docs/logoarts_md/programs')

    if not md_dir.exists():
        print(f"Directory not found: {md_dir}")
        return

    # Process all markdown files recursively
    count = 0
    for md_file in md_dir.rglob('*.md'):
        if md_file.name == 'index.md':
            continue
        update_program_file(md_file)
        count += 1
        print(f"Updated {md_file.relative_to(md_dir)}")

    print(f"\nTotal files updated: {count}")

if __name__ == '__main__':
    main()