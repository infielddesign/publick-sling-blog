package com.nateyolles.sling.publick.services.impl;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.PageService;
import org.apache.felix.scr.annotations.*;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.LoginException;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.Map;

@Service( value = PageService.class )
@Component( metatype = true, immediate = true )
public class PageServiceImpl implements PageService {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PageServiceImpl.class);

    /**
     * JCR_SQL2 query to get all pages in order of newest first.
     */
    private static final String PATH_QUERY = String.format("SELECT * FROM [%s] AS s WHERE "
                    + "ISDESCENDANTNODE([%s]) AND s.[%s] = '%s' ORDER BY [%s] desc",
            PublickConstants.NODE_TYPE_PAGE,
            PublickConstants.PAGE_PATH,
            JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY,
            PublickConstants.PAGE_TYPE_PAGE,
            JcrConstants.JCR_CREATED);

    /**
     * The JCR session.
     */
    private Session session;

    /**
     * The JCR Repository.
     */
    @Reference
    private SlingRepository repository;

    /**
     * Get all pages without pagination.
     *
     * @return The pages.
     */
    public NodeIterator getPages() {
        return getPages(null, null);
    }

    /**
     * Get pages with pagination
     *
     * @param offset The starting point of pages to return.
     * @param limit The number of pages to return.
     * @return The pages.
     */
    public NodeIterator getPages(Long offset, Long limit) {
        NodeIterator nodes = null;

        if (session != null) {
            try {
                QueryManager queryManager = session.getWorkspace().getQueryManager();
                Query query = queryManager.createQuery(PATH_QUERY, Query.JCR_SQL2);

                if (offset != null) {
                    query.setOffset(offset);
                }

                if (limit != null) {
                    query.setLimit(limit);
                }

                QueryResult result = query.execute();
                nodes = result.getNodes();
            } catch (RepositoryException e) {
                LOGGER.error("Could not search repository", e);
            }
        }

        return nodes;
    }

    /**
     * Get the number of pagination pages based on number of pages
     * found and specified number of pages per page.
     *
     * @param pageSize The number of pages per pagination page.
     * @return The number of pagination pages.
     */
    public long getNumberOfPages(int pageSize) {
        long posts = getNumberOfPages();

        return (long)Math.ceil((double)posts / pageSize);
    }

    /**
     * Get number of pages.
     *
     * @return The number of pages.
     */
    public long getNumberOfPages() {
        return getPages().getSize();
    }

    /**
     * Activate Service.
     *
     * @param properties
     */
    @Activate
    protected void activate(Map<String, Object> properties) {
        try {
            session = repository.loginAdministrative(null);
        } catch (LoginException e) {
            LOGGER.error("Could not get session.", e);
        } catch (RepositoryException e) {
            LOGGER.error("Could not get session.", e);
        }
    }

    /**
     * Deactivate Service.
     *
     * @param ctx
     */
    @Deactivate
    protected void deactivate(ComponentContext ctx) {
        if (session != null && session.isLive()) {
            session.logout();
            session = null;
        }
    }
}