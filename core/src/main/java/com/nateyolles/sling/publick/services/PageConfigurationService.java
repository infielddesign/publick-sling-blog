package com.nateyolles.sling.publick.services;

import javax.jcr.NodeIterator;

/**
 * API to search and retrieve pages.
 */
public interface PageConfigurationService {

    /**
     * Get all page configurations in order of newest first.
     *
     * @return All page configurations in order of newest first.
     */
    NodeIterator getPageConfigurations();
}