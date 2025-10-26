package com.todo.task.data.dto

import com.todo.task.data.Todo
import jakarta.validation.constraints.NotBlank

data class TodoDto(
    val id: Long? = null,
    @field:NotBlank(message = "Title is required")
    val title: String,
    val completed: Boolean = false
)

fun Todo.toDto() = TodoDto(
    id = id,
    title = title,
    completed = completed
)

fun TodoDto.toEntity() = Todo(
    id = id,
    title = title,
    completed = completed
)
