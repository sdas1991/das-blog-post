package com.das.portfolioservice.model

import jakarta.persistence.*

@Entity
@Table(name = "skills")
data class Skill(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var category: String,

    @Column(nullable = false)
    var level: Int, // 0-100

    var description: String? = null
)
