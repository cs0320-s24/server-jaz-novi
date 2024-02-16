package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.common.CSVSharedVar;
import edu.brown.cs.student.main.common.FactoryFailureException;
import edu.brown.cs.student.main.common.ServerAPI;
import edu.brown.cs.student.main.common.utility;
import edu.brown.cs.student.main.creators.StringCreatorFromRow;
import edu.brown.cs.student.main.csv.CSVParser;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSVHandler implements Route {
  private edu.brown.cs.student.main.common.CSVSharedVar CSVSharedVar;

  public LoadCSVHandler(CSVSharedVar sharedVar) {
    this.CSVSharedVar = sharedVar;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    String filepath = request.queryParams("filepath");
    if (filepath == null) {
      //      return ServerAPI.GetServerErrorResponse("error_bad_request", "No filepath provided");
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", "No filepath provided");
      return ServerAPI.serializeResponse(responseMap);
    }
    CSVSharedVar.setFilePath(filepath);
    // should I make the header flag a must? ->optional, but return an error if search with header
    String headerFlag = request.queryParams("headerFlag");
    if (headerFlag == null) {
      headerFlag = "false";
    }
    CSVSharedVar.setHeaderFlag(Boolean.parseBoolean(headerFlag));
    ;
    // if there is no file path provided, return an error,but not halt the server

    // check if the file path is valid
    if (!utility.isValidPath(filepath)) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("message", "Invalid filepath");
      return ServerAPI.serializeResponse(responseMap);
      //      return ServerAPI.GetServerErrorResponse("error_datasource", "Invalid filepath");
    }
    // create a csv parser and parse the file
    try {
      CSVParser parser =
          new CSVParser<>(
              new FileReader(filepath),
              new StringCreatorFromRow() {},
              CSVSharedVar.getHeaderFlag());
      CSVSharedVar.setParseResult(parser.parse());
      CSVSharedVar.setFileLoaded(true);
      responseMap.put("result", "success");
      return ServerAPI.serializeResponse(responseMap);
    } catch (IOException | FactoryFailureException e) {
      responseMap.put("result", "error_datasource");
      responseMap.put("message", "Error happens when parsing" + e.toString());
      return ServerAPI.serializeResponse(responseMap);
    }
  }
}
