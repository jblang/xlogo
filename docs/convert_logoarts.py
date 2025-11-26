#!/usr/bin/env python3
"""
Convert logoarts HTML files to Markdown format.
- Removes website chrome (headers, navigation, footers)
- Extracts main content
- Converts to Markdown
- Reorganizes structure with proper indexes
"""

import os
import re
from pathlib import Path
from bs4 import BeautifulSoup
from html import unescape

def html_to_markdown(soup, base_path=""):
    """Convert BeautifulSoup HTML to Markdown."""
    text = []

    def process_element(elem, indent=0):
        if elem.name == 'p':
            # Process paragraph
            content = extract_text(elem)
            if content.strip():
                text.append('\n' + content.strip() + '\n')

        elif elem.name in ['h1', 'h2', 'h3', 'h4', 'h5', 'h6']:
            level = int(elem.name[1])
            content = extract_text(elem)
            if content.strip():
                text.append('\n' + '#' * level + ' ' + content.strip() + '\n')

        elif elem.name == 'ul':
            text.append('\n')
            for li in elem.find_all('li', recursive=False):
                content = extract_text(li)
                if content.strip():
                    text.append('  ' * indent + '- ' + content.strip() + '\n')

        elif elem.name == 'ol':
            text.append('\n')
            for i, li in enumerate(elem.find_all('li', recursive=False), 1):
                content = extract_text(li)
                if content.strip():
                    text.append('  ' * indent + f'{i}. ' + content.strip() + '\n')

        elif elem.name == 'blockquote':
            text.append('\n')
            for child in elem.children:
                if hasattr(child, 'name'):
                    if child.name == 'p':
                        content = extract_text(child)
                        if content.strip():
                            text.append('> ' + content.strip() + '\n')

        elif elem.name == 'table':
            text.append('\n')
            text.append(table_to_markdown(elem))
            text.append('\n')

        elif elem.name == 'img':
            alt = elem.get('alt', '')
            src = elem.get('src', '')
            # Adjust image path
            if src and not any(x in src for x in ['logoicon', 'xlogo.gif', 'blob', 'arrl', 'cr31']):
                # Remove ../ prefixes and adjust path (skip navigation icons)
                src = re.sub(r'^(\.\./)+', '', src)
                text.append(f'![{alt}]({src})')

        elif elem.name == 'a':
            href = elem.get('href', '')
            content = extract_text(elem)
            if href and not href.startswith('#'):
                # Convert HTML links to MD links
                if href.endswith('.html'):
                    href = href.replace('.html', '.md')
                text.append(f'[{content}]({href})')
            else:
                text.append(content)

        elif elem.name == 'br':
            text.append('  \n')

        elif elem.name == 'div' and 'codearea' in elem.get('class', []):
            # Handle code blocks
            text.append('\n```logo\n')
            code_text = extract_code(elem)
            text.append(code_text)
            text.append('\n```\n')

        else:
            # Process children
            if hasattr(elem, 'children'):
                for child in elem.children:
                    if hasattr(child, 'name'):
                        process_element(child, indent)

    def extract_text(elem):
        """Extract text from element, handling links and formatting."""
        result = []
        for child in elem.children:
            if isinstance(child, str):
                result.append(child)
            elif child.name == 'a':
                href = child.get('href', '')
                content = ''.join(child.stripped_strings)
                if href and not href.startswith('#') and content:
                    if href.endswith('.html'):
                        href = href.replace('.html', '.md')
                    result.append(f'[{content}]({href})')
                else:
                    result.append(content)
            elif child.name == 'img':
                alt = child.get('alt', '')
                src = child.get('src', '')
                if src:
                    src = src.replace('../', '')
                    result.append(f'![{alt}]({src})')
            elif child.name == 'strong' or child.name == 'b':
                content = ''.join(child.stripped_strings)
                if content:
                    result.append(f'**{content}**')
            elif child.name == 'em' or child.name == 'i':
                content = ''.join(child.stripped_strings)
                if content:
                    result.append(f'*{content}*')
            elif child.name == 'code':
                content = ''.join(child.stripped_strings)
                if content:
                    result.append(f'`{content}`')
            elif child.name == 'span':
                # Handle span classes for code highlighting
                classes = child.get('class', [])
                content = ''.join(child.stripped_strings)
                if content:
                    result.append(content)
            elif child.name == 'br':
                result.append('  \n')
            else:
                result.append(extract_text(child))
        return ''.join(result)

    def extract_code(elem):
        """Extract code from codearea div, preserving formatting."""
        lines = []
        for p in elem.find_all('p', recursive=False):
            # Remove span formatting but keep text
            for span in p.find_all('span'):
                span.unwrap()
            # Get text with line breaks preserved
            text_content = p.get_text()
            # Clean up but preserve indentation
            text_content = text_content.replace('\xa0', ' ')
            text_content = unescape(text_content)
            # Remove excessive whitespace but keep indentation
            text_content = re.sub(r'\n\s*\n', '\n', text_content)
            text_content = re.sub(r'[ \t]+', ' ', text_content)
            lines.append(text_content.strip())
        return '\n'.join(lines)

    def table_to_markdown(table):
        """Convert HTML table to Markdown table."""
        rows = []
        has_header = False

        for tr in table.find_all('tr', recursive=False):
            cells = []
            has_th = False

            for cell in tr.find_all(['td', 'th'], recursive=False):
                # Extract text without nested tables
                content_parts = []
                for child in cell.children:
                    if hasattr(child, 'name'):
                        if child.name == 'table':
                            # Skip nested tables
                            continue
                        elif child.name == 'br':
                            content_parts.append('<br>')
                        else:
                            text = ''.join(child.stripped_strings)
                            if text:
                                content_parts.append(text)
                    elif isinstance(child, str):
                        stripped = child.strip()
                        if stripped:
                            content_parts.append(stripped)

                content = ' '.join(content_parts).strip()
                content = content.replace('|', '\\|')  # Escape pipes
                cells.append(content)

                if cell.name == 'th':
                    has_th = True

            if cells:
                rows.append('| ' + ' | '.join(cells) + ' |')
                if has_th and not has_header:
                    # Add separator after header
                    rows.append('| ' + ' | '.join(['---'] * len(cells)) + ' |')
                    has_header = True

        return '\n'.join(rows) if rows else ''

    # Process the soup
    for elem in soup.children:
        if hasattr(elem, 'name'):
            process_element(elem)

    return ''.join(text)

