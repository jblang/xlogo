#!/usr/bin/env python3
"""
Remove extraneous links at the bottom of program markdown files.
"""

import re
from pathlib import Path

def remove_bottom_links(md_path):
    """Remove Gallery and instruction links at the bottom of program files."""
    with open(md_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Track if we made changes
    original_content = content

    # Remove lines with Gallery or instruction links
    # These typically appear at the bottom as bullet points
    lines = content.split('\n')
    filtered_lines = []

    for i, line in enumerate(lines):
        # Skip lines that are just links to Gallery, instructions, files, etc.
        if re.match(r'^\s*-\s*\[Gallery\]', line):
            continue
        if re.match(r'^\s*-\s*\[\?\]', line):
            continue
        if re.match(r'^\s*-\s*Main Command:', line):
            continue
        if re.match(r'^\s*-\s*\[On-Line\]', line):
            continue
        if re.match(r'^\s*-\s*\[File\]', line):
            continue

        filtered_lines.append(line)

    # Remove trailing empty lines
    while filtered_lines and not filtered_lines[-1].strip():
        filtered_lines.pop()

    content = '\n'.join(filtered_lines) + '\n'

    # Only write if changed
    if content != original_content:
        with open(md_path, 'w', encoding='utf-8') as f:
            f.write(content)
        return True

    return False

def main():
    """Remove links from all program files."""
    md_dir = Path('docs/logoarts_md/programs')

    if not md_dir.exists():
        print(f"Directory not found: {md_dir}")
        return

    # Process all markdown files recursively
    count = 0
    for md_file in md_dir.rglob('*.md'):
        if md_file.name == 'index.md':
            continue
        if remove_bottom_links(md_file):
            count += 1
            print(f"Updated {md_file.relative_to(md_dir)}")

    print(f"\nTotal files updated: {count}")

if __name__ == '__main__':
    main()