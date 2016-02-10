package com.infielddigital.sling.publick.components.foundation;

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
public class IdBannerView extends WCMUse {

    /**
     * Logger instance to log and debug errors.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IdBannerView.class);

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
    private String image_url;
    private Boolean is_text;
    private String title;
    private String text;
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
            image_url = properties.get("banner-background-image-url", String.class);
            is_text = Boolean.valueOf(properties.get("banner-is-text", false));
            title = properties.get("banner-title", String.class);
            text = properties.get("banner-text", String.class);
        }
    }


    /**
     * Get the banners banner-background-image-url.
     *
     * @return The banner's banner-background-image-url.
     */
    public String getBannerImageBackground() {
        return image_url;
    }

    /**
     * Get the banners banner-is-text, weather or not to dis[lay a banner message (title and text).
     *
     * @return The banner's banner-is-text.
     */
    public Boolean getIsText() {
        return is_text;
    }

    /**
     * Get the banner banner-link-logo.
     *
     * @return The banner's banner-link-logo.
     */
    public String getBannerTitle() {
        return title;
    }

    /**
     * Get the banner banner-link-text.
     *
     * @return The banner's banner-link-text.
     */
    public String getBannerText() {
        return text;
    }

    /**
     * Get whether the page is being requested in list view.
     *
     * @return Whether the page is being requested in list view.
     */
    public boolean getListView() {
        return listView;
    }
}