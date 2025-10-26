# Todo Task Manager API

A simple, RESTful Todo API built with **Spring Boot** and **Kotlin**, designed for managing todo tasks with full CRUD operations. This service follows modern best practices including validation, structured logging, OpenAPI documentation, and containerization.

---

## 🚀 Features

- **Full CRUD operations** for todo items
- **Request validation** using Bean Validation (`@Valid`)
- **Structured JSON logging** with SLF4J
- **OpenAPI 3 (Swagger) documentation** via `springdoc-openapi`
- **Dockerized** for easy deployment
- **Clean architecture** with separation of concerns (Controller ↔ Service ↔ Data)

---

## 🛠️ Tech Stack

- **Language**: Kotlin
- **Framework**: Spring Boot 3.x
- **Build Tool**: Gradle
- **API Documentation**: OpenAPI 3 / Swagger UI
- **Validation**: Jakarta Bean Validation
- **Logging**: SLF4J with Logback
- **Containerization**: Docker (multi-stage build)

---

## 📦 Endpoints

| Method | Path                | Description                     | Status Codes       |
|--------|---------------------|----------------------------------|--------------------|
| GET    | `/api/v1/todos`     | Get all todos                   | `200 OK`           |
| GET    | `/api/v1/todos/{id}`| Get a todo by ID                | `200 OK`, `404 Not Found` |
| POST   | `/api/v1/todos`     | Create a new todo               | `201 Created`      |
| PUT    | `/api/v1/todos/{id}`| Update an existing todo         | `200 OK`, `404 Not Found` |
| DELETE | `/api/v1/todos/{id}`| Delete a todo                   | `204 No Content`, `404 Not Found` |

> 💡 All request/response bodies use JSON format.  
> Example todo:
> ```json
> {
>   "title": "Buy groceries",
>   "completed": false
> }
> ```

---

## 🐳 Running with Docker

### Build the image
```bash
docker build -t todo-api .
```

### Run the container
```bash
docker run -p 8081:8081 todo-api
```

The application will be available at:  
👉 http://localhost:8081

### View API Documentation (Swagger UI)
Once running, visit:  
👉 http://localhost:8081/swagger-ui.html

---

## 🧪 Testing

Unit tests are included for the controller layer using JUnit 5 and MockMvc.

Run tests locally:
```bash
./gradlew test
```

---

## 📁 Project Structure

```
src/
├── main/
│   ├── kotlin/com/todo/task/
│   │   ├── controller/    # REST controllers
│   │   ├── service/       # Business logic
│   │   └── data/          # DTOs, entities, repositories
└── test/
    └── kotlin/            # Unit and integration tests
```

---

## 🏗️ Building Locally (without Docker)

Prerequisites: JDK 17, Gradle

```bash
./gradlew build
java -jar build/libs/*.jar
```

> The app runs on port **8081** by default (configured in `application.yml` or `application.properties`).

---

## 📜 License

This project is open-source and available under the [MIT License](LICENSE).

---

> ✨ **Tip**: Customize the application name, port, or persistence layer (e.g., add PostgreSQL) as needed for your use case.