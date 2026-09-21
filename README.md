# Tracker

Tracker is a personal productivity, planning, study, and habit-tracking Android application.

The project is being rebuilt around a reliable **offline-first** core. The long-term product direction is:

```
Plan → Schedule → Execute → Track → Review → Adjust
```

The eventual goal is to combine:

- Tasks and daily planning
- Habits
- Activities and time tracking
- Goals and projects
- Study planning and study sessions
- Journal and knowledge notes
- Reviews and analytics
- Reminders and alarms
- Deterministic scheduling
- Optional AI-assisted planning
- Cloud sync and web access later

---

## Current Status

**Current milestone: V2 foundation complete and stable on `dev`.**

The current Android application is a clean local-first foundation. The next development milestone is **V3.0 Dashboard / UX**.

### Current branches

- `main` — stable milestone branch
- `dev` — active development branch
- `feature/*` — optional branches for larger isolated changes

The current V2 milestone is developed on `dev` and should be promoted to `main` only after the milestone has been validated.

---

# V2 — Local-First Foundation

V2 establishes the core Tracker domain and data-safety foundation before adding scheduling, cloud sync, or AI automation.

## Implemented

### Core domain

- Days
- Tasks
- Habits
- Habit logs
- Activities
- Goals
- Projects
- Journal entries
- Study subjects
- Study topics
- Study sessions
- Assessments
- Knowledge notes
- Weekly reviews

### Android foundation

- Kotlin
- Jetpack Compose
- Material 3
- ViewModel
- Kotlin Coroutines / Flow
- Room / SQLite
- KSP
- Robolectric tests
- GitHub Actions CI
- Debug APK artifact generation

### Data safety

Tracker has a versioned JSON backup/export format containing the application's domain collections and metadata.

Restore behavior is intentionally conservative:

- Backup JSON is validated before import.
- Entity IDs and references are validated.
- Malformed scalar values are rejected.
- Restore replaces the current local dataset only after user confirmation.
- Restore runs inside a Room transaction.
- Failed restores roll back instead of leaving a partially restored database.

Portable JSON was chosen instead of exposing the raw SQLite database as the application's backup format.

### Testing

The current test suite covers:

- Model validation
- Database foundation
- Backup format/version validation
- Broken-reference rejection
- Malformed scalar rejection
- Backup/restore round trips
- Restore transaction rollback

### CI

GitHub Actions builds the Android debug APK.

The CI workflow also uploads the generated APK as a downloadable workflow artifact for testing.

---

# Architecture

The current application is deliberately local-first:

```
Android UI
    ↓
ViewModel
    ↓
Room / SQLite
    ↓
Local Tracker data
```

The future synchronization architecture is planned as:

```
Android
   ↓
Room
   ↓
Sync Outbox
   ↓
WorkManager
   ↓
HTTPS
   ↓
Supabase / PostgreSQL
   ↓
Authentication + Row-Level Security
```

Cloud synchronization is **not part of the current V2 foundation**.

---

# Product Roadmap

## V3.0 — Dashboard / UX

Build the main Tracker experience around a real dashboard rather than separate basic lists.

Planned dashboard elements:

- Today overview
- Current date
- Progress summary
- Next scheduled item
- Today's tasks
- Habits
- Study progress
- Quick add
- Timeline / upcoming items
- Consistent cards, spacing, typography, and navigation
- Adaptive layouts for different screen sizes

The goal is to make the existing domain data useful as a coherent daily workflow.

---

## V3.1 — Plans & Milestones

Introduce structured planning:

- Plans
- Milestones
- Deadlines
- Estimated duration
- Priority
- Dependencies
- Available time
- Fixed commitments
- Plan progress

Example:

```
Plan
 ├── Milestones
 │    ├── Tasks
 │    ├── Study topics
 │    └── Deliverables
 └── Deadline
```

---

## V3.2 — Scheduling Engine

Convert plans into feasible schedules.

The scheduling engine should consider:

- Available time
- Fixed commitments
- Task duration
- Priority
- Deadlines
- Dependencies
- Existing scheduled work
- Study requirements
- Habit routines

The scheduling engine must be deterministic and testable.

---

## V3.3 — Calendar & Execution

Turn schedules into actionable time blocks.

Planned functionality:

- Calendar / timeline
- Scheduled start notifications
- Task reminders
- Recurring reminders
- Snooze
- Skip
- Reschedule
- Planned vs actual time
- Alarm/reminder handling using appropriate modern Android APIs

Exact-alarm behavior will follow Android's current permission and platform requirements rather than assuming unrestricted alarm access.

---

## V3.4 — Analytics & Reviews

Add:

