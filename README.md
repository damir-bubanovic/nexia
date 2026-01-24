# Nexia

## Local development (Linux Mint / IntelliJ IDEA)

### Prerequisites
- Java 21 (Temurin recommended)
- Docker (required for Testcontainers-based tests)
- Node.js 20+ and npm
- Make

---

## Backend (Core)

### Run backend tests
```bash
chmod +x mvnw
./mvnw test
```

### Build the backend jar
```bash
./mvnw -DskipTests package
```

### Build Docker image (core)
```bash
docker build -f Dockerfile.core -t nexia-core:local .
```

---

## Frontend / BFF (Node)

### Install dependencies
```bash
cd nexia-bff
npm ci
```

---

## Local CI (same intent as GitHub Actions)

From repo root:

```bash
make ci
```

If Docker is not available, run at least:

```bash
./mvnw test
./mvnw -DskipTests package
cd nexia-bff && npm ci
```

---

## Commit message convention

This project uses **Conventional Commits**.

### Format
```text
<type>: <short summary>
```

### Types
- `feat:` new feature
- `fix:` bug fix
- `docs:` documentation only
- `chore:` tooling, build, deps, cleanup
- `refactor:` code change without behavior change
- `test:` tests only

### Examples
- `feat: add JWT refresh endpoint`
- `fix: prevent NPE in login flow`
- `chore: update CI workflow`

---

## Contributing

See `CONTRIBUTING.md` for:
- Branching strategy
- Code review standards
- Release process
- Documentation rules
- Team workflow

---

## Project structure

- `nexia-core/` — Spring Boot backend
- `nexia-bff/` — Node BFF / frontend support
- `.github/` — GitHub Actions and templates
- `docs/` — Architecture Decision Records (ADRs) and documentation
