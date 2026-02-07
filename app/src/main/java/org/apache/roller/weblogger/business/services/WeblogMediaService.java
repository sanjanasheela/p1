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
import org.apache.roller.weblogger.pojos.MediaFileDirectory;
import org.apache.roller.weblogger.WebloggerException;
import java.util.List;

/**
 * Service for managing weblog media files.
 * 
 * Extracted from Weblog POJO to eliminate Hub-like Modularization smell.
 */
public interface WeblogMediaService {
    
    /**
     * Get all media file directories for a weblog.
     * 
     * @param weblog The weblog
     * @return List of media directories (never null, may be empty)
     */
    List<MediaFileDirectory> getMediaFileDirectories(Weblog weblog);
    
    /**
     * Get a media file directory by name.
     * 
     * @param weblog The weblog
     * @param name Directory name
     * @return Media directory or null if not found
     */
    MediaFileDirectory getMediaFileDirectory(Weblog weblog, String name);
    
    /**
     * Check if weblog has a media directory with given name.
     * 
     * @param weblog The weblog
     * @param name Directory name
     * @return true if directory exists
     */
    boolean hasMediaFileDirectory(Weblog weblog, String name);
}
