package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.csv.ParseResult;
import spark.Spark;

public class Server {
  // requested api key:43ae22b3d5ae6792522631efdb1e455ad5893817
  public static void main(String[] args) {
    int port = 3232;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });
    boolean isFileLoaded = false;
    ParseResult parseResult = null;
    // Setting up the handler for the GET /order and /activity endpoints
    Spark.get("loadcsv", new LoadCSVHandler(isFileLoaded, parseResult));
    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }
}
