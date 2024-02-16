package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.common.CSVSharedVar;
import edu.brown.cs.student.main.common.ServerAPI;
import edu.brown.cs.student.main.csv.ParseResult;
import edu.brown.cs.student.main.server.ViewCSVHandler.ParseSuccessResponse.InvalidOperationResponse;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewCSVHandler implements Route {

  private final edu.brown.cs.student.main.common.CSVSharedVar CSVSharedVar;

  public ViewCSVHandler(CSVSharedVar sharedVar) {
    this.CSVSharedVar = sharedVar;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    if (!CSVSharedVar.isFileLoaded()) {
      return ServerAPI.GetServerErrorResponse("error_bad_request", "No file loaded");
    }
    try {
      Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("result", "success");
      responseMap.put("content", CSVSharedVar.getParseResult());
      return new ParseSuccessResponse(responseMap).serialize();
    } catch (Exception e) {
      return ServerAPI.GetServerErrorResponse(
          "error_datasource", "Error happens when parsing" + e.toString());
    }
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
        Moshi moshi = new Moshi.Builder().build();

        // Assuming ParseResult's data is a List<MyDataObject>, replace MyDataObject with your
        // actual data type
        Type dataListType = Types.newParameterizedType(List.class, String.class);
        JsonAdapter<List<String>> dataAdapter = moshi.adapter(dataListType);

        // Retrieve the ParseResult from the responseMap
        ParseResult<String> parseResult = (ParseResult<String>) responseMap.get("content");
        String headersJson;
        // Serialize the headers and data separately
        if (parseResult.getHeaders() != null) {
          headersJson = moshi.adapter(String.class).toJson(parseResult.getHeaders());
        } else {
          headersJson = moshi.adapter(String.class).toJson("No headers");
        }

        String dataJson = dataAdapter.toJson(parseResult.getData());

        // Combine headers and data into one JSON string
        return "{\"response_type\": \""
            + response_type
            + "\", \"headers\": "
            + headersJson
            + ", \"data\": "
            + dataJson
            + "}";
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Error serializing ParseSuccessResponse", e);
      }
    }

    /** Response object to send if provided filepath is null or invalid */
    public record InvalidOperationResponse(String response_type, String message) {

      public InvalidOperationResponse(String message) {
        this("error", message);
      }

      String serialize() {
        Moshi moshi = new Moshi.Builder().build();
        return moshi.adapter(InvalidOperationResponse.class).toJson(this);
      }
    }
  }
}
