package com.moviles.primer_examen.network

import StudentWithCourse
import com.moviles.primer_examen.model.Course
import com.moviles.primer_examen.model.Student
import com.moviles.primer_examen.model.StudentWithCourseDTO
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/Student/{id}")
    suspend fun getStudentById(@Path("id") id: Int): StudentWithCourseDTO

    @GET("api/Student")
    suspend fun getStudentsByCourse(@Query("courseId") courseId: Int): List<Student>

    @POST("api/Student")
    suspend fun addStudent(@Body student: Student): ApiResponseStudent

    @PUT("api/Student/{id}")
    suspend fun updateStudent(
        @Path("id") id: Int?, @Body student: Student): ApiResponseStudent

    @DELETE("api/Student/{id}")
    suspend fun deleteStudent(@Path("id") id: Int?): Response<Unit>

      @GET("api/Courses")
    suspend fun getCourses(): List<Course>

    @POST("api/Courses")
    suspend fun addCourse(@Body course: Course): Response<Course>

    @PUT("api/Courses/{id}")
    suspend fun updateCourse(@Path("id") id: Int, @Body course: Course): Response<Course>

    @DELETE("api/Courses/{id}")
    suspend fun deleteCourse(@Path("id") id: Int): Response<Unit>
    
}

data class ApiResponseStudent(
    val status: String,
    val message: String,
    val student: Student
)
