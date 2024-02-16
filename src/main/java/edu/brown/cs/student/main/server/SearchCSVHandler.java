package edu.brown.cs.student.main.server;
import edu.brown.cs.student.main.common.CSVSharedVar;
import edu.brown.cs.student.main.common.ServerAPI;
import edu.brown.cs.student.main.creators.StringCreatorFromRow;
import edu.brown.cs.student.main.csv.CSVSearcher;
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
    Map<String, Object> responseMap = new HashMap<>();
    if (!CSVSharedVar.isFileLoaded()) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", "No file loaded");
      return ServerAPI.serializeResponse(responseMap);
    }
    String searchVal = request.queryParams("val");
    String colIdentifier = request.queryParams("col");
    String multiflag = request.queryParams("multi");
    if (searchVal == null) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", "No search value provided");
      return ServerAPI.serializeResponse(responseMap);
    }
    if (multiflag == null) {
      multiflag = "false";
    } else if (!multiflag.equals("true") && !multiflag.equals("false")) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", "multi flag should be true or false");
      return ServerAPI.serializeResponse(responseMap);
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
      responseMap.put("result", "success");
      responseMap.put("data", searchResult);
      return ServerAPI.serializeResponse(responseMap);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "error_datasource");
      responseMap.put("message", "Error happens when searching" + e.toString());
      return ServerAPI.serializeResponse(responseMap);
    }
  }
}
