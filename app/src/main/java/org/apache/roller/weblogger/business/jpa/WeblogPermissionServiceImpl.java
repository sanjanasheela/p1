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

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.WebloggerException;
import org.apache.roller.weblogger.business.UserManager;
import org.apache.roller.weblogger.business.WeblogPermissionService;
import org.apache.roller.weblogger.business.Weblogger;
import org.apache.roller.weblogger.pojos.User;
import org.apache.roller.weblogger.pojos.Weblog;
import org.apache.roller.weblogger.pojos.WeblogPermission;

/**
 * Implementation of WeblogPermissionService.
 * Delegates to UserManager for permission checking.
 */
@com.google.inject.Singleton
public class WeblogPermissionServiceImpl implements WeblogPermissionService {
    
    private static final Log log = LogFactory.getLog(WeblogPermissionServiceImpl.class);
    private final Weblogger roller;
    
    @com.google.inject.Inject
    protected WeblogPermissionServiceImpl(Weblogger roller) {
        log.debug("Instantiating Weblog Permission Service");
        this.roller = roller;
    }
    
    @Override
    public boolean hasUserPermission(Weblog weblog, User user, String action) {
        return hasUserPermissions(weblog, user, List.of(action));
    }
    
    @Override
    public boolean hasUserPermissions(Weblog weblog, User user, List<String> actions) {
        try {
            UserManager umgr = roller.getUserManager();
            WeblogPermission userPerms = new WeblogPermission(weblog, user, actions);
            return umgr.checkPermission(userPerms, user);
        } catch (WebloggerException ex) {
            log.error("ERROR checking user permission", ex);
        }
        return false;
    }
    
    @Override
    public void release() {}
}
