# Tracker

A personal productivity and study-tracking application being rebuilt from the current V1 prototype into a clean, offline-first V2.

> **Development rule:** `main` is the stable branch. Active development happens on `dev` and future feature branches. Do not push unfinished V2 work directly to `main`.

## Project Status

**Current stage:** V1 prototype / foundation investigation  
**Active development branch:** `dev`  
**Stable branch:** `main`

The repository currently contains an Android Kotlin/Jetpack Compose application originally focused on GATE and placement preparation. V2 will rebuild Tracker as a broader personal operating system while preserving useful study functionality.

The existing V1 code is treated as a **reference/prototype**, not as an architecture that V2 must preserve.

---

## V1 — Existing Prototype

### What V1 currently is

The current repository is an Android application using:

- Kotlin
- Jetpack Compose
- Material 3
- Android Gradle Plugin
- Kotlin Symbol Processing (KSP)
- Room
- Kotlin Coroutines
- Retrofit / OkHttp / Moshi
- Firebase AI integration
- Firebase App Check
- Robolectric / Roborazzi testing infrastructure

The current application is still strongly centered around study preparation.

### V1 functionality present in the current source

The existing code contains a study dashboard with concepts including:

- 3-phase study engine
- 5-hour daily study blocks
- Study block timers
- Study progress tracking
- Active recall notes
- Oswaal practice logging
- Mock-test logging
- AI tutor / memory context
- Text analytics
- Study context generation
- Filtering/search for notes and practice data
- Local Room persistence

### Important V1 limitations

The current implementation has several prototype-level characteristics that V2 will address:

- Package/application identifiers are still generic/GATE-specific.
- The application structure is heavily study-specific.
- The main dashboard contains a large amount of domain logic and UI.
- Data/domain boundaries need to be separated.
- Database and migration strategy needs to be formalized.
- Core personal-productivity concepts such as generic tasks, habits, activities, goals and projects are not yet first-class concepts.
- Cloud synchronization is not yet part of the core architecture.
- The current project configuration contains optional Firebase infrastructure that should be reassessed rather than carried forward automatically.
- Release signing configuration must remain local/secret and must never be committed with credentials.

V1 should therefore be treated as a **working reference and source of useful ideas**, not as the final V2 architecture.

---

# V2 — Personal Tracker

## Vision

Tracker V2 is initially being built for **one personal user: Muneer**.

It is not being designed as a public multi-account service yet.

The goal is a reliable personal system that brings together:

- Daily planning
- Tasks
- Habits
- Activity logging
- Study
- Goals
- Projects
- Notes
- Journal
- Analytics
- Optional AI assistance

The architecture should still avoid decisions that would make a future multi-user V3 unnecessarily difficult.

## Core principle

> **Offline first. Local data first. Sync second. AI optional.**

The core application must remain useful without an internet connection.

---

## V2 Architecture Direction

### Android

- Kotlin
- Jetpack Compose
- Room / SQLite
- Kotlin Coroutines + Flow
- ViewModel
- WorkManager for background sync
- Adaptive layouts for phones, tablets and foldables
- Modern Android APIs without device-specific hacks

### Web

Planned:

- Next.js
- React
- TypeScript

The web application will be developed after the core Android/domain model is stable.

### Cloud / Sync

Planned:

- PostgreSQL
- Supabase as the initial managed backend candidate
- Secure authentication/sync infrastructure when cloud synchronization is introduced
- Row-level ownership/security designed so V3 can add multi-user accounts without rebuilding the data model

### Deployment

Planned:

- Web application: Vercel
- Database/backend services: Supabase
- Android: APK/AAB build and normal Android distribution

Deployment is intentionally postponed until the application and data model are stable.

---

# V2 Domain Model

The initial core model is expected to contain:

```
Day
Task
Habit
HabitLog
Activity
```

Then expand into:

```
Goal
Project
JournalEntry
StudySubject
StudyTopic
StudySession
Assessment
KnowledgeNote
WeeklyReview
```

Analytics should primarily be derived from source records instead of duplicating mutable aggregate state wherever practical.

---

# V2 Development Stages

## V2.0 — Foundation

Build the clean core:

- Project/package cleanup
- Application architecture
- Room database
- Database migrations
- Day model
- Task model
- Habit model
- Habit log
- Activity model
- Today screen
- Task completion
- Habit completion
- Activity logging
- Core tests
- Error handling
- Backup/export foundation

### Target loop

```
Open Tracker
    ↓
See Today
    ↓
Add/complete task
    ↓
Complete habit
    ↓
Log activity/study
    ↓
See progress
```

---

## V2.1 — Study Module

Turn the existing study functionality into a proper module rather than the entire application.

Planned concepts:

- Study subjects
- Study topics
- Study sessions
- Study plans
- Practice sessions
- Assessments/mock tests
- Knowledge/active-recall notes
- Study timers
- GATE/DA-specific planning
- Study analytics

Existing V1 study functionality will be selectively reused/refactored.

---

## V2.2 — Personal Management

Add:

- Goals
- Projects
- Journal
- Weekly review
- Better analytics
- Personal dashboards
- Recurring tasks
- Reminders
- Achievement/progress system

---

## V2.3 — AI Layer

AI is an enhancement, not a dependency.

Planned capabilities:

- General Tracker AI assistant
- Daily summaries
- Weekly reviews
- Study-context generation
- Natural-language task/plan assistance
- Pattern detection
- Personalized insights

