#!/usr/bin/env python3
"""
Update markdown files to use flattened image paths.
"""

from pathlib import Path
import re

def load_path_mapping():
    """Load the path mapping from flatten_and_rename_images.py."""
    mapping = {}
    mapping_file = Path('docs/logoarts_md/art/path_mapping.txt')

    with open(mapping_file, 'r') as f:
        for line in f:
            old_path, new_path = line.strip().split('\t')
            # Normalize slashes to forward slashes for comparison
            old_path = old_path.replace('\\', '/')
            new_path = new_path.replace('\\', '/')
            mapping[old_path] = new_path

    return mapping

def convert_path(old_path, mapping):
    """Convert a markdown image path from gallery format to flattened format."""
    # Extract the ../ prefix and the art path separately
    prefix_match = re.match(r'((?:\.\./)+)(art/.*)', old_path)
    if not prefix_match:
        return old_path

    prefix = prefix_match.group(1)
    art_path = prefix_match.group(2)

    # Try to find in mapping
    # First, try extracting just the gallery part (e.g., art/400g4/chaos.png -> 400g4/chaos.png)
    art_match = re.match(r'art/(400g\d+/.*)', art_path)
    if art_match:
        gallery_rel_path = art_match.group(1)
        if gallery_rel_path in mapping:
            new_art_path = mapping[gallery_rel_path]
            # Strip 'art/' prefix and add back the ../../art/
            new_relative = new_art_path.replace('art/', '')
            return f"{prefix}art/{new_relative}"

    # No mapping found, return original
    return old_path

def update_markdown_file(md_path, mapping):
    """Update image paths in a markdown file."""
    with open(md_path, 'r', encoding='utf-8') as f:
        content = f.read()

    original_content = content

    # Find and replace all image references
    def replace_image(match):
        alt_text = match.group(1)
        img_path = match.group(2)
        new_path = convert_path(img_path, mapping)
        return f'![{alt_text}]({new_path})'

    content = re.sub(r'!\[(.*?)\]\((.*?)\)', replace_image, content)

    if content != original_content:
        with open(md_path, 'w', encoding='utf-8') as f:
            f.write(content)
        return True

    return False

def main():
    """Update all program markdown files."""
    mapping = load_path_mapping()
    print(f"Loaded {len(mapping)} path mappings\n")

    md_base = Path('docs/logoarts_md/programs')
    updated_count = 0

    for md_file in md_base.rglob('*.md'):
        if md_file.name == 'index.md':
            continue

        if update_markdown_file(md_file, mapping):
            updated_count += 1
            print(f"Updated {md_file.relative_to(md_base)}")

    print(f"\nTotal files updated: {updated_count}")

if __name__ == '__main__':
    main()