# Close remaining standards gaps

Historical implementation plan; current setup and status live in [Development](../../development.md).

**Goal:** Resolve the remaining Java-target, repository-description and required-check gaps while retaining the owner-approved README title.

**Authorization:** The owner requested all remaining gaps fixed except the README title. This authorizes the Java 21 target and repository settings changes. Do not commit or push file changes.

## Constraints

- Keep `Playwright Java Accelerator` as the README title.
- Set the compiler target to Java 21, matching the existing JDK 21 CI build.
- Preserve the existing Allure/Surefire compatibility pins and all runtime test logic.
- Preserve existing editor settings and report-image edits.
- Preserve the active ruleset's default-branch scope, deletion/force-push protections and bypass settings.
- Require the four existing GitHub Actions contexts, bound to app 15368, without bypassing failed native-browser checks.

## Tasks

- [x] Change the POM target, Java badge and maintained instructions to Java 21; simplify the smoke-suite wording.
- [x] Verify fresh unit/static and controlled-browser execution in a temporary source copy; inspect bytecode target. Run the default suite where available.
- [x] Prepare and apply the description and ruleset updates, then read back their exact values.
- [x] Update current verification/settings evidence, run pinned file checks and local documentation link/render checks.

## Settings specification

Description: `Tests browser flows with Java, Playwright and TestNG, with Allure reports (starter template)`.

Required contexts: `Quality and unit tests`, `Run Tests (macos-14)`, `Run Tests (windows-latest)` and `Analyze (java-kotlin)`. Require the branch to be up to date. Do not require `Deploy Report`, which intentionally skips on PRs.

## Review focus

Verify Java 21 bytecode rather than inferring the target from the JDK. Preserve existing rules and bind required contexts to their actual integration. Distinguish local evidence from hosted checks on the existing remote head. Leave all edits uncommitted for review.

## Outcome

The clean Java 21 build passed 10/10 units and Checkstyle. Controlled Chromium
passed 2/2 and the default suite passed 36/36, with zero failures, errors or skips.
All 27 generated classes use bytecode major version 65. The active main ruleset
now requires all four named checks from app 15368 and an up-to-date branch;
readback confirmed the original protections and bypass settings remain. The live
description includes the starter-template qualifier. The remote head's cancelled
macOS job remains blocking evidence, separate from local success. No commits or
pushes were made.
