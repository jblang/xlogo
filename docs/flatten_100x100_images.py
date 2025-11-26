#!/usr/bin/env python3
"""Flatten 100g* gallery directories into single 100x100 directory, renaming duplicates."""

import shutil
from pathlib import Path
from collections import defaultdict

# Source and destination
source_base = Path('logoarts/art')
dest_base = Path('logoarts_md/art')

# Collect all files from 100g* directories
file_dict = defaultdict(list)

# Scan all 100g* directories
for gallery_dir in sorted(source_base.glob('100g*')):
    if not gallery_dir.is_dir():
        continue

    gallery_num = gallery_dir.name.replace('100g', '')

    # Find all files recursively
    for file_path in gallery_dir.rglob('*'):
        if file_path.is_file():
            # Get relative path from gallery directory
            rel_path = file_path.relative_to(gallery_dir)
            file_dict[str(rel_path)].append((gallery_num, file_path))

# Create destination directory
dest_dir = dest_base / '100x100'
dest_dir.mkdir(parents=True, exist_ok=True)

# Copy files, renaming duplicates
path_mapping = {}
copied_count = 0

for rel_path_str, file_list in sorted(file_dict.items()):
    rel_path_obj = Path(rel_path_str)

    if len(file_list) == 1:
        # No duplicate, use original name (preserve subdirectory structure)
        gallery_num, source_file = file_list[0]

        # Create destination with subdirectory if needed
        if rel_path_obj.parent != Path('.'):
            dest_file = dest_dir / rel_path_obj
            dest_file.parent.mkdir(parents=True, exist_ok=True)
        else:
            dest_file = dest_dir / rel_path_obj.name

        # Copy file
        shutil.copy2(source_file, dest_file)

        # Record mapping
        old_path = f"100g{gallery_num}/{rel_path_str}"
        new_path = f"art/100x100/{rel_path_str}"
        path_mapping[old_path] = new_path
        copied_count += 1
    else:
        # Duplicates: rename with gallery prefix
        print(f"Duplicate found: {rel_path_str}")
        for gallery_num, source_file in file_list:
            # Create new name with gallery prefix
            if rel_path_obj.parent != Path('.'):
                # Has subdirectory - prefix the filename only
                new_name = f"g{gallery_num}_{rel_path_obj.name}"
                dest_subdir = dest_dir / rel_path_obj.parent
                dest_subdir.mkdir(parents=True, exist_ok=True)
                dest_file = dest_subdir / new_name
            else:
                new_name = f"g{gallery_num}_{rel_path_obj.name}"
                dest_file = dest_dir / new_name

            # Copy file
            shutil.copy2(source_file, dest_file)

            # Record mapping
            old_path = f"100g{gallery_num}/{rel_path_str}"
            new_path = str(dest_file.relative_to(dest_base))
            path_mapping[old_path] = new_path
            copied_count += 1

# Write path mapping file
mapping_file = dest_base / '100x100_path_mapping.txt'
with open(mapping_file, 'w') as f:
    for old_path, new_path in sorted(path_mapping.items()):
        f.write(f"{old_path}\t{new_path}\n")

print(f"Copied {copied_count} total images")
print(f"Path mapping written to {mapping_file}")