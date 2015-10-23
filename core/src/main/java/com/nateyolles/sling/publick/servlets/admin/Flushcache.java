package com.nateyolles.sling.publick.servlets.admin;

import com.nateyolles.sling.publick.services.DispatcherService;
import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.servlets.AdminServlet;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.servlet.ServletException;

//e.g. - curl -v -X POST -H "CQ-Action: Activate" -H "CQ-Handle: /page/capabilities.html" -H "Content-Length: 0" http://publickdisp.dev/dispatcher/invalidate.cache

@SlingServlet(paths = PublickConstants.SERVLET_PATH_ADMIN + "/flushcache")
public class FlushCache extends AdminServlet {
    DispatcherService dispatcherService;

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)  throws ServletException, IOException {
        try {

            /**
             * Retrieve the request parameter
             */
            String handle = request.getParameter("handle");


            /**
             * Get Host and URI from dispatcherService
             */
            String server = dispatcherService.getDispatcherHost();
            String uri = dispatcherService.getDispatcherInvalidateCacheUri();
            String url = "http://" + server + uri;

            dispatcherService.invalidate(url, handle);


            /**
             * Redirect to page list after invalidating page in dispatcher.
             */
            response.sendRedirect(PublickConstants.ADMIN_PAGE_LIST_PATH + ".html");

        } catch (Exception e) {
            LOGGER.error("Flushcache servlet exception: " + e.getMessage());
        }
    }
}