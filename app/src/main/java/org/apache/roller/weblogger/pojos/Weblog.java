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
import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.roller.weblogger.WebloggerException;
import org.apache.roller.weblogger.business.plugins.entry.WeblogEntryPlugin;
import org.apache.roller.weblogger.business.WebloggerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.config.WebloggerRuntimeConfig;
import org.apache.roller.weblogger.business.BookmarkManager;
import org.apache.roller.weblogger.business.plugins.PluginManager;
import org.apache.roller.weblogger.business.Weblogger;
import org.apache.roller.weblogger.business.themes.ThemeManager;
import org.apache.roller.weblogger.business.WeblogEntryManager;
import org.apache.roller.weblogger.pojos.WeblogEntry.PubStatus;
import org.apache.roller.util.UUIDGenerator;
import org.apache.roller.weblogger.business.UserManager;
import org.apache.roller.weblogger.util.I18nUtils;
import org.apache.roller.weblogger.util.Utilities;


/**
 * Website has many-to-many association with users. Website has one-to-many and
 * one-direction associations with weblog entries, weblog categories, folders and
 * other objects. Use UserManager to create, fetch, update and retrieve websites.
 *
 * @author David M Johnson
 */
public class Weblog implements Serializable {
    
    public static final long serialVersionUID = 206437645033737127L;
    
    private static Log log = LogFactory.getLog(Weblog.class);
    
    // Core identity properties
    private String  id               = UUIDGenerator.generateUUID();
    private String  handle           = null;
    private String  name             = null;
    private String  tagline          = null;
    private String  emailAddress     = null;
    private String  creator          = null;
    private Date    dateCreated      = new java.util.Date();
    private Date    lastModified     = new Date();

    // Component objects - encapsulate different concerns
    private WeblogConfiguration configuration;
    private WeblogCommentSettings commentSettings;
    private WeblogLocalization localization;
    private WeblogContentManager contentManager;
    private WeblogMetrics metrics;

    public Weblog() {
        initializeComponents();
    }
    
    public Weblog(
            String handle,
            String creator,
            String name,
            String desc,
            String email,
            String editorTheme,
            String locale,
            String timeZone) {
        
        initializeComponents();
        
        this.handle = handle;
        this.creator = creator;
        this.name = name;
        this.tagline = desc;
        this.emailAddress = email;
        this.configuration.setEditorTheme(editorTheme);
        this.localization.setLocale(locale);
        this.localization.setTimeZone(timeZone);
    }
    
    /**
     * Initialize component objects
     */
    private void initializeComponents() {
        this.configuration = new WeblogConfiguration(this);
        this.commentSettings = new WeblogCommentSettings(this);
        this.localization = new WeblogLocalization(this);
        this.contentManager = new WeblogContentManager(this);
        this.metrics = new WeblogMetrics(this);
    }
    
    //------------------------------------------------------- Good citizenship

