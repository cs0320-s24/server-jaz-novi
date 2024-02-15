package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetStateCodes {

  public static Map<String, String> getStatesCodes()
      throws URISyntaxException, IOException, InterruptedException {
    Map<String, String> stateCodes = new HashMap<>();
    String responseJson = sendRequest();
    stateCodes = deserializeStateCodes(responseJson);
    stateCodes.forEach((state, code) -> System.out.println(state + ": " + code));
    return stateCodes;
  }

  public static Map<String, String> deserializeStateCodes(String json) {
    Moshi moshi = new Moshi.Builder().build();
    Type listType = Types.newParameterizedType(List.class, List.class, String.class);
    JsonAdapter<List<List<String>>> jsonAdapter = moshi.adapter(listType);

    try {
      List<List<String>> states = jsonAdapter.fromJson(json);
      if (states == null || states.isEmpty()) {
        return new HashMap<>();
      }

      // Skip the header row
      states.remove(0);

      Map<String, String> stateCodes = new HashMap<>();
      for (List<String> state : states) {
        if (state.size() >= 2) {
          stateCodes.put(state.get(0), state.get(1));
        }
      }
      return stateCodes;
    } catch (IOException e) {
      e.printStackTrace();
      return new HashMap<>();
    }
  }

  private static String sendRequest() throws URISyntaxException, IOException, InterruptedException {

    HttpRequest buildCensusApiRequest =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
            .GET()
            .build();

    // Send that API request then store the response in this variable. Note the generic type.
    HttpResponse<String> sentCensusApiResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildCensusApiRequest, HttpResponse.BodyHandlers.ofString());
    return sentCensusApiResponse.body();
  }
}
