# Security Scanner Benchmark Suite - Implementation Plan

## Project Overview

This document outlines the comprehensive plan to build a security scanner benchmark suite consisting of three realistic enterprise applications with strategically placed vulnerabilities. The benchmark will test scanning tools across OWASP Top 10, ML Top 10, and Agentic AI Top 10 vulnerability categories.

**CRITICAL REQUIREMENT**: Every single vulnerability must be meticulously tracked with exact file locations, line numbers, and comprehensive metadata for accurate evaluation.

## Repository Structure

### Branch Architecture
```
benchmark-repo/
├── main (documentation and vulnerability catalog)
├── testbed-web-app (TaskFlow - Full-stack JS/TS)
├── testbed-enterprise-java (FinanceHub - Spring Boot)
└── testbed-ml-python (InsightAI - Python ML Platform)
```

### Main Branch - Vulnerability Tracking Focus
```
main/
├── README.md                           # Project overview and usage
├── vulnerability-catalog/              # MASTER VULNERABILITY TRACKING
│   ├── web-app-vulnerabilities.json   # Complete TaskFlow vulnerability catalog
│   ├── java-vulnerabilities.json      # Complete FinanceHub vulnerability catalog  
│   ├── python-vulnerabilities.json    # Complete InsightAI vulnerability catalog
│   └── vulnerability-schema.json      # JSON schema for vulnerability format
├── docs/                              
│   ├── vulnerability-methodology.md   # How vulnerabilities are categorized
│   ├── testing-guide.md              # How to test against the benchmark
│   └── implementation-notes.md       # Technical notes for each testbed
└── scripts/                          # Future evaluation tools
    └── validate-catalog.py           # Validates vulnerability catalog format
```

## Vulnerability Tracking Schema

### Master Vulnerability Format
Every vulnerability MUST be documented with this exact format:

```json
{
  "vuln_id": "WEB-A01-001",              // Unique identifier: [TESTBED]-[CATEGORY]-[NUMBER]
  "testbed": "web-app",                  // web-app | enterprise-java | ml-python
  "category": "owasp",                   // owasp | ml | agentic
  "subcategory": "A01",                  // Specific OWASP/ML/Agentic category
  "file": "backend/routes/tasks.js",     // Exact file path from repo root
  "startLine": 45,                      // Starting line number
  "endLine": 52,                        // Ending line number (inclusive)
  "code": "app.get('/api/tasks/:id', (req, res) => {\n  const taskId = req.params.id;\n  const query = 'SELECT * FROM tasks WHERE id = ?';\n  db.get(query, [taskId], (err, task) => {\n    res.json(task);\n  });\n});", // EXACT vulnerable code
  "cwe": ["CWE-639"],                   // Relevant CWE numbers
  "severity": "high",                   // critical | high | medium | low
  "message": "Direct object reference without authorization check", // One-line description
  "name": "IDOR in Task Access",        // Vulnerability title
  "explanation": "Endpoint returns any task by ID without verifying user ownership or permissions", // Detailed explanation
  "remediation": "Add authorization check: if (task.userId !== req.user.id) return 403", // How to fix
  "expected_detection": ["semgrep", "llm-analysis"], // Which tools should detect this
  "difficulty": "easy",                 // easy | medium | hard (for scanner detection)
  "business_impact": "Users can access other users' private tasks", // Real-world impact
  "introduced_in_phase": "vulnerability-injection", // When this was added to clean code
  "verification_method": "manual-test", // How to verify the vulnerability exists
  "false_positive_risk": "low"          // Risk of false positives for this pattern
}
```

## Vulnerability Distribution Strategy

### Total Vulnerability Targets
- **TaskFlow (Web App)**: 38 total vulnerabilities
- **FinanceHub (Java)**: 37 total vulnerabilities  
- **InsightAI (Python)**: 40 total vulnerabilities
- **Grand Total**: 115 vulnerabilities

### Category Distribution

#### TaskFlow (testbed-web-app) - 38 vulnerabilities
```
OWASP Top 10: 25 vulnerabilities
├── A01 (Broken Access Control): 4 vulns
├── A02 (Cryptographic Failures): 3 vulns
├── A03 (Injection): 5 vulns
├── A04 (Insecure Design): 2 vulns
├── A05 (Security Misconfiguration): 3 vulns
├── A06 (Vulnerable Components): 3 vulns
├── A07 (Authentication Failures): 3 vulns
├── A08 (Data Integrity Failures): 1 vuln
├── A09 (Logging Failures): 1 vuln
└── A10 (SSRF): 2 vulns

Agentic AI Top 10: 8 vulnerabilities
├── Prompt Injection: 2 vulns
├── Insecure AI Output Handling: 2 vulns
├── AI Training Data Poisoning: 1 vuln
├── AI Model DoS: 1 vuln
├── AI Supply Chain: 1 vuln
└── Excessive AI Agency: 1 vuln

ML Top 10: 5 vulnerabilities
├── ML Input Manipulation: 2 vulns
├── ML Model Information Leakage: 2 vulns
└── ML API Security: 1 vuln
```

#### FinanceHub (testbed-enterprise-java) - 37 vulnerabilities
```
OWASP Top 10: 25 vulnerabilities
├── A01 (Broken Access Control): 5 vulns
├── A02 (Cryptographic Failures): 4 vulns
├── A03 (Injection): 5 vulns
├── A04 (Insecure Design): 3 vulns
├── A05 (Security Misconfiguration): 3 vulns
├── A06 (Vulnerable Components): 2 vulns
├── A07 (Authentication Failures): 2 vulns
└── A08-A10 (Remaining): 1 vuln each

ML Top 10: 7 vulnerabilities
├── ML Model Serving Vulnerabilities: 3 vulns
├── ML Training Data Security: 2 vulns
└── ML Model Theft/Extraction: 2 vulns

Agentic AI Top 10: 5 vulnerabilities
├── Enterprise AI Integration: 2 vulns
├── AI Agent Permissions: 2 vulns
└── AI Workflow Security: 1 vuln
```

