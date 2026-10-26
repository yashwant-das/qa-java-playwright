# Repository instructions

This is a test-only Java Playwright/TestNG starter. Read [development](docs/development.md)
and [architecture](docs/architecture.md) before changing its build or behavior.

Use the `complete` documentation profile and the relevant guides in
[repo-standards at 073f51ff68ec050f6b3372c46bc3bd4e721a5342](https://github.com/yashwant-das/repo-standards/tree/073f51ff68ec050f6b3372c46bc3bd4e721a5342).
Explicit owner instructions take precedence. Keep standards profile and rollout
bookkeeping out of the project README; record evidence and pending exceptions
in the development guide.

- Build and target Java 21. Further runtime-baseline changes need owner approval.
- Retain Maven test-source layout and the existing MIT license.
- Use Maven group `com.qaframework` and base package `com.qaframework.playwright`.
  Keep project subpackages descriptive and free of personal names or abbreviations.
- Use the owner-approved README title `Playwright Java Accelerator`; retain
  `qa-java-playwright` as the repository identifier.
- Keep root README badges, showcase capabilities, Mermaid diagram and structure/stack
  overview accurate; preserve setup depth in the guides.
- Keep controlled smoke tests separate from public-site examples and offline units.
- Keep hooks independent of browsers and external services after installation.
- Run documented local checks before committing; do not bypass failed hooks.
- Publish reports only from checked trusted-branch runs. Do not change account
  protection or environment settings without owner approval.
