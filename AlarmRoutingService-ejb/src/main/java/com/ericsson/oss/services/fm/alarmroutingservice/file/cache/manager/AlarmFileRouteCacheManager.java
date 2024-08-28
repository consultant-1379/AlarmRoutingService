/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.fm.alarmroutingservice.file.cache.manager;

import static com.ericsson.oss.services.fm.alarmroutingservice.util.Constants.DELIMITER_UNDERSCORE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.cache.Cache;
import javax.cache.Cache.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.cache.classic.CacheProviderBean;
import com.ericsson.oss.itpf.sdk.cache.infinispan.producer.CacheEntryIterator;
import com.ericsson.oss.services.fm.alarmroutemanagement.models.AlarmRoute;
import com.ericsson.oss.services.fm.alarmroutingservice.instrumentation.AlarmRouteCounters;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;

/**
 * Provides access to {@link AlarmFileRouteCache} for add,remove operations. <br>
 * <p>
 * This {@link AlarmFileRouteCache} holds information of alarms and list of matched AlarmRoutes.<br>
 * <p>
 */
public final class AlarmFileRouteCacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmFileRouteCacheManager.class);
    private static final String MODELED_CACHE_NAME = "AlarmFileRouteCache";

    private Cache<String, ProcessedAlarmEvent> alarmFileRouteCache;
    private final AtomicInteger cacheCounter = new AtomicInteger(0);

    private AlarmFileRouteCacheManager() {
    }

    public static AlarmFileRouteCacheManager getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void addToCache(final ProcessedAlarmEvent processedAlarmEvent, final List<AlarmRoute> alarmRoutes) {
        if (isCacheNotInitialized()) {
            initializeCache();
        }
        writeToCache(processedAlarmEvent, alarmRoutes);
    }

    public void removeEntriesFromCache(final Set<String> keys) {
        if (keys != null && alarmFileRouteCache != null) {
            alarmFileRouteCache.removeAll(keys);
            LOGGER.debug("Removed {} entries from AlarmFileRouteCache.", keys);
        }
    }

    /**
     * Reads the entries from AlarmFileRouteCache.
     * @param maxEntries
     *            Maximum number of entries to be read from Cache.
     * @return Map of all the events read from the cache successfully.
     */
    public Map<String, ProcessedAlarmEvent> readFromAlarmFileRouteCache(final int maxEntries) {
        Map<String, ProcessedAlarmEvent> cacheEntries = new HashMap<>();
        if(alarmFileRouteCache != null) {
           cacheEntries = readFromCache(maxEntries);
        }
        return cacheEntries;
    }

    /**
     * Initializes AlarmFileRouteCache which stores alarms which are routed to file.
     */
    public void initializeCache() {
        try {
            final CacheProviderBean cacheProviderBean = new CacheProviderBean();
            alarmFileRouteCache = cacheProviderBean.createOrGetModeledCache(MODELED_CACHE_NAME);
        } catch (final Exception exception) {
            LOGGER.error("Exception in createOrGetModeledCache for AlarmFileRouteCache is : ", exception);
        }
    }


    /**
     * Checks whether an attempt to initialize cache is made.
     * @return true if Cache is Not Initialized, false if an attempt is already made to initialize.
     */
    private boolean isCacheNotInitialized() {
        if (cacheCounter.get() == 0 && alarmFileRouteCache == null) {
            cacheCounter.incrementAndGet();
            return true;
        }
        return false;
    }

    /**
     * Writes received alarm to AlarmFileRouteCache with unique key.
     * @param processedAlarmEvent
     *            {@link ProcessedAlarmEvent}
     * @param alarmRoutes
     *            list of alarm routes
     */
    private void writeToCache(final ProcessedAlarmEvent processedAlarmEvent, final List<AlarmRoute> alarmRoutes) {
        if (alarmFileRouteCache != null) {
            final String uuid = UUID.randomUUID().toString();
            for (final AlarmRoute alarmRoute : alarmRoutes) {
                final Long routeId = alarmRoute.getRouteId();
                final String key = buildKeyForCacheEntry(routeId, uuid);
                alarmFileRouteCache.put(key, processedAlarmEvent);
                LOGGER.trace("Added {} to Cache with key:{}", processedAlarmEvent, key);
            }
        } else {
            LOGGER.error("Alarms {} will not be routed to file as AlarmFileRouteCache is not initialized.", alarmRoutes.size());
            AlarmRouteCounters.increasedFailedAlarmCount(alarmRoutes.size());
        }
    }

    private Map<String, ProcessedAlarmEvent> readFromCache(final int maxEntries) {
        final Map<String, ProcessedAlarmEvent> cacheEntries = new HashMap<String, ProcessedAlarmEvent>();
        CacheEntryIterator<String, ProcessedAlarmEvent> cacheIterator = null;
        try {
               cacheIterator = (CacheEntryIterator) alarmFileRouteCache.iterator();
               while (cacheIterator.hasNext()) {
                    final Entry<String, ProcessedAlarmEvent> entry = cacheIterator.next();
                    if (cacheEntries.size() >= maxEntries) {
                        break;
                    }
                    if (entry != null) {
                        final String key = entry.getKey();
                        final ProcessedAlarmEvent processedAlarmEvent = alarmFileRouteCache.get(key);
                        if (processedAlarmEvent != null) {
                            cacheEntries.put(key, processedAlarmEvent);
                        }
                    }
                }
            }catch (final Exception exception) {
                LOGGER.error("Exception occurred while reading AlarmFileRouteCache is : ", exception);
            }finally {
                if(cacheIterator != null) {
                    cacheIterator.close();
                }
            }
        return cacheEntries;
    }

    private String buildKeyForCacheEntry(final Long routeId, final String uuid) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(routeId);
        stringBuilder.append(DELIMITER_UNDERSCORE);
        stringBuilder.append(uuid);
        return stringBuilder.toString();
    }

    private static class SingletonHelper {
        private static final AlarmFileRouteCacheManager INSTANCE = new AlarmFileRouteCacheManager();
    }
}
