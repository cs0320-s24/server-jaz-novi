package edu.brown.cs.student.main.query;

public class OrQuery extends Query {
  private Query[] queries;

  public OrQuery(Query... queries) {
    this.queries = queries;
  }

  @Override
  public boolean contains(String[] row) {
    for (Query query : queries) {
      if (query.contains(row)) {
        return true;
      }
    }
    return false;
  }
}
