#!/bin/bash

echo "=========================================="
echo "🔐 JWT AUTHENTICATION TEST SCRIPT"
echo "=========================================="
echo ""

BASE_URL="http://localhost:8080"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "📋 Test Plan:"
echo "1. Test login dengan credentials valid"
echo "2. Test akses /api/ingredients TANPA token (should fail)"
echo "3. Test akses /api/ingredients DENGAN token (should succeed)"
echo "4. Test akses /api/recipes DENGAN token (should succeed)"
echo ""

# Test 1: Login
echo "=========================================="
echo "TEST 1: Login untuk mendapatkan JWT Token"
echo "=========================================="
echo -e "${YELLOW}Endpoint: POST /api/users/login${NC}"
echo ""

LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}')

echo "Response:"
echo "$LOGIN_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$LOGIN_RESPONSE"
echo ""

# Extract token menggunakan grep dan sed
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | sed 's/"token":"//')

if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
    echo -e "${RED}❌ FAILED: Login gagal atau token tidak ditemukan${NC}"
    echo "Mencoba dengan username/password yang berbeda..."
    
    # Try with different credentials
    LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/users/login" \
      -H "Content-Type: application/json" \
      -d '{"username":"manager","password":"password"}')
    
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | sed 's/"token":"//')
    
    if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
        echo -e "${RED}❌ GAGAL TOTAL: Tidak bisa mendapatkan token${NC}"
        echo "Silakan periksa:"
        echo "1. Apakah server sudah running?"
        echo "2. Apakah ada user di database?"
        echo "3. Username dan password yang benar?"
        exit 1
    fi
fi

echo -e "${GREEN}✅ SUCCESS: Token berhasil didapatkan!${NC}"
echo "Token: ${TOKEN:0:50}..."
echo ""

# Test 2: Access without token
echo "=========================================="
echo "TEST 2: Akses /api/ingredients TANPA token"
echo "=========================================="
echo -e "${YELLOW}Endpoint: GET /api/ingredients (No Authorization Header)${NC}"
echo ""

RESPONSE_NO_TOKEN=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "$BASE_URL/api/ingredients")
HTTP_CODE_NO_TOKEN=$(echo "$RESPONSE_NO_TOKEN" | grep "HTTP_CODE" | cut -d: -f2)
BODY_NO_TOKEN=$(echo "$RESPONSE_NO_TOKEN" | sed '/HTTP_CODE/d')

echo "HTTP Status: $HTTP_CODE_NO_TOKEN"
echo "Response: $BODY_NO_TOKEN" | head -c 200
echo ""

if [ "$HTTP_CODE_NO_TOKEN" == "403" ] || [ "$HTTP_CODE_NO_TOKEN" == "401" ]; then
    echo -e "${GREEN}✅ SUCCESS: Request ditolak (seperti yang diharapkan)${NC}"
else
    echo -e "${RED}⚠️  WARNING: Expected 403/401, got $HTTP_CODE_NO_TOKEN${NC}"
fi
echo ""

# Test 3: Access with token - Ingredients
echo "=========================================="
echo "TEST 3: Akses /api/ingredients DENGAN token"
echo "=========================================="
echo -e "${YELLOW}Endpoint: GET /api/ingredients${NC}"
echo -e "${YELLOW}Authorization: Bearer ${TOKEN:0:30}...${NC}"
echo ""

RESPONSE_WITH_TOKEN=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "$BASE_URL/api/ingredients" \
  -H "Authorization: Bearer $TOKEN")
HTTP_CODE_WITH_TOKEN=$(echo "$RESPONSE_WITH_TOKEN" | grep "HTTP_CODE" | cut -d: -f2)
BODY_WITH_TOKEN=$(echo "$RESPONSE_WITH_TOKEN" | sed '/HTTP_CODE/d')

echo "HTTP Status: $HTTP_CODE_WITH_TOKEN"
echo "Response:"
echo "$BODY_WITH_TOKEN" | python3 -m json.tool 2>/dev/null | head -20 || echo "$BODY_WITH_TOKEN" | head -20
echo ""

if [ "$HTTP_CODE_WITH_TOKEN" == "200" ]; then
    echo -e "${GREEN}✅ SUCCESS: Akses berhasil dengan token!${NC}"
else
    echo -e "${RED}❌ FAILED: Expected 200, got $HTTP_CODE_WITH_TOKEN${NC}"
fi
echo ""

# Test 4: Access with token - Recipes
echo "=========================================="
echo "TEST 4: Akses /api/recipes DENGAN token"
echo "=========================================="
echo -e "${YELLOW}Endpoint: GET /api/recipes${NC}"
echo -e "${YELLOW}Authorization: Bearer ${TOKEN:0:30}...${NC}"
echo ""

RESPONSE_RECIPES=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "$BASE_URL/api/recipes" \
  -H "Authorization: Bearer $TOKEN")
HTTP_CODE_RECIPES=$(echo "$RESPONSE_RECIPES" | grep "HTTP_CODE" | cut -d: -f2)
BODY_RECIPES=$(echo "$RESPONSE_RECIPES" | sed '/HTTP_CODE/d')

echo "HTTP Status: $HTTP_CODE_RECIPES"
echo "Response:"
echo "$BODY_RECIPES" | python3 -m json.tool 2>/dev/null | head -20 || echo "$BODY_RECIPES" | head -20
echo ""

if [ "$HTTP_CODE_RECIPES" == "200" ]; then
    echo -e "${GREEN}✅ SUCCESS: Akses recipes berhasil dengan token!${NC}"
else
    echo -e "${RED}❌ FAILED: Expected 200, got $HTTP_CODE_RECIPES${NC}"
fi
echo ""

# Final Summary
echo "=========================================="
echo "📊 TEST SUMMARY"
echo "=========================================="
echo ""
echo "1. Login & Get Token:        $([ -n "$TOKEN" ] && echo -e "${GREEN}✅ PASSED${NC}" || echo -e "${RED}❌ FAILED${NC}")"
echo "2. Access without Token:     $([ "$HTTP_CODE_NO_TOKEN" == "403" ] && echo -e "${GREEN}✅ PASSED${NC}" || echo -e "${YELLOW}⚠️  $HTTP_CODE_NO_TOKEN${NC}")"
echo "3. Access Ingredients (JWT): $([ "$HTTP_CODE_WITH_TOKEN" == "200" ] && echo -e "${GREEN}✅ PASSED${NC}" || echo -e "${RED}❌ FAILED${NC}")"
echo "4. Access Recipes (JWT):     $([ "$HTTP_CODE_RECIPES" == "200" ] && echo -e "${GREEN}✅ PASSED${NC}" || echo -e "${RED}❌ FAILED${NC}")"
echo ""
echo "=========================================="
echo "🎉 JWT Authentication Testing Complete!"
echo "=========================================="
