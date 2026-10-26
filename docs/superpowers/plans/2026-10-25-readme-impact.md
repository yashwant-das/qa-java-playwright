# README impact and documentation accuracy implementation plan

> Historical implementation record. Current instructions live in [Development](../../development.md).
> **For agentic workers:** Use superpowers:executing-plans for this documentation-only change.

**Goal:** Restore the Java-team adoption story and correct presentation and factual claims across maintained documentation.

**Architecture:** Keep the pinned complete-profile README anatomy. Lead with the audience and setup burden, connect capabilities to benefits, and keep detailed setup and limitations in the existing guides.

**Tech stack:** GitHub-Flavored Markdown, Mermaid, existing pinned pre-commit hooks, JDK 21 and Maven Wrapper.

**Spec:** The owner approved the proposed opening, Why it exists section, benefit-oriented capabilities and results-section preview in the chat on 2026-10-25, then requested accuracy and formatting checks across all documents.

## Global constraints

- Use repo-standards revision `073f51ff68ec050f6b3372c46bc3bd4e721a5342`, complete profile.
- Preserve runtime code, dependencies, workflows, Java 17 target and JDK 21 build.
- Preserve the unrelated existing `.vscode/settings.json` edit.
- Do not commit or push; leave the documentation changes for owner review.
- Work in the existing PR checkout, branch `codex/adopt-repo-standards`.
- Keep public examples, controlled smoke and browser-free units distinct.

## Review focus

- Describe browser selection per run without implying every example launches three browsers.
- Describe screenshot conditions without promising capture after every failure.
- Keep retry/parallel suite-listener requirements visible beside example commands.
- Verify directory trees, configuration defaults, snippets and versions against sources.
- Preserve historical evidence without implying old results validate the current head.

## Task 1: Restore purpose and readable presentation

**Files:** `README.md`, `docs/architecture.md`, `docs/development.md`, `docs/examples.md`, `AGENTS.md` (review).

- [x] Inspect implementation, suites, configuration, POM and workflow against documentation.
- [x] Rewrite the README opening and Why it exists, retain required sections and move the scoped preview into Verification.
- [x] Replace grouped path lists with directory trees; normalize guide headings, tables and code formatting.
- [x] Correct inaccurate examples, screenshot promises, browser/retry descriptions and configuration coverage.

## Task 2: Verify and review

- [x] Run pinned all-file hooks and check changed-file whitespace and relative links/anchors.
- [x] Run JDK 21 unit/static verification and controlled Chromium smoke; inspect counts and failures.
- [x] Render maintained guides to HTML and inspect presentation, including trees, tables and diagrams.
- [x] Review the final documentation diff against sources and pinned standards; record fresh evidence in Development.

## Execution notes

The owner instructed execution, so no additional approval round is needed. This is a reversible documentation edit in the existing PR checkout; no new worktree is needed. No new implementation tests are warranted. Existing checks and direct source comparison validate the claims.

## Outcome

Implemented the approved documentation changes without committing or pushing.
Pinned all-file hooks passed. Fresh unit/static checks passed (10 units, zero
Checkstyle violations); a temporary copy also passed controlled Chromium smoke
(2 tests). The primary checkout contained incompatible compiled smoke-test output;
the fresh copy used the expected current API. All three architecture Java snippets
compiled. Local Markdown links/anchors and rendered Mermaid/layout checks passed.
Independent review found one minor search-description overstatement, corrected.
The existing editor settings edit was preserved. Public-site/native matrix checks
remain outside this documentation-only validation.
