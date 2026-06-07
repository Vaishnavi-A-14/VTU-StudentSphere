package com.example.data

import com.example.BuildConfig
import com.example.network.Content
import com.example.network.GenerateContentRequest
import com.example.network.Part
import com.example.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

// Static resource classes to populate core VTU modules
data class Subject(val code: String, val name: String, val credits: Int, val description: String)
data class SemesterData(val semester: Int, val totalCredits: Int, val subjects: List<Subject>)

data class HackathonInfo(val id: Int, val title: String, val organizer: String, val date: String, val tags: List<String>, val link: String)
data class TechUpdate(val id: Int, val title: String, val source: String, val time: String, val snippet: String)
data class TrendingSkill(val title: String, val demand: Int, val difficulty: String, val keywords: List<String>, val description: String)

data class SolvedPaper(val subject: String, val year: String, val questionsCount: Int, val downloadUrl: String)
data class PracticeMcq(val question: String, val options: List<String>, val correctIndex: Int, val explanation: String)
data class SubjectPractice(val subjectName: String, val chapterName: String, val mcqs: List<PracticeMcq>)

class VtuRepository(private val db: AppDatabase) {

    val savedCgpaList: Flow<List<SavedCgpa>> = db.cgpaDao().getAll()
    val studyTasksList: Flow<List<StudyTask>> = db.studyTaskDao().getAll()
    val chatDoubtList: Flow<List<ChatDoubt>> = db.chatDoubtDao().getAll()
    val offlineNotesList: Flow<List<OfflineNote>> = db.offlineNoteDao().getAll()

    // Database Actions
    suspend fun saveCgpa(cgpa: SavedCgpa) = db.cgpaDao().insert(cgpa)
    suspend fun deleteCgpa(id: Int) = db.cgpaDao().delete(id)

    suspend fun addTask(task: StudyTask) = db.studyTaskDao().insert(task)
    suspend fun removeTask(task: StudyTask) = db.studyTaskDao().delete(task)

    suspend fun saveChat(chat: ChatDoubt) = db.chatDoubtDao().insert(chat)
    suspend fun clearChat() = db.chatDoubtDao().clearAll()

    suspend fun seedNotes(notes: List<OfflineNote>) = db.offlineNoteDao().insertAll(notes)
    suspend fun updateNoteDownload(id: String, isDownloaded: Boolean, localPath: String?) =
        db.offlineNoteDao().updateDownloadState(id, isDownloaded, localPath)

    // Gemini Doubt Solver API Interaction
    suspend fun askAiDoubt(question: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey == "MY_GEMINI_API_KEY" || apiKey.isEmpty()) {
            return "Demo Response: (Please set your Gemini API key in AI Studio Secrets)\n\n" +
                   "Under the VTU 2025 Scheme, computing credits got revised to emphasize skill development. " +
                   "This subject covers computer fundamental logic, which includes boolean algebraic simplify rules and digital gate design."
        }

        // Fetch preceding messages for conversation context
        val contextHistory = db.chatDoubtDao().getAll().first()
        val formattedContents = mutableListOf<Content>()
        
        // Form system prompt + dialogue history
        formattedContents.add(Content(parts = listOf(Part(
            text = "You are VTU StudentSphere AI, a dedicated assistant for students under the VTU 2025 scheme. " +
                   "Answer accurately, explaining engineering and core academic concepts simply. Keep response under 150 words."
        ))))
        
        contextHistory.takeLast(10).forEach { item ->
            formattedContents.add(Content(parts = listOf(Part(text = "${item.sender}: ${item.message}"))))
        }
        
        formattedContents.add(Content(parts = listOf(Part(text = "user: $question"))))

