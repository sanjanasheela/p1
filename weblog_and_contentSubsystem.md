# Apache Roller - Weblog Subsytem documentation

# Weblog

## 1. weblog (POJO)

**File:** `Weblog.java`  

**Location:** `app/src/main/java/org/apache/roller/weblogger/pojos/Weblog.java`


**Purpose:** Primary domain entity representing a weblog/website. It's essentially the core entity that encapsulates all information about a single weblog including basic information, ownership,settings,features,content management, metaddata.
Weblog serves as a data container, maintains associations with related entities, takes cre of permissions , content retrieval, statisccs, validation.

**key functions:**
* hasUserPermission(): Access control for all weblog operations
* getRecentWeblogEntries()
* getURL()/getAbsoluteURL()-URL generation for linking
* getTheme() -Theme rendering
* addCategory()/getWeblogCategories() - Content organization
* getCommentCount()/getEntryCount() - Statistics display


**interactions:** It is used in weblog creation, weblog management, content operations, rendering, access control, database persistence.



---
 
 ## 2.CreateWeblog (Struts2 Action)
**File:** `CreateWeblog.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/core/CreateWeblog.java`

**Purpose:** Struts2 action controller that handles the weblog creation workflow in the UI. Manages the presentation logic, validation, and orchestration of creating a new weblog/website in the Apache Roller platform. Extends UIAction to provide standard UI action capabilities.

**key functions:**
* execute() -  Initial page load handler that checks permissions, validates weblog creation is enabled, enforces one-blog limit (when group blogging disabled), and pre-populates form with user defaults (locale, timezone, email)
* save() - Form submission handler that validates input, creates new 
* myValidate() - Custom validation logic that ensures handle contains only allowed characters, checks handle uniqueness by querying WeblogManager.getWeblogByHandle(), and validates theme selection
* isWeblogRequired() - Security override that allows access without an existing weblog context (returns false)
* getThemes() - Retrieves list of available themes from ThemeManager for theme selection dropdown
* getBean()/setBean() - Access to CreateWeblogBean that holds form data (handle, name, description, theme, locale, timezone, email)


**interactions:** Used in weblog creation flow triggered from UI, interacts with WeblogManager for persistence and handle validation, UserManager for permission checks, ThemeManager for theme retrieval, WebloggerConfig/WebloggerRuntimeConfig for configuration settings, bound to Struts2 action mapping and JSP/Tiles view layer, creates and populates 
Weblog
 POJO instances.

---

## 2. CreateWeblogBean (Form Bean)
**File:** `CreateWeblogBean.java`

**Location:** ` app/src/main/java/org/apache/roller/weblogger/ui/struts2/core/CreateWeblogBean.java`

**Purpose:**Simple data transfer object (DTO/form bean) used by 
CreateWeblog
 Struts2 action to hold weblog creation form data. Acts as the intermediary between the UI form and the business logic, capturing user input from the weblog creation page.



**key functions:** It contains getters and setters for Handle, Name, Description, EmailAddress, Locale, TimeZone, Theme

**interactions:** Bound to weblog creation form fields in the UI view layer, populated by Struts2 framework from HTTP request parameters during form submission, accessed by 
CreateWeblog
 action to retrieve form data for validation and 
Weblog
 POJO instantiation, pre-populated with user defaults (locale, timezone, email) by CreateWeblog.execute() method before form display.

---

## 3. WebloggerFactory (Singleton Factory)
**File:** `WebloggerFactory.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/business/WebloggerFactory.java`

**Purpose:** Static singleton factory class that provides centralized access to the 
Weblogger
 business tier instance and handles application bootstrapping. Implements the Factory pattern to abstract the instantiation and initialization of the core business tier, ensuring a single point of access throughout the application lifecycle.

**key functions:** 
* getWeblogger() - Static accessor that returns the singleton Weblogger instance, providing access to all manager interfaces (WeblogManager, UserManager, ThemeManager, etc.). Throws IllegalStateException if called before bootstrapping
* bootstrap() - Initializes the business tier using default WebloggerProvider from configuration property weblogger.provider.class, validates WebloggerStartup.isPrepared(), instantiates provider via reflection, and delegates to overloaded bootstrap method
* bootstrap(WebloggerProvider provider) - Overloaded bootstrap using custom provider, validates application preparation state, stores provider reference, invokes provider's bootstrap, and logs version/revision information
* isBootstrapped() - Returns boolean indicating whether the factory has been initialized (checks if webloggerProvider != null)

**interactions:**  Called during application startup to initialize the entire business tier, used throughout the codebase via static calls to WebloggerFactory.getWeblogger() to access managers (e.g., 
getWeblogger().getWeblogManager()
, 
getWeblogger().getUserManager()
), depends on WebloggerStartup for preparation state, loads WebloggerProvider implementation from configuration, referenced by all business logic classes including 
CreateWeblog
, 
Weblog
 POJO methods, and manager implementations.

---

## 4. WeblogManager
**File:** `WeblogManager.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/business/WeblogManager.java`

**Purpose:** Core business interface defining contract for weblog and weblog template management operations. Abstracts data access layer to allow different implementations (currently JPA-based via 
JPAWeblogManagerImpl
). Provides clean API for all weblog CRUD operations, querying, template management, and statistics.

**key functions:** 
* Interface defines methods for weblog CRUD operations (
addWeblog()
, 
saveWeblog()
, 
removeWeblog()
, 
getWeblog()
, 
getWeblogByHandle()
), user-weblog relationships (
getUserWeblogs()
, 
getWeblogUsers()
), template management (
saveTemplate()
, 
removeTemplate()
, 
getTemplate()
, etc.), querying/statistics (
getWeblogs()
, 
getMostCommentedWeblogs()
, 
getWeblogCount()
), and resource cleanup (
release()
)
All methods are implemented by 
JPAWeblogManagerImpl
 - see next point for detailed implementation descriptions

**interactions:**  Implemented by 
JPAWeblogManagerImpl
 for JPA/database persistence, accessed via WebloggerFactory.getWeblogger().getWeblogManager() throughout the application, called directly by CreateWeblog.save() to persist new weblogs via 
addWeblog()
, used by 
Weblog
 POJO for handle validation in CreateWeblog.myValidate() via 
getWeblogByHandle()
, defines contract used by all UI controllers, business logic, and rendering components that need weblog data access.


---


## 5. JPAWeblogManagerImpl (Manager Implementation)
**File:** `JPAWeblogManagerImpl.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/business/jpa/JPAWeblogManagerImpl.java`

**Purpose:** JPA (Jakarta Persistence API) implementation of the 
WeblogManager
 interface, providing data access and business logic for weblog CRUD operations. Serves as the primary data access layer for all weblog-related database operations using JPA/Hibernate for ORM. Implements singleton pattern via Google Guice injection.

**key functions:** 
* addWeblog(Weblog newWeblog) - Persists new weblog to database via strategy.store(), flushes changes, calls addWeblogContents()
 to initialize default categories, permissions, bookmarks, folders, media directories, and auto-enabled ping targets
* addWeblogContents(Weblog newWeblog) - Initializes weblog with default content: grants creator ADMIN permission, creates default categories from config property newuser.categories, sets first category as Blogger API default, creates default bookmark folder with blogroll entries from config, creates default media directory, and adds auto-enabled ping targets
* saveWeblog(Weblog weblog) - Updates existing weblog, sets lastModified timestamp, persists via strategy.store()
* removeWeblog(Weblog weblog) - Deletes weblog with cascade: calls 
* removeWeblogContents()- removes weblog entity, clears handle-to-id cache mapping
* getWeblogByHandle(String handle, Boolean visible) - Retrieves weblog by unique handle with caching: checks weblogHandleToIdMap cache first, validates alphanumeric handle, executes named query Weblog.getByHandle, caches result, filters by visibility flag
* getUserWeblogs(User user, boolean enabledOnly) - Returns list of weblogs accessible to user based on WeblogPermission entries, optionally filters for enabled/visible weblogs only
* getWeblog(String id) - Loads weblog by primary key ID
* saveTemplate(WeblogTemplate template)
 - Persists template changes and updates weblog's lastModified date

**interactions:**  Injected as singleton via Google Guice, accessed through WebloggerFactory.getWeblogger().getWeblogManager(), used by 
CreateWeblog
 action's 
save()
 method to persist new weblogs, delegates to JPAPersistenceStrategy for all database operations (queries, stores, removes), collaborates with UserManager for permissions, WeblogEntryManager for entries/categories, MediaFileManager for media directories, AutoPingManager/PingTargetManager for ping configuration, maintains handle-to-id cache for performance optimization.

--- 

## 6. WebloggerProvider (Interface)
**File:** `WebloggerProvider.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/business/WebloggerProvider.java`

