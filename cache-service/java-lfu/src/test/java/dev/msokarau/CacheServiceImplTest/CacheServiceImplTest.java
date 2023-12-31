package dev.msokarau.CacheServiceImplTest;

import static org.junit.Assert.*;
import org.junit.Test;

import dev.msokarau.CacheServiceImpl.CacheServiceImpl;
import dev.msokarau.ConfigImpl.ConfigImpl;
import dev.msokarau.interfaces.CacheService.CacheService;

public class CacheServiceImplTest {

  @Test
  public void testPutAndGetValue() {
    CacheService cacheService = new CacheServiceImpl();
    cacheService.put("key", "value");

    assertEquals(cacheService.get("key"), "value");
    assertNull(cacheService.get("unknown"));
  };

  @Test
  public void testEviction() {
    CacheService cacheService = new CacheServiceImpl(new ConfigImpl(10, 10));
    putItemsInService(10, cacheService);

    assertEquals(cacheService.get("1"), "Value 1");
    assertEquals(cacheService.get("10"), "Value 10");
    assertNull(cacheService.get("11"));
  }

  @Test
  public void testEvictionCount() {
    CacheService cacheService = new CacheServiceImpl(new ConfigImpl(10, 10));
    putItemsInService(15, cacheService);

    assertEquals(cacheService.getEvictionCount(), 5);
  }

  @Test
  public void testAveragePutTime() {
    CacheService cacheService = new CacheServiceImpl(new ConfigImpl(10, 10));
    putItemsInService(10, cacheService);

    assertNotEquals(cacheService.getAveragePutTime(), 0);
  }

  @Test
  public void testAveragePutTimeWithNoItems() {
    CacheService cacheService = new CacheServiceImpl(new ConfigImpl(10, 10));

    assertEquals(cacheService.getAveragePutTime(), 0);
  }

  @Test
  public void testEvictionAfterAccess() throws InterruptedException {
    CacheService cacheService = new CacheServiceImpl(new ConfigImpl(10, 5, 15));
    putItemsInService(10, cacheService);

    assertEquals(cacheService.get("1"), "Value 1");
    assertEquals(cacheService.get("10"), "Value 10");
    assertNull(cacheService.get("11"));

    Thread.sleep(10);

    assertEquals(cacheService.get("10"), "Value 10");

    Thread.sleep(10);

    assertEquals(cacheService.get("10"), "Value 10");
    assertNull(cacheService.get("1"));
    assertNull(cacheService.get("11"));

    Thread.sleep(100);

    assertNull(cacheService.get("10"));
  }

  @Test
  public void testShutdown() throws InterruptedException {
    CacheService cacheService = new CacheServiceImpl(new ConfigImpl(10, 10, 10));
    putItemsInService(1, cacheService);

    cacheService.shutdown();

    Thread.sleep(100);

    assertEquals(cacheService.get("1"), "Value 1");
  }

  private void putItemsInService(int numberOfItems, CacheService cacheService) {
    for (int i = 1; i <= numberOfItems; i++) {
      cacheService.put(String.valueOf(i), "Value " + i);
    }
  }
}