#### InsightAI (testbed-ml-python) - 40 vulnerabilities
```
ML Top 10: 20 vulnerabilities
├── Input Manipulation: 4 vulns
├── Data Poisoning: 4 vulns
├── Model Inversion: 3 vulns
├── Membership Inference: 2 vulns
├── Model Theft: 2 vulns
├── AI Supply Chain: 3 vulns
└── Model Serving Security: 2 vulns

Agentic AI Top 10: 15 vulnerabilities
├── Prompt Injection: 4 vulns
├── Insecure Output Handling: 3 vulns
├── Training Data Poisoning: 2 vulns
├── Model DoS: 2 vulns
├── Information Disclosure: 2 vulns
└── Plugin Security: 2 vulns

OWASP Top 10: 5 vulnerabilities
├── API Security (FastAPI): 2 vulns
├── Authentication Issues: 2 vulns
└── Configuration Problems: 1 vuln
```

## Complete Application File Structures

### Branch: testbed-web-app (TaskFlow) - Complete File Tree

```
taskflow/
├── README.md                           # Application overview, setup instructions, API docs
├── package.json                        # Root workspace configuration, scripts, dev dependencies
├── .gitignore                         # Git ignore patterns for node_modules, .env, logs, build
├── .env.example                       # Template environment variables (no secrets)
├── docker-compose.yml                 # Multi-container setup: app, db, redis
├── docker-compose.prod.yml            # Production Docker configuration
├── Dockerfile                         # Multi-stage build for production
├── jest.config.js                     # Jest test configuration for monorepo
├── tsconfig.json                      # TypeScript configuration for workspace
├── .eslintrc.json                     # ESLint rules for TypeScript/React
├── .prettierrc                        # Code formatting rules
│
├── frontend/
│   ├── package.json                   # React app dependencies and scripts
│   ├── vite.config.ts                 # Vite build tool configuration
│   ├── tsconfig.json                  # TypeScript config for React app
│   ├── index.html                     # Root HTML template
│   ├── public/
│   │   ├── favicon.ico               # App favicon
│   │   ├── manifest.json             # PWA manifest
│   │   ├── robots.txt                # SEO robots file
│   │   └── icons/                    # App icons for different sizes
│   │       ├── icon-192.png
│   │       └── icon-512.png
│   ├── src/
│   │   ├── main.tsx                  # React app entry point
│   │   ├── App.tsx                   # Root App component with routing
│   │   ├── App.css                   # Global app styles
│   │   ├── index.css                 # Base CSS reset and variables
│   │   ├── components/
│   │   │   ├── auth/
│   │   │   │   ├── LoginForm.tsx     # User login form with validation
│   │   │   │   ├── RegisterForm.tsx  # User registration form
│   │   │   │   ├── ForgotPassword.tsx # Password reset form
│   │   │   │   ├── ProtectedRoute.tsx # Route guard component
│   │   │   │   ├── OAuthButtons.tsx   # Google/GitHub OAuth buttons
│   │   │   │   └── AuthProvider.tsx   # Auth context provider
│   │   │   ├── tasks/
│   │   │   │   ├── TaskList.tsx      # Task list display with filtering
│   │   │   │   ├── TaskCard.tsx      # Individual task card component
│   │   │   │   ├── TaskForm.tsx      # Create/edit task form
│   │   │   │   ├── TaskFilters.tsx   # Task filtering controls
│   │   │   │   ├── TaskComments.tsx  # Task comments section
│   │   │   │   ├── TaskAssignee.tsx  # Task assignment component
│   │   │   │   └── TaskProgress.tsx  # Task progress indicator
│   │   │   ├── projects/
│   │   │   │   ├── ProjectList.tsx   # Project list display
│   │   │   │   ├── ProjectCard.tsx   # Project card component
│   │   │   │   ├── ProjectForm.tsx   # Create/edit project form
│   │   │   │   └── ProjectMembers.tsx # Project team management
│   │   │   ├── files/
│   │   │   │   ├── FileUpload.tsx    # Drag-and-drop file upload
│   │   │   │   ├── FileViewer.tsx    # File preview component
│   │   │   │   ├── FileList.tsx      # File listing with actions
│   │   │   │   └── FileThumbnail.tsx # File thumbnail generator
│   │   │   ├── shared/
│   │   │   │   ├── Header.tsx        # App header with navigation
│   │   │   │   ├── Sidebar.tsx       # Navigation sidebar
│   │   │   │   ├── Footer.tsx        # App footer
│   │   │   │   ├── LoadingSpinner.tsx # Loading indicator
│   │   │   │   ├── ErrorBoundary.tsx # React error boundary
│   │   │   │   ├── Modal.tsx         # Reusable modal component
│   │   │   │   ├── Button.tsx        # Styled button component
│   │   │   │   ├── Input.tsx         # Styled input component
│   │   │   │   ├── Textarea.tsx      # Styled textarea component
│   │   │   │   ├── Select.tsx        # Styled select component
│   │   │   │   └── Notification.tsx  # Toast notification component
│   │   │   └── ai/
│   │   │       ├── AIAssistant.tsx   # AI chatbot interface
│   │   │       ├── AIPromptInput.tsx # AI prompt input component
│   │   │       └── AIResponseDisplay.tsx # AI response formatter
│   │   ├── pages/
│   │   │   ├── Dashboard.tsx         # Main dashboard page
│   │   │   ├── Login.tsx             # Login page
│   │   │   ├── Register.tsx          # Registration page
│   │   │   ├── Profile.tsx           # User profile page
│   │   │   ├── Projects.tsx          # Projects listing page
│   │   │   ├── ProjectDetail.tsx     # Individual project page
│   │   │   ├── Tasks.tsx             # Tasks listing page
│   │   │   ├── Settings.tsx          # User settings page
│   │   │   ├── Team.tsx              # Team management page
│   │   │   └── NotFound.tsx          # 404 error page
│   │   ├── hooks/
│   │   │   ├── useAuth.ts            # Authentication hook
│   │   │   ├── useTasks.ts           # Task management hook
│   │   │   ├── useProjects.ts        # Project management hook
│   │   │   ├── useApi.ts             # API request hook
│   │   │   ├── useLocalStorage.ts    # Local storage hook
│   │   │   ├── useDebounce.ts        # Debounce hook for search
│   │   │   └── useWebSocket.ts       # WebSocket connection hook
│   │   ├── services/
│   │   │   ├── api.ts                # Axios API client configuration
│   │   │   ├── auth.ts               # Authentication service
│   │   │   ├── tasks.ts              # Task API service
│   │   │   ├── projects.ts           # Project API service
│   │   │   ├── files.ts              # File upload/download service
│   │   │   ├── users.ts              # User management service
│   │   │   └── websocket.ts          # WebSocket service
│   │   ├── utils/
│   │   │   ├── validation.ts         # Form validation utilities
│   │   │   ├── formatting.ts         # Date/text formatting utilities
│   │   │   ├── constants.ts          # App constants and enums
│   │   │   ├── storage.ts            # Local storage utilities
│   │   │   ├── permissions.ts        # Permission checking utilities
│   │   │   └── errorHandling.ts      # Error handling utilities
│   │   ├── types/
│   │   │   ├── auth.ts               # Authentication type definitions
│   │   │   ├── task.ts               # Task type definitions
│   │   │   ├── project.ts            # Project type definitions
│   │   │   ├── user.ts               # User type definitions
│   │   │   ├── file.ts               # File type definitions
│   │   │   └── api.ts                # API response type definitions
│   │   └── __tests__/
│   │       ├── components/           # Component tests
│   │       ├── hooks/                # Hook tests
│   │       ├── services/             # Service tests
│   │       └── utils/                # Utility tests
│   └── dist/                         # Built frontend files (git ignored)
│
├── backend/
│   ├── package.json                  # Backend dependencies and scripts
│   ├── tsconfig.json                 # TypeScript config for backend
│   ├── nodemon.json                  # Development server configuration
│   ├── src/
│   │   ├── server.ts                 # Express server entry point
│   │   ├── app.ts                    # Express app configuration
│   │   ├── controllers/
│   │   │   ├── authController.ts     # Authentication endpoints
│   │   │   ├── taskController.ts     # Task CRUD endpoints
│   │   │   ├── projectController.ts  # Project CRUD endpoints
│   │   │   ├── userController.ts     # User management endpoints
│   │   │   ├── fileController.ts     # File upload/download endpoints
│   │   │   ├── commentController.ts  # Comment CRUD endpoints
│   │   │   ├── webhookController.ts  # Webhook handling endpoints
│   │   │   └── aiController.ts       # AI assistant endpoints
│   │   ├── routes/
│   │   │   ├── index.ts              # Main router configuration
│   │   │   ├── auth.ts               # Authentication routes
│   │   │   ├── tasks.ts              # Task routes
│   │   │   ├── projects.ts           # Project routes
│   │   │   ├── users.ts              # User routes
│   │   │   ├── files.ts              # File routes
│   │   │   ├── comments.ts           # Comment routes
│   │   │   ├── webhooks.ts           # Webhook routes
│   │   │   └── ai.ts                 # AI assistant routes
│   │   ├── middleware/
│   │   │   ├── auth.ts               # JWT authentication middleware
│   │   │   ├── validation.ts         # Request validation middleware
│   │   │   ├── errorHandler.ts       # Global error handling middleware
│   │   │   ├── rateLimiter.ts        # Rate limiting middleware
│   │   │   ├── security.ts           # Security headers middleware
│   │   │   ├── cors.ts               # CORS configuration middleware
│   │   │   ├── logging.ts            # Request logging middleware
│   │   │   └── upload.ts             # File upload middleware (multer)
│   │   ├── services/
│   │   │   ├── authService.ts        # Authentication business logic
│   │   │   ├── taskService.ts        # Task business logic
│   │   │   ├── projectService.ts     # Project business logic
│   │   │   ├── userService.ts        # User management business logic
│   │   │   ├── fileService.ts        # File handling business logic
│   │   │   ├── commentService.ts     # Comment business logic
│   │   │   ├── emailService.ts       # Email notification service
│   │   │   ├── notificationService.ts # Push notification service
│   │   │   ├── searchService.ts      # Search functionality service
│   │   │   └── aiService.ts          # AI integration service
│   │   ├── models/
│   │   │   ├── User.ts               # User data model
│   │   │   ├── Task.ts               # Task data model
│   │   │   ├── Project.ts            # Project data model
│   │   │   ├── Comment.ts            # Comment data model
│   │   │   ├── File.ts               # File data model
│   │   │   ├── Session.ts            # Session data model
│   │   │   └── AuditLog.ts           # Audit log data model
│   │   ├── database/
│   │   │   ├── connection.ts         # Database connection setup
│   │   │   ├── query.ts              # Database query utilities
│   │   │   ├── migrations/
│   │   │   │   ├── 001_create_users.sql
│   │   │   │   ├── 002_create_projects.sql
│   │   │   │   ├── 003_create_tasks.sql
│   │   │   │   ├── 004_create_comments.sql
│   │   │   │   ├── 005_create_files.sql
│   │   │   │   └── 006_create_audit_logs.sql
│   │   │   └── seeds/
│   │   │       ├── users.sql         # Sample user data
│   │   │       ├── projects.sql      # Sample project data
│   │   │       └── tasks.sql         # Sample task data
│   │   ├── utils/
│   │   │   ├── crypto.ts             # Password hashing utilities
│   │   │   ├── jwt.ts                # JWT token utilities
│   │   │   ├── logger.ts             # Logging configuration
│   │   │   ├── validation.ts         # Data validation utilities
│   │   │   ├── email.ts              # Email template utilities
│   │   │   ├── fileUtils.ts          # File handling utilities
│   │   │   └── constants.ts          # Backend constants
│   │   ├── types/
│   │   │   ├── express.d.ts          # Express type extensions
│   │   │   ├── database.ts           # Database type definitions
│   │   │   └── jwt.ts                # JWT payload types
│   │   └── __tests__/
│   │       ├── controllers/          # Controller tests
│   │       ├── services/             # Service tests
│   │       ├── middleware/           # Middleware tests
│   │       ├── utils/                # Utility tests
│   │       └── integration/          # Integration tests
│   ├── uploads/                      # File upload directory (git ignored)
│   ├── logs/                         # Application logs (git ignored)
│   └── dist/                         # Compiled JavaScript (git ignored)
│
├── database/
│   ├── init.sql                      # Database initialization script
│   ├── schema.sql                    # Complete database schema
│   ├── seed-data.sql                 # Sample data for testing
│   └── backup/                       # Database backup scripts
│       ├── backup.sh                 # Backup script
│       └── restore.sh                # Restore script
│
├── docs/                             # Application documentation
│   ├── API.md                        # API documentation
│   ├── DEPLOYMENT.md                 # Deployment instructions
│   ├── DEVELOPMENT.md                # Development setup guide
│   └── ARCHITECTURE.md               # System architecture overview
│
├── scripts/                          # Utility scripts
│   ├── dev-setup.sh                  # Development environment setup
│   ├── build.sh                      # Build script
│   ├── test.sh                       # Test execution script
│   └── deploy.sh                     # Deployment script
│
└── .github/                          # GitHub workflows
    └── workflows/
        ├── ci.yml                    # Continuous integration
        ├── deploy.yml                # Deployment workflow
        └── security.yml              # Security scanning workflow
```

