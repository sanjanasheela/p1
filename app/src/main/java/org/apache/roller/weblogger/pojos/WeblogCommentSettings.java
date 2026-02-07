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
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.WebloggerException;
import org.apache.roller.weblogger.business.WeblogEntryManager;
import org.apache.roller.weblogger.business.WebloggerFactory;
import org.apache.roller.weblogger.config.WebloggerRuntimeConfig;

/**
 * Manages comment-related settings and functionality for a Weblog.
 * Handles comment permissions, moderation, notifications, and retrieval.
 *
 * This class is part of a refactoring to address insufficient modularization
 * in the Weblog class by extracting comment-related functionality.
 */
public class WeblogCommentSettings implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(WeblogCommentSettings.class);
    private static final int MAX_ENTRIES = 100;
    
    private final Weblog weblog;
    
    // Comment settings
    private Boolean allowComments = Boolean.TRUE;
    private Boolean defaultAllowComments = Boolean.TRUE;
    private int defaultCommentDays = 0;
    private Boolean moderateComments = Boolean.FALSE;
    private Boolean emailComments = Boolean.FALSE;
    
    /**
     * Constructor - package private, only accessible through Weblog class
     */
    WeblogCommentSettings(Weblog weblog) {
        this.weblog = weblog;
    }
    
    // Comment permission methods
    
    public Boolean getAllowComments() {
        return this.allowComments;
    }
    
    public void setAllowComments(Boolean allowComments) {
        this.allowComments = allowComments;
    }
    
    public Boolean getDefaultAllowComments() {
        return defaultAllowComments;
    }
    
    public void setDefaultAllowComments(Boolean defaultAllowComments) {
        this.defaultAllowComments = defaultAllowComments;
    }
    
    public int getDefaultCommentDays() {
        return defaultCommentDays;
    }
    
    public void setDefaultCommentDays(int defaultCommentDays) {
        this.defaultCommentDays = defaultCommentDays;
    }
    
    // Comment moderation methods
    
    public Boolean getModerateComments() {
        return moderateComments;
    }
    
    public void setModerateComments(Boolean moderateComments) {
        this.moderateComments = moderateComments;
    }
    
    /**
     * Returns true if comment moderation is required by website or config.
     */
    public boolean getCommentModerationRequired() {
        return (getModerateComments()
                || WebloggerRuntimeConfig.getBooleanProperty("users.moderation.required"));
    }
    
    /** No-op */
    public void setCommentModerationRequired(boolean modRequired) {
    }
    
    // Comment notification methods
    
    public Boolean getEmailComments() {
        return this.emailComments;
    }
    
    public void setEmailComments(Boolean emailComments) {
        this.emailComments = emailComments;
    }
    
    // Comment retrieval methods
    
    /**
     * Get up to 100 most recent approved and non-spam comments in weblog.
     * @param length Max entries to return (1-100)
     * @return List of comment objects.
     */
    public List<WeblogEntryComment> getRecentComments(int length) {
        if (length > MAX_ENTRIES) {
            length = MAX_ENTRIES;
        }
        if (length < 1) {
            return Collections.emptyList();
        }
        try {
            WeblogEntryManager wmgr = WebloggerFactory.getWeblogger().getWeblogEntryManager();
            CommentSearchCriteria csc = new CommentSearchCriteria();
            csc.setWeblog(weblog);
            csc.setStatus(WeblogEntryComment.ApprovalStatus.APPROVED);
            csc.setReverseChrono(true);
            csc.setMaxResults(length);
            return wmgr.getComments(csc);
        } catch (WebloggerException e) {
            log.error("ERROR: getting recent comments", e);
        }
        return Collections.emptyList();
    }
    
    /**
     * Get the total count of comments for this weblog.
     * @return Total number of comments
     */
    public long getCommentCount() {
        long count = 0;
        try {
            WeblogEntryManager mgr = WebloggerFactory.getWeblogger().getWeblogEntryManager();
            count = mgr.getCommentCount(weblog);
        } catch (WebloggerException e) {
            log.error("Error getting comment count for weblog " + weblog.getName(), e);
        }
        return count;
    }
    
    /**
     * Copy comment settings from another WeblogCommentSettings instance
     */
    void copyFrom(WeblogCommentSettings other) {
        this.allowComments = other.allowComments;
        this.defaultAllowComments = other.defaultAllowComments;
        this.defaultCommentDays = other.defaultCommentDays;
        this.moderateComments = other.moderateComments;
        this.emailComments = other.emailComments;
    }
}
