package com.das.portfolioservice.repository

import com.das.portfolioservice.model.Contact
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactRepository : JpaRepository<Contact, Long>
