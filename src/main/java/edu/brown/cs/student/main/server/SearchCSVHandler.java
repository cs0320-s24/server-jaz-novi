package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.common.CSVSharedVar;
import edu.brown.cs.student.main.creators.StringCreatorFromRow;
import edu.brown.cs.student.main.csv.CSVSearcher;
import edu.brown.cs.student.main.server.LoadCSVHandler.FileInvalidResponse;
import edu.brown.cs.student.main.server.ViewCSVHandler.ParseSuccessResponse.InvalidOperationResponse;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchCSVHandler implements Route {

  private final edu.brown.cs.student.main.common.CSVSharedVar CSVSharedVar;

  public SearchCSVHandler(CSVSharedVar sharedVar) {
    this.CSVSharedVar = sharedVar;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    if (!CSVSharedVar.isFileLoaded()) {
      return new InvalidOperationResponse("No file loaded").serialize();
    }
    String searchVal = request.queryParams("val");
    String colIdentifier = request.queryParams("col");
    String multiflag = request.queryParams("multi");
    if (searchVal == null) {
      return new FileInvalidResponse("No search target provided").serialize();
    }
    if (multiflag == null) {
      multiflag = "false";
    } else if (!multiflag.equals("true") && !multiflag.equals("false")) {
      return new FileInvalidResponse("multi flag should be true or false").serialize();
    }
    try {
      CSVSearcher searcher =
          new CSVSearcher(
              new FileReader(CSVSharedVar.getFilePath()),
              new StringCreatorFromRow() {},
              CSVSharedVar.getHeaderFlag());
      List<String> searchResult;
      if (Boolean.parseBoolean(multiflag)) {
        searchResult = searcher.searchMulti(searchVal);
      } else {
        searchResult = searcher.search(searchVal, colIdentifier);
      }
      Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("result", "success");
      responseMap.put("content", searchResult);
      return new ParseSuccessResponse(responseMap).serialize();
    } catch (Exception e) {
      return new InvalidOperationResponse("Error happens when loading content", e.toString())
          .serialize();
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

        // No need to specify a type for headers since they're a String
        JsonAdapter<String> stringAdapter = moshi.adapter(String.class);

        // Type for the list of strings
        Type stringListType = Types.newParameterizedType(List.class, String.class);
        JsonAdapter<List<String>> listStringAdapter = moshi.adapter(stringListType);

        // Retrieve the ParseResult from the responseMap
        List<String> searchResult = (List<String>) responseMap.get("content");
        // Serialize the data list
        String dataJson = listStringAdapter.toJson(searchResult);
        return "{\"response_type\": \"" + response_type + ", \"data\": " + dataJson + "}";
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
