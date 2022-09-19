package io.dialob.client.spi;


import java.util.Optional;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import io.dialob.client.api.DialobCache;
import io.dialob.client.api.DialobComposerDocument;
import io.dialob.client.api.DialobStore.StoreEntity;
import io.dialob.client.api.ImmutableCacheEntry;
import io.dialob.program.DialobProgram;


public class DialobEhCache implements DialobCache {
  
  private static final String CACHE_PREFIX = DialobCache.class.getCanonicalName();
  private final CacheManager cacheManager;
  private final String cacheName;
  
  private DialobEhCache(CacheManager cacheManager, String cacheName) {
    super();
    this.cacheManager = cacheManager;
    this.cacheName = cacheName;
  }
  private Cache<String, CacheEntry> getCache() {
    return cacheManager.getCache(cacheName, String.class, CacheEntry.class);
  }
  @Override
  public Optional<DialobProgram> getProgram(StoreEntity src) {
    final var cache = getCache();
    return Optional.ofNullable(cache.get(src.getHash()))
        .or(() -> Optional.ofNullable(cache.get(src.getId())))
        .map(e -> e.getProgram().orElse(null));
  }
  @Override
  public Optional<DialobComposerDocument> getAst(StoreEntity src) {
    final var cache = getCache();
    return Optional.ofNullable(cache.get(src.getHash()))
        .or(() -> Optional.ofNullable(cache.get(src.getId())))
        .map(e -> e.getAst());
  }
  @Override
  public DialobProgram setProgram(DialobProgram program, StoreEntity src) {
    final var cache = getCache();
    final var previous = cache.get(src.getHash());
    final var entry = ImmutableCacheEntry.builder().from(previous).program(program).build();
    cache.put(entry.getId(), entry);
    cache.put(src.getHash(), entry);
    return program;
  }
  @Override
  public DialobComposerDocument setAst(DialobComposerDocument ast, StoreEntity src) {
    final var entry = ImmutableCacheEntry.builder().id(src.getId()).source(src).ast(ast).build();
    final var cache = getCache();
    cache.put(entry.getId(), entry);
    cache.put(src.getHash(), entry);
    return ast;
  }
  
  @Override
  public DialobEhCache withName(String name) {
    final var cacheName = createName(name);
    final var cacheHeap = 500;
    final var cacheManager = CacheManagerBuilder.newCacheManagerBuilder() 
        .withCache(cacheName,
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                String.class, CacheEntry.class, 
                ResourcePoolsBuilder.heap(cacheHeap))) 
        .build(); 
    cacheManager.init();
    return new DialobEhCache(cacheManager, cacheName);
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    public DialobEhCache build(String name) {
      final var cacheName = createName(name);
      final var cacheHeap = 500;
      final var cacheManager = CacheManagerBuilder.newCacheManagerBuilder() 
          .withCache(cacheName,
              CacheConfigurationBuilder.newCacheConfigurationBuilder(
                  String.class, CacheEntry.class, 
                  ResourcePoolsBuilder.heap(cacheHeap))) 
          .build(); 
      cacheManager.init();
      
      return new DialobEhCache(cacheManager, cacheName);
    }
  }
  
  private static String createName(String name) {
    return CACHE_PREFIX + "-" + name;
  }
  @Override
  public void flush(String id) {
    final var cache = getCache();
    final var entity = cache.get(id);
    if(entity == null) {
      return;
    }
    cache.remove(entity.getId());
    cache.remove(entity.getSource().getHash());
  }
}