    @Override
    public String toString() {
        return  "{" + getId() + ", " + getHandle()
        + ", " + getName() + ", " + getEmailAddress()
        + ", " + getLocale() + ", " + getTimeZone() + "}";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Weblog)) {
            return false;
        }
        Weblog o = (Weblog)other;
        return new EqualsBuilder()
            .append(getHandle(), o.getHandle()) 
            .isEquals();
    }
    
    @Override
    public int hashCode() { 
        return new HashCodeBuilder()
            .append(getHandle())
            .toHashCode();
    } 
    
    /**
     * Get the Theme object in use by this weblog, or null if no theme selected.
     * @deprecated Use {@link org.apache.roller.weblogger.business.services.WeblogThemeService#getTheme(Weblog)} instead
     */
    @Deprecated
    public WeblogTheme getTheme() {
        try {
            return WebloggerFactory.getWeblogger()
                .getWeblogThemeService()
                .getTheme(this);
        } catch (Exception ex) {
            log.error("Error getting theme for weblog - "+getHandle(), ex);
        }
        return null;
    }

    /**
     * Id of the Website.
     */
    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Short URL safe string that uniquely identifies the website.
     */
    public String getHandle() {
        return this.handle;
    }
    
    public void setHandle(String handle) {
        this.handle = handle;
    }
    
    /**
     * Name of the Website.
     */
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = Utilities.removeHTML(name);
    }
    
    /**
     * Description
     *
     */
    public String getTagline() {
        return this.tagline;
    }
    
    public void setTagline(String tagline) {
        this.tagline = Utilities.removeHTML(tagline);
    }
    
    /**
     * Original creator of website.
     */
    public org.apache.roller.weblogger.pojos.User getCreator() {
        try {
            return WebloggerFactory.getWeblogger().getUserManager().getUserByUserName(creator);
        } catch (Exception e) {
            log.error("ERROR fetching user object for username: " + creator, e);
        }
        return null;
    }
    
    /**
     * Username of original creator of website.
     */
    public String getCreatorUserName() {
        return creator;
    }
    
    public void setCreatorUserName(String creatorUserName) {
        creator = creatorUserName;
    }

    public Boolean getEnableBloggerApi() {
        return configuration.getEnableBloggerApi();
    }
    
    public void setEnableBloggerApi(Boolean enableBloggerApi) {
        configuration.setEnableBloggerApi(enableBloggerApi);
    }
    
    public WeblogCategory getBloggerCategory() {
        return configuration.getBloggerCategory();
    }
    
    public void setBloggerCategory(WeblogCategory bloggerCategory) {
        configuration.setBloggerCategory(bloggerCategory);
    }
    
    public String getEditorPage() {
        return configuration.getEditorPage();
    }
    
    public void setEditorPage(String editorPage) {
        configuration.setEditorPage(editorPage);
    }
    
    public String getBannedwordslist() {
        return configuration.getBannedwordslist();
    }
    
    public void setBannedwordslist(String bannedwordslist) {
        configuration.setBannedwordslist(bannedwordslist);
    }
    
    public Boolean getAllowComments() {
        return commentSettings.getAllowComments();
    }
    
    public void setAllowComments(Boolean allowComments) {
        commentSettings.setAllowComments(allowComments);
    }
    
    public Boolean getDefaultAllowComments() {
        return commentSettings.getDefaultAllowComments();
    }
    
    public void setDefaultAllowComments(Boolean defaultAllowComments) {
        commentSettings.setDefaultAllowComments(defaultAllowComments);
    }
    
    public int getDefaultCommentDays() {
        return commentSettings.getDefaultCommentDays();
    }
    
    public void setDefaultCommentDays(int defaultCommentDays) {
        commentSettings.setDefaultCommentDays(defaultCommentDays);
    }
    
    public Boolean getModerateComments() {
        return commentSettings.getModerateComments();
    }
    
    public void setModerateComments(Boolean moderateComments) {
        commentSettings.setModerateComments(moderateComments);
    }
    
    public Boolean getEmailComments() {
        return commentSettings.getEmailComments();
    }
    
    public void setEmailComments(Boolean emailComments) {
        commentSettings.setEmailComments(emailComments);
    }
    
    public String getEmailAddress() {
        return this.emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    /**
     * EditorTheme of the Website.
     */
    public String getEditorTheme() {
        return configuration.getEditorTheme();
    }
    
    public void setEditorTheme(String editorTheme) {
        configuration.setEditorTheme(editorTheme);
    }
    
    /**
     * Locale of the Website.
     */
    public String getLocale() {
        return localization.getLocale();
    }
    
    public void setLocale(String locale) {
        localization.setLocale(locale);
    }
    
    /**
     * Timezone of the Website.
     */
    public String getTimeZone() {
        return localization.getTimeZone();
    }
    
    public void setTimeZone(String timeZone) {
        localization.setTimeZone(timeZone);
    }
    
    public Date getDateCreated() {
        if (dateCreated == null) {
            return null;
        } else {
            return (Date)dateCreated.clone();
        }
    }

    public void setDateCreated(final Date date) {
        if (date != null) {
            dateCreated = (Date)date.clone();
        } else {
            dateCreated = null;
        }
    }

    /**
     * Comma-delimited list of user's default Plugins.
     */
    public String getDefaultPlugins() {
        return configuration.getDefaultPlugins();
    }

    public void setDefaultPlugins(String string) {
        configuration.setDefaultPlugins(string);
    }

    /**
     * Set bean properties based on other bean.
     */
    public void setData(Weblog other) {
        this.setId(other.getId());
        this.setName(other.getName());
        this.setHandle(other.getHandle());
        this.setTagline(other.getTagline());
        this.setCreatorUserName(other.getCreatorUserName());
        this.setEmailAddress(other.getEmailAddress());
        this.setDateCreated(other.getDateCreated());
        this.setLastModified(other.getLastModified());
        
        // Delegate to components
        this.configuration.copyFrom(other.configuration);
        this.commentSettings.copyFrom(other.commentSettings);
        this.localization.copyFrom(other.localization);
        this.contentManager.copyFrom(other.contentManager);
    }
    
    
    /**
     * Parse locale value and instantiate a Locale object,
     * otherwise return default Locale.
     *
     * @return Locale
     */
    public Locale getLocaleInstance() {
        return localization.getLocaleInstance();
    }
    
    
    /**
     * Return TimeZone instance for value of timeZone,
     * otherwise return system default instance.
     * @return TimeZone
     */
    public TimeZone getTimeZoneInstance() {
        return localization.getTimeZoneInstance();
    }
    
    /**
     * Returns true if user has all permission action specified.
     * @deprecated Use {@link org.apache.roller.weblogger.business.services.WeblogPermissionService#hasUserPermission(Weblog, User, String)} instead
     */
    @Deprecated
    public boolean hasUserPermission(User user, String action) {
        return WebloggerFactory.getWeblogger()
            .getWeblogPermissionService()
            .hasUserPermission(this, user, action);
    }
    
    
    /**
     * Returns true if user has all permissions actions specified in the weblog.
     * @deprecated Use {@link org.apache.roller.weblogger.business.services.WeblogPermissionService#hasUserPermissions(Weblog, User, List)} instead
     */
    @Deprecated
    public boolean hasUserPermissions(User user, List<String> actions) {
        return WebloggerFactory.getWeblogger()
            .getWeblogPermissionService()
            .hasUserPermissions(this, user, actions);
    }
    
    public int getEntryDisplayCount() {
        return configuration.getEntryDisplayCount();
    }
    
    public void setEntryDisplayCount(int entryDisplayCount) {
        configuration.setEntryDisplayCount(entryDisplayCount);
    }
    
    /**
     * Set to FALSE to disable and hide this weblog from public view.
     */
    public Boolean getVisible() {
        return configuration.getVisible();
    }
    
    public void setVisible(Boolean visible) {
        configuration.setVisible(visible);
    }
    
    /**
     * Set to FALSE to exclude this weblog from community areas such as the
     * front page and the planet page.
     */
    public Boolean getActive() {
        return configuration.getActive();
    }
    
    public void setActive(Boolean active) {
        configuration.setActive(active);
    }
    
    /**
     * Returns true if comment moderation is required by website or config.
     */ 
    public boolean getCommentModerationRequired() {
        return commentSettings.getCommentModerationRequired();
    }
    
    /** No-op */
    public void setCommentModerationRequired(boolean modRequired) {
        commentSettings.setCommentModerationRequired(modRequired);
    }    

    
    /**
     * The last time any visible part of this weblog was modified.
     * This includes a change to weblog settings, entries, themes, templates, 
     * comments, categories, bookmarks, folders, etc.
     *
     * Pings are explicitly not included because pings do not
     * affect visible changes to a weblog.
     *
     */
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
  
    
    /**
     * Is multi-language blog support enabled for this weblog?
     *
     * If false then urls with various locale restrictions should fail.
     */
    public boolean isEnableMultiLang() {
        return configuration.isEnableMultiLang();
    }

    public void setEnableMultiLang(boolean enableMultiLang) {
        configuration.setEnableMultiLang(enableMultiLang);
    }
    
    
    /**
     * Should the default weblog view show entries from all languages?
     *
     * If false then the default weblog view only shows entry from the
     * default locale chosen for this weblog.
     */
    public boolean isShowAllLangs() {
        return configuration.isShowAllLangs();
    }

    public void setShowAllLangs(boolean showAllLangs) {
        configuration.setShowAllLangs(showAllLangs);
    }
    
    public String getURL() {
        return WebloggerFactory.getWeblogger().getUrlStrategy().getWeblogURL(this, null, false);
    }

    public String getAbsoluteURL() {
        return WebloggerFactory.getWeblogger().getUrlStrategy().getWeblogURL(this, null, true);
    }

    /**
     * The path under the weblog's resources to an icon image.
     */
    public String getIconPath() {
        return configuration.getIconPath();
    }

    public void setIconPath(String iconPath) {
        configuration.setIconPath(iconPath);
    }

    public String getAnalyticsCode() {
        return configuration.getAnalyticsCode();
    }

    public void setAnalyticsCode(String analyticsCode) {
        configuration.setAnalyticsCode(analyticsCode);
    }

    /**
     * A description for the weblog (its purpose, authors, etc.)
     *
     * This field is meant to hold a paragraph describing the weblog, in contrast
     * to the short sentence or two 'description' attribute meant for blog taglines
     * and HTML header META description tags.
     *
     */
    public String getAbout() {
        return configuration.getAbout();
    }

    public void setAbout(String about) {
        configuration.setAbout(about);
    }
    
    
    /**
     * Get initialized plugins for use during rendering process.
     * @deprecated Use {@link org.apache.roller.weblogger.business.services.WeblogPluginService#getInitializedPlugins(Weblog)} instead
     */
    @Deprecated
    public Map<String, WeblogEntryPlugin> getInitializedPlugins() {
        return configuration.getInitializedPlugins();
    }
    
    /** 
     * Get weblog entry specified by anchor or null if no such entry exists.
     * @param anchor Weblog entry anchor
     * @return Weblog entry specified by anchor
     */
    public WeblogEntry getWeblogEntry(String anchor) {
        return contentManager.getWeblogEntry(anchor);
    }

    /**
     * Get weblog category by name.
     * @param categoryName The name of the category
     * @return The category or null if not found
     * @deprecated Use {@link org.apache.roller.weblogger.business.services.WeblogCategoryService#getCategoryByName(Weblog, String)} instead
     */
    @Deprecated
    public WeblogCategory getWeblogCategory(String categoryName) {
        return contentManager.getWeblogCategory(categoryName);
    }

    
    /**
     * Get up to 100 most recent published entries in weblog.
     * @param cat Category name or null for no category restriction
     * @param length Max entries to return (1-100)
     * @return List of weblog entry objects.
     */
    public List<WeblogEntry> getRecentWeblogEntries(String cat, int length) {
        return contentManager.getRecentWeblogEntries(cat, length);
    }
    
    /**
     * Get up to 100 most recent published entries in weblog.
     * @param tag Blog entry tag to query by
     * @param length Max entries to return (1-100)
     * @return List of weblog entry objects.
     */
    public List<WeblogEntry> getRecentWeblogEntriesByTag(String tag, int length) {
        return contentManager.getRecentWeblogEntriesByTag(tag, length);
    }   
    
    /**
     * Get up to 100 most recent approved and non-spam comments in weblog.
     * @param length Max entries to return (1-100)
     * @return List of comment objects.
     */
    public List<WeblogEntryComment> getRecentComments(int length) {
        return commentSettings.getRecentComments(length);
    }

    
    /**
     * Get bookmark folder by name.
     * @param folderName Name or path of bookmark folder to be returned (null for root)
     * @return Folder object requested.
     * @deprecated This method delegates to BookmarkManager - consider using BookmarkManager directly
     */
    @Deprecated
    public WeblogBookmarkFolder getBookmarkFolder(String folderName) {
        return contentManager.getBookmarkFolder(folderName);
    }


    /**
     * Get number of hits counted today.
     */
    public int getTodaysHits() {
        return metrics.getTodaysHits();
    }

    /**
     * Get a list of TagStats objects for the most popular tags
     *
     * @param sinceDays Number of days into past (or -1 for all days)
     * @param length    Max number of tags to return.
     * @return          Collection of WeblogEntryTag objects
     */
    public List<TagStat> getPopularTags(int sinceDays, int length) {
        return contentManager.getPopularTags(sinceDays, length);
    }      

    public long getCommentCount() {
        return commentSettings.getCommentCount();
    }
    
    public long getEntryCount() {
        return contentManager.getEntryCount();
    }


    /**
     * Add a category as a child of this category.
     */
    public void addCategory(WeblogCategory category) {
        contentManager.addCategory(category);
    }

    public List<WeblogCategory> getWeblogCategories() {
        return contentManager.getWeblogCategories();
    }

    public void setWeblogCategories(List<WeblogCategory> cats) {
        contentManager.setWeblogCategories(cats);
    }

    /**
     * Check if weblog has a category with the given name.
     * @deprecated Use {@link org.apache.roller.weblogger.business.services.WeblogCategoryService#hasCategory(Weblog, String)} instead
     */
    @Deprecated
    public boolean hasCategory(String name) {
        return contentManager.hasCategory(name);
    }

    public List<WeblogBookmarkFolder> getBookmarkFolders() {
        return contentManager.getBookmarkFolders();
    }

    public void setBookmarkFolders(List<WeblogBookmarkFolder> bookmarkFolders) {
        contentManager.setBookmarkFolders(bookmarkFolders);
    }

    public List<MediaFileDirectory> getMediaFileDirectories() {
        return contentManager.getMediaFileDirectories();
    }

    public void setMediaFileDirectories(List<MediaFileDirectory> mediaFileDirectories) {
        contentManager.setMediaFileDirectories(mediaFileDirectories);
    }

    /**
     * Add a bookmark folder to this weblog.
     */
    public void addBookmarkFolder(WeblogBookmarkFolder folder) {
        contentManager.addBookmarkFolder(folder);
    }

    /**
     * Does this Weblog have a bookmark folder with the specified name?
     *
     * @param name The name of the folder to check for.
     * @return boolean true if exists, false otherwise.
     * @deprecated Use {@link org.apache.roller.weblogger.business.services.WeblogBookmarkService#hasBookmarkFolder(Weblog, String)} instead
     */
    @Deprecated
    public boolean hasBookmarkFolder(String name) {
        return contentManager.hasBookmarkFolder(name);
    }

    /**
     * Indicates whether this weblog contains the specified media file directory
     *
     * @param name directory name
     *
     * @return true if directory is present, false otherwise.
     * @deprecated Use {@link org.apache.roller.weblogger.business.services.WeblogMediaService#hasMediaFileDirectory(Weblog, String)} instead
     */
    @Deprecated
    public boolean hasMediaFileDirectory(String name) {
        return contentManager.hasMediaFileDirectory(name);
    }

    /**
     * Get media file directory by name.
     * @deprecated Use {@link org.apache.roller.weblogger.business.services.WeblogMediaService#getMediaFileDirectory(Weblog, String)} instead
     */
    @Deprecated
    public MediaFileDirectory getMediaFileDirectory(String name) {
        return contentManager.getMediaFileDirectory(name);
    }

}