- Daily review
- Weekly review
- Completion trends
- Study-time trends
- Habit consistency
- Planned vs actual analysis
- Goal/project progress
- Review-driven adjustments

Analytics should be derived from source records wherever practical instead of storing duplicated mutable aggregates.

---

## V3.5 — AI Plan → Schedule

AI should assist with planning, not replace the deterministic application logic.

Target flow:

```
Natural-language plan
        ↓
AI
        ↓
Structured plan JSON
        ↓
Validation
        ↓
Deterministic Tracker planner
        ↓
Schedule
```

AI must not directly modify the database.

The application should validate AI-generated structured data before creating or changing Tracker records.

AI should remain optional; core Tracker functionality must work without an AI provider.

---

# Future V4 — Cloud, Sync & Web

After the local Android product is stable, the project can evolve toward:

- Account/authentication
- Multi-device sync
- PostgreSQL/Supabase backend
- Row-level security
- Conflict resolution
- Web application
- Device/session management
- Optional sharing and permissions

The target synchronization model is:

```
Operation ID
    +
Idempotency
    +
Unique constraints
    +
Transactions
    +
Versioning
    +
Conflict handling
```

Different records may require different conflict semantics.

Examples:

| Data | Planned approach |
|---|---|
| Tasks | Versioned updates / safe merge |
| Task completion | Idempotent state update |
| Habit logs | Unique `(habit_id, date)` |
| Activities | Append-only where appropriate |
| Study sessions | Append-only |
| Journal / notes | Conflict detection / preserved versions |
| Goals / settings | Versioned updates |
| Analytics | Derived from source records |

Network arrival time must not be treated as the definition of which user edit is newer.

---

# Data Model

Current core entities:

```
Day
Task
Habit
HabitLog
Activity
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

The model is intentionally designed so study functionality is a module within Tracker rather than the entire application.

---

# Security & Privacy

Tracker is intended to contain personal productivity, study, journal, and activity data.

Repository rules:

- Never commit API keys.
- Never commit passwords or authentication tokens.
- Never commit service-role credentials.
- Never commit personal database contents.
- Never commit private signing credentials.
- Do not log sensitive personal data unnecessarily.
- Validate externally supplied data.
- Use HTTPS/TLS for future network communication.
- Use secure token/credential storage when accounts are introduced.
- Minimize AI context sent to external providers.
- Keep database migrations explicit and safe.
- Maintain backup/export capability.

A fresh clone should produce a clean Tracker installation without containing personal user data.

---

# Development Workflow

## Branching

```
main
  │
  └── dev
       │
       ├── feature/*
       ├── fix/*
       └── refactor/*
```

Use `dev` for active development.

For larger isolated work:

```
dev
 ↓
feature/...
 ↓
Pull Request
 ↓
dev
```

Promote `dev` to `main` at stable milestones.

## Before merging significant changes

Run:

```text
assembleDebug
testDebugUnitTest
lintDebug
```

GitHub Actions provides an additional build verification layer.

---

# Toolchain

The current project uses:

- Android Gradle Plugin 9.1.1
- Gradle 9.3.1
- Kotlin 2.2.10
- KSP 2.3.6
- Room 2.7.0
- Android SDK Platform 36
- JDK 17 in CI

Toolchain upgrades should be deliberate and compatibility-tested rather than performed simply to use the newest available version.

---

# Project Structure

The main application package is:

```
com.muneer.tracker
```

Important areas include:

```
app/
├── src/main/
│   ├── java/com/muneer/tracker/
│   │   ├── MainActivity.kt
│   │   ├── TrackerViewModel.kt
│   │   ├── data/
│   │   │   ├── TrackerDatabase.kt
│   │   │   └── TrackerBackup.kt
│   │   └── ui/
│   │       └── TrackerApp.kt
│   └── res/
└── src/test/
    └── java/com/muneer/tracker/
        ├── TrackerModelTest.kt
        ├── TrackerDatabaseFoundationTest.kt
        └── TrackerBackupTest.kt
```

The structure will expand as V3 introduces dedicated dashboard, planner, scheduling, calendar, analytics, and AI layers.

---

# Design Principles

1. **Offline first.**
2. **Local data first.**
3. **AI optional.**
4. **Deterministic application logic.**
5. **Data safety over convenience.**
6. **Test behavior, not just compilation.**
7. **Keep domain logic separate from UI.**
8. **Avoid unnecessary toolchain churn.**
9. **Never commit secrets or personal data.**
10. **Build incrementally from a stable foundation.**

The priority is:

```
Correctness
    ↓
Reliability
    ↓
Maintainability
    ↓
Usability
    ↓
Automation
```

---

# License

No public open-source license has been selected. The repository is currently private.
