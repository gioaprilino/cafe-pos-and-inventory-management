# TerraCafe Backend API Documentation

## Summary
**Last Updated:** December 3, 2025  
**Total Endpoints:** 43  
**Authentication:** None (No security configuration detected)

---

## Authentication Status
⚠️ **WARNING:** This API currently has **NO AUTHENTICATION** implemented. All endpoints are publicly accessible.

### Recommendation
Consider implementing Spring Security with JWT tokens or OAuth2 for production use.

---

## API Endpoints

### 1. Category Management (`/api/categories`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/categories` | Get all categories | No |
| GET | `/api/categories/{id}` | Get category by ID | No |
| POST | `/api/categories` | Create new category | No |
| PUT | `/api/categories/{id}` | Update category | No |
| DELETE | `/api/categories/{id}` | Delete category | No |

**Request Body (POST/PUT):**
```json
{
  "name": "string",
  "description": "string"
}
```

**Response Example:**
```json
{
  "id": 1,
  "name": "Beverages",
  "description": "Hot and cold drinks",
  "createdAt": "2025-12-03T10:00:00",
  "updatedAt": "2025-12-03T10:00:00"
}
```

---

### 2. Ingredient Management (`/api/ingredients`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/ingredients` | Get all ingredients | No |
| GET | `/api/ingredients/{id}` | Get ingredient by ID | No |
| POST | `/api/ingredients` | Create new ingredient | No |
| PUT | `/api/ingredients/{id}` | Update ingredient | No |
| DELETE | `/api/ingredients/{id}` | Delete ingredient | No |
| GET | `/api/ingredients/low-stock` | Get low stock ingredients | No |

**Request Body (POST/PUT):**
```json
{
  "name": "string",
  "unit": "string",
  "currentStock": 0.0,
  "minStock": 0.0,
  "unitPrice": 0.0
}
```

**Response Example:**
```json
{
  "id": 1,
  "name": "Coffee Beans",
  "unit": "kg",
  "currentStock": 50.0,
  "minStock": 10.0,
  "unitPrice": 150000.0,
  "createdAt": "2025-12-03T10:00:00",
  "updatedAt": "2025-12-03T10:00:00"
}
```

---

### 3. Product Management (`/api/products`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/products` | Get all products | No |
| GET | `/api/products/{id}` | Get product by ID | No |
| POST | `/api/products` | Create new product | No |
| PUT | `/api/products/{id}` | Update product | No |
| DELETE | `/api/products/{id}` | Delete product | No |
| GET | `/api/products/category/{categoryId}` | Get products by category | No |
| GET | `/api/products/available` | Get available products | No |

**Request Body (POST/PUT):**
```json
{
  "name": "string",
  "description": "string",
  "price": 0.0,
  "categoryId": 0,
  "imageUrl": "string",
  "isAvailable": true
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
  "isAvailable": true,
  "createdAt": "2025-12-03T10:00:00",
  "updatedAt": "2025-12-03T10:00:00"
}
```

---

### 4. Recipe Management (`/api/recipes`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/recipes` | Get all recipes | No |
| GET | `/api/recipes/{id}` | Get recipe by ID | No |
| POST | `/api/recipes` | Create new recipe | No |
| PUT | `/api/recipes/{id}` | Update recipe | No |
| DELETE | `/api/recipes/{id}` | Delete recipe | No |
| GET | `/api/recipes/product/{productId}` | Get recipes by product | No |
| GET | `/api/recipes/ingredient/{ingredientId}` | Get recipes by ingredient | No |

**Request Body (POST/PUT):**
```json
{
  "productId": 0,
  "ingredientId": 0,
  "quantityNeeded": 0.0
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
  "createdAt": "2025-12-03T10:00:00",
  "updatedAt": "2025-12-03T10:00:00"
}
```

---

