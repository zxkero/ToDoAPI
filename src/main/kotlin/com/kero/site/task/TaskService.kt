package com.kero.site.task

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Service
class TaskService {

    companion object {
        private val logger = LoggerFactory.getLogger(TaskService::class.java)
    }

    private val tasks = mutableListOf<Task>()
    private val idCounter = AtomicInteger()

    fun getAllTasks(): List<Task> {
        logger.info("Запрошен список всех задач")
        return tasks
    }

    fun getTaskById(id:Int): Task? {
        logger.info("Запрошена задача с id=$id")
        // Используем firstOrNull для поиска первого элемента с таким ид
        return tasks.firstOrNull { it.id == id }
    }

    fun addTask(title: String): Task {
        val newTask = Task(
            id = idCounter.incrementAndGet(),
            title = title)
        tasks.add(newTask)
        logger.info("Создана новая задача: id=${newTask.id}, title=${newTask.title}")
        return newTask
    }

    fun updateTask(id: Int, title: String, isCompleted: Boolean): Task? {
        // Ищем задачу, которую нужно обновить
        val taskToUpdate = tasks.firstOrNull { it.id == id } ?: return null

        val taskIndex = tasks.indexOf(taskToUpdate)

        // Обновленная копия
        val updatedTask = taskToUpdate.copy(title = title, isCompleted = isCompleted)

        // Создание обновленной копии
        tasks[taskIndex] = updatedTask

        logger.info("Обновлена задача id=${updatedTask.id}, title=${updatedTask.title}, статус = ${updatedTask.isCompleted}")

        return updatedTask
    }

    fun deleteTask(id: Int): Boolean {
        val taskToDelete = tasks.firstOrNull { it.id == id }
        if (taskToDelete != null) {
            val isDeleted = tasks.removeIf { it.id == id }
            if (isDeleted) {
                logger.info("Удалена задача: id=${taskToDelete.id}, title='${taskToDelete.title}'")
            }
            return isDeleted
        }
        // Если задача не найдена
        logger.warn("Попытка удаления несуществующей задачи с id=$id")
        return false
    }
}