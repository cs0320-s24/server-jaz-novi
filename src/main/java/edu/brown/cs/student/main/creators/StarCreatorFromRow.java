package edu.brown.cs.student.main.creators;

import edu.brown.cs.student.main.common.FactoryFailureException;
import java.util.List;

public class StarCreatorFromRow implements CreatorFromRow<Star> {
  @Override
  public Star create(List<String> row) throws FactoryFailureException {
    try {
      // return a row of strings as a single string
      int starID = Integer.parseInt(row.get(0));
      String proper = row.get(1).isEmpty() ? null : row.get(1);
      double x = Double.parseDouble(row.get(2));
      double y = Double.parseDouble(row.get(3));
      double z = Double.parseDouble(row.get(4));
      return new Star(starID, proper, x, y, z);
    } catch (Exception e) {
      throw new FactoryFailureException("Failed to create Star from row", row);
    }
  }
}
