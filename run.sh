#!/bin/bash

set -e

echo "Building the application..."
./mvnw clean package -DskipTests

echo "Building Docker image..."
# Build Docker image with explicit tag
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=user-policy-manager:latest

echo "Starting containers with Docker Compose..."
docker-compose up -d

echo "Application started successfully!"
echo "API available at http://localhost:8080"