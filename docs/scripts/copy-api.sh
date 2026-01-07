#!/usr/bin/env bash
set -euo pipefail

api_source="docs/api"
site_root="build/site"

if [[ ! -d "${api_source}" ]]; then
  echo "No ${api_source} directory found; skipping API reference copy."
  exit 0
fi

if [[ ! -d "${site_root}" ]]; then
  echo "No ${site_root} directory found; run Antora first."
  exit 1
fi

copy_dir() {
  local destination="$1"
  mkdir -p "${destination}"
  if command -v rsync >/dev/null 2>&1; then
    rsync -a "${api_source}/" "${destination}/"
  else
    cp -R "${api_source}/" "${destination}/"
  fi
}

shopt -s nullglob
version_root="${site_root}/tapik"

if [[ -f "${version_root}/index.html" ]]; then
  copy_dir "${version_root}/api"
  echo "Copied API reference into ${version_root}/api"
  exit 0
fi

version_dirs=("${version_root}"/*/)

for version_dir in "${version_dirs[@]}"; do
  if [[ -f "${version_dir}index.html" ]]; then
    copy_dir "${version_dir}api"
    echo "Copied API reference into ${version_dir}api"
  fi
done
