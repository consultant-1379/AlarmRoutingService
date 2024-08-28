/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.file.cache.manager;

import static org.junit.Assert.assertNotNull;


import javax.cache.Cache;
import javax.cache.Cache.Entry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import java.util.Map;

import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.sdk.cache.classic.CacheProviderBean;
import com.ericsson.oss.itpf.sdk.cache.infinispan.producer.CacheEntryIterator;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

@RunWith(MockitoJUnitRunner.class)
public final class AlarmFileRouteCacheManagerTest {

    @InjectMocks
    private AlarmFileRouteCacheManager alarmFileRouteCacheManager = AlarmFileRouteCacheManager.getInstance();

    @Mock
    private Cache<String, Object> alarmFileRouteCache;

    @Mock
    private CacheEntryIterator<String, Object> cacheIterator;

    @Mock
    private CacheProviderBean cacheProviderBean;

    @Mock
    Entry<String, Object> entry;

    @Test
    public void readFromAlarmFileRouteCachePositive_test() {
         final int fileRouteAlarmsBatchSize =2;
         ProcessedAlarmEvent alarm1 =new ProcessedAlarmEvent();
         ProcessedAlarmEvent alarm2 =new ProcessedAlarmEvent();
         when(alarmFileRouteCache.get("key1")).thenReturn(alarm1);
         when(alarmFileRouteCache.get("key2")).thenReturn(null);
         when(alarmFileRouteCache.get("key3")).thenReturn(alarm2);
         when(alarmFileRouteCache.iterator()).thenReturn(cacheIterator);
         when(cacheIterator.hasNext()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true);
         when(cacheIterator.next()).thenReturn(entry).thenReturn(entry).thenReturn(null).thenReturn(entry);
         when(entry.getKey()).thenReturn("key1").thenReturn("key2").thenReturn("key3");
        assertNotNull(alarmFileRouteCacheManager.readFromAlarmFileRouteCache(fileRouteAlarmsBatchSize));
    }

    @Test
    public void readFromAlarmFileRouteCacheWithNoCacheEntries_test() {
         final int fileRouteAlarmsBatchSize =2;
         when(alarmFileRouteCache.iterator()).thenReturn(cacheIterator);
         when(cacheIterator.hasNext()).thenReturn(false);
         assertNotNull(alarmFileRouteCacheManager.readFromAlarmFileRouteCache(fileRouteAlarmsBatchSize));
    }

    @Test
    public void readFromAlarmFileRouteCacheWithException_test() {
         final int fileRouteAlarmsBatchSize =2;
         final Map<String, ProcessedAlarmEvent> cacheEntries = alarmFileRouteCacheManager.readFromAlarmFileRouteCache(fileRouteAlarmsBatchSize);
         assert(cacheEntries.isEmpty());
    }

}
