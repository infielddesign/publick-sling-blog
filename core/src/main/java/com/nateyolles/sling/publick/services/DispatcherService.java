package com.nateyolles.sling.publick.services;

import java.util.Map;

/**
 * API's to get and set email configurations.
 */
public interface DispatcherService {

    /** OSGi property name for Dispatcher Host */
    public static final String DISPATCHER_HOST = "dispatcher.host";

    /** OSGi property name for Dispatcher Invalidate Cache URI */
    public static final String DISPATCHER_INVALIDATE_CACHE_URI = "dispatcher.invalidate.cache.uri";


    /**
     * Get the Dispatcher host.
     *
     * @return The Dispatcher host.
     */
    String getDispatcherHost();


    /**
     * Get the Dispatcher host.
     *
     * @return The Dispatcher host.
     */
    String getDispatcherInvalidateCacheUri();


    /**
     * Set multiple properties for the Dispatcher Settings service.
     *
     * This is useful for setting multiple properties as the same
     * time in that the OSGi component will only be updated once
     * and thus reset only once.
     *
     * @param properties A map of properties to set.
     * @return true if save was successful.
     */
    boolean setProperties(final Map<String, Object> properties);

    /**
     * Invalidate cache page.
     * @param url
     * @param handle
     */
    void invalidate(String url, String handle) throws Exception;
}