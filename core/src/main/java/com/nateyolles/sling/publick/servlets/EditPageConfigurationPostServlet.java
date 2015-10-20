package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.apache.sling.jcr.resource.JcrResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet to save pages.
 */
@SlingServlet(paths = PublickConstants.SERVLET_PATH_ADMIN + "/editpageconf")
public class EditPageConfigurationPostServlet extends SlingAllMethodsServlet {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EditPageConfigurationPostServlet.class);

    /**
     * Root resource of all pages.
     */
    private static final String PAGE_CONF_ROOT = "pageconf";

    /**
     * Create and save page resource.
     *
     * Creates page path and saves properties. If page resource already
     * exists, the resource is updated with new properties.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        ResourceResolver resolver = request.getResourceResolver();
        Session session = resolver.adaptTo(Session.class);

        final String configurationName = request.getParameter("configurationName");
        final String header = request.getParameter("header");
        final String footer = request.getParameter("footer");
        final String[] links = request.getParameterValues("links");
        final String[] scripts = request.getParameterValues("scripts");
        final String pageConfPath = PublickConstants.PAGE_PATH_CONF + "/" + configurationName;

        Resource existingNode = resolver.getResource(pageConfPath);

        Map<String, Object> properties = new HashMap<String, Object>();
//        properties.put(JcrConstants.JCR_PRIMARYTYPE, PublickConstants.NODE_TYPE_PAGE);
//        properties.put(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, PublickConstants.PAGE_TYPE_PAGE);
        properties.put("configurationName", configurationName);
        properties.put("header", header);
        properties.put("footer", footer);

        if (links != null) {
            properties.put("links", links);
        }

        if (scripts != null) {
            properties.put("scripts", scripts);
        }

        try {
            UserManager userManager = ((JackrabbitSession)session).getUserManager();
            Authorizable auth = userManager.getAuthorizable(session.getUserID());
            properties.put("author", auth.getID());
        } catch (RepositoryException e) {
            LOGGER.error("Could not get user.", e);
        }

        try {
            if (existingNode != null) {
                ModifiableValueMap existingProperties = existingNode.adaptTo(ModifiableValueMap.class);
                existingProperties.putAll(properties);
            } else {
                Node node = JcrResourceUtil.createPath(resolver.getResource(PublickConstants.CONTENT_PATH).adaptTo(Node.class), PAGE_CONF_ROOT, NodeType.NT_UNSTRUCTURED, NodeType.NT_UNSTRUCTURED, true);

                Resource page = resolver.create(resolver.getResource(node.getPath()), configurationName, properties);

                Node pageNode = page.adaptTo(Node.class);
                pageNode.addMixin(NodeType.MIX_CREATED);
            }


            resolver.commit();
            resolver.close();

            response.sendRedirect(PublickConstants.ADMIN_PAGE_CONF_LIST_PATH + ".html");
        } catch (RepositoryException e) {
            LOGGER.error("Could not save page to repository.", e);
            response.sendRedirect(request.getHeader("referer"));
        } catch (PersistenceException e){
            LOGGER.error("Could not save page to repository.", e);
            response.sendRedirect(request.getHeader("referer"));
        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
                resolver = null;
            }
        }
    }
}