**Purpose:** Interface defining the contract for providers that bootstrap and supply the 
Weblogger
 business tier instance. Serves as an abstraction layer allowing different dependency injection frameworks to be used for initializing the application.


**Key Functions:**

* bootstrap() - Triggers the bootstrapping process to initialize the business tier, throws BootstrapException if initialization fails
* getWeblogger() - Returns the initialized singleton 
Weblogger
 instance that provides access to all manager interfaces

**Interactions:** Implemented by 
GuiceWebloggerProvider
 (Google Guice-based implementation), instantiated by WebloggerFactory.bootstrap() via reflection using configured provider class from config property weblogger.provider.class, provides abstraction for dependency injection strategy.


---

## 7. GuiceWebloggerProvider (Dependency Injection Provider)

**File:** `GuiceWebloggerProvider.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/business/GuiceWebloggerProvider.java`

**Purpose:** Guice-based dependency injection implementation of the 
WebloggerProvider interface. Serves as the bootstrapping mechanism that configures and initializes the entire business tier using Google Guice for dependency injection. Creates the Guice injector from a configured module and provides the singleton 
Weblogger instance to the application.

**Key Functions:**

* GuiceWebloggerProvider() - Default constructor that loads Guice module classname from config property guice.backend.module, instantiates the module via reflection, and creates Guice injector with Guice.

* createInjector(module)
GuiceWebloggerProvider(String moduleClassname) - Overloaded constructor accepting custom Guice module classname, validates non-null parameter, instantiates module, creates injector, throws RuntimeException on configuration errors
* bootstrap() - Initializes the business tier by obtaining 
Weblogger
 instance from Guice injector via injector.getInstance(Weblogger.class), stores singleton reference in webloggerInstance
* getWeblogger() - Returns the bootstrapped singleton 
Weblogger instance


**Interactions:**
 Instantiated by WebloggerFactory.bootstrap() using reflection based on weblogger.provider.class config property, responsible for creating entire dependency graph via Guice module (typically GuiceModule), initializes all manager implementations (
JPAWeblogManagerImpl
, JPAUserManagerImpl, etc.) as singletons, provides 
Weblogger
 instance that serves as gateway to all business tier services accessed throughout the application.

---

 ## 9. Weblogger (Interface)

**File:** `Weblogger.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/business/Weblogger.java`

**Purpose:** Main entry point interface to the Weblogger business tier, serving as a facade/gateway that defines contracts for accessing all manager components. Provides a unified interface for the entire business logic layer of Apache Roller.

**Key Functions:** It has getters related to managers. Detailed describtion given below

**Interactions:** Implemented by 
JPAWebloggerImpl
, instantiated by 
GuiceWebloggerProvider
 during application bootstrap, accessed globally throughout the application via WebloggerFactory.getWeblogger(), used by all Struts2 actions, POJO business methods, and anywhere manager access is needed.

---
## 8. JPAWebloggerImpl (Implementation)

**File:** `JPAWebloggerImpl.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/business/jpa/JPAWebloggerImpl.java`

**Purpose:** JPA-specific implementation of the 
Weblogger
 interface that aggregates all manager instances and handles persistence lifecycle. Extends 
WebloggerImpl
 base class and adds JPA-specific persistence strategy integration.

**Key Functions:**

* JPAWebloggerImpl() - Constructor with Google Guice @Inject annotation that receives all manager implementations (WeblogManager, UserManager, ThemeManager, etc.) and JPAPersistenceStrategy, delegates to parent 
* flush() - Overrides parent to flush all pending JPA operations to database via strategy.flush()
* release() - Overrides parent to release both parent resources and JPA persistence strategy via strategy.release()
* shutdown() - Overrides parent to perform complete shutdown: releases resources, calls parent shutdown, shuts down persistence strategy via strategy.shutdown()

**Interactions:** Instantiated as singleton by Google Guice with all dependencies injected, created during GuiceWebloggerProvider.bootstrap(), holds references to all manager implementations (
JPAWeblogManagerImpl
, JPAUserManagerImpl, etc.), delegates persistence operations to JPAPersistenceStrategy, provides manager instances to entire application via inherited getter methods from 
WebloggerImpl
.



## 9. WebloggerImpl (Base Implementation)
**File:** `WebloggerImpl.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/business/WebloggerImpl.java`

**Purpose:** Abstract base class implementation of the 
Weblogger
 interface that holds common functionality applicable to all Weblogger implementations regardless of persistence strategy. Stores references to all manager instances and provides default implementations of manager accessor methods, initialization, and shutdown logic.

**Key Functions:**
* WebloggerImpl(...) - Protected constructor that receives all manager instances (AutoPingManager, BookmarkManager, WeblogManager, UserManager, etc.) and URLStrategy, stores them as final fields, loads build metadata from roller-version.properties resource file (version, revision, buildTime, buildUser)
* Manager accessor methods - 
getWeblogManager()
, 
getUserManager()
, 
getWeblogEntryManager()
, 
getThemeManager()
, 
getMediaFileManager()
, 
getPluginManager()
, etc. - Return stored manager instances
* initialize() - Initializes business tier by calling 
initialize()
 on PropertiesManager, ThemeManager, ThreadManager, IndexManager, MediaFileManager, configures SAX parser security features, initializes ping systems from PingConfig, removes autopings if disabled, calls 
flush()
 at end

* getVersion()
 / 
getRevision()
 / 
getBuildTime()
 / 
getBuildUser() - Return build metadata loaded from properties file


**Interactions:** 




## 10. WeblogCategory (POJO)
**File:** `WeblogCategory.java`

**Location:** 
`app/src/main/java/org/apache/roller/weblogger/pojos/WeblogCategory.java`

**Purpose:** Domain entity representing a blog category for organizing weblog entries. Categories allow users to classify and group blog posts, enabling filtered views and better content organization. Implements Serializable and Comparable for persistence and sorting.

**Key Functions:**

* WeblogCategory(Weblog weblog, String name, String description, String image) - Constructor that creates category, associates with parent weblog, automatically calculates position, adds itself to weblog's category list
* calculatePosition() - Auto-calculates 0-based position in display order

* retrieveWeblogEntries(boolean publishedOnly) - Fetches all entries in this category
* isInUse() - Checks if category has any associated entries
* compareTo() - Enables sorting categories alphabetically by name

* getters and setters for Name, Description, Position, Weblog.

**Interactions:** Created during weblog initialization by JPAWeblogManagerImpl.addWeblogContents() from config property newuser.categories, associated with 
Weblog
 via bidirectional relationship, referenced by 
WeblogEntry
 instances for categorization, used by Weblog.getWeblogCategory() and category management operations, persisted via JPA as entity.



## 11. WeblogPermission (Security POJO)
**File:** `WeblogPermission.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/pojos/WeblogPermission.java`

**Purpose:** Security permission entity defining user access rights for a specific weblog. Extends ObjectPermission and implements Java's 
Permission
 interface, providing granular role-based access control (RBAC) with three permission levels: EDIT_DRAFT, POST, and ADMIN.


**Key Functions:**
* WeblogPermission(Weblog weblog, User user, List actions) - Constructor creating permission for specific user on specific weblog with list of actions
* getWeblog() - Retrieves associated Weblog object via WeblogManager 
* getUser() - Retrieves associated User object via UserManager
* implies(Permission perm) - Implements hierarchical permission checking: ADMIN implies all permissions, POST implies EDIT_DRAFT
* Permission Constants:
EDIT_DRAFT - Can edit draft entries
POST - Can publish entries (implies EDIT_DRAFT)
ADMIN - Full weblog administration (implies all)


**Interactions:** Created by JPAWeblogManagerImpl.addWeblogContents() which grants creator ADMIN permission on new weblogs via UserManager.grantWeblogPermission(), checked by Weblog.hasUserPermission() for access control, validated by UserManager.checkPermission() throughout application, used by CreateWeblog to enforce one-blog limit when group blogging disabled.



## 12. JPAPersistenceStrategy (Persistence Layer)
**File:** `JPAPersistenceStrategy.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/business/jpa/JPAPersistenceStrategy.java`

**Purpose:**  Lowest-level persistence utility class responsible for direct interaction with JPA (Jakarta Persistence API). Manages EntityManager lifecycle, database transactions, and provides abstraction layer for all CRUD operations. Implements singleton pattern via Google Guice injection and uses ThreadLocal storage for EntityManager instances to ensure thread safety.

**Key Functions:**

