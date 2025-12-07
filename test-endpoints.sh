#!/bin/bash

echo "========================================"
echo "Testing TerraCafe API Endpoints"
echo "========================================"
echo ""

BASE_URL="http://localhost:8081"

# Test 1: Test Public Endpoint - Products (Should Work)
echo "Test 1: GET /api/products (Public - No Auth Required)"
echo "Expected: 200 OK with product list"
curl -s -w "\nHTTP Status: %{http_code}\n" -X GET "${BASE_URL}/api/products"
echo ""
echo "========================================"
echo ""

# Test 2: Test Login
echo "Test 2: POST /api/users/login"
echo "Expected: 200 OK with JWT token"
LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}')

echo "$LOGIN_RESPONSE" | jq .

# Extract token from response
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token // empty')

if [ -z "$TOKEN" ]; then
    echo "ERROR: Failed to get JWT token from login"
    echo "========================================"
    exit 1
fi

echo ""
echo "JWT Token obtained successfully!"
echo "Token (first 50 chars): ${TOKEN:0:50}..."
echo "========================================"
echo ""

# Test 3: Test Protected Endpoint - Recipes WITHOUT Token (Should Fail)
echo "Test 3: GET /api/recipes (Protected - Without Token)"
echo "Expected: 403 Forbidden"
curl -s -w "\nHTTP Status: %{http_code}\n" -X GET "${BASE_URL}/api/recipes"
echo ""
echo "========================================"
echo ""

# Test 4: Test Protected Endpoint - Recipes WITH Token (Should Work)
echo "Test 4: GET /api/recipes (Protected - With Valid Token)"
echo "Expected: 200 OK with recipe list"
curl -s -w "\nHTTP Status: %{http_code}\n" -X GET "${BASE_URL}/api/recipes" \
  -H "Authorization: Bearer ${TOKEN}"
echo ""
echo "========================================"
echo ""

# Test 5: Test Public Endpoint - Products by ID
echo "Test 5: GET /api/products/1 (Public - No Auth Required)"
echo "Expected: 200 OK or 404 Not Found"
curl -s -w "\nHTTP Status: %{http_code}\n" -X GET "${BASE_URL}/api/products/1"
echo ""
echo "========================================"
echo ""

# Test 6: Test Public Endpoint - Active Products
echo "Test 6: GET /api/products/active (Public - No Auth Required)"
echo "Expected: 200 OK with active products"
curl -s -w "\nHTTP Status: %{http_code}\n" -X GET "${BASE_URL}/api/products/active"
echo ""
echo "========================================"
echo ""

echo "All tests completed!"
echo ""
echo "Summary:"
echo "- Test 1 (Products): Should return 200"
echo "- Test 2 (Login): Should return JWT token"
echo "- Test 3 (Recipes without token): Should return 403"
echo "- Test 4 (Recipes with token): Should return 200"
echo "- Test 5 (Product by ID): Should return 200 or 404"
echo "- Test 6 (Active Products): Should return 200"
