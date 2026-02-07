[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/HIOZk3CI)

# Apache Roller - Complete System Guide

![Roller Home](docs/images/roller-home.png)

> **A step-by-step guide to understanding and using the Apache Roller blogging platform**

---

## Design Smell Analysis

**Analyzed Classes**: `WeblogEntry`, `Weblog`  
**Analysis Date**: February 4, 2026  
**Tools Used**: Designite Java, SonarQube, UML Class Diagram Analysis

**Design Smells Identified**:
1. **Insufficient Modularization** - Detected in `WeblogEntry` (91 public methods) and `Weblog` (97 public methods)
2. **Hub-like Modularization** - Detected in `WeblogEntry` (53 incoming dependencies) and `Weblog` (113 incoming dependencies)
3. **Deficient Encapsulation** - Detected in `WeblogEntry` (29 public fields) and `Weblog` (36 public fields)

**Key Findings**:
- `WeblogEntry` class located at `/app/src/main/java/org/apache/roller/weblogger/pojos/WeblogEntry.java` suffers from bloated interface with 91 public methods (3.6x over threshold)
- `Weblog` class acts as central hub with 113 incoming dependencies (4.5x over threshold)
- Both classes violate Single Responsibility Principle with 8+ distinct responsibilities each

For detailed analysis, see [IDENTIFIED_DESIGN_SMELLS.md](IDENTIFIED_DESIGN_SMELLS.md)

---

## 📑 Table of Contents

