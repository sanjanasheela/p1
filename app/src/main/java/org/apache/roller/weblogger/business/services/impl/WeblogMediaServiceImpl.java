/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version  2.0 (the "License"); you may not
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

import org.apache.roller.weblogger.business.services.WeblogMediaService;
import org.apache.roller.weblogger.pojos.Weblog;
import org.apache.roller.weblogger.pojos.MediaFileDirectory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of WeblogMediaService.
 */
public class WeblogMediaServiceImpl implements WeblogMediaService {
    
    @Override
    public List<MediaFileDirectory> getMediaFileDirectories(Weblog weblog) {
        if (weblog == null || weblog.getMediaFileDirectories() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(weblog.getMediaFileDirectories());
    }
    
    @Override
    public MediaFileDirectory getMediaFileDirectory(Weblog weblog, String name) {
        if (weblog == null || name == null) {
            return null;
        }
        
        for (MediaFileDirectory dir : getMediaFileDirectories(weblog)) {
            if (name.equals(dir.getName())) {
                return dir;
            }
        }
        
        return null;
    }
    
    @Override
    public boolean hasMediaFileDirectory(Weblog weblog, String name) {
        return getMediaFileDirectory(weblog, name) != null;
    }
}
