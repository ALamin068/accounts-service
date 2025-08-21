#!/bin/bash

echo "🚀 Setting up Account Service..."

# Install dependencies
echo "📦 Installing dependencies..."
npm install

# Start services with Docker Compose
echo "🐳 Starting services..."
docker-compose up -d

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 10

# Check if services are running
echo "🔍 Checking service health..."
docker-compose ps

echo "✅ Setup complete!"
echo "🌐 Application should be running at: http://localhost:3000"
echo "🗄️ Database is running at: localhost:5432"
echo ""
echo "Commands:"
echo "  npm run dev          - Start development server"
echo "  npm run docker:logs  - View logs"
echo "  npm run docker:down  - Stop services"

