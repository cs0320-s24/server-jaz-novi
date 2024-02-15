package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.LoadCSVHandler.FileInvalidResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {
  private static Map<String, String> stateCodes = null;

  public BroadbandHandler() {
    try {
      if (stateCodes == null) {
        stateCodes = GetStateCodes.getStatesCodes();
      }
    } catch (Exception e) {
      e.printStackTrace();
      stateCodes = new HashMap<>();
    }
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {

    String stateName = request.queryParams("state");
    if (stateName == null) {
      return new FileInvalidResponse("No state provided").serialize();
    }
    String countyName = request.queryParams("county");
    if (countyName == null) {
      return new FileInvalidResponse("No county provided").serialize();
    }

    String stateCode = stateCodes.getOrDefault(stateName, null); // Implement this method
    if (stateCode == null) {
      return new FileInvalidResponse("Invalid state name").serialize();
    }
    String countyCode =
        GetCountyCodes.getCountyCode(stateCode, countyName); // Implement this method
    if (countyCode == null) {
      return new FileInvalidResponse("Invalid county name").serialize();
    }

    Map<String, Object> responseMap =
        queryBroadbandData(stateCode, stateName, countyCode, countyName);

    return serializeResponse(responseMap);
  }

  public record ParseSuccessResponse(String response_type, Map<String, Object> responseMap) {
    public ParseSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ParseSuccessResponse> adapter = moshi.adapter(ParseSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  private static Map<String, Object> queryBroadbandData(
      String stateCode, String stateName, String countyCode, String countyName)
      throws IOException, InterruptedException, URISyntaxException {
    // Updated URI to match the provided API endpoint, using stateCode and countyCode for filtering
    String uri =
        String.format(
            "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:%s&in=state:%s",
            countyCode, stateCode);

    HttpRequest request = HttpRequest.newBuilder().uri(new URI(uri)).GET().build();

    HttpResponse<String> response =
        HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());

    // Assuming the response body is a JSON array of arrays as per the provided document content
    // Use a JSON library like Moshi to parse the JSON response
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<List<List<String>>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(List.class, List.class, String.class));

    List<List<String>> responseDataList = jsonAdapter.fromJson(response.body());

    Map<String, Object> responseData = new HashMap<>();
    if (responseDataList != null && responseDataList.size() > 1) {
      // Skip the header row and get the first data row
      List<String> dataRow = responseDataList.get(1);
      // Assuming the percentage data is in the second column (index 1)
      String percentageData = dataRow.get(1);
      responseData.put("percentageData", percentageData);
    } else {
      responseData.put("percentageData", "Data not found");
    }

    responseData.put("retrievalTime", LocalDateTime.now().toString()); // Include retrieval time
    responseData.put("stateName", stateName); // Include state name
    responseData.put("countyName", countyName); // Include county name

    return responseData;
  }

  private String serializeResponse(Map<String, Object> responseData) {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<Map<String, Object>> jsonAdapter =
          moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));
      return jsonAdapter.toJson(responseData);
    } catch (Exception e) {
      e.printStackTrace();
      return "{}"; // Return an empty JSON object in case of error
    }
  }
}