* JPAPersistenceStrategy(DatabaseProvider dbProvider) - Constructor with @Inject annotation that initializes JPA EntityManagerFactory, supports both JNDI lookup and manual configuration based on jpa.configurationType property, loads persistence properties from WebloggerConfig, creates EntityManagerFactory for persistence unit "RollerPU"
* store(Object obj) - Persists or updates object to database, checks if entity is managed via em.contains(), calls em.persist() for new entities, returns persisted object
* remove(Object po)/  - Deletes object from database, overloaded to accept either object instance or class+id combination
* load(Class<?> clazz, String id) - Retrieves object from database by class type and ID using em.find()
* flush() - Commits current transaction to database via em.getTransaction().commit(), throws WebloggerException on persistence errors
* release() - Rolls back uncommitted changes, closes EntityManager, removes from ThreadLocal storage
* shutdown() - Closes EntityManagerFactory on application shutdown
* getters for the EntityManger, NamedQuery, DynamicQuery, NamedUpdate


**Interactions:**  Injected as singleton into 
JPAWebloggerImpl
 and all JPA manager implementations (
JPAWeblogManagerImpl
, JPAUserManagerImpl, etc.), receives database configuration from DatabaseProvider, uses ThreadLocal pattern to provide isolated EntityManager per request thread, called by all manager CRUD operations for database access, manages transaction boundaries for the entire business tier.



## 13. DatabaseProvider (Configuration Provider)
**File:** `DatabaseProvider.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/business/DatabaseProvider.java`

**Purpose:** Encapsulates database configuration and connection management for Roller, supporting both JNDI DataSource lookup and direct JDBC configuration. Performs early validation during startup by attempting test connection and provides detailed startup logging for debugging installation issues.

**Key Functions:**

* DatabaseProvider() - Constructor that reads database configuration from WebloggerConfig, determines configuration type (JNDI vs JDBC), loads appropriate driver/datasource, and validates with test connection
* getConnection() - Returns database Connection from either DriverManager (JDBC mode) or DataSource (JNDI mode)
* getType() - Returns ConfigurationType enum indicating JNDI or JDBC mode
* getStartupLog() - Returns diagnostic messages for debugging database configuration issues

**Interactions:** Instantiated by Guice and injected into 
JPAPersistenceStrategy
 constructor, configuration properties read from WebloggerConfig (roller.properties), used by 
JPAPersistenceStrategy
 to configure JPA EntityManagerFactory with appropriate database connection settings, provides database connection validation during application bootstrap before business tier initialization.



## 14. WeblogEntry (POJO)

**File:** `WeblogEntry.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/pojos/WeblogEntry.java`

**Purpose:** Main POJO representing a blog post/entry in Apache Roller. Contains all data and relationships for a weblog entry including title, content, publication status, comments, tags, and categories. Serves as the core data model for all blog content operations.

**Key Functions:**
* Standard getters/setters for: `id`, `title`, `text`, `summary`, `status`, `pubTime`, `updateTime`, `website`, `category`, `allowComments`, `locale`, `anchor`
* `setData(WeblogEntry other)` - Copies all properties from another entry
* `getCreator()` - Returns User object for entry creator by looking up username
* `addTag(String name)` - Adds tag to entry with normalized name
* `getTagsAsString()` / `setTagsAsString(String tags)` - Get/set tags as space-separated string
* `getCommentsStillAllowed()` - Checks if comments are still allowed based on weblog settings and comment days limit
* `getComments()` - Returns list of approved comments for this entry
* `getCommentCount()` - Returns number of comments
* `getPermalink()` - Returns absolute URL to entry
* `getDisplayTitle()` - Returns title or first 255 characters of text if title is empty

**Interactions:** Created and managed by `EntryEdit` action via `EntryBean`, persisted by `JPAWeblogEntryManagerImpl`, associated with `Weblog`, `WeblogCategory`, `User`, `WeblogEntryComment`, and `WeblogEntryTag` entities, used in entry CRUD operations, rendering, and search functionality.

---

## 15. EntryBean (Form Bean)

**File:** `EntryBean.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/EntryBean.java`

**Purpose:** Form bean/DTO for managing entry data in the UI layer. Holds form field values during entry creation and editing, handles date/time conversion, and provides data transfer between Struts2 actions and WeblogEntry POJOs.

**Key Functions:**
* Getter/setter methods for all entry properties (`id`, `title`, `summary`, `text`, `status`, `locale`, `categoryId`, `tagsAsString`, `plugins`)
* `getDateString()` / `setDateString(String date)` - Get/set publication date as formatted string
* `getHours()` / `setHours(int hours)` - Get/set publication hour (0-23) 
* `getMinutes()` / `setMinutes(int minutes)` - Get/set publication minute
* `getPubTime(Locale locale, TimeZone timezone)` - Combines date string and time fields into Timestamp
* `isDraft()` / `isPending()` / `isPublished()` / `isScheduled()` - Status check methods
* `copyTo(WeblogEntry entry)` - Transfers form data to WeblogEntry POJO for persistence
* `copyFrom(WeblogEntry entry, Locale locale)` - Populates form bean from existing WeblogEntry

**Interactions:** Used by `EntryEdit` Struts2 action to collect form data, populated from HTTP request parameters, converted to/from `WeblogEntry` POJO, enables data validation and transformation between UI and business layers.

---

## 16. EntryEdit (Struts2 Action)

**File:** `EntryEdit.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/EntryEdit.java`

**Purpose:** Struts2 action class handling the complete blog entry editing workflow. Manages UI for creating new entries and editing existing ones, including form display, validation, draft saving, publishing, and trackback sending.

**Key Functions:**
* `myPrepare()` - Initializes action by loading existing entry if editing, or creating new EntryBean if adding
* `execute()` - Displays entry edit form, loads entry data, populates available categories and plugins
* `save()` - Main save logic: validates input, converts EntryBean to WeblogEntry, saves via WeblogEntryManager, updates search index, handles tag aggregates, manages enclosures
* `saveDraft()` - Sets status to DRAFT and calls `save()`
* `publish()` - Sets status to PUBLISHED or SCHEDULED based on pubTime, calls `save()`, triggers ping queue if newly published
* `trackback()` - Sends trackback notification to specified URL for published entry
* `getCategories()` - Returns list of all categories for the weblog
* `getEntryPlugins()` - Returns available entry plugins from PluginManager
* `getRecentPublishedEntries()` / `getRecentDraftEntries()` / `getRecentPendingEntries()` - Returns recent entries by status for sidebar display

**Interactions:** Extends `UIAction` base class, uses `EntryBean` for form data, calls `WeblogEntryManager.saveWeblogEntry()` to persist changes, interacts with `IndexManager` for search indexing, `PingQueueManager` for ping notifications, `ThemeManager` for editor selection, validates with `myValidate()` method, redirects based on save result.

---

## 17. WeblogEntryComment (POJO)

**File:** `WeblogEntryComment.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/pojos/WeblogEntryComment.java`

**Purpose:** POJO representing a comment on a weblog entry. Stores comment content, commenter information, moderation status, and metadata for spam detection and notification.

**Key Functions:**
* Standard getters/setters for: `id`, `weblogEntry`, `name`, `email`, `url`, `content`, `postTime`, `status`, `notify`, `remoteHost`, `referrer`, `userAgent`
* `getSpam()` - Returns true if status is SPAM
* `getPending()` - Returns true if status is PENDING
* `getApproved()` - Returns true if status is APPROVED
* `getTimestamp()` - Returns post time as milliseconds string for permalink generation

**Interactions:** Associated with `WeblogEntry` via foreign key, managed by `WeblogEntryManager`, used in comment moderation, spam filtering, and display, retrieved via `CommentSearchCriteria`, processed by comment plugins for spam detection.

---

## 18. WeblogEntryTag (POJO)

**File:** `WeblogEntryTag.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/pojos/WeblogEntryTag.java`

**Purpose:** POJO representing a tag associated with a weblog entry. Links entries to tags for categorization, searching, and tag cloud generation. Maintains relationship between tags, entries, weblogs, and users.

**Key Functions:**
* Standard getters/setters for: `id`, `weblog`, `weblogEntry`, `creatorUserName`, `name`, `time`
* `getUser()` - Returns User object for tag creator by looking up username

**Interactions:** Created when tags are added to `WeblogEntry`, stored in `WeblogEntry.tagSet` collection, managed by `WeblogEntryManager`, used for tag cloud generation, tag-based search, and entry filtering, aggregated in `WeblogEntryTagAggregate` for statistics.

---

## 19. WeblogEntryManager (Interface)

**File:** `WeblogEntryManager.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/business/WeblogEntryManager.java`

**Purpose:** Interface defining contract for weblog entry, category, and comment management operations. Provides methods for CRUD operations on entries, comments, categories, and tags, along with search and retrieval functionality.