AI providers must be accessed through a provider abstraction rather than coupling the application directly to one vendor.

**Secrets must never be embedded in the Android APK or browser client.**

---

## V2.4 — Multi-device Sync

Introduce:

```
Android
  ↓
Room
  ↓
Sync Outbox
  ↓
WorkManager
  ↓
Secure API / Supabase
  ↓
PostgreSQL
```

The synchronization layer will use:

- Unique operation IDs
- Idempotent operations
- Database unique constraints
- Transactions / ACID guarantees
- Versioned records
- Conflict detection
- Tombstones where required
- Append-only records for suitable event data
- Safe retries after lost network responses

A repeated network request must never accidentally perform a logical action twice.

---

# Database & Sync Rules

## ACID is necessary but not sufficient

Transactions protect database consistency, but network retries can still duplicate an operation.

Therefore V2 sync will use both:

```
ACID
+
Idempotency
+
Unique constraints
+
Versioning
+
Conflict resolution
```

Example:

```
operation_id = UUID
```

If the same operation is transmitted again because the first response was lost, the server must recognize it and avoid applying the logical operation twice.

## Conflict strategy

Different data types may require different strategies:

| Data | Strategy |
|---|---|
| Tasks | Versioned updates / safe merge |
| Task completion | Idempotent state update |
| Habit logs | Unique `(habit_id, date)` |
| Activities | Append-only where appropriate |
| Study sessions | Append-only |
| Journal/notes | Conflict detection / preserve versions |
| Goals/settings | Versioned updates |
| Analytics | Derived from source records |

Do not use network arrival time as the definition of which edit is newer.

---

# Security

V2 security requirements:

- Never commit API keys or service-role secrets.
- Use environment variables/secrets for external services.
- HTTPS/TLS for network communication.
- Validate all externally supplied data.
- Use secure local credential/token storage.
- Keep AI context minimal.
- Avoid logging sensitive personal data.
- Use database constraints and transactions.
- Use proper Room migrations.
- Maintain backups/export capability.
- Run dependency/security checks before releases.

The future V3 multi-user system will additionally require:

- Authentication
- Per-user ownership
- Row-level authorization
- Account isolation
- Session/device management
- Password/account recovery
- Multi-user security testing

These are intentionally not part of the current personal V2 scope.

---

# Branching Strategy

## Branches

```
main
  │
  └── dev
       │
       ├── feature/*
       ├── fix/*
       └── refactor/*
```

### `main`

Stable code only.

**Do not develop directly on `main`.**

### `dev`

Primary V2 development branch.

All major V2 work should land here first.

### Feature branches

For larger changes:

```
dev
 ↓
feature/task-system
 ↓
PR
 ↓
dev
```

Examples:

- `feature/core-database`
- `feature/today-screen`
- `feature/habit-system`
- `feature/study-module`
- `feature/sync-engine`
- `feature/web-app`

---

# Git Rules

1. Never commit secrets.
2. Never commit personal database contents.
3. Never use destructive database migrations for production/personal data.
4. Do not push unfinished V2 development directly to `main`.
5. Keep commits focused and understandable.
6. Build and test before merging significant changes.
7. Update documentation when architecture changes.
8. Prefer pull requests from feature branches into `dev`.
9. Promote `dev` to `main` only after a stable milestone is tested.

---

# Repository Safety

The repository should contain:

- Source code
- Schemas/migrations
- Configuration templates
- Documentation
- Tests
- Build configuration

It should **not** contain:

- Personal tasks
- Journal entries
- Personal study history
- Database files containing private data
- API keys
- Passwords
- Authentication tokens
- Production service-role credentials
- Private signing credentials

A fresh clone should be able to become a clean Tracker installation after following the setup instructions, without receiving Muneer's personal data.

---

# Future V3 — Multi-user Tracker

Multi-user support is deliberately postponed.

When V3 begins, the architecture can evolve toward:

```
User
  ↓
Account
  ↓
Owned data
  ↓
Devices
  ↓
Cloud synchronization
```

Expected V3 additions:

- Sign up / sign in
- Authentication
- User-owned data
- Database row-level security
- Account settings
- Device/session management
- Password recovery
- Optional social login
- Sharing/permissions if ever required
- Public deployment

V3 should be an extension of the stable V2 domain model, not a complete rewrite.

---

# Development Philosophy

Tracker is being rebuilt because the V1 prototype grew around a specific study workflow.

V2 will instead establish a clean general-purpose core first and place study functionality inside that core.

The order is:

```
Foundation
    ↓
Core personal tracking
    ↓
Study module
    ↓
Personal management
    ↓
AI
    ↓
Cloud sync
    ↓
Web
    ↓
Multi-user V3
```

The priority is **correctness, reliability, maintainability and real daily usefulness** over adding large numbers of features quickly.

---

## Current Next Step

The next development task is to finish the V1 repository audit and establish the V2 foundation on the `dev` branch:

1. Verify the complete current source tree.
2. Identify reusable V1 components.
3. Identify code that should be replaced.
4. Define the V2 package/module structure.
5. Design the Room schema.
6. Implement the core Day/Task/Habit/Activity models.
7. Build the Today screen.
8. Add tests.
9. Only then begin the Study module.

---

## License

No public open-source license has been selected yet. The repository is currently private.

