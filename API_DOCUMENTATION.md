# TerraCafe Backend API Documentation

## Summary
**Last Updated:** December 5, 2025  
**Total Endpoints:** 43  
**Authentication:** ✅ **Spring Security with Role-Based Access Control (RBAC) Enabled**

---

## 🔐 Authentication & Authorization

### Status
✅ **Spring Security ENABLED** with role-based access control implemented.

### Available Roles
The system has three main roles with specific permissions:

1. **MANAGER** - Full system access (all CRUD operations)
2. **CASHIER** - Transaction management and menu viewing
3. **KITCHEN** - Inventory and recipe management

### Authentication Flow

#### Login
**Endpoint:** `POST /api/users/login`  
**Access:** Public (No authentication required)

**Request:**
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "username": "admin",
  "roleId": "1",
  "roleName": "MANAGER",
  "message": "Login successful"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "id": null,
  "username": null,
  "roleId": null,
  "roleName": null,
  "message": "Invalid credentials"
}
```

### Security Features
- ✅ BCrypt password hashing
- ✅ Method-level security with `@PreAuthorize`
- ✅ Stateless session management (REST API)
- ✅ CSRF protection disabled (for REST API)
- ✅ Role-based endpoint access control

---

## 📋 Role-Based Access Matrix

| Resource | GET (Read) | POST (Create) | PUT (Update) | DELETE |
|----------|-----------|--------------|--------------|--------|
| **Categories** | Public | MANAGER | MANAGER | MANAGER |
| **Products** | Public | MANAGER | MANAGER | MANAGER |
| **Ingredients** | MANAGER, KITCHEN | MANAGER | MANAGER | MANAGER |
| **Recipes** | MANAGER, KITCHEN | MANAGER | MANAGER | MANAGER |
| **Users** | MANAGER | MANAGER | MANAGER* | MANAGER |
| **Transactions** | MANAGER, CASHIER | CASHIER | MANAGER, CASHIER | N/A** |
| **Stock Movements** | MANAGER, KITCHEN | MANAGER, KITCHEN | N/A | N/A** |

*Users can update their own profile  
**Not implemented for audit trail purposes

---

## API Endpoints

### 1. Category Management (`/api/categories`)

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/categories` | Get all categories | Public |
| GET | `/api/categories/{id}` | Get category by ID | Public |
| POST | `/api/categories` | Create new category | MANAGER |
| PUT | `/api/categories/{id}` | Update category | MANAGER |
| DELETE | `/api/categories/{id}` | Delete category | MANAGER |

**Request Body (POST/PUT):**
```json
{
  "name": "Beverages",
  "description": "Hot and cold drinks"
}
```

**Response Example:**
```json
{
  "id": 1,
  "name": "Beverages",
  "description": "Hot and cold drinks",
  "createdAt": "2025-12-05T10:00:00",
  "updatedAt": "2025-12-05T10:00:00"
}
```

---

### 2. Product Management (`/api/products`)

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/products` | Get all products | Public |
| GET | `/api/products/active` | Get active products | Public |
| GET | `/api/products/{id}` | Get product by ID | Public |
| POST | `/api/products` | Create new product | MANAGER |
| PUT | `/api/products/{id}` | Update product | MANAGER |
| DELETE | `/api/products/{id}` | Delete product | MANAGER |

**Request Body (POST/PUT):**
```json
{
  "name": "Espresso",
  "description": "Strong Italian coffee",
  "price": 25000.0,
  "category": {
    "id": 1
  },
  "imageUrl": "https://example.com/espresso.jpg",
  "isActive": true
}
```

**Response Example:**
```json
{
  "id": 1,
  "name": "Espresso",
  "description": "Strong Italian coffee",
  "price": 25000.0,
  "category": {
    "id": 1,
    "name": "Beverages"
  },
  "imageUrl": "https://example.com/espresso.jpg",
  "isActive": true,
  "createdAt": "2025-12-05T10:00:00",
  "updatedAt": "2025-12-05T10:00:00"
}
```

---

### 3. Ingredient Management (`/api/ingredients`)

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/ingredients` | Get all ingredients | MANAGER, KITCHEN |
| GET | `/api/ingredients/{id}` | Get ingredient by ID | MANAGER, KITCHEN |
| GET | `/api/ingredients/{id}/stock` | Get current stock | MANAGER, KITCHEN |
| GET | `/api/ingredients/{id}/low-stock` | Check if low stock | MANAGER, KITCHEN |
| POST | `/api/ingredients` | Create new ingredient | MANAGER |
| PUT | `/api/ingredients/{id}` | Update ingredient | MANAGER |
| DELETE | `/api/ingredients/{id}` | Delete ingredient | MANAGER |

