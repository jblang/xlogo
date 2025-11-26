#!/usr/bin/env python3
"""Fix markdown index files to use correct thumbnails from HTML source."""

import re
from pathlib import Path
from bs4 import BeautifulSoup

# Category mapping
html_to_md = {
    'anim.html': 'anim',
    'art.html': 'art',
    'ca.html': 'ca',
    'coding.html': 'coding',
    'demo.html': 'demo',
    'dotplots.html': 'dotplot',
    'fractals.html': 'fractal',
    'grids.html': 'grid',
    'illusion.html': 'illusion',
    'l_systems.html': 'l-system',
    'multi.html': 'multi',
    'pers.html': 'pers',
    'plane.html': 'plane',
    'polar.html': 'polar',
    'puzzle.html': 'puzzle',
    'recur.html': 'recur',
    'spirals.html': 'spiral',
    'spirograph.html': 'spiro',
    'trees.html': 'tree',
    'walks.html': 'walk',
}

def get_html_thumbnails(html_file):
    """Extract program thumbnails from HTML file."""
    with open(html_file, 'r', encoding='iso-8859-1') as f:
        soup = BeautifulSoup(f.read(), 'html.parser')

    thumbnails = {}

    # Find all image tags
    for img in soup.find_all('img'):
        src = img.get('src', '')
        # Looking for program thumbnails (32g* or 100g*)
        if '/32g' in src or '/100g' in src:
            # Get the parent link
            parent_link = img.find_parent('a')
            if parent_link:
                href = parent_link.get('href', '')
                # Extract program name from href (e.g., ../walk/arcstep.html -> arcstep)
                if '../' in href:
                    prog_name = Path(href).stem
                    # Convert 32g* to 100g* (we want 100x100 thumbnails)
                    thumb_src = src.replace('/32g', '/100g')
                    thumbnails[prog_name] = thumb_src

    return thumbnails

# Fix each category
print("Fixing thumbnails...\n")
fixed_count = 0

for html_name, md_dir in html_to_md.items():
    html_file = Path(f'logoarts/prog/top/{html_name}')
    md_file = Path(f'logoarts_md/programs/{md_dir}/index.md')

    if not html_file.exists() or not md_file.exists():
        continue

    html_thumbs = get_html_thumbnails(html_file)

    # Read markdown content
    with open(md_file, 'r', encoding='utf-8') as f:
        content = f.read()

    # Process each program
    for prog_name, html_thumb in html_thumbs.items():
        # Extract gallery and filename from HTML thumbnail
        # e.g., ../../art/100g6/bifur.gif
        html_match = re.search(r'/100g(\d+)/(.*)', html_thumb)
        if not html_match:
            continue

        gallery_num = html_match.group(1)
        filename = html_match.group(2)

        # Check if file exists in 100x100 (may be renamed for duplicates or without "32" suffix)
        expected_path = Path(f'logoarts_md/art/100x100/{filename}')

        if expected_path.exists():
            new_thumb = f"../../art/100x100/{filename}"
        else:
            # Try without "32" suffix (100g versions often don't have the "32")
            filename_no32 = filename.replace('32.', '.')
            alt_path = Path(f'logoarts_md/art/100x100/{filename_no32}')

            if alt_path.exists():
                new_thumb = f"../../art/100x100/{filename_no32}"
            else:
                # Check for renamed duplicate with gallery prefix
                renamed_path = Path(f'logoarts_md/art/100x100/g{gallery_num}_{filename}')
                renamed_path_no32 = Path(f'logoarts_md/art/100x100/g{gallery_num}_{filename_no32}')

                if renamed_path.exists():
                    new_thumb = f"../../art/100x100/g{gallery_num}_{filename}"
                elif renamed_path_no32.exists():
                    new_thumb = f"../../art/100x100/g{gallery_num}_{filename_no32}"
                else:
                    # Check if it's in a subdirectory (try both with and without "32")
                    matches = list(Path('logoarts_md/art/100x100').rglob(filename))
                    if not matches:
                        matches = list(Path('logoarts_md/art/100x100').rglob(filename_no32))

                    if matches:
                        rel_path = matches[0].relative_to(Path('logoarts_md/art/100x100'))
                        new_thumb = f"../../art/100x100/{rel_path}".replace('\\', '/')
                    else:
                        print(f"  WARNING: {md_dir}/{prog_name}: Image not found: {filename} or {filename_no32}")
                        continue

        # Find and replace thumbnail in markdown
        # Pattern: [![Title](../../art/*/image)](progname.md)
        pattern = rf'(\[!\[.*?\]\()([^)]+)(\)\]\({prog_name}\.md\))'
        match = re.search(pattern, content)

        if match:
            old_thumb = match.group(2)
            if old_thumb != new_thumb:
                content = content.replace(match.group(0), f"{match.group(1)}{new_thumb}{match.group(3)}")
                print(f"  FIXED {md_dir}/{prog_name}: {Path(new_thumb).name}")
                fixed_count += 1
        else:
            print(f"  NOT FOUND in MD: {md_dir}/{prog_name}")

    # Write updated content
    with open(md_file, 'w', encoding='utf-8') as f:
        f.write(content)

print(f"\nFixed {fixed_count} thumbnails")
