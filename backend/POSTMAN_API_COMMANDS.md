# 📬 POSTMAN API TESTING COMMANDS
## TaskManager API Complete Testing Guide

### 🌐 **Base Configuration**
- **Base URL:** `http://localhost:8080`
- **Content-Type:** `application/json`
- **Authorization:** Bearer Token (after login)

---

## 🔐 **1. AUTHENTICATION ENDPOINTS**

### **📍 Test Server Connection**
```http
GET http://localhost:8080/api/auth/test
```
**Headers:**
```
Content-Type: application/json
```

### **🔑 User Registration (Sign Up)**
```http
POST http://localhost:8080/api/auth/signup
```
**Headers:**
```
Content-Type: application/json
```
**Body (raw JSON):**
```json
{
    "username": "testuser",
    "email": "testuser@example.com",
    "password": "password123"
}
```

### **🔓 User Login (Sign In)**
```http
POST http://localhost:8080/api/auth/signin
```
**Headers:**
```
Content-Type: application/json
```
**Body (raw JSON):**
```json
{
    "username": "testuser",
    "password": "password123"
}
```
**Expected Response:**
```json
{
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "id": 1,
    "username": "testuser",
    "email": "testuser@example.com"
}
```
**⚠️ IMPORTANT:** Copy the `token` value for use in subsequent requests!

---

## 📋 **2. TASK MANAGEMENT ENDPOINTS**
*All task endpoints require authentication - add Bearer token to Authorization header*

### **📝 Create New Task**
```http
POST http://localhost:8080/api/tasks
```
**Headers:**
```
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```
**Body (raw JSON):**
```json
{
    "title": "Complete Project Documentation",
    "description": "Write comprehensive documentation for the TaskManager API",
    "priority": "HIGH",
    "status": "TODO",
    "dueDate": "2025-09-25T10:00:00"
}
```

### **📋 Get All Tasks (Current User)**
```http
GET http://localhost:8080/api/tasks
```
**Headers:**
```
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```

### **🔍 Get Task by ID**
```http
GET http://localhost:8080/api/tasks/1
```
**Headers:**
```
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```

### **✏️ Update Task**
```http
PUT http://localhost:8080/api/tasks/1
```
**Headers:**
```
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```
**Body (raw JSON):**
```json
{
    "title": "Updated Task Title",
    "description": "Updated task description with more details",
    "priority": "MEDIUM",
    "status": "IN_PROGRESS",
    "dueDate": "2025-09-26T15:30:00"
}
```

### **🗑️ Delete Task**
```http
DELETE http://localhost:8080/api/tasks/1
```
**Headers:**
```
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```

### **🔍 Get Tasks by Status**
```http
GET http://localhost:8080/api/tasks/status/TODO
```
**Headers:**
```
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```

**Available Status Values:**
- `TODO`
- `IN_PROGRESS` 
- `COMPLETED`

---

## 🧪 **3. COMPREHENSIVE TEST SCENARIOS**

### **Test Scenario 1: Complete User Journey**
1. **Register User** → `POST /api/auth/signup`
2. **Login User** → `POST /api/auth/signin` (save JWT token)
3. **Create Task** → `POST /api/tasks` (with token)
4. **Get All Tasks** → `GET /api/tasks` (with token)
5. **Update Task** → `PUT /api/tasks/{id}` (with token)
6. **Get Task by Status** → `GET /api/tasks/status/IN_PROGRESS` (with token)
7. **Delete Task** → `DELETE /api/tasks/{id}` (with token)

### **Test Scenario 2: Error Handling**
1. **Invalid Login** → `POST /api/auth/signin` (wrong credentials)
2. **Unauthorized Access** → `GET /api/tasks` (no token)
3. **Invalid Task Data** → `POST /api/tasks` (empty title)
4. **Task Not Found** → `GET /api/tasks/999` (non-existent ID)

---

## 📊 **4. SAMPLE TEST DATA**

### **Multiple Users for Testing:**
```json
// User 1
{
    "username": "alice",
    "email": "alice@test.com",
    "password": "alice123"
}

// User 2
{
    "username": "bob",
    "email": "bob@test.com", 
    "password": "bob123"
}
```

### **Various Task Examples:**
```json
// High Priority Task
{
    "title": "Fix Critical Bug",
    "description": "Resolve the authentication issue in production",
    "priority": "HIGH",
    "status": "TODO",
    "dueDate": "2025-09-22T09:00:00"
}

// Medium Priority Task
{
    "title": "Code Review",
    "description": "Review pull requests from team members",
    "priority": "MEDIUM", 
    "status": "IN_PROGRESS",
    "dueDate": "2025-09-23T14:00:00"
}

// Low Priority Task
{
    "title": "Update Dependencies",
    "description": "Update all npm packages to latest versions",
    "priority": "LOW",
    "status": "TODO",
    "dueDate": "2025-09-30T17:00:00"
}
```

---

## 🔧 **5. POSTMAN COLLECTION SETUP**

### **Environment Variables:**
Create a Postman Environment with:
```
baseUrl = http://localhost:8080
jwtToken = (leave empty, will be set after login)
```

### **Pre-request Script for Authentication:**
Add to requests requiring authentication:
```javascript
// Add this to Pre-request Script tab
if (pm.environment.get("jwtToken")) {
    pm.request.headers.add({
        key: "Authorization", 
        value: "Bearer " + pm.environment.get("jwtToken")
    });
}
```

### **Test Script for Login:**
Add to login request's Tests tab:
```javascript
// Extract JWT token from login response
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("jwtToken", jsonData.token);
    pm.test("Login successful", function () {
        pm.expect(jsonData.token).to.not.be.null;
    });
}
```

---

## ⚠️ **IMPORTANT NOTES**

### **Before Testing:**
1. ✅ Ensure your Spring Boot server is running (`mvn spring-boot:run`)
2. ✅ Database is properly configured and running
3. ✅ Server is accessible at `http://localhost:8080`

### **Authentication Flow:**
1. 🔑 **Always register/login first** to get JWT token
2. 🎫 **Copy the JWT token** from login response
3. 🔐 **Add Bearer token** to all task-related requests
4. ⏰ **Token expires** - login again if you get 401 errors

### **Common Response Codes:**
- ✅ **200 OK** - Request successful
- ✅ **201 Created** - Resource created successfully
- ❌ **400 Bad Request** - Invalid request data
- ❌ **401 Unauthorized** - Missing or invalid JWT token
- ❌ **404 Not Found** - Resource not found
- ❌ **500 Internal Server Error** - Server error

---

## 🌊 **QUICK START COMMANDS**

Copy these exact commands into Postman:

**1. Test Connection:**
```
GET http://localhost:8080/api/auth/test
```

**2. Register User:**
```
POST http://localhost:8080/api/auth/signup
Body: {"username":"testuser","email":"test@test.com","password":"test123"}
```

**3. Login User:**
```
POST http://localhost:8080/api/auth/signin  
Body: {"username":"testuser","password":"test123"}
```

**4. Create Task (with your JWT token):**
```
POST http://localhost:8080/api/tasks
Headers: Authorization: Bearer YOUR_TOKEN_HERE
Body: {"title":"Test Task","description":"Test Description","priority":"HIGH","status":"TODO"}
```

Start with these commands and expand your testing from there! 🎯