### 5. Role Management (`/api/roles`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/roles` | Get all roles | No |
| GET | `/api/roles/{id}` | Get role by ID | No |
| POST | `/api/roles` | Create new role | No |
| PUT | `/api/roles/{id}` | Update role | No |
| DELETE | `/api/roles/{id}` | Delete role | No |

**Request Body (POST/PUT):**
```json
{
  "name": "string",
  "description": "string"
}
```

**Response Example:**
```json
{
  "id": 1,
  "name": "ADMIN",
  "description": "Administrator role with full access",
  "createdAt": "2025-12-03T10:00:00",
  "updatedAt": "2025-12-03T10:00:00"
}
```

---

### 6. Stock Movement Management (`/api/stock-movements`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/stock-movements` | Get all stock movements | No |
| GET | `/api/stock-movements/{id}` | Get stock movement by ID | No |
| POST | `/api/stock-movements` | Create new stock movement | No |
| PUT | `/api/stock-movements/{id}` | Update stock movement | No |
| DELETE | `/api/stock-movements/{id}` | Delete stock movement | No |
| GET | `/api/stock-movements/ingredient/{ingredientId}` | Get movements by ingredient | No |
| GET | `/api/stock-movements/type/{type}` | Get movements by type | No |
| GET | `/api/stock-movements/date-range` | Get movements by date range | No |

**Query Parameters for date-range:**
- `startDate`: ISO date string (e.g., "2025-01-01")
- `endDate`: ISO date string (e.g., "2025-12-31")

**Request Body (POST/PUT):**
```json
{
  "ingredientId": 0,
  "movementType": "IN|OUT|ADJUSTMENT",
  "quantity": 0.0,
  "notes": "string",
  "userId": 0
}
```

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
    "id": 1,
    "username": "admin"
  },
  "createdAt": "2025-12-03T10:00:00"
}
```

---

### 7. Transaction Management (`/api/transactions`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/transactions` | Get all transactions | No |
| GET | `/api/transactions/{id}` | Get transaction by ID | No |
| POST | `/api/transactions` | Create new transaction | No |
| PUT | `/api/transactions/{id}` | Update transaction | No |
| DELETE | `/api/transactions/{id}` | Delete transaction | No |
| GET | `/api/transactions/user/{userId}` | Get transactions by user | No |
| GET | `/api/transactions/status/{status}` | Get transactions by status | No |
| GET | `/api/transactions/date-range` | Get transactions by date range | No |
| GET | `/api/transactions/daily-sales` | Get daily sales report | No |

**Query Parameters for date-range:**
- `startDate`: ISO date string
- `endDate`: ISO date string

**Request Body (POST/PUT):**
```json
{
  "userId": 0,
  "totalAmount": 0.0,
  "paymentMethod": "CASH|CARD|E_WALLET",
  "status": "PENDING|COMPLETED|CANCELLED",
  "notes": "string"
}
```

**Response Example:**
```json
{
  "id": 1,
  "user": {
    "id": 1,
    "username": "cashier01"
  },
  "totalAmount": 75000.0,
  "paymentMethod": "CASH",
  "status": "COMPLETED",
  "notes": "Table 5",
  "createdAt": "2025-12-03T10:00:00",
  "updatedAt": "2025-12-03T10:00:00"
}
```

---

### 8. Transaction Item Management (`/api/transaction-items`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/transaction-items` | Get all transaction items | No |
| GET | `/api/transaction-items/{id}` | Get transaction item by ID | No |
| POST | `/api/transaction-items` | Create new transaction item | No |
| PUT | `/api/transaction-items/{id}` | Update transaction item | No |
| DELETE | `/api/transaction-items/{id}` | Delete transaction item | No |
| GET | `/api/transaction-items/transaction/{transactionId}` | Get items by transaction | No |
| GET | `/api/transaction-items/product/{productId}` | Get items by product | No |

**Request Body (POST/PUT):**
```json
{
  "transactionId": 0,
  "productId": 0,
  "quantity": 0,
  "price": 0.0,
  "subtotal": 0.0,
  "notes": "string"
}
```

