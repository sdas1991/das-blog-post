package com.das.portfolioservice.controller

import com.das.portfolioservice.model.Project
import com.das.portfolioservice.service.ProjectService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/portfolio/projects")
@CrossOrigin(origins = ["*"])
class ProjectController(private val projectService: ProjectService) {

    @GetMapping
    fun getAllProjects(): List<Project> {
        return projectService.getAllProjects()
    }

    @GetMapping("/{id}")
    fun getProjectById(@PathVariable id: Long): ResponseEntity<Project> {
        val project = projectService.getProjectById(id)
        return if (project != null) {
            ResponseEntity.ok(project)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createProject(@RequestBody project: Project): ResponseEntity<Project> {
        val createdProject = projectService.createProject(project)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject)
    }

    @PutMapping("/{id}")
    fun updateProject(@PathVariable id: Long, @RequestBody project: Project): ResponseEntity<Project> {
        val updatedProject = projectService.updateProject(id, project)
        return if (updatedProject != null) {
            ResponseEntity.ok(updatedProject)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteProject(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        return if (projectService.deleteProject(id)) {
            ResponseEntity.ok(mapOf("message" to "Project deleted successfully"))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
