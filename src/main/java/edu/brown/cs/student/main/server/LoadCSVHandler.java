package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.common.CSVSharedVar;
import edu.brown.cs.student.main.common.FactoryFailureException;
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

    String filepath = request.queryParams("filepath");
    if (filepath == null) {
      return new FileInvalidResponse("No filepath provided").serialize();
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
    Map<String, Object> responseMap = new HashMap<>();
    // check if the file path is valid
    if (!utility.isValidPath(filepath)) {
      return new FileInvalidResponse("Invalid filepath").serialize();
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
      return new ParseSuccessResponse(responseMap).serialize();
    } catch (IOException | FactoryFailureException e) {
      return new FileInvalidResponse("Error happens when parsing", e.toString()).serialize();
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

  /** Response object to send if provided filepath is null or invalid */
  public record FileInvalidResponse(String response_type, String message) {
    public FileInvalidResponse(String message) {
      this("error", message);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(FileInvalidResponse.class).toJson(this);
    }
  }
}