        return try {
            val request = GenerateContentRequest(contents = formattedContents)
            val response = RetrofitClient.geminiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No insight received from AI Solver."
        } catch (e: Exception) {
            "Connection Error: ${e.localizedMessage}. Under the VTU 2025 Scheme, topics such as OOP with Java and Data Structures are key. Make sure your internet connection is active."
        }
    }

    // AI Project Idea Generator
    suspend fun generateProjectIdeas(domain: String, sem: Int): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val prompt = "Generate 3 high-quality engineering project ideas for VTU semester $sem student in the domain of $domain. " +
                "Include a cool title, tech stack, and a brief description for each. Output in a neat bulleted presentation."

        if (apiKey == "MY_GEMINI_API_KEY" || apiKey.isEmpty()) {
            return "• **Smart VTU 2025 GPA Tracker**\n" +
                   "  - Target/Domain: Web / Mobile App\n" +
                   "  - Tech: Kotlin, Room Database, Jetpack Compose\n" +
                   "  - Benefit: Automates GPA and credit estimations customized to 2025 grading changes.\n\n" +
                   "• **EcoSphere Smart Room Venting**\n" +
                   "  - Target/Domain: IoT & Embedded Systems\n" +
                   "  - Tech: ESP32, DHT11, Firebase, Kotlin\n" +
                   "  - Benefit: Automates class ventilation based on live student occupancy telemetry."
        }

        return try {
            val request = GenerateContentRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))))
            val response = RetrofitClient.geminiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Unavailable at this moment."
        } catch (e: Exception) {
            "Unable to generate ideas due to: ${e.localizedMessage}"
        }
    }

    // Static/Prepopulated Data for VTU 2025 Scheme

    val semestersRoadmap = listOf(
        SemesterData(1, 20, listOf(
            Subject("25MAT11", "Mathematics-I for Computing", 4, "Covers calculus, linear algebra, vector theory, and computation formulas."),
            Subject("25PHY12", "Engineering Physics", 4, "Focuses on quantum mechanics, optics, semiconductors, and laser physics."),
            Subject("25CIV13", "Civil Engineering & Mechanics", 3, "Essential statics, stress-strain, and structural dynamics principles."),
            Subject("25ME14", "Basic Mechanical Engineering", 3, "Covers thermodynamics, fluid power, and energy conversion systems."),
            Subject("25ENG15", "Communicative English", 2, "Technical grammar, formal correspondence, and comprehension skills."),
            Subject("25POP16", "Problem Solving with Python", 4, "Core logic programming, loops, lists, and algorithm designs.")
        )),
        SemesterData(2, 20, listOf(
            Subject("25MAT21", "Mathematics-II for Computing", 4, "Differential equations, double/triple integrals, numerical analyses."),
            Subject("25CHE22", "Engineering Chemistry", 4, "Battery materials, corrosion science, polymer chemistry, and environmental tech."),
            Subject("25ELN23", "Basic Electronics", 3, "Diodes, BJTs, op-amps, and fundamental logic circuits."),
            Subject("25KAN24", "Balake Kannada / Samskrutika Kannada", 1, "State language communication customized for VTU students."),
            Subject("25COE25", "Constitution of India & Professional Ethics", 1, "Indian constitutional structure, human rights, and professional responsibilities."),
            Subject("25CAD26", "Computer Aided Engineering Drawing", 3, "Orthographic projections, multi-view alignments utilizing CAD instruments."),
            Subject("25BEE27", "Basic Electrical Engineering", 4, "AC/DC circuitry, polyphase mechanisms, and magnetic induction loops.")
        )),
        SemesterData(3, 22, listOf(
            Subject("25CS31", "Data Structures with C", 4, "Pointers, lists, stacks, queues, binary trees, sorting & searching algorithms."),
            Subject("25CS32", "Computer Organization & Architecture", 3, "CPU architecture, memory hierarchies, I/O pipeline designs."),
            Subject("25CS33", "Object Oriented Programming (Java/C++)", 3, "Inheritance, polymorphism, encapsulation, exception modules."),
            Subject("25CS34", "Discrete Math Structures", 3, "Combinatorics, graph properties, set relations, and formal syntax theory.")
        )),
        SemesterData(4, 22, listOf(
            Subject("25CS41", "Design & Analysis of Algorithms", 4, "Divide-and-conquer, greedy models, dynamic logic, NP-completeness."),
            Subject("25CS42", "Operating Systems", 3, "Process synchronization, deadlock avoidance, virtual page tables."),
            Subject("25CS43", "Microprocessors & Microcontrollers", 4, "8086 assembly, interrupt vectors, ADC/DAC controller interfacing."),
            Subject("25CS44", "Web Application Development", 3, "Modern frontend systems, backend REST APIs, client integrations.")
        )),
        SemesterData(5, 22, listOf(
            Subject("25CS51", "Software Engineering & Project Management", 3, "Agile processes, UML notations, verification/validation criteria."),
            Subject("25CS52", "Computer Networks", 4, "OSI Layers, routing algorithms, transport congestion control loops."),
            Subject("25CS53", "Database Management Systems", 4, "E-R diagrams, relational calculus, normalization, SQL, index tables.")
        )),
        SemesterData(6, 20, listOf(
            Subject("25CS61", "Compiler Design", 4, "Lexical analysis, bottom-up parsing, intermediate code representation."),
            Subject("25CS62", "Artificial Intelligence & ML", 4, "Supervised classifiers, backpropagation, heuristic state searches.")
        )),
        SemesterData(7, 16, listOf(
            Subject("25CS71", "Cloud Computing & Services", 4, "Virtualization, SaaS/PaaS models, AWS/GCP system topologies."),
            Subject("25CS72", "Cryptography & Network Security", 3, "Symmetric/Asymmetric encryption algorithms, digital signatures.")
        )),
        SemesterData(8, 12, listOf(
            Subject("25CS81", "Major Project Work Phase-II", 10, "Design, document, implement, and review an engineering project."),
            Subject("25CS82", "Technical Seminar / Evaluation", 2, "Deliver a formal presentation on a cutting-edge technological development.")
        ))
    )

    val hackathonsAndCirculars = listOf(
        HackathonInfo(1, "Smart India Hackathon (SIH) 2025", "Ministry of Education", "August 12, 2025", listOf("National", "Software", "Hardware"), "https://sih.gov.in"),
        HackathonInfo(2, "VTU State Level Hackfest 2025", "VTU Belagavi HQ", "September 05, 2025", listOf("State", "IoT", "AI"), "https://vtu.ac.in"),
        HackathonInfo(3, "IEEE Student Chapter Hack", "IEEE Bangalore Section", "October 18, 2025", listOf("Regional", "Cybersecurity", "Blockchain"), "https://ieee.org"),
        HackathonInfo(4, "CIRCULAR: 2025 Scheme Credit Evaluation Update", "Registrar VTU", "June 02, 2025", listOf("Official", "Academic"), "https://vtu.ac.in/circulars"),
        HackathonInfo(5, "CIRCULAR: Practical Examination Guidelines", "Evaluation Section", "June 04, 2025", listOf("Official", "Exams"), "https://vtu.ac.in/exams")
    )

    val techUpdates = listOf(
        TechUpdate(1, "Kotlin 2.2 Released with Heavy K2 Compiler Improvements", "Kotlin Blog", "2 hours ago", "Kotlin 2.2 introduces faster compilation times, streamlined Compose plugin compilation features, and enhanced multiplatform speed indices."),
        TechUpdate(2, "Gemini 3.5 Flash Setting New Speed Records for Inference", "DeepMind Insights", "5 hours ago", "With enhanced context sizes and incredibly low response latency parameters, Gemini 3.5 Flash is ideal for mobile client integrations."),
        TechUpdate(3, "Jetpack Compose Elevates Android Edge-to-Edge Integrations", "Android Developers", "1 day ago", "The latest Material 3 updates make edge-to-edge screens native, offering automatically synced WindowInsets controllers.")
    )

    val trendingSkills = listOf(
        TrendingSkill("Artificial Intelligence / Prompt Engineering", 95, "Medium", listOf("LLMs", "Vector DBs", "Prompt Styling"), "Learn the art of designing dynamic prompts and integrating cognitive AI layers with production systems."),
        TrendingSkill("Mobile Development (Jetpack Compose / Kotlin)", 90, "Easy", listOf("Kotlin", "M3 Styling", "Coroutines"), "Build responsive mobile native apps with beautiful design systems, responsive threads, and Room database caching."),
        TrendingSkill("Cloud Topologies & DevOps", 85, "Hard", listOf("Docker", "Kubernetes", "AWS CI/CD"), "Deploy, scale, and secure enterprise applications across AWS, Google Cloud, and multi-tenant architectures.")
    )

    val solvedPapers = listOf(
        SolvedPaper("Mathematics-I for Computing", "Jan 2025", 8, "https://vtu.ac.in/solved/math1_2025.pdf"),
        SolvedPaper("Engineering Physics", "Feb 2025", 6, "https://vtu.ac.in/solved/physics_2025.pdf"),
        SolvedPaper("Problem Solving with Python", "Jan 2025", 10, "https://vtu.ac.in/solved/python_2025.pdf")
    )

    val practiceQuestions = listOf(
        SubjectPractice("Mathematics-I for Computing", "Chapter 1: Matrices & Eigenvalues", listOf(
            PracticeMcq(
                "If A is a square matrix of order n, then the rank of A is less than n if:",
                listOf("|A| ≠ 0", "|A| = 0", "A is identity", "A is symmetric"),
                1,
                "A matrix's determinant becomes 0 when its vectors are linearly dependent, meaning its rank is strictly less than n."
            ),
            PracticeMcq(
                "What are the eigenvalues of an orthogonal matrix?",
                listOf("Always 0", "Always ±1", "Unit modulus", "Any real number"),
                2,
                "Eigenvalues of orthogonal matrices are always values of unit modulus because their transformations preserve length."
            )
        )),
        SubjectPractice("Problem Solving with Python", "Chapter 1: Control Streams", listOf(
            PracticeMcq(
                "What is the output of values = [2 * x for x in range(3)]?",
                listOf("[0, 2, 4]", "[2, 4, 6]", "[0, 1, 2]", "[2, 2, 2]"),
                0,
                "range(3) produces 0, 1, and 2. Multiplying by 2 yields [0, 2, 4]."
            ),
            PracticeMcq(
                "Which keyword represents a block of code that executes when no exceptions are raised?",
                listOf("except", "try", "else", "finally"),
                2,
                "The else block inside a try-catch executes purely when absolutely no exceptions were raised."
            )
        ))
    )
}
