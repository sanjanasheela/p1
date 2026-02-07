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

import org.apache.roller.weblogger.pojos.Weblog;

/**
 * Service for weblog category management operations.
 * Extracts category-related business logic from Weblog POJO.
 */
public interface WeblogCategoryService {
    
    /**
     * Check if weblog has a category with the specified name.
     *
     * @param weblog The weblog to check.
     * @param name The category name to look for.
     * @return boolean true if category exists, false otherwise.
     */
    boolean hasCategory(Weblog weblog, String name);
    
    /**
     * Release all resources associated with this service.
     */
    void release();
}
