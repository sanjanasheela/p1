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

import org.apache.roller.weblogger.business.services.WeblogCategoryService;
import org.apache.roller.weblogger.pojos.Weblog;
import org.apache.roller.weblogger.pojos.WeblogCategory;
import org.apache.roller.weblogger.WebloggerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of WeblogCategoryService.
 */
public class WeblogCategoryServiceImpl implements WeblogCategoryService {
    
    @Override
    public List<WeblogCategory> getCategories(Weblog weblog) {
        if (weblog == null || weblog.getWeblogCategories() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(weblog.getWeblogCategories());
    }
    
    @Override
    public WeblogCategory getDefaultCategory(Weblog weblog) {
        if (weblog == null) {
            return null;
        }
        
        // Return blogger category if set
        if (weblog.getBloggerCategory() != null) {
            return weblog.getBloggerCategory();
        }
        
        // Otherwise return first category
        List<WeblogCategory> categories = getCategories(weblog);
        return categories.isEmpty() ? null : categories.get(0);
    }
    
    @Override
    public boolean hasCategory(Weblog weblog, String name) {
        for (WeblogCategory cat : weblog.getWeblogCategories()) {
            if (name.equals(cat.getName())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public WeblogCategory getCategoryByName(Weblog weblog, String name) {
        if (weblog == null || name == null) {
            return null;
        }
        
        List<WeblogCategory> categories = getCategories(weblog);
        for (WeblogCategory cat : categories) {
            if (name.equals(cat.getName())) {
                return cat;
            }
        }
        
        return null;
    }
    
    @Override
    public void addCategory(Weblog weblog, WeblogCategory category) throws WebloggerException {
        if (weblog == null || category == null) {
            throw new WebloggerException("Weblog and category cannot be null");
        }
        
        if (category.getName() == null) {
            throw new WebloggerException("Category must have a valid name");
        }
        
        // Check for duplicate
        if (hasCategory(weblog, category.getName())) {
            throw new WebloggerException("Duplicate category name '" + category.getName() + "'");
        }
        
        // Add to weblog's category list
        category.setWeblog(weblog);
        weblog.getWeblogCategories().add(category);
    }
}
