package edu.brown.cs.student.main.creators;

import edu.brown.cs.student.main.common.FactoryFailureException;
import java.util.List;

public interface CreatorFromRow<T> {
  T create(List<String> row) throws FactoryFailureException;
}
