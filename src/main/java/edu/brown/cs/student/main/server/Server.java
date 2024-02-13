package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import spark.Spark;

public class Server {
  // TODO 0: Read through this class and determine the shape of this project...
  // What are the endpoints that we can access... What happens if you go to them?
  public static void main(String[] args) {
    int port = 3232;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET /order and /activity endpoints
    //    Spark.get("");
    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }
}
