---
trigger: manual
---

Android Project Code Rules

1. Documentation

Document every method (purpose → input → output).

Keep logic notes short and clear.

2. Architecture

Use MVVM + Clean Architecture.

Use modules, Hilt, Kotlin DSL, Ktlint.

Follow proper naming conventions.

3. Code Quality

Follow SOLID.

No long methods, no duplicate code.

Use Coroutines (no heavy work on Main Thread).

Format code with Ktlint before commit.

4. Refactoring & Analysis

AI-generated code must be clean, short, and reusable.

Use Repository → UseCase → ViewModel pattern.

5. Testing

Use JUnit5 + Mockito/Mockk + Coroutines Test.

Add positive, negative, and edge-case tests.

6. Security

No sensitive data in logs.

Enable ProGuard/R8 + Lint + Pre-commit hooks.

7. Upgrades

Fix deprecated APIs.

Update libraries safely.

Provide a short migration note.