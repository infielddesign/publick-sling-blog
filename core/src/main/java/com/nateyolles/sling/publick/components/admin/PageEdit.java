package com.nateyolles.sling.publick.components.admin;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.sightly.WCMUse;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONArray;

import java.util.Arrays;

/**
 * Sightly component to edit pages in the admin section. The
 * component determines whether to create a new page or edit
 * and existing page. To edit an existing page, pass
 * the resource path in the URL as the suffix.
 */
public class PageEdit extends WCMUse {

    private Resource resource;
    private SlingHttpServletRequest request;
    private String url;
    private boolean visible;
    private String[] keywords;
    private String content;
    private String description;
    private String handle;

    /**
     * Sightly component initialization.
     */
    @Override
    public void activate() {
        resource = getResource();
        request = getRequest();

        String path = request.getParameter("post");

        if (StringUtils.isNotBlank(path)) {
            getPage(path);
        }
    }

    /**
     * Get the page properties if resource already exists otherwise
     * set the month and year properties to the current date.
     *
     * @param path The resource path to the page.
     */
    private void getPage(String path) {
        ResourceResolver resolver = resource.getResourceResolver();
        Resource page = resolver.getResource(path);

        if (page != null) {
            ValueMap properties = page.adaptTo(ValueMap.class);
            url = properties.get("url", String.class);
            visible = Boolean.valueOf(properties.get("visible", false));
            keywords = properties.get("keywords", String[].class);
            content = properties.get("content", String.class);
            description = properties.get("description", String.class);
            url = page.getName();
            handle = StringUtils.removeStart(PublickConstants.PAGE_PATH + "/" + url, PublickConstants.CONTENT_PATH);
        }
    }

    /**
     * Get the resource name of the page URL.
     *
     * @return The resource name of the page URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the visible property.
     *
     * @return The visible property.
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * Get the multi-value keyword property.
     *
     * @return The multi-value keyword property.
     */
    public String[] getKeywords() {
        return keywords;
    }

    /**
     * Get the multi-value keywords property as a JSON string.
     *
     * @return The multi-value keyword property as a JSON string.
     */
    public String getKeywordsJSON() {
        JSONArray jsonArray = null;

        if (keywords != null) {
            jsonArray = new JSONArray(Arrays.asList(keywords));
        } else {
            jsonArray = new JSONArray();
        }

        return jsonArray.toString();
    }

    /**
     * Get the page content.
     *
     * @return The page content.
     */
    public String getContent() {
        return content;
    }


    /**
     * Get the description property.
     *
     * @return The description property.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the handle property.
     *
     * @return The handle property.
     */
    public String getHandle() {
        return handle + ".html";
    }
}