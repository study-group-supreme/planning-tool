# 🤝 Contributing to Planning Tool

Thank you for contributing to the project! 🚀

## 📌 Getting Started

1. 🍴 Fork this repository

2. 📥 Clone your fork locally

```bash
git clone https://github.com/your-username/planning-tool.git
```

3. 🌿 Create a feature branch

```bash
git branch feature/your-feature-name
```

4. 🔄 Switch to the branch

```bash
git switch feature/your-feature-name
```

---

## 🛠️ Project Setup

This is a Java Spring Boot project using MySQL.

### ✅ Requirements

- ☕ Java 17+ installed
- 📦 Maven installed
- 🗄️ MySQL running locally

---

## 📏 Code Rules

### 🗂️ Repository Naming Conventions
Use the following naming patterns:

- `INSERT`
- `DELETE`
- `UPDATE`
- `FIND`

### ⚙️ Service Naming Conventions
Use the following naming patterns:

- `CREATE`
- `REMOVE`
- `EDIT`
- `GET`

### 🌐 Controller Naming Rules
If a `GET` and `POST` mapping belong together as a pair, they must share the same name.

---

## 🧪 Testing Requirements

Before creating a pull request, make sure you have written the necessary tests for all newly created methods:

- ✅ H2 integration tests
- ✅ Service unit tests
- ✅ Controller web-layer slice tests

---

## 🔍 Pull Request Rules

- 👥 Do NOT merge a pull request without assigning reviewers
- ✅ Wait for approval before merging
- 🧹 Ensure your code follows the project structure and naming conventions
