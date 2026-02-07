/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */

package org.apache.roller.weblogger.pojos;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.business.WebloggerFactory;
import org.apache.roller.weblogger.business.plugins.entry.WeblogEntryPlugin;
import org.apache.roller.weblogger.util.Utilities;

/**
 * Manages configuration settings for a Weblog.
 * Handles editor settings, display preferences, API configuration,
 * multi-language settings, analytics, and plugin management.
 *
 * This class is part of a refactoring to address insufficient modularization
 * in the Weblog class by extracting configuration-related functionality.
 */
public class WeblogConfiguration implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(WeblogConfiguration.class);
    
    private final Weblog weblog;
    
    // Editor configuration
    private String editorPage = null;
    private String editorTheme = null;
    
    // Display settings
    private int entryDisplayCount = 15;
    private Boolean visible = Boolean.TRUE;
    private Boolean active = Boolean.TRUE;
    
    // API settings
    private Boolean enableBloggerApi = Boolean.TRUE;
    private WeblogCategory bloggerCategory = null;
    
    // Content moderation
    private String bannedwordslist = null;
    
    // Multi-language settings
    private boolean enableMultiLang = false;
    private boolean showAllLangs = true;
    
    // Analytics and tracking
    private String analyticsCode = null;
    private String iconPath = null;
    private String about = null;
    
    // Plugin configuration
    private String defaultPlugins = null;
    private Map<String, WeblogEntryPlugin> initializedPlugins = null;
    
    /**
     * Constructor - package private, only accessible through Weblog class
     */
    WeblogConfiguration(Weblog weblog) {
        this.weblog = weblog;
    }
    
    // Editor configuration methods
    
    public String getEditorPage() {
        return this.editorPage;
    }
    
    public void setEditorPage(String editorPage) {
        this.editorPage = editorPage;
    }
    
    public String getEditorTheme() {
        return this.editorTheme;
    }
    
    public void setEditorTheme(String editorTheme) {
        this.editorTheme = editorTheme;
    }
    
    // Display settings methods
    
    public int getEntryDisplayCount() {
        return entryDisplayCount;
    }
    
    public void setEntryDisplayCount(int entryDisplayCount) {
        this.entryDisplayCount = entryDisplayCount;
    }
    
    /**
     * Set to FALSE to disable and hide this weblog from public view.
     */
    public Boolean getVisible() {
        return this.visible;
    }
    
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Set to FALSE to exclude this weblog from community areas such as the
     * front page and the planet page.
     */
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    // API settings methods
    
    public Boolean getEnableBloggerApi() {
        return this.enableBloggerApi;
    }
    
    public void setEnableBloggerApi(Boolean enableBloggerApi) {
        this.enableBloggerApi = enableBloggerApi;
    }
    
    public WeblogCategory getBloggerCategory() {
        return bloggerCategory;
    }
    
    public void setBloggerCategory(WeblogCategory bloggerCategory) {
        this.bloggerCategory = bloggerCategory;
    }
    
    // Content moderation methods
    
    public String getBannedwordslist() {
        return this.bannedwordslist;
    }
    
    public void setBannedwordslist(String bannedwordslist) {
        this.bannedwordslist = bannedwordslist;
    }
    
    // Multi-language settings methods
    
    /**
     * Is multi-language blog support enabled for this weblog?
     *
     * If false then urls with various locale restrictions should fail.
     */
    public boolean isEnableMultiLang() {
        return enableMultiLang;
    }
    
    public void setEnableMultiLang(boolean enableMultiLang) {
        this.enableMultiLang = enableMultiLang;
    }
    
    /**
     * Should the default weblog view show entries from all languages?
     *
     * If false then the default weblog view only shows entry from the
     * default locale chosen for this weblog.
     */
    public boolean isShowAllLangs() {
        return showAllLangs;
    }
    
    public void setShowAllLangs(boolean showAllLangs) {
        this.showAllLangs = showAllLangs;
    }
    
    // Analytics and tracking methods
    
    /**
     * The path under the weblog's resources to an icon image.
     */
    public String getIconPath() {
        return iconPath;
    }
    
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
    
    public String getAnalyticsCode() {
        return analyticsCode;
    }
    
    public void setAnalyticsCode(String analyticsCode) {
        this.analyticsCode = analyticsCode;
    }
    
    /**
     * A description for the weblog (its purpose, authors, etc.)
     *
     * This field is meant to hold a paragraph describing the weblog, in contrast
     * to the short sentence or two 'description' attribute meant for blog taglines
     * and HTML header META description tags.
     */
    public String getAbout() {
        return about;
    }
    
    public void setAbout(String about) {
        this.about = Utilities.removeHTML(about);
    }
    
    // Plugin configuration methods
    
    /**
     * Comma-delimited list of user's default Plugins.
     */
    public String getDefaultPlugins() {
        return defaultPlugins;
    }
    
    public void setDefaultPlugins(String string) {
        defaultPlugins = string;
    }
    
    /**
     * Get initialized plugins for use during rendering process.
     * @deprecated Use {@link org.apache.roller.weblogger.business.services.WeblogPluginService#getInitializedPlugins(Weblog)} instead
     */
    @Deprecated
    public Map<String, WeblogEntryPlugin> getInitializedPlugins() {
        if (initializedPlugins == null) {
            initializedPlugins = WebloggerFactory.getWeblogger()
                .getWeblogPluginService()
                .getInitializedPlugins(weblog);
        }
        return initializedPlugins;
    }
    
    /**
     * Copy configuration data from another WeblogConfiguration instance
     */
    void copyFrom(WeblogConfiguration other) {
        this.editorPage = other.editorPage;
        this.editorTheme = other.editorTheme;
        this.entryDisplayCount = other.entryDisplayCount;
        this.visible = other.visible;
        this.active = other.active;
        this.enableBloggerApi = other.enableBloggerApi;
        this.bloggerCategory = other.bloggerCategory;
        this.bannedwordslist = other.bannedwordslist;
        this.enableMultiLang = other.enableMultiLang;
        this.showAllLangs = other.showAllLangs;
        this.analyticsCode = other.analyticsCode;
        this.iconPath = other.iconPath;
        this.about = other.about;
        this.defaultPlugins = other.defaultPlugins;
    }
}
