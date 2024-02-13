package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.common.FactoryFailureException;
import edu.brown.cs.student.main.common.utility;
import edu.brown.cs.student.main.creators.CreatorFromRow;
import edu.brown.cs.student.main.creators.StringCreatorFromRow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVParser<T> {

  private final CreatorFromRow<T> creator;
  private final Reader reader;
  private final boolean headerFlag;

  public CSVParser(Reader reader, CreatorFromRow<T> creator, boolean headerFlag) {
    this.reader = reader;
    this.creator = creator;
    this.headerFlag = headerFlag;
  }

  public ParseResult<T> parse() throws IOException, FactoryFailureException {
    int expectedColumns = -1;
    String line;
    BufferedReader curReader = new BufferedReader(reader);
    List<T> result = new ArrayList<>();
    boolean isFirstRow = true;
    String headers = null;
    while ((line = curReader.readLine()) != null) {

      String[] fields = utility.regexSplitCSVRow.split(line);
      if (expectedColumns == -1) {
        expectedColumns = fields.length;
      } else if (fields.length != expectedColumns) {
        throw new IOException("Inconsistent column count at line: " + line);
      }
      List<String> row =
          Arrays.stream(fields).map(utility::postprocess).collect(Collectors.toList());
      if (isFirstRow && headerFlag) { // Optionally use headers for something
        isFirstRow = false;
        CreatorFromRow<String> strCreator = new StringCreatorFromRow() {};
        headers = (String) strCreator.create(row);
        continue; // Skip adding the header row as a data object
      }
      T object = creator.create(row);
      result.add(object);
    }
    curReader.close();
    return new ParseResult<>(headers, result);
  }

  //  public String getHeader() throws IOException, FactoryFailureException {
  //    BufferedReader curReader = new BufferedReader(reader);
  //    String line = curReader.readLine();
  //    String[] fields = utility.regexSplitCSVRow.split(line);
  //    curReader.close();
  //    List<String> row =
  // Arrays.stream(fields).map(utility::postprocess).collect(Collectors.toList());
  //    CreatorFromRow<String> strCreator = new StringCreatorFromRow() {};
  //    String headers = (String) strCreator.create(row);
  //    return headers;
  //  }
  // purely for test purposes
  public String getFirstLine() throws IOException {
    BufferedReader curReader = new BufferedReader(reader);
    String line = curReader.readLine();
    curReader.close();
    return line;
  }
}
