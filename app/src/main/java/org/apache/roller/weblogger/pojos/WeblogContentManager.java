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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.WebloggerException;
import org.apache.roller.weblogger.business.BookmarkManager;
import org.apache.roller.weblogger.business.WeblogEntryManager;
import org.apache.roller.weblogger.business.Weblogger;
import org.apache.roller.weblogger.business.WebloggerFactory;
import org.apache.roller.weblogger.pojos.WeblogEntry.PubStatus;

/**
 * Manages content access for a Weblog.
 * Handles entries, categories, tags, bookmarks, and media file directories.
 *
 * This class is part of a refactoring to address insufficient modularization
 * in the Weblog class by extracting content management functionality.
 */
public class WeblogContentManager implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(WeblogContentManager.class);
    private static final int MAX_ENTRIES = 100;
    
    private final Weblog weblog;
    
    // Collections
    private List<WeblogCategory> weblogCategories = new ArrayList<>();
    private List<WeblogBookmarkFolder> bookmarkFolders = new ArrayList<>();
    private List<MediaFileDirectory> mediaFileDirectories = new ArrayList<>();
    
    /**
     * Constructor - package private, only accessible through Weblog class
     */
    WeblogContentManager(Weblog weblog) {
        this.weblog = weblog;
    }
    
    // Entry access methods
    
    /**
     * Get weblog entry specified by anchor or null if no such entry exists.
     * @param anchor Weblog entry anchor
     * @return Weblog entry specified by anchor
     */
    public WeblogEntry getWeblogEntry(String anchor) {
        WeblogEntry entry = null;
        try {
            Weblogger roller = WebloggerFactory.getWeblogger();
            WeblogEntryManager wmgr = roller.getWeblogEntryManager();
            entry = wmgr.getWeblogEntryByAnchor(weblog, anchor);
        } catch (WebloggerException e) {
            log.error("ERROR: getting entry by anchor");
        }
        return entry;
    }
    
    /**
     * Get up to 100 most recent published entries in weblog.
     * @param cat Category name or null for no category restriction
     * @param length Max entries to return (1-100)
     * @return List of weblog entry objects.
     */
    public List<WeblogEntry> getRecentWeblogEntries(String cat, int length) {
        if (cat != null && "nil".equals(cat)) {
            cat = null;
        }
        if (length > MAX_ENTRIES) {
            length = MAX_ENTRIES;
        }
        if (length < 1) {
            return Collections.emptyList();
        }
        try {
            WeblogEntryManager wmgr = WebloggerFactory.getWeblogger().getWeblogEntryManager();
            WeblogEntrySearchCriteria wesc = new WeblogEntrySearchCriteria();
            wesc.setWeblog(weblog);
            wesc.setCatName(cat);
            wesc.setStatus(PubStatus.PUBLISHED);
            wesc.setMaxResults(length);
            return wmgr.getWeblogEntries(wesc);
        } catch (WebloggerException e) {
            log.error("ERROR: getting recent entries", e);
        }
        return Collections.emptyList();
    }
    
    /**
     * Get up to 100 most recent published entries in weblog.
     * @param tag Blog entry tag to query by
     * @param length Max entries to return (1-100)
     * @return List of weblog entry objects.
     */
    public List<WeblogEntry> getRecentWeblogEntriesByTag(String tag, int length) {
        if (tag != null && "nil".equals(tag)) {
            tag = null;
        }
        if (length > MAX_ENTRIES) {
            length = MAX_ENTRIES;
        }
        if (length < 1) {
            return Collections.emptyList();
        }
        List<String> tags = Collections.emptyList();
        if (tag != null) {
            tags = List.of(tag);
        }
        try {
            WeblogEntryManager wmgr = WebloggerFactory.getWeblogger().getWeblogEntryManager();
            WeblogEntrySearchCriteria wesc = new WeblogEntrySearchCriteria();
            wesc.setWeblog(weblog);
            wesc.setTags(tags);
            wesc.setStatus(PubStatus.PUBLISHED);
            wesc.setMaxResults(length);
            return wmgr.getWeblogEntries(wesc);
        } catch (WebloggerException e) {
            log.error("ERROR: getting recent entries", e);
        }
        return Collections.emptyList();
    }
    
    /**
     * Get the total count of entries for this weblog.
     * @return Total number of entries
     */
    public long getEntryCount() {
        long count = 0;
        try {
            Weblogger roller = WebloggerFactory.getWeblogger();
            WeblogEntryManager mgr = roller.getWeblogEntryManager();
            count = mgr.getEntryCount(weblog);
        } catch (WebloggerException e) {
            log.error("Error getting entry count for weblog " + weblog.getName(), e);
        }
        return count;
    }
    
    /**
     * Get a list of TagStats objects for the most popular tags
     *
     * @param sinceDays Number of days into past (or -1 for all days)
     * @param length    Max number of tags to return.
     * @return          Collection of WeblogEntryTag objects
     */
    public List<TagStat> getPopularTags(int sinceDays, int length) {
        Date startDate = null;
        if (sinceDays > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, -1 * sinceDays);
            startDate = cal.getTime();
        }
        try {
            Weblogger roller = WebloggerFactory.getWeblogger();
            WeblogEntryManager wmgr = roller.getWeblogEntryManager();
            return wmgr.getPopularTags(weblog, startDate, 0, length);
        } catch (Exception e) {
            log.error("ERROR: fetching popular tags for weblog " + weblog.getName(), e);
        }
        return Collections.emptyList();
    }
    
    // Category access methods
    
    /**
     * Get weblog category by name.
     * @param categoryName The name of the category
     * @return The category or null if not found
     * @deprecated Use {@link org.apache.roller.weblogger.business.services.WeblogCategoryService#getCategoryByName(Weblog, String)} instead
     */
    @Deprecated
    public WeblogCategory getWeblogCategory(String categoryName) {
        return WebloggerFactory.getWeblogger()
                .getWeblogCategoryService()
                .getCategoryByName(weblog, categoryName);
    }
    
    /**
     * Add a category as a child of this category.
     */
    public void addCategory(WeblogCategory category) {
        // make sure category is not null
        if (category == null || category.getName() == null) {
            throw new IllegalArgumentException("Category cannot be null and must have a valid name");
        }
        
        // make sure we don't already have a category with that name
        if (hasCategory(category.getName())) {
            throw new IllegalArgumentException("Duplicate category name '" + category.getName() + "'");
        }
        
        // add it to our list of child categories
        getWeblogCategories().add(category);
    }
    
    public List<WeblogCategory> getWeblogCategories() {
        return weblogCategories;
    }
    
    public void setWeblogCategories(List<WeblogCategory> cats) {
        this.weblogCategories = cats;
    }
    
    /**
     * Check if weblog has a category with the given name.
     * @deprecated Use {@link org.apache.roller.weblogger.business.services.WeblogCategoryService#hasCategory(Weblog, String)} instead
     */
    @Deprecated
    public boolean hasCategory(String name) {
        return WebloggerFactory.getWeblogger()
                .getWeblogCategoryService()
                .hasCategory(weblog, name);
    }
    
    // Bookmark folder methods
    
    /**
     * Get bookmark folder by name.
     * @param folderName Name or path of bookmark folder to be returned (null for root)
     * @return Folder object requested.
     * @deprecated This method delegates to BookmarkManager - consider using BookmarkManager directly
     */
    @Deprecated
    public WeblogBookmarkFolder getBookmarkFolder(String folderName) {
        try {
            Weblogger roller = WebloggerFactory.getWeblogger();
            BookmarkManager bmgr = roller.getBookmarkManager();
            if (folderName == null || folderName.equals("nil") || folderName.trim().equals("/")) {
                return bmgr.getDefaultFolder(weblog);
            } else {
                return bmgr.getFolder(weblog, folderName);
            }
        } catch (WebloggerException re) {
            log.error("ERROR: fetching folder for weblog", re);
        }
        return null;
    }
    
    public List<WeblogBookmarkFolder> getBookmarkFolders() {
        return bookmarkFolders;
    }
    
    public void setBookmarkFolders(List<WeblogBookmarkFolder> bookmarkFolders) {
        this.bookmarkFolders = bookmarkFolders;
    }
    
    /**
     * Add a bookmark folder to this weblog.
     */
    public void addBookmarkFolder(WeblogBookmarkFolder folder) {
        // make sure folder is not null
        if (folder == null || folder.getName() == null) {
            throw new IllegalArgumentException("Folder cannot be null and must have a valid name");
        }
        
        // make sure we don't already have a folder with that name
        if (this.hasBookmarkFolder(folder.getName())) {
            throw new IllegalArgumentException("Duplicate folder name '" + folder.getName() + "'");
        }
        
        // add it to our list of child folder
        getBookmarkFolders().add(folder);
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
        return WebloggerFactory.getWeblogger()
                .getWeblogBookmarkService()
                .hasBookmarkFolder(weblog, name);
    }
    
    // Media file directory methods
    
    public List<MediaFileDirectory> getMediaFileDirectories() {
        return mediaFileDirectories;
    }
    
    public void setMediaFileDirectories(List<MediaFileDirectory> mediaFileDirectories) {
        this.mediaFileDirectories = mediaFileDirectories;
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
        return WebloggerFactory.getWeblogger()
                .getWeblogMediaService()
                .hasMediaFileDirectory(weblog, name);
    }
    
    /**
     * Get media file directory by name.
     * @deprecated Use {@link org.apache.roller.weblogger.business.services.WeblogMediaService#getMediaFileDirectory(Weblog, String)} instead
     */
    @Deprecated
    public MediaFileDirectory getMediaFileDirectory(String name) {
        return WebloggerFactory.getWeblogger()
                .getWeblogMediaService()
                .getMediaFileDirectory(weblog, name);
    }
    
    /**
     * Copy content settings from another WeblogContentManager instance
     */
    void copyFrom(WeblogContentManager other) {
        this.weblogCategories = other.weblogCategories;
        this.bookmarkFolders = other.bookmarkFolders;
        this.mediaFileDirectories = other.mediaFileDirectories;
    }
}
