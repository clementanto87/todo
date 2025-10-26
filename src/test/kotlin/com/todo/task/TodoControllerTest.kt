package com.todo.task.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.todo.task.data.Todo
import com.todo.task.data.dto.TodoDto
import com.todo.task.service.TodoService
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(TodoController::class)
class TodoControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var todoService: TodoService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    // Helper function to handle Kotlin nullability with Mockito
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    @Test
    fun `getAllTodos should return list of todos`() {
        // Given
        val todos = listOf(
            Todo(id = 1L, title = "Task 1", completed = false),
            Todo(id = 2L, title = "Task 2", completed = true)
        )
        `when`(todoService.getAllTodos()).thenReturn(todos)

        // When & Then
        mockMvc.perform(get("/api/v1/todos"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("Task 1"))
            .andExpect(jsonPath("$[0].completed").value(false))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].title").value("Task 2"))
            .andExpect(jsonPath("$[1].completed").value(true))

        verify(todoService, times(1)).getAllTodos()
    }

    @Test
    fun `getAllTodos should return empty list when no todos exist`() {
        // Given
        `when`(todoService.getAllTodos()).thenReturn(emptyList())

        // When & Then
        mockMvc.perform(get("/api/v1/todos"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(0))

        verify(todoService, times(1)).getAllTodos()
    }

    @Test
    fun `getTodoById should return todo when it exists`() {
        // Given
        val todo = Todo(id = 1L, title = "Test Task", completed = false)
        `when`(todoService.getTodoById(1L)).thenReturn(todo)

        // When & Then
        mockMvc.perform(get("/api/v1/todos/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Task"))
            .andExpect(jsonPath("$.completed").value(false))

        verify(todoService, times(1)).getTodoById(1L)
    }

    @Test
    fun `getTodoById should return 404 when todo does not exist`() {
        // Given
        `when`(todoService.getTodoById(999L)).thenReturn(null)

        // When & Then
        mockMvc.perform(get("/api/v1/todos/999"))
            .andExpect(status().isNotFound)

        verify(todoService, times(1)).getTodoById(999L)
    }

    @Test
    fun `createTodo should create and return new todo`() {
        // Given
        val todoDto = TodoDto(title = "New Task", completed = false)
        val savedTodo = Todo(id = 1L, title = "New Task", completed = false)
        `when`(todoService.createTodo(anyObject())).thenReturn(savedTodo)

        // When & Then
        mockMvc.perform(
            post("/api/v1/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoDto))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("New Task"))
            .andExpect(jsonPath("$.completed").value(false))

        verify(todoService, times(1)).createTodo(anyObject())
    }

    @Test
    fun `createTodo should return 400 when title is blank`() {
        // Given
        val invalidTodoDto = TodoDto(title = "", completed = false)

        // When & Then
        mockMvc.perform(
            post("/api/v1/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTodoDto))
        )
            .andExpect(status().isBadRequest)

        verify(todoService, never()).createTodo(anyObject())
    }

    @Test
    fun `updateTodo should update and return todo when it exists`() {
        // Given
        val todoDto = TodoDto(id = 1L, title = "Updated Task", completed = true)
        val updatedTodo = Todo(id = 1L, title = "Updated Task", completed = true)
        `when`(todoService.updateTodo(eq(1L), anyObject())).thenReturn(updatedTodo)

        // When & Then
        mockMvc.perform(
            put("/api/v1/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoDto))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Updated Task"))
            .andExpect(jsonPath("$.completed").value(true))

        verify(todoService, times(1)).updateTodo(eq(1L), anyObject())
    }

    @Test
    fun `updateTodo should return 404 when todo does not exist`() {
        // Given
        val todoDto = TodoDto(id = 999L, title = "Updated Task", completed = true)
        `when`(todoService.updateTodo(eq(999L), anyObject())).thenReturn(null)

        // When & Then
        mockMvc.perform(
            put("/api/v1/todos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoDto))
        )
            .andExpect(status().isNotFound)

        verify(todoService, times(1)).updateTodo(eq(999L), anyObject())
    }

    @Test
    fun `updateTodo should return 400 when title is blank`() {
        // Given
        val invalidTodoDto = TodoDto(id = 1L, title = "", completed = false)

        // When & Then
        mockMvc.perform(
            put("/api/v1/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTodoDto))
        )
            .andExpect(status().isBadRequest)

        verify(todoService, never()).updateTodo(anyLong(), anyObject())
    }

    @Test
    fun `deleteTodoById should return 204 when todo is deleted successfully`() {
        // Given
        `when`(todoService.deleteTodoById(1L)).thenReturn(true)

        // When & Then
        mockMvc.perform(delete("/api/v1/todos/1"))
            .andExpect(status().isNoContent)

        verify(todoService, times(1)).deleteTodoById(1L)
    }

    @Test
    fun `deleteTodoById should return 404 when todo does not exist`() {
        // Given
        `when`(todoService.deleteTodoById(999L)).thenReturn(false)

        // When & Then
        mockMvc.perform(delete("/api/v1/todos/999"))
            .andExpect(status().isNotFound)

        verify(todoService, times(1)).deleteTodoById(999L)
    }

}