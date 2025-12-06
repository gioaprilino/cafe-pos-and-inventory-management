# 🔐 JWT Authentication Implementation Guide

## ✅ Implementation Completed

JWT (JSON Web Token) authentication telah berhasil diimplementasikan pada TerraCafe Backend API!

---

## 📋 What Was Implemented

### 1. **Dependencies Added** (`pom.xml`)
```xml
<!-- JWT Dependencies -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
```

### 2. **New Classes Created**

#### `JwtUtil.java` - JWT Token Utility
- Generate JWT token dengan username, role, dan userId
- Validate JWT token
- Extract claims dari token (username, role, userId, expiration)
- Secret key: Configurable via `application.properties`
- Token validity: 24 hours (configurable)

#### `CustomUserDetailsService.java` - User Authentication
- Load user dari database by username
- Convert role ke Spring Security GrantedAuthority
- Menambahkan prefix `ROLE_` otomatis

#### `JwtAuthenticationFilter.java` - JWT Filter
- Intercept setiap HTTP request
- Extract JWT token dari Authorization header
- Validate token dan set authentication ke SecurityContext
- Format: `Authorization: Bearer <token>`

### 3. **Updated Classes**

#### `SecurityConfig.java`
- Enable JWT authentication filter
- Configure authentication provider dengan CustomUserDetailsService
- Add AuthenticationManager bean

#### `UserController.java`
- Login endpoint sekarang return JWT token
- Token included dalam LoginResponse

#### `UserService.java`
- Method `generateJwtToken()` untuk generate token saat login

#### `LoginResponse.java`
- Added `token` field
- Added `tokenExpiry` field

---

## 🚀 How to Use JWT Authentication

### Step 1: Login to Get JWT Token

**Endpoint:** `POST /api/users/login`

**Request:**
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Response (Success):**
```json
{
  "userId": 1,
  "username": "admin",
  "roleId": "1",
  "roleName": "MANAGER",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiTUFOQUdFUiIsInVzZXJJZCI6MSwic3ViIjoiYWRtaW4iLCJpYXQiOjE3MzM0MjA0MDAsImV4cCI6MTczMzUwNjgwMH0.abc123def456...",
  "message": "Login successful",
  "loginTime": "2025-12-05T00:40:00",
  "tokenExpiry": "2025-12-06T00:40:00"
}
```

**Important:** Save the `token` value! You'll need it for all subsequent requests.

---

### Step 2: Use Token in Subsequent Requests

For all protected endpoints, include the JWT token in the `Authorization` header:

**Format:**
```
Authorization: Bearer <your-jwt-token>
```

**Example using cURL:**
```bash
curl -X GET http://localhost:8080/api/ingredients \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiTUFOQUdFUiIsInVzZXJJZCI6MSwic3ViIjoiYWRtaW4iLCJpYXQiOjE3MzM0MjA0MDAsImV4cCI6MTczMzUwNjgwMH0.abc123def456..."
```

**Example using JavaScript (Fetch API):**
```javascript
const token = "eyJhbGciOiJIUzI1NiJ9..."; // Token dari login

fetch('http://localhost:8080/api/ingredients', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log(data));
```

**Example using Postman:**
1. Open Postman
2. Create new request
3. Go to **Authorization** tab
4. Select **Type:** Bearer Token
5. Paste your token in **Token** field
6. Send request

---

## 🔒 Token Details

### Token Structure
JWT token terdiri dari 3 bagian (dipisahkan dengan `.`):
```
HEADER.PAYLOAD.SIGNATURE
```

### Payload Contains:
- `sub` (subject): Username
- `role`: User role (MANAGER, CASHIER, KITCHEN)
- `userId`: User ID
- `iat` (issued at): Token creation timestamp
- `exp` (expiration): Token expiration timestamp (24 hours from creation)

### Token Security:
- Signed dengan HMAC SHA-256
- Secret key stored in application (configurable)
- Cannot be tampered with without secret key
- Stateless (no server-side storage needed)

---

## ⚙️ Configuration

### Application Properties
Tambahkan di `application.properties`:

```properties
# JWT Configuration
jwt.secret=TerraC@f3Sup3rS3cr3tK3yF0rJWTToken2025MustBeLongEnoughForHS256
jwt.expiration=86400000
```

**Notes:**
- `jwt.secret`: Secret key untuk signing token (WAJIB minimal 256-bit/32 characters untuk HS256)
- `jwt.expiration`: Token validity dalam milliseconds (86400000 = 24 hours)
- ⚠️ **PRODUCTION:** Simpan secret key di environment variables, JANGAN di source code!

---

## 🛡️ Security Features

### ✅ What's Secured:
- All protected endpoints require valid JWT token
- Token expires after 24 hours
- Role-based access control via `@PreAuthorize`
- Token signature verification
- Protection against token tampering

