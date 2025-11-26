#!/usr/bin/env python3
"""
Update the programs index to include descriptions from category overview pages.
"""

from bs4 import BeautifulSoup
from pathlib import Path
import re

# Mapping of category folder names to their HTML overview files
CATEGORY_MAP = {
    'anim': 'anim.html',
    'art': 'art.html',
    'ca': 'ca.html',
    'coding': 'coding.html',
    'demo': 'demo.html',
    'dotplot': 'dotplots.html',
    'fractal': 'fractals.html',
    'grid': 'grids.html',
    'illusion': 'illusion.html',
    'l-system': 'l_systems.html',
    'multi': 'multi.html',
    'oneline': 'oneline.html',
    'pers': 'pers.html',
    'plane': 'plane.html',
    'polar': 'polar.html',
    'puzzle': 'puzzle.html',
    'recur': 'recur.html',
    'sound': 'sound.html',
    'spiral': 'spirals.html',
    'spirograph': 'spirograph.html',
    'tree': 'trees.html',
    'walk': 'walks.html'
}

def extract_category_description(html_file):
    """Extract the description from a category overview HTML file."""
    try:
        with open(html_file, 'r', encoding='iso-8859-1') as f:
            soup = BeautifulSoup(f.read(), 'html.parser')

        # Find the main content area
        maco = soup.find('div', id='maco')
        if not maco:
            return None

        # Extract the first column (left side) which usually has the description
        table = maco.find('table')
        if table:
            first_td = table.find('td')
            if first_td:
                # Extract text and clean it up
                description_parts = []
                for p in first_td.find_all('p', recursive=False):
                    # Skip links to other programs (they have thumbnails)
                    if p.find('img'):
                        continue

                    # Get text content
                    text = []
                    for content in p.children:
                        if isinstance(content, str):
                            # Clean up whitespace
                            cleaned = ' '.join(content.strip().split())
                            if cleaned:
                                text.append(cleaned)
                        elif content.name == 'strong':
                            text.append(f"**{content.get_text()}**")
                        elif content.name == 'a':
                            link_text = content.get_text()
                            href = content.get('href', '')
                            if href:
                                # Convert to relative markdown link
                                href = href.replace('../ipt/prim/', '../../primitives/')
                                # Fix program links to be relative to programs directory
                                if '../' in href:
                                    # Extract just the category and filename
                                    parts = href.split('/')
                                    if len(parts) >= 2:
                                        category = parts[-2]
                                        filename = parts[-1].replace('.html', '.md')
                                        href = f"{category}/{filename}"
                                else:
                                    href = href.replace('.html', '.md')
                                text.append(f"[{link_text}]({href})")
                            else:
                                text.append(link_text)
                        else:
                            cleaned = ' '.join(content.get_text().strip().split())
                            if cleaned:
                                text.append(cleaned)

                    paragraph = ' '.join(text).strip()
                    # Clean up extra spaces
                    paragraph = re.sub(r'\s+', ' ', paragraph)
                    paragraph = re.sub(r'\s+([.,!?])', r'\1', paragraph)
                    if paragraph and not paragraph.startswith('See also:'):
                        description_parts.append(paragraph)

                return '\n\n'.join(description_parts)

        return None
    except Exception as e:
        print(f"Error processing {html_file}: {e}")
        return None

def main():
    base_dir = Path(__file__).parent / 'logoarts'
    output_dir = Path(__file__).parent / 'logoarts_md' / 'programs'

    # First, read the category index files to get thumbnails
    category_thumbnails = {}
    for category_dir in sorted(output_dir.iterdir()):
        if not category_dir.is_dir() or category_dir.name.startswith('.'):
            continue

        category_name = category_dir.name
        index_file = category_dir / 'index.md'

        if index_file.exists():
            with open(index_file, 'r', encoding='utf-8') as f:
                content = f.read()
                # Find all thumbnail links
                thumbs = []
                for line in content.split('\n'):
                    if line.strip().startswith('[![') and '](' in line:
                        thumbs.append(line.strip())

                if thumbs:
                    # Take first 4 thumbnails
                    category_thumbnails[category_name] = ' '.join(thumbs[:4])

    # Build new index content
    new_content = []
    new_content.append("# XLogo Programs\n\n")
    new_content.append("A collection of XLogo programs demonstrating various techniques and concepts.\n\n")

    # Process each category
    for category_dir in sorted(output_dir.iterdir()):
        if not category_dir.is_dir() or category_dir.name.startswith('.'):
            continue

        category_name = category_dir.name

        # Get the HTML overview file
        if category_name in CATEGORY_MAP:
            html_file = base_dir / 'prog' / 'top' / CATEGORY_MAP[category_name]

            # Get category display name
            display_name = category_name.replace('_', ' ').replace('-', ' ').title()
            if category_name == 'ca':
                display_name = 'Cellular Automata'
            elif category_name == 'l-system':
                display_name = 'L-Systems'
            elif category_name == 'multi':
                display_name = 'Multiple Turtles'
            elif category_name == 'oneline':
                display_name = 'One-Liners'
            elif category_name == 'pers':
                display_name = 'Perspective (3D)'
            elif category_name == 'dotplot':
                display_name = 'Dot Plots'

            new_content.append(f"## [{display_name}]({category_name}/index.md)\n\n")

            # Add description if available
            if html_file.exists():
                description = extract_category_description(html_file)
                if description:
                    new_content.append(f"{description}\n\n")

            # Add thumbnails
            if category_name in category_thumbnails:
                new_content.append(f"{category_thumbnails[category_name]}\n\n")

    # Write the new index
    index_path = output_dir / 'index.md'
    with open(index_path, 'w', encoding='utf-8') as f:
        f.write(''.join(new_content))

    print(f"Updated {index_path}")

if __name__ == '__main__':
    main()
