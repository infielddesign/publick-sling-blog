package com.nateyolles.sling.publick.components.admin;

import com.nateyolles.sling.publick.sightly.WCMUse;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONArray;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Sightly component to edit pages in the admin section. The
 * component determines whether to create a new page or edit
 * and existing page. To edit an existing page, pass
 * the resource path in the URL as the suffix.
 */
public class PageConfigurationEdit extends WCMUse {

    private Resource resource;
    private SlingHttpServletRequest request;
    private String configurationName;
    private List<String> configurationNames = new ArrayList<String>();
    private String header;
    private String footer;
    private String[] links;
    private String[] scripts;

    /**
     * Sightly component initialization.
     */
    @Override
    public void activate() {
        resource = getResource();
        request = getRequest();

        String path = request.getParameter("post");

        if (StringUtils.isNotBlank(path)) {
            getPageConfiguration(path);
        }

        ResourceResolver resolver = resource.getResourceResolver();
        Resource pageconf = resolver.getResource("/content/pageconf");

        if (pageconf != null) {
            Iterator<Resource> children = pageconf.listChildren();
            System.out.println(children);
            System.out.println("\n");
            while (children.hasNext()) {
                Resource pageconfResource = children.next();
                ValueMap properties = pageconfResource.adaptTo(ValueMap.class);
                String confName = properties.get("configurationName", String.class);
                configurationNames.add(confName);
            }
        }
    }

    /**
     * Get the page properties if resource already exists otherwise
     * set the month and year properties to the current date.
     *
     * @param path The resource path to the page.
     */
    private void getPageConfiguration(String path) {
        ResourceResolver resolver = resource.getResourceResolver();
        Resource page = resolver.getResource(path);

        if (page != null) {
            ValueMap properties = page.adaptTo(ValueMap.class);
            configurationName = properties.get("configurationName", String.class);
            header = properties.get("header", String.class);
            footer = properties.get("footer", String.class);
            links = properties.get("links", String[].class);
            scripts = properties.get("scripts", String[].class);
        }
    }

    /**
     * Get the page configurationName.
     *
     * @return The page configurationNames.
     */
    public List<String> getConfigurationNames() {
        return configurationNames;
    }

    /**
     * Get the page configurationName.
     *
     * @return The page configurationName.
     */
    public String getConfigurationName() {
        return configurationName;
    }

    /**
     * Get the page header.
     *
     * @return The page header.
     */
    public String getHeader() {
        return header;
    }

    /**
     * Get the page footer.
     *
     * @return The page footer.
     */
    public String getFooter() {
        return footer;
    }

    /**
     * Get the page link.
     *
     * @return The page link.
     */
    public String getLinksJSON() {
        JSONArray jsonArray = null;

        if (links != null) {
            jsonArray = new JSONArray(Arrays.asList(links));
        } else {
            jsonArray = new JSONArray();
        }

        return jsonArray.toString();
    }

    /**
     * Get the page script.
     *
     * @return The page script.
     */
    public String getScriptsJSON() {
        JSONArray jsonArray = null;

        if (scripts != null) {
            jsonArray = new JSONArray(Arrays.asList(scripts));
        } else {
            jsonArray = new JSONArray();
        }

        return jsonArray.toString();
    }
}