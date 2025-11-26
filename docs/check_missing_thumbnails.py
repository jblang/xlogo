#!/usr/bin/env python3
"""Check for missing or broken thumbnails in markdown index files."""

import re
from pathlib import Path

def check_index_file(md_file):
    """Check markdown index file for thumbnail issues."""
    with open(md_file, 'r', encoding='utf-8') as f:
        content = f.read()

    issues = []

    # Find all program entries
    # Pattern: ## [Title](progname.md)
    title_pattern = r'##\s+\[(.*?)\]\((\w+)\.md\)'

    for match in re.finditer(title_pattern, content):
        title = match.group(1)
        prog_name = match.group(2)

        # Look for corresponding thumbnail right after the title
        # Pattern: [![Title](../../art/path/image.ext)](progname.md)
        thumb_pattern = rf'\[!\[{re.escape(title)}\]\((.*?)\)\]\({prog_name}\.md\)'
        thumb_match = re.search(thumb_pattern, content)

        if not thumb_match:
            issues.append(f"Missing thumbnail: {prog_name} ({title})")
            continue

        thumb_path = thumb_match.group(1)

        # Check if image exists
        # Convert ../../art/path to logoarts_md/art/path
        if thumb_path.startswith('../../art/'):
            rel_path = thumb_path.replace('../../art/', '')
            img_file = Path(f'logoarts_md/art/{rel_path}')

            if not img_file.exists():
                issues.append(f"Broken thumbnail: {prog_name} ({title}) - {thumb_path}")

    return issues

# Check all program index files
all_issues = {}

for index_file in sorted(Path('logoarts_md/programs').rglob('index.md')):
    category = index_file.parent.name
    issues = check_index_file(index_file)

    if issues:
        all_issues[category] = issues

# Print results
if all_issues:
    print("Issues found:\n")
    for category, issues in sorted(all_issues.items()):
        print(f"\n{category}:")
        for issue in issues:
            print(f"  - {issue}")

    total_issues = sum(len(issues) for issues in all_issues.values())
    print(f"\n\nTotal issues: {total_issues}")
else:
    print("No issues found - all thumbnails present and valid!")