**Request Body (POST/PUT):**
```json
{
  "name": "Coffee Beans",
  "unit": "kg",
  "minimumStockThreshold": 10.0
}
```

**Response Example:**
```json
{
  "id": 1,
  "name": "Coffee Beans",
  "unit": "kg",
  "minimumStockThreshold": 10.0,
  "createdAt": "2025-12-05T10:00:00",
  "updatedAt": "2025-12-05T10:00:00"
}
```

---

### 4. Recipe Management (`/api/recipes`)

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/recipes` | Get all recipes | MANAGER, KITCHEN |
| GET | `/api/recipes/{id}` | Get recipe by ID | MANAGER, KITCHEN |
| GET | `/api/recipes/product/{productId}` | Get recipes by product | MANAGER, KITCHEN |
| GET | `/api/recipes/ingredient/{ingredientId}` | Get recipes by ingredient | MANAGER, KITCHEN |
| POST | `/api/recipes` | Create new recipe | MANAGER |
| PUT | `/api/recipes/{id}` | Update recipe | MANAGER |
| DELETE | `/api/recipes/{id}` | Delete recipe | MANAGER |

**Request Body (POST/PUT):**
```json
{
  "product": {
    "id": 1
  },
  "ingredient": {
    "id": 1
  },
  "quantityNeeded": 0.02
}
```

**Response Example:**
```json
{
  "id": 1,
  "product": {
    "id": 1,
    "name": "Espresso"
  },
  "ingredient": {
    "id": 1,
    "name": "Coffee Beans"
  },
  "quantityNeeded": 0.02,
  "createdAt": "2025-12-05T10:00:00",
  "updatedAt": "2025-12-05T10:00:00"
}
```

---

### 5. User Management (`/api/users`)

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/users` | Get all users | MANAGER |
| GET | `/api/users/{id}` | Get user by ID | MANAGER or Self |
| POST | `/api/users` | Create new user | MANAGER |
| PUT | `/api/users/{id}` | Update user | MANAGER or Self |
| DELETE | `/api/users/{id}` | Delete user | MANAGER |
| POST | `/api/users/login` | User login | Public |

**Request Body (POST/PUT):**
```json
{
  "username": "cashier01",
  "passwordHash": "password123",
  "role": {
    "id": 2
  }
}
```

**Response Example:**
```json
{
  "id": 1,
  "username": "cashier01",
  "role": {
    "id": 2,
    "name": "CASHIER"
  },
  "createdAt": "2025-12-05T10:00:00",
  "updatedAt": "2025-12-05T10:00:00"
}
```

---

### 6. Transaction Management (`/api/transactions`)

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/transactions` | Get all transactions | MANAGER, CASHIER |
| GET | `/api/transactions/{id}` | Get transaction by ID | MANAGER, CASHIER |
| POST | `/api/transactions` | Create new transaction | CASHIER |
| PUT | `/api/transactions/{id}/status` | Update transaction status | MANAGER, CASHIER |
| GET | `/api/transactions/reports/sales` | Get sales report | MANAGER |

**Request Body (POST):**
```json
[
  {
    "productId": 1,
    "quantity": 2,
    "notes": "Extra hot"
  }
]
```

**Query Parameters:**
- `cashierId` (required): ID of the cashier

**Response Example:**
```json
{
  "id": 1,
  "user": {
    "id": 2,
    "username": "cashier01"
  },
  "totalAmount": 50000.0,
  "paymentMethod": "CASH",
  "status": "COMPLETED",
  "createdAt": "2025-12-05T10:00:00",
  "updatedAt": "2025-12-05T10:00:00"
}
```

**Sales Report Endpoint:**
```
GET /api/transactions/reports/sales?start=2025-12-01T00:00:00&end=2025-12-31T23:59:59
```

---

### 7. Stock Movement Management (`/api/stock-movements`)

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/stock-movements` | Get all stock movements | MANAGER, KITCHEN |
| GET | `/api/stock-movements/{id}` | Get stock movement by ID | MANAGER, KITCHEN |
| GET | `/api/stock-movements/ingredient/{ingredientId}` | Get movements by ingredient | MANAGER, KITCHEN |
| GET | `/api/stock-movements/ingredient/{id}/type/{type}` | Get movements by ingredient and type | MANAGER, KITCHEN |
| POST | `/api/stock-movements` | Create new stock movement | MANAGER, KITCHEN |

