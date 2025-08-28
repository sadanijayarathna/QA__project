# Task Manager Database Setup

## Step 1: Create Database
Connect to MySQL/MariaDB and run:

```sql
CREATE DATABASE taskmanager_db;
```

## Step 2: Create User (Optional - for production)
```sql
CREATE USER 'taskmanager_user'@'localhost' IDENTIFIED BY 'taskmanager_password';
GRANT ALL PRIVILEGES ON taskmanager_db.* TO 'taskmanager_user'@'localhost';
FLUSH PRIVILEGES;
```

## Step 3: Database Configuration
The application is configured to use:
- Database: taskmanager_db
- Username: root
- Password: password
- Port: 3306

To change these settings, edit: `backend/src/main/resources/application.properties`

## Step 4: Automatic Table Creation
The application will automatically create tables when you start it for the first time due to the `spring.jpa.hibernate.ddl-auto=update` setting.

## Tables that will be created:
1. **users** - Stores user accounts
   - id (Primary Key)
   - username (Unique)
   - email (Unique)
   - password (Encrypted)
   - created_at
   - updated_at

2. **tasks** - Stores tasks
   - id (Primary Key)
   - title
   - description
   - status (PENDING, IN_PROGRESS, COMPLETED)
   - priority (LOW, MEDIUM, HIGH)
   - due_date
   - created_at
   - updated_at
   - user_id (Foreign Key to users table)
