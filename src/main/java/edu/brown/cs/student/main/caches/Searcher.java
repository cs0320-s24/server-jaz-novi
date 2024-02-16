package edu.brown.cs.student.main.caches;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

public interface Searcher<RESULT, TARGET> {

  Collection<RESULT> search(TARGET target)
      throws IOException, InterruptedException, URISyntaxException;
}
