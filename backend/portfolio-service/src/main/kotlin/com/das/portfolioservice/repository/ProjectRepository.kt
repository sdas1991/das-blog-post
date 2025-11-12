package com.das.portfolioservice.repository

import com.das.portfolioservice.model.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : JpaRepository<Project, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<Project>
}
