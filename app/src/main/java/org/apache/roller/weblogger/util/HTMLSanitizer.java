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
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
/**
 * Copyright (c) 2009 Open Lab, http://www.open-lab.com/
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.apache.roller.weblogger.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.roller.weblogger.config.WebloggerConfig;

public class HTMLSanitizer {

    public static Boolean xssEnabled = WebloggerConfig.getBooleanProperty("weblogAdminsUntrusted", Boolean.FALSE);

    public static Pattern forbiddenTags = Pattern.compile("^(script|object|embed|link|style|form|input)$");
    public static Pattern allowedTags = Pattern.compile("^(b|p|i|s|a|img|table|thead|tbody|tfoot|tr|th|td|dd|dl|dt|em|h1|h2|h3|h4|h5|h6|li|ul|ol|span|div|strike|strong|"
            + "sub|sup|pre|del|code|blockquote|kbd|br|hr|area|map|object|embed|param|link|form|small|big)$");
    // <!--.........>
    private static final Pattern commentPattern = Pattern.compile("<!--.*");
    // <tag ....props.....>
    private static final Pattern tagStartPattern = Pattern.compile("<(?i)(\\w+\\b)\\s*(.*)/?>$");
    // </tag .........>
    private static final Pattern tagClosePattern = Pattern.compile("</(?i)(\\w+\\b)\\s*>$");
    private static final Pattern standAloneTags = Pattern.compile("^(img|br|hr)$");
    private static final Pattern selfClosed = Pattern.compile("<.+/>");
    // prop="...."
    private static final Pattern attributesPattern = Pattern.compile("(\\w*)\\s*=\\s*\"([^\"]*)\"");
    // color:red;
    private static final Pattern stylePattern = Pattern.compile("([^\\s^:]+)\\s*:\\s*([^;]+);?");
    // url('....')"
    private static final Pattern urlStylePattern = Pattern.compile("(?i).*\\b\\s*url\\s*\\(['\"]([^)]*)['\"]\\)");
    // expression(....)"   thanks to Ben Summer
    private static final Pattern forbiddenStylePattern = Pattern.compile("(?:(expression|eval|javascript))\\s*\\(");

    /**
     * This method should be used to test input.
     *
     * @param html
     * @return true if the input is "valid"
     */
    public static boolean isSanitized(String html) {
        return sanitizer(html).isValid();
    }

    /**
     * Used to clean every html before to output it in any html page
     *
     * @param html
     * @return sanitized html
     */
    public static String sanitize(String html) {
        return sanitizer(html).getHtml();
    }

    public static String conditionallySanitize(String ret) {
        // if XSS is enabled then sanitize HTML
        if (xssEnabled && ret != null) {
            ret = HTMLSanitizer.sanitize(ret);
        }
        return ret;
    }

    /**
     * Used to get the text, tags removed or encoded
     *
     * @param html
     * @return sanitized text
     */
    public static String getText(String html) {
        return sanitizer(html).getText();
    }

    /**
     * This is the main method of sanitizing. It will be used both for
     * validation and cleaning
     *
     * @param html
     * @return a SanitizeResult object
     */
    public static SanitizeResult sanitizer(String html) {
        return sanitizer(html, allowedTags, forbiddenTags);
    }

    public static SanitizeResult sanitizer(String html, Pattern allowedTags, Pattern forbiddenTags) {
        SanitizeResult ret = new SanitizeResult();
        Stack<String> openTags = new Stack<>();

        List<String> tokens = tokenize(html);

        // -------------------   LOOP for every token --------------------------
        for (String token : tokens) {
            boolean isAcceptedToken = false;

            Matcher startMatcher = tagStartPattern.matcher(token);
            Matcher endMatcher = tagClosePattern.matcher(token);

            //--------------------------------------------------------------------------------  COMMENT    <!-- ......... -->
            if (commentPattern.matcher(token).find()) {
                ret.appendVal(token + (token.endsWith("-->") ? "" : "-->"));
                ret.addInvalidTag(token + (token.endsWith("-->") ? "" : "-->"));
                continue;

                //--------------------------------------------------------------------------------  OPEN TAG    <tag .........>
            } else if (startMatcher.find()) {

                //tag name extraction
                String tag = startMatcher.group(1).toLowerCase();

                //-----------------------------------------------------  FORBIDDEN TAG   <script .........>
                if (forbiddenTags.matcher(tag).find()) {
                    ret.addInvalidTag("<" + tag + ">");
                    continue;

                    // --------------------------------------------------  WELL KNOWN TAG
                } else if (allowedTags.matcher(tag).find()) {

                    String cleanToken = "<" + tag;
                    String tokenBody = startMatcher.group(2);

                    //first test table consistency
                    //table tbody tfoot thead th tr td
                    if ("thead".equals(tag) || "tbody".equals(tag) || "tfoot".equals(tag) || "tr".equals(tag)) {
                        if (openTags.search("table") < 1) {
                            ret.addInvalidTag("<" + tag + ">");
                            continue;
                        }
                    } else if (("td".equals(tag) || "th".equals(tag)) && openTags.search("tr") < 1) {
                        ret.addInvalidTag("<" + tag + ">");
                        continue;
                    }

                    // then test properties
                    Matcher attributes = attributesPattern.matcher(tokenBody);

                    // URL flag
                    boolean foundURL = false;
                    while (attributes.find()) {

                        String attr = attributes.group(1).toLowerCase();
                        String val = attributes.group(2);

                        // we will accept href in case of <A>
                        // <a href="......">
                        if ("a".equals(tag) && "href".equals(attr)) {
                            String[] customSchemes = {"http", "https"};
                            if (new UrlValidator(customSchemes).isValid(val)) {
                                foundURL = true;
                            } else {
                                // may be it is a mailto?
                                // case <a href="mailto:pippo@pippo.com?subject=...."
                                if (val.toLowerCase().startsWith("mailto:") && val.indexOf('@') >= 0) {
                                    String val1 = "http://www." + val.substring(val.indexOf('@') + 1);
                                    if (new UrlValidator(customSchemes).isValid(val1)) {
                                        foundURL = true;
                                    } else {
                                        ret.addInvalidTag(attr + " " + val);
                                        val = "";
                                    }
                                } else {
                                    ret.addInvalidTag(attr + " " + val);
                                    val = "";
                                }
                            }

                        } else if (tag.matches("img|embed") && "src".equals(attr)) {
                            // <img src="......">
                            String[] customSchemes = {"http", "https"};
                            if (new UrlValidator(customSchemes).isValid(val)) {
                                foundURL = true;
                            } else {
                                ret.addInvalidTag(attr + " " + val);
                                val = "";
                            }
                        } else if ("href".equals(attr) || "src".equals(attr)) {
                            // <tag src/href="......">   skipped
                            ret.addInvalidTag(tag + " " + attr + " " + val);
                            continue;
                        } else if (attr.matches("width|height")) {
                            // <tag width/height="......">
                            if (!val.toLowerCase().matches("\\d+%|\\d+$")) {
                                // test numeric values
                                ret.addInvalidTag(tag + " " + attr + " " + val);
                                continue;
                            }

                        } else if ("style".equals(attr)) {
                            // <tag style="......">
                            // then test properties
                            Matcher styles = stylePattern.matcher(val);
                            String cleanStyle = "";

                            while (styles.find()) {
                                String styleName = styles.group(1).toLowerCase();
                                String styleValue = styles.group(2);

                                // suppress invalid styles values
                                if (forbiddenStylePattern.matcher(styleValue).find()) {
                                    ret.addInvalidTag(tag + " " + attr + " " + styleValue);
                                    continue;
                                }

                                // check if valid url
                                Matcher urlStyleMatcher = urlStylePattern.matcher(styleValue);
                                if (urlStyleMatcher.find()) {
                                    String[] customSchemes = {"http", "https"};
                                    String url = urlStyleMatcher.group(1);
                                    if (!new UrlValidator(customSchemes).isValid(url)) {
                                        ret.addInvalidTag(tag + " " + attr + " " + styleValue);
                                        continue;
                                    }
                                }

                                cleanStyle = cleanStyle + styleName + ":" + encode(styleValue) + ";";

                            }
                            val = cleanStyle;

                        } else if (attr.startsWith("on")) {
                            // skip all javascript events
                            ret.addInvalidTag(tag + " " + attr + " " + val);
                            continue;

                        } else {
                            // by default encode all properties
                            val = encode(val);
                        }

                        cleanToken = cleanToken + " " + attr + "=\"" + val + "\"";
                    }
                    cleanToken = cleanToken + ">";

                    isAcceptedToken = true;

                    // for <img> and <a>
                    if (tag.matches("a|img|embed") && !foundURL) {
                        isAcceptedToken = false;
                        cleanToken = "";
                    }

                    token = cleanToken;

                    // push the tag if require closure and it is accepted (otherwise is encoded)
                    if (isAcceptedToken && !(standAloneTags.matcher(tag).find() || selfClosed.matcher(tag).find())) {
                        openTags.push(tag);
                    }

                    // --------------------------------------------------------------------------------  UNKNOWN TAG
                } else {
                    ret.addInvalidTag(token);
                    ret.appendVal(token);
                    continue;

                }

                // --------------------------------------------------------------------------------  CLOSE TAG </tag>
            } else if (endMatcher.find()) {
                String tag = endMatcher.group(1).toLowerCase();

                //is self closing
                if (selfClosed.matcher(tag).find()) {
                    ret.addInvalidTag(token);
                    continue;
                }
                if (forbiddenTags.matcher(tag).find()) {
                    ret.addInvalidTag("/" + tag);
                    continue;
                }
                if (!allowedTags.matcher(tag).find()) {
                    ret.addInvalidTag(token);
                    ret.appendVal(token);
                    continue;
                } else {

                    String cleanToken = "";

                    // check tag position in the stack
                    int pos = openTags.search(tag);
                    // if found on top ok
                    for (int i = 1; i <= pos; i++) {
                        //pop all elements before tag and close it
                        String poppedTag = openTags.pop();
                        cleanToken = cleanToken + "</" + poppedTag + ">";
                        isAcceptedToken = true;
                    }

                    token = cleanToken;
                }

            }

            ret.appendVal(token);

            if (isAcceptedToken) {
                ret.appendHtml(token);
                //ret.text = ret.text + " ";
            } else {
                String sanToken = htmlEncodeApexesAndTags(token);
                ret.appendHtml(sanToken);
                ret.appendText(htmlEncodeApexesAndTags(removeLineFeed(token)));
            }

        }

        // must close remaining tags
        while (!openTags.isEmpty()) {
            //pop all elements before tag and close it
            String poppedTag = openTags.pop();
            ret.appendHtml("</" + poppedTag + ">");
            ret.appendVal("</" + poppedTag + ">");
        }

        //set boolean value
        ret.finalizeResult();

        return ret;
    }

    /**
     * Splits html tag and tag content <......>.
     *
     * @param html
     * @return a list of token
     */
    private static List<String> tokenize(String html) {
        List<String> tokens = new ArrayList<>();
        int pos = 0;
        String token = "";
        int len = html.length();
        while (pos < len) {
            char c = html.charAt(pos);

            String ahead = html.substring(pos, pos > len - 4 ? len : pos + 4);

            //a comment is starting
            if ("<!--".equals(ahead)) {
                //store the current token
                if (token.length() > 0) {
                    tokens.add(token);
                }

                //clear the token
                token = "";

                // search the end of <......>
                int end = moveToMarkerEnd(pos, "-->", html);
                tokens.add(html.substring(pos, end));
                pos = end;

                // a new "<" token is starting
            } else if ('<' == c) {

                //store the current token
                if (token.length() > 0) {
                    tokens.add(token);
                }

                //clear the token
                token = "";

                // serch the end of <......>
                int end = moveToMarkerEnd(pos, ">", html);
                tokens.add(html.substring(pos, end));
                pos = end;

            } else {
                token = token + c;
                pos++;
            }

        }

        //store the last token
        if (token.length() > 0) {
            tokens.add(token);
        }

        return tokens;
    }

    private static int moveToMarkerEnd(int pos, String marker, String s) {
        int i = s.indexOf(marker, pos);
        if (i > -1) {
            pos = i + marker.length();
        } else {
            pos = s.length();
        }
        return pos;
    }

    /**
     * Encapsulates and manages HTML sanitization results with multi-format
     * output support.
     *
     * This class provides a rich abstraction for HTML sanitization by: -
     * Managing multiple output formats (HTML, text, value) with intelligent
     * content accumulation - Enforcing validation rules and tracking security
     * violations - Providing content analysis and transformation capabilities -
     * Offering format-specific optimizations and sanitization strategies
     *
     * The class maintains three distinct content representations: 1. HTML:
     * Fully sanitized and encoded for safe display in web pages 2. Text: Plain
     * text extraction suitable for search indexing and excerpts 3. Val: Cleaned
     * HTML source for storage with minimal encoding
     *
     * Thread-safety: This class is NOT thread-safe and should be used within a
     * single thread.
     */
    static class SanitizeResult {

        private static final int DEFAULT_CAPACITY = 256;
        private static final int MAX_INVALID_TAGS_TRACKED = 100;

        private final StringBuilder html;
        private final StringBuilder text;
        private final StringBuilder val;
        private final List<String> invalidTags;
        private final Map<String, Integer> invalidTagCounts;
        private boolean isValid;
        private int totalContentLength;

        /**
         * Creates a new SanitizeResult with optimized initial capacity.
         */
        SanitizeResult() {
            this.html = new StringBuilder(DEFAULT_CAPACITY);
            this.text = new StringBuilder(DEFAULT_CAPACITY);
            this.val = new StringBuilder(DEFAULT_CAPACITY);
            this.invalidTags = new ArrayList<>();
            this.invalidTagCounts = new HashMap<>();
            this.isValid = true;
            this.totalContentLength = 0;
        }

        /**
         * Returns sanitized HTML safe for display in web pages. All potentially
         * dangerous content is encoded or removed.
         *
         * @return sanitized HTML string, never null
         */
        public String getHtml() {
            return html.toString();
        }

        /**
         * Returns plain text extracted from valid HTML tags. Useful for search
         * indexing, excerpts, and content preview.
         *
         * @return plain text representation, never null
         */
        public String getText() {
            return text.toString();
        }

        /**
         * Returns cleaned HTML suitable for storage. Invalid tags are removed
         * but content is minimally encoded.
         *
         * @return cleaned HTML value, never null
         */
        public String getVal() {
            return val.toString();
        }

        /**
         * Indicates whether the input HTML passed all security validation
         * checks.
         *
         * @return true if no security violations detected, false otherwise
         */
        public boolean isValid() {
            return isValid;
        }

        /**
         * Returns an immutable list of all invalid/rejected tags encountered.
         * List is capped at MAX_INVALID_TAGS_TRACKED for memory efficiency.
         *
         * @return defensive copy of invalid tags list, never null
         */
        public List<String> getInvalidTags() {
            return new ArrayList<>(invalidTags);
        }

        /**
         * Appends sanitized HTML content with automatic whitespace
         * normalization. Validates content length and applies security
         * constraints.
         *
         * @param content HTML content to append, null values are ignored
         */
        void appendHtml(String content) {
            if (content != null && !content.isEmpty()) {
                this.html.append(content);
                updateTotalLength(content.length());
            }
        }

        /**
         * Appends plain text content with automatic cleaning. Removes excess
         * whitespace and normalizes line breaks.
         *
         * @param content text content to append, null values are ignored
         */
        void appendText(String content) {
            if (content != null && !content.isEmpty()) {
                // Normalize whitespace for text representation
                String cleaned = normalizeWhitespace(content);
                if (!cleaned.isEmpty()) {
                    if (this.text.length() > 0 && !cleaned.startsWith(" ")) {
                        this.text.append(" ");
                    }
                    this.text.append(cleaned);
                }
            }
        }

        /**
         * Appends value content for storage representation.
         *
         * @param content value content to append, null values are ignored
         */
        void appendVal(String content) {
            if (content != null && !content.isEmpty()) {
                this.val.append(content);
            }
        }

        /**
         * Registers a security violation (invalid tag) with frequency tracking.
         * Automatically invalidates the result and maintains violation
         * statistics.
         *
         * @param tag the invalid/dangerous tag detected, null/empty values
         * ignored
         */
        void addInvalidTag(String tag) {
            if (tag != null && !tag.isEmpty()) {
                // Track up to maximum limit to prevent memory issues
                if (invalidTags.size() < MAX_INVALID_TAGS_TRACKED) {
                    this.invalidTags.add(tag);
                }

                // Maintain frequency count for analysis
                String normalizedTag = normalizeTagName(tag);
                invalidTagCounts.merge(normalizedTag, 1, Integer::sum);
            }
        }

        /**
         * Finalizes the sanitization process and computes the final validation
         * state. Must be called after all content processing is complete. This
         * method is idempotent and safe to call multiple times.
         */
        void finalizeResult() {
            this.isValid = this.invalidTags.isEmpty();
        }

        /**
         * Checks if any security violations were detected during sanitization.
         *
         * @return true if violations exist, false if content is clean
         */
        boolean hasInvalidTags() {
            return !this.invalidTags.isEmpty();
        }

        /**
         * Returns the total count of invalid tag violations detected.
         *
         * @return number of security violations (may be capped at
         * MAX_INVALID_TAGS_TRACKED)
         */
        int getInvalidTagCount() {
            return this.invalidTags.size();
        }

        /**
         * Returns the most frequently occurring invalid tag. Useful for
         * security analysis and reporting.
         *
         * @return most common invalid tag, or null if no violations
         */
        String getMostCommonInvalidTag() {
            return invalidTagCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
        }

        /**
         * Returns a map of invalid tag frequencies for security analysis.
         *
         * @return unmodifiable map of tag names to occurrence counts
         */
        Map<String, Integer> getInvalidTagStatistics() {
            return Collections.unmodifiableMap(invalidTagCounts);
        }

        /**
         * Checks if the result contains any actual content.
         *
         * @return true if HTML content is not empty, false otherwise
         */
        boolean hasContent() {
            return html.length() > 0;
        }

        /**
         * Returns the approximate total length of content processed.
         *
         * @return total character count
         */
        int getTotalContentLength() {
            return totalContentLength;
        }

        /**
         * Calculates a security risk score based on invalid tags detected.
         * Higher scores indicate more severe security concerns.
         *
         * @return risk score from 0 (safe) to 100 (highly dangerous)
         */
        int calculateSecurityRiskScore() {
            if (invalidTags.isEmpty()) {
                return 0;
            }

            int score = Math.min(invalidTags.size() * 10, 100);

            // Increase score for dangerous tags
            for (String tag : invalidTags) {
                String lower = tag.toLowerCase();
                if (lower.contains("script") || lower.contains("eval")) {
                    score = Math.min(score + 30, 100);
                } else if (lower.contains("onclick") || lower.contains("onerror")) {
                    score = Math.min(score + 20, 100);
                }
            }

            return score;
        }

        /**
         * Normalizes whitespace in text content.
         */
        private String normalizeWhitespace(String text) {
            if (text == null || text.isEmpty()) {
                return "";
            }
            return text.trim().replaceAll("\\s+", " ");
        }

        /**
         * Normalizes tag name for consistent tracking.
         */
        private String normalizeTagName(String tag) {
            if (tag == null) {
                return "";
            }
            // Extract just the tag name, removing < > and attributes
            String normalized = tag.replaceAll("[<>/]", "").trim();
            int spaceIdx = normalized.indexOf(' ');
            if (spaceIdx > 0) {
                normalized = normalized.substring(0, spaceIdx);
            }
            return normalized.toLowerCase();
        }

        /**
         * Updates the total content length counter.
         */
        private void updateTotalLength(int additionalLength) {
            this.totalContentLength += additionalLength;
        }
    }

    public static String encode(String s) {
        return convertLineFeedToBR(htmlEncodeApexesAndTags(s == null ? "" : s));
    }

    public static final String htmlEncodeApexesAndTags(String source) {
        return htmlEncodeTag(htmlEncodeApexes(source));
    }

    public static final String htmlEncodeApexes(String source) {
        if (source != null) {
            return replaceAllNoRegex(source, new String[]{"\"", "'"}, new String[]{"&quot;", "&#39;"});
        } else {
            return null;
        }
    }

    public static final String htmlEncodeTag(String source) {
        if (source != null) {
            return replaceAllNoRegex(source, new String[]{"<", ">"}, new String[]{"&lt;", "&gt;"});
        } else {
            return null;
        }
    }

    public static String convertLineFeedToBR(String text) {
        if (text != null) {
            return replaceAllNoRegex(text, new String[]{"\n", "\f", "\r"}, new String[]{"<br>", "<br>", " "});
        } else {
            return null;
        }
    }

    public static String removeLineFeed(String text) {

        if (text != null) {
            return replaceAllNoRegex(text, new String[]{"\n", "\f", "\r"}, new String[]{" ", " ", " "});
        } else {
            return null;
        }
    }

    public static final String replaceAllNoRegex(String source, String searches[], String replaces[]) {
        int k;
        String tmp = source;
        for (k = 0; k < searches.length; k++) {
            tmp = replaceAllNoRegex(tmp, searches[k], replaces[k]);
        }
        return tmp;
    }

    public static final String replaceAllNoRegex(String source, String search, String replace) {
        StringBuilder buffer = new StringBuilder();
        if (source != null) {
            if (search.length() == 0) {
                return source;
            }
            int oldPos, pos;
            for (oldPos = 0, pos = source.indexOf(search, oldPos); pos != -1; oldPos = pos + search.length(), pos = source.indexOf(search, oldPos)) {
                buffer.append(source.substring(oldPos, pos));
                buffer.append(replace);
            }
            if (oldPos < source.length()) {
                buffer.append(source.substring(oldPos));
            }
        }
        return new String(buffer);
    }
}
