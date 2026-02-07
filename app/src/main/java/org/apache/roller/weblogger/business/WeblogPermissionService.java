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

package org.apache.roller.weblogger.business;

import java.util.List;
import org.apache.roller.weblogger.pojos.User;
import org.apache.roller.weblogger.pojos.Weblog;

/**
 * Service for weblog permission checking operations.
 * Extracts permission-related business logic from Weblog POJO.
 */
public interface WeblogPermissionService {
    
    /**
     * Check if user has a specific permission action in the weblog.
     *
     * @param weblog The weblog to check permissions for.
     * @param user The user to check.
     * @param action The permission action to check.
     * @return boolean true if user has the permission, false otherwise.
     */
    boolean hasUserPermission(Weblog weblog, User user, String action);
    
    /**
     * Check if user has all specified permission actions in the weblog.
     *
     * @param weblog The weblog to check permissions for.
     * @param user The user to check.
     * @param actions List of permission actions to check.
     * @return boolean true if user has all permissions, false otherwise.
     */
    boolean hasUserPermissions(Weblog weblog, User user, List<String> actions);
    
    /**
     * Release all resources associated with this service.
     */
    void release();
}
