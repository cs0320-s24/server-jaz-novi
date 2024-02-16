package edu.brown.cs.student.main.caches;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CachedACSInfo implements Searcher<Map<String, Object>, ACSQuery> {

  private final Searcher<Map<String, Object>, ACSQuery> wrappedSearcher;
  private final LoadingCache<ACSQuery, Collection<Map<String, Object>>> cache;

  public CachedACSInfo(
      Searcher<Map<String, Object>, ACSQuery> toWrap, int maxCacheSize, int cacheMinutesDuration) {
    this.wrappedSearcher = toWrap;
    this.cache =
        CacheBuilder.newBuilder()
            .maximumSize(maxCacheSize)
            .expireAfterWrite(cacheMinutesDuration, TimeUnit.MINUTES)
            .recordStats()
            .build(
                new CacheLoader<>() {
                  @Override
                  public Collection<Map<String, Object>> load(ACSQuery acsQuery)
                      throws IOException, URISyntaxException, InterruptedException {
                    // If this isn't yet present in the cache, load it using the wrapped searcher
                    return wrappedSearcher.search(acsQuery);
                  }
                });
  }

  public CacheStats getStats() {
    return cache.stats();
  }

  @Override
  public Collection<Map<String, Object>> search(ACSQuery acsQuery) {

    return cache.getUnchecked(acsQuery);
  }
}
