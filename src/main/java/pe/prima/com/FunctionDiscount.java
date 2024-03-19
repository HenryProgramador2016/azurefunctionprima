package pe.prima.com;

import java.io.IOException;
import java.time.LocalDateTime;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.sql.annotation.SQLInput;
import okhttp3.OkHttpClient;
import pe.prima.com.Constants.ConfigEnum;
import pe.prima.com.Constants.Querys;
import pe.prima.com.business.ApiService;
import pe.prima.com.business.dto.SendMailResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.*;
import java.util.stream.Collectors;

public class FunctionDiscount {

    private static final String DANA_URL_FUNCTION = System.getenv(ConfigEnum.DANA_URL_FUNCTION.getString());
    private static final String DANA_CONVERSATION_ID = System.getenv(ConfigEnum.DANA_CONVERSATION_ID.getString());

    @FunctionName("FunctionDiscount")
    public HttpResponseMessage  run(
            //@TimerTrigger(name = "timerInfo", schedule = "0 0 0 15 * ?") String timerInfo,
            //@TimerTrigger(name = "timerInfo", schedule = "0 */15 * * * *") String timerInfo,
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            @SQLInput(
                    name = "itemsFunctionDiscount",
                    commandText = Querys.LIST_DISCOUNT,
                    commandType = "Text",
                    connectionStringSetting = "SqlConnectionString")
            Map<String, Object>[] mapDataObject, final ExecutionContext context) {

        List<String> logs = new ArrayList<>();
        logs.add("Java Timer trigger function executed at: " + LocalDateTime.now());

        List<Map<String, Object>> transformedData =
                Arrays.stream(mapDataObject)
                        .map(item -> {
                            Map<String, Object> newItem = new HashMap<>();
                            newItem.put("additionalValues", item);
                            return newItem;
                        })
                        .collect(Collectors.toList());

        Retrofit retrofit = configureRetrofit();
        ApiService apiService = retrofit.create(ApiService.class);

        callApi(apiService, transformedData, logs, context);
        return request.createResponseBuilder(HttpStatus.OK).body(String.join("\n", logs)).build();
    }

    private Retrofit configureRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        return new Retrofit.Builder()
                .baseUrl(DANA_URL_FUNCTION)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void callApi(ApiService apiService, List<Map<String, Object>> transformedData, List<String> logs,ExecutionContext context) {
        try {
            Call<List<SendMailResponse>> call = apiService.sendData(FunctionDiscount.DANA_CONVERSATION_ID, transformedData);
            Response<List<SendMailResponse>> response = call.execute();
            String logMessage = response.isSuccessful() ? "Solicitud exitosa" : "Error en la solicitud: " + response.code();
            logs.add(logMessage);
            context.getLogger().info(logMessage);
        } catch (IOException e) {
            String errorMessage = "Error durante la llamada a la API: " + e.getMessage();
            logs.add(errorMessage);
            context.getLogger().severe(errorMessage);
        }
    }
}