### ✅ Public Endpoints (No Token Required):
- `POST /api/users/login` - Login
- `GET /api/products/**` - View products
- `GET /api/categories/**` - View categories

### ✅ Protected Endpoints (Token Required):
All other endpoints require authentication + appropriate role

---

## 🧪 Testing JWT Authentication

### Test 1: Login and Get Token
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

### Test 2: Access Protected Endpoint WITH Token
```bash
# Replace YOUR_TOKEN with actual token from login
curl -X GET http://localhost:8080/api/ingredients \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Expected:** 200 OK with data

### Test 3: Access Protected Endpoint WITHOUT Token
```bash
curl -X GET http://localhost:8080/api/ingredients
```

**Expected:** 403 Forbidden

### Test 4: Access with Invalid Token
```bash
curl -X GET http://localhost:8080/api/ingredients \
  -H "Authorization: Bearer invalid_token_here"
```

**Expected:** 403 Forbidden

---

## 🚨 Error Responses

### 401 Unauthorized - Invalid Credentials
```json
{
  "userId": null,
  "username": null,
  "roleId": null,
  "roleName": null,
  "token": null,
  "message": "Invalid credentials"
}
```

### 403 Forbidden - No Token or Invalid Token
```json
{
  "timestamp": "2025-12-05T00:40:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/ingredients"
}
```

### 403 Forbidden - Insufficient Permissions
User has valid token but wrong role for the endpoint
```json
{
  "timestamp": "2025-12-05T00:40:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/users"
}
```

---

## 📱 Frontend Integration Guide

### 1. Store Token After Login
```javascript
async function login(username, password) {
  const response = await fetch('http://localhost:8080/api/users/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  
  const data = await response.json();
  
  if (response.ok) {
    // Store token in localStorage or sessionStorage
    localStorage.setItem('jwt_token', data.token);
    localStorage.setItem('user_role', data.roleName);
    localStorage.setItem('user_id', data.userId);
    return data;
  } else {
    throw new Error(data.message);
  }
}
```

### 2. Create API Helper Function
```javascript
async function apiRequest(endpoint, options = {}) {
  const token = localStorage.getItem('jwt_token');
  
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    }
  };
  
  const mergedOptions = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...options.headers
    }
  };
  
  const response = await fetch(`http://localhost:8080${endpoint}`, mergedOptions);
  
  // Handle token expiration
  if (response.status === 403 || response.status === 401) {
    // Token expired or invalid, redirect to login
    localStorage.removeItem('jwt_token');
    window.location.href = '/login';
    throw new Error('Authentication failed');
  }
  
  return response.json();
}
```

### 3. Use Helper in Your App
```javascript
// Example: Get ingredients
async function getIngredients() {
  try {
    const data = await apiRequest('/api/ingredients', { method: 'GET' });
    console.log('Ingredients:', data);
    return data;
  } catch (error) {
    console.error('Failed to get ingredients:', error);
  }
}

// Example: Create transaction
async function createTransaction(items, cashierId) {
  try {
    const data = await apiRequest('/api/transactions?cashierId=' + cashierId, {
      method: 'POST',
      body: JSON.stringify(items)
    });
    console.log('Transaction created:', data);
    return data;
  } catch (error) {
    console.error('Failed to create transaction:', error);
  }
}
```

### 4. Logout Function
```javascript
function logout() {
  localStorage.removeItem('jwt_token');
  localStorage.removeItem('user_role');
  localStorage.removeItem('user_id');
  window.location.href = '/login';
}
```

---

## 🔄 Token Refresh (Future Enhancement)

Saat ini token berlaku 24 jam. Untuk production, pertimbangkan implementasi:
- **Refresh Token**: Token dengan expiry lebih lama (7-30 hari)
- **Access Token**: Token dengan expiry pendek (15-60 menit)
- **Refresh Endpoint**: `/api/users/refresh` untuk get new access token

---

## ✅ Production Checklist

Before deploying to production:

- [ ] Change JWT secret key (store in environment variable)
- [ ] Consider shorter token expiry (1-2 hours)
- [ ] Implement refresh token mechanism
- [ ] Add token blacklist for logout
- [ ] Enable HTTPS only
- [ ] Add rate limiting on login endpoint
- [ ] Implement account lockout after failed attempts
- [ ] Add logging for authentication events
- [ ] Monitor for suspicious token usage
- [ ] Regular security audits

---

## 📚 Resources

- **JJWT Library:** https://github.com/jwtk/jjwt
- **JWT Specification:** https://tools.ietf.org/html/rfc7519
- **JWT Debugger:** https://jwt.io
- **Spring Security:** https://docs.spring.io/spring-security/reference/

---

## 🎯 Summary

✅ JWT authentication fully implemented and tested  
✅ Token-based stateless authentication  
✅ Role-based access control integrated  
✅ 24-hour token validity  
✅ Production-ready with minor enhancements  

**Your API is now secured with industry-standard JWT authentication! 🔐🚀**
