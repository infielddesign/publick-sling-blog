package com.nateyolles.sling.publick.servlets;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.UserService;
import org.apache.commons.lang.CharEncoding;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet to save page posts.
 */
@SlingServlet(paths = PublickConstants.SERVLET_PATH_ADMIN + "/deletepage")
public class DeletePagePostServlet extends AdminServlet {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeletePagePostServlet.class);

    /**
     * Root resource of all page posts.
     */
    private static final String PAGE_ROOT = "page";

    /**
     * Service to determine if the current user has write permissions.
     */
    @Reference
    UserService userService;

    /**
     * Create and save page resource.
     * <p/>
     * Creates page path and saves properties. If page resource already
     * exists, the resource is updated with new properties. Saves file
     * to the assets folder using the FileUploadService.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        final PrintWriter writer = response.getWriter();
        final boolean allowWrite = userService.isAuthorable(request.getResourceResolver().adaptTo(Session.class));

        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType("application/json");

        if (allowWrite) {
            // Delete node
            ResourceResolver resolver = request.getResourceResolver();

            final String deletePath = request.getParameter("deletePath");
            Resource existingNode = resolver.getResource(PublickConstants.CONTENT_PATH + "/" + deletePath);

            try {
                resolver.delete(existingNode);
                response.setStatus(SlingHttpServletResponse.SC_OK);
                sendResponse(writer, "OK", "Node " + deletePath + " successfully deleted.");

                resolver.commit();
                resolver.close();

//                response.sendRedirect(PublickConstants.ADMIN_PAGE_LIST_PATH + ".html");
            } catch (PersistenceException e){
                LOGGER.error("Could not save page to repository.", e);
                response.sendRedirect(request.getHeader("referer"));
            } finally {
                if (resolver != null && resolver.isLive()) {
                    resolver.close();
                    resolver = null;
                }
            }

        } else {
            response.setStatus(SlingHttpServletResponse.SC_FORBIDDEN);
            sendResponse(writer, "Error", "Current user not authorized.");
        }
    }
}