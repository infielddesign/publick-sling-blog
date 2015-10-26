package com.nateyolles.sling.publick.components.admin;

import com.nateyolles.sling.publick.services.DispatcherService;
import com.nateyolles.sling.publick.sightly.WCMUse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingScriptHelper;

/**
 * Sightly component to get Dispatcher Settings such as dispatcher host.
 */
public class DispatcherConfig extends WCMUse {

    /** The Sling Script Helper to get services. */
    private SlingScriptHelper scriptHelper;

    /** The current resource. */
    private Resource resource;

    /** The name of the dispatcher host. */
    private String dispatcherHost;

    /** The name of the dispatcher invalidate cache URI. */
    private String dispatcherInvalidateCacheUri;

    /** Initialize the Sightly component. */
    @Override
    public void activate() {
        scriptHelper = getSlingScriptHelper();
        resource = getResource();

        DispatcherService dispatcherService = scriptHelper.getService(DispatcherService.class);

        if (dispatcherService != null) {
            dispatcherHost = dispatcherService.getDispatcherHost();
            dispatcherInvalidateCacheUri = dispatcherService.getDispatcherInvalidateCacheUri();
        }
    }

    /**
     * Get the dispatcher host.
     *
     * @return The dispatcher host.
     */
    public String getDispatcherHost() {
        return dispatcherHost;
    }

    /**
     * Get the dispatcher invalidate cache URI.
     *
     * @return The dispatcher invalidate cache URI.
     */
    public String getDispatcherInvalidateCacheUri() {
        return dispatcherInvalidateCacheUri;
    }

}