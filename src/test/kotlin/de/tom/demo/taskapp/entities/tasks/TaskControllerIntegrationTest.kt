package de.tom.demo.taskapp.entities.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.tom.demo.taskapp.config.DataConfiguration
import de.tom.demo.taskapp.entities.Task
import de.tom.demo.taskapp.entities.projects.ProjectService
import de.tom.demo.taskapp.entities.users.UserService
import io.mockk.InternalPlatformDsl.toStr
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate
import kotlin.random.Random


/**
 * Integration tests of the TaskController
 * using the TestRestTemplate of the Spring Boot Test Framework
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerIntegrationTest(@Autowired val client: TestRestTemplate,
                                    @LocalServerPort val port: Int, @Autowired val objectMapper: ObjectMapper,
                                    @Autowired val service: TaskService) {
    val endpoint = "/api/tasks"

    @BeforeAll
    fun setUp() {
        val list: List<Task> = service.getTasks()
        list.map { println(it) }
    }

    @AfterAll
    fun setDown() {
        val list: List<Task> = service.getTasks()
        list.map { println(it) }
    }

    @Test
    @DisplayName("integration test for GET /api/tasks")
    fun getAll() {
        val initialTasks: List<Task> = service.getTasks()
        val url = "http://localhost:${port}$endpoint/"

        val response = client.getForEntity<String>(url)
        val result: List<Task> = objectMapper.readValue(response.body.toStr())

        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(result).hasSize(initialTasks.size)
    }

    @Test
    @DisplayName("integration test for GET /api/tasks/{id}")
    fun getById() {
        val initialTasks: List<Task> = service.getTasks()
        val testTask: Task = initialTasks[Random.nextInt(0, initialTasks.size)]
        val url = "http://localhost:${port}$endpoint/${testTask.id}/"

        val response = client.getForEntity<String>(url)
        val result: Task = objectMapper.readValue(response.body.toStr())

        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(result.text).isEqualTo(testTask.text)
    }

    @Test
    @DisplayName("integration test for POST /api/tasks/")
    fun post() {
        val initialTasks: List<Task> = service.getTasks()
        val url = "http://localhost:${port}$endpoint/"

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val params = "?text=New Task&day=2022-03-01&reminder=true&reportedByEmail=${DataConfiguration().johnDoe.email}&projectName=${DataConfiguration().project.name}"
        val request = HttpEntity<String>(headers)

        val response: ResponseEntity<String> = client.postForEntity(url + params, request, String::class.java)
        val result: Task = objectMapper.readValue(response.body.toStr())
        val updatedTasks: List<Task> = service.getTasks()

        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        Assertions.assertThat(result.id).isNotEmpty()
        Assertions.assertThat(result.text).isEqualTo("New Task")
        Assertions.assertThat(updatedTasks.size).isEqualTo(initialTasks.size + 1)
    }

    @Test
    @DisplayName("integration test for DELETE /tasks/{id}")
    fun delete() {
        val initialTasks: List<Task> = service.getTasks()
        val testTask: Task = initialTasks[Random.nextInt(0, initialTasks.size)]
        val url = "http://localhost:${port}$endpoint/${testTask.id}"

        client.delete(url)
        val updatedTasks: List<Task> = service.getTasks()

        Assertions.assertThat(updatedTasks.size).isEqualTo(initialTasks.size - 1)
        val entity = client.getForEntity<String>(url)
        Assertions.assertThat(entity.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    @DisplayName("integration test for PUT /api/tasks/")
    fun put() {
        val initialTasks: List<Task> = service.getTasks()
        val testTask: Task = initialTasks[Random.nextInt(0, initialTasks.size)]
        val url = "http://localhost:${port}$endpoint/${testTask.id}"

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val params = "?text=Updated Task&day=2022-03-01&reminder=false"
        val request = HttpEntity<String>(headers)
        val response: ResponseEntity<String> = client.exchange(url + params, HttpMethod.PUT, request, String::class.java)

        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val result: Task = objectMapper.readValue(response.body.toStr())
        Assertions.assertThat(result.id).isEqualTo(testTask.id)
        Assertions.assertThat(result.text).isEqualTo("Updated Task")

    }
}