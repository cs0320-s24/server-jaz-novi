package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.common.FactoryFailureException;
import edu.brown.cs.student.main.common.utility;
import edu.brown.cs.student.main.creators.CreatorFromRow;
import edu.brown.cs.student.main.query.Query;
import edu.brown.cs.student.main.query.QueryParser;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVSearcher {

  private CSVParser<String> parser;
  private List<String> parsedData;
  private boolean headerFlag;
  private String headers;

  public CSVSearcher(Reader reader, CreatorFromRow<String> creator, Boolean headerFlag) {

    try {
      parser = new CSVParser<>(reader, creator, headerFlag);
      ParseResult parseResult = parser.parse();
      this.headerFlag = headerFlag;
      this.headers = parseResult.getHeaders();
      this.parsedData = parseResult.getData();

    } catch (IOException | FactoryFailureException e) {
      System.err.println("File outside of accessible directory. Exiting.");
      return;
    }
  }

  public List<String> search(String targetVal, String targetCol) {
    boolean valFound = false;
    String unifiedTargetVal = utility.unifyString(targetVal);
    List<String> searchResults = new ArrayList<>();
    int colIndex = 0;
    if (targetCol != null) {
      colIndex = utility.findColIndex(targetCol, this.headerFlag, this.headers);
      if (colIndex == -1) {
        return null;
      }
      // search for the target value in the targeted column
      for (String row : parsedData) {
        String curCol = utility.unifyString(row.split(",")[colIndex]);
        if (curCol.contains(unifiedTargetVal)) {
          valFound = true;
          System.out.println(row);
          searchResults.add(row);
        }
      }
    } else {
      // search for the target value in the entire csv file
      for (String row : parsedData) {
        String curRow = utility.unifyString(row);
        if (curRow.contains(unifiedTargetVal)) {
          valFound = true;
          System.out.println(row);
          searchResults.add(row);
        }
      }
    }
    if (!valFound) {
      System.err.println(
          "The requested value does not exist in the csv file or in the requested column.");
    }
    return searchResults;
  }

  public List<String> searchMulti(String queries) {
    boolean valFound = false;
    Query query = QueryParser.parse(queries, this.headers, this.headerFlag);
    // create an array to hold the search results
    List<String> searchResults = new ArrayList<>();
    for (String row : parsedData) {
      String[] rowArray = row.split(",");
      if (query.contains(rowArray)) {
        valFound = true;
        System.out.println(row);
        searchResults.add(row);
      }
    }
    if (!valFound) {
      System.err.println(
          "The requested value does not exist in the csv file or in the requested column.");
    }
    return searchResults;
  }
}
