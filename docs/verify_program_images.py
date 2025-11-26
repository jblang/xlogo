#!/usr/bin/env python3
"""
Verify and fix program images by comparing against HTML sources.
"""

import re
from pathlib import Path
from bs4 import BeautifulSoup

def get_html_image(html_path):
    """Extract the main image path from HTML file."""
    try:
        with open(html_path, 'r', encoding='iso-8859-1') as f:
            html_content = f.read()
    except:
        return None

    soup = BeautifulSoup(html_content, 'html.parser')

    # Look for image in rico div (thumbnail)
    rico = soup.find('div', id='rico')
    if rico:
        img = rico.find('img', src=lambda x: x and ('/100g' in x or '/32g' in x) if x else False)
        if img:
            src = img.get('src', '')
            # Convert from 100g to 400g and adjust path
            src = src.replace('../', '')
            src = src.replace('/100g', '/400g')
            src = src.replace('/32g', '/400g')
            # Change extension from .gif to .png if it's in a 400g directory
            if '/400g' in src and src.endswith('.gif'):
                src = src[:-4] + '.png'
            return src

    return None

def get_md_image(md_path):
    """Extract the image path from markdown file."""
    try:
        with open(md_path, 'r', encoding='utf-8') as f:
            content = f.read()
    except:
        return None

    # Find first image reference
    match = re.search(r'!\[.*?\]\((.*?)\)', content)
    if match:
        return match.group(1)
    return None

def update_md_image(md_path, new_image_path):
    """Update the image path in markdown file."""
    with open(md_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Replace first image
    updated = re.sub(r'(!\[.*?\]\().*?(\))', rf'\1{new_image_path}\2', content, count=1)

    with open(md_path, 'w', encoding='utf-8') as f:
        f.write(updated)

def main():
    """Verify all program images."""
    html_base = Path('docs/logoarts/prog')
    md_base = Path('docs/logoarts_md/programs')

    if not html_base.exists() or not md_base.exists():
        print("Base directories not found")
        return

    issues = []
    fixed = []

    # Process all markdown files
    for md_file in md_base.rglob('*.md'):
        if md_file.name == 'index.md':
            continue

        # Find corresponding HTML file
        rel_path = md_file.relative_to(md_base)
        html_file = html_base / str(rel_path).replace('.md', '.html')

        if not html_file.exists():
            continue

        # Get image paths
        html_img = get_html_image(html_file)
        md_img = get_md_image(md_file)

        if not html_img:
            # No image in HTML, skip
            continue

        # Check if image exists in markdown directory
        if md_img:
            md_img_full = (md_file.parent / md_img).resolve()
            html_img_full = Path('docs/logoarts_md/programs') / html_img

            # Normalize paths for comparison
            expected_img = '../../' + html_img

            if md_img != expected_img:
                issues.append(f"{rel_path}: {md_img} -> {expected_img}")
                update_md_image(md_file, expected_img)
                fixed.append(str(rel_path))
        else:
            # No image in markdown but there should be one
            expected_img = '../../' + html_img
            issues.append(f"{rel_path}: MISSING -> {expected_img}")

            # Insert image after title
            with open(md_file, 'r', encoding='utf-8') as f:
                lines = f.readlines()

            # Find title and insert image after it
            for i, line in enumerate(lines):
                if line.startswith('# '):
                    title = line.strip('#').strip()
                    lines.insert(i + 1, f'![{title}]({expected_img})\n\n')
                    break

            with open(md_file, 'w', encoding='utf-8') as f:
                f.writelines(lines)

            fixed.append(str(rel_path))

    if issues:
        print("Image issues found and fixed:")
        for issue in sorted(issues):
            print(f"  {issue}")
        print(f"\nTotal files fixed: {len(fixed)}")
    else:
        print("All images verified successfully!")

if __name__ == '__main__':
    main()