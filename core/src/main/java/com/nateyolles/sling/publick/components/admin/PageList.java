package com.nateyolles.sling.publick.components.admin;

import com.nateyolles.sling.publick.services.PageService;
import com.nateyolles.sling.publick.sightly.WCMUse;
import org.apache.sling.api.scripting.SlingScriptHelper;

import javax.jcr.NodeIterator;

/**
 * Sightly component to list pages in the admin section.
 */
public class PageList extends WCMUse {

    /**
     * Sling Script Helper to get services.
     */
    private SlingScriptHelper scriptHelper;

    /**
     * Page Service to get pages.
     */
    private PageService pageService;

    /**
     * Initialize Sightly component.
     */
    @Override
    public void activate() {
        scriptHelper = getSlingScriptHelper();
        pageService = scriptHelper.getService(PageService.class);
    }

    /**
     * Get all pages without pagination.
     *
     * @return The pages ordered from newest to oldest.
     */
    public NodeIterator getPages() {
        NodeIterator nodes = null;

        System.out.print("pageService");
        System.out.print(pageService);
        System.out.print("pageService");
        if (pageService != null) {
            nodes = pageService.getPages();
        }

        return nodes;
    }
}