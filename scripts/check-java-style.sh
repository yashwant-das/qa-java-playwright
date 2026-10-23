#!/usr/bin/env bash
# pre-commit supplies paths from the index; do not inspect unrelated drafts.
set -euo pipefail

includes=()
check_tracked_sources=false
for path in "$@"; do
  case "$path" in
    pom.xml|config/checkstyle.xml|scripts/check-java-style.sh)
      check_tracked_sources=true
      ;;
    src/main/java/*.java) includes+=("${path#src/main/java/}") ;;
    src/test/java/*.java) includes+=("${path#src/test/java/}") ;;
  esac
done

if "$check_tracked_sources"; then
  includes=()
  while IFS= read -r -d '' path; do
    case "$path" in
      src/main/java/*.java) includes+=("${path#src/main/java/}") ;;
      src/test/java/*.java) includes+=("${path#src/test/java/}") ;;
    esac
  done < <(git ls-files -z -- src/main/java src/test/java)
fi

if [[ ${#includes[@]} -eq 0 ]]; then
  echo 'No staged or tracked Java sources to check.'
  exit 0
fi

# Checkstyle uses a comma-separated set of source-relative include patterns.
include_patterns=$(IFS=,; echo "${includes[*]}")
exec bash ./mvnw -o -B -ntp checkstyle:check "-Dcheckstyle.includes=$include_patterns"
