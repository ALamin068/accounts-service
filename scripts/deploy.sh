#!/bin/bash

# E-Commerce API Docker Deployment Script
echo "🚀 Starting E-Commerce REST API with MySQL..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Create .env file if it doesn't exist
if [ ! -f .env ]; then
    echo "📝 Creating .env file from template..."
    cp .env.example .env
    echo "⚠️  Please update .env file with your actual Stripe keys if needed"
fi

# Stop any existing containers
echo "🛑 Stopping existing containers..."
docker-compose down

# Build and start the services
echo "🏗️  Building and starting services..."
docker-compose up --build -d

# Wait for services to be healthy
echo "⏳ Waiting for services to be ready..."
sleep 30

# Check service status
echo "🔍 Checking service status..."
docker-compose ps

# Test the application
echo "🧪 Testing application health..."
if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ E-Commerce API is running successfully!"
    echo "📖 API Documentation: http://localhost:8080/swagger-ui.html"
    echo "🗄️  Database Admin (optional): http://localhost:8081 (run with --profile admin)"
    echo "💾 Health Check: http://localhost:8080/actuator/health"
else
    echo "⚠️  Application is starting up, please wait a moment and check http://localhost:8080/actuator/health"
fi

echo ""
echo "🎉 Deployment complete!"
echo "📋 Quick commands:"
echo "  - View logs: docker-compose logs -f"
echo "  - Stop services: docker-compose down"
echo "  - Restart: docker-compose restart"
echo "  - Access database admin: docker-compose --profile admin up -d"
