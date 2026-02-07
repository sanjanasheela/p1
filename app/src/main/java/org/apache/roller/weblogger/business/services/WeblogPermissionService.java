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
import org.apache.roller.weblogger.pojos.User;
import org.apache.roller.weblogger.WebloggerException;
import java.util.List;

/**
 * Service for managing weblog permissions.
 * 
 * Extracted from Weblog POJO to eliminate Hub-like Modularization smell.
 */
public interface WeblogPermissionService {
    
    /**
     * Check if user has permission for specified action.
     * 
     * @param weblog The weblog
     * @param user The user
     * @param action Permission action
     * @return true if user has permission
     */
    boolean hasUserPermission(Weblog weblog, User user, String action);
    
    /**
     * Check if user has all specified permissions.
     * 
     * @param weblog The weblog
     * @param user The user
     * @param actions List of permission actions
     * @return true if user has all permissions
     */
    boolean hasUserPermissions(Weblog weblog, User user, List<String> actions);
    
    /**
     * Get the creator of the weblog.
     * 
     * @param weblog The weblog
     * @return Creator user
     */
    User getCreator(Weblog weblog);
    
    /**
     * Get the creator username.
     * 
     * @param weblog The weblog
     * @return Creator username
     */
    String getCreatorUserName(Weblog weblog);
}
