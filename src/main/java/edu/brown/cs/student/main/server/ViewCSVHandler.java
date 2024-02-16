package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.common.CSVSharedVar;
import edu.brown.cs.student.main.common.ServerAPI;
import java.util.HashMap;
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
    Map<String, Object> responseMap = new HashMap<>();
    if (!CSVSharedVar.isFileLoaded()) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", "No file loaded");
      return ServerAPI.serializeResponse(responseMap);
    }
    try {
      responseMap.put("result", "success");
      responseMap.put("data", CSVSharedVar.getParseResult());
      return ServerAPI.serializeResponse(responseMap);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "error_datasource");
      responseMap.put("message", "Error happens when parsing" + e.toString());
      return ServerAPI.serializeResponse(responseMap);

    }
  }

}
