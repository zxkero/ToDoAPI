package com.kero.site.task

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

// DTO для получения данных
data class CreateTaskDto(val title: String, val description: String)

// DTO для изменения задачи
data class UpdateTaskDto(val title: String, val description: String, val isCompleted: Boolean)

// DTO для получения задачи


@RestController
@CrossOrigin(origins = ["*"])
class TaskController(private val taskService: TaskService) {

    // Получение всех задач
    @GetMapping("/tasks")
    fun getTasks() : List<Task> = taskService.getAllTasks()

    // Получение задачи по ид
    @GetMapping("/tasks/{id}")
    fun getTaskById(@PathVariable id: Int): ResponseEntity<Task> {
        val task = taskService.getTaskById(id)
        return if (task != null) {
            ResponseEntity.ok(task)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // Создание задачи
    @PostMapping("/tasks")
    fun addTask(@RequestBody taskDto: CreateTaskDto): Task {
        return taskService.addTask(taskDto.title, taskDto.description)
    }

    // Изменение задачи
    @PutMapping("/tasks/{id}")
    fun updateTask(
        @PathVariable id: Int,
        @RequestBody taskDto: UpdateTaskDto
    ) : ResponseEntity<Task> {
        val updatedTask = taskService.updateTask(id, taskDto.title, taskDto.description, taskDto.isCompleted)

        return if (updatedTask != null) {
            ResponseEntity.ok(updatedTask)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // Удаление задачи
    @DeleteMapping("/tasks/{id}")
    fun deleteTask(@PathVariable id: Int): ResponseEntity<Void> {
        val isDeleted = taskService.deleteTask(id)

        return if (isDeleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

}