**Request Body (POST):**
```json
{
  "ingredient": {
    "id": 1
  },
  "movementType": "IN",
  "quantity": 10.0,
  "notes": "Restocking",
  "user": {
    "id": 3
  }
}
```

**Movement Types:**
- `IN` - Stock incoming
- `OUT` - Stock outgoing
- `ADJUSTMENT` - Stock adjustment

**Response Example:**
```json
{
  "id": 1,
  "ingredient": {
    "id": 1,
    "name": "Coffee Beans"
  },
  "movementType": "IN",
  "quantity": 10.0,
  "notes": "Restocking",
  "user": {
    "id": 3,
    "username": "kitchen01"
  },
  "createdAt": "2025-12-05T10:00:00"
}
```

---

### 8. Role Management (`/api/roles`)

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/roles` | Get all roles | Authenticated |
| GET | `/api/roles/{id}` | Get role by ID | Authenticated |
| POST | `/api/roles` | Create new role | MANAGER |
| PUT | `/api/roles/{id}` | Update role | MANAGER |
| DELETE | `/api/roles/{id}` | Delete role | MANAGER |

**Default Roles:**
- `MANAGER` (id: 1) - Full system access
- `CASHIER` (id: 2) - Transaction and menu access
- `KITCHEN` (id: 3) - Inventory and recipe access

---

## 🔒 Security Implementation Details

### Password Encoding
Passwords are hashed using **BCrypt** with the default strength factor. When creating or updating users, plain text passwords are automatically encoded before storage.

**Example:**
```
Plain: "password123"
Encoded: "{bcrypt}$2a$10$N9qo8uLOickgx2ZMRZoMe.q2ZWfhJpXvXXzXXzXXzXXzXXzXXz"
```

### Authentication Process
1. User sends credentials to `/api/users/login`
2. System retrieves user from database by username
3. Password is verified using BCrypt password matcher
4. On success, user details (including role) are returned
5. Client must include authentication in subsequent requests

### Authorization Process
- Each protected endpoint has `@PreAuthorize` annotation
- Spring Security checks user's role against required role(s)
- If authorized, request proceeds; otherwise, **403 Forbidden** is returned

---

## 📊 Error Responses

| Status Code | Description | Example Scenario |
|-------------|-------------|------------------|
| 200 OK | Successful GET/PUT | Resource retrieved/updated |
| 201 Created | Successful POST | Resource created |
| 400 Bad Request | Invalid input | Validation failed |
| 401 Unauthorized | Authentication failed | Invalid credentials |
| 403 Forbidden | Authorization failed | Insufficient permissions |
| 404 Not Found | Resource not found | Invalid ID |
| 500 Internal Server Error | Server error | Database connection failed |

**Error Response Format:**
```json
{
  "timestamp": "2025-12-05T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/users"
}
```

---

## 🧪 Testing the API

### Login Example
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

### Accessing Protected Endpoint (Example)
Since the API uses stateless authentication without JWT implementation yet, you'll need to implement session-based or token-based authentication for production use.

### Current Testing Approach
For testing during development:
1. Login to get user role information
2. Test endpoints based on user role
3. Expect 403 Forbidden for unauthorized access

---

## 🚀 Production Recommendations

### Immediate Next Steps
1. ✅ ~~Implement Spring Security~~ **COMPLETED**
2. ✅ ~~Add password hashing (BCrypt)~~ **COMPLETED**
3. ✅ ~~Add role-based access control~~ **COMPLETED**
4. ⚠️ Implement JWT token-based authentication
5. ⚠️ Add refresh token mechanism
6. ⚠️ Configure CORS for frontend access
7. ⚠️ Add API rate limiting
8. ⚠️ Implement comprehensive logging
9. ⚠️ Add Swagger/OpenAPI documentation
10. ⚠️ Write unit and integration tests

### Security Best Practices
- ✅ All passwords are BCrypt hashed
- ✅ CSRF disabled for REST API
- ✅ Stateless session management
- ✅ Role-based access control implemented
- ⚠️ Consider adding JWT for stateless authentication
- ⚠️ Implement HTTPS in production
- ⚠️ Add request rate limiting
- ⚠️ Implement audit logging for sensitive operations

---

## 📞 Support & Contact

For issues or questions regarding this API, please contact the development team or refer to the project repository.

**Project:** TerraCafe Backend  
**Version:** 0.0.1-SNAPSHOT  
**Last Updated:** December 5, 2025
