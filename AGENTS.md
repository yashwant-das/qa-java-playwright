# Repository instructions

This is a test-only Java Playwright/TestNG starter. Read [development](docs/development.md)
and [architecture](docs/architecture.md) before changing its build or behavior.

Use the `complete` documentation profile and the relevant guides in
[repo-standards at c00f280a94c5289aba795d240aaa963e7c886ed2](https://github.com/yashwant-das/repo-standards/tree/c00f280a94c5289aba795d240aaa963e7c886ed2).
Explicit owner instructions take precedence. Keep standards profile and rollout
bookkeeping out of the project README; record evidence and pending exceptions
in the development guide.

- Preserve the Java 17 target; use JDK 21 to build. Target changes need owner approval.
- Retain Maven test-source layout and the existing MIT license.
- Use Maven group `com.qaframework` and base package `com.qaframework.playwright`.
  Keep project subpackages descriptive and free of personal names or abbreviations.
- Keep controlled smoke tests separate from public-site examples and offline units.
- Keep hooks independent of browsers and external services after installation.
- Run documented local checks before committing; do not bypass failed hooks.
- Publish reports only from checked trusted-branch runs. Do not change account
  protection or environment settings without owner approval.
