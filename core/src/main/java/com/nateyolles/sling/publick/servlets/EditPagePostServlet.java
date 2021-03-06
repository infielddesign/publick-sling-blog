package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.FileUploadService;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.*;
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
@SlingServlet(paths = PublickConstants.SERVLET_PATH_ADMIN + "/editpage")
public class EditPagePostServlet extends SlingAllMethodsServlet {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EditPagePostServlet.class);

    /**
     * Page path in the format of /yyyy/MM.
     */
    private static final String PAGE_PATH = "/%d/%02d";

    /**
     * Root resource of all pages.
     */
    private static final String PAGE_ROOT = "page";

    /**
     * File upload service.
     */
    @Reference
    private FileUploadService fileUploadService;

    /**
     * Create and save page resource.
     *
     * Creates page path and saves properties. If page resource already
     * exists, the resource is updated with new properties. Saves file
     * to the assets folder using the FileUploadService.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        ResourceResolver resolver = request.getResourceResolver();
        Session session = resolver.adaptTo(Session.class);

        final String description = request.getParameter("description");
        final String content = request.getParameter("content");
        final String url = request.getParameter("url");
        final boolean visible = Boolean.parseBoolean(request.getParameter("visible"));
        final String[] keywords = request.getParameterValues("keywords");
        final String pagePath = PublickConstants.PAGE_PATH + "/" + url;

        Resource existingNode = resolver.getResource(pagePath);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JcrConstants.JCR_PRIMARYTYPE, PublickConstants.NODE_TYPE_PAGE);
        properties.put(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, PublickConstants.PAGE_TYPE_PAGE);
        properties.put("visible", visible);
        properties.put("content", content);
        properties.put("description", description);

        if (keywords != null) {
            properties.put("keywords", keywords);
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
                Node node = JcrResourceUtil.createPath(resolver.getResource(PublickConstants.CONTENT_PATH).adaptTo(Node.class), PAGE_ROOT, NodeType.NT_UNSTRUCTURED, NodeType.NT_UNSTRUCTURED, true);

                Resource page = resolver.create(resolver.getResource(node.getPath()), url, properties);
                Node pageNode = page.adaptTo(Node.class);
                pageNode.addMixin(NodeType.MIX_CREATED);
            }

            resolver.commit();
            resolver.close();

            response.sendRedirect(PublickConstants.ADMIN_PAGE_LIST_PATH + ".html");
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