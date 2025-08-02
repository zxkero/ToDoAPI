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
import org.springframework.test.web.servlet.post
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertFalse

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
        val taskDto = CreateTaskDto(title = "Изучить Testcontainers")

        // Act
        mockMvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(taskDto)
        }.andExpect {
            // Assert (проверка HTTP-ответа)
            status { isOk() }
            jsonPath("$.title") { value(taskDto.title) }
            jsonPath("$.isCompleted") { value(false) }
            jsonPath("$.id") { exists() }
        }

        // Assert (проверка состояния базы данных)
        val tasksInDb = taskRepository.findAll()
        assertEquals(1, tasksInDb.size)
        assertEquals(taskDto.title, tasksInDb[0].title)
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