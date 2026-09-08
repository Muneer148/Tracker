package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StudyBlockEntity::class,
        OswaalLogEntity::class,
        MockTestLogEntity::class,
        ActiveRecallNoteEntity::class,
        ChatMessageEntity::class,
        SyllabusTopicEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studyDao(): StudyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gate_study_tracker.db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                populateInitialData(database.studyDao())
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(dao: StudyDao) {
            // Preload Oswaal sample chapters for realistic initial experience
            dao.insertOswaalLog(
                OswaalLogEntity(
                    chapterName = "Linear Algebra (Matrices & Eigenvalues)",
                    subject = "Engineering Mathematics",
                    questionsAttempted = 35,
                    accuracyRate = 82.5f,
                    reviewNeeded = false,
                    dateLogged = "Yesterday",
                    notes = "Oswaal Ch 1 - Mastered Cayley-Hamilton and rank calculations."
                )
            )
            dao.insertOswaalLog(
                OswaalLogEntity(
                    chapterName = "Probability & Bayes Theorem",
                    subject = "Engineering Mathematics",
                    questionsAttempted = 28,
                    accuracyRate = 64.0f,
                    reviewNeeded = true,
                    dateLogged = "2 days ago",
                    notes = "Oswaal Ch 3 - Re-check conditional probability density functions."
                )
            )
            dao.insertOswaalLog(
                OswaalLogEntity(
                    chapterName = "General Aptitude: Quantitative & Spatial",
                    subject = "General Aptitude",
                    questionsAttempted = 40,
                    accuracyRate = 90.0f,
                    reviewNeeded = false,
                    dateLogged = "3 days ago",
                    notes = "Oswaal Aptitude Section A - High speed on ratio, time & work."
                )
            )

            // Preload Mock Test sample
            dao.insertMockTestLog(
                MockTestLogEntity(
                    testName = "GATE CS 2023 Official Free PYQ Mock",
                    paperCategory = "GATE CS",
                    scoreObtained = 64.5f,
                    totalMarks = 100.0f,
                    dateTaken = "Sep 04",
                    keyMistakes = "Tricky questions in Computer Networks (Subnetting) and Pointers in C."
                )
            )
            dao.insertMockTestLog(
                MockTestLogEntity(
                    testName = "TCS NQT Advanced Mock - Coding & Aptitude",
                    paperCategory = "TCS NQT",
                    scoreObtained = 78.0f,
                    totalMarks = 100.0f,
                    dateTaken = "Sep 05",
                    keyMistakes = "Need faster DP solution for question 2 (Matrix Chain Multiplication)."
                )
            )

            // Preload Active Recall Core Formulas & Concepts
            dao.insertNote(
                ActiveRecallNoteEntity(
                    title = "Master Theorem for Divide & Conquer",
                    contentMarkdown = "T(n) = a*T(n/b) + Θ(n^k * log^p(n))\nCompare log_b(a) with k:\n1. If log_b(a) > k => T(n) = Θ(n^{log_b(a)})\n2. If log_b(a) = k:\n   - if p > -1 => T(n) = Θ(n^k * log^{p+1}(n))\n   - if p = -1 => T(n) = Θ(n^k * log(log n))\n3. If log_b(a) < k => T(n) = Θ(n^k)",
                    tag = "#Algorithms",
                    reviewTomorrow = true
                )
            )
            dao.insertNote(
                ActiveRecallNoteEntity(
                    title = "Eigenvalues & Matrix Properties",
                    contentMarkdown = "• Sum of eigenvalues = Trace of Matrix (sum of diagonal elements)\n• Product of eigenvalues = Determinant |A|\n• Eigenvalues of A^k are λ^k\n• Symmetric matrix => Real eigenvalues & orthogonal eigenvectors\n• Cayley-Hamilton: Every square matrix satisfies its own characteristic equation: P(A) = 0",
                    tag = "#MathOverlap",
                    reviewTomorrow = true
                )
            )
            dao.insertNote(
                ActiveRecallNoteEntity(
                    title = "Snowflake Architecture & Virtual Warehouses",
                    contentMarkdown = "3 Layers:\n1. Cloud Services Layer (Auth, Metadata, Query Optimizer, Access Control)\n2. Query Processing Layer (Virtual Warehouses: compute clusters, scale up/out)\n3. Database Storage Layer (Hybrid columnar, micro-partitions, immutable)\n• Time Travel: 0-90 days via `AT` or `BEFORE` timestamp\n• Zero-Copy Cloning: Metadata-only copy until modified",
                    tag = "#Snowflake",
                    reviewTomorrow = false
                )
            )
            dao.insertNote(
                ActiveRecallNoteEntity(
                    title = "Normal Forms Summary & BCNF Decomposition",
                    contentMarkdown = "• 1NF: Atomic attributes\n• 2NF: 1NF + No partial dependency (non-prime on proper subset of candidate key)\n• 3NF: 2NF + No transitive dependency (X -> A implies X is superkey OR A is prime)\n• BCNF: For every FD X -> A, X must be a superkey\n• BCNF decomposition is always lossless, but dependency preservation is not guaranteed (unlike 3NF)",
                    tag = "#DBMS/SQL",
                    reviewTomorrow = false
                )
            )

            // Preload 3-Phase Syllabus Allocation & Daily Invariant Topics (AI Context Memory)
            val initialTopics = listOf(
                // Phase 1: Overlap Core (GATE CS & GATE DA)
                SyllabusTopicEntity(
                    phaseNumber = 1,
                    paperCategory = "GATE CS & DA Overlap",
                    subject = "Database Management & SQL",
                    topicName = "Relational Algebra, SQL Joins & Subqueries",
                    isCompleted = true,
                    lastRevisedDate = "Sep 03"
                ),
                SyllabusTopicEntity(
                    phaseNumber = 1,
                    paperCategory = "GATE CS & DA Overlap",
                    subject = "Database Management & SQL",
                    topicName = "Normalization & Normal Forms (1NF, 2NF, 3NF, BCNF)",
                    isCompleted = true,
                    lastRevisedDate = "Sep 05"
                ),
                SyllabusTopicEntity(
                    phaseNumber = 1,
                    paperCategory = "GATE CS & DA Overlap",
                    subject = "Data Structures & Algorithms",
                    topicName = "Binary Trees, BST, AVL & Heap Operations",
                    isCompleted = true,
                    lastRevisedDate = "Sep 04"
                ),
                SyllabusTopicEntity(
                    phaseNumber = 1,
                    paperCategory = "GATE CS & DA Overlap",
                    subject = "Data Structures & Algorithms",
                    topicName = "Graph Algorithms (BFS, DFS, Dijkstra, Bellman-Ford)",
                    isCompleted = false
                ),
                SyllabusTopicEntity(
                    phaseNumber = 1,
                    paperCategory = "GATE CS & DA Overlap",
                    subject = "Mathematics",
                    topicName = "Linear Algebra (Matrices, Eigenvalues & Cayley-Hamilton)",
                    isCompleted = true,
                    lastRevisedDate = "Yesterday"
                ),
                SyllabusTopicEntity(
                    phaseNumber = 1,
                    paperCategory = "GATE CS & DA Overlap",
                    subject = "Mathematics",
                    topicName = "Probability & Statistics (Conditional, Bayes, Distributions)",
                    isCompleted = false,
                    struggled = true,
                    struggleNotes = "Struggled with continuous random variable variance and Bayes theorem joint distributions."
                ),
                SyllabusTopicEntity(
                    phaseNumber = 1,
                    paperCategory = "GATE CS & DA Overlap",
                    subject = "Mathematics",
                    topicName = "Calculus (Limits, Continuity, Derivatives & Maxima/Minima)",
                    isCompleted = false
                ),

                // Phase 2: Non-Overlapped Core (Excl. COA & Digital Logic)
                // CS Unique
                SyllabusTopicEntity(
                    phaseNumber = 2,
                    paperCategory = "GATE CS Unique",
                    subject = "Theory of Computation",
                    topicName = "Finite Automata (DFA/NFA) & Regular Expressions",
                    isCompleted = false
                ),
                SyllabusTopicEntity(
                    phaseNumber = 2,
                    paperCategory = "GATE CS Unique",
                    subject = "Theory of Computation",
                    topicName = "Context-Free Grammars, Pushdown Automata & Turing Machines",
                    isCompleted = false
                ),
                SyllabusTopicEntity(
                    phaseNumber = 2,
                    paperCategory = "GATE CS Unique",
                    subject = "Compiler Design",
                    topicName = "Lexical Analysis, LL(1) & LR Parsing Techniques",
                    isCompleted = false
                ),
                SyllabusTopicEntity(
                    phaseNumber = 2,
                    paperCategory = "GATE CS Unique",
                    subject = "Operating Systems",
                    topicName = "Process Synchronization (Semaphores, Monitors, Mutex)",
                    isCompleted = false
                ),
                SyllabusTopicEntity(
                    phaseNumber = 2,
                    paperCategory = "GATE CS Unique",
                    subject = "Operating Systems",
                    topicName = "Virtual Memory, Paging, TLB & Page Replacement Algorithms",
                    isCompleted = false
                ),
                SyllabusTopicEntity(
                    phaseNumber = 2,
                    paperCategory = "GATE CS Unique",
                    subject = "Computer Networks",
                    topicName = "IPv4/IPv6 Addressing, Subnetting, TCP/UDP & Congestion Control",
                    isCompleted = false
                ),
                SyllabusTopicEntity(
                    phaseNumber = 2,
                    paperCategory = "GATE CS Unique",
                    subject = "Discrete Mathematics",
                    topicName = "Propositional & First-Order Logic, Combinatorics & Graph Theory",
                    isCompleted = false
                ),

                // DA Unique
                SyllabusTopicEntity(
                    phaseNumber = 2,
                    paperCategory = "GATE DA Unique",
                    subject = "Machine Learning",
                    topicName = "Data Preprocessing & Data Cleaning",
                    isCompleted = true,
                    struggled = true,
                    struggleNotes = "Struggled with missing value imputation (KNN vs MICE) and outlier detection using IQR boundaries last week.",
                    lastRevisedDate = "Sep 01"
                ),
                SyllabusTopicEntity(
                    phaseNumber = 2,
                    paperCategory = "GATE DA Unique",
                    subject = "Machine Learning",
                    topicName = "Linear & Logistic Regression, Loss Functions & Regularization (L1/L2)",
                    isCompleted = false
                ),
                SyllabusTopicEntity(
                    phaseNumber = 2,
                    paperCategory = "GATE DA Unique",
                    subject = "Machine Learning",
                    topicName = "Decision Trees, Random Forest, SVM & K-Means Clustering",
                    isCompleted = false
                ),
                SyllabusTopicEntity(
                    phaseNumber = 2,
                    paperCategory = "GATE DA Unique",
                    subject = "Artificial Intelligence",
                    topicName = "Informed/Uninformed Search (A*, BFS/DFS), Heuristics & Adversarial Search",
                    isCompleted = false
                ),

                // Phase 3: Final Stretch & Dedicated Crash Course
                SyllabusTopicEntity(
                    phaseNumber = 3,
                    paperCategory = "Crash Course / Revision",
                    subject = "Computer Organization & Architecture (COA)",
                    topicName = "COA Crash Course: Instruction Pipelining, Branch Hazards & Speedup",
                    isCompleted = false
                ),
                SyllabusTopicEntity(
                    phaseNumber = 3,
                    paperCategory = "Crash Course / Revision",
                    subject = "Computer Organization & Architecture (COA)",
                    topicName = "COA Crash Course: Cache Memory Organization (Direct, Associative, Set-Associative)",
                    isCompleted = false
                ),
                SyllabusTopicEntity(
                    phaseNumber = 3,
                    paperCategory = "Crash Course / Revision",
                    subject = "Digital Logic",
                    topicName = "Digital Logic Crash Course: Boolean Minimization & K-Maps",
                    isCompleted = false
                ),
                SyllabusTopicEntity(
                    phaseNumber = 3,
                    paperCategory = "Crash Course / Revision",
                    subject = "Digital Logic",
                    topicName = "Digital Logic Crash Course: Combinational & Sequential Circuits (Flip-Flops, Counters)",
                    isCompleted = false
                ),
                SyllabusTopicEntity(
                    phaseNumber = 3,
                    paperCategory = "Crash Course / Revision",
                    subject = "Hardcore Full-Syllabus Revision",
                    topicName = "Full-Length Past Year Mock Tests & Formula Recalls (CS & DA)",
                    isCompleted = false
                ),

                // Daily Invariant: Active Across All Phases
                SyllabusTopicEntity(
                    phaseNumber = 0,
                    paperCategory = "Daily Invariant (TCS NQT)",
                    subject = "Aptitude & Basic Maths (TCS NQT Invariant)",
                    topicName = "Quantitative Aptitude: Percentages, Profit & Loss, Ratio, Time & Work",
                    isCompleted = true,
                    lastRevisedDate = "Today"
                ),
                SyllabusTopicEntity(
                    phaseNumber = 0,
                    paperCategory = "Daily Invariant (TCS NQT)",
                    subject = "Aptitude & Basic Maths (TCS NQT Invariant)",
                    topicName = "Logical Reasoning & Basic Verbal English (Speed & Accuracy Drills)",
                    isCompleted = false
                )
            )
            dao.insertSyllabusTopicsIfAbsent(initialTopics)

            // Preload Initial AI Tutor Message with Contextual Memory
            dao.insertChatMessage(
                ChatMessageEntity(
                    role = "model",
                    text = "Hello! I am your GATE & Placements AI Tutor. I have real-time memory access to your 3-Phase Study Plan, Room database logs, daily adherence streaks, and active recall formulas.\n\nYou are currently in **Phase 1: GATE CS & DA Overlap Core** (Data Structures & Algorithms, Database Management & SQL, and Mathematics). Today's mandatory 1.0-1.5h Aptitude block is active.\n\nAsk me anything like:\n• *'What specific data cleaning concepts did I struggle with last week?'*\n• *'Summarize my active recall notes for #DataManagement'*\n• *'Quiz me on BCNF decomposition rules'*",
                    contextSummary = "Phase 1 Active | 3 Study Blocks | 23 Syllabus Topics Seeded | 4 Recall Notes Loaded"
                )
            )
        }
    }
}
