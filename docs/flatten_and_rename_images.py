#!/usr/bin/env python3
"""
Flatten gallery directories into size-based directories,
renaming duplicate files to make them unique.
"""

from pathlib import Path
from collections import defaultdict
import shutil
import re

def get_gallery_num(path_str):
    """Extract gallery number from path like '400g3'."""
    match = re.search(r'400g(\d+)', path_str)
    return match.group(1) if match else None

def main():
    src_base = Path('docs/logoarts/art')
    dest_base = Path('docs/logoarts_md/art')

    # Create destination directories
    (dest_base / '400x400').mkdir(parents=True, exist_ok=True)
    (dest_base / '100x100').mkdir(parents=True, exist_ok=True)
    (dest_base / '32x32').mkdir(parents=True, exist_ok=True)

    # Track files by relative path to detect duplicates
    files_by_relpath = defaultdict(list)
    path_mapping = {}  # old_path -> new_path mapping

    # First pass: collect all files and identify duplicates
    for gallery_dir in sorted(src_base.glob('400g*')):
        gallery_num = get_gallery_num(str(gallery_dir))

        for img_file in gallery_dir.rglob('*'):
            if not img_file.is_file() or img_file.suffix not in ['.png', '.gif', '.jpg']:
                continue

            rel_path = img_file.relative_to(gallery_dir)
            gallery_path = str(img_file.relative_to(src_base))
            files_by_relpath[str(rel_path)].append((gallery_num, gallery_path, img_file))

    # Second pass: copy files with unique names
    for rel_path, file_list in files_by_relpath.items():
        rel_path_obj = Path(rel_path)

        if len(file_list) == 1:
            # No duplicate, use original name
            gallery_num, gallery_path, src_file = file_list[0]
            dest_file = dest_base / '400x400' / rel_path
            dest_file.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(src_file, dest_file)
            path_mapping[gallery_path] = str(Path('art/400x400') / rel_path)
            print(f"Copied {gallery_path} -> art/400x400/{rel_path}")
        else:
            # Duplicate: rename with gallery prefix
            for gallery_num, gallery_path, src_file in file_list:
                # Add gallery number prefix to filename
                new_name = f"g{gallery_num}_{rel_path_obj.name}"
                if rel_path_obj.parent != Path('.'):
                    new_rel_path = rel_path_obj.parent / new_name
                else:
                    new_rel_path = Path(new_name)

                dest_file = dest_base / '400x400' / new_rel_path
                dest_file.parent.mkdir(parents=True, exist_ok=True)
                shutil.copy2(src_file, dest_file)
                path_mapping[gallery_path] = str(Path('art/400x400') / new_rel_path)
                print(f"Copied {gallery_path} -> art/400x400/{new_rel_path} (renamed)")

    # Save mapping for later use
    mapping_file = dest_base / 'path_mapping.txt'
    with open(mapping_file, 'w') as f:
        for old_path, new_path in sorted(path_mapping.items()):
            f.write(f"{old_path}\t{new_path}\n")

    print(f"\nTotal files copied: {len(path_mapping)}")
    print(f"Path mapping saved to: {mapping_file}")

    return path_mapping

if __name__ == '__main__':
    main()