**Key Functions:**
* `saveWeblogEntry(WeblogEntry entry)` - Persists new or updated entry
* `removeWeblogEntry(WeblogEntry entry)` - Deletes entry and associated data
* `getWeblogEntry(String id)` - Retrieves entry by ID
* `getWeblogEntryByAnchor(Weblog website, String anchor)` - Retrieves entry by anchor within weblog
* `getWeblogEntries(WeblogEntrySearchCriteria wesc)` - Searches entries with criteria (weblog, category, tags, status, date range, sort order, pagination)
* `getWeblogEntryObjectMap(WeblogEntrySearchCriteria wesc)` - Returns entries grouped by calendar day
* `getMostCommentedWeblogEntries(...)` - Gets entries sorted by comment count
* `getNextEntry(WeblogEntry current, String catName, String locale)` - Gets chronologically next entry
* `getPreviousEntry(WeblogEntry current, String catName, String locale)` - Gets chronologically previous entry
* `saveWeblogCategory(WeblogCategory cat)` - Persists category
* `removeWeblogCategory(WeblogCategory cat)` - Deletes category
* `saveComment(WeblogEntryComment comment)` - Persists comment
* `removeComment(WeblogEntryComment comment)` - Deletes comment
* `getComments(CommentSearchCriteria csc)` - Searches comments with criteria
* `getComment(String id)` - Retrieves comment by ID
* `createAnchor(WeblogEntry data)` - Generates unique anchor for entry

**Interactions:** Implemented by `JPAWeblogEntryManagerImpl`, accessed via `WebloggerFactory.getWeblogger().getWeblogEntryManager()`, used by `EntryEdit` action, search functionality, rendering, and all entry-related operations throughout the application.

---

## 20. JPAWeblogEntryManagerImpl (Manager Implementation)

**File:** `JPAWeblogEntryManagerImpl.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/business/jpa/JPAWeblogEntryManagerImpl.java`

**Purpose:** JPA implementation of WeblogEntryManager interface. Handles all database persistence for entries, comments, categories, and tags using JPA queries and the JPAPersistenceStrategy.

**Key Functions:**
* `saveWeblogEntry(WeblogEntry entry)` - Persists entry, creates anchor if needed, updates weblog's lastModified timestamp, manages tag aggregates, queues pings for newly published entries
* `removeWeblogEntry(WeblogEntry entry)` - Deletes entry and cascades to comments and tags, removes from search index
* `getWeblogEntry(String id)` - Loads entry by ID
* `getWeblogEntryByAnchor(Weblog website, String anchor)` - Executes named query to find entry by anchor
* `getWeblogEntries(WeblogEntrySearchCriteria wesc)` - Builds dynamic JPQL query based on search criteria with filtering by weblog, category, tags, text, status, locale, date range, and sorting
* `getWeblogEntryObjectMap(WeblogEntrySearchCriteria wesc)` - Groups entries by calendar day
* `getMostCommentedWeblogEntries(...)` - Queries entries ordered by comment count
* `saveWeblogCategory(WeblogCategory cat)` - Persists category
* `removeWeblogCategory(WeblogCategory cat)` - Deletes category after checking if in use
* `moveWeblogCategoryContents(WeblogCategory srcCat, WeblogCategory destCat)` - Moves allentries from source to destination category
* `saveComment(WeblogEntryComment comment)` - Persists comment and updates entry updateTime
* `removeComment(WeblogEntryComment comment)` - Deletes comment
* `getComments(CommentSearchCriteria csc)` - Builds dynamic query for comment search
* `createAnchor(WeblogEntry data)` - Generates URL-safe anchor from title with duplicate checking

**Interactions:** Injected as singleton via Guice, uses `JPAPersistenceStrategy` for database operations, accessed through `WebloggerFactory.getWeblogger().getWeblogEntryManager()`, collaborates with `IndexManager` for search indexing and `PingQueueManager` for ping notifications.




## 21. PageServlet (Servlet)

**File:** `PageServlet.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/servlets/PageServlet.java`

**Purpose:** Main servlet for rendering weblog pages and content. Handles all weblog page requests, performs caching, processes referrers, tracks hits, and renders pages using Velocity templates via the rendering framework.

**Key Functions:**
* `init(ServletConfig servletConfig)` - Initializes servlet, loads configuration for referrer processing
* `doGet(HttpServletRequest request, HttpServletResponse response)` - Main request handler: parses URL with `WeblogPageRequest`, checks cache (`WeblogPageCache`), loads template, creates `PageModel`, renders via `RendererManager`, processes hits and referrers
* `doPost(HttpServletRequest request, HttpServletResponse response)` - Handles POST (forwards from comment servlet)
* `processHit(Weblog weblog)` - Queues hit count update via `HitCountQueue`
* `processReferrer(HttpServletRequest request)` - Extracts and validates referrer, checks for spam, queues for tracking

**Interactions:** Entry point for all weblog page rendering, uses `WeblogRequestMapper` for URL parsing, `WeblogPageCache` for caching, `RendererManager` for template rendering, `PageModel` for data, forwards to error pages on exceptions.

---

## 22. PageModel (Model)

**File:** `PageModel.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/model/PageModel.java`

**Purpose:** Velocity template model providing page-level data and context for weblog rendering. Exposes weblog information, entry/category data, pagers for navigation, and request parameters to Velocity templates.

**Key Functions:**
* `init(Map<String, Object> initData)` - Initializes model from request data
* `getWeblog()` - Returns weblog being displayed
* `getLocale()` - Returns weblog locale
* `isPermalink()` - Checks if displaying single entry
* `isSearchResults()` - Checks if showing search results
* `getWeblogEntry()` - Returns entry for permalink pages
* `getWeblogCategory()` - Returns category from request
* `getTags()` - Returns tag list from request
* `getDeviceType()` - Returns device type (mobile or standard)
* `getWeblogEntriesPager()` / `getWeblogEntriesPager(String catArgument)` / `getWeblogEntriesPagerByTag(String tagArgument)` - Returns pager with entries grouped by day, optionally filtered by category or tags
* `getCommentForm()` - Returns comment form data (including preview)
* `getRequestParameter(String paramName)` - Gets request parameter value

**Interactions:** Created by `PageServlet`, populated with data from `WeblogPageRequest`, used in Velocity templates to display weblog pages, provides access to pagers, entries, categories, tags, and rendering context.

---

## 23. WeblogPageRequest (Request Parser)

**File:** `WeblogPageRequest.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/util/WeblogPageRequest.java`

**Purpose:** Parses incoming weblog page URLs and extracts embedded information such as weblog handle, entry anchor, date, category, tags, and custom page names. Acts as a request data holder for the rendering pipeline.

**Key Functions:**
* Constructor `WeblogPageRequest(HttpServletRequest request)` - Parses URL and extracts: weblog handle, locale, context (entry/date/category/tag/page), entry anchor, date string, category name, tags, page name, pagination parameters
* Standard getters/setters for: `context`, `weblogAnchor`, `weblogPageName`, `weblogCategoryName`, `weblogDate`, `pageNum`, `tags`, `customParams`
* `getWeblogEntry()` - Loads and returns the WeblogEntry if anchor is present
* `getWeblogPage()` - Loads and returns the WeblogTemplate for custom pages
* `isValidDateString(String dateString)` - Validates date format (YYYYMMDD, YYYYMM, or YYYY)

**Interactions:** Created by `PageServlet` from HTTP request, provides parsed URL data to `PageModel`, used throughout rendering pipeline to determine what content to display, validates request format.

---

## 24. WeblogRequestMapper (Request Router)

**File:** `WeblogRequestMapper.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/WeblogRequestMapper.java`

