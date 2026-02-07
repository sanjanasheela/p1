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

import org.apache.roller.weblogger.business.services.WeblogBookmarkService;
import org.apache.roller.weblogger.pojos.Weblog;
import org.apache.roller.weblogger.pojos.WeblogBookmarkFolder;
import org.apache.roller.weblogger.WebloggerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of WeblogBookmarkService.
 */
public class WeblogBookmarkServiceImpl implements WeblogBookmarkService {
    
    @Override
    public List<WeblogBookmarkFolder> getBookmarkFolders(Weblog weblog) {
        if (weblog == null || weblog.getBookmarkFolders() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(weblog.getBookmarkFolders());
    }
    
    @Override
    public boolean hasBookmarkFolder(Weblog weblog, String name) {
        if (weblog == null || name == null) {
            return false;
        }
        
        for (WeblogBookmarkFolder folder : getBookmarkFolders(weblog)) {
            if (name.equalsIgnoreCase(folder.getName())) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void addBookmarkFolder(Weblog weblog, WeblogBookmarkFolder folder) throws WebloggerException {
        if (weblog == null || folder == null) {
            throw new WebloggerException("Weblog and folder cannot be null");
        }
        
        if (folder.getName() == null) {
            throw new WebloggerException("Folder must have a valid name");
        }
        
        // Check for duplicate
        if (hasBookmarkFolder(weblog, folder.getName())) {
            throw new WebloggerException("Duplicate folder name '" + folder.getName() + "'");
        }
        
        // Add to weblog's folder list
        folder.setWeblog(weblog);
        weblog.getBookmarkFolders().add(folder);
    }
}
