#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

API_URL="http://localhost:8080/api"

# Function to print test result
test_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}PASS${NC}: $2"
    else
        echo -e "${RED}FAIL${NC}: $2"
        exit 1
    fi
}

echo -e "${YELLOW}Starting User Policy Manager API Tests${NC}"
echo "================================================="
echo ""

# Clean up existing data (if any)
echo -e "${YELLOW}Cleanup: Removing any existing users and policies${NC}"
curl -s -X DELETE $API_URL/users/jdoe 2>&1 > /dev/null
curl -s -X DELETE $API_URL/users/asmith 2>&1 > /dev/null
curl -s -X DELETE $API_URL/policies/underaged 2>&1 > /dev/null
curl -s -X DELETE $API_URL/policies/internal-user 2>&1 > /dev/null
curl -s -X DELETE $API_URL/policies/developer-full-access 2>&1 > /dev/null
echo ""

# Step 1: Create Policies
echo -e "${YELLOW}Test 1: Creating Policies${NC}"

echo "Creating underaged policy"
underaged_response=$(curl -s -w "%{http_code}" -X POST $API_URL/policies -H "Content-Type: application/json" -d '{
  "id": "underaged",
  "name": "Underaged User",
  "youngerThan": {
    "value": 18
  }
}')
status_code=${underaged_response: -3}
test_result $((status_code != 201)) "Create underaged policy (HTTP $status_code)"

echo "Creating internal-user policy"
internal_response=$(curl -s -w "%{http_code}" -X POST $API_URL/policies -H "Content-Type: application/json" -d '{
  "id": "internal-user",
  "name": "Internal User",
  "emailDomainIs": {
    "value": "evolveum.com"
  }
}')
status_code=${internal_response: -3}
test_result $((status_code != 201)) "Create internal-user policy (HTTP $status_code)"

echo "Creating developer-full-access policy"
developer_response=$(curl -s -w "%{http_code}" -X POST $API_URL/policies -H "Content-Type: application/json" -d '{
  "id": "developer-full-access",
  "name": "Developer (Full Access)",
  "isMemberOf": {
    "value": "Software Development"
  }
}')
status_code=${developer_response: -3}
test_result $((status_code != 201)) "Create developer-full-access policy (HTTP $status_code)"

echo ""
echo "Verifying policies were created"
policies_response=$(curl -s -X GET $API_URL/policies)
echo $policies_response | grep -q "underaged"
test_result $? "Verify underaged policy exists"
echo $policies_response | grep -q "internal-user"
test_result $? "Verify internal-user policy exists"
echo $policies_response | grep -q "developer-full-access"
test_result $? "Verify developer-full-access policy exists"
echo ""

# Step 2: Create Users
echo -e "${YELLOW}Test 2: Creating Users and Verifying Policy Application${NC}"

echo "Creating underaged internal developer user"
user_response=$(curl -s -X POST $API_URL/users -H "Content-Type: application/json" -d '{
  "name": "jdoe",
  "firstName": "John",
  "lastName": "Doe",
  "emailAddress": "jdoe@evolveum.com",
  "organizationUnit": [
    "Software Development",
    "Support"
  ],
  "birthDate": "2007-09-07"
}')
echo ""

echo "Checking if all policies were applied"
echo "$user_response" | grep -q "underaged"
test_result $? "Verify underaged policy was applied"
echo "$user_response" | grep -q "internal-user"
test_result $? "Verify internal-user policy was applied"
echo "$user_response" | grep -q "developer-full-access"
test_result $? "Verify developer-full-access policy was applied"
echo ""

echo "Creating adult external user"
adult_response=$(curl -s -X POST $API_URL/users -H "Content-Type: application/json" -d '{
  "name": "asmith",
  "firstName": "Alice",
  "lastName": "Smith",
  "emailAddress": "asmith@external.com",
  "organizationUnit": [
    "Marketing"
  ],
  "birthDate": "1990-01-15"
}')
echo ""

# Step 3: Check policies for adult external user
echo -e "Test: 3${YELLOW}Checking that no policies were applied${NC}"
if echo "$adult_response" | rg -q "\"policy\":\[\]"; then
    test_result 0 "Verify no policies were applied to adult external user"
else
    test_result 1 "Verify no policies were applied to adult external user"
fi
echo ""

# Step 4: Update User
echo -e "${YELLOW}Test 4: Updating User and Checking Policy Changes${NC}"

echo "Updating asmith to be an internal developer"
update_response=$(curl -s -X PUT $API_URL/users/asmith -H "Content-Type: application/json" -d '{
  "name": "asmith",
  "firstName": "Alice",
  "lastName": "Smith",
  "emailAddress": "asmith@evolveum.com",
  "organizationUnit": [
    "Software Development"
  ],
  "birthDate": "1990-01-15"
}')
echo ""

echo "Checking if appropriate policies were applied"
echo "$update_response" | grep -q "internal-user"
test_result $? "Verify internal-user policy was applied"
echo "$update_response" | grep -q "developer-full-access"
test_result $? "Verify developer-full-access policy was applied"
echo "$update_response" | grep -v -q "underaged"
test_result $? "Verify underaged policy was NOT applied"
echo ""

# Step 5: Update Policy
echo -e "${YELLOW}Test 5: Updating Policy and Checking Effect on Users${NC}"

echo "Updating underaged policy to apply to users under 40"
policy_update_response=$(curl -s -X PUT $API_URL/policies/underaged -H "Content-Type: application/json" -d '{
  "id": "underaged",
  "name": "Underaged User",
  "youngerThan": {
    "value": 40
  }
}')
echo ""

echo "Checking if asmith now has the underaged policy"
asmith_check=$(curl -s -X GET $API_URL/users/asmith)
echo "$asmith_check" | grep -q "underaged"
test_result $? "Verify updated underaged policy was applied to asmith"
echo "$asmith_check" | grep -q "internal-user"
test_result $? "Verify internal-user policy is still applied"
echo "$asmith_check" | grep -q "developer-full-access"
test_result $? "Verify developer-full-access policy is still applied"
echo ""

# Step 6: Delete Policy
echo -e "${YELLOW}Test 6: Deleting Policy and Checking Effect on Users${NC}"

echo "Deleting developer-full-access policy"
curl -s -X DELETE $API_URL/policies/developer-full-access -w "%{http_code}" | grep -q "204"
test_result $? "Verify policy was deleted successfully"
echo ""

echo "Checking if the policy was removed from users"
jdoe_check=$(curl -s -X GET $API_URL/users/jdoe)
echo "$jdoe_check" | grep -v -q "developer-full-access"
test_result $? "Verify developer-full-access policy was removed from jdoe"
echo ""

asmith_check=$(curl -s -X GET $API_URL/users/asmith)
echo "$asmith_check" | grep -v -q "developer-full-access"
test_result $? "Verify developer-full-access policy was removed from asmith"
echo ""

# Clean up test data
echo -e "${YELLOW}Cleanup: Removing test users and policies${NC}"
curl -s -X DELETE $API_URL/users/jdoe 2>&1 > /dev/null
curl -s -X DELETE $API_URL/users/asmith 2>&1 > /dev/null
curl -s -X DELETE $API_URL/policies/underaged 2>&1 > /dev/null
curl -s -X DELETE $API_URL/policies/internal-user 2>&1 > /dev/null
echo ""

echo -e "${GREEN}All tests passed successfully!${NC}"
echo "================================================="