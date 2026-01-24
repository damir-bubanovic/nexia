# Contributing to Nexia

This project follows lightweight team practices designed for quality, speed, and maintainability.

## 1) Workflow Overview
- Work happens on short-lived branches.
- All changes go through a Pull Request (PR).
- CI must be green before merge.
- Prefer small PRs (< ~300 lines changed) unless unavoidable.

## 2) Branching Strategy
- `main` is always deployable.
- Branch naming:
    - `feat/<short-description>` (new feature)
    - `fix/<short-description>` (bug fix)
    - `chore/<short-description>` (tooling/cleanup)
    - `docs/<short-description>` (documentation)
    - `refactor/<short-description>` (refactor without behavior change)
- Merge strategy: **Squash & merge** (keeps history clean).

## 3) Code Review Standards
### PR requirements
- Clear PR title + description
- Link the issue (if any)
- Tests included/updated when behavior changes
- No secrets or credentials committed
- No large refactors mixed with functional changes

### Reviewer checklist
- Correctness: does it do what it claims?
- Security: auth, input validation, secret handling
- Reliability: error handling, retries/timeouts where needed
- Maintainability: naming, structure, complexity
- Observability: logging/metrics where appropriate
- Tests: meaningful and stable

## 4) Commit Message Convention (Conventional Commits)
Use:
- `feat: ...`
- `fix: ...`
- `docs: ...`
- `chore: ...`
- `refactor: ...`
- `test: ...`

Examples:
- `feat: add password reset endpoint`
- `fix: handle null email in user registration`
- `chore: update CI workflow`

## 5) Release & Versioning
- Semantic Versioning: `MAJOR.MINOR.PATCH`
- Git tag format: `vX.Y.Z`
- Releases should include short notes (changes + any migration notes).

## 6) Documentation Practices
- Keep `README.md` current for local setup.
- Update `Features.md` when a phase is completed.
- Technical decisions go into ADRs: `docs/adr/`.

## 7) Local checks before PR
From repo root:
```bash
make ci
