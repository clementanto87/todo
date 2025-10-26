package com.todo.task.controller

import com.todo.task.data.dto.TodoDto
import com.todo.task.data.dto.toDto
import com.todo.task.data.dto.toEntity
import com.todo.task.service.TodoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/todos")
class TodoController(
    private val todoService: TodoService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Get all todos")
    @GetMapping
    fun getAllTodos(): List<TodoDto> {
        logger.debug("Fetching all todos")
        return todoService.getAllTodos().map { it.toDto() }
    }

    @Operation(summary = "Get a todo by ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Found the todo"),
            ApiResponse(responseCode = "404", description = "Todo not found")
        ]
    )
    @GetMapping("/{id}")
    fun getTodoById(@PathVariable id: Long): ResponseEntity<TodoDto> {
        logger.debug("Fetching todo with ID: $id")
        val todo = todoService.getTodoById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(todo.toDto())
    }

    @Operation(summary = "Create a new todo")
    @PostMapping
    fun createTodo(@Valid @RequestBody todoDto: TodoDto): ResponseEntity<TodoDto> {
        logger.debug("Creating new todo: $todoDto")
        val savedTodo = todoService.createTodo(todoDto.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTodo.toDto())
    }

    @Operation(summary = "Update an existing todo")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Todo updated successfully"),
            ApiResponse(responseCode = "404", description = "Todo not found")
        ]
    )
    @PutMapping("/{id}")
    fun updateTodo(
        @PathVariable id: Long,
        @Valid @RequestBody todoDto: TodoDto
    ): ResponseEntity<TodoDto> {
        logger.debug("Updating todo with ID: $id")
        val updatedTodo = todoService.updateTodo(id, todoDto.toEntity()) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(updatedTodo.toDto())
    }

    @Operation(summary = "Delete a todo by ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Todo deleted successfully"),
            ApiResponse(responseCode = "404", description = "Todo not found")
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteTodoById(@PathVariable id: Long): ResponseEntity<Unit> {
        logger.debug("Deleting todo with ID: $id")
        val deleted = todoService.deleteTodoById(id)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}