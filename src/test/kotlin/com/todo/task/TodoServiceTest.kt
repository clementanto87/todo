package com.todo.task.service

import com.todo.task.data.Todo
import com.todo.task.exception.TodoNotFoundException
import com.todo.task.repository.TodoRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class TodoServiceTest {

    @Mock
    private lateinit var todoRepository: TodoRepository

    @InjectMocks
    private lateinit var todoService: TodoService

    // Helper function for Kotlin nullability
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    @Test
    fun `getAllTodos should return all todos from repository`() {
        // Given
        val todos = listOf(
            Todo(id = 1L, title = "Task 1", completed = false),
            Todo(id = 2L, title = "Task 2", completed = true),
            Todo(id = 3L, title = "Task 3", completed = false)
        )
        `when`(todoRepository.findAll()).thenReturn(todos)

        // When
        val result = todoService.getAllTodos()

        // Then
        assertEquals(3, result.size)
        assertEquals(todos, result)
        verify(todoRepository, times(1)).findAll()
    }

    @Test
    fun `getAllTodos should return empty list when no todos exist`() {
        // Given
        `when`(todoRepository.findAll()).thenReturn(emptyList())

        // When
        val result = todoService.getAllTodos()

        // Then
        assertTrue(result.isEmpty())
        verify(todoRepository, times(1)).findAll()
    }

    @Test
    fun `getTodoById should return todo when it exists`() {
        // Given
        val todo = Todo(id = 1L, title = "Test Task", completed = false)
        `when`(todoRepository.findById(1L)).thenReturn(Optional.of(todo))

        // When
        val result = todoService.getTodoById(1L)

        // Then
        assertNotNull(result)
        assertEquals(1L, result?.id)
        assertEquals("Test Task", result?.title)
        assertEquals(false, result?.completed)
        verify(todoRepository, times(1)).findById(1L)
    }

    @Test
    fun `getTodoById should throw TodoNotFoundException when todo does not exist`() {
        // Given
        `when`(todoRepository.findById(999L)).thenReturn(Optional.empty())

        // When & Then
        val exception = assertThrows<TodoNotFoundException> {
            todoService.getTodoById(999L)
        }

        assertEquals("Todo with ID 999 not found", exception.message)
        verify(todoRepository, times(1)).findById(999L)
    }

    @Test
    fun `createTodo should save and return todo`() {
        // Given
        val newTodo = Todo(title = "New Task", completed = false)
        val savedTodo = Todo(id = 1L, title = "New Task", completed = false)
        `when`(todoRepository.save(anyObject())).thenReturn(savedTodo)

        // When
        val result = todoService.createTodo(newTodo)

        // Then
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("New Task", result.title)
        assertEquals(false, result.completed)
        verify(todoRepository, times(1)).save(anyObject())
    }

    @Test
    fun `createTodo should throw exception when title is blank`() {
        // Given
        val invalidTodo = Todo(title = "", completed = false)

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            todoService.createTodo(invalidTodo)
        }

        assertEquals("Todo title cannot be blank", exception.message)
        verify(todoRepository, never()).save(anyObject())
    }

    @Test
    fun `createTodo should throw exception when title is whitespace only`() {
        // Given
        val invalidTodo = Todo(title = "   ", completed = false)

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            todoService.createTodo(invalidTodo)
        }

        assertEquals("Todo title cannot be blank", exception.message)
        verify(todoRepository, never()).save(anyObject())
    }

    @Test
    fun `updateTodo should update and return todo when it exists`() {
        // Given
        val existingTodo = Todo(id = 1L, title = "Old Title", completed = false)
        val updatedTodo = Todo(title = "Updated Title", completed = true)
        val savedTodo = Todo(id = 1L, title = "Updated Title", completed = true)

        `when`(todoRepository.findById(1L)).thenReturn(Optional.of(existingTodo))
        `when`(todoRepository.save(anyObject())).thenReturn(savedTodo)

        // When
        val result = todoService.updateTodo(1L, updatedTodo)

        // Then
        assertNotNull(result)
        assertEquals(1L, result?.id)
        assertEquals("Updated Title", result?.title)
        assertEquals(true, result?.completed)
        verify(todoRepository, times(1)).findById(1L)
        verify(todoRepository, times(1)).save(anyObject())
    }

    @Test
    fun `updateTodo should return null when todo does not exist`() {
        // Given
        val updatedTodo = Todo(title = "Updated Title", completed = true)
        `when`(todoRepository.findById(999L)).thenReturn(Optional.empty())

        // When
        val result = todoService.updateTodo(999L, updatedTodo)

        // Then
        assertNull(result)
        verify(todoRepository, times(1)).findById(999L)
        verify(todoRepository, never()).save(anyObject())
    }

    @Test
    fun `updateTodo should throw exception when title is blank`() {
        // Given
        val invalidTodo = Todo(title = "", completed = false)

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            todoService.updateTodo(1L, invalidTodo)
        }

        assertEquals("Todo title cannot be blank", exception.message)
        verify(todoRepository, never()).findById(anyLong())
        verify(todoRepository, never()).save(anyObject())
    }

    @Test
    fun `updateTodo should preserve id of existing todo`() {
        // Given
        val existingTodo = Todo(id = 1L, title = "Old Title", completed = false)
        val updatedTodo = Todo(id = 999L, title = "Updated Title", completed = true) // Different ID
        val savedTodo = Todo(id = 1L, title = "Updated Title", completed = true) // Should keep original ID

        `when`(todoRepository.findById(1L)).thenReturn(Optional.of(existingTodo))
        `when`(todoRepository.save(anyObject())).thenReturn(savedTodo)

        // When
        val result = todoService.updateTodo(1L, updatedTodo)

        // Then
        assertNotNull(result)
        assertEquals(1L, result?.id) // Should be original ID, not the one from updatedTodo
        assertEquals("Updated Title", result?.title)
        verify(todoRepository, times(1)).findById(1L)
        verify(todoRepository, times(1)).save(anyObject())
    }

    @Test
    fun `deleteTodoById should return true when todo exists and is deleted`() {
        // Given
        `when`(todoRepository.existsById(1L)).thenReturn(true)
        doNothing().`when`(todoRepository).deleteById(1L)

        // When
        val result = todoService.deleteTodoById(1L)

        // Then
        assertTrue(result)
        verify(todoRepository, times(1)).existsById(1L)
        verify(todoRepository, times(1)).deleteById(1L)
    }

    @Test
    fun `deleteTodoById should return false when todo does not exist`() {
        // Given
        `when`(todoRepository.existsById(999L)).thenReturn(false)

        // When
        val result = todoService.deleteTodoById(999L)

        // Then
        assertFalse(result)
        verify(todoRepository, times(1)).existsById(999L)
        verify(todoRepository, never()).deleteById(999L)
    }

    @Test
    fun `createTodo should accept todo with completed true`() {
        // Given
        val newTodo = Todo(title = "Completed Task", completed = true)
        val savedTodo = Todo(id = 1L, title = "Completed Task", completed = true)
        `when`(todoRepository.save(anyObject())).thenReturn(savedTodo)

        // When
        val result = todoService.createTodo(newTodo)

        // Then
        assertNotNull(result)
        assertEquals(true, result.completed)
        verify(todoRepository, times(1)).save(anyObject())
    }

    @Test
    fun `updateTodo should handle partial updates correctly`() {
        // Given
        val existingTodo = Todo(id = 1L, title = "Original Title", completed = false)
        val partialUpdate = Todo(title = "Original Title", completed = true) // Only changing completed
        val savedTodo = Todo(id = 1L, title = "Original Title", completed = true)

        `when`(todoRepository.findById(1L)).thenReturn(Optional.of(existingTodo))
        `when`(todoRepository.save(anyObject())).thenReturn(savedTodo)

        // When
        val result = todoService.updateTodo(1L, partialUpdate)

        // Then
        assertNotNull(result)
        assertEquals("Original Title", result?.title)
        assertEquals(true, result?.completed)
        verify(todoRepository, times(1)).findById(1L)
        verify(todoRepository, times(1)).save(anyObject())
    }
}