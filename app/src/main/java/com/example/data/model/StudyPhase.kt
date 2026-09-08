package com.example.data.model

import java.time.LocalDate
import java.time.Month

enum class PhaseId(val number: Int) {
    PHASE_1(1),
    PHASE_2(2),
    PHASE_3(3)
}

data class PhaseScheduleInfo(
    val phaseId: PhaseId,
    val title: String,
    val dateRangeText: String,
    val description: String,
    val focusSubjects: List<String>,
    val excludedSubjects: List<String>,
    val mandatoryDailyAptitudeHours: Double = 1.25, // 1.0 to 1.5 Hours invariant
    val dailyBlocksSummary: List<String>
)

object StudyPhaseManager {

    val PHASES = listOf(
        PhaseScheduleInfo(
            phaseId = PhaseId.PHASE_1,
            title = "Phase 1: The Exact Overlap (GATE CS & GATE DA)",
            dateRangeText = "Starting Now (Sep – Late Oct)",
            description = "Focused strictly on the shared syllabus between GATE CS and GATE DA.",
            focusSubjects = listOf(
                "Data Structures & Algorithms",
                "Database Management & SQL",
                "Mathematics (Linear Algebra, Calculus, Probability & Statistics)"
            ),
            excludedSubjects = listOf(
                "Computer Organization & Architecture (COA)",
                "Digital Logic",
                "Theory of Computation",
                "Compiler Design",
                "Operating Systems",
                "Computer Networks"
            ),
            mandatoryDailyAptitudeHours = 1.25,
            dailyBlocksSummary = listOf(
                "Block 1: 2.5h Exact Overlap (DSA / DBMS & SQL / Math)",
                "Block 2: 1.5h Overlap Deep Practice (Algorithms & SQL Problem Sets)",
                "Block 3: 1.0h to 1.5h Locked Mandatory Invariant (Aptitude & Basic Maths / TCS NQT)"
            )
        ),
        PhaseScheduleInfo(
            phaseId = PhaseId.PHASE_2,
            title = "Phase 2: Non-Overlapped Core (CS & DA)",
            dateRangeText = "Ends Mid/3rd Week of December (~Dec 20)",
            description = "Focused on the distinct subjects unique to each paper (strictly excluding COA and Digital Logic).",
            focusSubjects = listOf(
                "CS Unique: Theory of Computation",
                "CS Unique: Compiler Design",
                "CS Unique: Operating Systems",
                "CS Unique: Computer Networks",
                "CS Unique: Discrete Mathematics",
                "DA Unique: Machine Learning (including Regression & Predictive models)",
                "DA Unique: Artificial Intelligence"
            ),
            excludedSubjects = listOf("Computer Organization & Architecture (COA)", "Digital Logic"),
            mandatoryDailyAptitudeHours = 1.25,
            dailyBlocksSummary = listOf(
                "Block 1: 2.5h CS Unique (TOC, Compilers, OS, Networks, Discrete Math)",
                "Block 2: 1.5h DA Unique (Machine Learning & AI / Snowflake)",
                "Block 3: 1.0h to 1.5h Locked Mandatory Invariant (Aptitude & Basic Maths / TCS NQT)"
            )
        ),
        PhaseScheduleInfo(
            phaseId = PhaseId.PHASE_3,
            title = "Phase 3: The Final Stretch & Crash Course",
            dateRangeText = "Late Dec to Exam (~Dec 21 – Feb)",
            description = "Hardcore daily revision of all Phase 1 and Phase 2 topics with dedicated Crash Course tracking for COA and Digital Logic.",
            focusSubjects = listOf(
                "Hardcore Daily Revision: Phase 1 Overlap Topics",
                "Hardcore Daily Revision: Phase 2 Core Topics",
                "Dedicated Crash Course: Computer Organization & Architecture (COA)",
                "Dedicated Crash Course: Digital Logic",
                "Full-Length PYQ Mock Analysis & Speed Drills"
            ),
            excludedSubjects = emptyList(),
            mandatoryDailyAptitudeHours = 1.25,
            dailyBlocksSummary = listOf(
                "Block 1: 2.5h Hardcore Daily Revision (Phase 1 & Phase 2 Full Syllabus)",
                "Block 2: 1.5h Dedicated Crash Course Tracking (COA & Digital Logic)",
                "Block 3: 1.0h to 1.5h Locked Mandatory Invariant (Aptitude & Basic Maths Fast Drills)"
            )
        )
    )

    /**
     * Determines phase automatically based on current date:
     * - Phase 1: From now until Oct 31
     * - Phase 2: Nov 1 to Dec 20 (3rd week of December)
     * - Phase 3: Dec 21 to Exam date (February)
     */
    fun determineCurrentPhase(date: LocalDate = LocalDate.now()): PhaseScheduleInfo {
        val month = date.month
        val day = date.dayOfMonth

        return when {
            // Before Nov (e.g. Sep, Oct)
            month in listOf(Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER) -> PHASES[0]
            // Nov through Dec 20
            month == Month.NOVEMBER || (month == Month.DECEMBER && day <= 20) -> PHASES[1]
            // Dec 21 through February
            else -> PHASES[2]
        }
    }
}