### Part 1: Introduction
- [What is Apache Roller?](#what-is-apache-roller)
- [Key Features](#key-features)
- [System Architecture](#system-architecture)
- [Technology Stack](#technology-stack)

### Part 2: Setup & Installation
- [Prerequisites](#prerequisites)
- [Method 1: Quick Start with Docker](#method-1-quick-start-with-docker)
- [Method 2: Development Setup with Maven](#method-2-development-setup-with-maven)
- [Method 3: Production Deployment](#method-3-production-deployment)
- [Initial Configuration](#initial-configuration)

### Part 3: Understanding the System
- [First Login & Interface Overview](#first-login--interface-overview)
- [Content Management System](#content-management-system)
- [User Management System](#user-management-system)
- [Media Management System](#media-management-system)
- [Theme & Design System](#theme--design-system)
- [Comment Management System](#comment-management-system)
- [Configuration & Settings](#configuration--settings)

### Part 4: Advanced Features
- [Feed Management & Syndication](#feed-management--syndication)
- [Planet Feed Aggregator](#planet-feed-aggregator)
- [API & External Integration](#api--external-integration)
- [Plugin Development](#plugin-development)
- [Scripting & Automation](#scripting--automation)
- [Internationalization](#internationalization)

### Part 5: Development & Contribution
- [Developer Setup](#developer-setup)
- [Project Structure](#project-structure)
- [Building & Testing](#building--testing)
- [Contributing Guidelines](#contributing-guidelines)

### Part 6: Reference
- [Troubleshooting Guide](#troubleshooting-guide)
- [Configuration Reference](#configuration-reference)
- [Documentation Resources](#documentation-resources)

---

# Part 1: Introduction

## What is Apache Roller?

---

# Part 1: Introduction

## What is Apache Roller?

**Apache Roller** is a full-featured, professional-grade blogging platform built with Java. It's designed for both individual bloggers and large organizations that need a reliable, scalable blogging solution.

**Project Information:**
- **Started**: 2002 (24+ years of development)
- **License**: Apache License 2.0 (Free and Open Source)
- **Language**: Java
- **Deployment**: Runs on any Java application server (Tomcat, Jetty, etc.)
- **Database**: Supports MySQL, PostgreSQL, MariaDB, Oracle, Derby
- **Website**: http://roller.apache.org
- **Repository**: https://github.com/apache/roller

**Who uses Apache Roller?**
- Individual bloggers who want full control
- Corporate blogs and news sites
- Technical documentation sites
- Community blogging platforms
- Multi-author collaborative blogs

## Key Features

### 🎯 Core Blogging Features
- ✅ **Multi-user Support** - Unlimited users and blogs on one installation
- ✅ **Rich Text Editor** - WYSIWYG editor with full formatting capabilities
- ✅ **Categories & Tags** - Organize content with hierarchical categories and tags
- ✅ **Draft & Scheduling** - Save drafts and schedule posts for future publication
- ✅ **Media Management** - Upload and organize images, videos, and files
- ✅ **Search Engine** - Apache Lucene powered full-text search
- ✅ **SEO Friendly** - Clean URLs, meta tags, sitemaps

### 🎨 Customization Features
- ✅ **Theme System** - Fully customizable Velocity-based themes
- ✅ **Template Engine** - Edit templates with live preview
- ✅ **Custom CSS/JS** - Add your own styles and scripts
- ✅ **Responsive Design** - Mobile-friendly out of the box
- ✅ **Plugin Architecture** - Extend functionality with plugins

### 💬 Community Features
- ✅ **Comment System** - Threaded comments with moderation
- ✅ **Spam Protection** - Built-in spam filtering and blacklists
- ✅ **Trackbacks/Pingbacks** - Notify other blogs of your posts
- ✅ **Social Integration** - Share to social media platforms

### 📡 Syndication & APIs
- ✅ **RSS/Atom Feeds** - Automatic feed generation
- ✅ **AtomPub API** - Standard publishing protocol
- ✅ **MetaWeblog API** - Compatible with blog clients
- ✅ **Planet Aggregator** - Combine multiple blogs into one

### 🌍 Enterprise Features
- ✅ **Internationalization** - 8+ language translations
- ✅ **Role-Based Access** - Admin, Editor, Author, Limited roles
- ✅ **Multi-Blog Management** - Manage multiple blogs from one interface
- ✅ **Group Blogging** - Multiple authors per blog
- ✅ **Security** - Spring Security integration with LDAP support

## System Architecture

Apache Roller follows a classic multi-tier architecture:

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT LAYER                              │
│              (Web Browser - Desktop/Mobile)                  │
└───────────────────────┬─────────────────────────────────────┘
                        │ HTTP/HTTPS Requests
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                 PRESENTATION LAYER                           │
│  ┌──────────────┬──────────────┬──────────────────────┐    │
│  │   JSP Pages  │   Velocity   │   AngularJS/jQuery   │    │
│  │              │   Templates  │                      │    │
│  └──────────────┴──────────────┴──────────────────────┘    │
│  ┌──────────────────────────────────────────────────┐      │
│  │         Struts 2 MVC Framework                   │      │
│  │         (Actions, Interceptors, Results)         │      │
│  └──────────────────────────────────────────────────┘      │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                   BUSINESS LAYER                             │
│  ┌────────────────────────────────────────────────────┐    │
│  │              Business Managers                      │    │
│  │  • WeblogManager      • MediaFileManager           │    │
│  │  • WeblogEntryManager • BookmarkManager            │    │
│  │  • UserManager        • ThemeManager               │    │
│  │  • CommentManager     • SearchManager              │    │
│  └────────────────────────────────────────────────────┘    │
│  ┌────────────────────────────────────────────────────┐    │
│  │      Dependency Injection (Google Guice)           │    │
│  └────────────────────────────────────────────────────┘    │
│  ┌────────────────────────────────────────────────────┐    │
│  │         Spring Security (Authentication)           │    │
│  └────────────────────────────────────────────────────┘    │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                 PERSISTENCE LAYER                            │
│  ┌────────────────┬──────────────┬───────────────────┐     │
│  │   JPA/ORM      │  EclipseLink │  Apache Lucene    │     │
│  │   Entities     │  (Provider)  │  (Search Index)   │     │
│  └────────────────┴──────────────┴───────────────────┘     │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                   DATA LAYER                                 │
│     MySQL / PostgreSQL / MariaDB / Oracle / Derby           │
│                                                              │
│  Tables: roller_user, weblog, weblogentry,                  │
│          weblogcategory, roller_comment, mediafile...       │
└─────────────────────────────────────────────────────────────┘

          ┌──────────────────────────────────┐
          │    EXTERNAL INTEGRATIONS          │
          ├──────────────────────────────────┤
          │  • File System (uploads)         │
          │  • SMTP Server (emails)          │
          │  • External RSS/Atom feeds       │
          │  • Blog clients (via APIs)       │
          └──────────────────────────────────┘
```

**Layer Responsibilities:**

1. **Client Layer**: Web browsers, mobile devices, blog clients
2. **Presentation Layer**: User interface, templates, MVC framework
3. **Business Layer**: Core business logic, managers, services
4. **Persistence Layer**: Database operations, search indexing
5. **Data Layer**: Physical data storage

## Technology Stack

### Backend Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 11+ | Core programming language |
| **Struts 2** | 2.5.29 | MVC web framework |
| **Spring Framework** | 5.3.39 | Dependency injection, utilities |
| **Spring Security** | 5.8.14 | Authentication & authorization |
| **Google Guice** | 7.0.0 | Dependency injection |
| **JPA** | 2.2 | Java Persistence API |
| **EclipseLink** | 4.0.5 | JPA provider (ORM) |
| **Apache Velocity** | 2.4.1 | Template engine |
| **Apache Lucene** | 9.12.1 | Full-text search engine |
| **ROME** | 1.19.0 | RSS/Atom feed library |
| **Log4j2** | 2.24.3 | Logging framework |

### Frontend Technologies

| Technology | Purpose |
|------------|---------|
| **JSP** | JavaServer Pages for dynamic content |
| **AngularJS** | JavaScript framework for interactive UI |
| **jQuery** | JavaScript library for DOM manipulation |
| **Bootstrap** | Responsive CSS framework |
| **Custom JavaScript** | Blog-specific functionality |

### Database Support

- ✅ **MySQL** 5.7+ (Recommended for production)
- ✅ **MariaDB** 10.2+ (MySQL alternative)
- ✅ **PostgreSQL** 9.6+ (Excellent performance)
- ✅ **Oracle** 11g+ (Enterprise deployments)
- ✅ **Apache Derby** (Development/testing only)

### Build & Development Tools

| Tool | Version | Purpose |
|------|---------|---------|
| **Maven** | 3.6+ | Build automation, dependencies |
| **Jetty** | 10.0.24 | Development web server |
| **Docker** | Latest | Containerization |
| **JUnit** | 4.x/5.x | Unit testing |
| **Selenium** | Latest | Browser automation testing |

---

# Part 2: Setup & Installation

## Prerequisites

Before installing Apache Roller, ensure you have:

### Required Software

| Software | Minimum Version | Recommended | Purpose |
|----------|----------------|-------------|---------|
| **JDK** | 11 | 11 or 17 | Java Development Kit |
| **Maven** | 3.6 | 3.8+ | Build tool (for source build) |
| **Database** | See below | MySQL 8.0 | Data storage |
| **App Server** | Tomcat 9.0 | Tomcat 9.0.50+ | Web container |

### Database Requirements

Choose ONE of the following:

**For Development/Testing:**
- Apache Derby (embedded, no setup needed)

**For Production:**
- MySQL 5.7+ or MariaDB 10.2+
- PostgreSQL 9.6+
- Oracle 11g+

### System Requirements

- **RAM**: Minimum 2GB, Recommended 4GB+
- **Disk Space**: 500MB for application + database storage
- **Operating System**: Any OS supporting Java (Linux, Windows, macOS)

---

## Method 1: Quick Start with Docker

**⭐ Recommended for first-time users and testing**

This method requires only Docker - no Java, Maven, or database installation needed!

### Step 1: Install Docker

**Linux:**
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo systemctl start docker
```

**macOS/Windows:**
- Download Docker Desktop from https://www.docker.com/products/docker-desktop

### Step 2: Clone and Run

```bash
# Clone the repository
git clone https://github.com/apache/roller.git
cd roller

# Start Roller with Docker Compose
docker-compose up
```

### Step 3: Wait for Startup

You'll see output like:
```
roller-db-1   | PostgreSQL init process complete; ready for start up
roller-app-1  | Server started. Listening on port 8080
```

### Step 4: Access Roller

Open your browser: **http://localhost:8080/roller**

**What's Running:**
- ✅ Roller web application on port 8080
- ✅ PostgreSQL database (automatically configured)
- ✅ Data persisted in Docker volumes

### Step 5: Create Admin Account

You'll see the welcome screen:

![Welcome Screen](docs/images/user-guide-1-welcome.png)

Continue to [Initial Configuration](#initial-configuration) below.

---

## Method 2: Development Setup with Maven

**👨‍💻 Recommended for developers**

Perfect for development, testing, and making code changes.

### Step 1: Install Prerequisites

**Install JDK 11:**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-11-jdk

# macOS (using Homebrew)
brew install openjdk@11

# Verify installation
java -version  # Should show version 11.x
```

**Install Maven:**
```bash
# Ubuntu/Debian
sudo apt install maven

# macOS
brew install maven

# Verify installation
mvn -version  # Should show Maven 3.6+
```

### Step 2: Clone Repository

```bash
git clone https://github.com/apache/roller.git
cd roller
```

### Step 3: Build Roller

```bash
# Build all modules (skip tests for faster build)
mvn clean install -DskipTests=true
```

This will:
- Download all dependencies (~200MB first time)
- Compile Java source code
- Package the web application
- Build typically takes 2-5 minutes

### Step 4: Run with Embedded Jetty

```bash
# Navigate to app module
cd app

# Start Jetty server with embedded Derby database
mvn jetty:run
```

You'll see:
```
[INFO] Started Jetty Server
[INFO] Starting scanner at interval of 10 seconds.
```

### Step 5: Access Roller

Open browser: **http://localhost:8080/roller**

**Development Features:**
- ✅ Hot reload (changes reflected automatically)
- ✅ Embedded Derby database (no setup needed)
- ✅ Fast startup (30-60 seconds)
- ⚠️ Data cleared on each restart

**To stop**: Press `Ctrl+C` in terminal

---

## Method 3: Production Deployment

**🏢 Recommended for production environments**

For production use with Tomcat and MySQL/PostgreSQL.

### Step 1: Install Java

```bash
# Install OpenJDK 11
sudo apt update
sudo apt install openjdk-11-jdk

# Verify
java -version
```

### Step 2: Install & Configure Database

#### Option A: MySQL/MariaDB

```bash
# Install MySQL
sudo apt install mysql-server

# Secure installation
sudo mysql_secure_installation

# Login to MySQL
sudo mysql -u root -p
```

**Create database and user:**
```sql
CREATE DATABASE roller CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'roller'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON roller.* TO 'roller'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

**Run database schema:**
```bash
# Clone Roller if not done
git clone https://github.com/apache/roller.git
cd roller

# Run MySQL creation script
mysql -u roller -p roller < app/src/main/resources/dbscripts/mysql/createdb.sql
```

#### Option B: PostgreSQL

```bash
# Install PostgreSQL
sudo apt install postgresql postgresql-contrib

# Switch to postgres user
sudo -i -u postgres

# Create database and user
createdb roller
createuser roller
psql
```

```sql
ALTER USER roller WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE roller TO roller;
\q
exit
```

**Run database schema:**
```bash
psql -U roller -d roller -f app/src/main/resources/dbscripts/postgresql/createdb.sql
```

### Step 3: Install Apache Tomcat

```bash
# Download Tomcat 9
cd /opt
sudo wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.85/bin/apache-tomcat-9.0.85.tar.gz

# Extract
sudo tar xzvf apache-tomcat-9.0.85.tar.gz
sudo mv apache-tomcat-9.0.85 tomcat9

# Set permissions
sudo chmod +x /opt/tomcat9/bin/*.sh
```

### Step 4: Build Roller WAR

```bash
cd roller
mvn clean package -DskipTests=true
```

**Output**: `app/target/roller.war`

### Step 5: Configure Roller

Create configuration file:

```bash
sudo mkdir -p /opt/tomcat9/lib
sudo nano /opt/tomcat9/lib/roller-custom.properties
```

**For MySQL:**
```properties
# Database Configuration
database.jpa.configuration=roller-jpa-config
database.jdbc.driverClass=com.mysql.cj.jdbc.Driver
database.jdbc.connectionURL=jdbc:mysql://localhost:3306/roller?useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC
database.jdbc.username=roller
database.jdbc.password=your_secure_password

# File Upload
uploads.enabled=true
uploads.dir=/var/roller/uploads
uploads.types.allowed=image/jpeg,image/png,image/gif,image/svg+xml

# Search Index
search.enabled=true
search.index.dir=/var/roller/search-index

# Mail Configuration (optional)
mail.smtp.host=localhost
mail.smtp.port=25
mail.from=noreply@yourdomain.com

# Site URL (change to your domain)
site.absoluteurl=http://yourdomain.com
```

**For PostgreSQL:**
```properties
database.jdbc.driverClass=org.postgresql.Driver
database.jdbc.connectionURL=jdbc:postgresql://localhost:5432/roller
database.jdbc.username=roller
database.jdbc.password=your_secure_password
```

### Step 6: Copy Database Driver

**MySQL:**
```bash
# Download MySQL Connector
wget https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar
sudo cp mysql-connector-java-8.0.33.jar /opt/tomcat9/lib/
```

**PostgreSQL:**
```bash
# Download PostgreSQL Driver
wget https://jdbc.postgresql.org/download/postgresql-42.6.0.jar
sudo cp postgresql-42.6.0.jar /opt/tomcat9/lib/
```

### Step 7: Deploy to Tomcat

![Tomcat Deployment](docs/images/roller-install-guide-tomcat-deploy.png)

```bash
# Copy WAR file
sudo cp app/target/roller.war /opt/tomcat9/webapps/

# Create upload directory
sudo mkdir -p /var/roller/uploads
sudo mkdir -p /var/roller/search-index
sudo chown -R tomcat:tomcat /var/roller  # If running as tomcat user

# Start Tomcat
sudo /opt/tomcat9/bin/startup.sh

# Check logs
tail -f /opt/tomcat9/logs/catalina.out
```

### Step 8: Access Roller

Open browser: **http://yourserver:8080/roller**

![Tomcat Configuration](docs/images/roller-install-guide-tomcat.png)

---

## Initial Configuration

### Welcome & Setup Wizard

After accessing Roller for the first time, you'll go through initial setup:

#### 1. Welcome Screen

![Welcome Screen](docs/images/user-guide-1-welcome.png)

Click **"Get Started"** or **"Create Account"**

#### 2. User Registration

![User Registration](docs/images/user-guide-2-registration.png)

**Fill in registration form:**
- **Username**: Your login name (lowercase, no spaces)
- **Email Address**: For notifications and password recovery
- **Password**: Strong password (8+ characters)
- **Confirm Password**: Re-enter password
- **Full Name**: Your display name
- **Locale**: Choose your language

**Important**: The first user automatically becomes the system administrator!

Click **"Register"**

#### 3. Create Your First Blog

![Create Weblog](docs/images/user-guide-3-webblog.png)

**Blog Configuration:**
- **Blog Name**: Display name (e.g., "My Tech Blog")
- **Blog Handle**: URL identifier (e.g., "techblog" → `/roller/techblog`)
  - Must be unique
  - Lowercase, no spaces
  - Use hyphens for multiple words
- **Description**: Brief description of your blog
- **Email Address**: Contact email for this blog
- **Theme**: Choose a theme (you can change later)
- **Locale**: Blog language
- **Timezone**: For accurate timestamps
- **Entry Display Count**: Posts per page (default: 15)

Click **"Save"**

#### 4. Setup Complete!

You now have:
- ✅ Administrator account
- ✅ Your first blog
- ✅ Database tables created
- ✅ Ready to create content

---

# Part 3: Understanding the System

## First Login & Interface Overview

### Main Interface Components

#### Status Bar

![Status Bar](docs/images/user-guide-4-statusbar.png)

The top status bar shows:
- **👤 Logged-in User**: Your username
- **📝 Current Blog**: Active blog context
- **🏠 View Blog**: Link to see your published blog
- **⚙️ Settings**: Quick access to settings
- **🚪 Logout**: End your session

![Status Bar - Weblog View](docs/images/user-guide-5-statusbar-webblog.png)

When in blog context, additional information appears:
- Blog name and handle
- Last modified date
- Quick actions for the current blog

#### Navigation Menu

![Navigation Menu](docs/images/user-guide-6-navigation.png)

**Primary sections:**

1. **📝 Entries**
   - Create new blog posts
   - Manage existing entries
   - View drafts and scheduled posts

2. **💬 Comments**
   - Moderate comments
   - Approve/reject/spam
   - Manage comment settings

3. **🔗 Blogroll**
   - Manage links to other blogs
   - Organize in folders
   - Import/export OPML

4. **📁 Media**
   - Upload images and files
   - Organize in directories
   - Manage media library

5. **👥 Members** (for group blogs)
   - Invite contributors
   - Manage permissions
   - View member activity

6. **⚙️ Settings**
   - Blog configuration
   - Features and options
   - Delete blog

7. **🎨 Design**
   - Choose themes
   - Edit templates
   - Customize appearance

#### Main Menu

![Main Menu](docs/images/user-guide-7-main-menu.png)

**Quick actions:**
- **➕ New Entry**: Create a blog post immediately
- **👁️ View Blog**: Preview your published blog
- **⚙️ Settings**: Blog configuration
- **🚪 Logout**: End session

---

## Content Management System

### Creating Blog Posts

#### The Blog Editor

![Blog Editor](docs/images/user-guide-8-editor.png)

The entry editor is where you create and edit blog posts.

**Editor Components:**

**1. Title Field**
- Main headline for your post
- Used in SEO and social sharing
- Keep concise and descriptive

**2. Content Editor**
- WYSIWYG rich text editor
- Formatting toolbar
- Media insertion
- HTML source view

**3. Formatting Options**

![Formatting Options](docs/images/user-guide-formatting.png)

**Available formats:**
- **Rich Text**: WYSIWYG editor (default)
- **HTML**: Direct HTML editing
- **Markdown**: Markdown syntax
- **Plain Text**: No formatting
- **Auto-format**: Automatic paragraph detection

**4. Toolbar Features:**
- **Bold** / *Italic* / <u>Underline</u>
- Headers (H1 - H6)
- Lists (bulleted/numbered)
- Links and anchors
- Images
- Code blocks
- Blockquotes
- Tables
- Horizontal rules

![Entry Editor Detail](docs/readme-images/edit-entry.jpg)

**5. Entry Options (Right Sidebar):**

**Status:**
- **Draft**: Not visible to public
- **Published**: Live on your blog
- **Scheduled**: Publish at future date/time
- **Pending**: Awaiting approval (for limited users)

**Category:**
- Select from existing categories
- Organize your content
- Generates category-specific feeds

**Tags:**
- Comma-separated keywords
- Improves discoverability
- Creates tag pages and clouds

**Publish Date:**
- Schedule for future publication
- Change publication timestamp
- Useful for backdating content

**Search Description:**
- Custom meta description for SEO
- Shows in search results
- 150-160 characters recommended

**Permalink:**
- Custom URL slug
- Auto-generated from title
- Can be customized

**Allow Comments:**
- Enable/disable for this post
- Override global setting

**6. Save Options:**
- **Save**: Save as draft
- **Post to Weblog**: Publish immediately
- **Preview**: See how it looks before publishing

### Managing Blog Entries

#### Entries List

![Blog Entries List](docs/images/user-guide-9-entries.png)

**View all your posts:**

**Columns:**
- **Title**: Click to edit
- **Category**: Assigned category
- **Status**: Current state (Published/Draft/Scheduled)
- **Date**: Publication or creation date
- **Actions**: Edit, Delete, View buttons

**Status Indicators:**
- 🟢 **Published**: Live and visible
- 🟡 **Draft**: Saved but not published
- 🔵 **Scheduled**: Will publish at set time
- 🟠 **Pending**: Awaiting approval

**Filter Options:**
- Search by keyword
- Filter by category
- Filter by status
- Sort by date, title, status

**Bulk Actions:**
- Select multiple entries
- Change status
- Delete multiple entries
- Move to category

![Entries Overview](docs/readme-images/entries.jpg)

**Entry Management Tips:**
- Use drafts for work-in-progress
- Schedule posts for consistent publishing
- Regular content helps SEO
- Archive outdated content

### Categories

![Categories](docs/images/user-guide-10-categories.png)

**Organize your content with categories.**

**Category Features:**

**1. Hierarchical Structure**
- Create parent categories
- Add subcategories
- Unlimited nesting levels

**2. Category Properties:**
- **Name**: Display name
- **Description**: SEO-friendly text
- **Image**: Category icon/image
- **Parent**: Make it a subcategory

**3. Category Management:**
- **Create**: Add new categories
- **Edit**: Modify properties
- **Reorder**: Drag and drop to reorder
- **Delete**: Remove (reassign posts first)

**4. Category Feeds:**
- Each category gets its own RSS/Atom feed
- URL: `/roller/{blog}/category/{categoryname}/feed`

**Example Category Structure:**
```
Technology
├── Programming
│   ├── Java
│   └── Python
├── Web Development
│   ├── Frontend
│   └── Backend
└── DevOps
    ├── Docker
    └── Kubernetes
```

### Blogroll

![Blogroll](docs/images/user-guide-11-blogroll.png)

**Manage links to other blogs and websites.**

**Blogroll Features:**

**1. Link Management:**
- Add links to favorite blogs
- Organize in folders
- Display in sidebar

**2. Link Properties:**
- **Name**: Display text
- **URL**: Link destination
- **Description**: Tooltip or description
- **Image**: Icon for the link
- **Relationship**: Friend, colleague, partner, etc.
- **Folder**: Organization category

**3. Folders:**
- Create folders for organization
- Example: "Tech Blogs", "Friends", "Resources"

**4. OPML Support:**
- **Import**: Import blogroll from OPML file
- **Export**: Share your blogroll
- Compatible with feed readers

**5. Display Options:**
- Choose which folders to show
- Order links
- Show/hide descriptions

---

## User Management System

### Understanding User Roles

Apache Roller has a role-based permission system:

| Role | Permissions |
|------|-------------|
| **Admin** | • Full system access<br>• Manage all blogs<br>• User management<br>• System configuration<br>• Database operations |
| **Editor** | • Manage assigned blog(s)<br>• Publish any entry<br>• Moderate all comments<br>• Manage members<br>• Configure blog settings |
| **Author** | • Create/edit own entries<br>• Publish own entries<br>• Upload media<br>• Manage own comments |
| **Limited** | • Create draft entries only<br>• Cannot publish<br>• Limited media access<br>• Entries need approval |

### User Administration (Admin Only)

![User Administration](docs/images/user-guide-24-user-admin.png)

**System administrators can:**

**1. View All Users:**
- List of all registered users
- User status (enabled/disabled)
- Registration date
- Last login

**2. User Actions:**
- **Edit**: Modify user profile
- **Enable/Disable**: Temporarily disable accounts
- **Delete**: Remove user (handle content ownership)
- **Reset Password**: Administrative password reset

![User Admin Extended](docs/images/user-guide-25-user-admin.png)

**3. Create Users:**
- Add users without registration process
- Set initial password
- Assign global role
- Bypass email verification

**4. Global Roles:**
- Assign admin privileges
- Control system access
- Manage permissions

### Member Management (Group Blogs)

![Member List](docs/images/user-guide-21-member.png)

**For collaborative blogging:**

**What are Members?**
- Users who can contribute to your blog
- Each member has a role
- Multiple authors on one blog

**Member Information:**
- **Username**: Member's username
- **Full Name**: Display name
- **Email**: Contact email
- **Role**: Permission level (Editor/Author/Limited)
- **Status**: Pending/Active
- **Actions**: Edit role, Remove member

#### Inviting Members

![Invite Member](docs/images/user-guide-22-invite-member.png)

**Invitation Process:**

**Step 1: Select User**
- Choose from registered users
- Cannot invite unregistered users
- One invitation per user per blog

**Step 2: Assign Role**
- **Editor**: Full blog control
- **Author**: Create and publish
- **Limited**: Create drafts only

**Step 3: Send Invitation**
- System sends email notification
- User receives invitation in Roller
- Link to accept/decline

**Step 4: User Accepts**
- User logs in
- Sees invitation
- Accepts or declines

**Step 5: Active Member**
- User gains access to blog
- Can perform role-specific actions
- Appears in member list

**Collaborative Workflow Benefits:**
- Multiple perspectives
- Content variety
- Shared workload
- Different expertise areas

**Member Management Best Practices:**
- Define clear roles and responsibilities
- Limit Editor role to trusted members
- Regular review of member list
- Remove inactive members
- Use Limited role for new contributors

---

## Media Management System

Media management in Roller allows you to upload, organize, and use images, documents, and other files in your blog posts.

### Media Library Overview

![Media Files](docs/images/user-guide-12-media.png)

**What can you upload?**

**Supported File Types:**
- **Images**: JPG, PNG, GIF, SVG, WebP, BMP
- **Documents**: PDF, DOC, DOCX, TXT, RTF
- **Audio**: MP3, WAV, OGG (for podcasts)
- **Video**: MP4, WebM, AVI
- **Archives**: ZIP, TAR, GZ

**Media Library Features:**
- **Directory Organization**: Organize files in folders
- **Thumbnail Generation**: Automatic for images
- **Metadata**: Title, description, tags, copyright
- **Search**: Find files quickly
- **Usage Tracking**: See where files are used
- **Direct URLs**: Link directly to files

**View Modes:**
- **Grid View**: Thumbnails
- **List View**: Detailed information
- **Folders**: Navigate directory structure

### Uploading Media

![Add Media](docs/images/user-guide-13-add-media.png)

**Upload Process:**

**Step 1: Access Media Upload**
- Navigate to **Media** section
- Click **"Upload Files"**

**Step 2: Select Files**
- **Click to Browse**: Traditional file picker
- **Drag & Drop**: Drag files from your computer
- **Multiple Files**: Upload many files at once

**Step 3: Choose Destination**
- Select target directory
- Create new folder if needed
- Organize by date, topic, or type

**Step 4: Upload**
- Click **"Upload"**
- Progress bar shows upload status
- Large files may take time

![Upload Complete](docs/images/user-guide-14-upload-complete.png)

**Step 5: Upload Complete**
- Files appear in media library
- Thumbnails generated automatically
- Ready to use in blog posts

**Upload Information:**
- File name and size
- Upload timestamp
- Direct URL for embedding
- Quick insert button

**Tips:**
- ✅ Optimize images before upload (compress)
- ✅ Use descriptive filenames
- ✅ Organize in folders from the start
- ✅ Add metadata after upload

### Editing Media Properties

![Edit Media](docs/images/user-guide-15-edit-media.png)

**Editable Properties:**

**1. Basic Information:**
- **Name**: Display name (not filename)
- **Title**: Full title for the image
- **Description**: Alt text for accessibility and SEO
- **Tags**: Searchable keywords

**2. Attribution:**
- **Copyright Notice**: Copyright information
- **Creator Credit**: Photographer/artist credit
- **Rights**: Usage rights and licenses

**3. Organization:**
- **Directory**: Move to different folder
- **Categories**: Assign to categories

**4. Advanced:**
- **Content Type**: MIME type (auto-detected)
- **Dimensions**: Width x Height (for images)
- **File Size**: Size in KB/MB

**Why Add Metadata?**
- 🔍 **SEO**: Alt text helps search engines
- ♿ **Accessibility**: Screen readers use descriptions
- 🔎 **Search**: Find files easily
- 📝 **Attribution**: Proper credit to creators
- 📊 **Organization**: Better file management

### Directory Organization

![Media Directory](docs/images/user-guide-media-directory.png)

**Organize your media files:**

**Directory Features:**

**1. Hierarchical Structure:**
```
/media
  /2024
    /january
      /screenshots
      /photos
    /february
  /logos
  /banners
```

**2. Folder Operations:**
- **Create Folder**: Organize by topic, date, type
- **Rename Folder**: Change folder names
- **Move Files**: Drag-drop between folders
- **Delete Folder**: Remove empty folders

**3. Organization Strategies:**

**By Date:**
```
/2024
  /01-January
  /02-February
  /03-March
```

**By Topic:**
```
/tutorials
/reviews
/news
/personal
```

**By Type:**
```
/images
/documents
/videos
/downloads
```

**4. Folder Permissions:**
- All media visible to blog members
- Access controlled by blog membership

### File Upload Configuration (Admin)

![File Upload Configuration](docs/images/user-guide-29-fileupload.png)

**Administrators can configure:**

**1. Upload Limits:**
- **Maximum File Size**: Per-file limit (e.g., 10MB)
- **Total User Quota**: Storage limit per user
- **Blog Quota**: Total storage per blog

**2. Allowed File Types:**
- **MIME Types**: Specify allowed types
- **Extension Filter**: Block/allow by extension
- **Security**: Prevent executable uploads

**3. Image Processing:**
- **Thumbnail Size**: Default thumbnail dimensions
- **Image Scaling**: Auto-resize large images
- **Quality Settings**: JPEG compression level

**4. Storage Configuration:**
- **Upload Directory**: File system location
- **URL Path**: Public access URL
- **Storage Backend**: Local, S3, etc.

**Example Configuration:**
```properties
# File upload settings
uploads.enabled=true
uploads.dir=/var/roller/uploads
uploads.maxFileMB=10
uploads.maxDirMB=100
uploads.types.allowed=image/jpeg,image/png,image/gif,application/pdf
```

### Using Media in Posts

**To insert media in a blog post:**

**Method 1: Media Picker**
1. In entry editor, click **"Insert Image"**
2. Browse media library
3. Select file
4. Choose size (thumbnail, medium, large, original)
5. Click **"Insert"**

**Method 2: Direct URL**
- Copy file URL from media library
- Use in HTML: `<img src="URL">`
- Link to files: `<a href="URL">Download</a>`

**Method 3: Drag & Drop** (if supported)
- Drag image from media library
- Drop into editor
- Auto-inserted at cursor position

### Podcast Support

![Podcast Settings](docs/images/user-guide-podcast.png)

**Podcasting with Roller:**

**What is Podcasting?**
- Audio/video episodes in blog posts
- Distributed via RSS with enclosures
- Subscribers auto-download episodes

**Setup Podcasting:**

**1. Upload Audio/Video:**
- Upload MP3, M4A, or video files
- Use Media Library

**2. Attach to Entry:**
- Edit blog post
- Attach media file as **enclosure**
- Enter file URL and MIME type

**3. Podcast Metadata:**
- **Title**: Episode title
- **Description**: Episode summary
- **Duration**: Length (HH:MM:SS)
- **Keywords**: Episode tags
- **Category**: iTunes category

**4. iTunes Tags:**
- Configure in blog settings
- Add iTunes-specific metadata
- Author, subtitle, summary
- Artwork (1400x1400 px recommended)

**5. RSS Feed:**
- Roller auto-generates podcast feed
- Submit feed to iTunes, Spotify
- Feed URL: `/roller/{blog}/feed/entries/rss`

**6. Embedded Player:**
- Audio/video player auto-embeds
- Visitors can listen on your blog
- Download option available

---

## Theme & Design System

Roller's theme system gives you complete control over your blog's appearance.

### Design Interface

![Design Menu](docs/images/user-guide-20-design-menu.png)

**Design Options:**

1. **Theme**: Select and customize pre-built themes
2. **Templates**: Edit Velocity templates directly
3. **Stylesheet**: Add custom CSS
4. **Resources**: Upload theme assets (images, JS)
5. **Advanced**: Custom JavaScript, meta tags

### Selecting a Theme

![Design Theme](docs/images/user-guide-design-theme.png)

**Available Themes:**

Each theme has unique features and layouts:

**1. Basic Theme**
- Simple, clean design
- Fast loading
- Easy to customize
- Good for beginners

**2. Effortless Theme**
- Minimalist design
- Focus on content
- Large typography
- Photography blogs

**3. Gaurav Theme**
- Modern responsive design
- Mobile-optimized
- Social media integration
- Magazine-style layout

**4. Snapshot Theme**
- Photo-centric design
- Large images
- Gallery support
- Portfolio blogs

**5. Stripes Theme**
- Traditional blog layout
- Sidebar navigation
- Widget areas
- Classic feel

**6. Awesome Theme**
- Feature-rich
- Multiple layouts
- Customizable widgets
- Professional appearance

**Theme Selection:**
- Preview theme before applying
- Switch themes anytime
- Theme settings preserved
- Content unchanged

### Customizing Themes

![Customize Theme - Step 1](docs/images/customize-theme-1.png)

**Basic Customization:**

**1. Layout Options:**
- **Sidebar Position**: Left, Right, or No sidebar
- **Content Width**: Full width or fixed
- **Column Ratios**: Sidebar width vs content

**2. Color Scheme:**
- **Primary Color**: Main brand color
- **Secondary Color**: Accent color
- **Background**: Page background
- **Text Color**: Body text
- **Link Color**: Hyperlinks
- **Header/Footer**: Special areas

**3. Typography:**
- **Font Family**: Choose fonts
  - System fonts (fastest)
  - Google Fonts
  - Custom fonts
- **Font Sizes**: Body, headings, etc.
- **Line Height**: Readability
- **Letter Spacing**: Character spacing

![Customize Theme - Step 2](docs/images/customize-theme-2.png)

**Advanced Customization:**

**4. Header:**
- **Logo**: Upload blog logo
- **Tagline**: Subtitle/slogan
- **Background Image**: Header background
- **Height**: Header size
- **Navigation**: Menu style

**5. Widgets:**
- **Sidebar Widgets**: Choose which to display
  - Recent posts
  - Categories
  - Tag cloud
  - Blogroll
  - Search
  - Archives
- **Widget Order**: Drag to reorder
- **Custom Widgets**: Add HTML/JS

**6. Footer:**
- **Copyright Notice**: Auto or custom
- **Footer Links**: Social media, pages
- **Footer Columns**: 1, 2, 3, or 4 columns
- **Footer Text**: Additional information

**7. Custom CSS:**
```css
/* Override theme styles */
body {
    font-family: 'Georgia', serif;
}

.entry-title {
    color: #333;
    font-size: 2em;
}

.sidebar {
    background-color: #f5f5f5;
}
```

**8. Custom JavaScript:**
```javascript
// Add Google Analytics
(function(i,s,o,g,r,a,m){...})(window,document,'script',...);

// Custom functionality
document.addEventListener('DOMContentLoaded', function() {
    // Your code here
});
```

### Template Editing

![Templates Overview](docs/images/templates.png)

**What are Templates?**

Templates are files that control how your blog displays content. Roller uses Apache Velocity template engine.

**Template Types:**

| Template | Purpose |
|----------|---------|
| **Weblog** | Main blog page (list of entries) |
| **Permalink** | Single entry view |
| **Search Results** | Search result display |
| **Day Template** | Day archive view |
| **Month Template** | Month archive view |
| **Stylesheet** | CSS styles |
| **Custom Pages** | User-defined pages |

![Template Edit](docs/images/template-edit.png)

**Template Editor Features:**

**1. Code Editor:**
- Syntax highlighting for Velocity
- Line numbers
- Auto-indentation
- Code folding

**2. Editor Toolbar:**
- **Save**: Save changes
- **Revert**: Undo all changes
- **Preview**: Test template
- **Help**: Velocity syntax help

**3. Template Variables:**

Access blog data through model objects:

```velocity
## Blog information
$model.weblog.name
$model.weblog.description
$model.weblog.handle

## Blog entries
#foreach($entry in $model.entries)
    <article>
        <h2><a href="$entry.permalink">$entry.title</a></h2>
        <time>$entry.pubTime</time>
        <div>$entry.displayContent</div>
    </article>
#end

## Categories
#foreach($cat in $model.categories)
    <a href="$cat.permalink">$cat.name</a>
#end

## User information (if authenticated)
#if($model.authenticatedUser)
    Welcome, $model.authenticatedUser.fullName!
#end
```

![Model Objects](docs/images/model-object.png)

**Available Model Objects:**

**$model.weblog:**
- `.name` - Blog name
- `.description` - Blog description
- `.handle` - URL handle
- `.locale` - Language
- `.timezone` - Timezone

**$model.entries:**
- `.title` - Entry title
- `.text` - Entry content
- `.displayContent` - Formatted content
- `.permalink` - Entry URL
- `.pubTime` - Publication date
- `.category` - Entry category
- `.tags` - Entry tags
- `.commentCount` - Number of comments

**$model.categories:**
- `.name` - Category name
- `.description` - Description
- `.permalink` - Category URL

**$model.tags:**
- Tag cloud data
- Tag counts and links

**4. Custom Macros:**

Create reusable template snippets:

```velocity
## Define macro
#macro(showEntry $entry)
    <article class="blog-entry">
        <h2><a href="$entry.permalink">$entry.title</a></h2>
        <time datetime="$entry.pubTime">
            $utilities.formatDate($entry.pubTime, "MMMM dd, yyyy")
        </time>
        <div class="entry-content">
            $entry.displayContent
        </div>
        <div class="entry-meta">
            Category: <a href="$entry.category.permalink">
                $entry.category.name
            </a>
            #if($entry.tags.size() > 0)
                | Tags:
                #foreach($tag in $entry.tags)
                    <a href="$tag.permalink">$tag.name</a>
                #end
            #end
        </div>
    </article>
#end

## Use macro
#foreach($entry in $model.entries)
    #showEntry($entry)
#end
```

**5. Template Best Practices:**
- ✅ Test changes on copy first
- ✅ Keep backups of working templates
- ✅ Use macros for repeated code
- ✅ Comment your code
- ✅ Validate HTML output
- ✅ Check mobile responsiveness

---

## Comment Management System

Comments allow readers to interact with your blog posts.

### Comment Overview

![Comments](docs/images/user-guide-18-comments.png)

**Comment Management Interface:**

**Comment List Shows:**
- **Commenter Name**: Who posted
- **Entry Title**: Which post
- **Comment Text**: The comment content
- **Date/Time**: When posted
- **Status**: Approved, Pending, Spam
- **Actions**: Approve, Delete, Mark as Spam

**Comment States:**

| State | Description |
|-------|-------------|
| **Approved** | Visible on blog |
| **Pending** | Awaiting moderation |
| **Spam** | Marked as spam |
| **Deleted** | Permanently removed |

![Comments Detail](docs/images/user-guide-comments.png)

**Comment Actions:**

**1. Approve**
- Makes comment visible
- Publishes on entry page
- Sends notification (if enabled)

**2. Delete**
- Permanently removes comment
- Cannot be undone
- Use for spam or inappropriate content

**3. Mark as Spam**
- Hides comment
- Trains spam filter
- Helps prevent future spam

**4. Edit**
- Modify comment content
- Fix formatting
- Remove inappropriate parts

**5. Ban Commenter**
- Block IP address
- Block email address
- Prevent future comments

**Batch Operations:**
- Select multiple comments
- Apply action to all selected
- Efficient for spam cleanup

### Comment Configuration

![Comment Settings](docs/images/user-guide-27-comments.png)

**Configure comment behavior:**

**1. Global Comment Settings:**

**Enable Comments:**
- ☑️ **Allow comments**: Master switch
- ☐ **Disable comments**: Turn off completely

**Moderation Level:**
- **No moderation**: Auto-approve all
- **Moderate all**: Review before publishing
- **Moderate first-time**: Only new commenters
- **Moderate authenticated**: Only logged-in users can skip moderation

**2. Comment Form Settings:**

**Required Fields:**
- ☑️ **Name**: Commenter must provide name
- ☑️ **Email**: Email address required
- ☐ **Website**: Optional website URL

**3. Notification Settings:**

- ☑️ **Email on new comment**: Notify blog owner
- ☑️ **Email on spam**: Alert about spam
- **Email Address**: Where to send notifications

**4. HTML & Formatting:**

**Allow HTML Tags:**
- Choose allowed tags: `<b>`, `<i>`, `<a>`, etc.
- Prevent `<script>` and dangerous tags
- Auto-sanitize input

**Auto-formatting:**
- Convert URLs to links
- Line breaks to `<br>`
- Paragraphs to `<p>`

**5. Comment Limits:**

- **Maximum Length**: Character limit (e.g., 5000)
- **Time Window**: Days to allow comments (e.g., 30 days)
- **Close old entries**: Auto-disable on old posts

**6. CAPTCHA:**

- ☑️ **Enable CAPTCHA**: Prevent bots
- **Type**: reCAPTCHA, simple math, etc.
- **Anonymous only**: Logged-in users skip

**Example Configuration:**
```properties
# Moderate first-time commenters only
comment.moderation.enabled=true
comment.moderation.firstTimeOnly=true

# Require email
comment.requireEmail=true

# Allow basic HTML
comment.allowedHTML=<b>,<i>,<a>,<blockquote>

# CAPTCHA for anonymous
comment.captcha.enabled=true
comment.captcha.anonymousOnly=true
```

### Spam Protection

![Spam Settings](docs/images/user-guide-spam.png)

**Protect against comment spam:**

**1. Blacklist/Blocklist:**

**Word Blacklist:**
```
# Block comments containing these words
viagra
cialis
pharmacy
casino
poker
[url=
<a href
```

**IP Blacklist:**
```
# Block specific IP addresses
192.168.1.100
10.0.0.50
```

**Email Blacklist:**
```
# Block email patterns
*@spam-domain.com
fake@*
*viagra*
```

**2. URL Filtering:**

- **Maximum URLs**: Limit links in comments (e.g., 3)
- **Suspicious TLDs**: Block .ru, .cn, etc. (configurable)
- **Rel="nofollow"**: Add to all comment links (SEO)

**3. Content Analysis:**

**Automatic Spam Detection:**
- ☑️ **Keyword matching**: Check against blacklist
- ☑️ **URL count**: Too many links = spam
- ☑️ **Repeat content**: Same comment multiple times
- ☑️ **Foreign characters**: Excessive special chars

**4. External Spam Services:**

**Akismet Integration:**
- Cloud-based spam detection
- Machine learning powered
- Requires API key
- Very effective

**Configuration:**
```properties
comment.spam.akismet.enabled=true
comment.spam.akismet.key=YOUR_API_KEY
```

**5. Comment Throttling:**

**Rate Limiting:**
- Max comments per IP per hour
- Prevents spam floods
- Example: 5 comments/hour

**Delay Between Comments:**
- Minimum seconds between comments
- Prevents automated posting

**6. Moderation Queue:**

- All suspected spam goes to queue
- Review before deleting
- Train filter by marking spam
- False positives can be approved

**Best Practices:**
- ✅ Start with moderate-all, then relax
- ✅ Regularly update blacklist
- ✅ Review spam queue weekly
- ✅ Use Akismet for large sites
- ✅ Block persistent spammers by IP
- ✅ Keep legitimate comments flowing

---

## Configuration & Settings

### Blog Settings

![Blog Settings](docs/images/user-guide-setting.png)

**Basic Blog Configuration:**

**1. Blog Information:**
- **Blog Name**: Display name
- **Description**: Meta description
- **Email Address**: Contact email
- **Locale**: Language/region
- **Timezone**: For timestamps

**2. Blog Options:**
- **Entry Display Count**: Posts per page
- **Active**: Enable/disable blog
- **Front Page Blog**: Show on site front page

![Blog Settings Extended](docs/images/user-guide-settings.png)

**3. Entry Settings:**

**Default Entry:**
- **Allow Comments**: Default for new entries
- **Comment Status**: Auto-approve or moderate
- **Editor**: Rich text or HTML

**4. Comment Settings:**
- **Allow Comments**: Master switch
- **Moderation**: Level of moderation
- **Email Notifications**: Comment alerts
- **CAPTCHA**: Bot prevention

**5. Features:**
- **Search**: Enable/disable search
- **Tagline**: Show blog tagline
- **Sidebar**: Show/hide sidebar
- **Breadcrumbs**: Navigation trail

**6. Plugins:**
- Enable page model plugins
- Configure plugin settings
- Custom renderers

**7. Analytics:**
- **Google Analytics**: Add tracking code
- **Custom Analytics**: Other services
- **Stats Tracking**: Built-in stats

### Site Administration

![Site Settings](docs/images/user-guide-26-site-setting.png)

**System-Wide Configuration (Admin Only):**

**1. Site Information:**
- **Site Name**: Installation name
- **Site Description**: Overall description
- **Admin Email**: System administrator email
- **Default Locale**: System language
- **Timezone**: Server timezone

**2. User Registration:**
- **Allow Registration**: Enable self-registration
- **Email Verification**: Require email confirmation
- **Default Role**: Role for new users
- **Admin Approval**: Require admin approval

**3. Front Page:**
- **Front Page Blog**: Which blog on homepage
- **Aggregated Front Page**: Show all blogs
- **Planet Front Page**: Use planet aggregator

**4. Blog Defaults:**
- **Default Theme**: Theme for new blogs
- **Allow Comments**: Default comment setting
- **Entries Per Page**: Default pagination

**5. System Features:**
- **Blog Creation**: Who can create blogs
  - Anyone
  - Only admins
  - Registered users
- **Multiple Blogs**: Users can have multiple blogs
- **Group Blogs**: Allow multi-author blogs

**6. Upload Settings:**
- **Enabled**: Allow file uploads
- **Max File Size**: Per-file limit
- **User Quota**: Storage per user
- **Allowed Types**: MIME types

**7. Email Configuration:**
```properties
# SMTP Settings
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.smtp.username=your-email@gmail.com
mail.smtp.password=your-password
mail.smtp.starttls.enable=true
mail.from=noreply@yourdomain.com
```

**8. Search Configuration:**
- **Search Enabled**: Enable search feature
- **Index Directory**: Lucene index location
- **Rebuild Index**: Reindex all content

**9. Cache Settings:**
- **Enable Caching**: Performance boost
- **Cache Size**: Memory allocation
- **Cache Timeout**: Expiration time

---

# Part 4: Advanced Features

## Feed Management & Syndication

![Feed Configuration](docs/images/user-guide-28-feed.png)

### Understanding RSS/Atom Feeds

**What are Feeds?**
- Syndication format for blog content
- Readers subscribe to get updates
- Automatic delivery of new posts

**Feed Formats:**

**1. RSS 2.0**
- Most common format
- Wide compatibility
- XML-based

**2. Atom 1.0**
- More structured
- Better internationalization
- Recommended by W3C

### Feed Configuration

**Feed Settings:**

**1. Format:**
- Choose RSS or Atom
- Both can be enabled
- Different URLs for each

**2. Entry Count:**
- Number of entries in feed
- Typical: 10-20 entries
- Balance freshness vs. size

**3. Content Type:**
- **Full Content**: Complete post in feed
  - Pros: Readers see everything
  - Cons: Larger feed size
- **Excerpt/Summary**: Partial content
  - Pros: Smaller feed, drives traffic
  - Cons: Readers must click through

**4. Excerpt Length:**
- Characters to include in summary
- Typical: 200-500 characters
- Auto-truncate at word boundary

**5. Custom Feed:**
- Filter by category
- Filter by tag
- Custom entry selection

### Available Feeds

**Feed URLs:**

**Main Blog Feed:**
```
RSS: /roller/{bloghandle}/feed/entries/rss
Atom: /roller/{bloghandle}/feed/entries/atom
```

**Category Feeds:**
```
RSS: /roller/{bloghandle}/feed/entries/rss?cat={category}
Atom: /roller/{bloghandle}/feed/entries/atom?cat={category}
```

**Tag Feeds:**
```
RSS: /roller/{bloghandle}/feed/entries/rss?tag={tagname}
Atom: /roller/{bloghandle}/feed/entries/atom?tag={tagname}
```

**Comment Feeds:**
```
RSS: /roller/{bloghandle}/feed/comments/rss
Atom: /roller/{bloghandle}/feed/comments/atom
```

**Search Feeds:**
```
RSS: /roller/{bloghandle}/feed/entries/rss?q={search-term}
```

### Podcast Feeds

**Configure for podcasting:**

**iTunes Tags:**
- Podcast title and subtitle
- Author information
- Artwork (1400x1400px)
- Category and subcategory
- Explicit content flag

**Episode Data:**
- Audio/video enclosure URL
- File size and duration
- Episode number
- Season number

**Feed Enhancement:**
```xml
<itunes:author>Your Name</itunes:author>
<itunes:summary>Podcast description</itunes:summary>
<itunes:image href="artwork-url.jpg"/>
<itunes:category text="Technology"/>
```

### Feed Discovery

**Auto-discovery tags in HTML:**
```html
<link rel="alternate" type="application/rss+xml" 
      title="RSS Feed" href="/feed/entries/rss"/>
<link rel="alternate" type="application/atom+xml" 
      title="Atom Feed" href="/feed/entries/atom"/>
```

**Benefits:**
- Browsers auto-detect feeds
- Feed readers find feeds easily
- Standard web practice

---

## Planet Feed Aggregator

![Planet Configuration](docs/images/user-guide-32-planet-config.png)

### What is Planet?

**Planet Aggregator** combines RSS/Atom feeds from multiple blogs into a single unified view.

**Use Cases:**
- **Company Blog Hub**: Aggregate all employee blogs
- **Topic Aggregator**: Collect blogs about specific topic
- **Community Portal**: Central place for community content
- **Team Blog**: Combine project member blogs

### Planet Configuration

**Planet Settings:**

**1. Planet Information:**
- **Planet Title**: Name of aggregated view
- **Planet Description**: What it aggregates
- **Admin Email**: Planet administrator

**2. Display Settings:**
- **Entries to Display**: How many entries to show
- **Days of Content**: Show last X days
- **Grouping**: By blog or by date

**3. Update Frequency:**
- **Check Interval**: How often to fetch feeds
- Typical: Every 1-6 hours
- Balance freshness vs. server load

**4. Feed Processing:**
- **Max Entries per Feed**: Limit per source
- **Content Filtering**: Remove certain content
- **Error Handling**: Skip failed feeds

### Managing Subscriptions

![Subscription Management](docs/images/user-guide-33-subscription.png)

**Add Feed Subscription:**

**Step 1: Enter Feed URL**
- Paste RSS/Atom feed URL
- System validates feed
- Fetches feed metadata

**Step 2: Configure Subscription**
- **Title**: Override feed title (optional)
- **Author**: Override author name
- **Feed URL**: The RSS/Atom URL
- **Site URL**: Blog homepage

**Step 3: Settings**
- **Active**: Enable/disable subscription
- **Category**: Organize feeds
- **Rank**: Priority order

**Step 4: Save**
- Subscription added
- First fetch scheduled
- Entries appear in planet

**Subscription List:**
- View all subscribed feeds
- Last update time
- Entry count
- Health status (working/broken)

**Subscription Management:**
- **Edit**: Modify settings
- **Refresh**: Force immediate fetch
- **Deactivate**: Temporarily disable
- **Delete**: Remove subscription

### Planet Display

**Create Planet Page:**

**1. Create Custom Page Template**
```velocity
## Planet page template
<h1>$planet.title</h1>
<p>$planet.description</p>

#foreach($entry in $planet.aggregation)
    <article>
        <h2><a href="$entry.permalink">$entry.title</a></h2>
        <p class="meta">
            By $entry.author | 
            From <a href="$entry.weblogHandle">$entry.weblogTitle</a> |
            $entry.pubTime
        </p>
        <div>$entry.summary</div>
        <a href="$entry.permalink">Read more...</a>
    </article>
#end
```

**2. Planet Feed**
- Planet generates its own feed
- Subscribers get all aggregated content
- URL: `/roller/planet/feed/entries/rss`

---

## API & External Integration

![API Configuration](docs/images/user-guide-17-api.png)

### Supported APIs

Roller provides multiple APIs for external access:

**1. AtomPub (Atom Publishing Protocol)**
- RESTful API
- Standard protocol (RFC 5023)
- Create, read, update, delete entries
- Media upload support
- Authentication: WSSE or Basic Auth

**2. MetaWeblog API**
- XML-RPC based
- Most compatible with blog clients
- Entry management
- Media upload
- Category support

**3. Blogger API**
- Legacy XML-RPC API
- Basic functionality
- Backward compatibility

### API Configuration

**Enable APIs:**

**Settings → API Access**
- ☑️ **Enable AtomPub**: REST API
- ☑️ **Enable MetaWeblog**: XML-RPC
- ☑️ **Enable Blogger**: Legacy API

**Authentication:**
- Use Roller username/password
- HTTPS recommended for security
- API key support (optional)

### API Endpoints

**AtomPub Service Document:**
```
GET /roller/api/{bloghandle}/service
```

**AtomPub Entry Collection:**
```
GET    /roller/api/{bloghandle}/entries      # List entries
POST   /roller/api/{bloghandle}/entries      # Create entry
GET    /roller/api/{bloghandle}/entry/{id}   # Get entry
PUT    /roller/api/{bloghandle}/entry/{id}   # Update entry
DELETE /roller/api/{bloghandle}/entry/{id}   # Delete entry
```

**MetaWeblog Endpoint:**
```
POST /roller/xmlrpc
```

### Using Blog Clients

**Compatible Desktop Clients:**

**1. MarsEdit (macOS)**
- Professional blog editor
- WYSIWYG editor
- Media management
- Multi-blog support

**Configuration:**
```
API: MetaWeblog API
Endpoint: https://yourblog.com/roller/xmlrpc
Blog ID: {your-blog-handle}
Username: {your-username}
Password: {your-password}
```

**2. Windows Live Writer**
- Free Microsoft tool
- Rich editor
- Plugin support
- Photo editing

**3. BlogJet**
- Windows client
- Offline editing
- Categories and tags
- Media library

**4. Ecto**
- macOS client
- Multiple blog support
- Markdown support

**Mobile Apps:**
- Many mobile apps support MetaWeblog API
- Search "MetaWeblog client" in app stores

### API Usage Examples

**Python Example (AtomPub):**
```python
import requests
from requests.auth import HTTPBasicAuth

# Configuration
blog_url = "https://yourblog.com/roller/api/myblog"
auth = HTTPBasicAuth('username', 'password')

# Create new entry
entry_xml = """
<entry xmlns="http://www.w3.org/2005/Atom">
    <title>My New Post</title>
    <content type="html">
        <![CDATA[<p>This is my blog post content.</p>]]>
    </content>
    <category term="Technology"/>
</entry>
"""

response = requests.post(
    f"{blog_url}/entries",
    data=entry_xml,
    headers={'Content-Type': 'application/atom+xml'},
    auth=auth
)

print(f"Entry created: {response.status_code}")
```

**JavaScript Example (MetaWeblog):**
```javascript
const xmlrpc = require('xmlrpc');

const client = xmlrpc.createClient({
    host: 'yourblog.com',
    port: 443,
    path: '/roller/xmlrpc',
    headers: {'User-Agent': 'NodeJS XML-RPC Client'}
});

// Create new post
client.methodCall('metaWeblog.newPost', [
    'myblog',              // Blog ID
    'username',            // Username
    'password',            // Password
    {
        title: 'My Post',
        description: '<p>Post content</p>',
        categories: ['Technology'],
        keywords: 'tech,blog'
    },
    true                   // Publish immediately
], function(error, value) {
    if (error) {
        console.error('Error:', error);
    } else {
        console.log('Post ID:', value);
    }
});
```

### Ping Services

![Ping Configuration](docs/images/user-guide-23-ping.png)

**What are Ping Services?**

Ping services notify blog directories and search engines when you publish new content.

![Ping Settings](docs/images/user-guide-30-ping.png)

**Ping Configuration:**

**1. Automatic Ping:**
- Ping when entry published
- Ping on entry update
- Skip ping for minor edits

**2. Ping Targets:**
- Google Blog Search
- Technorati
- Ping-o-Matic (aggregates multiple services)
- Custom XML-RPC ping servers

![Add Ping Target](docs/images/user-guide-31-add-ping.png)

**Add Ping Target:**

**Required Information:**
- **Name**: Display name (e.g., "Google Blog Search")
- **Ping URL**: XML-RPC endpoint
- **Auto Ping**: Enable automatic ping
- **Condition**: Always, or only for certain categories

**Example Ping Targets:**
```
Google Blog Search:
http://blogsearch.google.com/ping/RPC2

Ping-o-Matic:
http://rpc.pingomatic.com/

Technorati:
http://rpc.technorati.com/rpc/ping
```

**Manual Ping:**
- Trigger ping manually
- Useful after blog changes
- View ping history and results

---

## Plugin Development

![Plugin Model](docs/images/user-guide-plugin.png)

### Understanding Plugins

**What are Plugins?**

Plugins extend Roller's functionality without modifying core code.

**Plugin Types:**

**1. Page Model Plugins**
- Add custom objects to template context
- Provide data to templates
- Example: Weather widget, stock ticker

**2. Renderer Plugins**
- Custom content rendering engines
- Alternative to Velocity
- Example: Markdown, Textile, custom formats

**3. Event Listeners**
- React to system events
- Execute custom logic
- Example: Auto-tweet on publish, backup entries

### Creating a Plugin

**Example: Custom Page Model Plugin**

Location: `docs/examples/plugins/pluginmodel/src/org/apache/roller/examples/plugins/pagemodel/AuthenticatedUserModel.java`

```java
package org.apache.roller.examples.plugins.pagemodel;

import org.apache.roller.weblogger.ui.rendering.plugins.PageModel;
import org.apache.roller.weblogger.business.WebloggerFactory;
import org.apache.roller.weblogger.pojos.User;
import java.util.Map;

/**
 * Plugin that exposes authenticated user information to templates
 */
public class AuthenticatedUserModel implements PageModel {
    
    private User currentUser;
    
    /**
     * Called when plugin is initialized
     */
    public void init(Map initData) throws Exception {
        // Get current user from security context
        currentUser = WebloggerFactory.getWeblogger()
            .getUserManager()
            .getCurrentUser();
    }
    
    /**
     * Name used in templates: $plugins.authenticatedUser
     */
    public String getModelName() {
        return "authenticatedUser";
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Get user's full name
     */
    public String getFullName() {
        return currentUser != null ? 
            currentUser.getFullName() : "Guest";
    }
    
    /**
     * Get user's email
     */
    public String getEmail() {
        return currentUser != null ? 
            currentUser.getEmailAddress() : null;
    }
    
    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return currentUser != null && 
            currentUser.hasRole("admin");
    }
}
```

### Using Plugin in Templates

```velocity
## Check if user is logged in
#if($plugins.authenticatedUser.loggedIn)
    <div class="user-info">
        Welcome, $plugins.authenticatedUser.fullName!
        #if($plugins.authenticatedUser.admin)
            <a href="/roller/roller-ui/admin">Admin Panel</a>
        #end
    </div>
#else
    <a href="/roller/login">Login</a>
#end
```

### Plugin Deployment

**1. Build Plugin:**
```bash
cd docs/examples/plugins/pluginmodel
ant build
```

**2. Package as JAR:**
```bash
jar cvf authuser-plugin.jar -C build/classes .
```

**3. Deploy:**
```bash
cp authuser-plugin.jar {tomcat}/webapps/roller/WEB-INF/lib/
```

**4. Register Plugin:**

Edit `roller-custom.properties`:
```properties
rendering.pageModels=org.apache.roller.examples.plugins.pagemodel.AuthenticatedUserModel
```

**5. Restart Roller:**
```bash
{tomcat}/bin/shutdown.sh
{tomcat}/bin/startup.sh
```

**6. Use in Templates:**
- Plugin available as `$plugins.authenticatedUser`
- Access methods directly

---

## Scripting & Automation

### Groovy Scripting

Roller includes Groovy scripting examples for automation.

**Location**: `docs/examples/scripting/groovy/`

### User Management Scripts

**Create User (`createuser.gy`):**
```groovy
// Create a new user programmatically
import org.apache.roller.weblogger.business.WebloggerFactory
import org.apache.roller.weblogger.pojos.User

def roller = WebloggerFactory.getWeblogger()
def userMgr = roller.getUserManager()

// Create user object
def user = new User()
user.userName = "newuser"
user.password = "password123"  // Will be encrypted
user.fullName = "New User"
user.emailAddress = "newuser@example.com"
user.locale = "en_US"
user.timezone = "America/New_York"
user.dateCreated = new Date()
user.enabled = true

// Save user
userMgr.saveUser(user)
roller.flush()

println "User created: ${user.userName}"
```

**List Users (`listusers.gy`):**
```groovy
// List all users in the system
import org.apache.roller.weblogger.business.WebloggerFactory

def roller = WebloggerFactory.getWeblogger()
def userMgr = roller.getUserManager()

def users = userMgr.getUsers(0, -1)

println "Total users: ${users.size()}"
println ""

users.each { user ->
    println "Username: ${user.userName}"
    println "Full Name: ${user.fullName}"
    println "Email: ${user.emailAddress}"
    println "Enabled: ${user.enabled}"
    println "Created: ${user.dateCreated}"
    println "---"
}
```

**Delete User (`deleteuser.gy`):**
```groovy
// Remove a user from the system
import org.apache.roller.weblogger.business.WebloggerFactory

def roller = WebloggerFactory.getWeblogger()
def userMgr = roller.getUserManager()

def username = "userToDelete"
def user = userMgr.getUserByUserName(username)

if (user) {
    // Remove user (also handles blogs, entries, etc.)
    userMgr.removeUser(user)
    roller.flush()
    println "User deleted: ${username}"
} else {
    println "User not found: ${username}"
}
```

### Blog Management Scripts

**Create Blog (`createblog.gy`):**
```groovy
// Create a new blog
import org.apache.roller.weblogger.business.WebloggerFactory
import org.apache.roller.weblogger.pojos.Weblog
import org.apache.roller.weblogger.pojos.WeblogPermission

def roller = WebloggerFactory.getWeblogger()
def weblogMgr = roller.getWeblogManager()
def userMgr = roller.getUserManager()

// Get user
def user = userMgr.getUserByUserName("admin")

// Create weblog
def weblog = new Weblog()
weblog.handle = "techblog"
weblog.name = "Tech Blog"
weblog.description = "Technology and programming"
weblog.emailAddress = "tech@example.com"
weblog.locale = "en_US"
weblog.timezone = "America/New_York"
weblog.dateCreated = new Date()
weblog.enabled = true

// Save weblog
weblogMgr.saveWeblog(weblog)

// Grant permissions to creator
def perms = new WeblogPermission()
perms.weblog = weblog
perms.user = user
perms.pending = false
perms.permissionMask = WeblogPermission.ADMIN
weblogMgr.saveWeblogPermission(perms)

roller.flush()

println "Blog created: ${weblog.handle}"
```

**Create Entry (`createentry.gy`):**
```groovy
// Publish a blog entry
import org.apache.roller.weblogger.business.WebloggerFactory
import org.apache.roller.weblogger.pojos.WeblogEntry
import org.apache.roller.weblogger.pojos.WeblogEntry.PubStatus

def roller = WebloggerFactory.getWeblogger()
def weblogMgr = roller.getWeblogManager()
def entryMgr = roller.getWeblogEntryManager()

// Get weblog
def weblog = weblogMgr.getWeblogByHandle("techblog")

// Create entry
def entry = new WeblogEntry()
entry.weblog = weblog
entry.creator = weblog.creator
entry.title = "My Automated Post"
entry.text = """
<p>This post was created automatically using Groovy scripting.</p>
<p>You can create posts programmatically!</p>
"""
entry.anchor = "automated-post-" + System.currentTimeMillis()
entry.pubTime = new Date()
entry.updateTime = new Date()
entry.status = PubStatus.PUBLISHED
entry.locale = weblog.locale
entry.allowComments = true

// Get default category
def categories = weblog.getWeblogCategories()
if (categories) {
    entry.category = categories.get(0)
}

// Save entry
entryMgr.saveWeblogEntry(entry)
roller.flush()

println "Entry published: ${entry.title}"
println "URL: ${entry.permalink}"
```

### AtomPub API Scripts

**Publish via AtomPub (`atompost.groovy`):**
```groovy
// Post via AtomPub API
@Grab('org.apache.httpcomponents:httpclient:4.5.13')
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.auth.AuthScope
import org.apache.http.impl.client.BasicCredentialsProvider

// Configuration
def blogUrl = "http://localhost:8080/roller/api/techblog/entries"
def username = "admin"
def password = "password"

// Create HTTP client with authentication
def credsProvider = new BasicCredentialsProvider()
credsProvider.setCredentials(
    AuthScope.ANY,
    new UsernamePasswordCredentials(username, password)
)

def httpClient = HttpClients.custom()
    .setDefaultCredentialsProvider(credsProvider)
    .build()

// Create entry XML
def entryXml = """
<entry xmlns="http://www.w3.org/2005/Atom">
    <title>Posted via AtomPub</title>
    <content type="html">
        <![CDATA[
            <p>This entry was posted using the AtomPub API.</p>
        ]]>
    </content>
    <category term="Automation"/>
</entry>
"""

// Post entry
def post = new HttpPost(blogUrl)
post.setHeader("Content-Type", "application/atom+xml")
post.setEntity(new StringEntity(entryXml))

def response = httpClient.execute(post)
println "Status: ${response.statusLine.statusCode}"
println "Entry created successfully!"

response.close()
httpClient.close()
```

### Running Scripts

**Method 1: Command Line**
```bash
# Using Groovy directly
groovy createuser.gy

# With classpath
groovy -cp "lib/*:roller.war/WEB-INF/lib/*" createuser.gy
```

**Method 2: Ant Build**
```bash
cd docs/examples/scripting/groovy
ant run-createuser
```

**Method 3: Within Application**
- Create custom admin page
- Execute scripts via web interface
- Schedule with cron/scheduler

---

## Internationalization

![i18n Support](docs/images/user-guide-internationalization.png)

### Supported Languages

Roller is fully translated into multiple languages:

| Language | Code | Completion |
|----------|------|------------|
| 🇺🇸 English | en_US | 100% (default) |
| 🇪🇸 Spanish | es_ES | 100% |
| 🇫🇷 French | fr_FR | 100% |
| 🇩🇪 German | de_DE | 100% |
| 🇯🇵 Japanese | ja_JP | 100% |
| 🇰🇷 Korean | ko_KR | 100% |
| 🇷🇺 Russian | ru_RU | 100% |
| 🇨🇳 Chinese | zh_CN | 100% |

### Changing Language

**User Interface Language:**

**1. Per-User Setting:**
- Go to user profile
- Select locale
- Interface changes immediately

**2. Per-Blog Setting:**
- Blog settings
- Choose blog locale
- Affects blog-specific text

**3. System Default:**
- Admin sets default locale
- New users inherit this
- Can be overridden per-user

### Translation Files

**Location**: `app/target/classes/`

```
ApplicationResources.properties          # English
ApplicationResources_es.properties       # Spanish
ApplicationResources_fr.properties       # French
ApplicationResources_de.properties       # German
ApplicationResources_ja.properties       # Japanese
ApplicationResources_ko.properties       # Korean
ApplicationResources_ru.properties       # Russian
ApplicationResources_zh_CN.properties    # Chinese
```

**Example Content:**
```properties
# English (ApplicationResources.properties)
welcome.title=Welcome to Roller
entry.edit.title=Edit Entry
comment.moderate=Moderate Comments

# Spanish (ApplicationResources_es.properties)
welcome.title=Bienvenido a Roller
entry.edit.title=Editar Entrada
comment.moderate=Moderar Comentarios

# French (ApplicationResources_fr.properties)
welcome.title=Bienvenue à Roller
entry.edit.title=Modifier l'entrée
comment.moderate=Modérer les commentaires
```

### Adding New Translation

**To add a new language:**

**1. Copy English file:**
```bash
cp ApplicationResources.properties ApplicationResources_pt_BR.properties
```

**2. Translate all strings:**
```properties
# Portuguese (Brazil)
welcome.title=Bem-vindo ao Roller
entry.edit.title=Editar Entrada
comment.moderate=Moderar Comentários
```

**3. Add locale to configuration:**
```properties
# roller-config.xml
<locale>pt_BR</locale>
```

**4. Rebuild and deploy**

### Right-to-Left (RTL) Support

Roller supports RTL languages like Arabic and Hebrew:

**RTL Features:**
- Automatic text direction
- Mirrored layout
- RTL-aware CSS

**Enable RTL:**
```velocity
## In template
#if($model.locale.language == 'ar' || $model.locale.language == 'he')
    <body dir="rtl">
#else
    <body dir="ltr">
#end
```

### Date & Time Formatting

Locale-aware date formatting:

```velocity
## Format date according to locale
$utilities.formatDate($entry.pubTime, "LONG")

## English: January 15, 2024
## Spanish: 15 de enero de 2024
## Japanese: 2024年1月15日
```

---

# Part 5: Development & Contribution

## Developer Setup

### IDE Configuration

#### IntelliJ IDEA Setup

**1. Import Project:**
- File → Open
- Select `pom.xml` from Roller root
- Choose "Open as Project"
- Wait for indexing

**2. Configure JDK:**
- File → Project Structure
- Project SDK → Add JDK → Select JDK 11 or 17
- Language Level → 11

**3. Maven Configuration:**
- View → Tool Windows → Maven
- Reload All Maven Projects
- Enable Auto-Import

**4. Run Configuration:**
- Run → Edit Configurations
- Add → Maven
- Name: "Roller Jetty"
- Working directory: `{project}/app`
- Command line: `jetty:run`
- Click OK

**5. Install Plugins:**
- Velocity Support
- Properties Editor
- Database Navigator (optional)

**6. Code Style:**
- Settings → Editor → Code Style
- Import from: `docs/roller-code-style.xml` (if exists)
- Or use: Java Conventions with 4-space indent

#### Eclipse Setup

**1. Import Maven Project:**
- File → Import → Maven → Existing Maven Projects
- Root Directory: Roller folder
- Select all POMs
- Finish

**2. Configure Java Compiler:**
- Right-click project → Properties
- Java Compiler → Compiler compliance level: 11

**3. Install Plugins:**
- Help → Eclipse Marketplace
- Install:
  - M2Eclipse (Maven integration)
  - Web Tools Platform (WTP)
  - Velocity/Freemarker Editor

**4. Run Configuration:**
- Run → Run Configurations
- Maven Build → New
- Base directory: `${workspace_loc:/roller/app}`
- Goals: `jetty:run`

#### VS Code Setup

**1. Install Extensions:**
- Extension Pack for Java
- Spring Boot Extension Pack
- XML Tools
- Velocity

**2. Open Folder:**
- File → Open Folder
- Select Roller directory

**3. Configure Maven:**
- Java extension auto-detects Maven
- Use integrated terminal for Maven commands

---

## Project Structure

### Module Overview

```
roller/
│
├── pom.xml                 # Parent POM
│
├── app/                    # Main web application
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/           # Java source
│   │   │   ├── resources/      # Config files
│   │   │   └── webapp/         # Web resources
│   │   └── test/               # Unit tests
│   └── target/                 # Build output
│
├── assembly-release/       # Release packaging
│   └── pom.xml
│
├── db-utils/              # Database utilities
│   ├── pom.xml
│   └── src/
│
├── it-selenium/           # Selenium tests
│   ├── pom.xml
│   └── src/test/
│
├── docs/                  # Documentation
│   ├── *.adoc             # AsciiDoc guides
│   ├── examples/          # Code examples
│   └── images/            # Screenshots
│
└── docker/                # Docker files
    ├── Dockerfile
    └── docker-compose.yml
```

### Source Code Organization

```
app/src/main/java/org/apache/roller/
│
├── RollerException.java        # Base exception
│
├── planet/                     # Feed aggregator
│   ├── business/              # Business logic
│   ├── pojos/                 # Data models
│   └── ui/                    # UI components
│
├── util/                      # Utilities
│   ├── cache/                 # Caching
│   └── Utilities.java
│
└── weblogger/                 # Main blogging engine
    │
    ├── business/              # Business layer
    │   ├── WeblogManager.java
    │   ├── WeblogEntryManager.java
    │   ├── UserManager.java
    │   ├── MediaFileManager.java
    │   ├── BookmarkManager.java
    │   ├── ThemeManager.java
    │   └── SearchManager.java
    │
    ├── config/                # Configuration
    │   ├── WebloggerConfig.java
    │   ├── GuiceServletConfig.java
    │   └── runtime/           # Runtime config
    │
    ├── pojos/                 # Entities (JPA)
    │   ├── User.java
    │   ├── Weblog.java
    │   ├── WeblogEntry.java
    │   ├── WeblogCategory.java
    │   ├── WeblogEntry Comment.java
    │   ├── MediaFile.java
    │   └── ...
    │
    ├── ui/                    # User interface
    │   ├── core/              # Core UI
    │   ├── rendering/         # Template rendering
    │   │   ├── Renderer.java
    │   │   ├── velocity/
    │   │   └── plugins/
    │   └── struts2/           # Struts actions
    │       ├── editor/        # Editor actions
    │       ├── admin/         # Admin actions
    │       └── core/          # Core actions
    │
    └── util/                  # Weblogger utils
        ├── HTMLSanitizer.java
        └── ...
```

### Configuration Files

| File | Location | Purpose |
|------|----------|---------|
| `pom.xml` | Root & modules | Maven configuration |
| `web.xml` | `app/src/main/webapp/WEB-INF/` | Web app descriptor |
| `struts.xml` | `app/src/main/resources/` | Struts config |
| `persistence.xml` | `app/src/main/resources/META-INF/` | JPA config |
| `log4j2.xml` | `app/src/main/resources/` | Logging config |
| `roller-custom.properties` | Classpath/external | Custom config |
| `ApplicationResources*.properties` | `app/src/main/resources/` | i18n strings |

---

## Building & Testing

### Build Commands

**Full Build:**
```bash
# Build all modules with tests
mvn clean install

# Build without tests (faster)
mvn clean install -DskipTests=true

# Build with specific profile
mvn clean install -P production
```

**Module-Specific Build:**
```bash
# Build only app module
cd app
mvn clean package

# Build only db-utils
cd db-utils
mvn clean package
```

**Clean Build:**
```bash
# Remove all build artifacts
mvn clean

# Clean and rebuild everything
mvn clean install -U
```

### Running Tests

**All Tests:**
```bash
# Run all unit tests
mvn test

# Run all tests including integration
mvn verify
```

**Specific Tests:**
```bash
# Run specific test class
mvn test -Dtest=EntryBasicTests

# Run specific test method
mvn test -Dtest=EntryBasicTests#testEntryCreate

# Run tests matching pattern
mvn test -Dtest=*BasicTests
```

**Selenium Tests:**
```bash
# Run browser integration tests
cd it-selenium
mvn verify

# Run with specific browser
mvn verify -Dselenium.browser=chrome

# Run headless
mvn verify -Dselenium.headless=true
```

**Test Coverage:**
```bash
# Generate coverage report with JaCoCo
mvn clean test jacoco:report

# View report
open target/site/jacoco/index.html
```

### Debugging

**Debug Maven Build:**
```bash
# Enable debug output
mvn clean install -X

# Enable error stack traces
mvn clean install -e
```

**Debug Jetty:**
```bash
# Start Jetty with remote debugging
mvn jetty:run -Djetty.jvmArgs="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

# Connect debugger to localhost:5005
```

**IntelliJ Debug:**
1. Set breakpoints in code
2. Run → Debug 'Roller Jetty'
3. Application starts in debug mode

---

## Contributing Guidelines

### How to Contribute

**1. Find an Issue:**
- Browse GitHub issues
- Look for "good first issue" label
- Check feature requests

**2. Fork Repository:**
```bash
# Fork on GitHub, then clone
git clone https://github.com/YOUR_USERNAME/roller.git
cd roller

# Add upstream remote
git remote add upstream https://github.com/apache/roller.git
```

**3. Create Branch:**
```bash
# Create feature branch
git checkout -b feature/my-awesome-feature

# Or bugfix branch
git checkout -b fix/issue-123
```

**4. Make Changes:**
- Follow coding standards
- Add tests for new features
- Update documentation
- Keep commits logical and atomic

**5. Test Thoroughly:**
```bash
# Run tests
mvn clean test

# Check code style (if checkstyle configured)
mvn checkstyle:check

# Run Selenium tests
cd it-selenium
mvn verify
```

**6. Commit Changes:**
```bash
# Stage changes
git add .

# Commit with descriptive message
git commit -m "Add feature: user profile avatars

- Add avatar upload functionality
- Create avatar management UI
- Add database schema for avatars
- Include unit tests

Fixes #123"
```

**7. Push and Create PR:**
```bash
# Push to your fork
git push origin feature/my-awesome-feature

# Create Pull Request on GitHub
# - Describe changes clearly
# - Reference related issues
# - Add screenshots if UI changes
```

### Coding Standards

**Java Code Style:**
- **Indentation**: 4 spaces (no tabs)
- **Line Length**: 120 characters max
- **Braces**: K&R style (opening brace on same line)
- **Naming**:
  - Classes: `PascalCase`
  - Methods: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
  - Variables: `camelCase`

**Example:**
```java
public class WeblogEntryManager {
    private static final int MAX_RESULTS = 100;
    private WeblogEntryRepository repository;
    
    public WeblogEntry getEntryById(String id) throws RollerException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        
        WeblogEntry entry = repository.findById(id);
        if (entry == null) {
            throw new NotFoundException("Entry not found: " + id);
        }
        
        return entry;
    }
}
```

**JavaDoc Requirements:**
- All public classes and interfaces
- All public methods
- Include `@param`, `@return`, `@throws`

**Example:**
```java
/**
 * Retrieves a weblog entry by its unique identifier.
 *
 * @param id the unique identifier of the entry
 * @return the weblog entry
 * @throws IllegalArgumentException if id is null or empty
 * @throws NotFoundException if entry doesn't exist
 * @throws RollerException if database error occurs
 */
public WeblogEntry getEntryById(String id) throws RollerException {
    // Implementation
}
```

### Testing Requirements

**Unit Tests:**
- Test all new functionality
- Use JUnit 4 or 5
- Mock dependencies
- Aim for 80%+ coverage

**Example Test:**
```java
public class WeblogEntryManagerTest {
    
    @Test
    public void testCreateEntry() throws Exception {
        WeblogEntry entry = new WeblogEntry();
        entry.setTitle("Test Entry");
        entry.setText("Test content");
        
        manager.saveWeblogEntry(entry);
        
        assertNotNull(entry.getId());
        assertEquals("Test Entry", entry.getTitle());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetEntryWithNullId() throws Exception {
        manager.getEntryById(null);
    }
}
```

### Pull Request Guidelines

**PR Description Should Include:**
- Clear summary of changes
- Motivation/reasoning
- Related issues (Fixes #123)
- Testing performed
- Screenshots (for UI changes)
- Breaking changes (if any)

**Example PR Description:**
```markdown
## Add Avatar Support for User Profiles

### Changes
- Added avatar upload and management UI
- Created `UserAvatar` entity and repository
- Implemented image resizing and validation
- Updated user profile page

### Motivation
Users have requested the ability to add profile pictures. 
This improves personalization and makes comments more engaging.

### Testing
- Added unit tests for avatar upload/validation
- Tested with various image formats (JPG, PNG, GIF)
- Verified file size limits
- Tested on mobile devices

### Screenshots
![Avatar Upload](screenshot1.png)
![Profile with Avatar](screenshot2.png)

Fixes #123
Related to #456
```

**PR Checklist:**
- [ ] Code follows style guidelines
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] No breaking changes (or documented)
- [ ] Commits are clean and logical
- [ ] PR description is complete

---

# Part 6: Reference

## Troubleshooting Guide

### Common Installation Issues

#### Database Connection Errors

![DB Connection Error](docs/images/db-connection-error.png)

**Problem**: Cannot connect to database

**Symptoms:**
```
java.sql.SQLException: Access denied for user 'roller'@'localhost'
Unable to open JDBC connection
```

**Solutions:**

**1. Verify Database is Running:**
```bash
# MySQL/MariaDB
sudo systemctl status mysql
sudo systemctl start mysql

# PostgreSQL
sudo systemctl status postgresql
sudo systemctl start postgresql
```

**2. Check Credentials:**
```bash
# Test MySQL connection
mysql -u roller -p
# Enter password

# Test PostgreSQL connection
psql -U roller -d roller
# Enter password
```

**3. Verify Connection URL:**
```properties
# Check roller-custom.properties
database.jdbc.connectionURL=jdbc:mysql://localhost:3306/roller?useSSL=false
database.jdbc.username=roller
database.jdbc.password=your_password
```

**4. Grant Permissions:**
```sql
-- MySQL
GRANT ALL PRIVILEGES ON roller.* TO 'roller'@'localhost';
FLUSH PRIVILEGES;

-- PostgreSQL
GRANT ALL PRIVILEGES ON DATABASE roller TO roller;
```

**5. Check Firewall:**
```bash
# MySQL default port: 3306
# PostgreSQL default port: 5432
sudo ufw allow 3306
sudo ufw allow 5432
```

#### No Tables Found

![No Tables Found](docs/images/no-tables-found.png)

**Problem**: Database exists but tables are missing

**Solutions:**

**1. Run Creation Scripts:**
```bash
# MySQL
mysql -u roller -p roller < app/src/main/resources/dbscripts/mysql/createdb.sql

# PostgreSQL
psql -U roller -d roller -f app/src/main/resources/dbscripts/postgresql/createdb.sql
```

**2. Check JPA Configuration:**
```properties
# Should be set to validate or update
hibernate.hbm2ddl.auto=validate
```

**3. Verify Database Name:**
```sql
-- MySQL
SHOW DATABASES;
USE roller;
SHOW TABLES;

-- PostgreSQL
\l
\c roller
\dt
```

#### Tomcat Deployment Failures

**Problem**: WAR file won't deploy

**Solutions:**

**1. Check Tomcat Logs:**
```bash
tail -f {tomcat}/logs/catalina.out
tail -f {tomcat}/logs/localhost.log
```

**2. Verify Java Version:**
```bash
java -version  # Should be 11+
```

**3. Check Memory:**
```bash
# Increase Tomcat memory
export CATALINA_OPTS="-Xms512M -Xmx2048M"
```

**4. Verify File Permissions:**
```bash
# Tomcat user needs read access
chmod 644 roller.war
chown tomcat:tomcat roller.war
```

**5. Clean Deployment:**
```bash
# Remove old deployment
rm -rf {tomcat}/webapps/roller
rm {tomcat}/webapps/roller.war

# Copy new WAR
cp app/target/roller.war {tomcat}/webapps/

# Restart Tomcat
{tomcat}/bin/shutdown.sh
{tomcat}/bin/startup.sh
```

### Runtime Issues

#### Slow Performance

**Symptoms:**
- Pages load slowly
- Database queries timeout
- High CPU/memory usage

**Solutions:**

**1. Enable Caching:**
```properties
cache.enabled=true
cache.size.contentCache=100
cache.size.weblogPageCache=100
```

**2. Database Indexes:**
```sql
-- Verify indexes exist
SHOW INDEX FROM weblogentry;
SHOW INDEX FROM roller_comment;

-- Add missing indexes if needed
CREATE INDEX idx_pubtime ON weblogentry(pubtime);
```

**3. Increase Memory:**
```bash
# For Tomcat
export CATALINA_OPTS="-Xms1024M -Xmx4096M"

# For Jetty Maven plugin
mvn jetty:run -Djetty.jvmArgs="-Xmx2048M"
```

**4. Optimize Database:**
```sql
-- MySQL
OPTIMIZE TABLE weblogentry;
ANALYZE TABLE weblogentry;

-- PostgreSQL
VACUUM ANALYZE;
```

#### Upload Failures

**Problem**: Cannot upload files

**Solutions:**

**1. Check Upload Directory:**
```bash
# Create directory
mkdir -p /var/roller/uploads

# Set permissions
chmod 755 /var/roller/uploads
chown tomcat:tomcat /var/roller/uploads
```

**2. Verify Configuration:**
```properties
uploads.enabled=true
uploads.dir=/var/roller/uploads
uploads.maxFileMB=10
uploads.types.allowed=image/jpeg,image/png,image/gif
```

**3. Check Disk Space:**
```bash
df -h /var/roller/uploads
```

**4. Increase Upload Limits:**
```xml
<!-- web.xml -->
<multipart-config>
    <max-file-size>10485760</max-file-size>  <!-- 10MB -->
    <max-request-size>52428800</max-request-size>  <!-- 50MB -->
</multipart-config>
```

#### Search Not Working

**Problem**: Search returns no results

**Solutions:**

**1. Verify Search Enabled:**
```properties
search.enabled=true
search.index.dir=/var/roller/search-index
```

**2. Create Index Directory:**
```bash
mkdir -p /var/roller/search-index
chmod 755 /var/roller/search-index
chown tomcat:tomcat /var/roller/search-index
```

**3. Rebuild Search Index:**
- Login as admin
- Go to Server Admin → Maintenance
- Click "Rebuild Search Index"
- Wait for completion

**4. Check Lucene Index:**
```bash
ls -la /var/roller/search-index
# Should contain Lucene index files
```

---

## Configuration Reference

### Database Configuration

**MySQL/MariaDB:**
```properties
database.jpa.configuration=roller-jpa-config
database.jdbc.driverClass=com.mysql.cj.jdbc.Driver
database.jdbc.connectionURL=jdbc:mysql://localhost:3306/roller?useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC
database.jdbc.username=roller
database.jdbc.password=your_password
```

**PostgreSQL:**
```properties
database.jpa.configuration=roller-jpa-config
database.jdbc.driverClass=org.postgresql.Driver
database.jdbc.connectionURL=jdbc:postgresql://localhost:5432/roller
database.jdbc.username=roller
database.jdbc.password=your_password
```

**Oracle:**
```properties
database.jpa.configuration=roller-jpa-config
database.jdbc.driverClass=oracle.jdbc.driver.OracleDriver
database.jdbc.connectionURL=jdbc:oracle:thin:@localhost:1521:XE
database.jdbc.username=roller
database.jdbc.password=your_password
```

### File Upload Configuration

```properties
# Enable file uploads
uploads.enabled=true

# Upload directory (must be writable)
uploads.dir=/var/roller/uploads

# Maximum file size in MB
uploads.maxFileMB=10

# Maximum directory size in MB (per user)
uploads.maxDirMB=100

# Allowed file types (MIME types)
uploads.types.allowed=image/jpeg,image/png,image/gif,image/svg+xml,application/pdf

# Forbidden file types
uploads.types.forbidden=application/x-executable
```

### Search Configuration

```properties
# Enable search
search.enabled=true

# Search index directory
search.index.dir=/var/roller/search-index

# Rebuild index on startup (for development)
search.index.rebuild=false
```

### Email Configuration

```properties
# SMTP server
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587

# Authentication
mail.smtp.username=your-email@gmail.com
mail.smtp.password=your-app-password
mail.smtp.auth=true
mail.smtp.starttls.enable=true

# From address
mail.from=noreply@yourdomain.com
```

### Cache Configuration

```properties
# Enable caching
cache.enabled=true

# Cache sizes (number of objects)
cache.size.contentCache=100
cache.size.parsedTemplateCache=50
cache.size.weblogPageCache=100
cache.size.planetCache=50

# Cache timeout (seconds)
cache.timeout.contentCache=3600
cache.timeout.weblogPageCache=1800
```

### Site Configuration

```properties
# Site URL (used in feeds and emails)
site.absoluteurl=https://yourdomain.com

# Site name
site.name=My Roller Site

# Site description
site.description=A blogging platform

# Allow new user registration
registration.enabled=true

# Require email verification
registration.email.verify=true
```

---

## Documentation Resources

### Official Documentation

**In This Repository:**

📚 **Installation Guide**: [docs/roller-install-guide.adoc](docs/roller-install-guide.adoc)
- Complete installation instructions
- Database setup for all supported databases
- Tomcat/Jetty configuration
- Production deployment best practices
- Troubleshooting common issues

📚 **User Guide**: [docs/roller-user-guide.adoc](docs/roller-user-guide.adoc)
- Getting started tutorial
- Creating and managing content
- User interface walkthrough
- Customization options
- Administrative tasks
- FAQ and tips

📚 **Template Guide**: [docs/roller-template-guide.adoc](docs/roller-template-guide.adoc)
- Velocity template syntax
- Model object reference
- Creating custom themes
- Template customization
- Macro development
- Best practices

### Code Examples

**Plugin Development**: `docs/examples/plugins/pluginmodel/`
- Sample page model plugin
- Shows plugin structure
- Build configuration
- Deployment instructions

**Groovy Scripting**: `docs/examples/scripting/groovy/`
- User management scripts
- Blog automation scripts
- Entry creation scripts
- AtomPub API examples
- Admin protocol examples

**Configuration Examples**: `docs/examples/configs/`
- Tomcat configuration
- Apache mod_jk setup
- Blog client configurations

**Custom Macros**: `docs/examples/macros/`
- Sample Velocity macros
- Reusable template components

### Online Resources

🌐 **Apache Roller Wiki**:
- **Build and Run**: https://cwiki.apache.org/confluence/x/EM4
  - Development environment setup
  - Building from source
  - Running locally
  
- **Contributing**: https://cwiki.apache.org/confluence/x/2hsB
  - How to contribute
  - Coding guidelines
  - Patch submission process
  
- **Release Process**: https://cwiki.apache.org/confluence/x/gycB
  - For committers
  - Release procedures
  - Quality assurance
  
- **Developer Resources**: https://cwiki.apache.org/confluence/x/D84
  - Architecture documentation
  - API documentation
  - Development tips

🌐 **Official Website**: http://roller.apache.org
- Project overview
- News and announcements
- Download links
- Community information

📂 **Source Repository**: https://github.com/apache/roller
- Latest source code
- Issue tracking
- Pull requests
- Release history

### Getting Help

**Community Support**:

💬 **Mailing Lists**:
- **User List**: roller-user@apache.org
  - For users and administrators
  - Installation help
  - Usage questions
  
- **Developer List**: roller-dev@apache.org
  - For developers
  - Technical discussions
  - Feature proposals

Subscribe at: http://roller.apache.org/mailing-lists.html

🐛 **Issue Tracker**: https://github.com/apache/roller/issues
- Report bugs
- Request features
- Track progress

💻 **Stack Overflow**: Tag questions with `apache-roller`
- Community Q&A
- Code examples
- Troubleshooting

### Video Tutorials

While no official videos exist, community members have created tutorials. Search for:
- "Apache Roller installation"
- "Apache Roller tutorial"
- "Roller blog setup"

### Books & Articles

Check Apache Roller website for:
- Blog posts about Roller
- Technical articles
- Case studies

---

## Quick Reference

### Common URLs

```
# Blog URLs
Main blog:          /{bloghandle}
Entry permalink:    /{bloghandle}/entry/{anchor}
Category view:      /{bloghandle}/category/{category}
Tag view:           /{bloghandle}/tag/{tag}
Date archive:       /{bloghandle}/date/YYYYMMDD
Search results:     /{bloghandle}/search?q={query}

# Feed URLs
RSS feed:           /{bloghandle}/feed/entries/rss
Atom feed:          /{bloghandle}/feed/entries/atom
Category RSS:       /{bloghandle}/feed/entries/rss?cat={category}
Comments RSS:       /{bloghandle}/feed/comments/rss

# Admin URLs
Login:              /roller/login
Main menu:          /roller/roller-ui/menu
Create entry:       /roller/roller-ui/authoring/entryEdit
Comments:           /roller/roller-ui/authoring/comments
Media files:        /roller/roller-ui/authoring/mediaFiles
Blog settings:      /roller/roller-ui/authoring/weblogConfig
Theme customizer:   /roller/roller-ui/authoring/themeEdit
Site admin:         /roller/roller-ui/admin

# API Endpoints
AtomPub service:    /roller/api/{bloghandle}/service
AtomPub entries:    /roller/api/{bloghandle}/entries
MetaWeblog:         /roller/xmlrpc
```

### Essential Commands

```bash
# Building
mvn clean install -DskipTests=true   # Build without tests
mvn clean install                     # Build with tests
mvn package                           # Create WAR file

# Running
mvn jetty:run                         # Run with Jetty
docker-compose up                     # Run with Docker

# Testing
mvn test                              # Run unit tests
mvn verify                            # Run all tests
mvn test -Dtest=ClassName            # Run specific test

# Debugging
mvn jetty:run -Djetty.jvmArgs="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

# Database
mysql -u roller -p roller < dbscripts/mysql/createdb.sql
psql -U roller -d roller -f dbscripts/postgresql/createdb.sql

# Logs
tail -f logs/roller.log                        # Roller logs
tail -f {tomcat}/logs/catalina.out            # Tomcat logs
```

### Configuration Files

| File | Location | Purpose |
|------|----------|---------|
| `roller-custom.properties` | Classpath or /opt/tomcat/lib | Main configuration |
| `web.xml` | `WEB-INF/` | Web application descriptor |
| `struts.xml` | `WEB-INF/classes/` | Struts 2 configuration |
| `persistence.xml` | `META-INF/` | JPA/database config |
| `log4j2.xml` | `WEB-INF/classes/` | Logging configuration |
| `ApplicationResources*.properties` | Classpath | Internationalization |

### Version Information

**Current Version**: 6.1.5

**Requirements**:
- Java: 11 or higher
- Maven: 3.6 or higher (for building)
- Database: MySQL 5.7+, PostgreSQL 9.6+, MariaDB 10.2+, Oracle 11g+
- App Server: Tomcat 9.0+, Jetty 10.0+

**Key Dependencies**:
- Struts 2: 2.5.29
- Spring: 5.3.39
- Velocity: 2.4.1
- Lucene: 9.12.1
- EclipseLink: 4.0.5

---

## License & Acknowledgments

### Apache License 2.0

```
Copyright 2002-2024 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

Full license text: [LICENSE.txt](LICENSE.txt)

### Contributors

Apache Roller is developed and maintained by:
- The Apache Software Foundation
- Open source contributors worldwide
- Original creator: Dave Johnson

Special thanks to all who have contributed code, documentation, translations, and support!

### Third-Party Components

See [NOTICE.txt](NOTICE.txt) for complete list of third-party components and their licenses.

---

## 🚀 Getting Started Checklist

Ready to use Roller? Follow this checklist:

### Quick Start (5 minutes)
- [ ] Install Docker
- [ ] Clone repository: `git clone https://github.com/apache/roller.git`
- [ ] Run: `docker-compose up`
- [ ] Open: http://localhost:8080/roller
- [ ] Create admin account
- [ ] Create your first blog

### Production Setup (30-60 minutes)
- [ ] Install Java 11+
- [ ] Install database (MySQL/PostgreSQL)
- [ ] Create database and user
- [ ] Run database schema scripts
- [ ] Install Tomcat
- [ ] Build Roller: `mvn clean package`
- [ ] Configure `roller-custom.properties`
- [ ] Deploy WAR to Tomcat
- [ ] Access Roller and complete setup
- [ ] Create blogs and start posting!

### Next Steps
- [ ] Read User Guide for features
- [ ] Customize theme
- [ ] Configure comments and spam protection
- [ ] Set up RSS feeds
- [ ] Invite team members (if group blog)
- [ ] Configure backups

---

**Questions? Issues? Contributions?**

- 📧 **Mailing Lists**: http://roller.apache.org/mailing-lists.html
- 🐛 **Bug Reports**: https://github.com/apache/roller/issues
- 💻 **Source Code**: https://github.com/apache/roller
- 📚 **Documentation**: See `docs/` folder
- 🌐 **Website**: http://roller.apache.org

**Happy Blogging! 📝✨**

---

*This guide covers Apache Roller version 6.1.5. For the latest information, visit the official website.*

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Client Layer                            │
│                    (Web Browser)                             │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP/HTTPS
┌──────────────────────▼──────────────────────────────────────┐
│                  Presentation Layer                          │
│         ┌──────────────┬──────────────┬─────────────┐       │
│         │ JSP Pages    │ Velocity     │ Struts2     │       │
│         │              │ Templates    │ Actions     │       │
│         └──────────────┴──────────────┴─────────────┘       │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                   Business Layer                             │
│         ┌──────────────┬──────────────┬─────────────┐       │
│         │ Weblogger    │ Planet       │ Utilities   │       │
│         │ Services     │ Aggregator   │             │       │
│         └──────────────┴──────────────┴─────────────┘       │
│         ┌──────────────────────────────────────────┐        │
│         │      Dependency Injection (Guice)         │        │
│         └──────────────────────────────────────────┘        │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                 Persistence Layer                            │
│         ┌──────────────┬──────────────┬─────────────┐       │
│         │ JPA/         │ EclipseLink  │ Search      │       │
│         │ Hibernate    │              │ (Lucene)    │       │
│         └──────────────┴──────────────┴─────────────┘       │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                   Database Layer                             │
│    MySQL / PostgreSQL / Derby / MariaDB / Oracle            │
└─────────────────────────────────────────────────────────────┘
```

## Technology Stack

### Backend Technologies
- **Java**: Core programming language (Java 11+)
- **Struts 2**: MVC framework (v2.5.29)
- **Velocity**: Template engine (v2.4.1)
- **Spring Framework**: Dependency injection and security (v5.3.39)
- **Spring Security**: Authentication and authorization (v5.8.14)
- **Guice**: Google's dependency injection framework (v7.0.0)
- **JPA/EclipseLink**: Object-relational mapping (v4.0.5)
- **Apache Lucene**: Full-text search engine (v9.12.1)

### Frontend Technologies
- **JSP**: JavaServer Pages for dynamic content
- **AngularJS**: Frontend framework (v1.7.8)
- **Bootstrap**: UI components
- **jQuery**: JavaScript library

### Supporting Technologies
- **ROME**: RSS/Atom feed parsing and generation (v1.19.0)
- **Log4j2**: Logging framework (v2.24.3)
- **Apache Commons**: Utility libraries
- **OAuth**: Authentication protocol

### Build & Development
- **Maven**: Build automation and dependency management
- **Jetty**: Development web server
- **Docker**: Containerization
- **Selenium**: Browser automation for testing

## Project Structure

Roller is made up of the following Maven modules:

```
roller-project/
├── app/                      # Main web application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/         # Java source code
│   │   │   │   └── org/apache/roller/
│   │   │   │       ├── weblogger/    # Blog engine
│   │   │   │       ├── planet/       # Feed aggregation
│   │   │   │       └── util/         # Utilities
│   │   │   ├── resources/    # Configuration files
│   │   │   └── webapp/       # JSP, CSS, JS, themes
│   │   └── test/             # Unit tests
│   └── pom.xml
│
├── assembly-release/         # Distribution assembly
├── db-utils/                 # Database utilities
├── docs/                     # Documentation (AsciiDoc)
├── it-selenium/              # Selenium integration tests
├── docker/                   # Docker configuration
└── pom.xml                   # Parent POM
```

### Key Directories in `app/`

| Directory | Purpose |
|-----------|---------|
| `src/main/java/org/apache/roller/weblogger` | Core blogging functionality |
| `src/main/java/org/apache/roller/planet` | RSS/Atom feed aggregation |
| `src/main/webapp/roller-ui` | Admin UI and blog management |
| `src/main/webapp/WEB-INF` | Configuration (web.xml, struts.xml) |
| `src/main/resources/dbscripts` | Database schema scripts |
| `src/main/resources/template` | Email templates |
| `src/main/webapp/themes` | Blog themes |

---

## User Interface Walkthrough

### 1. Welcome & Initial Setup

#### Welcome Screen
![Welcome Screen](docs/images/user-guide-1-welcome.png)

When you first access Roller, you're greeted with the welcome screen. This is your entry point to the blogging platform.

#### User Registration
![Registration](docs/images/user-guide-2-registration.png)

**Registration Process**:
- Username selection (must be unique)
- Email address (for notifications and password recovery)
- Password (with strength requirements)
- Full name and optional profile information
- Locale selection for interface language

The first user registered automatically becomes the system administrator.

#### Creating Your First Weblog
![Create Weblog](docs/images/user-guide-3-webblog.png)

**Weblog Setup**:
- **Blog Name**: Display name for your blog
- **Blog Handle**: URL-friendly identifier (e.g., "myblog" → `/roller/myblog`)
- **Description**: Brief description of your blog
- **Email Address**: Contact email for your blog
- **Theme Selection**: Choose from available themes
- **Locale**: Language for your blog
- **Timezone**: For accurate post timestamps
- **Entry Display Count**: Number of posts per page

### 2. User Interface Overview

#### Status Bar
![Status Bar](docs/images/user-guide-4-statusbar.png)

The status bar shows:
- Currently logged-in user
- Active weblog context
- Quick navigation links
- System notifications

![Status Bar - Weblog View](docs/images/user-guide-5-statusbar-webblog.png)

When viewing a specific blog, the status bar updates to show blog-specific information and actions.

#### Main Navigation
![Navigation Menu](docs/images/user-guide-6-navigation.png)

**Primary Navigation Sections**:
- **Entries**: Create and manage blog posts
- **Comments**: Moderate and manage comments
- **Blogroll**: Manage links to other blogs
- **Media**: Upload and manage files
- **Members**: Manage blog contributors (group blogs)
- **Settings**: Configure blog options
- **Design**: Customize theme and templates

#### Main Menu
![Main Menu](docs/images/user-guide-7-main-menu.png)

The main menu provides access to:
- **New Entry**: Quick access to create a blog post
- **View Blog**: Preview your published blog
- **Settings**: Blog configuration
- **Logout**: End your session

---

## Core Components Deep Dive

---

## Core Components Deep Dive

### 1. Weblogger Module - The Blogging Engine

The Weblogger is the heart of Apache Roller, handling all core blogging operations.

**Architecture Components**:
- **Entry Management**: Create, edit, delete, schedule blog posts
- **Category System**: Hierarchical category organization
- **Tag System**: Flexible tagging for posts
- **Media Library**: Integrated file and image management
- **Comment System**: Advanced comment moderation
- **Permalink Engine**: SEO-friendly URL generation
- **Feed Generator**: RSS/Atom feed creation

**Key Java Classes**:
```
org.apache.roller.weblogger.business/
├── WeblogManager.java           # Blog CRUD operations
├── WeblogEntryManager.java      # Post management
├── UserManager.java             # User authentication
├── MediaFileManager.java        # File upload/management
├── BookmarkManager.java         # Blogroll management
├── ThemeManager.java            # Theme management
└── SearchManager.java           # Full-text search
```

### 2. Planet Feed Aggregator Module

![Planet Configuration](docs/images/user-guide-32-planet-config.png)

The Planet module aggregates RSS/Atom feeds from multiple blogs into a unified view.

**Features**:
- **Multi-source Aggregation**: Combine feeds from multiple blogs
- **Smart Parsing**: ROME library for feed parsing
- **Feed Caching**: Efficient feed retrieval and caching
- **Custom Filtering**: Filter content by category, tags, or keywords
- **Planet Page**: Dedicated aggregated view

![Subscription Management](docs/images/user-guide-33-subscription.png)

**Subscription Management**:
- Add external blog feeds
- Monitor feed health
- Set update frequency
- Organize by groups
- Enable/disable feeds

### 3. Template & Theme System

![Templates Overview](docs/images/templates.png)

Roller uses Apache Velocity as its template engine, providing powerful customization.

**Template Types**:
- **Weblog Template**: Main blog layout
- **Permalink Template**: Individual post view
- **Search Results**: Search result formatting
- **Custom Pages**: User-defined pages
- **Stylesheets**: CSS customization
- **Resource Files**: Images, JavaScript

![Template Editing](docs/images/template-edit.png)

**Velocity Template Features**:
- Access to blog data through model objects
- Custom macros for reusable components
- Conditional rendering
- Loop constructs for entries
- Date/time formatting
- Internationalization support

![Model Objects](docs/images/model-object.png)

**Available Model Objects**:
- `$model.weblog`: Current blog information
- `$model.entries`: Blog entries collection
- `$model.categories`: Category list
- `$model.tags`: Tag cloud data
- `$model.user`: Current user (if authenticated)
- `$model.site`: Site-wide settings

### 4. Plugin Architecture

![Plugin Model](docs/images/user-guide-plugin.png)

Roller's plugin system allows extending functionality without modifying core code.

**Plugin Types**:
1. **Page Model Plugins**: Add custom objects to template context
2. **Renderer Plugins**: Custom content rendering engines
3. **Event Listeners**: Hook into system events
4. **Custom Validators**: Form validation logic

**Example Plugin Implementation**:
```java
// docs/examples/plugins/pluginmodel/src/.../AuthenticatedUserModel.java
public class AuthenticatedUserModel implements PageModel {
    public void init(Map initData) throws RollerException {
        // Initialize plugin with page context
    }
    
    public String getModelName() {
        return "authenticatedUser";
    }
    
    // Custom methods accessible in templates
    public User getCurrentUser() {
        // Return authenticated user
    }
}
```

**Plugin Deployment**:
- Package as JAR file
- Place in `WEB-INF/lib`
- Register in plugin configuration
- Access via templates: `$plugins.authenticatedUser.currentUser`

---

## Content Management

### Creating and Managing Blog Entries

#### Blog Entry Editor
![Blog Editor](docs/images/user-guide-8-editor.png)

The entry editor provides a rich interface for content creation:

**Editor Features**:
1. **Title Field**: SEO-optimized title
2. **Content Editor**: WYSIWYG or HTML mode
3. **Category Selection**: Assign to categories
4. **Tag Input**: Add comma-separated tags
5. **Status Selection**: Draft, Published, Scheduled
6. **Publish Date**: Schedule future publication
7. **Search Description**: Custom SEO description
8. **Permalink**: Custom URL slug
9. **Comments**: Enable/disable for post
10. **Right to Left**: Support for RTL languages

![Entry Editor Detail](docs/readme-images/edit-entry.jpg)

**Content Formatting Options**:
![Formatting Options](docs/images/user-guide-formatting.png)

- **Rich Text**: WYSIWYG editor with toolbar
- **HTML**: Direct HTML editing
- **Markdown**: Markdown syntax support
- **Text**: Plain text
- **Auto-format**: Automatic paragraph detection

**Text Formatting Tools**:
- Bold, Italic, Underline
- Headers (H1-H6)
- Lists (ordered/unordered)
- Links and anchors
- Images insertion
- Code blocks
- Blockquotes
- Horizontal rules
- Tables

#### Managing Entries
![Blog Entries List](docs/images/user-guide-9-entries.png)

**Entry Management Features**:
- **Search/Filter**: Find entries by keyword, category, or status
- **Bulk Actions**: Delete or change status of multiple entries
- **Sort Options**: By date, title, or status
- **Status Indicators**: 
  - 🟢 Published (live)
  - 🟡 Draft (not visible)
  - 🔵 Scheduled (future publication)
  - 🔴 Pending (awaiting approval)

![Entries Overview](docs/readme-images/entries.jpg)

**Entry List Columns**:
- **Title**: Entry title with edit link
- **Category**: Assigned category
- **Status**: Publication status
- **Date**: Created/published date
- **Actions**: Edit, Delete, View

### Category Management

![Categories](docs/images/user-guide-10-categories.png)

**Category System Features**:
- **Hierarchical Structure**: Parent-child relationships
- **Category Description**: SEO-friendly descriptions
- **Image Association**: Category-specific images
- **RSS Feeds**: Per-category feeds
- **Reordering**: Drag-and-drop category ordering

**Category Operations**:
1. **Create**: Add new categories
2. **Edit**: Update name, description, image
3. **Move**: Change parent category
4. **Delete**: Remove (with entry reassignment)

### Blogroll Management

![Blogroll](docs/images/user-guide-11-blogroll.png)

Manage links to other blogs and websites:

**Blogroll Features**:
- **Folder Organization**: Group links by topic
- **Link Properties**:
  - Name and URL
  - Description
  - Image/icon
  - Relationship (friend, co-worker, etc.)
- **OPML Import/Export**: Import blogroll from other platforms
- **Display Control**: Show/hide in sidebar

---

## User & Access Management

### User Administration

![User Administration](docs/images/user-guide-24-user-admin.png)

**User Management Capabilities**:
- **Create Users**: Add new users manually
- **Edit Profiles**: Update user information
- **Enable/Disable**: Temporarily disable accounts
- **Delete Users**: Remove users and handle content
- **Password Reset**: Administrative password reset
- **Role Assignment**: Assign global roles

![User Admin Extended](docs/images/user-guide-25-user-admin.png)

**User Roles**:
1. **Admin**: Full system access
   - Manage all blogs
   - User management
   - System configuration
   - Database maintenance

2. **Editor**: Blog-level management
   - Manage assigned blogs
   - Moderate all comments
   - Publish any entry
   - Manage members

3. **Author**: Content creation
   - Create/edit own entries
   - Upload media
   - Manage own comments

4. **Limited**: Restricted access
   - Create drafts only
   - Cannot publish
   - Limited media access

### Member Management (Group Blogs)

![Member List](docs/images/user-guide-21-member.png)

For collaborative blogging, manage blog members:

**Member Features**:
- View all blog members
- See member roles
- Pending invitation status
- Edit member permissions
- Remove members

![Invite Member](docs/images/user-guide-22-invite-member.png)

**Invitation Process**:
1. **Select User**: Choose from registered users
2. **Assign Role**: Set permission level
3. **Send Invitation**: Email notification sent
4. **Accept/Decline**: User accepts invitation
5. **Active Member**: User gains blog access

**Collaborative Workflow**:
- Multiple authors per blog
- Role-based permissions
- Entry ownership tracking
- Collaborative editing
- Activity notifications

---

## Media Management System

### Media File Overview

![Media Files](docs/images/user-guide-12-media.png)

The media management system provides comprehensive file handling:

**Media Types Supported**:
- **Images**: JPG, PNG, GIF, SVG, WebP
- **Documents**: PDF, DOC, DOCX, TXT
- **Audio**: MP3, WAV, OGG (for podcasting)
- **Video**: MP4, WebM (embedded support)
- **Archives**: ZIP (for downloads)

**Media Features**:
- Drag-and-drop upload
- Bulk upload support
- Directory organization
- Thumbnail generation
- Image metadata display
- File size tracking
- Usage tracking

### Adding Media Files

![Add Media](docs/images/user-guide-13-add-media.png)

**Upload Process**:
1. **Select Files**: Choose files from computer
2. **Directory**: Select destination folder
3. **Upload**: Transfer files to server
4. **Process**: Automatic thumbnail generation
5. **Confirm**: Files ready for use

![Upload Complete](docs/images/user-guide-14-upload-complete.png)

**Post-Upload Information**:
- File name and size
- Upload timestamp
- Direct URL for embedding
- Thumbnail preview
- Quick insert options

### Editing Media Properties

![Edit Media](docs/images/user-guide-15-edit-media.png)

**Editable Properties**:
- **Title**: Display name
- **Description**: Alt text for images
- **Tags**: Searchable tags
- **Copyright**: Copyright notice
- **Creator Credit**: Photo credit
- **Directory**: Move to different folder

### Media Directory Organization

![Media Directory](docs/images/user-guide-media-directory.png)

**Directory Features**:
- **Hierarchical Structure**: Folders and subfolders
- **Create Folders**: Organize by topic/date
- **Move Files**: Drag-drop between folders
- **Batch Operations**: Multi-file management
- **Search**: Find files quickly

### File Upload Settings

![File Upload Configuration](docs/images/user-guide-29-fileupload.png)

**Administrator Controls**:
- **Maximum File Size**: Limit per file
- **Total Quota**: Per-user storage limit
- **Allowed Types**: MIME type restrictions
- **Image Processing**: Thumbnail settings
- **Upload Directory**: File system location

### Podcast Support

![Podcast Settings](docs/images/user-guide-podcast.png)

**Podcasting Features**:
- **Audio/Video Enclosures**: Attach media to entries
- **iTunes Tags**: Podcast-specific metadata
- **RSS Enclosures**: Automatic podcast feed
- **Media Player**: Embedded playback
- **Episode Management**: Track podcast episodes

---

## Theme & Design System

### Design Menu

![Design Menu](docs/images/user-guide-20-design-menu.png)

The Design section provides complete control over blog appearance:

**Design Options**:
1. **Theme**: Select and customize themes
2. **Templates**: Edit Velocity templates
3. **Stylesheet**: Custom CSS
4. **Resources**: Upload theme assets
5. **Advanced**: Custom JavaScript, HTML

### Theme Selection & Customization

![Design Theme](docs/images/user-guide-design-theme.png)

**Available Themes**:
- **Basic**: Simple, clean layout
- **Effortless**: Minimalist design
- **Gaurav**: Modern responsive theme
- **Snapshot**: Photo-centric theme
- **Stripes**: Traditional blog layout
- **Awesome**: Feature-rich theme
- **Custom**: Import custom themes

![Customize Theme - Step 1](docs/images/customize-theme-1.png)

**Theme Customization Options**:
- **Layout Settings**: Sidebar position, width
- **Color Scheme**: Primary, secondary, accent colors
- **Typography**: Font families, sizes
- **Header**: Logo, tagline, background
- **Footer**: Copyright, links
- **Widgets**: Sidebar component selection

![Customize Theme - Step 2](docs/images/customize-theme-2.png)

**Advanced Customization**:
- **Custom CSS**: Override theme styles
- **Custom Templates**: Modify template structure
- **JavaScript**: Add custom functionality
- **Head Content**: Meta tags, analytics
- **Footer Scripts**: Tracking codes

### Template Editing

![Template Edit](docs/images/template-edit.png)

**Template Editor Features**:
- **Syntax Highlighting**: Velocity syntax coloring
- **Code Completion**: Variable suggestions
- **Error Detection**: Template validation
- **Preview**: Test changes before saving
- **Revert**: Undo changes to original

**Common Template Customizations**:
```velocity
## Display blog entries
#foreach($entry in $entries)
    <article>
        <h2><a href="$entry.permalink">$entry.title</a></h2>
        <div class="entry-content">
            $entry.displayContent
        </div>
        <div class="entry-meta">
            Posted on $entry.pubTime | 
            Category: $entry.category.name |
            Tags: #foreach($tag in $entry.tags)$tag #end
        </div>
    </article>
#end

## Custom macro usage
#showEntriesByTag("java" 5)

## Conditional rendering
#if($model.authenticatedUser)
    <div>Welcome, $model.authenticatedUser.fullName!</div>
#end
```

---

## Comment & Interaction System

### Comment Management Overview

![Comments](docs/images/user-guide-18-comments.png)

Robust comment system with moderation and spam protection:

**Comment Features**:
- **Moderation Queue**: Review before publication
- **Spam Detection**: Automatic spam filtering
- **Blacklist**: Block specific words/IPs
- **Email Notifications**: Alert on new comments
- **Threading**: Nested comment replies
- **Rich Formatting**: Allow HTML or plain text

![Comments Detail](docs/images/user-guide-comments.png)

**Comment Management Actions**:
- **Approve**: Make comment visible
- **Mark as Spam**: Train spam filter
- **Delete**: Permanently remove
- **Edit**: Modify comment content
- **Ban User**: Block commenter's IP/email

### Comment Configuration

![Comment Settings](docs/images/user-guide-27-comments.png)

**Comment Settings**:
1. **Allow Comments**: Global enable/disable
2. **Moderation**: 
   - No moderation
   - Moderate all
   - Moderate first-time commenters
3. **Email Notifications**: Alert blog owner
4. **CAPTCHA**: Prevent automated spam
5. **Required Fields**: Name, email, URL
6. **HTML Allowed**: Permitted HTML tags
7. **Max Length**: Character limit
8. **Time Limit**: Days to allow comments

### Spam Protection

![Spam Settings](docs/images/user-guide-spam.png)

**Anti-Spam Features**:
- **Word Blacklist**: Block comments containing specific words
- **IP Blacklist**: Block IP addresses
- **URL Filtering**: Limit links in comments
- **Akismet Integration**: Cloud-based spam detection
- **CAPTCHA**: Human verification
- **Comment Throttling**: Rate limiting

**Blacklist Management**:
```
# Example blacklist entries
viagra
cialis
casino
poker
[url=
<a href
```

---

## Advanced Features

### API Access & Integration

![API Configuration](docs/images/user-guide-17-api.png)

Roller supports multiple APIs for programmatic access:

**Supported APIs**:

1. **AtomPub (Atom Publishing Protocol)**
   - Standard blog publishing API
   - RESTful interface
   - Entry CRUD operations
   - Media upload support
   - Authentication: WSSE or Basic Auth

2. **MetaWeblog API**
   - XML-RPC based
   - Compatible with popular blog clients
   - Create/edit/delete posts
   - Media upload
   - Category management

3. **Blogger API**
   - Legacy XML-RPC API
   - Basic posting functionality
   - Backward compatibility

**Compatible Blog Clients**:
- MarsEdit (macOS)
- Windows Live Writer
- BlogJet
- Ecto
- Blogo
- Mobile apps supporting MetaWeblog

**API Endpoint Configuration**:
```
AtomPub Service Document:
https://yourblog.com/roller/api/{bloghandle}/service

MetaWeblog Endpoint:
https://yourblog.com/roller/xmlrpc
```

### Feed Management

![Feed Configuration](docs/images/user-guide-28-feed.png)

**RSS/Atom Feed Options**:
- **Feed Format**: RSS 2.0 or Atom 1.0
- **Entry Count**: Number of entries in feed
- **Excerpt vs. Full**: Show summary or full content
- **Categories**: Per-category feeds
- **Comments Feed**: Separate feed for comments
- **Custom Feed**: Create filtered feeds

**Available Feeds**:
```
Main blog feed:
/roller/{bloghandle}/feed/entries/rss
/roller/{bloghandle}/feed/entries/atom

Category feed:
/roller/{bloghandle}/feed/entries/rss?cat={category}

Tag feed:
/roller/{bloghandle}/feed/entries/rss?tag={tag}

Comments feed:
/roller/{bloghandle}/feed/comments/rss
```

### Ping Services

![Ping Configuration](docs/images/user-guide-23-ping.png)

Notify blog directories and search engines of updates:

**Ping Targets**:
- Google Blog Search
- Technorati
- Ping-o-Matic
- Custom ping servers

![Ping Settings](docs/images/user-guide-30-ping.png)

**Ping Configuration**:
- **Automatic Ping**: On entry publication
- **Manual Ping**: Trigger manually
- **Ping History**: Track ping status
- **Custom Targets**: Add your own ping services

![Add Ping Target](docs/images/user-guide-31-add-ping.png)

**Adding Ping Targets**:
1. **Name**: Display name for service
2. **Ping URL**: XML-RPC endpoint
3. **Auto**: Enable automatic ping
4. **Test**: Verify connection

### Planet Feed Aggregator

![Planet Configuration](docs/images/user-guide-32-planet-config.png)

Create a unified view of multiple blogs:

**Planet Features**:
- **Multi-blog Aggregation**: Combine RSS/Atom feeds
- **Smart Caching**: Efficient feed retrieval
- **Filtering**: By keyword, category
- **Grouping**: Organize by topic
- **Custom Display**: Template-based rendering

![Subscription Management](docs/images/user-guide-33-subscription.png)

**Managing Subscriptions**:
- **Add Feed**: Subscribe to external blog
- **Feed URL**: RSS/Atom feed address
- **Update Frequency**: How often to check
- **Title/Author**: Override feed metadata
- **Active Status**: Enable/disable subscription

**Planet Use Cases**:
- Company blog aggregator
- Topic-specific blog collection
- Team member blog compilation
- Community blog hub

### Scripting Support

#### Groovy Scripting

Roller includes Groovy scripting support for automation:

**Available Scripts** (in `docs/examples/scripting/groovy/`):

1. **User Management**:
   - `createuser.gy` - Create new users programmatically
   - `deleteuser.gy` - Remove users
   - `listusers.gy` - List all users
   - `checkuser.gy` - Verify user existence

2. **Blog Management**:
   - `createblog.gy` - Create new blogs
   - `createentry.gy` - Publish blog entries
   - `createcomment.gy` - Add comments
   - `createtestdata.gy` - Generate test content

3. **AtomPub Examples**:
   - `atompost.groovy` - Publish via AtomPub API

4. **Admin Protocol**:
   - `createuser.gy` - Admin user creation
   - `listcollections.gy` - List blog collections

**Example Groovy Script**:
```groovy
// createentry.gy - Create a blog entry
def roller = new GroovyRollerBinding(context)
def blog = roller.getWeblog("myblog")
def entry = blog.createEntry()
entry.title = "My New Post"
entry.content = "Post content here..."
entry.pubTime = new Date()
entry.status = "PUBLISHED"
entry.save()
```

#### BSF Scripting

Support for Bean Scripting Framework (BSF) for Java integration.

---

## Administration & Configuration

### Blog Settings

![Blog Settings](docs/images/user-guide-setting.png)

**Basic Settings**:
- **Blog Name**: Display name
- **Description**: Blog description
- **Email**: Contact email
- **Locale**: Language setting
- **Timezone**: Post timestamp zone
- **Entry Display Count**: Posts per page

![Blog Settings Extended](docs/images/user-guide-settings.png)

**Advanced Settings**:
- **Blog Active**: Enable/disable blog
- **Allow Comments**: Global comment control
- **Default Comment Status**: Auto-approve or moderate
- **Entries Per Page**: Pagination
- **Analytics Code**: Google Analytics, etc.
- **Default Plugins**: Enable page model plugins
- **Editor Type**: Rich text or  plain text

### Site Administration

![Site Settings](docs/images/user-guide-26-site-setting.png)

**System-Wide Configuration** (Admin only):
- **Site Name**: Installation name
- **Site Description**: Site meta description
- **Admin Email**: System administrator email
- **Default Locale**: Default language
- **Timezone**: Server timezone
- **Registration**: Allow/disallow new user registration
- **New User Roles**: Default role for new users
- **Front Page Blog**: Default blog to display
- **Default Theme**: Theme for new blogs

**System Settings**:
- **Database**: Connection configuration
- **File Upload**: Upload limits and storage
- **Cache**: Caching configuration
- **Search**: Lucene index settings
- **Email**: SMTP configuration
- **Logging**: Log4j2 configuration

### Internationalization

![i18n Support](docs/images/user-guide-internationalization.png)

**Supported Languages**:
- 🇺🇸 English
- 🇪🇸 Spanish (Español)
- 🇫🇷 French (Français)
- 🇩🇪 German (Deutsch)
- 🇯🇵 Japanese (日本語)
- 🇰🇷 Korean (한국어)
- 🇷🇺 Russian (Русский)
- 🇨🇳 Chinese (中文)

**i18n Features**:
- **User Interface**: Fully translated admin interface
- **Blog Content**: Per-blog language settings
- **Date/Time**: Locale-aware formatting
- **RTL Support**: Right-to-left languages
- **Custom Translations**: Add your own language files

**Translation Files** (in `app/target/classes/`):
```
ApplicationResources.properties       # English (default)
ApplicationResources_es.properties    # Spanish
ApplicationResources_fr.properties    # French
ApplicationResources_de.properties    # German
ApplicationResources_ja.properties    # Japanese
ApplicationResources_ko.properties    # Korean
ApplicationResources_ru.properties    # Russian
ApplicationResources_zh_CN.properties # Chinese
```

---

## Installation & Deployment

### Prerequisites

- **Java Development Kit (JDK)**: Version 11 or higher
- **Apache Maven**: Version 3.6 or higher (for building)
- **Database**: MySQL 5.7+, PostgreSQL 9.6+, MariaDB 10.2+, or Derby (dev only)
- **Application Server**: Apache Tomcat 9.0+ or Jetty 10+

### Installation Method 1: Quick Start with Maven (Development)

Perfect for testing and development. Uses embedded Jetty and Derby database.

```bash
# Clone the repository
git clone https://github.com/apache/roller.git
cd roller

# Build the project (skip tests for faster build)
mvn clean install -DskipTests=true

# Run with embedded Jetty
cd app
mvn jetty:run
```

**Access**: `http://localhost:8080/roller`

**Features**:
- ✅ No database setup required (uses Derby)
- ✅ Quick start for testing
- ✅ Hot reload during development
- ⚠️ NOT for production use

### Installation Method 2: Docker (Recommended for Testing)

No Java or Maven installation required!

```bash
# Clone repository
git clone https://github.com/apache/roller.git
cd roller

# Start with Docker Compose
docker-compose up
```

**What's Included**:
- Roller web application
- PostgreSQL database
- Automatic database initialization
- Volume mounts for data persistence

**Access**: `http://localhost:8080/roller`

**Docker Configuration** (from `docker-compose.yml`):
```yaml
services:
  db:
    image: postgres:13
    environment:
      POSTGRES_DB: roller
      POSTGRES_USER: roller
      POSTGRES_PASSWORD: roller
    volumes:
      - postgres-data:/var/lib/postgresql/data
      
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      DB_TYPE: postgresql
      DB_HOST: db
      DB_PORT: 5432
```

### Installation Method 3: Production Deployment with Tomcat

![Tomcat Deployment](docs/images/roller-install-guide-tomcat.png)

**Step 1: Database Setup**

Choose and install your database:

**MySQL/MariaDB**:
```sql
CREATE DATABASE roller CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'roller'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON roller.* TO 'roller'@'localhost';
FLUSH PRIVILEGES;
```

**PostgreSQL**:
```sql
CREATE DATABASE roller;
CREATE USER roller WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE roller TO roller;
```

**Step 2: Run Database Scripts**

Locate scripts in `app/src/main/resources/dbscripts/`:
```
dbscripts/
├── mysql/
│   └── createdb.sql
├── postgresql/
│   └── createdb.sql
├── oracle/
│   └── createdb.sql
└── derby/
    └── createdb.sql
```

**Execute scripts**:
```bash
# MySQL
mysql -u roller -p roller < app/src/main/resources/dbscripts/mysql/createdb.sql

# PostgreSQL
psql -U roller -d roller -f app/src/main/resources/dbscripts/postgresql/createdb.sql
```

**Step 3: Build Roller WAR File**

```bash
cd roller
mvn clean package -DskipTests=true
```

**Output**: `app/target/roller.war`

**Step 4: Configure Roller**

Create `roller-custom.properties`:
```properties
# Database Configuration
database.jpa.configuration=roller-jpa-config
database.jdbc.driverClass=com.mysql.jdbc.Driver
database.jdbc.connectionURL=jdbc:mysql://localhost:3306/roller?useSSL=false&characterEncoding=UTF-8
database.jdbc.username=roller
database.jdbc.password=your_password

# File Upload
uploads.enabled=true
uploads.dir=/var/roller/uploads
uploads.types.allowed=image/jpeg,image/png,image/gif,image/svg+xml,application/pdf

# Search Index
search.enabled=true
search.index.dir=/var/roller/search-index

# Mail Configuration
mail.smtp.host=localhost
mail.smtp.port=25
mail.from=noreply@yourdomain.com

# Site URL
site.absoluteurl=https://yourblog.com
```

**Step 5: Deploy to Tomcat**

![Tomcat Deploy](docs/images/roller-install-guide-tomcat-deploy.png)

```bash
# Copy WAR to Tomcat
cp app/target/roller.war /opt/tomcat/webapps/

# Copy configuration
mkdir -p /opt/tomcat/lib
cp roller-custom.properties /opt/tomcat/lib/

# Copy database driver
cp ~/.m2/repository/mysql/mysql-connector-java/8.0.xx/mysql-connector-java-8.0.xx.jar /opt/tomcat/lib/

# Start Tomcat
/opt/tomcat/bin/startup.sh
```

**Step 6: Initial Setup**

1. Access `http://yourserver:8080/roller`
2. Follow setup wizard
3. Create administrator account
4. Configure site settings

### Troubleshooting Installation

#### Database Connection Errors

![DB Connection Error](docs/images/db-connection-error.png)

**Error**: Cannot connect to database

**Solutions**:
1. Verify database is running:
   ```bash
   # MySQL
   sudo systemctl status mysql
   
   # PostgreSQL
   sudo systemctl status postgresql
   ```

2. Check connection URL in `roller-custom.properties`
3. Verify username/password
4. Check firewall rules
5. Test connection:
   ```bash
   # MySQL
   mysql -h localhost -u roller -p
   
   # PostgreSQL
   psql -h localhost -U roller -d roller
   ```

#### No Tables Found

![No Tables Found](docs/images/no-tables-found.png)

**Error**: Database tables not found

**Solutions**:
1. Verify database scripts were executed
2. Check database name in connection URL
3. Manually run create scripts:
   ```bash
   mysql -u roller -p roller < dbscripts/mysql/createdb.sql
   ```
4. Check Roller logs for errors:
   ```bash
   tail -f /opt/tomcat/logs/catalina.out
   ```

#### Deployment Failures

**Build errors**:
```bash
# Clear Maven cache
mvn dependency:purge-local-repository

# Clean rebuild
mvn clean install -U -DskipTests=true
```

**Tomcat errors**:
```bash
# Check Tomcat logs
tail -f /opt/tomcat/logs/catalina.out

# Verify Java version
java -version  # Should be 11+

# Check Tomcat version
/opt/tomcat/bin/version.sh
```

---

## Development Guide

### Building from Source

```bash
# Full build with all tests
mvn clean install

# Quick build (skip tests)
mvn clean install -DskipTests=true

# Build specific module
cd app
mvn clean package

# Build with different database
mvn clean install -Ddb=postgresql
```

### Running Tests

```bash
# Run all unit tests
mvn test

# Run specific test
mvn test -Dtest=EntryBasicTests

# Run Selenium integration tests
cd it-selenium
mvn verify

# Test with coverage
mvn clean test jacoco:report
```

### IDE Setup

#### IntelliJ IDEA
1. **Import Project**
   - File → Open → Select `pom.xml`
   - Choose "Open as Project"
   
2. **Configure JDK**
   - File → Project Structure → Project SDK → Java 11+
   
3. **Enable Annotation Processing**
   - Settings → Build → Compiler → Annotation Processors → Enable
   
4. **Install Plugins**
   - Velocity support
   - Maven integration

5. **Run Configuration**
   - Add new "Maven" configuration
   - Working directory: `{project}/app`
   - Command: `jetty:run`

#### Eclipse
1. **Import Maven Project**
   - File → Import → Maven → Existing Maven Projects
   
2. **Configure Java Compiler**
   - Properties → Java Compiler → Compiler compliance level: 11
   
3. **Install Plugins**
   - M2Eclipse (Maven integration)
   - Web Tools Platform (WTP)
   - Velocity Editor

### Code Structure

```
org.apache.roller.weblogger/
├── business/                 # Business Logic Layer
│   ├── WeblogManager.java        # Blog operations
│   ├── WeblogEntryManager.java   # Entry CRUD
│   ├── UserManager.java          # User management
│   ├── MediaFileManager.java     # File operations
│   ├── BookmarkManager.java      # Blogroll
│   ├── ThemeManager.java         # Theme handling
│   └── SearchManager.java        # Lucene search
│
├── ui/                       # User Interface Layer
│   ├── core/                     # Core UI components
│   ├── rendering/                # Template rendering
│   │   ├── Renderer.java
│   │   ├── ThymeleafRenderer.java
│   │   └── VelocityRenderer.java
│   └── struts2/                  # Struts2 actions
│       ├── editor/               # Entry editor
│       ├── admin/                # Admin actions
│       └── core/                 # Core actions
│
├── pojos/                    # Data Models (JPA Entities)
│   ├── User.java                 # User entity
│   ├── Weblog.java               # Blog entity
│   ├── WeblogEntry.java          # Entry entity
│   ├── WeblogCategory.java       # Category entity
│   ├── MediaFile.java            # File entity
│   └── WeblogEntryComment.java   # Comment entity
│
├── config/                   # Configuration
│   ├── WebloggerConfig.java      # Main config
│   ├── GuiceServletConfig.java   # Guice DI
│   └── SecurityConfig.java       # Spring Security
│
└── util/                     # Utilities
    ├── HTMLSanitizer.java        # XSS protection
    ├── Utilities.java            # Common utils
    └── cache/                    # Caching utilities
```

### Database Schema

**Core Tables**:
- `roller_user` - User accounts
- `weblog` - Blog definitions
- `weblogentry` - Blog posts
- `weblogcategory` - Categories
- `entryattribute` - Entry metadata
- `weblogentry_tag` - Entry tags
- `mediafile` - Uploaded files
- `mediafiledir` - Directory structure
- `roller_comment` - Comments
- `roller_properties` - System configuration

### Adding New Features

**Example: Adding a Custom Field to Blog Entry**

1. **Update Database Schema**:
```sql
ALTER TABLE weblogentry ADD COLUMN custom_field VARCHAR(255);
```

2. **Update Entity** (`WeblogEntry.java`):
```java
@Column(name = "custom_field")
private String customField;

public String getCustomField() {
    return customField;
}

public void setCustomField(String customField) {
    this.customField = customField;
}
```

3. **Update UI** (JSP/Template):
```jsp
<s:textfield name="bean.customField" label="Custom Field" />
```

4. **Update Action** (Struts2):
```java
public class EntryEdit extends UIAction {
    private String customField;
    
    public String save() {
        getEntry().setCustomField(customField);
        // ... save logic
    }
}
```

5. **Update Template** (Velocity):
```velocity
#if($entry.customField)
    <div class="custom-field">$entry.customField</div>
#end
```

### Contributing

**Contribution Workflow**:

1. **Fork & Clone**:
```bash
git clone https://github.com/YOUR_USERNAME/roller.git
cd roller
git remote add upstream https://github.com/apache/roller.git
```

2. **Create Feature Branch**:
```bash
git checkout -b feature/my-awesome-feature
```

3. **Make Changes**:
- Follow Java coding conventions
- Add unit tests
- Update documentation
- Follow existing code style

4. **Test Thoroughly**:
```bash
mvn clean test
mvn verify
```

5. **Commit**:
```bash
git add .
git commit -m "Add awesome new feature

Detailed description of changes:
- Added XYZ functionality
- Updated ABC component
- Fixed issue with DEF

Closes #123"
```

6. **Push & PR**:
```bash
git push origin feature/my-awesome-feature
```
Then create Pull Request on GitHub.

**Coding Standards**:
- Use 4 spaces for indentation (no tabs)
- Maximum line length: 120 characters
- JavaDoc for public methods
- Unit tests for new features
- Follow existing naming conventions

---

## Documentation Resources

### Official Documentation

📚 **Installation Guide**: [docs/roller-install-guide.adoc](docs/roller-install-guide.adoc)
- System requirements
- Database configuration
- Application server setup
- Production deployment
- Performance tuning

📚 **User Guide**: [docs/roller-user-guide.adoc](docs/roller-user-guide.adoc)
- Getting started
- Creating content
- Managing blogs
- Customization
- Administration

📚 **Template Guide**: [docs/roller-template-guide.adoc](docs/roller-template-guide.adoc)
- Velocity syntax
- Model objects
- Custom macros
- Theme development
- Best practices

### Example Code

📂 **Examples Directory**: [docs/examples/](docs/examples/)

**Plugin Examples**:
- `plugins/pluginmodel/` - Custom page model plugin

**Scripting Examples**:
- `scripting/groovy/` - Groovy automation scripts
- `scripting/bsf/` - BSF integration examples

**Configuration Examples**:
- `configs/tomcat/` - Tomcat configuration
- `configs/wbloggar/` - Blog client setup

**Custom Macros**:
- `macros/site.vm` - Example Velocity macros

### Online Resources

🌐 **Apache Roller Wiki**:
- Build and Run: <https://cwiki.apache.org/confluence/x/EM4>
- Contributing Guide: <https://cwiki.apache.org/confluence/x/2hsB>
- Release Process: <https://cwiki.apache.org/confluence/x/gycB>
- Developer Resources: <https://cwiki.apache.org/confluence/x/D84>

📂 **Source Repository**: <https://github.com/apache/roller>

🌐 **Official Website**: <http://roller.apache.org>

🐛 **Issue Tracker**: <https://github.com/apache/roller/issues>

💬 **Mailing Lists**: <http://roller.apache.org/mailing-lists.html>

### Getting Help

**Community Support**:
- Mailing lists (user@ and dev@)
- Stack Overflow (tag: `apache-roller`)
- GitHub Discussions
- Apache Confluence wiki

**Commercial Support**:
- Contact Apache Roller PMC
- Professional services available

---

## Project Information

**Apache Roller Version 6.1.5**

| Property | Value |
|----------|-------|
| **Version** | 6.1.5 |
| **Inception Year** | 2002 (20+ years!) |
| **Language** | Java 11+ |
| **Build Tool** | Apache Maven 3.6+ |
| **License** | Apache License 2.0 |
| **Repository** | <https://github.com/apache/roller> |
| **Website** | <http://roller.apache.org> |

### Technology Stack Summary

**Backend**:
- Java 11+
- Struts 2.5.29
- Spring Framework 5.3.39
- Spring Security 5.8.14
- Google Guice 7.0.0
- EclipseLink 4.0.5 (JPA)
- Apache Lucene 9.12.1
- Velocity 2.4.1
- ROME 1.19.0 (RSS/Atom)

**Frontend**:
- JSP 2.2
- AngularJS 1.7.8
- jQuery
- Bootstrap
- Custom JavaScript

**Database**:
- MySQL / MariaDB (recommended)
- PostgreSQL
- Oracle
- Derby (development only)

**Build & Test**:
- Maven 3.6+
- Jetty 10.0.24
- Selenium (integration tests)
- JUnit (unit tests)

### License

```
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements. See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

### Acknowledgments

Apache Roller is developed and maintained by the Apache Software Foundation community. Special thanks to all contributors who have made Roller a stable, feature-rich blogging platform used by thousands worldwide.

**Key Contributors**:
- Dave Johnson (Original creator)
- Allen Gilliland
- Elias Torres
- Matt Raible
- And many more community contributors!

---

## Quick Reference

### Common URLs

```
Main blog:          /{bloghandle}
Entry permalink:    /{bloghandle}/entry/{anchor}
Category view:      /{bloghandle}/category/{category}
Tag view:           /{bloghandle}/tag/{tag}
RSS feed:           /{bloghandle}/feed/entries/rss
Atom feed:          /{bloghandle}/feed/entries/atom
Login:              /roller/login
Admin:              /roller/roller-ui/admin
```

### Configuration Files

| File | Location | Purpose |
|------|----------|---------|
| `roller-custom.properties` | Classpath | Main configuration |
| `web.xml` | `WEB-INF/` | Web app descriptor |
| `struts.xml` | `WEB-INF/classes/` | Struts config |
| `log4j2.xml` | `WEB-INF/classes/` | Logging |
| `persistence.xml` | `META-INF/` | JPA config |

### Useful Commands

```bash
# Build
mvn clean install -DskipTests=true

# Run development server
mvn jetty:run

# Run tests
mvn test

# Package WAR
mvn package

# Clean database (Derby)
rm -rf target/testdb

# View logs
tail -f logs/roller.log
```

---

**🚀 Ready to start blogging with Apache Roller?**

Choose your installation method above and get started in minutes!

For questions or issues, visit our [GitHub repository](https://github.com/apache/roller) or join our [mailing lists](http://roller.apache.org/mailing-lists.html).

**Happy Blogging! 📝**

### Prerequisites

- **Java Development Kit (JDK)**: Version 11 or higher
- **Apache Maven**: Version 3.6 or higher
- **Database**: MySQL, PostgreSQL, MariaDB, or Derby
- **Application Server**: Apache Tomcat 9+ or Jetty

### Installation Methods

#### Option 1: Quick Start with Maven (Development)

Perfect for testing and development. Uses embedded Jetty and Derby database.

```bash
# Clone the repository
git clone https://github.com/apache/roller.git
cd roller

# Build the project
mvn clean install -DskipTests=true

# Run with Jetty
mvn jetty:run
```

Access Roller at: `http://localhost:8080/roller`

#### Option 2: Quick Start with Docker (Recommended for Testing)

No Java or Maven installation required!

```bash
# Clone the repository
git clone https://github.com/apache/roller.git
cd roller

# Start with Docker Compose (includes PostgreSQL)
docker-compose up
```

Access Roller at: `http://localhost:8080/roller`

The Docker setup includes:
- Roller web application
- PostgreSQL database
- Automatic database initialization
- Volume mounts for persistence

#### Option 3: Production Deployment

![Tomcat Deployment](docs/images/roller-install-guide-tomcat-deploy.png)

For production environments:

1. **Download Official Release**
   - Get the latest stable release from Apache Roller website
   
2. **Setup Database**
   - Create database and user
   - Run database creation scripts from `app/src/main/resources/dbscripts/`
   - Configure connection in `roller-custom.properties`

3. **Deploy to Tomcat**
   - Copy WAR file to Tomcat's `webapps` directory
   - Configure database connection
   - Start Tomcat

4. **Initial Configuration**
   - Access the setup wizard
   - Create admin account
   - Configure site settings

![Database Connection](docs/images/db-connection-error.png)
*Note: Ensure proper database configuration to avoid connection errors*

### First-Time Setup

#### 1. Welcome Screen

![Welcome Screen](docs/images/user-guide-1-welcome.png)

After starting Roller, you'll see the welcome screen.

#### 2. User Registration

![Registration](docs/images/user-guide-2-registration.png)

Create your first user account (will be admin).

#### 3. Create Your Weblog

![Create Weblog](docs/images/user-guide-3-webblog.png)

Set up your first blog with:
- Blog name
- Blog handle (URL identifier)
- Description
- Theme selection

#### 4. Navigation

![Navigation Menu](docs/images/user-guide-6-navigation.png)

Navigate through the admin interface:
- **Entries**: Create and manage blog posts
- **Comments**: Moderate comments
- **Settings**: Configure blog options
- **Design**: Customize theme
- **Members**: Manage blog contributors

## Configuration

### Database Configuration

Supported databases:
- **MySQL/MariaDB**: Recommended for production
- **PostgreSQL**: Good performance and features
- **Derby**: Embedded database for development only
- **Oracle**: Enterprise deployments
- **HSQLDB**: Testing and development

### Configuration Files

| File | Location | Purpose |
|------|----------|---------|
| `roller-custom.properties` | Classpath | Main configuration |
| `web.xml` | `WEB-INF/` | Web application descriptor |
| `struts.xml` | `WEB-INF/` | Struts configuration |
| `log4j2.xml` | Classpath | Logging configuration |

### Key Configuration Properties

```properties
# Database
database.jpa.configuration=roller-jpa-config
database.jdbc.driverClass=com.mysql.jdbc.Driver
database.jdbc.connectionURL=jdbc:mysql://localhost:3306/roller
database.jdbc.username=roller
database.jdbc.password=your-password

# File uploads
uploads.enabled=true
uploads.dir=/var/roller/uploads
uploads.types.allowed=image/jpeg,image/png,image/gif

# Search
search.enabled=true
search.index.dir=/var/roller/search-index

# Email
mail.smtp.host=localhost
mail.smtp.port=25
```

## Development Guide

### Building from Source

```bash
# Full build with tests
mvn clean install

# Build without tests (faster)
mvn clean install -DskipTests=true

# Build specific module
cd app
mvn clean package
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=EntryBasicTests

# Run Selenium integration tests
cd it-selenium
mvn verify
```

### IDE Setup

#### IntelliJ IDEA
1. Import as Maven project
2. Set JDK 11 or higher
3. Enable annotation processing
4. Install Velocity plugin for template editing

#### Eclipse
1. Import as Maven project
2. Configure Java compiler (11+)
3. Install Maven integration (m2e)
4. Install Web Tools Platform (WTP)

### Code Structure

```
org.apache.roller.weblogger/
├── business/          # Business logic layer
│   ├── WeblogManager.java
│   ├── UserManager.java
│   ├── WeblogEntryManager.java
│   └── MediaFileManager.java
├── ui/                # Struts2 actions and UI logic
│   ├── core/
│   ├── rendering/
│   └── struts2/
├── pojos/             # Plain Old Java Objects (entities)
│   ├── User.java
│   ├── Weblog.java
│   ├── WeblogEntry.java
│   └── MediaFile.java
├── config/            # Configuration and initialization
└── util/              # Utility classes
```

## Common Tasks

### Creating a Blog Post

![Blog Editor](docs/images/user-guide-8-editor.png)

1. Navigate to **Entries** → **New Entry**
2. Enter title and content
3. Select category and tags
4. Choose status (Draft/Published/Scheduled)
5. Click **Save** or **Publish**

### Managing Categories

![Categories](docs/images/user-guide-10-categories.png)

Organize posts by category:
- Create hierarchical categories
- Assign posts to categories
- Generate category-specific feeds

### Managing Media

![Media Management](docs/images/user-guide-14-upload-complete.png)

Upload and organize images:
- Drag and drop upload
- Organize in directories
- Generate thumbnails
- Insert into blog posts

### Theme Customization

![Design Menu](docs/images/user-guide-20-design-menu.png)

Customize your blog's appearance:
- **Templates**: Edit Velocity templates
- **Stylesheets**: Modify CSS
- **Resources**: Add custom images/files
- **Import/Export**: Share themes

### Member Management

![Invite Member](docs/images/user-guide-22-invite-member.png)

Collaborate with multiple authors:
- Invite users to your blog
- Assign roles (Admin, Author, Limited)
- Manage permissions
- Track contributions

### Spam Protection

![Spam Settings](docs/images/user-guide-spam.png)

Protect against comment spam:
- Blacklist/whitelist management
- Keyword filtering
- CAPTCHA integration
- Comment moderation queue

## Advanced Features

### Ping Services

![Ping Configuration](docs/images/user-guide-30-ping.png)

Notify blog directories of updates:
- Configure ping targets
- Automatic ping on publish
- Manual ping option
- Track ping history

### Planet Aggregator

![Planet Configuration](docs/images/user-guide-32-planet-config.png)

Aggregate external blogs:
- Subscribe to RSS/Atom feeds
- Display aggregated content
- Filter by category
- Custom planet page

### Plugin Development

![Plugin Model](docs/images/user-guide-plugin.png)

Extend Roller's functionality:
- Create custom page models
- Add custom renderers
- Implement event listeners
- Package and deploy plugins

Example plugin structure:
```
src/org/apache/roller/examples/plugins/
└── pagemodel/
    └── AuthenticatedUserModel.java
```

### API Access

![API Settings](docs/images/user-guide-17-api.png)

Programmatic access to Roller:
- **AtomPub**: Atom Publishing Protocol
- **MetaWeblog API**: Compatible with blog clients
- **XML-RPC**: Remote procedure calls

Use blog clients like:
- MarsEdit (macOS)
- Windows Live Writer
- BlogJet
- Ecto

## Troubleshooting

### Common Issues

#### Database Connection Errors

![No Tables Found](docs/images/no-tables-found.png)

**Problem**: Cannot connect to database or tables not found

**Solutions**:
1. Verify database is running
2. Check connection properties in `roller-custom.properties`
3. Ensure database schema is created
4. Run database creation scripts manually

#### Tomcat Deployment Issues

![Tomcat Configuration](docs/images/roller-install-guide-tomcat.png)

**Problem**: Application fails to deploy

**Solutions**:
1. Check Tomcat logs (`catalina.out`)
2. Verify Java version compatibility
3. Ensure sufficient memory allocation
4. Check file permissions

#### Build Failures

**Problem**: Maven build fails

**Solutions**:
```bash
# Clear local repository cache
mvn dependency:purge-local-repository

# Clean and rebuild
mvn clean install -U

# Skip problematic tests
mvn install -DskipTests=true
```

## Documentation

### Available Guides

The comprehensive documentation is available in AsciiDoc format:

📚 **Installation Guide**: `docs/roller-install-guide.adoc`
- System requirements
- Database setup
- Application server configuration
- Production deployment

📚 **User Guide**: `docs/roller-user-guide.adoc`
- Getting started
- Creating and managing content
- Customization options
- Administration tasks

📚 **Template Guide**: `docs/roller-template-guide.adoc`
- Velocity template syntax
- Theme development
- Custom macros
- Template best practices

### Additional Resources

🌐 **Apache Roller Wiki**
- Build and run: <https://cwiki.apache.org/confluence/x/EM4>
- Contributing: <https://cwiki.apache.org/confluence/x/2hsB>
- Release process: <https://cwiki.apache.org/confluence/x/gycB>
- Developer resources: <https://cwiki.apache.org/confluence/x/D84>

📂 **Example Code**: `docs/examples/`
- Plugin examples
- Scripting examples (Groovy, BSF)
- Configuration examples
- Custom macros

## Contributing

We welcome contributions! Here's how you can help:

1. **Fork the Repository**
   ```bash
   git clone https://github.com/apache/roller.git
   cd roller
   ```

2. **Create a Feature Branch**
   ```bash
   git checkout -b feature/my-new-feature
   ```

3. **Make Your Changes**
   - Follow Java coding conventions
   - Add unit tests for new features
   - Update documentation

4. **Run Tests**
   ```bash
   mvn clean test
   ```

5. **Submit a Pull Request**
   - Describe your changes
   - Reference any related issues
   - Ensure CI passes

### Contribution Guidelines

- Follow Apache License 2.0
- Maintain backward compatibility
- Write clear commit messages
- Add JavaDoc for public APIs
- Follow existing code style

## License

Apache Roller is licensed under the **Apache License 2.0**

```
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements. See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Project Information

- **Version**: 6.1.5
- **Inception Year**: 2002
- **Language**: Java
- **Framework**: Struts 2, Spring, Velocity
- **Build Tool**: Maven
- **Repository**: <https://github.com/apache/roller>
- **Website**: <http://roller.apache.org>
- **License**: Apache License 2.0

## Support & Community

- **Issue Tracker**: <https://github.com/apache/roller/issues>
- **Mailing Lists**: <http://roller.apache.org/mailing-lists.html>
- **Apache Confluence**: <https://cwiki.apache.org/confluence/display/ROLLER>
- **Stack Overflow**: Tag your questions with `apache-roller`

## Version History

| Version | Release Date | Key Features |
|---------|--------------|--------------|
| 6.1.5 | Current | Security updates, dependency upgrades |
| 6.1.0 | - | Spring Security integration |
| 6.0.0 | - | Major refactoring, modern UI |
| 5.x | - | Java 8 support, REST API |

## Acknowledgments

Apache Roller is developed and maintained by the Apache Software Foundation community. Special thanks to all contributors who have helped make Roller a stable and feature-rich blogging platform.

---

**Ready to start blogging?** Follow the [Getting Started](#getting-started) guide above! 🚀
