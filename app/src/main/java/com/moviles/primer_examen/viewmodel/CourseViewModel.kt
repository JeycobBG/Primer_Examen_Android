package com.moviles.primer_examen.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.primer_examen.model.AppDatabase
import com.moviles.primer_examen.model.Course
import com.moviles.primer_examen.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourseViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val courseDao = db.courseDao()

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> get() = _courses

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun fetchCourses() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val apiCourses = RetrofitInstance.api.getCourses()
                courseDao.clearAll()
                courseDao.insertAllCourses(apiCourses)
                _courses.value = apiCourses
            } catch (e: Exception) {
                _error.value = "Error loading courses. Showing local data."
                _courses.value = courseDao.getAllCourses()
            } finally {
                _loading.value = false
            }
        }
    }

    fun addCourse(course: Course) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.addCourse(course)
                if (response.isSuccessful) {
                    courseDao.insertCourse(response.body()!!)
                    _courses.value += response.body()!!
                }
            } catch (e: Exception) {
                _error.value = "Error adding course."
            }
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.updateCourse(course.id, course)
                if (response.isSuccessful) {
                    courseDao.updateCourse(response.body()!!)
                    _courses.value = _courses.value.map {
                        if (it.id == course.id) response.body()!! else it
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error updating course."
            }
        }
    }

    fun deleteCourse(courseId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteCourse(courseId)
                if (response.isSuccessful) {
                    courseDao.deleteCourseById(courseId)
                    _courses.value = _courses.value.filter { it.id != courseId }
                }
            } catch (e: Exception) {
                _error.value = "Error deleting course."
            }
        }
    }
}