### Branch: testbed-enterprise-java (FinanceHub) - Complete File Tree

```
financehub/
├── README.md                         # Application overview and setup instructions
├── pom.xml                          # Maven project configuration and dependencies
├── .gitignore                       # Java/Maven specific ignore patterns
├── docker-compose.yml               # Multi-container setup: app, db, redis
├── Dockerfile                       # Multi-stage build for Spring Boot app
├── application.properties           # Default application configuration
├── lombok.config                    # Lombok configuration
├── checkstyle.xml                   # Code style configuration
│
├── src/
│   ├── main/
│   │   ├── java/com/financehub/
│   │   │   ├── FinanceHubApplication.java # Spring Boot main class
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java    # Spring Security configuration
│   │   │   │   ├── WebConfig.java         # Web MVC configuration
│   │   │   │   ├── DatabaseConfig.java    # JPA/Hibernate configuration
│   │   │   │   ├── RedisConfig.java       # Redis cache configuration
│   │   │   │   ├── SwaggerConfig.java     # API documentation configuration
│   │   │   │   ├── SchedulingConfig.java  # Scheduled tasks configuration
│   │   │   │   ├── AsyncConfig.java       # Async processing configuration
│   │   │   │   └── ActuatorConfig.java    # Spring Actuator configuration
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java    # Authentication endpoints
│   │   │   │   ├── TransactionController.java # Transaction CRUD endpoints
│   │   │   │   ├── AccountController.java     # Account management endpoints
│   │   │   │   ├── ReportController.java      # Financial report endpoints
│   │   │   │   ├── UserController.java        # User management endpoints
│   │   │   │   ├── AdminController.java       # Admin panel endpoints
│   │   │   │   ├── FileController.java        # File upload/download endpoints
│   │   │   │   ├── NotificationController.java # Notification endpoints
│   │   │   │   └── HealthController.java      # Health check endpoints
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java           # Authentication service
│   │   │   │   ├── TransactionService.java    # Transaction business logic
│   │   │   │   ├── AccountService.java        # Account management logic
│   │   │   │   ├── ReportService.java         # Report generation service
│   │   │   │   ├── UserService.java           # User management service
│   │   │   │   ├── NotificationService.java   # Notification service
│   │   │   │   ├── AuditService.java          # Audit logging service
│   │   │   │   ├── EmailService.java          # Email service
│   │   │   │   ├── FileService.java           # File handling service
│   │   │   │   ├── CacheService.java          # Cache management service
│   │   │   │   ├── SearchService.java         # Search functionality
│   │   │   │   └── MLService.java             # ML integration service
│   │   │   ├── repository/
│   │   │   │   ├── TransactionRepository.java # Transaction data access
│   │   │   │   ├── AccountRepository.java     # Account data access
│   │   │   │   ├── UserRepository.java        # User data access
│   │   │   │   ├── ReportRepository.java      # Report data access
│   │   │   │   ├── AuditLogRepository.java    # Audit log data access
│   │   │   │   ├── FileRepository.java        # File metadata data access
│   │   │   │   └── NotificationRepository.java # Notification data access
│   │   │   ├── entity/
│   │   │   │   ├── User.java                  # User entity with JPA annotations
│   │   │   │   ├── Account.java               # Financial account entity
│   │   │   │   ├── Transaction.java           # Transaction entity
│   │   │   │   ├── Report.java                # Report metadata entity
│   │   │   │   ├── AuditLog.java              # Audit log entity
│   │   │   │   ├── File.java                  # File metadata entity
│   │   │   │   ├── Notification.java          # Notification entity
│   │   │   │   └── Role.java                  # User role entity
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── LoginRequest.java      # Login request DTO
│   │   │   │   │   ├── TransactionRequest.java # Transaction creation DTO
│   │   │   │   │   ├── AccountRequest.java     # Account creation DTO
│   │   │   │   │   ├── ReportRequest.java      # Report generation DTO
│   │   │   │   │   ├── UserRequest.java        # User creation DTO
│   │   │   │   │   └── PasswordChangeRequest.java # Password change DTO
│   │   │   │   └── response/
│   │   │   │       ├── AuthResponse.java       # Authentication response DTO
│   │   │   │       ├── TransactionResponse.java # Transaction response DTO
│   │   │   │       ├── AccountResponse.java     # Account response DTO
│   │   │   │       ├── ReportResponse.java      # Report response DTO
│   │   │   │       ├── UserResponse.java        # User response DTO
│   │   │   │       └── ErrorResponse.java       # Error response DTO
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java      # JWT token generation/validation
│   │   │   │   ├── JwtAuthenticationFilter.java # JWT authentication filter
│   │   │   │   ├── UserPrincipal.java         # User principal implementation
│   │   │   │   ├── SecurityUtils.java         # Security utility methods
│   │   │   │   ├── PasswordEncoder.java       # Password encoding configuration
│   │   │   │   └── AuthenticationEntryPoint.java # Auth entry point
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java # Global exception handler
│   │   │   │   ├── BusinessException.java      # Business logic exception
│   │   │   │   ├── ValidationException.java    # Validation exception
│   │   │   │   ├── ResourceNotFoundException.java # Not found exception
│   │   │   │   ├── UnauthorizedException.java  # Authorization exception
│   │   │   │   └── InternalServerException.java # Server error exception
│   │   │   ├── validator/
│   │   │   │   ├── CustomValidators.java      # Custom validation annotations
│   │   │   │   └── ValidationUtils.java       # Validation utility methods
│   │   │   ├── util/
│   │   │   │   ├── DateUtils.java             # Date/time utilities
│   │   │   │   ├── CryptoUtils.java           # Encryption utilities
│   │   │   │   ├── FileUtils.java             # File handling utilities
│   │   │   │   ├── Constants.java             # Application constants
│   │   │   │   ├── JsonUtils.java             # JSON processing utilities
│   │   │   │   └── ValidationUtils.java       # Data validation utilities
│   │   │   └── scheduler/
│   │   │       ├── ReportScheduler.java       # Scheduled report generation
│   │   │       ├── CleanupScheduler.java      # Data cleanup tasks
│   │   │       └── NotificationScheduler.java # Scheduled notifications
│   │   └── resources/
│   │       ├── application.yml                # Main application configuration
│   │       ├── application-dev.yml            # Development configuration
│   │       ├── application-test.yml           # Test configuration
│   │       ├── application-prod.yml           # Production configuration
│   │       ├── banner.txt                     # Application startup banner
│   │       ├── logback-spring.xml            # Logging configuration
│   │       ├── db/
│   │       │   └── migration/                # Flyway database migrations
│   │       │       ├── V1__Create_users_table.sql
│   │       │       ├── V2__Create_roles_table.sql
│   │       │       ├── V3__Create_accounts_table.sql
│   │       │       ├── V4__Create_transactions_table.sql
│   │       │       ├── V5__Create_reports_table.sql
│   │       │       ├── V6__Create_audit_logs_table.sql
│   │       │       ├── V7__Create_files_table.sql
│   │       │       ├── V8__Create_notifications_table.sql
│   │       │       └── V9__Insert_initial_data.sql
│   │       ├── static/                       # Static web resources
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── images/
│   │       └── templates/                    # Email and report templates
│   │           ├── email/
│   │           │   ├── welcome.html
│   │           │   ├── password-reset.html
│   │           │   └── notification.html
│   │           └── reports/
│   │               ├── transaction-report.jrxml
│   │               └── account-summary.jrxml
│   └── test/
│       └── java/com/financehub/
│           ├── controller/                   # Controller integration tests
│           │   ├── AuthControllerTest.java
│           │   ├── TransactionControllerTest.java
│           │   ├── AccountControllerTest.java
│           │   └── ReportControllerTest.java
│           ├── service/                      # Service unit tests
│           │   ├── AuthServiceTest.java
│           │   ├── TransactionServiceTest.java
│           │   ├── AccountServiceTest.java
│           │   └── ReportServiceTest.java
│           ├── repository/                   # Repository tests
│           │   ├── TransactionRepositoryTest.java
│           │   ├── AccountRepositoryTest.java
│           │   └── UserRepositoryTest.java
│           ├── security/                     # Security tests
│           │   ├── JwtTokenProviderTest.java
│           │   └── SecurityConfigTest.java
│           ├── util/                         # Utility tests
│           │   ├── CryptoUtilsTest.java
│           │   └── ValidationUtilsTest.java
│           └── integration/                  # Integration tests
│               ├── AuthIntegrationTest.java
│               ├── TransactionIntegrationTest.java
│               └── ReportIntegrationTest.java
│
├── frontend/                                # React frontend SPA
│   ├── package.json                         # Frontend dependencies
│   ├── public/
│   │   ├── index.html                      # HTML template
│   │   └── favicon.ico                     # App icon
│   └── src/
│       ├── components/
│       │   ├── auth/
│       │   │   ├── LoginForm.jsx           # Login component
│       │   │   └── ProtectedRoute.jsx      # Route protection
│       │   ├── dashboard/
│       │   │   ├── Dashboard.jsx           # Main dashboard
│       │   │   └── FinancialChart.jsx      # Charts component
│       │   ├── transactions/
│       │   │   ├── TransactionList.jsx     # Transaction listing
│       │   │   ├── TransactionForm.jsx     # Transaction form
│       │   │   └── TransactionDetail.jsx   # Transaction details
│       │   ├── reports/
│       │   │   ├── ReportBuilder.jsx       # Report configuration
│       │   │   └── ReportViewer.jsx        # Report display
│       │   └── admin/
│       │       ├── UserManagement.jsx      # User admin panel
│       │       └── SystemSettings.jsx      # System configuration
│       ├── services/
│       │   ├── api.js                      # API client
│       │   ├── auth.js                     # Authentication service
│       │   └── transactions.js             # Transaction service
│       └── types/
│           └── index.ts                    # TypeScript type definitions
│
├── docker/                                 # Docker configuration files
│   ├── Dockerfile                          # Application container
│   ├── postgres.Dockerfile                 # Database container
│   └── nginx.conf                          # Reverse proxy configuration
│
├── docs/                                   # Project documentation
│   ├── API.md                              # API documentation
│   ├── DEPLOYMENT.md                       # Deployment guide
│   ├── ARCHITECTURE.md                     # System architecture
│   └── SECURITY.md                         # Security considerations
│
├── scripts/                                # Build and deployment scripts
│   ├── build.sh                            # Build script
│   ├── test.sh                             # Test execution script
│   ├── deploy.sh                           # Deployment script
│   └── db-setup.sh                         # Database setup script
│
└── .github/                                # CI/CD workflows
    └── workflows/
        ├── maven.yml                       # Maven build workflow
        ├── security-scan.yml               # Security scanning
        └── deploy.yml                      # Deployment workflow
```

