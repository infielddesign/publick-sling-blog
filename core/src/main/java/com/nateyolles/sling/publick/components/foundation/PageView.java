package com.nateyolles.sling.publick.components.foundation;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.LinkRewriterService;
import com.nateyolles.sling.publick.sightly.WCMUse;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Sightly component to display a single page.
 */
public class PageView extends WCMUse {

    /**
     * Logger instance to log and debug errors.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PageView.class);

    /**
     * Link Rewriter to create proper display paths for meta
     * tags and social shares.
     */
    private LinkRewriterService linkRewriter;

    /**
     * Selector to request view for displaying page in
     * list/digest view.
     */
    private static final String LIST_VIEW_SELECTOR = "list";

    /**
     * The resource resolver to map paths.
     */
    private ResourceResolver resolver;

    private Resource resource;
    private SlingHttpServletRequest request;
    private String url;
    private boolean visible;
    private String[] keywords;
    private String[] links;
    private String[] scripts;
    private String image;
    private String content;
    private String configurationName;
    private String description;
    private boolean listView;

    /**
     * The page post image's relative path
     */
    private String imageRelativePath;

    /**
     * The page post image's absolute path taking extensionless
     * URLs into account.
     */
    private String imageAbsolutePath;

    /**
     * Sightly component initialization.
     */
    @Override
    public void activate() {
        resource = getResource();
        request = getRequest();
        resolver = getResourceResolver();
        listView = Arrays.asList(request.getRequestPathInfo().getSelectors()).contains(LIST_VIEW_SELECTOR);
        SlingScriptHelper scriptHelper = getSlingScriptHelper();
        linkRewriter = scriptHelper.getService(LinkRewriterService.class);
        getPage(resource);
    }

    /**
     * Get the page post properties from the resource.
     *
     * @param page The page post resource.
     */
    private void getPage(Resource page) {
        if (page != null) {
            ValueMap properties = page.adaptTo(ValueMap.class);
            url = properties.get("url", String.class);
            visible = Boolean.valueOf(properties.get("visible", false));
            keywords = properties.get("keywords", String[].class);
            links = properties.get("links", String[].class);
            scripts = properties.get("scripts", String[].class);
            content = properties.get("content", String.class);
            description = properties.get("description", String.class);
            image = properties.get("image", String.class);
            configurationName = properties.get("configurationName", String.class);


            if (image != null) {
                image = resolver.map(image);
            }

            imageRelativePath = image;
            imageAbsolutePath = getAbsolutePath(image);
        }
    }

    /**
     * Get the page scripts set by the author.
     *
     * @return return the page scripts.
     */
    public String[] getConfScripts() {
        ResourceResolver resolver = resource.getResourceResolver();
        Resource pageconf = resolver.getResource(PublickConstants.PAGE_PATH_CONF + "/" + configurationName);
        ValueMap properties = pageconf.adaptTo(ValueMap.class);
        String[] scripts = properties.get("scripts", String[].class);

        return scripts;
    }

    /**
     * Get the page links set by the author.
     *
     * @return return the page links.
     */
    public String[] getConfLinks() {
        ResourceResolver resolver = resource.getResourceResolver();
        Resource pageconf = resolver.getResource(PublickConstants.PAGE_PATH_CONF + "/" + configurationName);
        ValueMap properties = pageconf.adaptTo(ValueMap.class);
        String[] links = properties.get("links", String[].class);

        return links;
    }

    /**
     * Get the page footer set by the author.
     *
     * @return return the page footer.
     */
    public String getFooter() {
        ResourceResolver resolver = resource.getResourceResolver();
        Resource pageconf = resolver.getResource(PublickConstants.PAGE_PATH_CONF + "/" + configurationName);
        ValueMap properties = pageconf.adaptTo(ValueMap.class);
        String footer = properties.get("footer", String.class);

        return footer;
    }

    /**
     * Get the page header set by the author.
     *
     * @return return the page header.
     */
    public String getHeader() {
        ResourceResolver resolver = resource.getResourceResolver();
        Resource pageconf = resolver.getResource(PublickConstants.PAGE_PATH_CONF + "/" + configurationName);
        ValueMap properties = pageconf.adaptTo(ValueMap.class);
        String header = properties.get("header", String.class);

        return header;
    }

    /**
     * Get the page configuraiton name set by the author.
     *
     * @return return the page configuration name.
     */
    public String getConfigurationName() {
        return configurationName;
    }

    /**
     * Get the friendly URL set by the author.
     *
     * This is the node name.
     *
     * @return return the page node name.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the page visibility set by the author.
     *
     * @return The page visibility.
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * Get the page keywords/tags set by the author.
     *
     * @return The page keywords/tags.
     */
    public String[] getKeywords() {
        return keywords;
    }

    /**
     * Get the page links set by the author.
     *
     * @return The page linkss.
     */
    public String[] getLinks() {
        return links;
    }

    /**
     * Get the page scripts set by the author.
     *
     * @return The page scripts.
     */
    public String[] getScripts() {
        return scripts;
    }

    /**
     * Get the page main content written by the author.
     *
     * @return The page main content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the page description written by the author.
     *
     * @return The page description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get whether the page is being requested in list view.
     *
     * @return Whether the page is being requested in list view.
     */
    public boolean getListView() {
        return listView;
    }

    /**
     * Get the page image's relative path with "/content" removed.
     *
     * @return The page image's relative path.
     */
    public String getImageRelativePath() {
        return imageRelativePath;
    }

    /**
     * Get the page image's absolute path with "/content" removed.
     *
     * @return The page image's absolute path.
     */
    public String getImageAbsolutePath() {
        return imageAbsolutePath;
    }
}