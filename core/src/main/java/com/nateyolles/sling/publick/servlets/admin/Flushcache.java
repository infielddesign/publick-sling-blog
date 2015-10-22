package com.nateyolles.sling.publick.servlets.admin;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.servlets.AdminServlet;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;

//e.g. - curl -v -X POST -H "CQ-Action: Activate" -H "CQ-Handle: /page/capabilities.html" -H "Content-Length: 0" http://publickdisp.dev/dispatcher/invalidate.cache

@SlingServlet(paths = PublickConstants.SERVLET_PATH_ADMIN + "/flushcache")
public class Flushcache extends AdminServlet {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // HTTP POST request
    private void invalidate(String url, String handle) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //Add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("CQ-Action", "Activate");
        con.setRequestProperty("CQ-Handle", handle);
        con.setRequestProperty("Content-length", "0");

        // Send post request
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        LOGGER.info("Sending 'POST' request to URL : " + url);
        LOGGER.info("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //Print result
        LOGGER.info("Flushcache response: " + response.toString());
    }

    @Override
    public void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)  throws ServletException, IOException {
        try {
            //Retrieve the request parameters
            String handle = request.getParameter("handle");

            //Hard-coding connection properties is a bad practice, but is done here to simplify the example
            String server = "33.33.33.14";
            String uri = "/dispatcher/invalidate.cache";
            String url = "http://" + server + uri;
            invalidate(url, handle);
            response.sendRedirect(PublickConstants.ADMIN_PAGE_LIST_PATH + ".html");

        } catch (Exception e) {
            LOGGER.error("Flushcache servlet exception: " + e.getMessage());
        }
    }
}