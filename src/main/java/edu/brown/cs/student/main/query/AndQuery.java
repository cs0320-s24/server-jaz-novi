package edu.brown.cs.student.main.query;

public class AndQuery extends Query {
  private Query[] queries;

  public AndQuery(Query... queries) {
    this.queries = queries;
  }

  @Override
  public boolean contains(String[] row) {
    for (Query query : queries) {
      if (!query.contains(row)) {
        return false;
      }
    }
    return true;
  }
}
