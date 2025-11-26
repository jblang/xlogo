#!/usr/bin/env python3
"""
Fix primitive documentation tables by converting <br> tags to separate rows.
"""

import os
import re
from pathlib import Path
from bs4 import BeautifulSoup, NavigableString

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

def convert_primitive_file(html_path, md_path):
    """Convert a primitive HTML file to Markdown with fixed tables."""
    with open(html_path, 'r', encoding='iso-8859-1') as f:
        html_content = f.read()

    soup = BeautifulSoup(html_content, 'html.parser')

    # Extract title
    title = ""
    band = soup.find('div', id='band')
    if band:
        tite = band.find('div', id='tite')
        if tite:
            h2 = tite.find('h2')
            if h2:
                title = h2.get_text().strip()

    # Extract main content
    main_content = soup.find('div', id='maco')
    if not main_content:
        print(f"No main content found in {html_path}")
        return

    # Build markdown
    markdown_parts = []

    # Add title
    if title:
        markdown_parts.append(f"# {title}\n")

    # Process content elements
    for elem in main_content.children:
        if not hasattr(elem, 'name'):
            continue

        if elem.name == 'br':
            continue

        elif elem.name == 'table':
            markdown_parts.append('\n' + table_to_markdown(elem) + '\n')

        elif elem.name == 'p':
            # Check if it's a subheading
            if 'subh' in elem.get('class', []):
                text = elem.get_text().strip()
                markdown_parts.append(f'\n{text}\n')
            else:
                text = extract_paragraph_text(elem)
                if text.strip():
                    markdown_parts.append(f'\n{text}\n')

        elif elem.name in ['h1', 'h2', 'h3', 'h4', 'h5', 'h6']:
            level = int(elem.name[1])
            text = elem.get_text().strip()
            if text:
                markdown_parts.append(f'\n{"#" * level} {text}\n')

        elif elem.name == 'ol':
            markdown_parts.append('\n')
            for i, li in enumerate(elem.find_all('li', recursive=False), 1):
                text = extract_paragraph_text(li)
                if text.strip():
                    markdown_parts.append(f'{i}. {text}\n')

        elif elem.name == 'ul':
            markdown_parts.append('\n')
            for li in elem.find_all('li', recursive=False):
                text = extract_paragraph_text(li)
                if text.strip():
                    markdown_parts.append(f'- {text}\n')

        elif elem.name == 'div' and 'codearea' in elem.get('class', []):
            markdown_parts.append('\n```logo\n')
            code_text = extract_code(elem)
            markdown_parts.append(code_text)
            markdown_parts.append('\n```\n')

    # Write markdown file
    markdown = ''.join(markdown_parts)
    with open(md_path, 'w', encoding='utf-8') as f:
        f.write(markdown)

    print(f"Converted {html_path.name} -> {md_path.name}")

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
            result.append('  \n')
        else:
            # Recursively process other elements
            if hasattr(child, 'children'):
                for grandchild in child.children:
                    process_child(grandchild)

    for child in elem.children:
        process_child(child)

    return ' '.join(result)

def extract_code(elem):
    """Extract code from codearea div."""
    lines = []
    for p in elem.find_all('p', recursive=False):
        # Remove span formatting but keep text
        for span in p.find_all('span'):
            span.unwrap()
        text_content = p.get_text()
        text_content = text_content.replace('\xa0', ' ')
        lines.append(text_content.strip())
    return '\n'.join(lines)

def main():
    """Convert all primitive files."""
    html_dir = Path('docs/logoarts/ipt/prim')
    md_dir = Path('docs/logoarts_md/primitives')

    if not html_dir.exists():
        print(f"HTML directory not found: {html_dir}")
        return

    # Process all HTML files
    for html_file in sorted(html_dir.glob('*.html')):
        md_file = md_dir / html_file.name.replace('.html', '.md')
        convert_primitive_file(html_file, md_file)

    print("\nAll primitive files updated!")

if __name__ == '__main__':
    main()
