# 🛠️ Planning Tool

## 📖 Project Description
Planning Tool is a project calculation tool for the business **Alpha Solutions**.  
The purpose of the tool is to calculate estimated prices for customers ordering tech stack solutions from Alpha Solutions.

## 🎯 Goal
The goal is to help Alpha Solutions provide better price estimates for customers by using our tool.

## ✨ Features
Our project offers the ability to:

- 📁 Create and manage projects
- ✅ Add tasks and subtasks
- ⏱️ Set time estimates
- 📊 Track time spent
- 👨‍💻 Assign employees to projects
- 🗄️ Archive projects

## ⚙️ Technology Stack
- ☕ Java 17+
- 📦 Maven
- 🚀 Spring Boot
- 🌐 Spring Web
- 🗄️ Database: MySQL
- ☁️ Deployment: Microsoft Azure

## 📥 Installation

```bash
# Clone repository
git clone https://github.com/study-group-supreme/planning-tool.git

# Enter project folder
cd planning-tool

# Install dependencies
./mvnw install

# Start development server
./mvnw spring-boot:run
```

## 🗂️ Project Structure

```text
├── CONTRIBUTING.md
├── HELP.md
├── README.md
├── mvnw
├── mvnw.cmd
├── pom.xml
├── qodana.yaml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── planningtool
│   │   │       ├── PlanningToolApplication.java
│   │   │       ├── config
│   │   │       │   └── WebConfig.java
│   │   │       ├── controller
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── ProjectController.java
│   │   │       │   └── TaskController.java
│   │   │       ├── exception
│   │   │       │   ├── BadRequestException.java
│   │   │       │   ├── DatabaseOperationException.java
│   │   │       │   └── NotFoundException.java
│   │   │       ├── interceptor
│   │   │       │   └── LoginInterceptor.java
│   │   │       ├── model
│   │   │       │   ├── Employee.java
│   │   │       │   ├── Project.java
│   │   │       │   ├── Task.java
│   │   │       │   └── TimeEntry.java
│   │   │       ├── repository
│   │   │       │   ├── EmployeeRepository.java
│   │   │       │   ├── ProjectRepository.java
│   │   │       │   └── TaskRepository.java
│   │   │       └── service
│   │   │           ├── EmployeeService.java
│   │   │           ├── ProjectService.java
│   │   │           └── TaskService.java
│   │   └── resources
│   │       ├── application-dev.properties
│   │       ├── application.properties
│   │       ├── sql
│   │       │   ├── data.sql
│   │       │   └── schema.sql
│   │       ├── static
│   │       │   ├── createandeditforms.css
│   │       │   ├── images
│   │       │   │   └── alphalogo.png
│   │       │   └── index.css
│   │       └── templates
│   │           ├── auth
│   │           │   └── login.html
│   │           ├── fragment
│   │           │   └── header.html
│   │           ├── project
│   │           │   ├── add-member.html
│   │           │   ├── create-project.html
│   │           │   ├── details-project.html
│   │           │   ├── edit-project.html
│   │           │   ├── list-project-history.html
│   │           │   └── list-projects.html
│   │           └── task
│   │               ├── add-time-entry.html
│   │               ├── create-task.html
│   │               ├── details-task.html
│   │               └── edit-task.html
│   └── test
│       ├── java
│       │   └── planningtool
│       │       ├── PlanningToolApplicationTests.java
│       │       ├── controller
│       │       │   ├── AuthControllerTest.java
│       │       │   ├── ProjectControllerTest.java
│       │       │   └── TaskControllerTest.java
│       │       ├── integration
│       │       │   ├── EmployeeRepositoryTest.java
│       │       │   ├── ProjectRepositoryTest.java
│       │       │   └── TaskRepositoryTest.java
│       │       └── service
│       │           ├── EmployeeServiceTest.java
│       │           ├── ProjectServiceTest.java
│       │           └── TaskServiceTest.java
│       └── resources
│           ├── application-test.properties
│           └── h2init.sql
└── target
    ├── classes
    │   ├── application-dev.properties
    │   ├── application.properties
    │   ├── planningtool
    │   │   ├── PlanningToolApplication.class
    │   │   ├── config
    │   │   │   └── WebConfig.class
    │   │   ├── controller
    │   │   │   ├── AuthController.class
    │   │   │   ├── ProjectController.class
    │   │   │   └── TaskController.class
    │   │   ├── exception
    │   │   │   ├── BadRequestException.class
    │   │   │   ├── DatabaseOperationException.class
    │   │   │   └── NotFoundException.class
    │   │   ├── interceptor
    │   │   │   └── LoginInterceptor.class
    │   │   ├── model
    │   │   │   ├── Employee.class
    │   │   │   ├── Project.class
    │   │   │   ├── Task.class
    │   │   │   └── TimeEntry.class
    │   │   ├── repository
    │   │   │   ├── EmployeeRepository.class
    │   │   │   ├── ProjectRepository.class
    │   │   │   └── TaskRepository.class
    │   │   └── service
    │   │       ├── EmployeeService.class
    │   │       ├── ProjectService.class
    │   │       └── TaskService.class
    │   ├── sql
    │   │   ├── data.sql
    │   │   └── schema.sql
    │   ├── static
    │   │   ├── createandeditforms.css
    │   │   ├── images
    │   │   │   └── alphalogo.png
    │   │   ├── index.css
    │   │   └── placeholder
    │   └── templates
    │       ├── auth
    │       │   └── login.html
    │       ├── fragment
    │       │   └── header.html
    │       ├── placeholder
    │       ├── project
    │       │   ├── add-member.html
    │       │   ├── create-project.html
    │       │   ├── details-project.html
    │       │   ├── edit-project.html
    │       │   ├── list-project-history.html
    │       │   └── list-projects.html
    │       └── task
    │           ├── add-time-entry.html
    │           ├── create-task.html
    │           ├── details-task.html
    │           └── edit-task.html
    ├── generated-sources
    │   └── annotations
    ├── generated-test-sources
    │   └── test-annotations
    └── test-classes
        ├── application-test.properties
        ├── h2init.sql
        └── planningtool
            ├── PlanningToolApplicationTests.class
            ├── controller
            │   ├── AuthControllerTest.class
            │   ├── ProjectControllerTest.class
            │   └── TaskControllerTest.class
            ├── integration
            │   ├── EmployeeRepositoryTest.class
            │   ├── ProjectRepositoryTest.class
            │   └── TaskRepositoryTest.class
            └── service
                ├── EmployeeServiceTest.class
                ├── ProjectServiceTest$1.class
                ├── ProjectServiceTest$2.class
                ├── ProjectServiceTest.class
                ├── TaskServiceTest$1.class
                ├── TaskServiceTest$2.class
                ├── TaskServiceTest$3.class
                └── TaskServiceTest.class
