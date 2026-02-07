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
import org.apache.roller.weblogger.pojos.WeblogBookmarkFolder;
import org.apache.roller.weblogger.WebloggerException;
import java.util.List;

/**
 * Service for managing weblog bookmarks.
 * 
 * Extracted from Weblog POJO to eliminate Hub-like Modularization smell.
 */
public interface WeblogBookmarkService {
    
    /**
     * Get all bookmark folders for a weblog.
     * 
     * @param weblog The weblog
     * @return List of bookmark folders (never null, may be empty)
     */
    List<WeblogBookmarkFolder> getBookmarkFolders(Weblog weblog);
    
    /**
     * Check if weblog has a bookmark folder with given name.
     * 
     * @param weblog The weblog
     * @param name Folder name
     * @return true if folder exists
     */
    boolean hasBookmarkFolder(Weblog weblog, String name);
    
    /**
     * Add a bookmark folder to a weblog.
     * 
     * @param weblog The weblog
     * @param folder The folder to add
     * @throws WebloggerException if operation fails or duplicate name
     */
    void addBookmarkFolder(Weblog weblog, WeblogBookmarkFolder folder) throws WebloggerException;
}
