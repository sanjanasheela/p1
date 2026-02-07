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
import org.apache.roller.weblogger.pojos.WeblogCategory;
import org.apache.roller.weblogger.WebloggerException;
import java.util.List;

/**
 * Service for managing weblog categories.
 * 
 * Extracted from Weblog POJO to eliminate Hub-like Modularization smell.
 * This service handles all category-related operations for weblogs.
 */
public interface WeblogCategoryService {
    
    /**
     * Get all categories for a weblog.
     * 
     * @param weblog The weblog
     * @return List of categories (never null, may be empty)
     */
    List<WeblogCategory> getCategories(Weblog weblog);
    
    /**
     * Get the default category for a weblog.
     * 
     * @param weblog The weblog
     * @return Default category or null if none exists
     */
    WeblogCategory getDefaultCategory(Weblog weblog);
    
    /**
     * Get a category by name.
     * 
     * @param weblog The weblog
     * @param name Category name
     * @return Category with given name or null if not found
     */
    WeblogCategory getCategoryByName(Weblog weblog, String name);
    
    /**
     * Check if a weblog has a category with the given name.
     * 
     * @param weblog The weblog to check
     * @param name The category name to look for
     * @return true if category exists, false otherwise
     */
    boolean hasCategory(Weblog weblog, String name);
    
    /**
     * Add a category to a weblog.
     * 
     * @param weblog The weblog
     * @param category The category to add
     * @throws WebloggerException if operation fails or duplicate name
     */
    void addCategory(Weblog weblog, WeblogCategory category) throws WebloggerException;
}
