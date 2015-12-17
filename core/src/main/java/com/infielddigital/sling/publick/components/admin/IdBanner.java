package com.infielddigital.sling.publick.components.admin;

import com.nateyolles.sling.publick.services.LinkRewriterService;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sightly component to display a single page.
 */
public class IdBanner extends IdHeader {

    /**
     * Logger instance to log and debug errors.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IdBanner.class);

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
    private String title;
    private String text;

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
        SlingScriptHelper scriptHelper = getSlingScriptHelper();
        linkRewriter = scriptHelper.getService(LinkRewriterService.class);

        String path = request.getParameter("post");
        String parent = request.getParameter("post2");
        String mode = request.getParameter("post3");

        if (StringUtils.isNotBlank(path)) {
            getPage(path, parent, mode);
        }
    }

    /**
     * Get the page post properties from the resource.
     *
     * @param page The page post resource.
     */
    private void getPage(String path, String parent, String Mode) {
        ResourceResolver resolver = resource.getResourceResolver();
        Resource page = resolver.getResource(path + "/" + parent);

        if(Mode.equals("new")) {
            page = null;
        }

        if (page != null) {
            ValueMap properties = page.adaptTo(ValueMap.class);
            title = properties.get("banner-title", String.class);
            text = properties.get("banner-text", String.class);
        }
    }


    /**
     * Get the banners banner-link-logo.
     *
     * @return The banner's banner-link-logo.
     */
    public String getBannerTitle() {
        return title;
    }

    /**
     * Get the banners banner-link-text.
     *
     * @return The banner's banner-link-text.
     */
    public String getBannerText() {
        return text;
    }

}