package com.nateyolles.sling.publick.services.impl;

import com.nateyolles.sling.publick.services.OsgiConfigurationService;
import com.nateyolles.sling.publick.services.DispatcherService;
import org.apache.felix.scr.annotations.*;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * System settings configuration to save blog engine settings
 * such as blog name and extensionless URLs.
 */
@Service(value = DispatcherService.class)
@Component(metatype = true,
           immediate = true,
           name = "Publick dispatcher settings",
           description = "General dispatcher settings.")
@Properties({
    @Property(name = DispatcherServiceImpl.DISPATCHER_HOST,
              value = DispatcherServiceImpl.DISPATCHER_HOST_DEFAULT_VALUE,
              label = "Dispatcher Host",
              description = "The Dispatcher's Host."),
    @Property(name = DispatcherServiceImpl.DISPATCHER_INVALIDATE_CACHE_URI,
              value = DispatcherServiceImpl.DISPATCHER_INVALIDATE_CACHE_URI_DEFAULT_VALUE,
              label = "Dispatcher Ivalidate cache URI",
              description = "Enabling extenionless URLs alters links written by the blog engine. "
                      + "You must also have corresponding web server redirects in place.")
})
public class DispatcherServiceImpl implements DispatcherService {

    /** Service to get and set OSGi properties. */
    @Reference
    private OsgiConfigurationService osgiService;

    /** The logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherService.class);

    /** PID of the current OSGi component */
    private static final String COMPONENT_PID = "Publick dispatcher settings";

    /** Default value for the dispatcher host */
    public static final String DISPATCHER_HOST_DEFAULT_VALUE = "http://localhost";

    /** Default value for dispatcher invalidate cache  URI */
    public static final String DISPATCHER_INVALIDATE_CACHE_URI_DEFAULT_VALUE = "/dispatcher/invalidate.cache";

    /** Service activation */
    @Activate
    protected void activate(Map<String, Object> properties) {
    }

    /**
     * Set multiple properties for the Dispatcher Settings service.
     *
     * This is useful for setting multiple properties as the same
     * time in that the OSGi component will only be updated once
     * and thus reset only once.
     *
     * @param properties A map of properties to set.
     * @return true if save was successful.
     */
    public boolean setProperties(final Map<String, Object> properties) {
        return osgiService.setProperties(COMPONENT_PID, properties);
    }

    /**
     * Get the name of the dispatcher host.
     *
     * @return The name of the dispatcher host.
     */
    public String getDispatcherHost() {
        return osgiService.getStringProperty(COMPONENT_PID, DISPATCHER_HOST, DISPATCHER_HOST_DEFAULT_VALUE);
    }

    /**
     * Get the name of the dispatcher invalidate cache URI.
     *
     * @return The name of the dispatcher invalidate cache URI.
     */
    public String getDispatcherInvalidateCacheUri() {
        return osgiService.getStringProperty(COMPONENT_PID, DISPATCHER_INVALIDATE_CACHE_URI, DISPATCHER_INVALIDATE_CACHE_URI_DEFAULT_VALUE);
    }


    /**
     * Invalidate cache page.
     *
     * @param url
     * @param handle
     * @throws Exception
     */
    // HTTP POST request
    public void invalidate(String url, String handle) throws Exception {

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
}