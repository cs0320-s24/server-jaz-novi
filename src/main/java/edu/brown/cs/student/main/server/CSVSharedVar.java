package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.csv.ParseResult;

public class CSVSharedVar {
  private boolean isFileLoaded = false;
  private ParseResult parseResult = null;
  private boolean headerFlag = false;
  private String fileName = null;
  // getter and setter methods
  public boolean isFileLoaded() {
    return isFileLoaded;
  }

  public void setFileLoaded(boolean fileLoaded) {
    isFileLoaded = fileLoaded;
  }

  public ParseResult getParseResult() {
    return parseResult;
  }

  public void setParseResult(ParseResult parseResult) {
    this.parseResult = parseResult;
  }

  public boolean getHeaderFlag() {
    return headerFlag;
  }

  public void setHeaderFlag(boolean headerFlag) {
    this.headerFlag = headerFlag;
  }

  public String getFilePath() {
    return fileName;
  }

  public void setFilePath(String fileName) {
    this.fileName = fileName;
  }
}
