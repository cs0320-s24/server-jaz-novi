package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.caches.ACSQuery;
import edu.brown.cs.student.main.caches.ACSSearcher;
import edu.brown.cs.student.main.caches.CachedACSInfo;
import edu.brown.cs.student.main.server.LoadCSVHandler.FileInvalidResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {
  private static Map<String, String> stateCodes = null;
  private static ACSSearcher acsSearcher;
  private static CachedACSInfo cachedACSInfo;

  public BroadbandHandler() {
    try {
      acsSearcher = new ACSSearcher();
      cachedACSInfo = new CachedACSInfo(acsSearcher, 100, 60);
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

    // optional
    String variablesParam = request.queryParams("variables");
    List<String> variableNames = new ArrayList<>();
    if (variablesParam != null && !variablesParam.isEmpty()) {
      variableNames = Arrays.asList(variablesParam.split(","));
    }
    ACSQuery acsQuery = new ACSQuery(stateCode, stateName, countyCode, countyName, variableNames);
    Collection<Map<String, Object>> searchResults = cachedACSInfo.search(acsQuery);
    Map<String, Object> responseMap = null;
    if (!searchResults.isEmpty()) {
      // Assuming we're only interested in the first result
      responseMap = searchResults.iterator().next();
    }
    return serializeResponse(responseMap);
    //    return new ParseSuccessResponse(responseMap).serialize();
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

  //  private static Map<String, Object> queryBroadbandData(
  //      String stateCode,
  //      String stateName,
  //      String countyCode,
  //      String countyName,
  //      List<String> variableNames)
  //      throws IOException, InterruptedException, URISyntaxException {
  //
  //    Map<String, Object> responseData = new HashMap<>();
  //    List<String> notFoundVariables = new ArrayList<>();
  //    Moshi moshi = new Moshi.Builder().build();
  //    JsonAdapter<List<List<String>>> jsonAdapter =
  //        moshi.adapter(Types.newParameterizedType(List.class, List.class, String.class));
  //
  //    for (String variable : variableNames) {
  //      String uri =
  //          String.format(
  //
  // "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,%s&for=county:%s&in=state:%s",
  //              variable, countyCode, stateCode);
  //
  //      HttpRequest request = HttpRequest.newBuilder().uri(new URI(uri)).GET().build();
  //      HttpResponse<String> response =
  //          HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
  //
  //      if (response.statusCode() == 200) {
  //        List<List<String>> responseDataList = jsonAdapter.fromJson(response.body());
  //        if (responseDataList != null && responseDataList.size() > 1) {
  //          // Assuming the data for the variable is in the second column of the first data row
  //          String data = responseDataList.get(1).get(1);
  //          responseData.put(variable, data);
  //        } else {
  //          notFoundVariables.add(variable);
  //        }
  //      } else {
  //        notFoundVariables.add(variable);
  //      }
  //    }
  //
  //    responseData.put("retrievalTime", LocalDateTime.now().toString());
  //    responseData.put("stateName", stateName);
  //    responseData.put("countyName", countyName);
  //    responseData.put("notFoundVariables", notFoundVariables);
  //
  //    return responseData;
  //  }

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
