package com.moviles.primer_examen.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.moviles.primer_examen.model.AppDatabase
import com.moviles.primer_examen.model.CreateStudentRequest
import com.moviles.primer_examen.model.Student
import com.moviles.primer_examen.model.StudentCourse
import com.moviles.primer_examen.model.StudentWithCourses
import com.moviles.primer_examen.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class StudentViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val studentDao = db.studentDao()

    private val _students = MutableStateFlow<List<StudentWithCourses>>(emptyList()) // Cambiar a StudentWithCourses
    val students: StateFlow<List<StudentWithCourses>> get() = _students

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun fetchStudentsByCourse(courseId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val apiStudents = RetrofitInstance.api.getStudentsByCourse(courseId)

                // Convertimos a entidades que Room entiende
                val students = apiStudents.map {
                    Student(
                        id = it.id,
                        name = it.name,
                        email = it.email,
                        phone = it.phone
                    )
                }

                studentDao.clearAll()

                val studentCourse = apiStudents.flatMap { student ->
                    student.courses.map { course ->
                        StudentCourse(studentId = student.id, courseId = course.id)
                    }
                }

                studentDao.insertAllStudents(students)
                studentDao.insertStudentCourseRelationship(studentCourse)

                val studentsWithCourses = studentDao.getAllStudentsWithCourses()

                // Actualizar el estado
                _students.value = studentsWithCourses

                Log.d("ViewModel", "Estudiantes guardados en la base de datos: $studentsWithCourses")
            } catch (e: Exception) {
                Log.e("ViewModelError", "No se pudo conectar a la API: ${e.message}")
                _error.value = "Sin conexión. Mostrando datos locales."

                // Cargar datos locales desde Room
                val localStudents = studentDao.getAllStudentsWithCourses()
                if (localStudents.isNotEmpty()) {
                    _students.value = localStudents
                    Log.i("ViewModelInfo", "Datos cargados desde Room.")
                } else {
                    _students.value = emptyList()
                    Log.i("ViewModelInfo", "Room también está vacío.")
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun addStudent(student: StudentWithCourses) {
        viewModelScope.launch {
            try {

                val request = CreateStudentRequest(
                    name = student.student.name,
                    email = student.student.email,
                    phone = student.student.phone,
                    courseIds = student.courses.map { it.id }
                )

                Log.i("ViewModelInfo", "Sending student data: ${student.student.name}")

                val response = RetrofitInstance.api.addStudent(request)

                if (response.status == "success") {
                    Log.i("ViewModelInfo", "Estudiante creado: ${response.student.student.name}")

                    val studentCourseRelationships = response.student.courses.map { course ->
                        StudentCourse(studentId = response.student.student.id!!, courseId = course.id)
                    } ?: emptyList()

                    if (studentCourseRelationships.isNotEmpty()) {
                        studentDao.insertStudentCourseRelationship(studentCourseRelationships)
                    }

                    _students.value += response.student

                } else {
                    Log.e("ViewModelError", "Error al agregar estudiante: ${response.message}")
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }

    fun updateStudent(studentWithCourses: StudentWithCourses) {
        viewModelScope.launch {
            try {
                Log.i("ViewModelInfo", "Updating Student: ${studentWithCourses.student}")

                val studentData: Map<String, RequestBody> = mutableMapOf<String, RequestBody>().apply {
                    put("name", studentWithCourses.student.name.toRequestBody("text/plain".toMediaType()))
                    put("email", studentWithCourses.student.email.toRequestBody("text/plain".toMediaType()))
                    put("phone", studentWithCourses.student.phone.toRequestBody("text/plain".toMediaType()))

                    val courseIds = studentWithCourses.courses.joinToString(",") { it.id.toString() }
                    put("courseIds", courseIds.toRequestBody("text/plain".toMediaType()))
                }

                val response = RetrofitInstance.api.updateStudent(studentWithCourses.student.id, studentData)

                if (response.status == "success") {
                    Log.i("ViewModelInfo", "Updated Student: ${response.student.student}")

                    _students.value = _students.value.map { existingStudent ->
                        if (existingStudent.student.id == response.student.student.id) {
                            response.student
                        } else {
                            existingStudent
                        }
                    }

                    studentDao.updateStudent(response.student.student)

                    studentDao.clearStudentCourses(response.student.student.id!!)

                    val studentCourseRelationships = response.student.courses.map { course ->
                        StudentCourse(studentId = response.student.student.id, courseId = course.id)
                    }
                    studentDao.insertStudentCourseRelationship(studentCourseRelationships)

                } else {
                    Log.e("ViewModelError", "Failed to update student: ${response.message}")
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }

    fun deleteStudent(studentId: Int?) {
        studentId?.let { id ->
            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.api.deleteStudent(id)

                    if (response.isSuccessful) {
                        studentDao.deleteStudentCoursesByStudentId(id)

                        studentDao.deleteStudentById(id)

                        _students.value = _students.value.filter { it.student.id != id }

                        Log.i("ViewModelInfo", "Estudiante eliminado correctamente de la API y la base de datos local")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("ViewModelError", "Error HTTP: ${response.code()}, Body: $errorBody")
                    }
                } catch (e: Exception) {
                    Log.e("ViewModelError", "Error eliminando estudiante: ${e.message}")
                }
            }
        } ?: Log.e("ViewModelError", "Error: studentId es null")
    }


    fun clearError() {
        _error.value = null
    }
}