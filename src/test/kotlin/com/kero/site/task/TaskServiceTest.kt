package com.kero.site.task

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class TaskServiceTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var taskService: TaskService

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        taskService = TaskService(taskRepository)
    }

    @Test
    fun `addTask should save and return a new task`() {
        // Arrange
        val taskTitle = "Изучить MockK"
        val expectedSavedTask = Task(id = 1, title = taskTitle, isCompleted = false)
        every { taskRepository.save(any()) } returns expectedSavedTask

        // Act
        val result = taskService.addTask(taskTitle)

        // Assert
        assertEquals(expectedSavedTask, result)
        verify { taskRepository.save(any<Task>()) }
    }

    @Test
    fun `updateTask should change task details if task exists`() {
        // Arrange
        val taskId = 1
        val originalTask = Task(id = taskId, title = "Старое название", isCompleted = false)
        val expectedUpdatedTask = Task(id = taskId, title = "Новое название", isCompleted = true)

        every { taskRepository.findById(taskId) } returns Optional.of(originalTask)
        every { taskRepository.save(any()) } returns expectedUpdatedTask

        // Act
        val result = taskService.updateTask(taskId, "Новое название", true)

        // Assert
        assertEquals(expectedUpdatedTask, result)
    }

    @Test
    fun `updateTask should return null if task does not exist`() {
        // Arrange
        val nonExistentId = 999
        every { taskRepository.findById(nonExistentId) } returns Optional.empty()

        // Act
        val result = taskService.updateTask(nonExistentId, "Что-то", true)

        // Assert
        assertNull(result)
        verify(exactly = 0) { taskRepository.save(any()) }
    }

    @Test
    fun `deleteTask should return true if task exists`() {
        // Arrange
        val taskId = 1
        every { taskRepository.existsById(taskId) } returns true

        // Act
        val result = taskService.deleteTask(taskId)

        // Assert
        assertTrue(result)
        verify { taskRepository.deleteById(taskId) }
    }

    @Test
    fun `deleteTask should return false if task does not exist`() {
        // Arrange
        val nonExistentId = 999
        every { taskRepository.existsById(nonExistentId) } returns false

        // Act
        val result = taskService.deleteTask(nonExistentId)

        // Assert
        assertFalse(result)
        verify(exactly = 0) { taskRepository.deleteById(any()) }
    }
}