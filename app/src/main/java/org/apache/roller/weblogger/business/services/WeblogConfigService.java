/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
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
 * limitations under the License.
 */

package org.apache.roller.weblogger.business.services;

import org.apache.roller.weblogger.pojos.Weblog;

/**
 * Service for managing weblog configuration and settings.
 * 
 * Extracted from Weblog POJO to eliminate Hub-like Modularization smell.
 */
public interface WeblogConfigService {
    
    // Locale and timezone
    String getLocale(Weblog weblog);
    void setLocale(Weblog weblog, String locale);
    String getTimezone(Weblog weblog);
    void setTimezone(Weblog weblog, String timezone);
    
    // Visibility and status
    Boolean isActive(Weblog weblog);
    void setActive(Weblog weblog, Boolean active);
    Boolean isVisible(Weblog weblog);
    void setVisible(Weblog weblog, Boolean visible);
    
    // Display settings
    int getEntryDisplayCount(Weblog weblog);
    void setEntryDisplayCount(Weblog weblog, int count);
    
    // Comment settings
    Boolean getAllowComments(Weblog weblog);
    void setAllowComments(Weblog weblog, Boolean allow);
    Boolean getEmailComments(Weblog weblog);
    void setEmailComments(Weblog weblog, Boolean email);
    Boolean getModerateComments(Weblog weblog);
    void setModerateComments(Weblog weblog, Boolean moderate);
    
    // Multi-language settings
    boolean isEnableMultiLang(Weblog weblog);
    void setEnableMultiLang(Weblog weblog, boolean enable);
    boolean isShowAllLangs(Weblog weblog);
    void setShowAllLangs(Weblog weblog, boolean show);
    
    // Other settings
    String getEditorTheme(Weblog weblog);
    void setEditorTheme(Weblog weblog, String theme);
    Boolean getEnableBloggerApi(Weblog weblog);
    void setEnableBloggerApi(Weblog weblog, Boolean enable);
}
