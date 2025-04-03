#!/usr/bin/env python3

import requests
import json
import sys
from datetime import datetime
from colorama import Fore, Style, init

# Initialize colorama for colored terminal output
init()

# API URL
API_URL = "http://localhost:8080/api"

def print_test(name):
    print(f"{Fore.YELLOW}Running test: {name}{Style.RESET_ALL}")

def print_success(message):
    print(f"{Fore.GREEN}✓ {message}{Style.RESET_ALL}")

def print_failure(message):
    print(f"{Fore.RED}✗ {message}{Style.RESET_ALL}")

def print_section(title):
    print(f"\n{Fore.CYAN}== {title} =={Style.RESET_ALL}")

def assert_true(condition, message, error_message=None):
    if condition:
        print_success(message)
        return True
    else:
        print_failure(error_message or message)
        return False

def cleanup():
    print_section("Cleanup: Removing any existing test data")
    try:
        requests.delete(f"{API_URL}/users/jdoe")
        requests.delete(f"{API_URL}/users/asmith")
        requests.delete(f"{API_URL}/policies/underaged")
        requests.delete(f"{API_URL}/policies/internal-user")
        requests.delete(f"{API_URL}/policies/developer-full-access")
    except Exception as e:
        print(f"Cleanup error (can be ignored): {e}")

