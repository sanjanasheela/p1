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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.business.services.WeblogThemeService;
import org.apache.roller.weblogger.business.WebloggerFactory;
import org.apache.roller.weblogger.business.themes.ThemeManager;
import org.apache.roller.weblogger.pojos.Weblog;
import org.apache.roller.weblogger.pojos.WeblogTheme;
import org.apache.roller.weblogger.WebloggerException;

/**
 * Implementation of WeblogThemeService.
 */
public class WeblogThemeServiceImpl implements WeblogThemeService {
    
    private static Log log = LogFactory.getLog(WeblogThemeServiceImpl.class);
    
    @Override
    public WeblogTheme getTheme(Weblog weblog) {
        if (weblog == null) {
            return null;
        }
        
        try {
            ThemeManager themeMgr = WebloggerFactory.getWeblogger().getThemeManager();
            return themeMgr.getTheme(weblog);
        } catch (WebloggerException ex) {
            log.error("Error getting theme for weblog - " + weblog.getHandle(), ex);
        }
        
        return null;
    }
    
    @Override
    public String getEditorTheme(Weblog weblog) {
        return weblog != null ? weblog.getEditorTheme() : null;
    }
    
    @Override
    public void setEditorTheme(Weblog weblog, String themeName) {
        if (weblog != null) {
            weblog.setEditorTheme(themeName);
        }
    }
}
