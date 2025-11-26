#!/usr/bin/env python3
"""Verify that markdown index files use correct thumbnails from HTML source."""

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

def get_markdown_thumbnails(md_file):
    """Extract thumbnails from markdown index file."""
    with open(md_file, 'r', encoding='utf-8') as f:
        content = f.read()

    thumbnails = {}

    # Pattern: [![Title](../../art/path/image.ext)](progname.md)
    pattern = r'\[!\[.*?\]\((.*?)\)\]\((\w+)\.md\)'

    for match in re.finditer(pattern, content):
        img_path = match.group(1)
        prog_name = match.group(2)
        thumbnails[prog_name] = img_path

    return thumbnails

# Check each category
print("Verifying thumbnails...\n")
issues_found = False

for html_name, md_dir in html_to_md.items():
    html_file = Path(f'logoarts/prog/top/{html_name}')
    md_file = Path(f'logoarts_md/programs/{md_dir}/index.md')

    if not html_file.exists():
        print(f"WARNING: HTML file not found: {html_file}")
        continue

    if not md_file.exists():
        print(f"WARNING: Markdown file not found: {md_file}")
        continue

    html_thumbs = get_html_thumbnails(html_file)
    md_thumbs = get_markdown_thumbnails(md_file)

    print(f"\n{'=' * 60}")
    print(f"Category: {md_dir}")
    print(f"{'=' * 60}")

    # Check each program
    all_programs = set(html_thumbs.keys()) | set(md_thumbs.keys())

    for prog in sorted(all_programs):
        html_thumb = html_thumbs.get(prog)
        md_thumb = md_thumbs.get(prog)

        if html_thumb is None:
            print(f"  WARNING: {prog}: Not in HTML")
            continue

        if md_thumb is None:
            print(f"  MISSING: {prog}: Missing thumbnail in markdown")
            issues_found = True
            continue

        # Extract gallery info from HTML thumbnail
        # e.g., ../../art/100g6/bifur.gif
        html_match = re.search(r'/100g(\d+)/(.*)', html_thumb)
        if html_match:
            gallery_num = html_match.group(1)
            filename = html_match.group(2)

            # Check if markdown uses correct path
            # Should be either:
            # - ../../art/100x100/filename (if no duplicate)
            # - ../../art/100x100/gN_filename (if duplicate with different gallery)

            expected_path = f"../../art/100x100/{filename}"
            alt_pattern = f"../../art/100x100/g\\d+_{filename}"

            if md_thumb == expected_path or re.match(alt_pattern, md_thumb):
                print(f"  OK {prog}: {Path(md_thumb).name}")
            else:
                print(f"  ISSUE {prog}:")
                print(f"     HTML: {html_thumb}")
                print(f"     MD:   {md_thumb}")
                print(f"     Expected: {expected_path}")
                issues_found = True

if not issues_found:
    print("\nAll thumbnails verified!")
else:
    print("\nIssues found - see above")