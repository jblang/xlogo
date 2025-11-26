#!/usr/bin/env python3
"""
Create a single programs index with all categories and programs in tables.
"""

from bs4 import BeautifulSoup
from pathlib import Path
import re

def extract_section_info(html_file):
    """Extract section title, description, and all program links from HTML."""
    try:
        with open(html_file, 'r', encoding='iso-8859-1') as f:
            soup = BeautifulSoup(f.read(), 'html.parser')

        # Get the section title from the band div
        title = ""
        band = soup.find('div', id='band')
        if band:
            tite = band.find('div', id='tite')
            if tite:
                h2 = tite.find('h2')
                if h2:
                    title = h2.get_text().strip()

        # Get the main content
        maco = soup.find('div', id='maco')
        if not maco:
            return None

        # Extract description from first column
        description_parts = []
        table = maco.find('table')
        if table:
            first_td = table.find('td')
            if first_td:
                for p in first_td.find_all('p', recursive=False):
                    # Skip paragraphs with program links (they have thumbnails)
                    if p.find('img'):
                        continue
                    if p.find('a', href=lambda x: x and '../' in x if x else False):
                        continue

                    # Get text content
                    text = []
                    for content in p.children:
                        if isinstance(content, str):
                            cleaned = ' '.join(content.strip().split())
                            if cleaned:
                                text.append(cleaned)
                        elif content.name == 'strong':
                            text.append(f"**{content.get_text()}**")
                        elif content.name == 'a':
                            # Skip program links
                            continue
                        else:
                            cleaned = ' '.join(content.get_text().strip().split())
                            if cleaned:
                                text.append(cleaned)

                    paragraph = ' '.join(text).strip()
                    paragraph = re.sub(r'\s+', ' ', paragraph)
                    if paragraph and not paragraph.startswith('See also:'):
                        description_parts.append(paragraph)

        # Extract all program entries from both columns
        programs = []
        if table:
            for td in table.find_all('td'):
                # Find all program links (they have thumbnails)
                for p in td.find_all('p'):
                    link = p.find('a', href=lambda x: x and '../' in x if x else False)
                    if not link:
                        continue

                    img = p.find('img')
                    if not img:
                        continue

                    # Extract program info
                    href = link.get('href', '')
                    if not href or '../' not in href:
                        continue

                    # Skip links to primitives or other sections
                    if '/ipt/' in href or '/top/' in href:
                        continue

                    # Get thumbnail
                    thumb_src = img.get('src', '')

                    # Get program name from the link text
                    prog_name = link.get_text().strip()

                    # Get description - it's after the link, separated by <br>
                    prog_desc = ""
                    # Find all text after the link
                    found_link = False
                    for content in p.children:
                        if content == link:
                            found_link = True
                            continue
                        if found_link:
                            if content.name == 'br':
                                continue
                            if isinstance(content, str):
                                text = content.strip()
                                if text:
                                    prog_desc = text
                                    break
                            elif hasattr(content, 'get_text'):
                                text = content.get_text().strip()
                                if text:
                                    prog_desc = text
                                    break

                    # Parse href to get category and filename
                    href_parts = href.split('/')
                    if len(href_parts) >= 2:
                        category = href_parts[-2]
                        filename = href_parts[-1].replace('.html', '')

                        # Clean up thumbnail path
                        if thumb_src:
                            thumb_src = re.sub(r'^(\.\./)+', '../', thumb_src)

                        programs.append({
                            'name': prog_name,
                            'description': prog_desc,
                            'thumbnail': thumb_src,
                            'category': category,
                            'filename': filename
                        })

        return {
            'title': title,
            'description': '\n\n'.join(description_parts),
            'programs': programs
        }

    except Exception as e:
        print(f"Error processing {html_file}: {e}")
        return None

def main():
    base_dir = Path(__file__).parent / 'logoarts' / 'prog' / 'top'
    output_file = Path(__file__).parent / 'logoarts_md' / 'programs' / 'index.md'

    # List of HTML files in order
    html_files = [
        'anim.html',
        'art.html',
        'ca.html',
        'coding.html',
        'demo.html',
        'dotplots.html',
        'fractals.html',
        'grids.html',
        'illusion.html',
        'l_systems.html',
        'multi.html',
        'oneline.html',
        'pers.html',
        'plane.html',
        'polar.html',
        'puzzle.html',
        'recur.html',
        'sound.html',
        'spirals.html',
        'spirograph.html',
        'trees.html',
        'walks.html'
    ]

    # Build the index
    content = []
    content.append("# XLogo Programs\n\n")
    content.append("A collection of XLogo programs demonstrating various techniques and concepts.\n\n")

    for html_file in html_files:
        html_path = base_dir / html_file
        if not html_path.exists():
            print(f"Warning: {html_path} not found")
            continue

        print(f"Processing {html_file}...")
        info = extract_section_info(html_path)

        if not info:
            continue

        # Write section header
        content.append(f"## {info['title']}\n\n")

        # Write description if available
        if info['description']:
            content.append(f"{info['description']}\n\n")

        # Write programs table
        if info['programs']:
            content.append("| Thumbnail | Name | Description |\n")
            content.append("|-----------|------|-------------|\n")

            for prog in info['programs']:
                # Format thumbnail with link
                thumb = f"[![{prog['name']}]({prog['thumbnail']})]({prog['category']}/{prog['filename']}.md)"
                # Format name with link
                name = f"[{prog['name']}]({prog['category']}/{prog['filename']}.md)"
                # Description
                desc = prog['description'] if prog['description'] else ""

                content.append(f"| {thumb} | {name} | {desc} |\n")

            content.append("\n")

    # Write the index file
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(''.join(content))

    print(f"\nCreated {output_file}")

if __name__ == '__main__':
    main()
