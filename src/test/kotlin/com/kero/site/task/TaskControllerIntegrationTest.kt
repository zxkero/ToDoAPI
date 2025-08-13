// Не все покрыто тестами, т.к лень)))
package com.kero.site.task

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper


    companion object {
        @Container
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:16-alpine").apply {
            withDatabaseName("testdb")
            withUsername("testuser")
            withPassword("testpass")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.password", postgresContainer::getPassword)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
        }
    }

    @AfterEach
    fun cleanup() {
        taskRepository.deleteAll()
    }


    @Test
    fun `should create a new task when calling POST tasks`() {
        // Arrange
        val taskDto = CreateTaskDto(title = "Изучить Testcontainers", description = "Описание")

        // Act
        mockMvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(taskDto)
        }.andExpect {
            // Assert (проверка HTTP-ответа)
            status { isOk() }
            jsonPath("$.title") { value(taskDto.title) }
            jsonPath("$.description") { value(taskDto.description) }
            jsonPath("$.isCompleted") { value(false) }
            jsonPath("$.id") { exists() }
        }

        // Assert (проверка состояния базы данных)
        val tasksInDb = taskRepository.findAll()
        assertEquals(1, tasksInDb.size)
        assertEquals(taskDto.title, tasksInDb[0].title)
        assertEquals(taskDto.description, tasksInDb[0].description)
    }

    @Test
    fun `should return all tasks when calling GET tasks`() {
        // Arrange
        taskRepository.saveAll(listOf(
            Task(title = "Первая задача"),
            Task(title = "Вторая задача")
        ))

        // Act & Assert
        mockMvc.get("/tasks") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(2) }
            jsonPath("$[0].title") { value("Первая задача") }
            jsonPath("$[1].title") { value("Вторая задача") }
        }
    }

    @Test
    fun `should return task by id when task exists`() {
        // Arrange
        val savedTask = taskRepository.save(Task(title = "Задача для поиска"))
        val savedTaskId = savedTask.id!!

        // Act & Assert
        mockMvc.get("/tasks/$savedTaskId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(savedTaskId) }
            jsonPath("$.title") { value(savedTask.title) }
        }
    }

    @Test
    fun `should return 404 when getting task by id that does not exist`() {
        // Arrange
        val nonExistentId = 999

        // Act & Assert
        mockMvc.get("/tasks/$nonExistentId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `should update task when calling PUT tasks with valid data`() {
        // Arrange
        val savedTask = taskRepository.save(Task(title = "Старое название", description = "Старое описание"))
        val savedTaskId = savedTask.id!!
        val updateDto = UpdateTaskDto(title = "Новое название", description = "Новое описание", isCompleted = true)

        // Act
        mockMvc.put("/tasks/$savedTaskId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateDto)
        }.andExpect {
            // Assert (проверка HTTP-ответа)
            status { isOk() }
            jsonPath("$.id") { value(savedTaskId) }
            jsonPath("$.title") { value(updateDto.title) }
            jsonPath("$.description") { value(updateDto.description) }
            jsonPath("$.isCompleted") { value(updateDto.isCompleted) }
        }

        // Assert (проверка состояния базы данных)
        val updatedTask = taskRepository.findByIdOrNull(savedTaskId)
        assertEquals(updateDto.title, updatedTask?.title)
        assertEquals(updateDto.description, updatedTask?.description)
        assertEquals(updateDto.isCompleted, updatedTask?.isCompleted)
    }

    @Test
    fun `should return 404 when updating a task that does not exist`() {
        // Arrange
        val nonExistentId = 999
        val updateDto = UpdateTaskDto(title = "Новое название", description = "Новое описание", isCompleted = true)

        // Act & Assert
        mockMvc.put("/tasks/$nonExistentId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateDto)
        }.andExpect {
            status { isNotFound() }
        }
    }


    @Test
    fun `should delete task by id`() {
        // Arrange
        val savedTask = taskRepository.save(Task(title = "Задача на удаление"))
        val savedTaskId  = savedTask.id!!

        // Act
        mockMvc.delete("/tasks/$savedTaskId") {
        }.andExpect {
            // Assert
            status { isNoContent() }
        }

        // Assert
        val taskExists = taskRepository.existsById(savedTaskId)
        assertFalse(taskExists, "Задача с id=$savedTaskId должна была быть удалена, но осталась в базе.")


    }
}