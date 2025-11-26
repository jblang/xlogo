#!/usr/bin/env python3
"""
Check for duplicate filenames across gallery directories.
"""

from pathlib import Path
from collections import defaultdict

# Find all images in gallery directories
gallery_dirs = Path('docs/logoarts/art').glob('400g*')
files_by_name = defaultdict(list)

for gallery_dir in sorted(gallery_dirs):
    for img_file in gallery_dir.rglob('*'):
        if img_file.is_file() and img_file.suffix in ['.png', '.gif', '.jpg']:
            # Get relative path from gallery dir
            rel_path = img_file.relative_to(gallery_dir)
            files_by_name[str(rel_path)].append(str(img_file.relative_to(Path('docs/logoarts/art'))))

# Print duplicates
print("Duplicate filenames across galleries:")
print("=" * 60)
duplicates = {name: paths for name, paths in files_by_name.items() if len(paths) > 1}

if duplicates:
    for name, paths in sorted(duplicates.items()):
        print(f"\n{name}:")
        for path in paths:
            print(f"  - {path}")
    print(f"\nTotal duplicate filenames: {len(duplicates)}")
else:
    print("No duplicates found!")