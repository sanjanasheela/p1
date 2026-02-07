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

import org.apache.roller.weblogger.business.plugins.entry.WeblogEntryPlugin;
import org.apache.roller.weblogger.pojos.Weblog;
import org.apache.roller.weblogger.WebloggerException;
import java.util.Map;

/**
 * Service for managing weblog plugins.
 * 
 * Extracted from Weblog POJO to eliminate Hub-like Modularization smell.
 */
public interface WeblogPluginService {
    
    /**
     * Get initialized plugins for weblog.
     * 
     * @param weblog The weblog
     * @return Map of plugin names to plugin instances
     */
    Map<String, WeblogEntryPlugin> getInitializedPlugins(Weblog weblog);
    
    /**
     * Get default plugins configuration for weblog.
     * 
     * @param weblog The weblog
     * @return Default plugins string
     */
    String getDefaultPlugins(Weblog weblog);
    
    /**
     * Set default plugins for weblog.
     * 
     * @param weblog The weblog
     * @param plugins Comma-separated list of plugins
     */
    void setDefaultPlugins(Weblog weblog, String plugins);
}
