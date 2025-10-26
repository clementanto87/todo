package com.todo.task.exception

class TodoNotFoundException(id: Long) : RuntimeException("Todo with ID $id not found")
