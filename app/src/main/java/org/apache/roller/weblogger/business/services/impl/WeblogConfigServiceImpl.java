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

package org.apache.roller.weblogger.business.services.impl;

import org.apache.roller.weblogger.business.services.WeblogConfigService;
import org.apache.roller.weblogger.pojos.Weblog;

/**
 * Implementation of WeblogConfigService.
 */
public class WeblogConfigServiceImpl implements WeblogConfigService {
    
    @Override
    public String getLocale(Weblog weblog) {
        return weblog != null ? weblog.getLocale() : null;
    }
    
    @Override
    public void setLocale(Weblog weblog, String locale) {
        if (weblog != null) {
            weblog.setLocale(locale);
        }
    }
    
    @Override
    public String getTimezone(Weblog weblog) {
        return weblog != null ? weblog.getTimeZone() : null;
    }
    
    @Override
    public void setTimezone(Weblog weblog, String timezone) {
        if (weblog != null) {
            weblog.setTimeZone(timezone);
        }
    }
    
    @Override
    public Boolean isActive(Weblog weblog) {
        return weblog != null ? weblog.getActive() : null;
    }
    
    @Override
    public void setActive(Weblog weblog, Boolean active) {
        if (weblog != null) {
            weblog.setActive(active);
        }
    }
    
    @Override
    public Boolean isVisible(Weblog weblog) {
        return weblog != null ? weblog.getVisible() : null;
    }
    
    @Override
    public void setVisible(Weblog weblog, Boolean visible) {
        if (weblog != null) {
            weblog.setVisible(visible);
        }
    }
    
    @Override
    public int getEntryDisplayCount(Weblog weblog) {
        return weblog != null ? weblog.getEntryDisplayCount() : 0;
    }
    
    @Override
    public void setEntryDisplayCount(Weblog weblog, int count) {
        if (weblog != null) {
            weblog.setEntryDisplayCount(count);
        }
    }
    
    @Override
    public Boolean getAllowComments(Weblog weblog) {
        return weblog != null ? weblog.getAllowComments() : null;
    }
    
    @Override
    public void setAllowComments(Weblog weblog, Boolean allow) {
        if (weblog != null) {
            weblog.setAllowComments(allow);
        }
    }
    
    @Override
    public Boolean getEmailComments(Weblog weblog) {
        return weblog != null ? weblog.getEmailComments() : null;
    }
    
    @Override
    public void setEmailComments(Weblog weblog, Boolean email) {
        if (weblog != null) {
            weblog.setEmailComments(email);
        }
    }
    
    @Override
    public Boolean getModerateComments(Weblog weblog) {
        return weblog != null ? weblog.getModerateComments() : null;
    }
    
    @Override
    public void setModerateComments(Weblog weblog, Boolean moderate) {
        if (weblog != null) {
            weblog.setModerateComments(moderate);
        }
    }
    
    @Override
    public boolean isEnableMultiLang(Weblog weblog) {
        return weblog != null && weblog.isEnableMultiLang();
    }
    
    @Override
    public void setEnableMultiLang(Weblog weblog, boolean enable) {
        if (weblog != null) {
            weblog.setEnableMultiLang(enable);
        }
    }
    
    @Override
    public boolean isShowAllLangs(Weblog weblog) {
        return weblog != null && weblog.isShowAllLangs();
    }
    
    @Override
    public void setShowAllLangs(Weblog weblog, boolean show) {
        if (weblog != null) {
            weblog.setShowAllLangs(show);
        }
    }
    
    @Override
    public String getEditorTheme(Weblog weblog) {
        return weblog != null ? weblog.getEditorTheme() : null;
    }
    
    @Override
    public void setEditorTheme(Weblog weblog, String theme) {
        if (weblog != null) {
            weblog.setEditorTheme(theme);
        }
    }
    
    @Override
    public Boolean getEnableBloggerApi(Weblog weblog) {
        return weblog != null ? weblog.getEnableBloggerApi() : null;
    }
    
    @Override
    public void setEnableBloggerApi(Weblog weblog, Boolean enable) {
        if (weblog != null) {
            weblog.setEnableBloggerApi(enable);
        }
    }
}
