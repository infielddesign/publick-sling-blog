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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

//e.g. - curl -v -X POST -H "CQ-Action: Activate" -H "CQ-Handle: /page/capabilities.html" -H "Content-Length: 0" http://publickdisp.dev/dispatcher/invalidate.cache

@SlingServlet(paths = PublickConstants.SERVLET_PATH_ADMIN + "/flushcache")
public class Flushcache extends AdminServlet {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // HTTP POST request
    private void invalidate(String url, String handle) throws Exception {

        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //Add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("CQ-Action", "Activate");
        con.setRequestProperty("CQ-Handle", handle);
        con.setRequestProperty("Content-length", "0");

        // Send post request
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        logger.info("Sending 'POST' request to URL : " + url);
        logger.info("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //Print result
        logger.info("Flushcache response: " + response.toString());
    }

    @Override
    public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            //retrieve the request parameters
            String handle = request.getParameter("handle");

            //hard-coding connection properties is a bad practice, but is done here to simplify the example
            String server = "publickdisp.dev";
            String uri = "/dispatcher/invalidate.cache";
            String url = "http://" + server + uri;
            invalidate(url, handle);

        } catch (Exception e) {
            logger.error("Flushcache servlet exception: " + e.getMessage());
        }
    }
}