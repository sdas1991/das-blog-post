package com.das.portfolioservice.service

import com.das.portfolioservice.model.Project
import com.das.portfolioservice.repository.ProjectRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProjectService(private val projectRepository: ProjectRepository) {

    fun getAllProjects(): List<Project> {
        return projectRepository.findAllByOrderByCreatedAtDesc()
    }

    fun getProjectById(id: Long): Project? {
        return projectRepository.findById(id).orElse(null)
    }

    fun createProject(project: Project): Project {
        return projectRepository.save(project)
    }

    fun updateProject(id: Long, updatedProject: Project): Project? {
        return projectRepository.findById(id).map { existingProject ->
            existingProject.title = updatedProject.title
            existingProject.description = updatedProject.description
            existingProject.imageUrl = updatedProject.imageUrl
            existingProject.liveUrl = updatedProject.liveUrl
            existingProject.githubUrl = updatedProject.githubUrl
            existingProject.technologies = updatedProject.technologies
            existingProject.updatedAt = LocalDateTime.now()
            projectRepository.save(existingProject)
        }.orElse(null)
    }

    fun deleteProject(id: Long): Boolean {
        return if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
