package edu.brown.cs.student.main.caches;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ACSSearcher implements Searcher<Map<String, Object>, ACSQuery> {

  Map<String, Object> responseData = new HashMap<>();
  List<String> notFoundVariables = new ArrayList<>();
  Moshi moshi = new Moshi.Builder().build();
  JsonAdapter<List<List<String>>> jsonAdapter =
      moshi.adapter(Types.newParameterizedType(List.class, List.class, String.class));

  @Override
  public Collection<Map<String, Object>> search(ACSQuery acsQuery)
      throws IOException, InterruptedException, URISyntaxException {
    String stateCode = acsQuery.stateCode;
    String stateName = acsQuery.stateName;
    String countyCode = acsQuery.countyCode;
    String countyName = acsQuery.countyName;
    List<String> variableNames = acsQuery.variableNames;

    Map<String, Object> responseData = new HashMap<>();
    List<String> notFoundVariables = new ArrayList<>();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<List<List<String>>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(List.class, List.class, String.class));

    for (String variable : variableNames) {
      String uri =
          String.format(
              "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,%s&for=county:%s&in=state:%s",
              variable, countyCode, stateCode);

      HttpRequest request = HttpRequest.newBuilder().uri(new URI(uri)).GET().build();
      HttpResponse<String> response =
          HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        List<List<String>> responseDataList = jsonAdapter.fromJson(response.body());
        if (responseDataList != null && responseDataList.size() > 1) {
          // Assuming the data for the variable is in the second column of the first data row
          String data = responseDataList.get(1).get(1);
          responseData.put(variable, data);
        } else {
          notFoundVariables.add(variable);
        }
      } else {
        notFoundVariables.add(variable);
      }
    }

    responseData.put("retrievalTime", LocalDateTime.now().toString());
    responseData.put("stateName", stateName);
    responseData.put("countyName", countyName);
    responseData.put("notFoundVariables", notFoundVariables);

    return (Collection<Map<String, Object>>) responseData;
  }
}
