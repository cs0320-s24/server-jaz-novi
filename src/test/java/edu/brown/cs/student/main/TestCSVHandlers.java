package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testng.Assert.assertNotNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.common.CSVSharedVar;
import edu.brown.cs.student.main.server.LoadCSVHandler;
import edu.brown.cs.student.main.server.SearchCSVHandler;
import edu.brown.cs.student.main.server.ViewCSVHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestCSVHandlers {

  private JsonAdapter<Map<String, Object>> adapter;

  public TestCSVHandlers() {
    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    adapter = moshi.adapter(type);
  }

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  /**
   * Shared state for all tests. We need to be able to mutate it (adding recipes etc.) but never
   * need to replace the reference itself. We clear this state out after every test runs.
   */
  private final CSVSharedVar csvSharedVar = new CSVSharedVar();

  @BeforeEach
  public void setup() {
    // In fact, restart the entire Spark server for every test!
    Spark.get("loadcsv", new LoadCSVHandler(csvSharedVar));
    Spark.get("viewcsv", new ViewCSVHandler(csvSharedVar));
    Spark.get("searchcsv", new SearchCSVHandler(csvSharedVar));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.unmap("loadcsv");
    Spark.unmap("viewcsv");
    Spark.unmap("searchcsv");
    Spark.awaitStop();
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestMethod("GET");
    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testNoFilepathProvided() throws Exception {
    HttpURLConnection clientConnection1 = tryRequest("loadcsv?headerFlag=true");
    String jsonResponse = new Buffer().readFrom(clientConnection1.getInputStream()).readUtf8();
    Map<String, Object> response = adapter.fromJson(jsonResponse);
    assertEquals("error_bad_request", response.get("result"));
    assertEquals("No filepath provided", response.get("message"));
  }

  @Test
  public void testLoadSuccessfully() throws Exception {
    HttpURLConnection clientConnection1 =
        tryRequest("loadcsv?filepath=data/server-data/city-town-income.csv&headerFlag=true");
    LoadCSVHandler handler = new LoadCSVHandler(new CSVSharedVar());
    Buffer buffer = new Buffer();
    buffer.readFrom(clientConnection1.getInputStream());
    Map<String, Object> response = adapter.fromJson(buffer);
    assertEquals("success", response.get("result"));
  }

  @Test
  public void testNotLoadViewCSV() throws IOException {
    HttpURLConnection clientViewConnection = tryRequest("viewcsv");
    Buffer buffer = new Buffer();
    buffer.readFrom(clientViewConnection.getInputStream());
    Map<String, Object> response = adapter.fromJson(buffer);
    assertEquals("error_bad_request", response.get("result"), "No file loaded");
    clientViewConnection.disconnect();
  }

  @Test
  public void testSuccessfulViewCSVFilepathProvided() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=data/server-data/city-town-income.csv&headerFlag=true");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientViewConnection = tryRequest("viewcsv");
    Buffer buffer = new Buffer();
    buffer.readFrom(clientViewConnection.getInputStream());
    Map<String, Object> response = adapter.fromJson(buffer);
    System.out.println(response);
    assertNotNull(response.get("data"), "The response data should not be null.");
    assertEquals("success", response.get("result"), "The operation should be successful.");
  }

  @Test
  public void testNotLoadSearchCSV() throws IOException {
    HttpURLConnection clientViewConnection = tryRequest("searchcsv");
    Buffer buffer = new Buffer();
    buffer.readFrom(clientViewConnection.getInputStream());
    Map<String, Object> response = adapter.fromJson(buffer);
    assertEquals("error_bad_request", response.get("result"), "No file loaded");
    clientViewConnection.disconnect();
  }

  @Test
  public void testSearchCSVWithInvalidInput() throws Exception {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=data/server-data/city-town-income.csv&headerFlag=true");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnectionSearch1 =
        tryRequest("searchcsv?column=city&value=Providence");
    Buffer buffer = new Buffer();
    buffer.readFrom(clientConnectionSearch1.getInputStream());
    Map<String, Object> response = adapter.fromJson(buffer);
    assertEquals("error_bad_request", response.get("result"));
    assertEquals("No search value provided", response.get("message"));
  }

  @Test
  public void testSearchCSVWithValidInput() throws Exception {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=data/server-data/city-town-income.csv&headerFlag=true");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnectionSearch1 =
        tryRequest("searchcsv?col=City/Town&val=Providence");
    Buffer buffer = new Buffer();
    buffer.readFrom(clientConnectionSearch1.getInputStream());
    Map<String, Object> response = adapter.fromJson(buffer);
    assertNotNull(response.get("data"), "The response data should not be null.");
    assertEquals("success", response.get("result"), "The operation should be successful.");
  }

  @Test
  public void testSearchCSVWithInvalidMultiFlag() throws Exception {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=data/server-data/city-town-income.csv&headerFlag=true");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnectionSearch1 =
        tryRequest("searchcsv?col=City/Town&val=Providence&multi=invalid");
    Buffer buffer = new Buffer();
    buffer.readFrom(clientConnectionSearch1.getInputStream());
    Map<String, Object> response = adapter.fromJson(buffer);
    assertEquals("error_bad_request", response.get("result"));
    assertEquals("multi flag should be true or false", response.get("message"));
  }

  @Test
  public void testSearchCSVWithValidMultiFlag() throws Exception {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=data/server-data/city-town-income.csv&headerFlag=true");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnectionSearch1 =
        tryRequest("searchcsv?multi=true&queries=or(not(4_1),and(1_1)) ");
    Buffer buffer = new Buffer();
    buffer.readFrom(clientConnectionSearch1.getInputStream());
    Map<String, Object> response = adapter.fromJson(buffer);
    assertNotNull(response.get("data"), "The response data should not be null.");
    assertEquals("success", response.get("result"), "The operation should be successful.");
  }
}
