package com.das.portfolioservice.controller

import com.das.portfolioservice.model.Skill
import com.das.portfolioservice.service.SkillService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/portfolio/skills")
@CrossOrigin(origins = ["*"])
class SkillController(private val skillService: SkillService) {

    @GetMapping
    fun getAllSkills(): List<Skill> {
        return skillService.getAllSkills()
    }

    @GetMapping("/{id}")
    fun getSkillById(@PathVariable id: Long): ResponseEntity<Skill> {
        val skill = skillService.getSkillById(id)
        return if (skill != null) {
            ResponseEntity.ok(skill)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/category/{category}")
    fun getSkillsByCategory(@PathVariable category: String): List<Skill> {
        return skillService.getSkillsByCategory(category)
    }

    @PostMapping
    fun createSkill(@RequestBody skill: Skill): ResponseEntity<Skill> {
        val createdSkill = skillService.createSkill(skill)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSkill)
    }

    @PutMapping("/{id}")
    fun updateSkill(@PathVariable id: Long, @RequestBody skill: Skill): ResponseEntity<Skill> {
        val updatedSkill = skillService.updateSkill(id, skill)
        return if (updatedSkill != null) {
            ResponseEntity.ok(updatedSkill)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteSkill(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        return if (skillService.deleteSkill(id)) {
            ResponseEntity.ok(mapOf("message" to "Skill deleted successfully"))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
