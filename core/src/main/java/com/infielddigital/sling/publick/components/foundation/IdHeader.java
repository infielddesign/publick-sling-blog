package com.infielddigital.sling.publick.components.foundation;

import com.nateyolles.sling.publick.PublickConstants;
import com.nateyolles.sling.publick.services.LinkRewriterService;
import com.nateyolles.sling.publick.sightly.WCMUse;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.commons.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Sightly component to display a single page.
 */
public class IdHeader extends WCMUse {

    /**
     * Logger instance to log and debug errors.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IdHeader.class);

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
    private String logo;
    private String text;
    private String[] menu;
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
            logo = properties.get("header-link-logo", String.class);
            text = properties.get("header-link-text", String.class);
            menu = properties.get("menu", String[].class);
        }
    }


    /**
     * Get the headers header-link-logo.
     *
     * @return The header's header-link-logo.
     */
    public String getLogo() {
        return logo;
    }

    /**
     * Get the headers header-link-text.
     *
     * @return The header's header-link-text.
     */
    public String getText() {
        return text;
    }

    /**
     * Get the header's menu.
     *
     * @return The header's menu.
     */
    public JSONArray getMenu() {
        String[][] fakemenu = new String[20][3];
        fakemenu[0][0] = "";

        JSONArray jsonArray = null;

        if (fakemenu != null) {
            jsonArray = new JSONArray(Arrays.asList(fakemenu));
        } else {
            jsonArray = new JSONArray();
        }

        return jsonArray;
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