def extract_main_content(html_content, file_type='program'):
    """Extract main content from HTML, removing chrome."""
    soup = BeautifulSoup(html_content, 'html.parser')

    # Find the main content area (id="maco")
    main_content = soup.find('div', id='maco')

    if not main_content:
        return None, None, None

    # Extract title from band section
    title = ""
    subtitle = ""
    band = soup.find('div', id='band')
    if band:
        tite = band.find('div', id='tite')
        if tite:
            h2 = tite.find('h2')
            if h2:
                title = h2.get_text().strip()
            p = tite.find('p')
            if p:
                subtitle = p.get_text().strip()

    # Extract thumbnail image from rico section if it's a program
    thumbnail = None
    if file_type == 'program':
        rico = soup.find('div', id='rico')
        if rico:
            img = rico.find('img', src=lambda x: x and '/100g' in x if x else False)
            if img:
                thumbnail = img.get('src', '').replace('../', '')

    # Remove button div with online/file/gallery links
    button_div = main_content.find('div', class_='button')
    if button_div:
        button_div.decompose()

    return main_content, title, thumbnail

def convert_program_file(input_path, output_path):
    """Convert a program HTML file to Markdown."""
    with open(input_path, 'r', encoding='iso-8859-1') as f:
        html_content = f.read()

    main_content, title, thumbnail = extract_main_content(html_content, 'program')

    if not main_content:
        print(f"Warning: Could not extract content from {input_path}")
        return None

    # Convert to markdown
    markdown = html_to_markdown(main_content)

    # Build final markdown with title and thumbnail
    final_md = []
    if title:
        final_md.append(f"# {title}\n")

    if thumbnail:
        # Add thumbnail at the top with relative path
        final_md.append(f"![{title}](../../{thumbnail})\n")

    # Fix image paths in markdown content to be relative
    markdown = re.sub(r'!\[(.*?)\]\(art/', r'![\1](../../art/', markdown)
    markdown = re.sub(r'!\[(.*?)\]\(pict/', r'![\1](../../pict/', markdown)

    final_md.append(markdown)

    # Write output
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(''.join(final_md))

    return {
        'title': title,
        'thumbnail': thumbnail,
        'path': output_path
    }

def convert_info_file(input_path, output_path):
    """Convert an info/resource HTML file to Markdown."""
    with open(input_path, 'r', encoding='iso-8859-1') as f:
        html_content = f.read()

    main_content, title, _ = extract_main_content(html_content, 'info')

    if not main_content:
        print(f"Warning: Could not extract content from {input_path}")
        return None

    # Convert to markdown
    markdown = html_to_markdown(main_content)

    # Build final markdown with title
    final_md = []
    if title:
        final_md.append(f"# {title}\n")

    final_md.append(markdown)

    # Write output
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(''.join(final_md))

    return {
        'title': title,
        'path': output_path
    }