### Branch: testbed-ml-python (InsightAI) - Complete File Tree

```
insightai/
├── README.md                               # Project overview and setup guide
├── pyproject.toml                          # Python project configuration (Poetry/pip)
├── requirements/                           # Dependency management
│   ├── base.txt                           # Core dependencies
│   ├── dev.txt                            # Development dependencies
│   ├── prod.txt                           # Production dependencies
│   └── test.txt                           # Testing dependencies
├── .python-version                         # Python version specification
├── .gitignore                             # Python/ML specific ignore patterns
├── docker-compose.yml                      # Multi-service container setup
├── Dockerfile                             # FastAPI application container
├── .env.example                           # Environment variables template
├── Makefile                               # Common commands automation
├── pytest.ini                            # Pytest configuration
├── .flake8                                # Code style configuration
├── mypy.ini                               # Type checking configuration
│
├── app/
│   ├── __init__.py                        # Package initialization
│   ├── main.py                            # FastAPI application entry point
│   ├── core/
│   │   ├── __init__.py
│   │   ├── config.py                      # Application configuration
│   │   ├── security.py                    # Authentication and authorization
│   │   ├── database.py                    # Database connection and models
│   │   ├── exceptions.py                  # Custom exception classes
│   │   ├── dependencies.py                # FastAPI dependency injection
│   │   ├── middleware.py                  # Custom middleware
│   │   └── logging.py                     # Logging configuration
│   ├── api/
│   │   ├── __init__.py
│   │   ├── deps.py                        # API dependencies
│   │   └── v1/
│   │       ├── __init__.py
│   │       ├── router.py                  # Main API router
│   │       ├── auth.py                    # Authentication endpoints
│   │       ├── users.py                   # User management endpoints
│   │       ├── models.py                  # ML model endpoints
│   │       ├── datasets.py                # Dataset management endpoints
│   │       ├── training.py                # Model training endpoints
│   │       ├── predictions.py             # Prediction endpoints
│   │       ├── monitoring.py              # Model monitoring endpoints
│   │       ├── files.py                   # File upload/management endpoints
│   │       └── admin.py                   # Admin endpoints
│   ├── models/
│   │   ├── __init__.py
│   │   ├── database/                      # Database models
│   │   │   ├── __init__.py
│   │   │   ├── base.py                    # Base model class
│   │   │   ├── user.py                    # User model
│   │   │   ├── model_registry.py          # ML model registry
│   │   │   ├── dataset.py                 # Dataset metadata model
│   │   │   ├── experiment.py              # Experiment tracking model
│   │   │   ├── prediction.py              # Prediction result model
│   │   │   └── audit_log.py               # Audit logging model
│   │   └── ml/                            # ML model definitions
│   │       ├── __init__.py
│   │       ├── base.py                    # Base ML model interface
│   │       ├── classifiers.py             # Classification models
│   │       ├── regressors.py              # Regression models
│   │       ├── transformers.py            # Text/NLP models
│   │       ├── generators.py              # Generative models
│   │       └── ensemble.py                # Ensemble methods
│   ├── services/
│   │   ├── __init__.py
│   │   ├── auth_service.py                # Authentication service
│   │   ├── user_service.py                # User management service
│   │   ├── model_service.py               # ML model management
│   │   ├── training_service.py            # Model training orchestration
│   │   ├── inference_service.py           # Model inference service
│   │   ├── dataset_service.py             # Dataset management service
│   │   ├── monitoring_service.py          # Model monitoring service
│   │   ├── file_service.py                # File handling service
│   │   ├── notification_service.py        # Notification service
│   │   └── experiment_service.py          # Experiment tracking service
│   ├── ml/
│   │   ├── __init__.py
│   │   ├── training/
│   │   │   ├── __init__.py
│   │   │   ├── trainer.py                 # Model training orchestrator
│   │   │   ├── data_loader.py             # Data loading and preprocessing
│   │   │   ├── validators.py              # Training data validation
│   │   │   ├── optimizers.py              # Training optimization
│   │   │   ├── callbacks.py               # Training callbacks
│   │   │   └── schedulers.py              # Learning rate scheduling
│   │   ├── inference/
│   │   │   ├── __init__.py
│   │   │   ├── predictor.py               # Model prediction interface
│   │   │   ├── preprocessor.py            # Data preprocessing for inference
│   │   │   ├── postprocessor.py           # Prediction post-processing
│   │   │   ├── batch_predictor.py         # Batch prediction handling
│   │   │   └── streaming_predictor.py     # Real-time prediction
│   │   ├── monitoring/
│   │   │   ├── __init__.py
│   │   │   ├── drift_detector.py          # Data drift detection
│   │   │   ├── performance_monitor.py     # Model performance monitoring
│   │   │   ├── explainability.py          # Model explainability tools
│   │   │   ├── bias_detector.py           # Bias detection in predictions
│   │   │   └── alerting.py                # Monitoring alerts
│   │   ├── agents/
│   │   │   ├── __init__.py
│   │   │   ├── base_agent.py              # Base AI agent framework
│   │   │   ├── data_agent.py              # Data analysis agent
│   │   │   ├── code_agent.py              # Code generation agent
│   │   │   ├── chat_agent.py              # Conversational agent
│   │   │   ├── tool_agent.py              # Tool-using agent
│   │   │   ├── plugin_manager.py          # Agent plugin system
│   │   │   └── workflow_engine.py         # Agent workflow orchestration
│   │   ├── pipelines/
│   │   │   ├── __init__.py
│   │   │   ├── feature_engineering.py     # Feature engineering pipeline
│   │   │   ├── data_validation.py         # Data quality validation
│   │   │   ├── model_pipeline.py          # End-to-end ML pipeline
│   │   │   └── deployment_pipeline.py     # Model deployment pipeline
│   │   └── utils/
│   │       ├── __init__.py
│   │       ├── data_validation.py         # Data validation utilities
│   │       ├── feature_engineering.py     # Feature engineering tools
│   │       ├── model_utils.py             # Model utility functions
│   │       ├── metrics.py                 # Custom metrics
│   │       ├── visualization.py           # Data/model visualization
│   │       └── serialization.py          # Model serialization utilities
│   ├── schemas/
│   │   ├── __init__.py
│   │   ├── auth.py                        # Authentication schemas
│   │   ├── user.py                        # User schemas
│   │   ├── model.py                       # ML model schemas
│   │   ├── dataset.py                     # Dataset schemas
│   │   ├── training.py                    # Training request/response schemas
│   │   ├── prediction.py                  # Prediction schemas
│   │   ├── monitoring.py                  # Monitoring schemas
│   │   └── common.py                      # Common schema definitions
│   ├── utils/
│   │   ├── __init__.py
│   │   ├── logging.py                     # Logging utilities
│   │   ├── validation.py                  # Input validation utilities
│   │   ├── security.py                    # Security utilities
│   │   ├── file_utils.py                  # File handling utilities
│   │   ├── date_utils.py                  # Date/time utilities
│   │   ├── encryption.py                  # Encryption/decryption utilities
│   │   └── constants.py                   # Application constants
│   └── workers/
│       ├── __init__.py
│       ├── training_worker.py             # Background training worker
│       ├── inference_worker.py            # Background inference worker
│       ├── monitoring_worker.py           # Monitoring tasks worker
│       └── cleanup_worker.py              # Data cleanup worker
│
├── frontend/                              # Streamlit dashboard
│   ├── main.py                            # Main Streamlit application
│   ├── config.py                          # Frontend configuration
│   ├── pages/
│   │   ├── 1_Data_Upload.py               # Data upload interface
│   │   ├── 2_Model_Training.py            # Model training interface
│   │   ├── 3_Predictions.py               # Prediction interface
│   │   ├── 4_Model_Monitoring.py          # Monitoring dashboard
│   │   ├── 5_Experiment_Tracking.py       # Experiment tracking
│   │   └── 6_Admin_Panel.py               # Admin interface
│   ├── components/
│   │   ├── __init__.py
│   │   ├── charts.py                      # Chart components
│   │   ├── forms.py                       # Form components
│   │   ├── tables.py                      # Table components
│   │   ├── file_upload.py                 # File upload component
│   │   └── model_viewer.py                # Model visualization component
│   ├── utils/
│   │   ├── __init__.py
│   │   ├── api_client.py                  # Backend API client
│   │   ├── auth.py                        # Authentication helpers
│   │   ├── visualization.py               # Visualization utilities
│   │   └── formatting.py                  # Data formatting utilities
│   └── static/
│       ├── css/
│       │   └── custom.css                 # Custom styles
│       └── images/
│           └── logo.png                   # Application logo
│
├── data/                                  # Data storage directory
│   ├── raw/                               # Raw input data
│   ├── processed/                         # Processed datasets
│   ├── models/                            # Trained model files
│   │   ├── production/                    # Production models
│   │   ├── staging/                       # Staging models
│   │   └── experiments/                   # Experimental models
│   ├── datasets/                          # Managed datasets
│   │   ├── training/                      # Training datasets
│   │   ├── validation/                    # Validation datasets
│   │   └── test/                          # Test datasets
│   └── uploads/                           # User uploaded files
│
├── notebooks/                             # Jupyter notebooks for analysis
│   ├── 01_data_exploration.ipynb          # Data exploration notebook
│   ├── 02_feature_engineering.ipynb       # Feature engineering notebook
│   ├── 03_model_development.ipynb         # Model development notebook
│   ├── 04_model_evaluation.ipynb          # Model evaluation notebook
│   └── 05_deployment_analysis.ipynb       # Deployment analysis notebook
│
├── tests/                                 # Test suite
│   ├── __init__.py
│   ├── conftest.py                        # Pytest configuration and fixtures
│   ├── test_auth.py                       # Authentication tests
│   ├── test_users.py                      # User management tests
│   ├── test_models.py                     # Model management tests
│   ├── test_training.py                   # Training pipeline tests
│   ├── test_inference.py                  # Inference tests
│   ├── test_monitoring.py                 # Monitoring tests
│   ├── test_agents.py                     # AI agent tests
│   ├── unit/                              # Unit tests
│   │   ├── test_services.py               # Service layer tests
│   │   ├── test_ml_utils.py               # ML utility tests
│   │   └── test_validators.py             # Validation tests
│   ├── integration/                       # Integration tests
│   │   ├── test_api_endpoints.py          # API endpoint tests
│   │   ├── test_ml_pipeline.py            # ML pipeline tests
│   │   └── test_database.py               # Database tests
│   └── performance/                       # Performance tests
│       ├── test_inference_latency.py      # Inference performance tests
│       └── test_training_performance.py   # Training performance tests
│
├── scripts/                               # Utility and deployment scripts
│   ├── setup.py                           # Environment setup script
│   ├── train_initial_models.py            # Initial model training
│   ├── deploy.py                          # Deployment script
│   ├── backup.py                          # Data backup script
│   ├── migrate.py                         # Database migration script
│   ├── health_check.py                    # System health check
│   └── cleanup.py                         # Data cleanup script
│
├── config/                                # Configuration files
│   ├── development.yml                    # Development environment config
│   ├── testing.yml                        # Testing environment config
│   ├── production.yml                     # Production environment config
│   └── logging.yml                        # Logging configuration
│
├── docker/                                # Docker configuration
│   ├── fastapi.Dockerfile                 # FastAPI service container
│   ├── streamlit.Dockerfile               # Streamlit dashboard container
│   ├── worker.Dockerfile                  # Background worker container
│   └── nginx.conf                         # Load balancer configuration
│
├── docs/                                  # Project documentation
│   ├── API.md                             # API documentation
│   ├── ML_MODELS.md                       # ML model documentation
│   ├── DEPLOYMENT.md                      # Deployment guide
│   ├── DATA_SCHEMA.md                     # Data schema documentation
│   └── AI_AGENTS.md                       # AI agent documentation
│
├── monitoring/                            # Monitoring and observability
│   ├── prometheus.yml                     # Prometheus configuration
│   ├── grafana/
│   │   └── dashboards/                    # Grafana dashboards
│   │       ├── system_metrics.json
│   │       └── ml_metrics.json
│   └── alerts/                            # Alert configurations
│       └── model_alerts.yml
│
└── .github/                               # CI/CD workflows
    └── workflows/
        ├── ci.yml                         # Continuous integration
        ├── ml_pipeline.yml                # ML pipeline automation
        ├── security_scan.yml              # Security scanning
        └── deploy.yml                     # Deployment automation
```