**Response Example:**
```json
{
  "id": 1,
  "transaction": {
    "id": 1
  },
  "product": {
    "id": 1,
    "name": "Espresso"
  },
  "quantity": 2,
  "price": 25000.0,
  "subtotal": 50000.0,
  "notes": "Extra hot",
  "createdAt": "2025-12-03T10:00:00",
  "updatedAt": "2025-12-03T10:00:00"
}
```

---

### 9. User Management (`/api/users`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/users` | Get all users | No |
| GET | `/api/users/{id}` | Get user by ID | No |
| POST | `/api/users` | Create new user | No |
| PUT | `/api/users/{id}` | Update user | No |
| DELETE | `/api/users/{id}` | Delete user | No |
| POST | `/api/users/login` | User login | No |
| GET | `/api/users/role/{roleId}` | Get users by role | No |
| GET | `/api/users/username/{username}` | Get user by username | No |

**Request Body (POST/PUT):**
```json
{
  "username": "string",
  "password": "string",
  "fullName": "string",
  "email": "string",
  "phoneNumber": "string",
  "roleId": 0,
  "isActive": true
}
```

**Request Body (LOGIN):**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response Example:**
```json
{
  "id": 1,
  "username": "admin",
  "fullName": "Administrator",
  "email": "admin@terracafe.com",
  "phoneNumber": "08123456789",
  "role": {
    "id": 1,
    "name": "ADMIN"
  },
  "isActive": true,
  "createdAt": "2025-12-03T10:00:00",
  "updatedAt": "2025-12-03T10:00:00"
}
```

---

## Security Recommendations

### 1. Implement Authentication
Add Spring Security to your `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
```

### 2. Suggested Security Configuration
- Public endpoints: `/api/users/login`, `/api/products` (read-only), `/api/categories` (read-only)
- Admin endpoints: Role management, User management, Stock movements
- Cashier endpoints: Transactions, Transaction items
- All users: Product ordering, viewing menus

### 3. Password Security
⚠️ **CRITICAL:** Passwords appear to be stored in plain text. Implement BCrypt password hashing immediately.

### 4. CORS Configuration
Add CORS configuration to allow frontend access from specific domains only.

### 5. Input Validation
Add `@Valid` annotations and validation constraints to all DTOs.

---

## Error Responses

All endpoints follow standard HTTP status codes:

- `200 OK`: Successful GET/PUT requests
- `201 Created`: Successful POST requests
- `204 No Content`: Successful DELETE requests
- `400 Bad Request`: Invalid input data
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server-side errors

**Error Response Format:**
```json
{
  "timestamp": "2025-12-03T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/products"
}
```

---

## Testing the API

### Using cURL
```bash
# Get all products
curl -X GET http://localhost:8080/api/products

# Create a new category
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name":"Beverages","description":"Hot and cold drinks"}'

# Login
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Using Postman/Insomnia
Import the base URL: `http://localhost:8080` and test each endpoint using the request bodies provided above.

---

## Database Schema Notes

Based on the entities, the system manages:
- **Categories**: Product categorization
- **Products**: Menu items for sale
- **Ingredients**: Raw materials
- **Recipes**: Product-ingredient relationships
- **Users**: System users with roles
- **Roles**: User authorization levels
- **Transactions**: Sales records
- **Transaction Items**: Individual items in transactions
- **Stock Movements**: Ingredient inventory tracking

---

## Next Steps

1. ✅ Complete TODO features (None found)
2. ⚠️ Implement Spring Security with JWT authentication
3. ⚠️ Add password hashing (BCrypt)
4. ⚠️ Add input validation with `@Valid` and constraints
5. ⚠️ Configure CORS for frontend access
6. ⚠️ Add API rate limiting
7. ⚠️ Implement proper error handling and custom exceptions
8. ⚠️ Add API documentation with Swagger/OpenAPI
9. ⚠️ Add logging and monitoring
10. ⚠️ Write unit and integration tests
