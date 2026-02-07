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
import org.apache.roller.weblogger.business.services.WeblogPluginService;
import org.apache.roller.weblogger.business.WebloggerFactory;
import org.apache.roller.weblogger.business.Weblogger;
import org.apache.roller.weblogger.business.plugins.PluginManager;
import org.apache.roller.weblogger.business.plugins.entry.WeblogEntryPlugin;
import org.apache.roller.weblogger.pojos.Weblog;

import java.util.Collections;
import java.util.Map;

/**
 * Implementation of WeblogPluginService.
 */
public class WeblogPluginServiceImpl implements WeblogPluginService {
    
    private static Log log = LogFactory.getLog(WeblogPluginServiceImpl.class);
    
    @Override
    public Map<String, WeblogEntryPlugin> getInitializedPlugins(Weblog weblog) {
        if (weblog == null) {
            return Collections.emptyMap();
        }
        
        try {
            Weblogger roller = WebloggerFactory.getWeblogger();
            PluginManager ppmgr = roller.getPluginManager();
            return ppmgr.getWeblogEntryPlugins(weblog);
        } catch (Exception e) {
            log.error("ERROR: initializing plugins for weblog " + weblog.getHandle(), e);
        }
        
        return Collections.emptyMap();
    }
    
    @Override
    public String getDefaultPlugins(Weblog weblog) {
        return weblog != null ? weblog.getDefaultPlugins() : null;
    }
    
    @Override
    public void setDefaultPlugins(Weblog weblog, String plugins) {
        if (weblog != null) {
            weblog.setDefaultPlugins(plugins);
        }
    }
}