def run_tests():
    # Step 1: Create policies
    print_section("Creating Policies")

    print_test("Creating underaged policy")
    policy1_response = requests.post(
        f"{API_URL}/policies",
        headers={"Content-Type": "application/json"},
        json={
            "id": "underaged",
            "name": "Underaged User",
            "youngerThan": {
                "value": 18
            }
        }
    )
    assert_true(policy1_response.status_code == 201,
                f"Created underaged policy (HTTP {policy1_response.status_code})")

    print_test("Creating internal-user policy")
    policy2_response = requests.post(
        f"{API_URL}/policies",
        headers={"Content-Type": "application/json"},
        json={
            "id": "internal-user",
            "name": "Internal User",
            "emailDomainIs": {
                "value": "evolveum.com"
            }
        }
    )
    assert_true(policy2_response.status_code == 201,
                f"Created internal-user policy (HTTP {policy2_response.status_code})")

    print_test("Creating developer-full-access policy")
    policy3_response = requests.post(
        f"{API_URL}/policies",
        headers={"Content-Type": "application/json"},
        json={
            "id": "developer-full-access",
            "name": "Developer (Full Access)",
            "isMemberOf": {
                "value": "Software Development"
            }
        }
    )
    assert_true(policy3_response.status_code == 201,
                f"Created developer-full-access policy (HTTP {policy3_response.status_code})")

    # Verify policies were created
    print_test("Verifying policies were created")
    policies_response = requests.get(f"{API_URL}/policies")
    policies = policies_response.json()

    policy_ids = [p.get("id") for p in policies]
    assert_true("underaged" in policy_ids, "Found underaged policy")
    assert_true("internal-user" in policy_ids, "Found internal-user policy")
    assert_true("developer-full-access" in policy_ids, "Found developer-full-access policy")

    # Step 2: Create Users
    print_section("Creating Users and Verifying Policy Application")

    print_test("Creating underaged internal developer user")
    user1_response = requests.post(
        f"{API_URL}/users",
        headers={"Content-Type": "application/json"},
        json={
            "name": "jdoe",
            "firstName": "John",
            "lastName": "Doe",
            "emailAddress": "jdoe@evolveum.com",
            "organizationUnit": [
                "Software Development",
                "Support"
            ],
            "birthDate": "2007-09-07"
        }
    )

    if user1_response.status_code == 201:
        print_success(f"Created jdoe user (HTTP {user1_response.status_code})")
        jdoe = user1_response.json()

        # Check if all applicable policies were applied
        policies = jdoe.get("policy", [])
        assert_true("underaged" in policies, "Underaged policy applied to jdoe")
        assert_true("internal-user" in policies, "Internal-user policy applied to jdoe")
        assert_true("developer-full-access" in policies, "Developer-full-access policy applied to jdoe")
    else:
        print_failure(f"Failed to create jdoe user (HTTP {user1_response.status_code})")
        return

    print_test("Creating adult external user")
    user2_response = requests.post(
        f"{API_URL}/users",
        headers={"Content-Type": "application/json"},
        json={
            "name": "asmith",
            "firstName": "Alice",
            "lastName": "Smith",
            "emailAddress": "asmith@external.com",
            "organizationUnit": [
                "Marketing"
            ],
            "birthDate": "1990-01-15"
        }
    )

    if user2_response.status_code == 201:
        print_success(f"Created asmith user (HTTP {user2_response.status_code})")
        asmith = user2_response.json()

        # Check that no policies were applied
        policies = asmith.get("policy", [])
        assert_true(len(policies) == 0, "No policies applied to asmith")
    else:
        print_failure(f"Failed to create asmith user (HTTP {user2_response.status_code})")
        return

    # Step 3: Update User
    print_section("Updating User and Checking Policy Changes")

    print_test("Updating asmith to be an internal developer")
    update_response = requests.put(
        f"{API_URL}/users/asmith",
        headers={"Content-Type": "application/json"},
        json={
            "name": "asmith",
            "firstName": "Alice",
            "lastName": "Smith",
            "emailAddress": "asmith@evolveum.com",
            "organizationUnit": [
                "Software Development"
            ],
            "birthDate": "1990-01-15"
        }
    )

    if update_response.status_code == 200:
        print_success(f"Updated asmith user (HTTP {update_response.status_code})")
        updated_asmith = update_response.json()

        policies = updated_asmith.get("policy", [])
        assert_true("internal-user" in policies, "Internal-user policy applied to asmith")
        assert_true("developer-full-access" in policies, "Developer-full-access policy applied to asmith")
        assert_true("underaged" not in policies, "Underaged policy not applied to asmith")
    else:
        print_failure(f"Failed to update asmith user (HTTP {update_response.status_code})")
        return

    # Step 4: Update Policy
    print_section("Updating Policy and Checking Effect on Users")

    print_test("Updating underaged policy to apply to users under 40")
    policy_update_response = requests.put(
        f"{API_URL}/policies/underaged",
        headers={"Content-Type": "application/json"},
        json={
            "id": "underaged",
            "name": "Underaged User",
            "youngerThan": {
                "value": 40
            }
        }
    )

    if policy_update_response.status_code == 200:
        print_success(f"Updated underaged policy (HTTP {policy_update_response.status_code})")

        # Check if asmith now has underaged policy
        asmith_check = requests.get(f"{API_URL}/users/asmith").json()
        policies = asmith_check.get("policy", [])

        assert_true("underaged" in policies, "Updated underaged policy applied to asmith")
        assert_true("internal-user" in policies, "Internal-user policy still applied to asmith")
        assert_true("developer-full-access" in policies, "Developer-full-access policy still applied to asmith")
    else:
        print_failure(f"Failed to update underaged policy (HTTP {policy_update_response.status_code})")
        return

    # Step 5: Delete Policy
    print_section("Deleting Policy and Checking Effect on Users")

    print_test("Deleting developer-full-access policy")
    delete_response = requests.delete(f"{API_URL}/policies/developer-full-access")

    assert_true(delete_response.status_code == 204,
                f"Deleted developer policy (HTTP {delete_response.status_code})")

    print_test("Checking policy removal from users")
    jdoe_check = requests.get(f"{API_URL}/users/jdoe").json()
    jdoe_policies = jdoe_check.get("policy", [])
    assert_true("developer-full-access" not in jdoe_policies,
                "Developer policy removed from jdoe")

    asmith_check = requests.get(f"{API_URL}/users/asmith").json()
    asmith_policies = asmith_check.get("policy", [])
    assert_true("developer-full-access" not in asmith_policies,
                "Developer policy removed from asmith")

    print_section("All tests passed successfully!")

if __name__ == "__main__":
    try:
        # Clean up existing data first
        cleanup()

        # Run all tests
        run_tests()

        # Clean up after tests
        cleanup()

    except Exception as e:
        print(f"{Fore.RED}Error: {e}{Style.RESET_ALL}")
        sys.exit(1)