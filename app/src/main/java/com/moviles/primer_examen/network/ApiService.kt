package com.moviles.primer_examen.network

import com.moviles.primer_examen.model.Student
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("api/student/{id}")
    suspend fun getStudentById(@Path("id") id: Int): Student

    @GET("api/student")
    suspend fun getStudentsByCourse(@Query("courseId") courseId: Int): List<Student>

    @POST("api/student")
    suspend fun addStudent(
        @Body studentData: Map<String, RequestBody>
    ): ApiResponseStudent

    @PUT("api/student/{id}")
    suspend fun updateStudent(@Path("id") id: Int, @Body studentDto: Student): ApiResponseStudent

    @DELETE("api/student/{id}")
    suspend fun deleteStudent(@Path("id") id: Int?): Response<Unit>
}

data class ApiResponseStudent(
    val status: String,
    val message: String,
    val student: Student
)