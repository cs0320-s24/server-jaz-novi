package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.common.CSVSharedVar;
import edu.brown.cs.student.main.common.GetStateCodes;
import java.io.IOException;
import java.net.URISyntaxException;
import spark.Spark;

public class Server {
  // requested api key:43ae22b3d5ae6792522631efdb1e455ad5893817
  // to do: print out the operation guide, parameter variable names for different routes.

  public static void main(String[] args)
      throws URISyntaxException, IOException, InterruptedException {
    int port = 3232;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });
    CSVSharedVar csvSharedVar = new CSVSharedVar();
    // Setting up the handler for the GET /order and /activity endpoints
    Spark.get("loadcsv", new LoadCSVHandler(csvSharedVar));
    Spark.get("viewcsv", new ViewCSVHandler(csvSharedVar));
    Spark.get("searchcsv", new SearchCSVHandler(csvSharedVar));
    //    Spark.get("broadband", new BroadbandHandler());
    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }
}
