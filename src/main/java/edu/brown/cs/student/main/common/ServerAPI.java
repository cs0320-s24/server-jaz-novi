package edu.brown.cs.student.main.common;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.util.Map;

public class ServerAPI {

  public static String serializeResponse(Map<String, Object> responseData) {
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