## Implementation Phases

### Phase 1: Clean Application Development

#### Task 1.1: Repository Setup
- [ ] Create repository with branch structure
- [ ] Initialize each testbed branch with complete file structure
- [ ] Set up branch protection rules and documentation

#### Task 1.2: Build TaskFlow (Clean Implementation)
**Core Features:**
1. User authentication with JWT and OAuth2
2. Task CRUD operations with proper authorization
3. Project management with team collaboration
4. File upload/download with security validation
5. Real-time updates via WebSocket
6. AI assistant integration (clean implementation)

**Security Best Practices:**
- Parameterized SQL queries
- Input validation and sanitization
- HTTPS enforcement with security headers
- Rate limiting on sensitive endpoints
- Secure password hashing (bcrypt)
- CSRF protection
- Proper error handling without information leakage

#### Task 1.3: Build FinanceHub (Clean Implementation)  
**Core Features:**
1. Multi-tenant enterprise authentication (OAuth2 + SAML)
2. Financial transaction processing with ACID compliance
3. Account management with proper authorization
4. Report generation (PDF/Excel) with templating
5. Admin dashboard with granular permissions
6. ML fraud detection integration
7. Comprehensive audit logging

**Security Best Practices:**
- Spring Security best practices
- JPA with parameterized queries
- Proper exception handling
- Bean Validation for input validation
- Actuator endpoints secured properly
- Database migrations with Flyway
- Comprehensive logging without sensitive data

