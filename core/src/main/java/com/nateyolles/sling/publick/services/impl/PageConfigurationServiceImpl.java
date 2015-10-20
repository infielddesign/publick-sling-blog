package com.nateyolles.sling.publick.services.impl;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.PageConfigurationService;
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

@Service( value = PageConfigurationService.class )
@Component( metatype = true, immediate = true )
public class PageConfigurationServiceImpl implements PageConfigurationService {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PageConfigurationServiceImpl.class);


    /**
     * JCR_SQL2 query to get all page configuration in order of newest first.
     */
    private static final String PAGE_CONF_PATH_QUERY = String.format("SELECT * FROM [nt:unstructured] AS s WHERE "
                    + "ISDESCENDANTNODE([%s]) ORDER BY [%s] desc",
            PublickConstants.PAGE_PATH_CONF,
            JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY,
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


    public NodeIterator getPageConfigurations(){
        NodeIterator nodes = null;

        if (session != null) {
            try {

                QueryManager queryManager = session.getWorkspace().getQueryManager();

                Query query = queryManager.createQuery(PAGE_CONF_PATH_QUERY, Query.JCR_SQL2);
                QueryResult result = query.execute();

                nodes = result.getNodes();

            } catch (RepositoryException e) {
                LOGGER.error("Could not search repository", e);
            }
        }

        return nodes;
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