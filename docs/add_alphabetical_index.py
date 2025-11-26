#!/usr/bin/env python3
"""
Extract and convert the alphabetical primitives index to Markdown.
"""

from bs4 import BeautifulSoup
from pathlib import Path
import re

# Read the original HTML
html_file = Path(__file__).parent / 'logoarts' / 'ipt' / 'top' / 'prim.html'
with open(html_file, 'r', encoding='iso-8859-1') as f:
    soup = BeautifulSoup(f.read(), 'html.parser')

# Find the table with the alphabetical index
table = soup.find('table', id='primtext')

if table:
    # Get the header row
    headers = []
    header_row = table.find('tr')
    for th in header_row.find_all('th'):
        headers.append(th.get_text().strip())

    # Get the content row
    content_row = table.find_all('tr')[1]
    columns = content_row.find_all('td')

    # Parse each column
    all_columns = []
    for td in columns:
        items = []
        # Get all <a> tags
        for link in td.find_all('a'):
            name = link.get_text()
            href = link.get('href', '')

            # Clean up the name - remove nested spans but keep the shortform text
            name_clean = []
            for child in link.children:
                if isinstance(child, str):
                    name_clean.append(child)
                elif child.name == 'span' and 'sf' in child.get('class', []):
                    # This is a shortform - keep it in italics
                    name_clean.append(f" *{child.get_text()}*")
                else:
                    name_clean.append(child.get_text())

            name = ''.join(name_clean).strip()

            # Convert href to markdown filename
            if href:
                href = href.replace('../prim/', '').replace('.html', '.md')

            # Check if it's in parentheses (text style or note name)
            if name.startswith('(') and name.endswith(')'):
                items.append(f"*{name}*")
            elif href:
                items.append(f"[{name}]({href})")
            else:
                items.append(name)

        all_columns.append(items)

    # Now create markdown table
    md_lines = []
    md_lines.append("\n## Alphabetical Index\n")
    md_lines.append("Complete list of all XLogo primitives and their alternative shortforms.\n\n")

    # Create table header
    md_lines.append("| A-E | F-M | N-R | S | T-Z |\n")
    md_lines.append("|-----|-----|-----|---|-----|\n")

    # Find the maximum number of rows needed
    max_rows = max(len(col) for col in all_columns)

    # Create table rows
    for i in range(max_rows):
        row = []
        for col in all_columns:
            if i < len(col):
                row.append(col[i])
            else:
                row.append("")  # Empty cell if column is shorter
        md_lines.append("| " + " | ".join(row) + " |\n")

    md_lines.append("\n**Note:** Alternative primitive shortforms are shown in *italics*. Words in *parentheses* are text styles or sound note names. `?` can be written as `p` (e.g., Mouse? = MouseP).\n")

    # Read current index file
    index_file = Path(__file__).parent / 'logoarts_md' / 'primitives' / 'index.md'
    with open(index_file, 'r', encoding='utf-8') as f:
        current_content = f.read()

    # Remove any existing alphabetical index section
    if '## Alphabetical Index' in current_content:
        # Keep everything before the alphabetical index
        current_content = current_content.split('## Alphabetical Index')[0].rstrip() + '\n'

    # Append the alphabetical index
    with open(index_file, 'w', encoding='utf-8') as f:
        f.write(current_content)
        f.write(''.join(md_lines))

    print(f"Added alphabetical index to {index_file}")
    print(f"Total primitives indexed: {sum(len(col) for col in all_columns)}")
