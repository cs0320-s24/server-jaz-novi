package edu.brown.cs.student.main.caches;

import java.util.List;
import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ACSQuery acsQuery = (ACSQuery) o;
    return Objects.equals(stateCode, acsQuery.stateCode)
        && Objects.equals(stateName, acsQuery.stateName)
        && Objects.equals(countyCode, acsQuery.countyCode)
        && Objects.equals(countyName, acsQuery.countyName)
        && Objects.equals(variableNames, acsQuery.variableNames);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stateCode, stateName, countyCode, countyName, variableNames);
  }
}
