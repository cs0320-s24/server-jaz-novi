package edu.brown.cs.student.main.query;

import edu.brown.cs.student.main.common.utility;

public class BasicQuery extends Query {
  private Integer colIndex;
  private String value;
  private boolean negation;

  public BasicQuery(
      String colIdentifier, String value, String headers, boolean headerFlag, boolean negation) {
    if (colIdentifier == null) {
      this.colIndex = Integer.parseInt(null);
    } else if (value == null || headers == null) {
      throw new IllegalArgumentException("Error: null argument");
    } else {
      this.colIndex = utility.findColIndex(colIdentifier, headerFlag, headers);
    }
    this.value = value;
    this.negation = negation;
  }

  public boolean contains(String[] row) {
    if (colIndex == null) {
      String searhString = String.join(",", row);
      if (negation) {
        return !searhString.contains(value);
      }
      // turns the row into a string and checks if the value is in the string
      return searhString.contains(value);
    }
    if (negation) {
      return !row[colIndex].contains(value);
    }
    return row[colIndex].contains(value);
  }
}
