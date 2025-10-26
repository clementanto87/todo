package com.todo.task.service

import com.todo.task.data.Todo
import com.todo.task.exception.TodoNotFoundException
import com.todo.task.repository.TodoRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TodoService(
    private val todoRepository: TodoRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun getAllTodos(): List<Todo> {
        logger.debug("Fetching all todos")
        return todoRepository.findAll()
    }

    fun getTodoById(id: Long): Todo? {
        logger.debug("Fetching todo with id: $id")
        return todoRepository.findById(id).orElseThrow { TodoNotFoundException(id) }
    }

    @Transactional
    fun createTodo(todo: Todo): Todo {
        validateTodo(todo)
        logger.info("Creating new todo with title: '{}'", todo.title)
        return todoRepository.save(todo)
    }

    @Transactional
    fun updateTodo(id: Long, updatedTodo: Todo): Todo? {
        validateTodo(updatedTodo)

        val existingTodo = todoRepository.findById(id).orElse(null) ?: run {
            logger.warn("Update failed: todo with id $id not found")
            return null
        }

        val updatedEntity = existingTodo.copy(
            title = updatedTodo.title,
            completed = updatedTodo.completed
        )

        logger.info("Updating todo with id: $id")
        return todoRepository.save(updatedEntity)
    }

    @Transactional
    fun deleteTodoById(id: Long): Boolean {
        return if (todoRepository.existsById(id)) {
            logger.info("Deleting todo with id: $id")
            todoRepository.deleteById(id)
            true
        } else {
            logger.debug("Delete ignored: todo with id $id does not exist")
            false
        }
    }

    private fun validateTodo(todo: Todo) {
        if (todo.title.isNullOrBlank()) {
            throw IllegalArgumentException("Todo title cannot be blank")
        }
    }
}