#### Task 1.4: Build InsightAI (Clean Implementation)
**Core Features:**
1. FastAPI-based ML serving with proper authentication
2. Model training pipeline with MLflow integration
3. Secure model management and versioning
4. Data upload with validation and preprocessing
5. Real-time inference with monitoring
6. AI agent system with controlled permissions
7. Streamlit dashboard for visualization

**Security Best Practices:**
- OAuth2 authentication with proper scopes
- Input validation for ML data and model inputs
- Secure file handling for datasets and models
- Rate limiting on inference endpoints
- Model versioning with rollback capabilities
- Proper error handling without model leakage
- Data validation and sanitization throughout pipeline

### Phase 2: Testing and Validation of Clean Applications
- [ ] Comprehensive testing (unit, integration, security)
- [ ] Performance benchmarking under normal load
- [ ] Security baseline establishment with existing scanners
- [ ] Documentation validation and completeness review

### Phase 3: Comprehensive Vulnerability Catalog Creation

#### Task 3.1: Complete Vulnerability Specification
**BEFORE implementing any vulnerabilities, create complete catalog:**

- [ ] **web-app-vulnerabilities.json**: All 38 vulnerabilities with exact file paths and line numbers
- [ ] **java-vulnerabilities.json**: All 37 vulnerabilities with precise locations
- [ ] **python-vulnerabilities.json**: All 40 vulnerabilities with complete metadata
- [ ] **vulnerability-schema.json**: JSON schema validation for consistency
- [ ] Cross-reference all CWE mappings and severity ratings
- [ ] Validate detection difficulty classifications
- [ ] Document expected scanning tool capabilities

