package edu.brown.cs.student.main.csv;

import java.util.List;

public class ParseResult<T> {
  private final String headers;
  private final List<T> data;

  // Correct constructor syntax
  public ParseResult(String headers, List<T> data) {
    this.headers = headers;
    this.data = data;
  }

  // Getters for headers and data
  public String getHeaders() {
    return headers;
  }

  public List<T> getData() {
    return data;
  }
}
