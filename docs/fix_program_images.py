#!/usr/bin/env python3
"""
Update program pages to use full-size images, matching actual files.
"""

from pathlib import Path
import re

def find_full_size_image(thumbnail_path, art_dir):
    """
    Find the full-size version of a thumbnail image.
    thumbnail_path format: ../../art/100g4/chaos.gif
    """
    # Parse the thumbnail path
    match = re.search(r'/(\d+)g(\d+)/(.+)$', thumbnail_path)
    if not match:
        return None

    size, gallery, filename = match.groups()
    basename = Path(filename).stem

    # Try 400g version with same extension
    full_size_dir = art_dir / f'400g{gallery}'
    if not full_size_dir.exists():
        return None

    # Try original extension
    original_ext = Path(filename).suffix
    full_size_file = full_size_dir / filename
    if full_size_file.exists():
        return f'../../art/400g{gallery}/{filename}'

    # Try .png extension
    png_file = full_size_dir / f'{basename}.png'
    if png_file.exists():
        return f'../../art/400g{gallery}/{basename}.png'

    # Try .gif extension
    gif_file = full_size_dir / f'{basename}.gif'
    if gif_file.exists():
        return f'../../art/400g{gallery}/{basename}.gif'

    # Try .jpg extension
    jpg_file = full_size_dir / f'{basename}.jpg'
    if jpg_file.exists():
        return f'../../art/400g{gallery}/{basename}.jpg'

    return None

def update_program_page(md_file, art_dir):
    """Update image references in a program page."""
    with open(md_file, 'r', encoding='utf-8') as f:
        content = f.read()

    # Find image references
    pattern = r'!\[([^\]]*)\]\((../../art/\d+g\d+/[^\)]+)\)'

    def replace_image(match):
        alt_text = match.group(1)
        old_path = match.group(2)

        # Find the full-size version
        new_path = find_full_size_image(old_path, art_dir)

        if new_path:
            return f'![{alt_text}]({new_path})'
        else:
            # Keep original if no full-size found
            return match.group(0)

    updated = re.sub(pattern, replace_image, content)

    # Write if changed
    if updated != content:
        with open(md_file, 'w', encoding='utf-8') as f:
            f.write(updated)
        return True
    return False

def main():
    base_dir = Path(__file__).parent / 'logoarts_md'
    programs_dir = base_dir / 'programs'
    art_dir = base_dir / 'art'

    updated_count = 0
    missing_count = 0
    missing_files = []

    # Process all program markdown files
    for category_dir in programs_dir.iterdir():
        if not category_dir.is_dir() or category_dir.name.startswith('.'):
            continue

        for md_file in category_dir.glob('*.md'):
            if md_file.name == 'index.md':
                continue

            if update_program_page(md_file, art_dir):
                updated_count += 1
                print(f"Updated: {md_file.relative_to(programs_dir)}")

    print(f"\nTotal files updated: {updated_count}")

if __name__ == '__main__':
    main()
