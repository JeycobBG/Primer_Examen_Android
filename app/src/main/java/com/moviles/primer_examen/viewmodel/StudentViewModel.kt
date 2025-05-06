package com.moviles.primer_examen.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.moviles.primer_examen.model.AppDatabase
import com.moviles.primer_examen.model.Student
import com.moviles.primer_examen.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class StudentViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val studentDao = db.studentDao()

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> get() = _students

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun fetchStudentsByCourse(courseId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val apiStudents = RetrofitInstance.api.getStudentsByCourse(courseId)
                _students.value = apiStudents

                // Actualizar Room
                studentDao.clearAll()
                studentDao.insertAll(apiStudents)
                Log.d("ViewModel", "Estudiantes guardados en la base de datos: ${studentDao.getStudentsByCourseId(courseId)}")

            } catch (e: Exception) {
                Log.e("ViewModelError", "No se pudo conectar a la API: ${e.message}")
                _error.value = "Sin conexión. Mostrando datos locales."

                // Cargar desde Room usando courseId
                val localStudents = studentDao.getStudentsByCourseId(courseId)
                if (localStudents.isNotEmpty()) {
                    _students.value = localStudents
                    Log.i("ViewModelInfo", "Datos cargados desde Room.")
                    Log.d("RoomDebug", "Estudiantes recuperados de Room: ${_students.value}")
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
                // 1. Construcción de los datos del estudiante para el backend
                val studentData: Map<String, RequestBody> = mutableMapOf<String, RequestBody>().apply {
                    put("name", student.name.toRequestBody("text/plain".toMediaType()))
                    put("email", student.email.toRequestBody("text/plain".toMediaType()))
                    put("phone", student.phone.toRequestBody("text/plain".toMediaType()))
                    put("courseId", student.courseId.toString().toRequestBody("text/plain".toMediaType()))
                }

                Log.i("ViewModelInfo", "Sending student data: ${student.name}")

                // 2. Llamada al backend para agregar el estudiante
                val response = RetrofitInstance.api.addStudent(studentData)

                // 3: Verificar el status de la respuesta
                if (response.status == "success") {
                    Log.i("ViewModelInfo", "Estudiante creado: ${response.student.name}")

                    // 4: Guardar el nuevo estudiante en Room
                    studentDao.insertAll(listOf(response.student))

                    // 5: Actualizar la lista observable de estudiantes (si es necesario)
                    _students.value += response.student

                } else {
                    // Si el status no es "success", mostrar el mensaje de error
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
                Log.i("ViewModelInfo", "Updating Student: $student")

                // Realizar la solicitud para actualizar el estudiante en el backend
                val response = RetrofitInstance.api.updateStudent(student.id, student)

                // Comprobar si la respuesta fue exitosa
                if (response.status == "success") {
                    // Actualizar la lista local de estudiantes
                    _students.value = _students.value.map { existingStudent ->
                        if (existingStudent.id == response.student.id) response.student else existingStudent
                    }

                    // Actualizar el estudiante en Room (Base de datos local)
                    studentDao.updateStudent(response.student)
                    Log.i("ViewModelInfo", "Updated Student: ${response.student}")

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
                        val updatedList = _students.value.filter { it.id != id }
                        _students.value = updatedList

                        studentDao.clearAll()
                        studentDao.insertAll(updatedList) // actualiza Room sin ese estudiante

                        Log.i("ViewModelInfo", "Estudiante eliminado correctamente")
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