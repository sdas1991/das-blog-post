package com.das.portfolioservice.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "projects")
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String,

    @Column(columnDefinition = "TEXT")
    var summary: String? = null,

    var imageUrl: String? = null,

    @ElementCollection
    @CollectionTable(name = "project_images", joinColumns = [JoinColumn(name = "project_id")])
    @Column(name = "image_url")
    var projectImages: MutableList<String> = mutableListOf(),

    var liveUrl: String? = null,
    var githubUrl: String? = null,
    var featured: Boolean = false,

    @ElementCollection
    @CollectionTable(name = "project_technologies", joinColumns = [JoinColumn(name = "project_id")])
    @Column(name = "technology")
    var technologies: MutableList<String> = mutableListOf(),

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
