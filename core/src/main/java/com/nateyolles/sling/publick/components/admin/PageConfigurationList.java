package com.nateyolles.sling.publick.components.admin;

import com.nateyolles.sling.publick.services.PageConfigurationService;
import com.nateyolles.sling.publick.sightly.WCMUse;
import org.apache.sling.api.scripting.SlingScriptHelper;

import javax.jcr.NodeIterator;

/**
 * Sightly component to list pages in the admin section.
 */
public class PageConfigurationList extends WCMUse {

    /**
     * Sling Script Helper to get services.
     */
    private SlingScriptHelper scriptHelper;

    /**
     * Page Service to get page configurations.
     */
    private PageConfigurationService pageConfigurationService;

    /**
     * Initialize Sightly component.
     */
    @Override
    public void activate() {
        scriptHelper = getSlingScriptHelper();
        pageConfigurationService = scriptHelper.getService(PageConfigurationService.class);
        System.out.print(pageConfigurationService);
    }

    /**
     * Get all page configurations.
     *
     * @return The page configurations ordered from newest to oldest.
     */
    public NodeIterator getPageConfigurations() {
        NodeIterator nodes = null;

        if (pageConfigurationService != null) {
            nodes = pageConfigurationService.getPageConfigurations();
        }

        return nodes;
    }
}