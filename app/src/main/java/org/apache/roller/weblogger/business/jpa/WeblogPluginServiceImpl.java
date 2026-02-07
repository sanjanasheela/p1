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

package org.apache.roller.weblogger.business.jpa;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.business.plugins.PluginManager;
import org.apache.roller.weblogger.business.plugins.entry.WeblogEntryPlugin;
import org.apache.roller.weblogger.business.WeblogPluginService;
import org.apache.roller.weblogger.business.Weblogger;
import org.apache.roller.weblogger.pojos.Weblog;

/**
 * Implementation of WeblogPluginService.
 * Delegates to PluginManager for plugin operations.
 */
@com.google.inject.Singleton
public class WeblogPluginServiceImpl implements WeblogPluginService {
    
    private static final Log log = LogFactory.getLog(WeblogPluginServiceImpl.class);
    private final Weblogger roller;
    
    @com.google.inject.Inject
    protected WeblogPluginServiceImpl(Weblogger roller) {
        log.debug("Instantiating Weblog Plugin Service");
        this.roller = roller;
    }
    
    @Override
    public Map<String, WeblogEntryPlugin> getInitializedPlugins(Weblog weblog) {
        try {
            PluginManager ppmgr = roller.getPluginManager();
            return ppmgr.getWeblogEntryPlugins(weblog);
        } catch (Exception e) {
            log.error("ERROR: initializing plugins for weblog - " + weblog.getHandle(), e);
        }
        return null;
    }
    
    @Override
    public void release() {}
}
