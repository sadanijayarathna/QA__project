-- MySQL Database Setup Script for Task Manager Application

-- Step 1: Create the database
CREATE DATABASE IF NOT EXISTS taskmanager_db;

-- Step 2: Use the database
USE taskmanager_db;

-- Step 3: Create a dedicated user (optional, you can use root)
CREATE USER IF NOT EXISTS 'taskmanager_user'@'localhost' IDENTIFIED BY 'engrashgates123';
GRANT ALL PRIVILEGES ON taskmanager_db.* TO 'taskmanager_user'@'localhost';

-- Step 4: Also grant privileges to root user with your password
ALTER USER 'root'@'localhost' IDENTIFIED BY 'engrashgates123';
FLUSH PRIVILEGES;

-- Note: Tables will be automatically created by Spring Boot JPA when you start the application
-- due to the setting: spring.jpa.hibernate.ddl-auto=update

-- If you want to see what tables will be created, they are:
-- 1. users table (id, username, email, password, created_at, updated_at)
-- 2. tasks table (id, title, description, status, priority, due_date, created_at, updated_at, user_id)