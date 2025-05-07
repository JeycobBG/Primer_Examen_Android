package com.moviles.primer_examen.network

import com.moviles.primer_examen.model.CreateStudentRequest
import com.moviles.primer_examen.model.StudentApiDto
import com.moviles.primer_examen.model.StudentWithCourses
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/Student/{id}")
    suspend fun getStudentById(@Path("id") id: Int): StudentWithCourses

    @GET("api/Student")  // Cambié de "students" a "Student"
    suspend fun getStudentsByCourse(@Query("courseId") courseId: Int): List<StudentApiDto>

    @POST("api/Student")
    suspend fun addStudent(@Body student: CreateStudentRequest): ApiResponseStudent

    @PUT("api/Student/{id}")
    suspend fun updateStudent(
        @Path("id") id: Int?,
        @Body studentData: Map<String, RequestBody>
    ): ApiResponseStudent

    @DELETE("api/Student/{id}")
    suspend fun deleteStudent(@Path("id") id: Int?): Response<Unit>
}

// Respuesta para agregar o actualizar un estudiante
data class ApiResponseStudent(
    val status: String,
    val message: String,
    val student: StudentWithCourses
)