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

package org.apache.roller.weblogger.pojos;

import java.io.Serializable;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.roller.weblogger.util.I18nUtils;

/**
 * Manages localization settings for a Weblog.
 * Handles locale and timezone configuration and provides instances.
 *
 * This class is part of a refactoring to address insufficient modularization
 * in the Weblog class by extracting localization-related functionality.
 */
public class WeblogLocalization implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final Weblog weblog;
    
    // Localization settings
    private String locale = null;
    private String timeZone = null;
    
    /**
     * Constructor - package private, only accessible through Weblog class
     */
    WeblogLocalization(Weblog weblog) {
        this.weblog = weblog;
    }
    
    /**
     * Locale of the Website.
     */
    public String getLocale() {
        return this.locale;
    }
    
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    /**
     * Timezone of the Website.
     */
    public String getTimeZone() {
        return this.timeZone;
    }
    
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
    
    /**
     * Parse locale value and instantiate a Locale object,
     * otherwise return default Locale.
     *
     * @return Locale
     */
    public Locale getLocaleInstance() {
        return I18nUtils.toLocale(getLocale());
    }
    
    /**
     * Return TimeZone instance for value of timeZone,
     * otherwise return system default instance.
     * @return TimeZone
     */
    public TimeZone getTimeZoneInstance() {
        if (getTimeZone() == null) {
            this.setTimeZone(TimeZone.getDefault().getID());
        }
        return TimeZone.getTimeZone(getTimeZone());
    }
    
    /**
     * Copy localization settings from another WeblogLocalization instance
     */
    void copyFrom(WeblogLocalization other) {
        this.locale = other.locale;
        this.timeZone = other.timeZone;
    }
}
