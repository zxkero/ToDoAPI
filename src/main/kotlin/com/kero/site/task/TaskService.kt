package com.kero.site.task

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TaskService(private val repository: TaskRepository) {

    companion object {
        private val logger = LoggerFactory.getLogger(TaskService::class.java)
    }

    fun getAllTasks(): List<Task> {
        logger.info("Запрошен список всех задач")
        return repository.findAll()
    }

    fun getTaskById(id:Int): Task? {
        logger.info("Запрошена задача с id=$id")
        return repository.findById(id).orElse(null)
    }

    fun addTask(title: String): Task {
        val newTask = Task(title = title)
        val savedTask = repository.save(newTask)
        logger.info("Создана новая задача: $savedTask")
        return savedTask
    }

    fun updateTask(id: Int, title: String, isCompleted: Boolean): Task? {
        // проверка, существует ли задача
        val existingTask = repository.findById(id).orElse(null) ?: return null

        // обновленный объект с тем же ид
        val updatedTask = existingTask.copy(title = title, isCompleted = isCompleted)

        // сохранение
        val savedTask = repository.save(updatedTask)
        logger.info("Обновлена задача: $savedTask")
        return savedTask
    }

    fun deleteTask(id: Int): Boolean {
        // проверка, существует ли здача

        if(!repository.existsById(id)) {
            logger.warn("Попытка удаления несуществующей задачи с id=$id")
            return false
        }
        repository.deleteById(id)
        logger.info("Удалена задача с id=$id")
        return true
    }
}