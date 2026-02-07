# Complete Subsystem Documentation: User and Role Management

Complete documentation for all Java classes and interfaces responsible for managing users, authentication, authorization, and permissions.

---

## Table of Contents

1. [Overview](#overview)
2. [User Registration & Profile Management](#user-registration--profile-management)
3. [User Administration](#user-administration)
4. [Permission Management](#permission-management)
5. [Domain Model & Entities](#domain-model--entities)
6. [Business Logic Layer](#business-logic-layer)
7. [Complete Class Diagram (PlantUML)](#complete-class-diagram-plantuml)
8. [Interaction Flows](#interaction-flows)
9. [Security Architecture](#security-architecture)

---

## Overview

The User and Role Management subsystem consists of **27 key classes/interfaces** across multiple functional areas:

**User Registration & Profile (3 classes):**
- Register, Profile, ProfileBean (shared by both)

**User Administration (4 classes):**
- UserAdmin, UserEdit, CreateUserBean, MembersInvite

**Permission Management (8 classes):**
- RollerPermission, GlobalPermission, ObjectPermission, WeblogPermission, UserRole, JPAUserManagerImpl (permission methods), WebloggerFactory, UserWrapper

**Domain Model (3 entities):**
- User, WeblogPermission, UserRole

**Business Layer (3 interfaces/implementations):**
- UserManager, JPAUserManagerImpl, JPAPersistenceStrategy

---

## User Registration & Profile Management

### 1. Register (Action)
**File**: `org.apache.roller.weblogger.ui.struts2.core.Register`

**Purpose**: Controller for user self-registration workflow

**Key Methods**:
- `execute()`: Display registration form
- `save()`: Process registration submission
- `activate()`: Handle email verification

**Registration Flow**:
```java
1. User fills registration form
2. Validate username uniqueness
3. Validate email format
4. Check password strength
5. Create User entity
6. Generate activation code
7. Send verification email
8. Store with enabled=false
9. Wait for email confirmation
```

**Validation Rules**:
- Username: 3-30 chars, alphanumeric + underscore
- Email: Valid format, unique
- Password: Minimum 8 chars, complexity rules
- Screen name: Required, sanitized

**Interactions**: 
- Uses: UserManager, MailUtil
- Creates: User entity
- Validates: ProfileBean

**Note**: Register uses ProfileBean (not a separate RegistrationBean class)

---

### 2. Profile (Action)
**File**: `org.apache.roller.weblogger.ui.struts2.core.Profile`

**Purpose**: Controller for user profile editing

**Key Methods**:
- `execute()`: Display profile form (pre-filled)
- `save()`: Update profile information
- `changePassword()`: Handle password change

**Editable Fields**:
- Screen name
- Full name
- Email address
- Locale
- Time zone
- OpenID URL

**Security**:
- User can only edit own profile
- Password change requires old password
- Email change may require re-verification

**Interactions**:
- Uses: UserManager
- Updates: User entity
- Validates: ProfileBean

---

### 3. ProfileBean (DTO)
**File**: `org.apache.roller.weblogger.ui.struts2.core.ProfileBean`

**Purpose**: Holds both registration and profile form data (shared by Register and Profile actions)

**Properties**:
```java
- id: String
- userName: String
- password: String
- screenName: String
- fullName: String
- emailAddress: String
- locale: String
- timeZone: String
- openIdUrl: String
- passwordText: String (for password input)
- passwordConfirm: String (for password confirmation)
```

**Methods**:
- `copyFrom(User user)`: Load from entity
- `copyTo(User user)`: Save to entity

---

## User Administration

### 4. UserAdmin (Action)
**File**: `org.apache.roller.weblogger.ui.struts2.admin.UserAdmin`

**Purpose**: Controller for administrative user management (list/search users)

**Key Methods**:
- `execute()`: Display user list
- `search()`: Search by username/email
- `toggleEnabled()`: Enable/disable accounts
- `delete()`: Remove user accounts

**Features**:
- Paginated user list
- Search by username pattern
- Filter by enabled status
- View user statistics
- Bulk operations

**Permissions Required**: Global ADMIN role

**Interactions**:
- Uses: UserManager
- Manages: User entities
- Validates: CreateUserBean

**Note**: UserAdmin uses CreateUserBean (no separate UserAdminBean exists)

---

### 5. UserEdit (Action)
**File**: `org.apache.roller.weblogger.ui.struts2.admin.UserEdit`

**Purpose**: Controller for editing existing user accounts (admin view)

**Key Methods**:
- `execute()`: Load user for editing
- `save()`: Update user data
- `grantRole()`: Assign global roles
- `revokeRole()`: Remove global roles

**Admin Capabilities**:
- Edit any user field
- Reset passwords
- Enable/disable accounts
- Manage global roles
- View permissions

**Interactions**:
- Uses: UserManager
- Updates: User, UserRole entities

---

### 6. Members (Action)
**File**: `org.apache.roller.weblogger.ui.struts2.editor.Members`

**Purpose**: Controller for managing weblog member permissions

**Key Methods**:
- `execute()`: Display members list
- `save()`: Update member permissions

**Features**:
- List all blog members
- Update permissions for existing members
- Remove members from blog
- Uses HttpParameters directly (no bean class)

**Permissions Required**: ADMIN permission on weblog

**Interactions**:
- Uses: UserManager, WeblogManager
- Manages: WeblogPermission entities

---

### 7. MembersInvite (Action)
**File**: `org.apache.roller.weblogger.ui.struts2.editor.MembersInvite`

**Purpose**: Controller for inviting new members to a weblog

**Key Methods**:
- `execute()`: Display invitation form
- `save()`: Send invitation to user
- `cancel()`: Cancel invitation

**Properties**:
```java
- userName: String (user to invite)
- permissionString: String (permissions to grant)
```

**Interactions**:
- Uses: UserManager, MailUtil
- Creates: WeblogPermission (pending)
- Sends: Email invitation

**Note**: No bean class; uses simple String fields

---

### 8. CreateUserBean (DTO)
**File**: `org.apache.roller.weblogger.ui.struts2.admin.CreateUserBean`

**Purpose**: Holds user creation data (admin-initiated)

**Properties**:
```java
- id: String
- userName: String
- password: String
- screenName: String
- fullName: String
- emailAddress: String
- locale: String
- timeZone: String
- openIdUrl: String
- enabled: Boolean (admin sets directly)
- activationCode: String
- administrator: boolean
- list: List<String> (roles/permissions)
```

**Methods**:
- `copyFrom(User user)`: Load from entity
- `copyTo(User user)`: Save to entity
- `hasRequiredGlobalPermissionActions(User user)`: Check admin permissions

---

## Permission Management

### 9. RollerPermission (Abstract Base Class)
**File**: `org.apache.roller.weblogger.pojos.RollerPermission`

**Purpose**: Base class for all permission types

**Type**: Abstract class extending `java.security.Permission`

**Key Attributes**:
```java
protected String actions;  // Comma-separated
```

**Key Methods**:
```java
public List<String> getActionsAsList()
public void setActionsAsList(List<String> actions)
public boolean hasAction(String action)
public boolean hasActions(List<String> actions)
public void addActions(List<String> newActions)
public void removeActions(List<String> actionsToRemove)
```

**Design Pattern**: Template Method - defines structure for permission checking

**Subclasses**: GlobalPermission, ObjectPermission (→ WeblogPermission)

---

### 10. GlobalPermission (Concrete)
**File**: `org.apache.roller.weblogger.pojos.GlobalPermission`

**Purpose**: System-wide permissions

**Permission Types**:
```java
public static final String LOGIN  = "login";   // Basic access
public static final String WEBLOG = "weblog";  // Create blogs
public static final String ADMIN  = "admin";   // System admin
```

**Permission Hierarchy**:
```
ADMIN > WEBLOG > LOGIN
```

**Constructors**:
```java
// Derive from user's roles
GlobalPermission(User user)

// Explicit actions
GlobalPermission(List<String> actions)

// User + explicit actions
GlobalPermission(User user, List<String> actions)
```

**Usage Example**:
```java
// Check if user is admin
GlobalPermission adminPerm = new GlobalPermission(
    Collections.singletonList("admin")
);
boolean isAdmin = userManager.checkPermission(adminPerm, user);
```

**Configuration Mapping**:
```properties
# roller.properties
role.action.admin=login,weblog,admin
role.action.editor=login,weblog
role.action.user=login
```

---

### 11. ObjectPermission (Abstract)
**File**: `org.apache.roller.weblogger.pojos.ObjectPermission`

**Purpose**: Base for permissions on specific objects

**Extends**: RollerPermission

**Additional Attributes**:
```java
protected String id;              // UUID
protected String userName;        // User granted to
protected String objectType;      // "Weblog", etc.
protected String objectId;        // ID of object
protected boolean pending;        // Invitation pending
protected Date dateCreated;       // Grant timestamp
```

**Pending Workflow**:
```
1. Owner invites user (pending=true)
2. Email sent to invited user
3. User accepts → pending=false
4. User declines → permission deleted
```

---

### 12. WeblogPermission (Entity)
**File**: `org.apache.roller.weblogger.pojos.WeblogPermission`

**Purpose**: User permissions for specific weblogs

**Extends**: ObjectPermission

**Implements**: Serializable

**Permission Actions**:
```java
public static final String EDIT_DRAFT = "edit_draft";  // Edit drafts only
public static final String POST = "post";              // Create & publish
public static final String ADMIN = "admin";            // Full blog control
```

**Action Hierarchy**:
```
ADMIN > POST > EDIT_DRAFT
```

**Constructors**:
```java
// For specific user on weblog
WeblogPermission(Weblog weblog, User user, List<String> actions)

// For weblog-wide check (no user)
WeblogPermission(Weblog weblog, List<String> actions)
```

**Key Methods**:
```java
public Weblog getWeblog()           // Retrieve weblog
public User getUser()               // Retrieve user
public boolean implies(Permission)  // Check permission
```

**Database Schema**:
```sql
CREATE TABLE weblogpermission (
    id VARCHAR(48) PRIMARY KEY,
    username VARCHAR(48) NOT NULL,
    weblog_id VARCHAR(48) NOT NULL,
    actions VARCHAR(255),
    pending BOOLEAN DEFAULT FALSE,
    datecreated TIMESTAMP,
    FOREIGN KEY (username) REFERENCES roller_user(username),
    FOREIGN KEY (weblog_id) REFERENCES weblog(id)
);
```

---

### 13. UserRole (Entity)
**File**: `org.apache.roller.weblogger.pojos.UserRole`

**Purpose**: Legacy global role assignments

**Type**: JPA Entity

**Status**: Deprecated (transitioning to GlobalPermission)

**Attributes**:
```java
private String id;         // UUID
private String userName;   // User's username
private String role;       // Role name
```

**Common Roles**:
- `admin` - System administrator
- `editor` - Blog creator
- `user` - Basic user

**Why Deprecated?**
- Less granular than action-based permissions
- No object-level permissions
- No pending invitation support
- Harder to audit

**Still Used For**:
- Spring Security integration
- Backward compatibility
- Configuration mapping

---

## Domain Model & Entities

### 14. User (Entity)
**File**: `org.apache.roller.weblogger.pojos.User`

**Purpose**: User account entity

**Type**: JPA Entity / POJO

**Implements**: Serializable

**Key Attributes**:
```java
@Id
private String id;                // UUID primary key

@Column(unique=true, nullable=false)
private String userName;          // Login name

@Column(nullable=false)
private String password;          // BCrypt hash

private String openIdUrl;         // OpenID authentication
private String screenName;        // Display name
private String fullName;          // Real name
private String emailAddress;      // Contact email
private Date dateCreated;         // Registration date
private String locale;            // Language (e.g., "en_US")
private String timeZone;          // Timezone (e.g., "America/New_York")
private Boolean enabled;          // Account active?
private String activationCode;    // Email verification token
```

**Constructor**:
```java
public User() {
    this.id = UUIDGenerator.generateUUID();
    this.enabled = Boolean.TRUE;
}
```

**Security Methods**:
```java
// Encrypt and set password
public void resetPassword(String newPassword) {
    PasswordEncoder encoder = RollerContext.getPasswordEncoder();
    setPassword(encoder.encode(newPassword));
}

// Check global permissions
public boolean hasGlobalPermission(String action)
public boolean hasGlobalPermissions(List<String> actions)
```

**Input Sanitization**:
All setters use `HTMLSanitizer.conditionallySanitize()` to prevent XSS:
```java
public void setScreenName(String screenName) {
    this.screenName = HTMLSanitizer.conditionallySanitize(screenName);
}
```

**Defensive Copying**:
```java
public Date getDateCreated() {
    return (Date) dateCreated.clone();  // Prevent external modification
}
```

**Relationships**:
- OneToMany → Weblog (blogs created)
- OneToMany → WeblogPermission (blog access)
- OneToMany → UserRole (global roles)

---

## Authentication & Security Layer

### 15. RollerUserDetailsService
**File**: `org.apache.roller.weblogger.ui.core.security.RollerUserDetailsService`

**Purpose**: Spring Security UserDetailsService implementation that loads user authentication data from Roller's database

**Type**: Service Class

**Implements**: `org.springframework.security.core.userdetails.UserDetailsService`

**Key Responsibilities**:
- Bridges Spring Security authentication with Roller's user management
- Loads user details for both standard username/password and OpenID authentication
- Converts Roller `User` entities to Spring Security `UserDetails`
- Populates user authorities (roles) from UserManager

**Key Methods**:
```java
public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    // Get Roller instance
    Weblogger roller = WebloggerFactory.getWeblogger();
    UserManager umgr = roller.getUserManager();
    
    User userData;
    
    // Check if OpenID URL (starts with http:// or https://)
    if (userName.startsWith("http://") || userName.startsWith("https://")) {
        // Remove trailing slash
        if (userName.endsWith("/")) {
            userName = userName.substring(0, userName.length() - 1);
        }
        userData = umgr.getUserByOpenIdUrl(userName);
        
        // Allow OpenID flow even if user not found (for SREG data)
        if (userData == null) {
            return new User("openid", "openid", 
                Collections.singletonList(new SimpleGrantedAuthority("rollerOpenidLogin")));
        }
    } else {
        // Standard username/password authentication
        userData = umgr.getUserByUserName(userName);
        if (userData == null) {
            throw new UsernameNotFoundException("User not found: " + userName);
        }
    }
    
    // Get user's roles and convert to authorities
    List<SimpleGrantedAuthority> authorities = getAuthorities(userData, umgr);
    
    return new org.springframework.security.core.userdetails.User(
        userData.getUserName(),
        userData.getPassword(),
        userData.getEnabled(),
        true, true, true,
        authorities
    );
}

private List<SimpleGrantedAuthority> getAuthorities(User user, UserManager umgr) {
    List<String> roles = umgr.getRoles(user);
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    for (String role : roles) {
        authorities.add(new SimpleGrantedAuthority(role));
    }
    return authorities;
}
```

**Authentication Flow**:
```
1. Spring Security requests user details by username
2. Check if username is OpenID URL or standard username
3. Query UserManager for user data
4. Retrieve user's roles from database
5. Convert roles to Spring Security authorities
6. Return UserDetails with credentials and authorities
```

**Integration**:
- Uses: UserManager, WebloggerFactory
- Called by: Spring Security authentication provider
- Returns: Spring Security UserDetails

---

### 16. RollerUserDetails
**File**: `org.apache.roller.weblogger.ui.core.security.RollerUserDetails`

**Purpose**: Interface extending Spring Security UserDetails with Roller-specific user profile properties

**Type**: Interface

**Extends**: `org.springframework.security.core.userdetails.UserDetails`

**Additional Properties**:
```java
public interface RollerUserDetails extends UserDetails {
    String getTimeZone();
    String getLocale();
    String getScreenName();
    String getFullName();
    String getEmailAddress();
}
```

**Purpose**: Provides additional user profile information needed for:
- SSO/LDAP auto-provisioning
- OpenID Simple Registration (SREG) data
- User profile creation from external authentication

**Use Cases**:
- LDAP authentication with profile attributes
- OpenID authentication with SREG extension
- CMA with custom user attributes
- Auto-provisioning new users from external systems

---

### 17. BasicUserAutoProvision
**File**: `org.apache.roller.weblogger.ui.core.security.BasicUserAutoProvision`

**Purpose**: Automatically creates Roller user accounts from SSO/external authentication systems

**Type**: Service Class

**Implements**: `AutoProvision`

**Key Responsibilities**:
- Creates user accounts on first SSO login
- Extracts user details from authentication context
- Assigns appropriate roles based on external authorities
- Generates UUID for new users
- Grants admin role if detected in authorities

**Key Methods**:
```java
@Override
public boolean execute(HttpServletRequest request) {
    // Extract user details from authentication
    User ud = CustomUserRegistry.getUserDetailsFromAuthentication(request);
    
    // Validate required fields
    if (!hasNecessaryFields(ud)) {
        return false;
    }
    
    UserManager mgr = WebloggerFactory.getWeblogger().getUserManager();
    
    // Generate UUID if not present
    if (ud.getId() == null) {
        ud.setId(UUIDGenerator.generateUUID());
    }
    
    // Add user to database
    mgr.addUser(ud);
    
    // Check authorities for admin role
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    for (GrantedAuthority auth : authentication.getAuthorities()) {
        if (auth.getAuthority().contains("admin") || 
            auth.getAuthority().contains("ADMIN")) {
            mgr.grantRole("admin", ud);
        }
    }
    
    WebloggerFactory.getWeblogger().flush();
    return true;
}

private boolean hasNecessaryFields(User user) {
    return user != null && 
           user.getUserName() != null && 
           user.getEmailAddress() != null;
}
```

**Auto-Provisioning Flow**:
```
1. User authenticates via SSO/LDAP/CMA
2. Check if user exists in Roller database
3. If not exists, trigger auto-provision
4. Extract user details from authentication
5. Create User entity with profile data
6. Assign roles based on authorities
7. Store in database
8. User can now access Roller
```

**Integration**:
- Uses: CustomUserRegistry, UserManager, UUIDGenerator
- Triggered by: Authentication filters/providers
- Works with: LDAP, OpenID, CMA authentication

---

### 18. CustomUserRegistry
**File**: `org.apache.roller.weblogger.ui.core.security.CustomUserRegistry`

**Purpose**: Extracts user profile data from LDAP directory attributes or OpenID SREG

**Type**: Utility Class

**Key Responsibilities**:
- Maps LDAP attributes to Roller User properties
- Configurable attribute names via properties file
- Supports OpenID Simple Registration (SREG) extension
- Creates User DTOs from authentication context
- Sets sensible defaults for missing attributes

**LDAP Attribute Mappings**:
```properties
# Default LDAP attribute names
users.ldap.registry.attributes.screenname=screenname
users.ldap.registry.attributes.uid=uid
users.ldap.registry.attributes.name=cn
users.ldap.registry.attributes.email=mail
users.ldap.registry.attributes.locale=locale
users.ldap.registry.attributes.timezone=timezone
```

**Key Methods**:
```java
public static User getUserDetailsFromAuthentication(HttpServletRequest request) {
    // Only works with LDAP
    if (WebloggerConfig.getAuthMethod() != AuthMethod.LDAP) {
        return null;
    }
    
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    User ud = new User();
    ud.setId(null);
    ud.setLocale(Locale.getDefault().toString());
    ud.setTimeZone(TimeZone.getDefault().getID());
    ud.setDateCreated(new Date());
    
    // Extract from LDAP attributes or OpenID SREG
    if (authentication.getPrincipal() instanceof LdapUserDetails) {
        LdapUserDetails ldapUser = (LdapUserDetails) authentication.getPrincipal();
        Attributes attrs = ldapUser.getAttributes();
        
        // Read configurable attribute names
        String snameAttr = WebloggerConfig.getProperty(
            "users.ldap.registry.attributes.screenname", "screenname");
        String emailAttr = WebloggerConfig.getProperty(
            "users.ldap.registry.attributes.email", "mail");
        String nameAttr = WebloggerConfig.getProperty(
            "users.ldap.registry.attributes.name", "cn");
        
        // Extract values
        ud.setScreenName(getAttributeValue(attrs, snameAttr));
        ud.setEmailAddress(getAttributeValue(attrs, emailAttr));
        ud.setFullName(getAttributeValue(attrs, nameAttr));
        ud.setUserName(ldapUser.getUsername());
    }
    
    return ud;
}

private static String getAttributeValue(Attributes attrs, String attrName) {
    try {
        Attribute attr = attrs.get(attrName);
        return attr != null ? (String) attr.get() : null;
    } catch (NamingException e) {
        return null;
    }
}
```

**Integration**:
- Uses: WebloggerConfig, Spring Security Authentication
- Called by: BasicUserAutoProvision
- Supports: LDAP, OpenID SREG

---

### 19. AutoProvision
**File**: `org.apache.roller.weblogger.ui.core.security.AutoProvision`

**Purpose**: Strategy interface for auto-provisioning user accounts from external authentication

**Type**: Interface

**Design Pattern**: Strategy Pattern

**Methods**:
```java
public interface AutoProvision {
    /**
     * Execute auto-provisioning logic
     * @param request HTTP request with authentication context
     * @return true if user was successfully provisioned, false otherwise
     */
    boolean execute(HttpServletRequest request);
}
```

**Implementations**:
- `BasicUserAutoProvision` - Default implementation
- Custom implementations can be configured for specific SSO systems

**Usage in Spring Security Configuration**:
```xml
<bean id="autoProvision" class="org.apache.roller.weblogger.ui.core.security.BasicUserAutoProvision"/>
```

---

### 20. AuthoritiesPopulator
**File**: `org.apache.roller.weblogger.ui.core.security.AuthoritiesPopulator`

**Purpose**: Populates Spring Security authorities from Roller's role system for LDAP authentication

**Type**: Service Class

**Implements**: `org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator`

**Key Responsibilities**:
- Loads user roles from Roller database after LDAP authentication succeeds
- Converts Roller roles to Spring Security GrantedAuthority
- Adds default role if configured
- Bridges LDAP authentication with Roller's authorization

**Key Methods**:
```java
@Override
public Collection<GrantedAuthority> getGrantedAuthorities(
        DirContextOperations userData, String username) {
    
    // Get user from Roller database
    Weblogger roller = WebloggerFactory.getWeblogger();
    UserManager umgr = roller.getUserManager();
    User user = umgr.getUserByUserName(username, Boolean.TRUE);
    
    List<String> roles = new ArrayList<>();
    if (user != null) {
        roles = umgr.getRoles(user);
    }
    
    // Convert to authorities
    int roleCount = roles.size() + (defaultRole != null ? 1 : 0);
    List<GrantedAuthority> authorities = new ArrayList<>(roleCount);
    
    for (String role : roles) {
        authorities.add(new SimpleGrantedAuthority(role));
    }
    
    if (defaultRole != null) {
        authorities.add(defaultRole);
    }
    
    return authorities;
}

public void setDefaultRole(GrantedAuthority defaultRole) {
    this.defaultRole = defaultRole;
}
```

**LDAP Authentication Flow**:
```
1. User authenticates with LDAP server (username/password)
2. LDAP authentication succeeds
3. Spring Security calls AuthoritiesPopulator
4. Populator queries Roller database for user's roles
5. Converts roles to GrantedAuthority objects
6. Returns authorities to Spring Security
7. User gains access based on Roller roles
```

**Integration**:
- Uses: UserManager, WebloggerFactory
- Called by: Spring Security LDAP authentication provider
- Works with: LDAP authentication only

---

### 21. RollerRememberMeAuthenticationProvider
**File**: `org.apache.roller.weblogger.ui.core.security.RollerRememberMeAuthenticationProvider`

**Purpose**: Spring Security authentication provider for remember-me functionality

**Type**: Service Class

**Extends**: `org.springframework.security.authentication.RememberMeAuthenticationProvider`

**Key Responsibilities**:
- Validates remember-me authentication tokens
- Configures remember-me key from Roller properties
- Ensures secure key configuration (not default value)

**Configuration**:
```java
public RollerRememberMeAuthenticationProvider() {
    super(WebloggerConfig.getProperty("rememberme.key", "springRocks"));
    
    // Security check: ensure key is configured
    if (WebloggerConfig.getBooleanProperty("rememberme.enabled") && 
        "springRocks".equals(getKey())) {
        throw new RuntimeException(
            "If remember-me is to be enabled, rememberme.key must be " +
            "specified in the roller properties file. Make sure it is " +
            "a secret and make sure it is NOT springRocks");
    }
}
```

**Properties**:
```properties
# Enable remember-me functionality
rememberme.enabled=true

# Secret key for remember-me tokens (REQUIRED if enabled)
rememberme.key=YOUR_SECRET_KEY_HERE
```

**Security Notes**:
- Key must be secret and unique per installation
- Default value "springRocks" is rejected to prevent security issues
- Key is used to sign remember-me cookies

---

### 22. RollerRememberMeServices
**File**: `org.apache.roller.weblogger.ui.core.security.RollerRememberMeServices`

**Purpose**: Remember-me token generation and validation with LDAP support

**Type**: Service Class

**Extends**: `org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices`

**Key Responsibilities**:
- Generates SHA-512 token signatures for remember-me cookies
- Validates remember-me cookies on subsequent visits
- Special handling for LDAP authentication (uses dummy password)
- Extends browser session persistence

**Key Methods**:
```java
@Override
protected String makeTokenSignature(long tokenExpiryTime, 
                                     String username, 
                                     String password) {
    
    // Special handling for LDAP users
    boolean usingLDAP = WebloggerConfig.getAuthMethod() == AuthMethod.LDAP;
    if (usingLDAP) {
        // LDAP users don't have passwords in Roller DB
        // Use configured dummy value instead
        password = WebloggerConfig.getProperty(
            "users.passwords.externalAuthValue", "<externalAuth>");
    }
    
    // Create signature: SHA-512(username:expiry:password:key)
    String data = username + ":" + tokenExpiryTime + ":" + 
                  password + ":" + getKey();
    
    MessageDigest digest = MessageDigest.getInstance("SHA-512");
    return new String(Hex.encode(digest.digest(data.getBytes())));
}
```

**Remember-Me Cookie Format**:
```
Base64(username:expiryTime:signature)
```

**Remember-Me Flow**:
```
1. User logs in with "Remember Me" checked
2. RollerRememberMeServices generates token:
   - username:expiryTime:SHA512(username:expiry:password:key)
3. Token stored in browser cookie
4. On subsequent visit:
   - Cookie is read
   - Signature is validated
   - If valid, user is auto-logged in
5. Cookie expires after configured time
```

**LDAP Handling**:
- LDAP users don't have passwords stored in Roller
- Uses dummy password value configured in properties
- Prevents remember-me from breaking with LDAP

**Configuration**:
```properties
# Remember-me token validity (seconds)
rememberme.validity=1209600  # 14 days

# Dummy password for LDAP users
users.passwords.externalAuthValue=<externalAuth>
```

---

## Authorization & Access Control

### 23. RoleAssignmentFilter
**File**: `org.apache.roller.weblogger.ui.core.filters.RoleAssignmentFilter`

**Purpose**: Servlet filter that integrates Roller's role system with Container Managed Authentication (CMA)

**Type**: Servlet Filter

**Implements**: `javax.servlet.Filter`

**Problem Solved**: 
When using Container Managed Authentication (CMA) without a JDBC realm, the container doesn't know about Roller's user roles. This filter wraps the request to make `isUserInRole()` check against Roller's database instead of the container's realm.

**Key Components**:
```java
public class RoleAssignmentFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, 
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        
        // Wrap request to override isUserInRole()
        chain.doFilter(new RoleAssignmentRequestWrapper(request), res);
    }
}

class RoleAssignmentRequestWrapper extends HttpServletRequestWrapper {
    @Override
    public boolean isUserInRole(String roleName) {
        UserManager umgr = WebloggerFactory.getWeblogger().getUserManager();
        
        if (getUserPrincipal() != null) {
            try {
                User user = umgr.getUserByUserName(
                    getUserPrincipal().getName(), Boolean.TRUE);
                return umgr.hasRole(roleName, user);
            } catch (WebloggerException ex) {
                log.error("ERROR checking user role", ex);
            }
        }
        return false;
    }
}
```

**Use Cases**:
- Container Managed Authentication (CMA) without JDBC realm
- Integration with external authentication systems
- Custom role management separate from container

**Configuration in web.xml**:
```xml
<filter>
    <filter-name>RoleAssignmentFilter</filter-name>
    <filter-class>org.apache.roller.weblogger.ui.core.filters.RoleAssignmentFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>RoleAssignmentFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

**Authorization Flow with CMA**:
```
1. Container authenticates user (CMA)
2. Request enters Roller application
3. RoleAssignmentFilter wraps request
4. Application calls request.isUserInRole("admin")
5. Wrapper queries Roller's UserManager
6. Returns true/false based on Roller's role data
7. Authorization decision made
```

**Integration**:
- Uses: UserManager, WebloggerFactory
- Works with: CMA authentication method
- Alternative to: JDBC realm configuration

---

### 24. MemberResign
**File**: `org.apache.roller.weblogger.ui.struts2.editor.MemberResign`

**Purpose**: Allows users to resign from weblog team membership (self-service)

**Type**: Struts2 Action

**Extends**: `UIAction`

**Key Responsibilities**:
- Display resignation confirmation page
- Process user's resignation from weblog
- Revoke all weblog permissions
- Update team membership

**Key Methods**:
```java
@Override
public String execute() {
    // Show confirmation page
    return INPUT;
}

public String resign() {
    try {
        UserManager umgr = WebloggerFactory.getWeblogger().getUserManager();
        
        // Revoke ALL permissions for this user on this weblog
        umgr.revokeWeblogPermission(
            getActionWeblog(), 
            getAuthenticatedUser(), 
            WeblogPermission.ALL_ACTIONS
        );
        
        WebloggerFactory.getWeblogger().flush();
        
        addMessage("yourWebsites.resigned", getWeblog());
        
        // TODO: Notify other weblog members of resignation
        
        return SUCCESS;
    } catch (WebloggerException ex) {
        log.error("Error doing weblog resign - " + 
                  getActionWeblog().getHandle(), ex);
        addError("Resignation failed - check system logs");
        return ERROR;
    }
}
```

**Permissions Required**:
- User must have at least `EDIT_DRAFT` permission on the weblog
- User must be authenticated

**Resignation Flow**:
```
1. User navigates to "Your Websites" page
2. Clicks "Resign" link for a weblog
3. Confirmation page displays
4. User confirms resignation
5. All permissions revoked via UserManager
6. User removed from weblog team
7. Redirect to websites list
```

**Notes**:
- User cannot resign if they are the only ADMIN
- Future enhancement: Notify other team members
- User can be re-invited later

**Integration**:
- Uses: UserManager, WebloggerFactory
- Related to: Members, MembersInvite actions
- Works with: WeblogPermission entity

---

### 25. UserDataServlet
**File**: `org.apache.roller.weblogger.ui.struts2.ajax.UserDataServlet`

**Purpose**: AJAX servlet providing user autocomplete/search functionality

**Type**: HTTP Servlet

**Extends**: `javax.servlet.http.HttpServlet`

**Key Responsibilities**:
- Return users matching search criteria (autocomplete)
- Support pagination for large result sets
- Filter by enabled status
- Provide data for member invitation UI

**Request Parameters**:
```java
- startsWith: String      // Match against username and email
- enabled: Boolean        // true=enabled only, false=disabled, null=all
- offset: int            // Pagination offset (default: 0)
- length: int            // Results to return (max: 50, default: 20)
```

**Response Format**:
```
username0, emailaddress0
username1, emailaddress1
username2, emailaddress2
...
```

**Implementation**:
```java
@Override
public void doGet(HttpServletRequest request, 
                  HttpServletResponse response) 
        throws ServletException, IOException {
    
    // Parse parameters
    String startsWith = request.getParameter("startsWith");
    String enabledParam = request.getParameter("enabled");
    String offsetParam = request.getParameter("offset");
    String lengthParam = request.getParameter("length");
    
    Boolean enabled = enabledParam != null ? 
        Boolean.valueOf(enabledParam) : null;
    int offset = offsetParam != null ? Integer.parseInt(offsetParam) : 0;
    int length = lengthParam != null ? 
        Math.min(Integer.parseInt(lengthParam), MAX_LENGTH) : 20;
    
    // Query users
    UserManager umgr = WebloggerFactory.getWeblogger().getUserManager();
    List<User> users = umgr.getUsersStartingWith(
        startsWith, enabled, offset, length);
    
    // Format response
    response.setContentType("text/plain;charset=utf-8");
    PrintWriter out = response.getWriter();
    
    for (User user : users) {
        out.println(user.getUserName() + "," + user.getEmailAddress());
    }
    
    out.flush();
}
```

**Access Control**:
- Requires `admin` or `editor` role
- Protected by Spring Security: `/roller-ui/authoring/**`

**URL Pattern**:
```
/roller-ui/authoring/userdata?startsWith=john&enabled=true&offset=0&length=10
```

**Use Cases**:
- Member invitation autocomplete
- Admin user search
- User selection dialogs
- AJAX-powered user pickers

**Integration**:
- Uses: UserManager
- Called by: JavaScript autocomplete widgets
- Returns: Plain text (CSV format)

---
## Business Logic Layer

### 15. UserManager (Interface)
**File**: `org.apache.roller.weblogger.business.UserManager`

**Purpose**: Defines contract for user/permission operations

**Type**: Interface

**Design Pattern**: Repository Pattern

**Method Categories**:

#### User CRUD
```java
void addUser(User newUser)
void saveUser(User user)
void removeUser(User user)
User getUser(String id)
User getUserByUserName(String userName)
User getUserByUserName(String userName, Boolean enabled)
User getUserByOpenIdUrl(String openIdUrl)
User getUserByActivationCode(String activationCode)
```

#### User Queries
```java
List<User> getUsers(Boolean enabled, Date start, Date end, int offset, int length)
List<User> getUsersStartingWith(String startsWith, Boolean enabled, int offset, int length)
List<User> getUsersByLetter(char letter, int offset, int length)
long getUserCount()
Map<String,Long> getUserNameLetterMap()
```

#### Permission Checking
```java
boolean checkPermission(RollerPermission perm, User user)
```

#### Weblog Permissions
```java
// Retrieve permissions
WeblogPermission getWeblogPermission(Weblog weblog, User user)
WeblogPermission getWeblogPermissionIncludingPending(Weblog weblog, User user)
List<WeblogPermission> getWeblogPermissions(User user)
List<WeblogPermission> getWeblogPermissions(Weblog weblog)
List<WeblogPermission> getWeblogPermissionsIncludingPending(Weblog weblog)

// Pending invitations
List<WeblogPermission> getPendingWeblogPermissions(User user)
List<WeblogPermission> getPendingWeblogPermissions(Weblog weblog)

// Grant/revoke
void grantWeblogPermission(Weblog weblog, User user, List<String> actions)
void grantWeblogPermissionPending(Weblog weblog, User user, List<String> actions)
void confirmWeblogPermission(Weblog weblog, User user)
void declineWeblogPermission(Weblog weblog, User user)
void revokeWeblogPermission(Weblog weblog, User user, List<String> actions)
```

#### Role Management (Deprecated)
```java
boolean hasRole(String roleName, User user)
List<String> getRoles(User user)
void grantRole(String roleName, User user)
void revokeRole(String roleName, User user)
```

---

### 16. JPAUserManagerImpl (Implementation)
**File**: `org.apache.roller.weblogger.business.jpa.JPAUserManagerImpl`

**Purpose**: JPA implementation of UserManager

**Type**: Singleton Class

**Annotations**:
- `@com.google.inject.Singleton`
- `@com.google.inject.Inject`

**Design Patterns**:
- Singleton Pattern
- Strategy Pattern (uses JPAPersistenceStrategy)
- Repository Pattern

**Attributes**:
```java
private final JPAPersistenceStrategy strategy;           // Database access
private final Map<String, String> userNameToIdMap;       // Cache: username → userId
private static final Log log;                            // Logger
```

**Key Implementation**:

#### User Creation
```java
@Override
public void addUser(User newUser) throws WebloggerException {
    // Check if first user (becomes admin)
    TypedQuery<User> q = strategy.getNamedQuery("User.getAll", User.class);
    List<User> existingUsers = q.getResultList();
    
    boolean firstUserAdmin = WebloggerConfig.getBooleanProperty("users.firstUserAdmin");
    if (existingUsers.isEmpty() && firstUserAdmin) {
        grantRole("admin", newUser);
    }
    
    // Store user
    strategy.store(newUser);
    
    // Cache username mapping
    userNameToIdMap.put(newUser.getUserName(), newUser.getId());
}
```

#### Permission Granting
```java
@Override
public void grantWeblogPermission(Weblog weblog, User user, List<String> actions) 
    throws WebloggerException {
    
    // Check if permission exists
    WeblogPermission perm = getWeblogPermission(weblog, user);
    
    if (perm == null) {
        // Create new permission
        perm = new WeblogPermission(weblog, user, actions);
        perm.setPending(false);
        strategy.store(perm);
    } else {
        // Add actions to existing
        perm.addActions(actions);
        strategy.store(perm);
    }
}
```

#### Pending Invitation Workflow
```java
@Override
public void grantWeblogPermissionPending(Weblog weblog, User user, List<String> actions) 
    throws WebloggerException {
    
    WeblogPermission perm = new WeblogPermission(weblog, user, actions);
    perm.setPending(true);
    perm.setDateCreated(new Date());
    strategy.store(perm);
    
    // Send invitation email
    MailUtil.sendInvitation(weblog, user, perm);
}

@Override
public void confirmWeblogPermission(Weblog weblog, User user) 
    throws WebloggerException {
    
    WeblogPermission perm = getWeblogPermissionIncludingPending(weblog, user);
    if (perm != null && perm.isPending()) {
        perm.setPending(false);
        strategy.store(perm);
    }
}

@Override
public void declineWeblogPermission(Weblog weblog, User user) 
    throws WebloggerException {
    
    WeblogPermission perm = getWeblogPermissionIncludingPending(weblog, user);
    if (perm != null && perm.isPending()) {
        strategy.remove(perm);
    }
}
```

#### Caching Strategy
```java
private final Map<String, String> userNameToIdMap = 
    Collections.synchronizedMap(new HashMap<>());
```
- Thread-safe concurrent access
- Populated on-demand
- Speeds up username → ID lookups

#### JPA Named Queries
```java
// Examples:
TypedQuery<User> q = strategy.getNamedQuery("User.getByUserName", User.class);
q.setParameter(1, userName);
return q.getSingleResult();
```

**Used Queries**:
- `User.getByUserName`
- `User.getByOpenIdUrl`
- `User.getByActivationCode`
- `User.getByEnabled&EndDate`
- `WeblogPermission.getByUserName&WeblogId`
- `WeblogPermission.getByUserName&WeblogIdIncludingPending`
- `WeblogPermission.getByWeblogId`
- `UserRole.getByUserName`

---

### 17. JPAPersistenceStrategy
**File**: `org.apache.roller.weblogger.business.jpa.JPAPersistenceStrategy`

**Purpose**: Abstracts JPA operations

**Type**: Class

**Design Pattern**: Strategy Pattern

**Key Methods**:
```java
public void store(Object obj)                    // Persist/merge entity
public void remove(Object obj)                   // Delete entity
public Object load(Class clazz, String id)       // Find by ID
public void flush()                              // Sync to database
public TypedQuery getNamedQuery(String name, Class clazz)  // Execute query
```

---

## Security Wrappers

### 18. UserWrapper
**File**: `org.apache.roller.weblogger.pojos.wrapper.UserWrapper`

**Purpose**: Read-only, sanitized view for templates

**Type**: Final Class (immutable)

**Design Patterns**:
- Wrapper Pattern
- Facade Pattern
- Immutable Object

**Attributes**:
```java
private final User pojo;  // Wrapped User
```

**Construction**:
```java
// Private constructor
private UserWrapper(User toWrap) {
    this.pojo = toWrap;
}

// Static factory method
public static UserWrapper wrap(User toWrap) {
    return (toWrap != null) ? new UserWrapper(toWrap) : null;
}
```

**Exposed Methods** (Read-Only):
```java
public String getUserName()      // Optional: can hide real names
public String getScreenName()    // Additional sanitization
public String getFullName()
public String getEmailAddress()
public Date getDateCreated()
public String getLocale()
public String getTimeZone()
```

**Security Features**:
- No setters (prevents modification from templates)
- No password access
- No sensitive fields (enabled, activationCode, ID)
- Extra XSS sanitization layer
- Configuration-controlled field hiding

**Template Usage**:
```velocity
## Velocity template
<h1>Welcome, $user.screenName!</h1>
<p>Member since: $user.dateCreated</p>

## This would fail (no setter):
## $user.setScreenName("hacker")  ← Not possible
```

---

## Utility Classes

### 19. UUIDGenerator
**File**: `org.apache.roller.util.UUIDGenerator`

**Purpose**: Generate unique identifiers for entity primary keys

**Method**:
```java
public static String generateUUID()
```

---

### 20. HTMLSanitizer
**File**: `org.apache.roller.weblogger.util.HTMLSanitizer`

**Purpose**: Prevent XSS attacks by sanitizing user input

**Method**:
```java
public static String conditionallySanitize(String input)
```

**Applied to**: All user-provided text fields

---

### 21. WebloggerFactory
**File**: `org.apache.roller.weblogger.business.WebloggerFactory`

**Purpose**: Singleton factory for Weblogger instance

**Methods**:
```java
public static Weblogger getWeblogger()
public static void bootstrap()
```

---

### 22. WebloggerConfig
**File**: `org.apache.roller.weblogger.config.WebloggerConfig`

**Purpose**: Configuration properties management

**Methods**:
```java
public static String getProperty(String key)
public static boolean getBooleanProperty(String key)
```

**Key Properties**:
- `users.firstUserAdmin` - Make first user admin
- `registration.enabled` - Allow self-registration
- `user.hideUserNames` - Privacy setting

---

## Interaction Flows

### Flow 1: User Self-Registration

```
Visitor → Register.execute()
  → Display registration form

Visitor submits → Register.save()
  → Validate RegistrationBean
    → Check username uniqueness
    → Validate email format
    → Check password strength
  → new User()
    → setUserName(bean.userName)
    → resetPassword(bean.password)  // BCrypt hash
    → setEmailAddress(bean.emailAddress)
    → setActivationCode(UUIDGenerator.generateUUID())
    → setEnabled(false)  // Requires email verification
  → UserManager.addUser(user)
    → JPAUserManagerImpl.addUser()
      → Check if first user
        → if yes: grantRole("admin", user)
      → strategy.store(user)
      → userNameToIdMap.put(username, id)
      → strategy.flush()
  → MailUtil.sendActivationEmail(user)
  → SUCCESS (show "Check your email" page)

User clicks email link → Register.activate()
  → UserManager.getUserByActivationCode(code)
  → if (user != null)
    → user.setEnabled(true)
    → user.setActivationCode(null)
    → UserManager.saveUser(user)
  → SUCCESS (redirect to login)
```

---

### Flow 2: Edit Profile

```
User logs in → Profile.execute()
  → Get authenticated user from session
  → UserManager.getUser(userId)
  → bean = new ProfileBean()
  → bean.copyFrom(user)
  → Display form (pre-filled)

User edits → Profile.save()
  → Validate ProfileBean
  → UserManager.getUser(bean.id)
  → bean.copyTo(user)
    → user.setScreenName(bean.screenName)  // Sanitized
    → user.setFullName(bean.fullName)
    → user.setEmailAddress(bean.emailAddress)
    → user.setLocale(bean.locale)
    → user.setTimeZone(bean.timeZone)
  → UserManager.saveUser(user)
    → JPAUserManagerImpl.saveUser()
      → strategy.store(user)
      → strategy.flush()
  → SUCCESS (show confirmation)
```

---

### Flow 3: Admin Creates User

```
Admin → UserEdit.execute()
  → Check user has ADMIN permission
    → GlobalPermission adminPerm = new GlobalPermission("admin")
    → if (!userManager.checkPermission(adminPerm, admin))
      → ERROR (access denied)
  → Display user creation form

Admin submits → UserEdit.save()
  → Validate CreateUserBean
  → new User()
  → bean.copyTo(user)
  → user.setEnabled(bean.enabled)  // Admin sets directly
  → UserManager.addUser(user)
  → for each role in bean.roles:
    → UserManager.grantRole(role, user)
      → new UserRole(user.userName, role)
      → strategy.store(userRole)
  → strategy.flush()
  → SUCCESS
```

---

### Flow 4: Grant Weblog Permission (Invitation)

```
Blog Owner → Members page → Invite user

Owner enters username → InviteMember.save()
  → UserManager.getUserByUserName(inviteeUsername)
  → Check owner has ADMIN permission on weblog
    → WeblogPermission ownerPerm = userManager.getWeblogPermission(weblog, owner)
    → if (!ownerPerm.hasAction("admin"))
      → ERROR (not authorized)
  → UserManager.grantWeblogPermissionPending(weblog, invitee, ["post"])
    → JPAUserManagerImpl.grantWeblogPermissionPending()
      → new WeblogPermission(weblog, invitee, ["post"])
      → perm.setPending(true)
      → perm.setDateCreated(new Date())
      → strategy.store(perm)
      → strategy.flush()
      → MailUtil.sendInvitation(weblog, invitee, perm)
  → SUCCESS

Invitee receives email → Clicks accept link → AcceptInvitation.execute()
  → Get invitee from session
  → UserManager.confirmWeblogPermission(weblog, invitee)
    → JPAUserManagerImpl.confirmWeblogPermission()
      → perm = getWeblogPermissionIncludingPending(weblog, invitee)
      → if (perm != null && perm.isPending())
        → perm.setPending(false)
        → strategy.store(perm)
        → strategy.flush()
  → SUCCESS (invitee can now access weblog)

OR

Invitee clicks decline → DeclineInvitation.execute()
  → UserManager.declineWeblogPermission(weblog, invitee)
    → JPAUserManagerImpl.declineWeblogPermission()
      → perm = getWeblogPermissionIncludingPending(weblog, invitee)
      → if (perm != null && perm.isPending())
        → strategy.remove(perm)
        → strategy.flush()
  → SUCCESS (invitation deleted)
```

---

### Flow 5: Check User Permissions

```
User attempts action → Security check

Example: User tries to publish blog entry

EntryEdit.publish()
  → Get authenticated user from session
  → Get weblog from entry
  → Check permission:
    → WeblogPermission perm = userManager.getWeblogPermission(weblog, user)
    → if (perm == null)
      → ERROR (no access)
    → if (!perm.hasAction("post"))
      → ERROR (can only edit drafts)
  → Proceed with publish
    → entry.setStatus("PUBLISHED")
    → weblogEntryManager.saveEntry(entry)
  → SUCCESS

Example: User tries to delete blog

WeblogConfig.remove()
  → Get authenticated user from session
  → Check permission:
    → WeblogPermission perm = userManager.getWeblogPermission(weblog, user)
    → if (perm == null || !perm.hasAction("admin"))
      → ERROR (only blog admins can delete)
  → weblogManager.removeWeblog(weblog)
  → SUCCESS
```

---

### Flow 6: Admin Manages Users

```
Admin → UserAdmin.execute()
  → Check global ADMIN permission
    → GlobalPermission adminPerm = new GlobalPermission(Collections.singletonList("admin"))
    → if (!userManager.checkPermission(adminPerm, admin))
      → ERROR (access denied)
  → UserManager.getUsers(enabledOnly=bean.enabledOnly, offset=bean.offset, length=bean.length)
    → JPAUserManagerImpl.getUsers()
      → TypedQuery q = strategy.getNamedQuery("User.getByEnabled&EndDate", User.class)
      → q.setParameter("enabled", enabledOnly)
      → q.setFirstResult(offset)
      → q.setMaxResults(length)
      → List<User> users = q.getResultList()
  → Display user list

Admin searches → UserAdmin.search()
  → UserManager.getUsersStartingWith(bean.searchString, bean.enabledOnly, offset, length)
    → JPAUserManagerImpl.getUsersStartingWith()
      → TypedQuery q = strategy.getNamedQuery("User.getByUserNameStartingWith", User.class)
      → q.setParameter("pattern", searchString + "%")
      → List<User> users = q.getResultList()
  → Display filtered results

Admin disables account → UserAdmin.toggleEnabled()
  → UserManager.getUser(userId)
  → user.setEnabled(!user.getEnabled())
  → UserManager.saveUser(user)
    → strategy.store(user)
    → strategy.flush()
  → SUCCESS (user cannot login if disabled)

Admin deletes user → UserAdmin.delete()
  → for each userId in bean.selectedUserIds:
    → UserManager.getUser(userId)
    → Check user has no blogs or transfer ownership
    → UserManager.removeUser(user)
      → JPAUserManagerImpl.removeUser()
        → Remove all WeblogPermissions for user
        → Remove all UserRoles for user
        → strategy.remove(user)
        → strategy.flush()
  → SUCCESS
```

---

## Security Architecture

### Authentication Flow

```
1. User enters credentials (username + password)
2. Spring Security intercepts login request
3. UserDetailsService loads User by username
   → UserManager.getUserByUserName(username, enabled=true)
4. PasswordEncoder.matches(rawPassword, user.password)
   → BCrypt comparison
5. If match:
   → Create Authentication object
   → Load GrantedAuthorities from UserRoles
   → Store in SecurityContext
6. User authenticated
```

### Authorization Flow

```
1. User attempts protected action
2. Security interceptor checks:
   
   A. Global Permission Check:
      → GlobalPermission required = new GlobalPermission(["admin"])
      → boolean hasPermission = userManager.checkPermission(required, user)
      → If hasPermission:
        → ALLOW
      → Else:
        → DENY (HTTP 403)
   
   B. Weblog Permission Check:
      → WeblogPermission perm = userManager.getWeblogPermission(weblog, user)
      → If perm == null:
        → DENY
      → If perm.hasAction(requiredAction):
        → ALLOW
      → Else:
        → DENY
```

### Password Security

```java
// Registration
plainPassword = "userPassword123"
encoder = new BCryptPasswordEncoder()
hashedPassword = encoder.encode(plainPassword)
// Result: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
user.setPassword(hashedPassword)

// Login attempt
loginPassword = "userPassword123"
storedHash = user.getPassword()
matches = encoder.matches(loginPassword, storedHash)
// BCrypt automatically handles salt
// Result: true (authenticated)
```

### XSS Prevention

```java
// All user input sanitized
userInput = "<script>alert('XSS')</script>"
sanitized = HTMLSanitizer.conditionallySanitize(userInput)
// Result: "&lt;script&gt;alert('XSS')&lt;/script&gt;"
user.setScreenName(sanitized)

// Templates use UserWrapper for extra safety
UserWrapper wrapper = UserWrapper.wrap(user)
template.put("user", wrapper)
// Template can only call getters, no setters exposed
```

### Permission Hierarchy

```
Global Permissions:
  ADMIN → Can do anything
    ├── WEBLOG → Can create blogs
    │     └── LOGIN → Can access system
    └── All weblog ADMIN permissions

Weblog Permissions:
  ADMIN → Full blog control
    ├── POST → Publish entries
    │     └── EDIT_DRAFT → Create drafts
    └── Can invite/remove members
```

---

## Summary Table

| Category | Action Class | Bean Class | Entity Class | Manager Interface | Implementation |
|----------|-------------|------------|--------------|-------------------|----------------|
| **Registration** | Register | ProfileBean | User | UserManager | JPAUserManagerImpl |
| **Profile** | Profile | ProfileBean | User | UserManager | JPAUserManagerImpl |
| **Admin** | UserAdmin, UserEdit | CreateUserBean | User, UserRole | UserManager | JPAUserManagerImpl |
| **Members** | Members, MembersInvite | None (HttpParameters) | WeblogPermission | UserManager | JPAUserManagerImpl |
| **Permissions** | - | - | GlobalPermission, WeblogPermission | UserManager | JPAUserManagerImpl |

---

## Key Relationships

**Composition (filled diamond `*--`):**
- Register ◆→ ProfileBean (Register owns ProfileBean)
- Profile ◆→ ProfileBean (Profile owns ProfileBean)
- UserAdmin ◆→ CreateUserBean (UserAdmin owns CreateUserBean)
- JPAUserManagerImpl ◆→ JPAPersistenceStrategy (Manager owns strategy)
- User ◆→ Date (User owns dateCreated)
- WeblogPermission ◆→ Date (Permission owns dateCreated)

**Aggregation (hollow diamond `o--`):**
- JPAUserManagerImpl ◇→ Map (caches username-to-id)
- JPAUserManagerImpl ◇→ Log (has logger)
- UserWrapper ◇→ User (wraps user without ownership)

**Association (solid arrow `-->`):**
- Register → User (creates)
- UserAdmin → User (manages)
- Members → WeblogPermission, Weblog (manages)
- MembersInvite → WeblogPermission (creates)
- MembersInvite → User (invites)
- Profile → User (updates)
- Weblogger → UserManager, WeblogManager (provides)
- JPAUserManagerImpl → User, WeblogPermission, UserRole (persists/manages)
- WeblogPermission "*" → "1" Weblog (for weblog)
- WeblogPermission "*" → "1" User (granted to user)
- UserRole "*" → "1" User (assigned to user)
- Weblog "*" → "1" User (created by)
- User → UserStatus (has status)

**Dependency (dashed arrow `..>`):**
- Register ⇢ UserManager, WebloggerFactory, MailUtil (uses)
- Profile ⇢ UserManager, WebloggerFactory (uses)
- UserAdmin ⇢ UserManager, WebloggerFactory (uses)
- Members ⇢ UserManager, WebloggerFactory (uses)
- MembersInvite ⇢ UserManager, WebloggerFactory, MailUtil (uses)
- ProfileBean ⇢ User (transfers data)
- CreateUserBean ⇢ User (transfers data)
- WebloggerFactory ⇢ Weblogger (provides)
- JPAUserManagerImpl ⇢ GlobalPermission, WebloggerException (checks/throws)
- JPAPersistenceStrategy ⇢ EntityManager, TypedQuery (uses)
- User ⇢ UUIDGenerator, HTMLSanitizer, WebloggerFactory, PasswordEncoder, RollerContext, GlobalPermission (uses)
- WeblogPermission ⇢ UUIDGenerator, WebloggerFactory (uses)
- UserRole ⇢ UUIDGenerator (uses)
- GlobalPermission ⇢ WebloggerFactory, WebloggerConfig (uses)
- UserWrapper ⇢ HTMLSanitizer, WebloggerConfig (uses)
- RollerContext ⇢ PasswordEncoder (provides)
- MailUtil ⇢ User, Weblog (notifies)

**Inheritance (solid arrow with triangle `--|>`):**
- RollerPermission --|> Permission (extends java.security.Permission)
- GlobalPermission --|> RollerPermission (extends)
- ObjectPermission --|> RollerPermission (extends)
- WeblogPermission --|> ObjectPermission (extends)

**Implementation (solid arrow with triangle `--|>`):**
- JPAUserManagerImpl --|> UserManager (implements)
- User --|> Serializable (implements)
- WeblogPermission --|> Serializable (implements)
- UserRole --|> Serializable (implements)
- Weblog --|> Serializable (implements)

---

## Total Class Count: 27

- **Controllers**: 6 (Register, Profile, UserAdmin, UserEdit, Members, MembersInvite)
- **DTOs**: 2 (ProfileBean, CreateUserBean)
- **Entities**: 3 (User, WeblogPermission, UserRole)
- **Permission Classes**: 3 (RollerPermission, GlobalPermission, ObjectPermission)
- **Interfaces**: 2 (UserManager, Weblogger)
- **Implementations**: 2 (JPAUserManagerImpl, JPAPersistenceStrategy)
- **Wrappers**: 1 (UserWrapper)
- **Utilities**: 3 (UUIDGenerator, HTMLSanitizer, WebloggerFactory, WebloggerConfig, MailUtil = 5)

---

## Configuration Properties

```properties
# roller.properties

# First user becomes admin
users.firstUserAdmin=true

# Allow self-registration
registration.enabled=true

# Hide real usernames in public views
user.hideUserNames=false

# Email verification required
registration.emailVerification=true

# Password requirements
password.minimumLength=8
password.requireUpperCase=true
password.requireNumber=true
password.requireSpecial=false

# Role action mappings
role.action.admin=login,weblog,admin
role.action.editor=login,weblog
role.action.user=login
```

---

## Database Schema Summary

```sql
-- Users table
CREATE TABLE roller_user (
    id VARCHAR(48) PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    screenname VARCHAR(255),
    fullname VARCHAR(255),
    emailaddress VARCHAR(255) NOT NULL,
    datecreated TIMESTAMP,
    locale VARCHAR(20),
    timezone VARCHAR(50),
    isenabled BOOLEAN DEFAULT TRUE,
    activationcode VARCHAR(48),
    openid_url VARCHAR(255)
);

-- Weblog permissions table
CREATE TABLE weblogpermission (
    id VARCHAR(48) PRIMARY KEY,
    username VARCHAR(48) NOT NULL,
    weblog_id VARCHAR(48) NOT NULL,
    actions VARCHAR(255),
    pending BOOLEAN DEFAULT FALSE,
    datecreated TIMESTAMP,
    FOREIGN KEY (username) REFERENCES roller_user(username),
    FOREIGN KEY (weblog_id) REFERENCES weblog(id)
);

-- User roles table (legacy)
CREATE TABLE userrole (
    id VARCHAR(48) PRIMARY KEY,
    username VARCHAR(48) NOT NULL,
    rolename VARCHAR(255) NOT NULL,
    FOREIGN KEY (username) REFERENCES roller_user(username)
);

-- Indexes
CREATE INDEX userrole_username_idx ON userrole(username);
CREATE INDEX weblogpermission_username_idx ON weblogpermission(username);
CREATE INDEX weblogpermission_weblog_idx ON weblogpermission(weblog_id);
CREATE INDEX weblogpermission_pending_idx ON weblogpermission(pending);
```

---

## End of Documentation

This comprehensive documentation covers all aspects of the User and Role Management subsystem in Apache Roller, following the same structure as the Blogs, Entries & Comments documentation.
