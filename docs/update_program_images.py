#!/usr/bin/env python3
"""
Update all program pages to use full-size (400x400) images instead of thumbnails (100x100).
"""

from pathlib import Path
import re

def update_image_size(md_file):
    """Update image references from 100g to 400g."""
    with open(md_file, 'r', encoding='utf-8') as f:
        content = f.read()

    # Replace 100g with 400g in image paths
    updated = re.sub(r'/100g(\d+)/', r'/400g\1/', content)

    # Check if any changes were made
    if updated != content:
        with open(md_file, 'w', encoding='utf-8') as f:
            f.write(updated)
        return True
    return False

def main():
    programs_dir = Path(__file__).parent / 'logoarts_md' / 'programs'

    updated_count = 0

    # Process all markdown files in category directories
    for category_dir in programs_dir.iterdir():
        if not category_dir.is_dir() or category_dir.name.startswith('.'):
            continue

        for md_file in category_dir.glob('*.md'):
            if md_file.name == 'index.md':
                continue

            if update_image_size(md_file):
                updated_count += 1
                print(f"Updated: {md_file.relative_to(programs_dir)}")

    print(f"\nTotal files updated: {updated_count}")

if __name__ == '__main__':
    main()
