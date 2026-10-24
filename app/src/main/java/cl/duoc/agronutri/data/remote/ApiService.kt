package cl.duoc.agronutri.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("api/sync")
    suspend fun sincronizar(@Body datos: SyncRequest): Response<Unit>

    @Multipart
    @POST("api/fotos")
    suspend fun subirFoto(
        @Part archivo: MultipartBody.Part,
        @Part("id") id: RequestBody,
        @Part("planId") planId: RequestBody,
        @Part("nota") nota: RequestBody
    ): Response<Unit>
}
