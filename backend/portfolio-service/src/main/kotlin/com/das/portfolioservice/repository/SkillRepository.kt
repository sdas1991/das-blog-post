package com.das.portfolioservice.repository

import com.das.portfolioservice.model.Skill
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SkillRepository : JpaRepository<Skill, Long> {
    fun findByCategory(category: String): List<Skill>
    fun findAllByOrderByCategoryAscLevelDesc(): List<Skill>
}
