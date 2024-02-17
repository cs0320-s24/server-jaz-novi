package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import edu.brown.cs.student.main.caches.ACSSearcher;
import edu.brown.cs.student.main.caches.CachedACSInfo;
import edu.brown.cs.student.main.server.BroadbandHandler;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import spark.Request;
import spark.Response;

public class TestBroadbandHandler {

  @Test
  public void testBroadbandDataErrorHandling() throws Exception {
    // Mock dependencies to simulate an error response from the ACS API
    ACSSearcher mockSearcher = Mockito.mock(ACSSearcher.class);
    CachedACSInfo mockCache = Mockito.mock(CachedACSInfo.class);
    when(mockCache.search(any())).thenThrow(new IOException("ACS API error"));

    // Initialize BroadbandHandler with mocks and simulate a request that would trigger the error
    BroadbandHandler handler = new BroadbandHandler(mockCache);
    Request mockRequest = Mockito.mock(Request.class);
    when(mockRequest.queryParams("state")).thenReturn("Rhode Island");
    when(mockRequest.queryParams("county")).thenReturn("Providence County");
    when(mockRequest.queryParams("variables")).thenReturn("SUMLEVEL");
    Response mockResp = Mockito.mock(Response.class);

    // Execute the handler and verify that it handles the error gracefully
    Object result = handler.handle(mockRequest, mockResp);
    assertNotNull(result);
    System.out.println("Result String is: " + result);
    assertTrue(result instanceof String);
    String resultStr = (String) result;
    assertTrue(resultStr.contains("Failed to retrieve data from ACS API: ACS API error"));
  }

  @Test
  public void testBroadbandDataInvalidInput() throws Exception {
    // Mock dependencies as before
    ACSSearcher mockSearcher = Mockito.mock(ACSSearcher.class);
    CachedACSInfo mockCache = Mockito.mock(CachedACSInfo.class);

    // Initialize BroadbandHandler with mocks
    BroadbandHandler handler = new BroadbandHandler(mockCache);
    Request mockRequest = Mockito.mock(Request.class);
    Response mockResp = Mockito.mock(Response.class);

    // Simulate a request with invalid input (e.g., missing state or county parameter)
    when(mockRequest.queryParams("state")).thenReturn(null); // Simulating missing state

    // Execute the handler and verify it responds correctly to invalid input
    Object result = handler.handle(mockRequest, mockResp);
    assertNotNull(result);
    assertTrue(result instanceof String);
    String resultStr = (String) result;
    assertTrue(
        resultStr.contains("\"result\":\"error_bad_request\"")
            && resultStr.contains("\"error_message\":\"Missing 'state' parameter\""));
  }
}
