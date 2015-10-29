package com.nateyolles.sling.publick.components.admin;

import java.util.ArrayList;
import java.util.Iterator;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.sightly.WCMUse;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONArray;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
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
    private String configurationName;
    private String url;
    private boolean visible;
    private String[] keywords;
    private String[] links;
    private String[] scripts;
    private String content;
    private String description;
    private String primaryType;

    /**
     * Array of strings that represents primaryTypes.
     */
    private String[] primaryTypes;

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
            configurationName = properties.get("configurationName", String.class);
            keywords = properties.get("keywords", String[].class);
            links = properties.get("links", String[].class);
            scripts = properties.get("scripts", String[].class);
            content = properties.get("content", String.class);
            description = properties.get("description", String.class);
            url = page.getName();

            primaryType = properties.get("jcr:primaryType", String.class);

//            Node node = page.adaptTo(Node.class);
//            try {
//                primaryType = node.getPrimaryNodeType().getName();
//            }
//            catch (RepositoryException e) {
//                LOGGER.error("Could not get user.", e);
//            }
        }
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
     * Get the multi-value link property.
     *
     * @return The multi-value link property.
     */
    public String[] getLinks() {
        return links;
    }

    /**
     * Get the multi-value script property.
     *
     * @return The multi-value script property.
     */
    public String[] getScripts() {
        return scripts;
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
     * Get all primaryTypes.
     *
     * @return All primaryTypes.
     */
    public String[] getPrimaryTypes() {
        primaryTypes[0] = PublickConstants.NODE_TYPE_FOLDER;
        primaryTypes[1] = PublickConstants.NODE_TYPE_PAGE;

        return primaryTypes;
    }

    /**
     * Get the page's primaryType.
     *
     * @return The page's primaryType.
     */
    public String getPrimaryType() {
        return primaryType;
    }

    /**
     * Is a page or not.
     *
     * @return True is page.
     */
    public String displayPageForm() {
        if(primaryType!=null && primaryType.equals("publick:page"))
            return "";
        return "hide";
    }
}