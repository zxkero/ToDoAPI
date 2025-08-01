package com.kero.site.task

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TaskServiceTest {

    @Test
    fun `addTask should create and return a new task`() {
        // 1. Arrange (Подготовка)
        val taskService = TaskService() // Реальный экземпляр
        val taskTitle = "Изучить юнит-тесты"

        // 2. Act (Действие)
        val createdTask = taskService.addTask(taskTitle)

        // 3. Assert (Проверка)
        assertNotNull(createdTask) // Проверяем, что задача не null
        assertEquals(1, createdTask.id) // Проврека, что ид первой задачи равен 1
        assertEquals(taskTitle, createdTask.title) // Проверка, что название совпадает
        assertFalse(createdTask.isCompleted) // Проверка, что задача не выполнена сразу
    }

    @Test
    fun `addTask should add a new task to the list`() {
        val taskService = TaskService()

        val task1 = taskService.addTask("Test Task")

        assertNotNull(taskService.getAllTasks())
        assertEquals(1, taskService.getAllTasks().size)
        assertEquals("Test Task", taskService.getAllTasks()[0].title)

    }

    @Test
    fun `updateTask should change task details if task exists`() {
        val taskService = TaskService()
        val originalTask = taskService.addTask("Старое название")

        val newTitle = "Новое название"
        val newStatus = true

        val updatedTask = taskService.updateTask(originalTask.id, newTitle, newStatus)

        assertNotNull(updatedTask)
        assertEquals(newTitle, updatedTask?.title)
        assertEquals(newStatus, updatedTask?.isCompleted)

        val taskFromService = taskService.getTaskById(originalTask.id)
        assertEquals(newTitle, taskFromService?.title)
        assertEquals(newStatus, taskFromService?.isCompleted)
    }

    @Test
    fun `updateTask should return null if task does not exist`() {
        val taskService = TaskService()

        val tryUpdate = taskService.updateTask(999, "Обновленная задача", true)
        assertNull(tryUpdate)
    }


    @Test
    fun `getTaskById should return task if it exists`() {
        val taskService = TaskService()
        val task1 = taskService.addTask("Сварить кашу")
        val expectedTask = taskService.addTask("Купить колбасу")
        val notExistentId = 999

        val foundTask = taskService.getTaskById(expectedTask.id)
        val notFoundTask = taskService.getTaskById(notExistentId)

        assertNotNull(foundTask)
        assertEquals(expectedTask, foundTask)
        assertNull(notFoundTask)
    }

    @Test
    fun `getAllTasks should return all tasks if it exists`() {
        val taskService = TaskService()
        val task1 = taskService.addTask("Задача 1")
        val task2 = taskService.addTask("Задача 2")

        val res = taskService.getAllTasks()

        assertNotNull(res)
        assertEquals(2, res.size)
    }

    @Test
    fun `getAllTasks should return an empty list when no tasks have been added`() {
        val taskService = TaskService()

        val res = taskService.getAllTasks()

        assertNotNull(res)
        assertTrue(res.isEmpty())
    }

    @Test
    fun `deleteTask should remove task if it exists`() {
        val taskService = TaskService()
        val task1 = taskService.addTask("zero task")

        val wasDeleted = taskService.deleteTask(task1.id)
        val resultAfterDeletion = taskService.getTaskById(task1.id)

        assertTrue(wasDeleted)
        assertNull(resultAfterDeletion)
    }

    @Test
    fun `deleteTask should return false if task does not exist`() {
        val taskService = TaskService()
        val notExistentId = 999

        val res = taskService.deleteTask(notExistentId)

        assertFalse(res)
    }


}