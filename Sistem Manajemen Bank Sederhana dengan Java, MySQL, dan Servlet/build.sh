#!/bin/bash

# Build script untuk Bank Management System
# Author: Manus AI
# Description: Script untuk kompilasi dan packaging aplikasi

echo "========================================="
echo "Bank Management System - Build Script"
echo "========================================="
echo ""

# Warna untuk output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Fungsi untuk print dengan warna
print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_info() {
    echo -e "${YELLOW}[INFO]${NC} $1"
}

# Cek Java
print_info "Checking Java installation..."
if ! command -v javac &> /dev/null; then
    print_error "javac not found. Please install JDK 8 or higher."
    exit 1
fi
JAVA_VERSION=$(javac -version 2>&1 | awk '{print $2}')
print_success "Java compiler found: $JAVA_VERSION"
echo ""

# Cek jar command
print_info "Checking jar command..."
if ! command -v jar &> /dev/null; then
    print_error "jar command not found. Please install JDK."
    exit 1
fi
print_success "jar command found"
echo ""

# Clean previous build
print_info "Cleaning previous build..."
rm -rf src/main/webapp/WEB-INF/classes/*
rm -f bank-system.war
print_success "Clean completed"
echo ""

# Create directories
print_info "Creating directories..."
mkdir -p src/main/webapp/WEB-INF/classes
mkdir -p src/main/webapp/WEB-INF/lib
print_success "Directories created"
echo ""

# Compile Java files
print_info "Compiling Java source files..."
javac -cp "lib/*" -d src/main/webapp/WEB-INF/classes \
    src/main/java/com/bank/model/*.java \
    src/main/java/com/bank/util/*.java \
    src/main/java/com/bank/servlet/*.java

if [ $? -eq 0 ]; then
    print_success "Compilation successful"
    
    # Count compiled classes
    CLASS_COUNT=$(find src/main/webapp/WEB-INF/classes -name "*.class" | wc -l)
    print_info "Compiled $CLASS_COUNT class files"
else
    print_error "Compilation failed"
    exit 1
fi
echo ""

# Copy MySQL JDBC driver
print_info "Copying MySQL JDBC driver..."
cp lib/mysql-connector-j-8.0.33.jar src/main/webapp/WEB-INF/lib/
if [ $? -eq 0 ]; then
    print_success "JDBC driver copied"
else
    print_error "Failed to copy JDBC driver"
    exit 1
fi
echo ""

# Create WAR file
print_info "Creating WAR file..."
cd src/main/webapp
jar -cvf ../../../bank-system.war * > /dev/null 2>&1
cd ../../..

if [ -f "bank-system.war" ]; then
    WAR_SIZE=$(du -h bank-system.war | cut -f1)
    print_success "WAR file created: bank-system.war ($WAR_SIZE)"
else
    print_error "Failed to create WAR file"
    exit 1
fi
echo ""

# Summary
echo "========================================="
echo "Build Summary"
echo "========================================="
echo "Project: Bank Management System"
echo "WAR File: bank-system.war"
echo "Size: $WAR_SIZE"
echo "Classes: $CLASS_COUNT"
echo ""
print_success "Build completed successfully!"
echo ""
echo "Next steps:"
echo "1. Setup MySQL database: mysql -u root -p < database.sql"
echo "2. Deploy WAR file to Tomcat webapps directory"
echo "3. Start Tomcat server"
echo "4. Access application at http://localhost:8080/bank-system/"
echo ""
echo "For detailed deployment instructions, see DEPLOYMENT.md"
echo "========================================="
