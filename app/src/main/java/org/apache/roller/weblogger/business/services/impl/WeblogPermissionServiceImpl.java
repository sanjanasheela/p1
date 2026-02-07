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
import org.apache.roller.weblogger.business.services.WeblogPermissionService;
import org.apache.roller.weblogger.business.WebloggerFactory;
import org.apache.roller.weblogger.business.UserManager;
import org.apache.roller.weblogger.pojos.Weblog;
import org.apache.roller.weblogger.pojos.User;
import org.apache.roller.weblogger.pojos.WeblogPermission;
import org.apache.roller.weblogger.WebloggerException;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of WeblogPermissionService.
 */
public class WeblogPermissionServiceImpl implements WeblogPermissionService {
    
    private static Log log = LogFactory.getLog(WeblogPermissionServiceImpl.class);
    
    @Override
    public boolean hasUserPermission(Weblog weblog, User user, String action) {
        return hasUserPermissions(weblog, user, Collections.singletonList(action));
    }
    
    @Override
    public boolean hasUserPermissions(Weblog weblog, User user, List<String> actions) {
        if (weblog == null || user == null || actions == null) {
            return false;
        }
        
        try {
            UserManager umgr = WebloggerFactory.getWeblogger().getUserManager();
            WeblogPermission userPerms = new WeblogPermission(weblog, user, actions);
            return umgr.checkPermission(userPerms, user);
        } catch (WebloggerException ex) {
            log.error("ERROR checking user permission", ex);
        }
        
        return false;
    }
    
    @Override
    public User getCreator(Weblog weblog) {
        if (weblog == null) {
            return null;
        }
        
        try {
            return WebloggerFactory.getWeblogger().getUserManager()
                .getUserByUserName(weblog.getCreatorUserName());
        } catch (Exception e) {
            log.error("ERROR fetching user object for username: " + weblog.getCreatorUserName(), e);
        }
        
        return null;
    }
    
    @Override
    public String getCreatorUserName(Weblog weblog) {
        return weblog != null ? weblog.getCreatorUserName() : null;
    }
}
