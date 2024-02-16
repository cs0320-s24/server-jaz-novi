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

  @Override
  public Collection<Map<String, Object>> search(ACSQuery acsQuery) {
    // Retrieve the result from cache, automatically loading if necessary
    CacheStats stats = cache.stats();

    System.out.println("Cache hit count: " + stats.hitCount());
    System.out.println("Cache miss count: " + stats.missCount());
    System.out.println("Cache hit rate: " + stats.hitRate());
    System.out.println("Cache miss rate: " + stats.missRate());
    System.out.println("Load success count: " + stats.loadSuccessCount());
    System.out.println("Load exception count: " + stats.loadExceptionCount());
    System.out.println("Total load time (ns): " + stats.totalLoadTime());
    System.out.println("Average load penalty (ns): " + stats.averageLoadPenalty());
    System.out.println("Eviction count: " + stats.evictionCount());
    return cache.getUnchecked(acsQuery);
  }
}
