package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VtuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VtuRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = VtuRepository(db)

        // Prepopulate offline notes metadata if empty
        viewModelScope.launch {
            repository.offlineNotesList.first().let { currentList ->
                if (currentList.isEmpty()) {
                    val defaultNotes = listOf(
                        OfflineNote("note_math1_ch1", "Matrices & Linear Algebra", "Mathematics-I for Computing", "Chapter 1", 1, "https://vtu.ac.in/notes/math1_ch1.pdf"),
                        OfflineNote("note_math1_ch2", "Differential Calculus & Curves", "Mathematics-I for Computing", "Chapter 2", 1, "https://vtu.ac.in/notes/math1_ch2.pdf"),
                        OfflineNote("note_phys_ch1", "Quantum Mechanics Foundations", "Engineering Physics", "Chapter 1", 1, "https://vtu.ac.in/notes/physics_ch1.pdf"),
                        OfflineNote("note_py_ch1", "Python Variables & Statements", "Problem Solving with Python", "Chapter 1", 1, "https://vtu.ac.in/notes/python_ch1.pdf"),
                        OfflineNote("note_ds_ch1", "Abstract Data Types & Linked Lists", "Data Structures with C", "Chapter 1", 3, "https://vtu.ac.in/notes/ds_ch1.pdf")
                    )
                    repository.seedNotes(defaultNotes)
                }
            }
        }
    }

    // Dynamic Lists from Database
    val savedCgpaList: StateFlow<List<SavedCgpa>> = repository.savedCgpaList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyTasks: StateFlow<List<StudyTask>> = repository.studyTasksList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatHistory: StateFlow<List<ChatDoubt>> = repository.chatDoubtList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val offlineNotes: StateFlow<List<OfflineNote>> = repository.offlineNotesList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // CGPA Calculation State
    private val _calculatorSemester = MutableStateFlow(1)
    val calculatorSemester = _calculatorSemester.asStateFlow()

    // Temporary list of subjects being calculated in the UI
    private val _calculatorSubjects = MutableStateFlow<List<CalculatorSubjectState>>(emptyList())
    val calculatorSubjects = _calculatorSubjects.asStateFlow()

    private val _calculatedSgpa = MutableStateFlow<Double?>(null)
    val calculatedSgpa = _calculatedSgpa.asStateFlow()

    data class CalculatorSubjectState(
        val code: String,
        val name: String,
        val credits: Int,
        val grade: String // "S", "A", "B", "C", "D", "E", "F"
    )

    init {
        // Load initial semester subjects
        loadCalculatorSemester(1)
    }

    fun loadCalculatorSemester(sem: Int) {
        _calculatorSemester.value = sem
        val subjects = repository.semestersRoadmap.find { it.semester == sem }?.subjects ?: emptyList()
        _calculatorSubjects.value = subjects.map {
            CalculatorSubjectState(it.code, it.name, it.credits, "S")
        }
        _calculatedSgpa.value = null
    }

    fun updateSubjectGrade(code: String, newGrade: String) {
        _calculatorSubjects.value = _calculatorSubjects.value.map {
            if (it.code == code) it.copy(grade = newGrade) else it
        }
    }

    fun calculateSgpa() {
        val list = _calculatorSubjects.value
        if (list.isEmpty()) return

        var totalCredits = 0
        var totalPoints = 0.0

        for (sub in list) {
            val gp = when (sub.grade) {
                "S" -> 10.0
                "A" -> 9.0
                "B" -> 8.0
                "C" -> 7.0
                "D" -> 6.0
                "E" -> 5.0
                else -> 0.0
            }
            totalCredits += sub.credits
            totalPoints += (sub.credits * gp)
        }

        val sgpa = if (totalCredits > 0) totalPoints / totalCredits else 0.0
        _calculatedSgpa.value = String.format("%.2f", sgpa).toDouble()
    }

    fun saveCurrentCgpaCalculation() {
        val sgpaVal = _calculatedSgpa.value ?: return
        viewModelScope.launch {
            // Find overall cumulative CGPA
            val currentList = savedCgpaList.value
            val semesterName = "Semester ${_calculatorSemester.value}"
            
            // Calculate new cumulative CGPA
            val count = currentList.size + 1
            val sumSgpa = currentList.sumOf { it.sgpa } + sgpaVal
            val newCgpa = sumSgpa / count

            repository.saveCgpa(SavedCgpa(
                semesterName = semesterName,
                sgpa = sgpaVal,
                cgpa = String.format("%.2f", newCgpa).toDouble()
            ))
        }
    }

    fun deleteSavedCgpa(id: Int) {
        viewModelScope.launch {
            repository.deleteCgpa(id)
        }
    }

    // AI Doubt Solver State
    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading = _isAiLoading.asStateFlow()

    fun askDoubt(message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            _isAiLoading.value = true
            // Save user prompt
            repository.saveChat(ChatDoubt(sender = "user", message = message))
            
            // Query Gemini
            val reply = repository.askAiDoubt(message)
            
            // Save AI result
            repository.saveChat(ChatDoubt(sender = "assistant", message = reply))
            _isAiLoading.value = false
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    // AI Project Generator State
    private val _isProjectLoading = MutableStateFlow(false)
    val isProjectLoading = _isProjectLoading.asStateFlow()

    private val _generatedProjects = MutableStateFlow("")
    val generatedProjects = _generatedProjects.asStateFlow()

    fun generateProjects(domain: String, sem: Int) {
        viewModelScope.launch {
            _isProjectLoading.value = true
            _generatedProjects.value = ""
            val output = repository.generateProjectIdeas(domain, sem)
            _generatedProjects.value = output
            _isProjectLoading.value = false
        }
    }

    // Study Planner Addition
    fun addStudyTask(title: String, dueDate: String, category: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTask(StudyTask(title = title, dueDate = dueDate, category = category))
        }
    }

    fun toggleTaskCompletion(task: StudyTask) {
        viewModelScope.launch {
            repository.addTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: StudyTask) {
        viewModelScope.launch {
            repository.removeTask(task)
        }
    }

    // Notes Download Simulation
    fun downloadNote(id: String) {
        viewModelScope.launch {
            // Find note and simulate internet download lag
            _noteDownloadProgress.value = _noteDownloadProgress.value.toMutableMap().apply {
                put(id, 10)
            }
            kotlinx.coroutines.delay(400)
            _noteDownloadProgress.value = _noteDownloadProgress.value.toMutableMap().apply {
                put(id, 65)
            }
            kotlinx.coroutines.delay(400)
            _noteDownloadProgress.value = _noteDownloadProgress.value.toMutableMap().apply {
                put(id, 100)
            }
            repository.updateNoteDownload(id, true, "/storage/emulated/0/Download/$id.pdf")
            kotlinx.coroutines.delay(800)
            _noteDownloadProgress.value = _noteDownloadProgress.value.toMutableMap().apply {
                remove(id)
            }
        }
    }

    private val _noteDownloadProgress = MutableStateFlow<Map<String, Int>>(emptyMap())
    val noteDownloadProgress = _noteDownloadProgress.asStateFlow()

    // Solved Papers Simulation
    private val _solvedPapersDownloadProgress = MutableStateFlow<Map<String, Int>>(emptyMap())
    val solvedPapersDownloadProgress = _solvedPapersDownloadProgress.asStateFlow()
    
    private val _downloadedPapers = MutableStateFlow<Set<String>>(emptySet())
    val downloadedPapers = _downloadedPapers.asStateFlow()

    fun downloadSolvedPaper(paperTitle: String) {
        viewModelScope.launch {
            _solvedPapersDownloadProgress.value = _solvedPapersDownloadProgress.value.toMutableMap().apply {
                put(paperTitle, 20)
            }
            kotlinx.coroutines.delay(300)
            _solvedPapersDownloadProgress.value = _solvedPapersDownloadProgress.value.toMutableMap().apply {
                put(paperTitle, 75)
            }
            kotlinx.coroutines.delay(300)
            _solvedPapersDownloadProgress.value = _solvedPapersDownloadProgress.value.toMutableMap().apply {
                put(paperTitle, 100)
            }
            _downloadedPapers.value = _downloadedPapers.value + paperTitle
            kotlinx.coroutines.delay(800)
            _solvedPapersDownloadProgress.value = _solvedPapersDownloadProgress.value.toMutableMap().apply {
                remove(paperTitle)
            }
        }
    }

    // State properties for Practice module
    private val _practiceSubjectIndex = MutableStateFlow(0)
    val practiceSubjectIndex = _practiceSubjectIndex.asStateFlow()

    private val _practiceQuestions = MutableStateFlow(repository.practiceQuestions)
    val practiceQuestions = _practiceQuestions.asStateFlow()

    private val _selectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap()) // maps mcq Index to selected option Index
    val selectedAnswers = _selectedAnswers.asStateFlow()

    fun selectPracticeSubject(index: Int) {
        _practiceSubjectIndex.value = index
        _selectedAnswers.value = emptyMap()
    }

    fun submitAnswer(mcqIndex: Int, optionIndex: Int) {
        _selectedAnswers.value = _selectedAnswers.value.toMutableMap().apply {
            put(mcqIndex, optionIndex)
        }
    }

    // Static collections exposed directly
    val semestersRoadmap = repository.semestersRoadmap
    val hackathonsAndCirculars = repository.hackathonsAndCirculars
    val techUpdates = repository.techUpdates
    val trendingSkills = repository.trendingSkills
    val staticSolvedPapers = repository.solvedPapers
}
