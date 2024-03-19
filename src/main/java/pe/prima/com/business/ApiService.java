package pe.prima.com.business;

import pe.prima.com.business.dto.SendMailResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;
import java.util.Map;

public interface ApiService {
    @POST("api/sendMailList/{conversationId}")
    Call<List<SendMailResponse>> sendData(
            @Path("conversationId") String conversationId,
            @Body List<Map<String, Object>> additionalValues);
}