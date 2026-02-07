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
import org.apache.roller.weblogger.pojos.WeblogTheme;
import org.apache.roller.weblogger.WebloggerException;

/**
 * Service for managing weblog themes.
 * 
 * Extracted from Weblog POJO to eliminate Hub-like Modularization smell.
 */
public interface WeblogThemeService {
    
    /**
     * Get the theme for a weblog.
     * 
     * @param weblog The weblog
     * @return Weblog theme or null if none set
     */
    WeblogTheme getTheme(Weblog weblog);
    
    /**
     * Get editor theme name for weblog.
     * 
     * @param weblog The weblog
     * @return Editor theme name
     */
    String getEditorTheme(Weblog weblog);
    
    /**
     * Set editor theme for weblog.
     * 
     * @param weblog The weblog
     * @param themeName Theme name
     */
    void setEditorTheme(Weblog weblog, String themeName);
}