#### Task 3.2: Vulnerability Location Planning
For each of the 115 vulnerabilities:
- [ ] Specify exact file path from repository root
- [ ] Plan exact line number ranges where vulnerability will be placed
- [ ] Write complete vulnerable code snippets
- [ ] Define detection difficulty (easy/medium/hard) and expected tools
- [ ] Document business impact and remediation approaches
- [ ] Plan integration with existing application functionality

### Phase 4: Strategic Vulnerability Implementation

#### Task 4.1: Systematic Vulnerability Injection
**Process for each vulnerability:**
1. **Pre-injection Verification**: Confirm clean code functionality
2. **Code Modification**: Implement exact vulnerable code as specified in catalog
3. **Location Verification**: Confirm actual line numbers match catalog entries
4. **Functionality Testing**: Ensure application remains functional
5. **Vulnerability Verification**: Manual testing to confirm vulnerability exists
6. **Catalog Update**: Record any line number adjustments or changes

#### Task 4.2: Quality Assurance and Validation
- [ ] Manual security testing of all 115 vulnerabilities
- [ ] Functional testing of all application features post-injection
- [ ] Line number accuracy verification across all catalogs
- [ ] Detection difficulty validation with sample scanning
- [ ] Cross-testbed consistency verification

### Phase 5: Documentation and Finalization