def create_index(category, items, output_path, is_subcategory=False):
    """Create an index file for a category."""
    md = [f"# {category}\n\n"]

    for item in items:
        title = item.get('title', item['path'].stem)
        rel_path = item['path'].name

        if 'thumbnail' in item and item['thumbnail']:
            # Add thumbnail with relative path
            thumb = item['thumbnail']
            if is_subcategory:
                # For category indexes (programs/anim/index.md)
                thumb = f"../../{thumb}"
            md.append(f"## [{title}]({rel_path})\n\n")
            md.append(f"[![{title}]({thumb})]({rel_path})\n\n")
        else:
            md.append(f"- [{title}]({rel_path})\n")

    output_path.parent.mkdir(parents=True, exist_ok=True)
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(''.join(md))

def main():
    """Main conversion function."""
    base_dir = Path(__file__).parent / 'logoarts'
    output_dir = Path(__file__).parent / 'logoarts_md'

    # Create output directories
    programs_dir = output_dir / 'programs'
    resources_dir = output_dir / 'resources'
    primitives_dir = output_dir / 'primitives'

    # Convert programs
    print("Converting programs...")
    prog_categories = {}

    for category_dir in (base_dir / 'prog').iterdir():
        if not category_dir.is_dir():
            continue

        category_name = category_dir.name
        print(f"  Processing category: {category_name}")

        category_items = []
        category_output_dir = programs_dir / category_name

        for html_file in category_dir.glob('*.html'):
            output_file = category_output_dir / (html_file.stem + '.md')
            result = convert_program_file(html_file, output_file)
            if result:
                category_items.append(result)
                print(f"    Converted: {html_file.name}")

        if category_items:
            # Create category index
            index_path = category_output_dir / 'index.md'
            create_index(category_name.replace('_', ' ').title(), category_items, index_path, is_subcategory=True)
            prog_categories[category_name] = category_items

    # Create main programs index
    print("\nCreating programs index...")
    main_prog_index = [f"# XLogo Programs\n\n"]
    for category_name, items in sorted(prog_categories.items()):
        display_name = category_name.replace('_', ' ').title()
        main_prog_index.append(f"## [{display_name}]({category_name}/index.md)\n\n")

        # Show thumbnails for first few items
        for item in items[:4]:
            if item.get('thumbnail'):
                rel_path = f"{category_name}/{item['path'].name}"
                thumb = f"../{item['thumbnail']}"  # Relative to programs/index.md
                title = item.get('title', '')
                main_prog_index.append(f"[![{title}]({thumb})]({rel_path}) ")
        main_prog_index.append("\n\n")

    with open(programs_dir / 'index.md', 'w', encoding='utf-8') as f:
        f.write(''.join(main_prog_index))

    # Convert info/resources
    print("\nConverting resources...")
    resource_items = []

    for html_file in (base_dir / 'ipt' / 'info').glob('*.html'):
        output_file = resources_dir / (html_file.stem + '.md')
        result = convert_info_file(html_file, output_file)
        if result:
            resource_items.append(result)
            print(f"  Converted: {html_file.name}")

    # Create resources index
    if resource_items:
        index_path = resources_dir / 'index.md'
        create_index("Resources", resource_items, index_path)

    # Convert primitives
    print("\nConverting primitives...")
    primitive_items = []

    for html_file in (base_dir / 'ipt' / 'prim').glob('*.html'):
        output_file = primitives_dir / (html_file.stem + '.md')
        result = convert_info_file(html_file, output_file)
        if result:
            primitive_items.append(result)
            print(f"  Converted: {html_file.name}")

    # Create primitives index
    if primitive_items:
        index_path = primitives_dir / 'index.md'
        create_index("XLogo Primitives Reference", primitive_items, index_path)

    # Create main index
    print("\nCreating main index...")
    main_index = [
        "# XLogo Arts\n\n",
        "Welcome to Logo programming using XLogo.\n\n",
        "## Sections\n\n",
        f"- [Programs](programs/index.md) - Complete working programs written in XLogo\n",
        f"- [Primitives](primitives/index.md) - Complete list of all XLogo commands with examples\n",
        f"- [Resources](resources/index.md) - Further information and resources on XLogo coding\n",
    ]

    with open(output_dir / 'index.md', 'w', encoding='utf-8') as f:
        f.write(''.join(main_index))

    print(f"\nConversion complete! Output in: {output_dir}")

if __name__ == '__main__':
    main()