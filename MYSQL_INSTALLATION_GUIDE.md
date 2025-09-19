# MySQL Installation and Setup Guide

## Option 1: Download and Install MySQL Community Server

1. **Download MySQL:**
   - Go to: https://dev.mysql.com/downloads/mysql/
   - Select "MySQL Community Server"
   - Choose "Windows (x86, 64-bit), MSI Installer"
   - Download the larger file (mysql-installer-community-X.X.XX.X.msi)

2. **Install MySQL:**
   - Run the downloaded MSI installer
   - Choose "Server only" or "Developer Default" setup type
   - Set root password to: `engrashgates123`
   - Choose "Use Strong Password Encryption"
   - Complete the installation

3. **Start MySQL Service:**
   - Open Services (services.msc)
   - Find "MySQL80" service
   - Right-click and select "Start"

## Option 2: Using XAMPP (Easier)

1. **Download XAMPP:**
   - Go to: https://www.apachefriends.org/download.html
   - Download XAMPP for Windows

2. **Install XAMPP:**
   - Run the installer
   - Select Apache, MySQL, and phpMyAdmin components
   - Install to default location (C:\xampp)

3. **Start MySQL:**
   - Open XAMPP Control Panel
   - Click "Start" next to MySQL
   - MySQL will run on port 3306

4. **Set MySQL Password:**
   - Click "Admin" next to MySQL (opens phpMyAdmin)
   - Go to "User accounts" tab
   - Edit root@localhost user
   - Set password to: `engrashgates123`

## Create Database

1. **Using Command Line:**
   ```cmd
   # Navigate to MySQL bin directory
   cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"
   # OR for XAMPP:
   cd "C:\xampp\mysql\bin"
   
   # Connect to MySQL
   mysql -u root -p
   # Enter password: engrashgates123
   
   # Run the SQL commands from mysql_setup.sql
   source C:\Users\User\Desktop\QA Finale\QA_project\QA_project\mysql_setup.sql
   ```

2. **Using phpMyAdmin (if using XAMPP):**
   - Open http://localhost/phpmyadmin
   - Click "Import" tab
   - Choose the mysql_setup.sql file
   - Click "Go"

## Test Connection

Once MySQL is installed and running:
1. Navigate to your backend directory
2. Run: `mvn spring-boot:run`
3. The application should start successfully and connect to the database

## Troubleshooting

- **Port 3306 in use:** Stop any other MySQL instances
- **Access denied:** Double-check password is `engrashgates123`
- **Connection refused:** Make sure MySQL service is running
- **Database not found:** Run the mysql_setup.sql script