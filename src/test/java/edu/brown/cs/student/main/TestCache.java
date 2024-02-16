package edu.brown.cs.student.main;

import static org.testng.Assert.assertEquals;

import com.google.common.cache.CacheStats;
import edu.brown.cs.student.main.caches.ACSQuery;
import edu.brown.cs.student.main.caches.ACSSearcher;
import edu.brown.cs.student.main.caches.CachedACSInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class TestCache {

  @Test
  public void testCache() {
    // Test that the cache is working
    // Load the CSV file
    String stateCode = "44";
    String stateName = "Rhode Island";
    String countyCode = "007";
    String countyName = "Providence County";
    ACSSearcher acsSearcher = new ACSSearcher();
    CachedACSInfo cachedACSInfo = new CachedACSInfo(acsSearcher, 100, 60);
    List<String> variableNames = new ArrayList<>();
    variableNames.add("SUMLEVEL");
    ACSQuery acsQuery = new ACSQuery(stateCode, stateName, countyCode, countyName, variableNames);
    Collection<Map<String, Object>> searchResults = cachedACSInfo.search(acsQuery);
    CacheStats stats = cachedACSInfo.getStats();
    System.out.println("Cache hit count: " + stats.hitCount());
    assertEquals(stats.hitCount(), 0);
    cachedACSInfo.search(acsQuery);
    stats = cachedACSInfo.getStats();
    assertEquals(stats.hitCount(), 1);
    ACSQuery acsQuery2 = new ACSQuery("44", "Rhode Island", "005", "Newport County", variableNames);
    cachedACSInfo.search(acsQuery2);
    stats = cachedACSInfo.getStats();
    assertEquals(stats.hitCount(), 1);
  }

  @Test
  public void testCacheEviction() {
    String stateCode = "44";
    String stateName = "Rhode Island";
    String countyCode = "007";
    String countyName = "Providence County";
    ACSSearcher acsSearcher = new ACSSearcher();
    CachedACSInfo cachedACSInfo = new CachedACSInfo(acsSearcher, 1, 60);
    List<String> variableNames = new ArrayList<>();
    variableNames.add("SUMLEVEL");
    ACSQuery acsQuery = new ACSQuery(stateCode, stateName, countyCode, countyName, variableNames);
    Collection<Map<String, Object>> searchResults = cachedACSInfo.search(acsQuery);
    CacheStats stats = cachedACSInfo.getStats();
    System.out.println("Cache hit count: " + stats.hitCount());
    assertEquals(stats.hitCount(), 0);
    cachedACSInfo.search(acsQuery);
    stats = cachedACSInfo.getStats();
    assertEquals(stats.hitCount(), 1);
    ACSQuery acsQuery2 = new ACSQuery("44", "Rhode Island", "005", "Newport County", variableNames);
    cachedACSInfo.search(acsQuery2);
    stats = cachedACSInfo.getStats();
    assertEquals(stats.hitCount(), 1);
    cachedACSInfo.search(acsQuery2);
    stats = cachedACSInfo.getStats();
    assertEquals(stats.hitCount(), 2);
    cachedACSInfo.search(acsQuery);
    stats = cachedACSInfo.getStats();
    assertEquals(stats.hitCount(), 2);
  }
}
