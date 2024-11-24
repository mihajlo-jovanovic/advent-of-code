#!/bin/sh

# List of files to delete
files_to_delete="CHANGELOG.md LICENSE README.md"

# List of directories to delete
# WARNING: This will force delete the directory and all its contents
dirs_to_delete="doc"

# Current working directory
current_dir=$(pwd)

# Delete files
echo "Deleting files in $current_dir..."
for file in $files_to_delete; do
  file_path="$current_dir/$file"
  if [ -f "$file_path" ]; then
    echo "Deleting file: $file_path"
    rm "$file_path"
  else
    echo "File not found: $file_path"
  fi
done

# Delete directories
echo "Deleting directories in $current_dir..."
for dir in $dirs_to_delete; do
  dir_path="$current_dir/$dir"
  if [ -d "$dir_path" ]; then
    echo "Deleting directory: $dir_path"
    rm -rf "$dir_path"
  else
    echo "Directory not found: $dir_path"
  fi
done

echo "Deletion complete."
