# Contributing to FarNDone

Welcome to **FarNDone**! FarNDone is a cross-platform remote control system built for reliability, simplicity, and real-world everyday use.

We value clean architecture, maintainability, and code that is easy to understand, test, and audit.

---

## 1. Branch Strategy

We follow a clean feature-branch workflow based on `main`:

- `main`: Production-ready, stable codebase. Direct commits to `main` are forbidden.
- `feature/<name>`: Feature branches for specific capabilities (e.g., `feature/protocol-models`, `feature/trackpad-touch`, `feature/uinput-linux`).
- `fix/<name>`: Bug fixes (e.g., `fix/scroll-delta-direction`).
- `docs/<name>`: Documentation updates (e.g., `docs/architecture-update`).

---

## 2. Commit Message Guidelines

We use [**Conventional Commits**](https://www.conventionalcommits.org/en/v1.0.0/) to keep git history clean, readable, and structured:

- `feat:` A new feature or capability (e.g., `feat: implement uinput mouse injector on Linux`)
- `fix:` A bug fix (e.g., `fix: handle socket disconnection gracefully`)
- `docs:` Documentation changes only (e.g., `docs: add protocol specification`)
- `test:` Adding or updating tests (e.g., `test: add unit tests for pairing rate limiter`)
- `refactor:` Code change that neither fixes a bug nor adds a feature (e.g., `refactor: extract audio controller interface`)
- `chore:` Maintenance tasks, dependency bumps, or config files (e.g., `chore: update gitignore for gradle cache`)

---

## 3. Peer Review Workflow

To maintain high code quality and shared ownership of the codebase:

1. **Create a Branch**: Branch off an up-to-date `main`.
2. **Implement Step by Step**: Keep commits small and focused.
3. **Open a Pull Request**: Provide a clear description of changes and test steps.
4. **Mandatory Peer Review**:
   - Every pull request must be reviewed and tested by the other maintainer.
   - Run tests locally on your machine before approving.
   - Clarify or refactor anything that is not immediately readable.
5. **Merge**: Once approved, rebase or squash-and-merge into `main`.

---

## 4. Coding Standards

- **Clear over Clever**: Write readable, straightforward code. Avoid unnecessary abstractions or premature micro-optimizations.
- **Comments**: Comment **WHY**, not **WHAT**. The code explains what it does; comments explain the reason, invariant, or edge case behind the decision.
- **No Secrets**: Never commit private keys, authentication tokens, or personal IP addresses.
- **Platform Separation**: OS-specific code belongs strictly in `FarNDone.Platform.*`. The core server and models must remain platform-agnostic.