**Purpose:** Maps/routes incoming weblog rendering requests to the appropriate servlet. Parses URLs of the form /<weblog handle>/* and forwards to `PageServlet`, `FeedServlet`, `MediaServlet`, or `SearchServlet` based on URL structure.

**Key Functions:**
* `handleRequest(HttpServletRequest request, HttpServletResponse response)` - Main routing logic: parses URL path, extracts weblog handle, determines request type (page/feed/media/search), forwards to appropriate servlet
* `calculateForwardUrl(HttpServletRequest request, String handle, String locale, String context, String data)` - Determines target servlet URL based on context
* `isWeblog(String potentialHandle)` - Validates if string is an existing weblog handle
* `isLocale(String potentialLocale)` - Validates if string is a valid locale code

**Interactions:** Used by Roller's main request filter, performs initial URL parsing and routing, forwards to `PageServlet` for page requests, `FeedServlet` for feeds, `MediaServlet` for media files, handles 404s for invalid weblogs.

---

## 25. RendererManager (Renderer Factory)

**File:** `RendererManager.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/RendererManager.java`

**Purpose:** Provides abstraction for template rendering by managing renderer factories and selecting the appropriate renderer for templates. Allows pluggable rendering implementations (Velocity, etc.).

**Key Functions:**
* `getRenderer(Template template, DeviceType deviceType)` - Finds and returns appropriate `Renderer` for the template by consulting configured renderer factories, throws `RenderingException` if no renderer found
* Static initialization loads renderer factories from configuration (`rendering.userRendererFactories` and `rendering.rollerRendererFactories`)

**Interactions:** Called by `PageServlet` to get renderer for templates, delegates to `VelocityRendererFactory` (default), supports custom renderer factories via configuration, decouples servlets from rendering implementation.

---

## 26. WeblogEntriesPager (Interface)

**File:** `WeblogEntriesPager.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/pagers/WeblogEntriesPager.java`

**Purpose:** Interface for paging through weblog entries in different views (latest, permalink, month, day). Provides navigation links and entry collections grouped by day for template rendering.

**Key Functions:**
* `getEntries()` - Returns map of entries grouped by publication date, each value is a collection of entries for that day
* `getHomeLink()` / `getHomeName()` - Link and name for pager home
* `getNextLink()` / `getNextName()` - Link and name for next page in current view
* `getPrevLink()` / `getPrevName()` - Link and name for previous page in current view
* `getNextCollectionLink()` / `getNextCollectionName()` - Link and name for next collection (e.g., next month)
* `getPrevCollectionLink()` / `getPrevCollectionName()` - Link and name for previous collection

**Interactions:** Implemented by various pager classes (`WeblogEntriesLatestPager`, `WeblogEntriesMonthPager`, `WeblogEntriesDayPager`, `WeblogEntriesPermalinkPager`), accessed from `PageModel` in Velocity templates, provides data and navigation for blog entry display.




## 27. WeblogCategory (POJO)

**File:** `WeblogCategory.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/pojos/WeblogCategory.java`

**Purpose:** Represents a category for organizing weblog entries. Each category has a name, description, position for display ordering, and belongs to a specific weblog. Categories are used to group and filter blog posts.

**Key Functions:**
* Standard getters/setters for: `id`, `name`, `description`, `image`, `position`, `weblog`
* `calculatePosition()` - Auto-assigns display position based on existing categories
* `retrieveWeblogEntries(boolean publishedOnly)` - Gets all entries in this category
* `isInUse()` - Checks if category has any entries
* `compareTo(WeblogCategory other)` - Compares by name for sorting

**Interactions:** Assigned to `WeblogEntry` objects, managed by `WeblogEntryManager`, used in `PageModel` for category-based entry filtering, displayed in navigation menus and sidebars.

---

## 28. WeblogTemplate (POJO)

**File:** `WeblogTemplate.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/pojos/WeblogTemplate.java`

**Purpose:** Represents a user-defined custom template page for a weblog. Stores template metadata including name, link, action type, content renditions, and belongs to a specific weblog. Different from theme templates as it's user-customizable.

**Key Functions:**
* Standard getters/setters for: `id`, `name`, `description`, `link`, `action`, `lastModified`, `navbar`, `hidden`, `outputContentType`, `weblog`
* `isRequired()` - Checks if template is required (cannot be deleted)
* `isCustom()` - Checks if template is a custom user template
* `getTemplateRendition(RenditionType desiredType)` - Gets template content for specific device type
* `addTemplateRendition(CustomTemplateRendition)` - Adds new rendition for different device
* `hasTemplateRendition(CustomTemplateRendition)` - Checks if rendition exists

**Interactions:** Created and managed by `WeblogManager`, rendered by `VelocityRenderer`, selected by `PageServlet` based on request, edited via `TemplateEdit` UI action, stored with multiple renditions for responsive design.

---

## 29. WeblogManager (Interface)

**File:** `WeblogManager.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/business/WeblogManager.java`

**Purpose:** Interface for weblog and custom template management. Provides methods for creating, updating, and deleting weblogs, managing weblog ownership, querying weblogs, and handling custom templates.

**Key Functions:**
* `addWeblog(Weblog newWebsite)` - Creates new weblog with all required objects (categories, permissions)
* `saveWeblog(Weblog data)` / `removeWeblog(Weblog website)` - Save/delete weblog
* `getWeblog(String id)` / `getWeblogByHandle(String handle)` - Retrieve by ID or handle
* `getWeblogs(Boolean enabled, Boolean active, Date startDate, Date endDate, int offset, int length)` - Query weblogs with filters
* `getUserWeblogs(User user, boolean enabledOnly)` - Get user's weblogs
* `getWeblogUsers(Weblog weblog, boolean enabledOnly)` - Get weblog's users
* `getMostCommentedWeblogs(...)` - Get weblogs ordered by comment count
* `saveTemplate(WeblogTemplate)` / `removeTemplate(WeblogTemplate)` - Manage custom templates
* `getTemplate(String id)` / `getTemplateByName(Weblog, String)` / `getTemplateByAction(Weblog, ComponentType)` - Retrieve templates
* `getWeblogCount()` - Get total active weblog count

**Interactions:** Implemented by `JPAWeblogManagerImpl`, accessed via `Weblogger.getWeblogManager()`, used by weblog creation/editing UI, template management UI, and rendering subsystem.

---

## 30. FeedServlet (Servlet)

**File:** `FeedServlet.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/servlets/FeedServlet.java`

**Purpose:** Servlet responsible for rendering weblog feeds in RSS and Atom formats. Handles feed requests, manages feed caching, generates appropriate headers, and uses feed templates to render entry/comment feeds.

**Key Functions:**
* `init(ServletConfig)` - Initializes servlet and feed cache
* `doGet(HttpServletRequest, HttpServletResponse)` - Main handler: parses feed request with `WeblogFeedRequest`, checks `WeblogFeedCache` for cached feed, validates Last-Modified headers, creates `FeedModel`, renders via `RendererManager`, sets appropriate content-type

**Interactions:** Routed to by `WeblogRequestMapper`, uses `WeblogFeedCache` for caching, `WeblogFeedRequest` for URL parsing, `FeedModel` for data, `VelocityRenderer` for template rendering, supports RSS/Atom formats.

---

## 31. CommentServlet (Servlet)

**File:** `CommentServlet.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/servlets/CommentServlet.java`

**Purpose:** Handles incoming weblog comment submissions. Validates comments for spam, applies comment moderation rules, saves approved/pending comments, sends email notifications, and forwards back to entry page.

**Key Functions:**
* `init(ServletConfig)` - Initializes spam validators and comment authenticators
* `doPost(HttpServletRequest, HttpServletResponse)` - Main handler: parses comment data, validates against spam checkers, applies moderation policy, saves comment with appropriate status, queues email notifications, invalidates page cache, redirects to entry
* Spam validation using configured validators (Akismet, etc.)
* Email notification to blog owner and other commenters
* Comment moderation (auto-approve, pending, blacklist)

**Interactions:** Receives POST from comment forms in `PageServlet`, uses `WeblogCommentRequest` for parsing, `WeblogEntryManager` for saving comments, spam validators for checking, `MailUtil` for notifications, forwards back to `PageServlet`.

---

### 38. MediaFile (POJO)

**File:** `MediaFile.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/pojos/MediaFile.java`

**Purpose:** Represents an uploaded media file (image, video, audio, document) in a weblog. Stores file metadata including name, description, dimensions, copyright, tags, and binary content with thumbnails for images.

**Key Functions:**
* Standard getters/setters for: `id`, `name`, `description`, `copyrightText`, `length`, `dateUploaded`, `lastUpdated`, `directory`, `sharedForGallery`
* `getContentType()` / `setContentType(String)` - MIME type
* `getWidth()` / `getHeight()` - Image dimensions
* `getThumbnailWidth()` / `getThumbnailHeight()` - Thumbnail dimensions
* `getInputStream()` / `getThumbnailInputStream()` - Access file content
* `getTags()` / `addTag(String)` / `onRemoveTag(String)` - Tag management
* `isImageFile()` / `isVideoFile()` / `isAudioFile()` - Type checking
* `getPermalink()` - URL to access media file

**Interactions:** Belongs to `MediaFileDirectory`, managed by `MediaFileManager`, uploaded via `MediaFileAdd` UI, displayed in media chooser, embedded in blog entries, served by `MediaResourceServlet`.

---

## 32. URLModel (Model)

**File:** `URLModel.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/model/URLModel.java`

**Purpose:** Velocity template model providing URL building functionality. Generates URLs for various weblog pages, entries, categories, dates, feeds, and actions. Ensures consistent URL structure across templates.

**Key Functions:**
* `getSite()` / `getAbsoluteSite()` - Base Roller URLs
* `getLogin()` / `getLogout()` / `getRegister()` - Authentication URLs
* `home()` / `home(int pageNum)` / `home(String locale)` - Weblog home page URLs
* `entry(String anchor)` - Permalink to specific entry
* `comment(String anchor, String timeStamp)` / `comments(String anchor)` - Comment URLs
* `date(String dateString)` / `date(String dateString, int pageNum)` - Date-based archive URLs
* `category(String catName)` / `category(String catName, int pageNum)` - Category URLs
* `tag(String tag)` / `tags(List<String> tags)` - Tag-based URLs
* `feed(String type)` / `getRss()` / `getAtom()` - Feed URLs
* `search(String query)` - Search URL
* `page(String pageName)` - Custom page URL
* `themeResource(String theme, String filePath)` - Theme resource URL

**Interactions:** Available in Velocity templates as `$url`, used to generate all links in weblog pages, ensures URLs follow weblog's URL strategy, handles locale and pagination.

---

## 33. FeedModel (Model)

**File:** `FeedModel.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/model/FeedModel.java`

**Purpose:** Velocity template model providing data for feed rendering. Exposes weblog entries, comments, or media files in a format suitable for RSS/Atom feed templates, with paging and filtering.

**Key Functions:**
* `init(Map<String, Object> initData)` - Initializes from feed request
* `getWeblog()` - Returns weblog being displayed
* `getLocale()` - Returns feed locale
* `getExcerpts()` - Checks if feed shows excerpts or full content
* `getCategoryName()` - Returns category filter
* `getTags()` - Returns tag filters
* `getWeblogEntriesPager()` - Returns pager with recent entries for feed
* `getCommentsPager()` - Returns pager with recent comments for feed
* `getMediaFilesPager()` - Returns pager with recent media files for feed

**Interactions:** Created by `FeedServlet`, populated from `WeblogFeedRequest`, used in feed templates (entries.atom.vm, comments.rss.vm), provides pagers that handle feed pagination.

---

## 34. WeblogPageCache (Cache)

**File:** `WeblogPageCache.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/util/cache/WeblogPageCache.java`

**Purpose:** Caching system for rendered weblog pages. Stores HTML output of rendered pages to improve performance, uses lazy expiring entries that check modification time, generates cache keys from request parameters.

**Key Functions:**
* `getInstance()` - Returns singleton cache instance
* `get(String key, long lastModified)` - Retrieves cached page if still valid
* `put(String key, Object value)` - Stores rendered page in cache
* `remove(String key)` - Removes specific page from cache
* `clear()` - Clears entire cache
* `generateKey(WeblogPageRequest)` - Creates cache key from request (handle/context/anchor/date/category/tags/locale/pageNum/user/deviceType/params)

**Interactions:** Used by `PageServlet` to cache rendered output, invalidated by `CacheManager` when entries/comments/templates are modified, configured via `roller.properties`, improves page load performance.

---

## 35. VelocityRenderer (Renderer)

**File:** `VelocityRenderer.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/velocity/VelocityRenderer.java`

**Purpose:** Renderer implementation using Apache Velocity template engine. Loads Velocity templates, merges them with model data, handles decorators, and renders final HTML/XML output.

**Key Functions:**
* Constructor `VelocityRenderer(Template, DeviceType)` - Loads Velocity template and optional decorator
* `render(Map<String, Object> model, Writer out)` - Main rendering: converts model to Velocity context, applies decorator if present, merges template with data, writes output
* `renderException(Map, Writer, String)` - Renders error page when template has syntax errors
* Handles template parse errors gracefully
* Supports template decoration for layout wrapping

**Interactions:** Created by `VelocityRendererFactory`, used by `PageServlet` and `FeedServlet`, loads templates via `RollerVelocity`, receives model data from `PageModel` or `FeedModel`, writes output to response.

---

## 36. ThemeManager (Interface)

**File:** `ThemeManager.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/business/themes/ThemeManager.java`

**Purpose:** Interface for weblog theme management. Provides methods for loading, retrieving, and managing theme templates, handling both shared system themes and custom weblog-specific themes.

**Key Functions:**
* `getTheme(Weblog weblog)` - Gets theme for specific weblog (shared or custom)
* `getTheme(String themeId)` - Gets shared theme by ID
* `getEnabledThemesList()` - Lists all available themes
* `importTheme(Weblog weblog, String fromTheme)` - Imports theme templates to weblog
* `saveThemeCustomization(Weblog weblog, ThemeTemplate template)` - Saves customized template

**Interactions:** Implemented by `ThemeManagerImpl`, accessed via `Weblogger.getThemeManager()`, used by theme selection UI, template editor, provides templates to rendering engine.

---

## 37. EntryEdit (UI Action)

**File:** `EntryEdit.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/EntryEdit.java`

**Purpose:** Struts2 action for creating and editing weblog entries. Handles entry form submission, validation, auto-save, tag management, category assignment, and publishing workflow.

**Key Functions:**
* `execute()` - Main action: loads or creates entry, populates form bean
* `save()` - Saves entry with validation, handles tags, category, status
* `publish()` - Publishes entry immediately
* `autoSave()` - Auto-saves draft via AJAX
* `loadEntry()` - Loads existing entry for editing
* `prepareEntry()` - Prepares new entry with defaults
* Tag parsing and validation
* Trackback/ping queue management on publish

**Interactions:** Uses `EntryBean` for form data, `WeblogEntryManager` for persistence, `PingQueueManager` for pings, redirects to entry list on success.

---

## 38. Categories (UI Action)

**File:** `Categories.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/Categories.java`

**Purpose:** Struts2 action for managing weblog categories. Displays category list, handles category creation, editing, deletion, and reordering.

**Key Functions:**
* `execute()` - Displays category management page
* `getCategories()` - Returns sorted list of categories
* Category drag-and-drop reordering support
* Used with `CategoryEdit` and `CategoryRemove` actions

**Interactions:** Uses `WeblogEntryManager` to fetch categories, works with `CategoryEdit` for individual category management, displays in category admin UI.

---

## 39. Comments (UI Action)

**File:** `Comments.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/Comments.java`

**Purpose:** Struts2 action for managing weblog comments. Displays pending/approved/spam comments, handles bulk operations (approve/spam/delete), provides comment moderation interface.

**Key Functions:**
* `execute()` - Displays comment management page
* `query()` - Searches comments with filters (status, entry, date range)
* `update()` - Bulk updates comment status (approve/spam/delete)
* `delete()` / `deleteSelected()` - Deletes individual or multiple comments
* Uses `CommentsBean` for search criteria
* Pagination support

**Interactions:** Uses `WeblogEntryManager` for comment operations, `CommentsBean` for search filters, invalidates cache on updates, sends notifications on approval.

---

## 40. Templates (UI Action)

**File:** `Templates.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/Templates.java`

**Purpose:** Struts2 action for managing weblog templates. Lists custom and shared templates, handles template creation, deletion, and provides template selector for theme customization.

**Key Functions:**
* `execute()` - Displays template management page
* `getTemplates()` - Returns list of weblog templates
* Template type categorization (required, custom, shared)
* Integration with theme system

**Interactions:** Uses `WeblogManager` for template operations, `ThemeManager` for theme templates, works with `TemplateEdit` for individual template editing.

---

## 41. TemplateEdit (UI Action)

**File:** `TemplateEdit.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/TemplateEdit.java`

**Purpose:** Struts2 action for editing custom weblog templates. Provides code editor interface for template content, handles template validation, saving, and preview functionality.

**Key Functions:**
* `execute()` - Displays template editor
* `save()` - Saves template content with validation
* `revert()` - Reverts to theme template
* Syntax highlighting support
* Template preview via `PreviewServlet`

**Interactions:** Uses `WeblogManager` for template persistence, `TemplateEditBean` for form data, validates Velocity syntax, invalidates page cache on save.

---

## 42. WeblogConfig (UI Action)

**File:** `WeblogConfig.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/WeblogConfig.java`

**Purpose:** Struts2 action for configuring weblog settings. Handles weblog metadata, appearance settings, comment policies, spam filters, and feature toggles.

**Key Functions:**
* `execute()` - Displays weblog configuration page
* `save()` - Saves weblog configuration
* Uses `WeblogConfigBean` for form data
* Settings include: name, description, locale, timezone, theme, comment moderation, trackback policy, etc.

**Interactions:** Uses `WeblogManager` for saving configuration, `ThemeManager` for theme selection, config changes may trigger cache invalidation and reindexing.

---

## 43. PreviewServlet (Servlet)

**File:** `PreviewServlet.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/servlets/PreviewServlet.java`

**Purpose:** Servlet for previewing unpublished entries and template changes. Allows users to see how content will look before publishing without affecting live site.

**Key Functions:**
* `doGet(HttpServletRequest, HttpServletResponse)` - Handles preview: loads unpublished entry or modified template, uses `PreviewPageModel`, renders preview
* No caching for preview content
* Access restricted to authenticated users
* Shows draft entries and pending templates

**Interactions:** Similar to `PageServlet` but uses `WeblogPreviewRequest`, `PreviewPageModel`, loads draft entries, bypasses caching, used from entry/template editors.

---

## 44. WeblogFeedRequest (Request Parser)

**File:** `WeblogFeedRequest.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/util/WeblogFeedRequest.java`

**Purpose:** Parses incoming feed request URLs and extracts feed parameters including format (RSS/Atom), feed type (entries/comments), category, tags, and excerpt mode.

**Key Functions:**
* Constructor `WeblogFeedRequest(HttpServletRequest)` - Parses feed URL
* Standard getters for: `format`, `feedType`, `weblogCategoryName`, `tags`, `excerpts`, `page`
* Validates feed format and type
* Handles feed-specific URL patterns

**Interactions:** Created by `FeedServlet`, provides data to `FeedModel`, validates request parameters, determines feed template to use.

---

## 45. ThemeEdit (UI Action)

**File:** `ThemeEdit.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/ThemeEdit.java`

**Purpose:** Struts2 action for changing weblog theme. Displays available themes, handles theme selection, theme import/customization, and theme preview.

**Key Functions:**
* `execute()` - Displays theme selector
* `save()` - Applies selected theme to weblog
* `preview()` - Shows theme preview
* Theme import copies templates to weblog
* Theme switching with backup option

**Interactions:** Uses `ThemeManager` to list themes and import, `WeblogManager` to save theme selection, invalidates all caches on theme change, may trigger template customization.

---

## 46. WeblogCommentRequest (Request Parser)

**File:** `WeblogCommentRequest.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/util/WeblogCommentRequest.java`

**Purpose:** Parses incoming comment submission requests. Extracts comment data, validates request parameters, loads associated weblog entry for comment attachment.

**Key Functions:**
* Constructor `WeblogCommentRequest(HttpServletRequest)` - Parses comment POST data
* Getters for comment fields: `name`, `email`, `url`, `content`, `notify`
* `getWeblogEntry()` - Loads entry being commented on
* CAPTCHA/authentication token validation

**Interactions:** Created by `CommentServlet`, provides data for comment creation, validates required fields, loads `WeblogEntry` for comment association.

---

## 47. CalendarModel (Model)

**File:** `CalendarModel.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/model/CalendarModel.java`

**Purpose:** Velocity template model providing calendar data for calendar widgets. Generates monthly calendar with entry publication indicators, handles month navigation.

**Key Functions:**
* `init(Map<String, Object> initData)` - Initializes for current month
* `getCalendar()` - Returns calendar grid data
* `getDays()` - Returns list of days with entry indicators
* `getNextMonth()` / `getPrevMonth()` - Navigation URLs
* `getMonthName()` / `getYear()` - Display labels

**Interactions:** Available in Velocity templates as `$calendar`, uses `WeblogEntryManager` to find entry publication dates, generates calendar HTML structure.

---

## 48. ConfigModel (Model)

**File:** `ConfigModel.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/model/ConfigModel.java`

**Purpose:** Velocity template model exposing weblog configuration and system settings. Provides access to weblog properties, Roller configuration, and feature flags.

**Key Functions:**
* `init(Map<String, Object> initData)` - Loads weblog configuration
* `get(String key)` - Gets Roller configuration property
* Access to weblog settings (comment policy, trackback, etc.)
* Feature flag checking
* System-wide configuration access

**Interactions:** Available in Velocity templates as `$config`, provides read-only access to configuration, used for conditional rendering based on settings.

---

## 49. UtilitiesModel (Model)

**File:** `UtilitiesModel.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/model/UtilitiesModel.java`

**Purpose:** Velocity template model providing utility functions for text formatting, date formatting, URL encoding, HTML escaping, and other template helpers.

**Key Functions:**
* `truncate(String text, int maxLength)` - Truncates text
* `htmlEscape(String text)` - Escapes HTML entities
* `urlEncode(String text)` - URL encodes text
* `formatDate(Date date, String format)` - Formats dates
* `removeHTML(String text)` - Strips HTML tags
* `truncateNicely(String text, int maxLength)` - Word-aware truncation

**Interactions:** Available in Velocity templates as `$utils`, provides string/date manipulation, used throughout templates for formatting.

---

## 50. MenuModel (Model)

**File:** `MenuModel.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/model/MenuModel.java`

**Purpose:** Velocity template model providing navigation menu data. Generates custom page menus, category menus, and navigation links for weblog templates.

**Key Functions:**
* `init(Map<String, Object> initData)` - Initializes menu data
* `getPages()` - Returns list of custom pages for menu
* `getCategories()` - Returns category list for menu
* Menu item filtering (hidden pages excluded)
* Active page/category detection

**Interactions:** Available in Velocity templates as `$menu`, uses `WeblogManager` for pages, `WeblogEntryManager` for categories, provides navigation structure.

---

## 51. WeblogEntryCommentForm (Form Data)

**File:** `WeblogEntryCommentForm.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/util/WeblogEntryCommentForm.java`

**Purpose:** Data holder for comment form state and preview. Stores commenter information, comment content, preview HTML, and validation errors for display in templates.

**Key Functions:**
* Standard getters/setters for: `name`, `email`, `url`, `content`, `notify`, `preview`
* `getPreviewContent()` - Returns formatted comment preview HTML
* Validation error storage  
* Form repopulation after errors

**Interactions:** Created by `PageModel`, populated from request parameters, used by `CommentServlet` for submission, displayed in page templates for comment form.

---

## 52. ResourceServlet (Servlet)

**File:** `ResourceServlet.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/servlets/ResourceServlet.java`

**Purpose:** Servlet for serving theme resources (CSS, JavaScript, images) from shared themes. Streams theme assets with appropriate content-types and caching headers.

**Key Functions:**
* `doGet(HttpServletRequest, HttpServletResponse)` - Serves theme resource: parses path, loads from theme, sets content-type and cache headers, streams content
* Theme resource loading
* Content-type detection
* Browser caching

**Interactions:** Serves resources for shared themes, uses `ThemeManager` to load resources, provides CSS/JS/images for theme templates, implements aggressive caching.

---

## 53. ModelLoader (Utility)

**File:** `ModelLoader.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/model/ModelLoader.java`

**Purpose:** Utility for loading and initializing template models. Dynamically instantiates model classes, passes initialization data, and makes models available to templates.

**Key Functions:**
* `loadModel(String modelName, Map<String, Object> initData)` - Loads and initializes model by name
* Model registration and discovery
* Model initialization with request context

**Interactions:** Used by servlets to load models (`PageModel`, `FeedModel`, etc.), ensures models are properly initialized, handles model lifecycle.

---

## 54. ModDateHeaderUtil (Utility)

**File:** `ModDateHeaderUtil.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/util/ModDateHeaderUtil.java`

**Purpose:** Utility for handling HTTP modification date headers (If-Modified-Since, Last-Modified). Implements browser cache validation for pages and feeds.

**Key Functions:**
* `respondIfNotModified(HttpServletRequest, HttpServletResponse, long lastModified)` - Checks If-Modified-Since header, returns 304 Not Modified if appropriate
* `setLastModifiedHeader(HttpServletResponse, long lastModified)` - Sets Last-Modified header
* Date parsing and formatting

**Interactions:** Used by `PageServlet`, `FeedServlet`, and resource servlets for caching, improves performance by avoiding re-rendering unchanged content.

---

## 55. PreviewPageModel (Model)

**File:** `PreviewPageModel.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/model/PreviewPageModel.java`

**Purpose:** Specialized page model for entry/template previews. Extends `PageModel` to include unpublished entries and modified templates for preview rendering.

**Key Functions:**
* Inherits from `PageModel`
* `init(Map<String, Object> initData)` - Loads draft/unpublished content
* Includes entries in any status (not just published)
* Uses modified template code for preview
* No caching

**Interactions:** Created by `PreviewServlet`, similar to `PageModel` but allows preview of drafts, used from entry editor preview and template editor.

---

## 56. PreviewURLModel (Model)

**File:** `PreviewURLModel.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/model/PreviewURLModel.java`

**Purpose:** URL building model for preview mode. Generates preview URLs that include authentication and preview parameters to view unpublished content.

**Key Functions:**
* Extends URLModel functionality
* Adds preview mode parameters to generated URLs
* Ensures preview links stay in preview mode
* Authentication token handling

**Interactions:** Used in preview templates, ensures links maintain preview context, allows navigation through draft content.

---

## 57. WeblogPreviewRequest (Request Parser)

**File:** `WeblogPreviewRequest.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/util/WeblogPreviewRequest.java`

**Purpose:** Parses preview request URLs. Similar to`WeblogPageRequest` but handles preview-specific parameters and authentication requirements.

**Key Functions:**
* Extends `WeblogPageRequest` parsing
* Validates preview authentication
* Handles preview entry IDs
* Loads draft entries

**Interactions:** Created by `PreviewServlet`, similar to `WeblogPageRequest`, provides access to unpublished content for authorized users.

---

## 58. WeblogFeedCache (Cache)

**File:** `WeblogFeedCache.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/util/cache/WeblogFeedCache.java`

**Purpose:** Caching system for rendered weblog feeds. Stores generated RSS/Atom XML, uses lazy expiration similar to page cache, improves feed performance.

**Key Functions:**
* Similar to `WeblogPageCache` 
* `get(String key, long lastModified)` / `put(String key, Object value)` - Cache operations
* `generateKey(WeblogFeedRequest)` - Creates cache key from feed request
* Feed-specific cache invalidation

**Interactions:** Used by `FeedServlet`, invalidated when entries/comments change, configured separately from page cache, stores XML output.

---

## 59. CustomTemplateRendition (POJO)

**File:** `CustomTemplateRendition.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/pojos/CustomTemplateRendition.java`

**Purpose:** Represents a specific device rendition of a custom template. Stores template content for different device types (standard, mobile) to support responsive design.

**Key Functions:**
* Standard getters/setters for: `id`, `template`, `type` (STANDARD/MOBILE), `templateLanguage`
* `getTemplate()` - Returns template code
* Device-type enumeration

**Interactions:** Belongs to `WeblogTemplate`, managed by `WeblogManager`, selected by `RendererManager` based on device type, allows different layouts per device.

---

## 60. EntryBean (Form Data)

**File:** `EntryBean.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/EntryBean.java`

**Purpose:** Form backing bean for entry creation/editing. Holds entry form data including title, content, category, tags, status, and publishing options.

**Key Functions:**
* Standard getters/setters for all entry fields
* `tagsAsString` - Comma-separated tag list
* `allowComments`, `allowTrackbacks` - Comment/trackback toggles
* `pubTime` - Scheduled publication time
* Form validation support

**Interactions:** Used by `EntryEdit` action, populated from form submission, converted to `WeblogEntry` for persistence, repopulates form on validation errors.

---

## 61. CategoryBean (Form Data)

**File:** `CategoryBean.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/CategoryBean.java`

**Purpose:** Form backing bean for category creation/editing. Holds category form data for name, description, and image.

**Key Functions:**
* Standard getters/setters for: `id`, `name`, `description`, `image`
* Form validation

**Interactions:** Used by `CategoryEdit` action, populated from form, converted to `WeblogCategory` for persistence.

---

## 62. WeblogConfigBean (Form Data)

**File:** `WeblogConfigBean.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/WeblogConfigBean.java`

**Purpose:** Form backing bean for weblog configuration. Holds all weblog settings including metadata, appearance, policies, and feature flags.

**Key Functions:**
* Standard getters/setters for: `name`, `description`, `handle`, `locale`, `timezone`, `theme`, `entryDisplayCount`, `commentPolicy`, `spamPolicy`, etc.
* Boolean flags for features (comments, trackbacks, etc.)
* Validation rules

**Interactions:** Used by `WeblogConfig` action, populated from form, applied to `Weblog` object for persistence, extensive validation.

---

## 63. TemplateEditBean (Form Data)

**File:** `TemplateEditBean.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/TemplateEditBean.java`

**Purpose:** Form backing bean for template editing. Holds template code, metadata, and rendition type for template editor.

**Key Functions:**
* Standard getters/setters for: `id`, `name`, `description`, `link`, `contents`, `type` (rendition type)
* Template code storage  
* Action type (weblog/permalink/etc.)

**Interactions:** Used by `TemplateEdit` action, holds template code for editing, converts to `WeblogTemplate` for saving.

---

## 64. CommentsBean (Form/Search Data)

**File:** `CommentsBean.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/struts2/editor/CommentsBean.java`

**Purpose:** Form and search criteria bean for comment management. Holds filters for searching comments (status, entry, date range) and selection state.

**Key Functions:**
* Search criteria getters/setters: `status`, `entryId`, `startDate`, `endDate`, `searchString`
* Selected comment IDs for bulk operations
* Results page number

**Interactions:** Used by `Comments` action, provides search criteria to `WeblogEntryManager`, holds selection state for bulk approve/delete/spam operations.

---

## 65. CommentsPager (Pager)

**File:** `CommentsPager.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/pagers/CommentsPager.java`

**Purpose:** Pager interface for browsing comments in feeds. Provides pagination for comment feeds.

**Key Functions:**
* `getComments()` - Returns paged list of comments
* Standard pager navigation methods
* Comment count

**Interactions:** Used by `FeedModel` for comment feeds, provides pagination for comment RSS/Atom feeds.

---

## 66. WeblogWrapper (Wrapper)

**File:** `WeblogWrapper.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/pojos/wrapper/WeblogWrapper.java`

**Purpose:** Wrapper class for `Weblog` POJO providing read-only access to weblog properties in templates. Prevents template code from modifying weblog data.

**Key Functions:**
* All `Weblog` getter methods as read-only
* No setters exposed
* Wraps related objects (entries, categories) in their own wrappers

**Interactions:** Exposed to Velocity templates instead of raw POJOs, ensures templates cannot modify data, provides security boundary.

---

## 67. WeblogEntryWrapper (Wrapper)

**File:** `WeblogEntryWrapper.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/pojos/wrapper/WeblogEntryWrapper.java`

**Purpose:** Wrapper class for `WeblogEntry` POJO providing read-only access to entry properties in templates. Prevents template modifications.

**Key Functions:**
* All `WeblogEntry` getter methods as read-only
* No setters exposed
* Wraps related objects

**Interactions:** Exposed to Velocity templates, returned by pagers, ensures template safety.

---

## 68. WeblogEntryCommentWrapper (Wrapper)

**File:** `WeblogEntryCommentWrapper.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/pojos/wrapper/WeblogEntryCommentWrapper.java`

**Purpose:** Wrapper class for `WeblogEntryComment` POJO providing read-only access to comment properties in templates.

**Key Functions:**
* All `WeblogEntryComment` getter methods as read-only
* No setters exposed to templates

**Interactions:** Exposed in Velocity templates for comment display, ensures comments cannot be modified from templates.

---

## 69. VelocityRendererFactory (Factory)

**File:** `VelocityRendererFactory.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/velocity/VelocityRendererFactory.java`

**Purpose:** Factory for creating `VelocityRenderer` instances. Implements `RendererFactory` interface to provide Velocity rendering capability to the rendering system.

**Key Functions:**
* `getRenderer(Template, DeviceType)` - Creates `VelocityRenderer` for template
* Checks if template is Velocity-based
* Returns null for non-Velocity templates

**Interactions:** Registered with `RendererManager`, consulted when renderer needed, instantiates `VelocityRenderer` with proper template and device type.

---

## 70. RollerVelocity (Utility)

**File:** `RollerVelocity.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/velocity/RollerVelocity.java`

**Purpose:** Velocity engine initialization and configuration for Roller. Sets up Velocity with custom resource loaders for theme and database templates.

**Key Functions:**
* `getTemplate(String templateId, DeviceType, String encoding)` - Loads Velocity template
* Velocity engine initialization
* Custom resource loader configuration (theme, database, file system)
* Template caching configuration

**Interactions:** Used by `VelocityRenderer` to load templates, configures Velocity engine on startup, provides template access from themes and database.

---

## 71. ThemeResourceLoader (Resource Loader)

**File:** `ThemeResourceLoader.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/velocity/ThemeResourceLoader.java`

**Purpose:** Velocity resource loader for loading templates from shared themes. Allows Velocity to load templates from theme directories.

**Key Functions:**
* `getResourceStream(String name)` - Loads template from theme
* `isSourceModified(Resource)` - Checks if theme template changed
* Theme template discovery

**Interactions:** Registered with Velocity engine, loads templates from shared themes, works with `ThemeManager` to access theme resources.

---

## 72. RollerResourceLoader (Resource Loader)

**File:** `RollerResourceLoader.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/velocity/RollerResourceLoader.java`

**Purpose:** Velocity resource loader for loading custom templates from database. Allows Velocity to render user-customized templates stored in database.

**Key Functions:**
* `getResourceStream(String templateId)` - Loads template from database
* `isSourceModified(Resource)` - Checks if template was modified
* Database template caching

**Interactions:** Registered with Velocity engine, loads `WeblogTemplate` content from database, enables custom template rendering.

---

## 73. WebappResourceLoader (Resource Loader)

**File:** `WebappResourceLoader.java`

**Location:** `app/src/main/java/org/apache/roller/weblogger/ui/rendering/velocity/WebappResourceLoader.java`

**Purpose:** Velocity resource loader for loading templates from web application resources. Loads built-in system templates like error pages.

**Key Functions:**
* `getResourceStream(String name)` - Loads template from webapp
* System template access (error pages, etc.)

**Interactions:** Registered with Velocity engine, loads error page templates and other system templates from webapp resources.

---

This completes the comprehensive documentation of **73 classes** in the Weblog and Content Subsystem (indices 33-98), excluding search subsystem classes.


