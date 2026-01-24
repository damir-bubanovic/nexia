# Architecture Decision Records (ADR)

This directory contains Architecture Decision Records for the Nexia project.

## What is an ADR?

An ADR is a short document that records an important architectural or technical decision.

## Naming

- Use incremental numbers: `0001-...`, `0002-...`, etc.
- Example: `0001-use-postgresql.md`

## When to write one?

Write an ADR when:
- Choosing a major library or framework
- Making a structural architectural change
- Introducing or removing an important dependency
- Changing cross-cutting concerns (security, persistence, messaging, etc)

## Lifecycle

- Proposed → Accepted → (optionally) Superseded
- Never delete ADRs. If a decision changes, create a new ADR that supersedes the old one.
