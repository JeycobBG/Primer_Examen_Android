package com.moviles.primer_examen.viewmodel

import StudentWithCourse
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.moviles.primer_examen.model.AppDatabase
import com.moviles.primer_examen.model.Course
import com.moviles.primer_examen.model.Student
import com.moviles.primer_examen.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class StudentViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val studentDao = db.studentDao()
    private val courseDao = db.courseDao()

    private val _students = MutableStateFlow<List<Student>>(emptyList()) // Cambiar a Student
    val students: StateFlow<List<Student>> get() = _students

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _selectedStudent = MutableStateFlow<Student?>(null)
    val selectedStudent: StateFlow<Student?> get() = _selectedStudent

    private val _studentWithCourse = MutableStateFlow<StudentWithCourse?>(null)
    val studentWithCourse: StateFlow<StudentWithCourse?> = _studentWithCourse

    fun fetchStudentsByCourse(courseId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val apiStudents = RetrofitInstance.api.getStudentsByCourse(courseId)

                // Convertimos los estudiantes obtenidos a entidades que Room entiende
                val students = apiStudents.map {
                    Student(
                        id = it.id,
                        name = it.name,
                        email = it.email,
                        phone = it.phone,
                        courseId = courseId // Asociamos al curso correspondiente
                    )
                }

                studentDao.clearAll()

                // Insertar los estudiantes en la base de datos
                studentDao.insertAllStudents(students)

                // Actualizar el estado
                _students.value = students

                Log.d("ViewModel", "Estudiantes guardados en la base de datos: $students")
            } catch (e: Exception) {
                Log.e("ViewModelError", "No se pudo conectar a la API: ${e.message}")
                _error.value = "Sin conexión. Mostrando datos locales."

                // Cargar datos locales desde Room
                val localStudents = studentDao.getAllStudents()
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

    fun addStudent(student: Student) {
        viewModelScope.launch {
            try {
                // Crear el estudiante para la API, solo con un curso
                val request = Student(
                    name = student.name,
                    email = student.email,
                    phone = student.phone,
                    courseId = student.courseId // Un solo curso asociado al estudiante
                )

                Log.i("ViewModelInfo", "Sending student data: ${student.name}")

                val response = RetrofitInstance.api.addStudent(request)

                if (response.status == "success") {
                    Log.i("ViewModelInfo", "Estudiante creado: ${response.student.name}")

                    val studentData = response.student
                    studentDao.insertStudent(studentData)

                    _students.value += studentData

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

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            try {
                Log.i("ViewModelInfo", "Actualizando Estudiante: $student")

                val studentData = Student(
                    name = student.name,
                    email = student.email,
                    phone = student.phone,
                    courseId = student.courseId
                )

                val response = RetrofitInstance.api.updateStudent(student.id, studentData)

                if (response.status == "success") {
                    Log.i("ViewModelInfo", "Estudiante actualizado: ${response.student}")

                    _students.value = _students.value.map { existingStudent ->
                        if (existingStudent.id == response.student.id) {
                            response.student
                        } else {
                            existingStudent
                        }
                    }

                    studentDao.updateStudent(response.student)

                } else {
                    Log.e("ViewModelError", "Error al actualizar el estudiante: ${response.message}")
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "Error HTTP: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }

    fun fetchStudentById(studentId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val apiStudent = RetrofitInstance.api.getStudentById(studentId) // StudentWithCourseDTO

                // Decompose and map to Room entities
                val student = Student(
                    id = apiStudent.id,
                    name = apiStudent.name,
                    email = apiStudent.email,
                    phone = apiStudent.phone,
                    courseId = apiStudent.course.id
                )

                val course = Course(
                    id = apiStudent.course.id,
                    name = apiStudent.course.name,
                    description = apiStudent.course.description,
                    imageUrl = apiStudent.course.imageUrl ?: "",
                    schedule = apiStudent.course.schedule ?: "",
                    professor = apiStudent.course.professor ?: ""
                )
                // Save both in local DB
                courseDao.insertCourse(course)
                studentDao.insertStudent(student)

            } catch (e: Exception) {
                Log.e("ViewModelError", "No se pudo obtener el estudiante de la API: ${e.message}")
                _error.value = "Sin conexión. Cargando datos locales."
            } finally {
                // Always load from local Room database
                val localStudentWithCourse = studentDao.getStudentWithCourseById(studentId)
                _studentWithCourse.value = localStudentWithCourse
                _loading.value = false
            }
        }
    }

    fun deleteStudent(studentId: Int?) {
        studentId?.let { id ->
            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.api.deleteStudent(id)

                    if (response.isSuccessful) {

                        studentDao.deleteStudentById(id)

                        _students.value = _students.value.filter { it.id != id }

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