package pe.prima.com;


import com.microsoft.azure.functions.*;

import com.microsoft.azure.functions.annotation.FunctionName;

import com.microsoft.azure.functions.annotation.TimerTrigger;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FunctionSecondGroup {

    private static final String DANA_URL_FUNCTION = System.getenv(ConfigEnum.DANA_URL_FUNCTION.getString());
    private static final String DANA_CONVERSATION_ID = System.getenv(ConfigEnum.DANA_CONVERSATION_ID.getString());

    @FunctionName("FunctionSecondGroup")
    public void run(
            //@TimerTrigger(name = "timerInfo", schedule = "0 0 0 5W * MON-FRI *") String timerInfo,
            @TimerTrigger(name = "timerInfo", schedule = "0 */6 * * * *") String timerInfo,
            @SQLInput(
                    name = "itemsSecondGroup",
                    commandText = Querys.LIST_WELCOME_SECOND_GROUP,
                    commandType = "Text",
                    connectionStringSetting = "SqlConnectionString")
            Map<String, Object>[] mapDataObject, final ExecutionContext context) {

        context.getLogger().info("Java Timer trigger function executed at: " + LocalDateTime.now());

        List<Map<String, Object>> transformedData =
                Arrays.stream(mapDataObject)
                        .map(item -> {
                            Map<String, Object> newItem = new HashMap<>();
                            newItem.put("additionalValues", item);
                            return newItem;
                        })
                        .collect(Collectors.toList());

        // Configurar Retrofit
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DANA_URL_FUNCTION)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear instancia de ApiService
        ApiService apiService = retrofit.create(ApiService.class);


        // Hacer la llamada a la API usando Retrofit

        try {
            Call<List<SendMailResponse>> call = apiService.sendData(DANA_CONVERSATION_ID, transformedData);

            Response<List<SendMailResponse>> response = call.execute();
            if (response.isSuccessful()) {
                // La solicitud fue exitosa
                context.getLogger().info("Solicitud exitosa");

            } else {
                // La solicitud falló
                context.getLogger().warning("Error en la solicitud: " + response.code());

            }
        } catch (Exception e) {
            context.getLogger().severe("Error durante la llamada a la API: " + e.getMessage());

        }

    }


    }

