# Code Review Standards

## Purpose
Code reviews exist to:
- Improve code quality
- Share knowledge
- Reduce bugs and security issues
- Keep the codebase consistent and maintainable

## What every PR should have
- Clear description of what and why
- Focused scope (one concern per PR)
- Tests added or updated if behavior changes
- No secrets or credentials
- CI passing

## Reviewer checklist

### 1) Correctness
- Does the code do what it claims?
- Are edge cases handled?
- Are errors handled properly?

### 2) Design & Architecture
- Is the change consistent with existing architecture?
- Does it introduce unnecessary coupling?
- Is the responsibility in the right place?

### 3) Readability & Maintainability
- Are names clear?
- Is the code easy to understand?
- Is there duplication?
- Is complexity reasonable?

### 4) Security
- Any auth/authz impact?
- Input validation?
- Secrets or sensitive data?

### 5) Performance & Reliability
- Any obvious performance problems?
- Any blocking calls or unbounded loops?
- Timeouts, retries, resource usage?

### 6) Tests
- Are tests meaningful?
- Do they fail without the change?
- Are they stable (no flakiness, no timing dependency)?

## Review etiquette
- Be constructive and respectful
- Explain *why*, not just *what*
- Prefer suggestions over commands
- The goal is better code, not winning arguments
