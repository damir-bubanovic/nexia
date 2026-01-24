# Branching strategy

## Goals
- Keep `main` always releasable.
- Enable parallel work with minimal conflicts.
- Make history readable and easy to revert.

## Main branches
- `main`: production-ready, protected by CI and review.

## Working branches
Create short-lived branches from `main`:

- `feat/<short-description>`
- `fix/<short-description>`
- `refactor/<short-description>`
- `docs/<short-description>`
- `chore/<short-description>`

Examples:
- `feat/user-registration`
- `fix/login-redirect`
- `chore/ci-artifacts`

## Pull Requests
- One purpose per PR.
- Keep PRs small when possible.
- CI must pass.
- At least one approval required (for team mode).

## Merge policy
Preferred: **Squash & merge**
- Produces a clean history (one commit per PR).
- PR title should be meaningful, because it becomes the commit message.

## Hotfixes
If urgent:
- branch from `main` as `fix/<...>`
- PR with tests
- merge quickly with review if possible
