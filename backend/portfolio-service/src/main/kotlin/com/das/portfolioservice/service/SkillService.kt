package com.das.portfolioservice.service

import com.das.portfolioservice.model.Skill
import com.das.portfolioservice.repository.SkillRepository
import org.springframework.stereotype.Service

@Service
class SkillService(private val skillRepository: SkillRepository) {

    fun getAllSkills(): List<Skill> {
        return skillRepository.findAllByOrderByCategoryAscLevelDesc()
    }

    fun getSkillById(id: Long): Skill? {
        return skillRepository.findById(id).orElse(null)
    }

    fun getSkillsByCategory(category: String): List<Skill> {
        return skillRepository.findByCategory(category)
    }

    fun createSkill(skill: Skill): Skill {
        return skillRepository.save(skill)
    }

    fun updateSkill(id: Long, updatedSkill: Skill): Skill? {
        return skillRepository.findById(id).map { existingSkill ->
            existingSkill.name = updatedSkill.name
            existingSkill.category = updatedSkill.category
            existingSkill.level = updatedSkill.level
            existingSkill.description = updatedSkill.description
            skillRepository.save(existingSkill)
        }.orElse(null)
    }

    fun deleteSkill(id: Long): Boolean {
        return if (skillRepository.existsById(id)) {
            skillRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
