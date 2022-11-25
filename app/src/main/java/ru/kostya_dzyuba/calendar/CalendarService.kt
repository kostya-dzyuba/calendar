package ru.kostya_dzyuba.calendar

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.kostya_dzyuba.calendar.adapter.DateAdapter
import ru.kostya_dzyuba.calendar.model.Task
import ru.kostya_dzyuba.calendar.model.User
import java.time.LocalDate

interface CalendarService {
    @POST("login.php")
    suspend fun login(@Body user: User): Response<ResponseBody>

    @POST("signup.php")
    suspend fun signUp(@Body user: User): Response<ResponseBody>

    @GET("tasks.php")
    suspend fun getTasks(): List<Task>

    @POST("tasks.php")
    suspend fun addTask(@Body task: Task): Long

    @PUT("tasks.php/{id}")
    suspend fun updateTask(@Path("id") id: Long, @Body task: Task)

    @DELETE("tasks.php/{id}")
    suspend fun deleteTask(@Path("id") id: Long)

    companion object {
        fun createInstance(token: String?) = Retrofit.Builder()
            .baseUrl("http://api.akordy.ru")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().registerTypeAdapter(
                        LocalDate::class.java,
                        DateAdapter()
                    ).create()
                )
            )
            .client(
                OkHttpClient.Builder()
                    .addInterceptor {
                        val request = it.request().newBuilder()
                            .addHeader("Authorization", "Bearer $token").build()
                        it.proceed(request)
                    }.build()
            )
            .build()
            .create(CalendarService::class.java)
    }
}