package edu.brown.cs.student.main.caches;

import java.util.List;

public class ACSQuery {
  static String stateCode;
  static String stateName;
  static String countyCode;
  static String countyName;
  static List<String> variableNames;

  public ACSQuery(
      String stateCode,
      String stateName,
      String countyCode,
      String countyName,
      List<String> variableNames) {
    this.stateCode = stateCode;
    this.stateName = stateName;
    this.countyCode = countyCode;
    this.countyName = countyName;
    this.variableNames = variableNames;
  }
}
