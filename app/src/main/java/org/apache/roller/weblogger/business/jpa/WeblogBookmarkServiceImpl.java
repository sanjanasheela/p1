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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.business.WeblogBookmarkService;
import org.apache.roller.weblogger.pojos.Weblog;
import org.apache.roller.weblogger.pojos.WeblogBookmarkFolder;

/**
 * Implementation of WeblogBookmarkService.
 * Handles bookmark folder-related operations.
 */
@com.google.inject.Singleton
public class WeblogBookmarkServiceImpl implements WeblogBookmarkService {
    
    private static final Log log = LogFactory.getLog(WeblogBookmarkServiceImpl.class);
    
    @com.google.inject.Inject
    protected WeblogBookmarkServiceImpl() {
        log.debug("Instantiating Weblog Bookmark Service");
    }
    
    @Override
    public boolean hasBookmarkFolder(Weblog weblog, String name) {
        for (WeblogBookmarkFolder folder : weblog.getBookmarkFolders()) {
            if (name.equalsIgnoreCase(folder.getName())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void release() {}
}