#### Task 5.1: Comprehensive Documentation
- [ ] **README.md**: Complete project overview and quick start guide
- [ ] **vulnerability-methodology.md**: Detailed categorization and methodology
- [ ] **testing-guide.md**: Step-by-step instructions for using benchmark
- [ ] **implementation-notes.md**: Technical implementation details and decisions

#### Task 5.2: Final Validation and Packaging
- [ ] End-to-end testing across all three testbeds
- [ ] Vulnerability catalog validation and consistency checking
- [ ] Docker containerization for consistent environments
- [ ] Performance and scalability testing
- [ ] Documentation completeness review

## Critical Success Requirements

### Vulnerability Tracking Precision
- **100% Accuracy**: Every vulnerability location precisely documented with exact line numbers
- **Complete Metadata**: All required fields populated for every vulnerability entry
- **Manual Verification**: Each vulnerability confirmed to actually exist through testing
- **Consistency**: Uniform formatting and validation across all vulnerability entries
- **Traceability**: Clear mapping from vulnerability catalog to actual code implementation

### Application Quality Standards
- **Enterprise Realism**: Applications must feel like authentic enterprise software
- **Maintained Functionality**: All features work correctly even with vulnerabilities present
- **Clean Architecture**: Well-structured code with clear separation of concerns
- **Comprehensive Testing**: Full test coverage for clean functionality
- **Professional Documentation**: Clear implementation notes and architecture decisions

### Benchmark Usability
- **Clear Instructions**: Complete step-by-step usage guide
- **Reproducible Results**: Consistent testing across different environments and tools
- **Scalable Design**: Easy to extend with additional vulnerabilities or testbeds
- **Tool Compatibility**: Support for various scanner input/output formats
- **Evaluation Ready**: Structured for automated comparison and reporting

## Timeline and Deliverables

### Phase 1: Clean Applications (4-6 weeks)
**Deliverables:**
- 3 fully functional, secure enterprise applications
- Complete test suites with high coverage
- Docker containerization for consistent environments
- Comprehensive documentation for each application

### Phase 2: Clean Application Validation (1-2 weeks)
**Deliverables:**
- Security baseline reports from existing scanners
- Performance benchmarks under normal load
- Functional test validation across all features
- Clean code security assessment

### Phase 3: Vulnerability Catalog Creation (2-3 weeks)
**Deliverables:**
- 3 comprehensive JSON vulnerability catalogs (115 total vulnerabilities)
- Complete vulnerability specifications with exact locations
- JSON schema validation for consistency
- CWE mapping and severity classification validation

### Phase 4: Vulnerability Implementation (3-4 weeks)
**Deliverables:**
- 115 strategically placed, manually verified vulnerabilities
- Maintained application functionality across all testbeds
- Updated vulnerability catalogs with precise line numbers
- Comprehensive vulnerability verification documentation

### Phase 5: Documentation and Finalization (2-3 weeks)
**Deliverables:**
- Complete usage documentation and methodology guide
- Validation reports for all vulnerabilities
- Final testing and quality assurance results
- Ready-to-use benchmark suite with comprehensive documentation

**Total Estimated Timeline**: 12-18 weeks

## Final Success Metrics

1. **115 Total Vulnerabilities** precisely documented and verified across 3 testbeds
2. **100% Location Accuracy** with exact file paths and line numbers
3. **3 Enterprise-Grade Applications** that remain fully functional
4. **Comprehensive Documentation** enabling easy benchmark usage
5. **Manual Verification** of every single vulnerability's existence
6. **Realistic Integration** of vulnerabilities into authentic business functionality

This benchmark suite will provide the most comprehensive, precisely documented, and realistically implemented security scanning test environment available, enabling accurate evaluation of security tools across traditional, enterprise, and cutting-edge ML/AI vulnerability categories.