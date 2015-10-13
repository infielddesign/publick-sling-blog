package com.nateyolles.sling.publick.services;

import javax.jcr.NodeIterator;

/**
 * API to search and retrieve pages.
 */
public interface PageService {

    /**
     * Get all pages in order of newest first.
     *
     * @return All pages in order of newest first.
     */
    NodeIterator getPages();

    /**
     * Get paginated pages in order of newest first.
     *
     * @param offset The starting point of pages to get.
     * @param limit The number of pages to get.
     * @return The pages according to the starting point and length.
     */
    NodeIterator getPages(Long offset, Long limit);

    /**
     * Get the number of pages in the system.
     *
     * @return The number of pages.
     */
    long getNumberOfPages();

    /**
     * Get the number of pagination pages determined by the total
     * number of pages and specified number of pages.
     * per page.
     *
     * @param pageSize The number of pages per size.
     * @return The number of pagination pages required to display all
     *            pages.
     */
    long getNumberOfPages(int pageSize);
}