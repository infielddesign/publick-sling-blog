package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;
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
     * Root resource of all pages.
     */
    private static final String PAGE_ROOT = "page";

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


        /**
         * Path properties
         */
        final String url = request.getParameter("url");
        final String parentpath = request.getParameter("parentpath");
        final String parentnode = request.getParameter("parentnode");
        final String pagePath = parentpath + "/" + url;
        final String primarytype = request.getParameter("primarytype");

        Resource existingNode = resolver.getResource(pagePath);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JcrConstants.JCR_PRIMARYTYPE, primarytype);

        if(primarytype.equals("publick:page")) {

            /**
             * Header properties
             */
            final String header_logo = request.getParameter("header-logo");
            final String header_text = request.getParameter("header-text");

            /**
             * Set header properties
             */
            properties.put("header-logo", header_logo);
            properties.put("header-text", header_text);




            /**
             * Banner properties
             */
            final String banner_title = request.getParameter("banner-title");
            final String banner_text = request.getParameter("banner-text");

            /**
             * Set Banner properties
             */
            properties.put("banner-title", banner_title);
            properties.put("banner-text", banner_text);




            /**
             * Footer properties
             */
            final String footer_logo = request.getParameter("footer-logo");
            final String footer_text = request.getParameter("footer-text");

            /**
             * Set footer properties
             */
            properties.put("footer-logo", footer_logo);
            properties.put("footer-text", footer_text);




            /**
             * Content and page behavior properties
             */
            final String description = request.getParameter("description");
            final String content = request.getParameter("content");
            final boolean visible = Boolean.parseBoolean(request.getParameter("visible"));
            final String[] keywords = request.getParameterValues("keywords");
            final String pageTitle = request.getParameter("pageTitle");
            final String navigationTitle = request.getParameter("navigationTitle");
            final String resourcetype = PublickConstants.PAGE_TYPE_PAGE;

            /**
             * Set content and page behavior properties
             */
            properties.put(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, resourcetype);
            properties.put("visible", visible);
            properties.put("content", content);
            properties.put("description", description);
            properties.put("pageTitle", pageTitle);
            properties.put("navigationTitle", navigationTitle);

            if (keywords != null) {
                properties.put("keywords", keywords);
            }




            /*
            * The following configuration feature has been removed for the moment but may be reinstated in the future
             */
            //final String[] links = request.getParameterValues("links");
            // final String[] scripts = request.getParameterValues("scripts");
            //if (links != null) {
            //    properties.put("links", links);
            //}
            //if (scripts != null) {
            //    properties.put("scripts", scripts);
            //}
            //final String configurationName = request.getParameter("configurationName");
            //properties.put("configurationName", configurationName);
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
                Node node = JcrResourceUtil.createPath(resolver.getResource(parentpath).adaptTo(Node.class), parentnode, NodeType.NT_UNSTRUCTURED, NodeType.NT_UNSTRUCTURED, true);

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