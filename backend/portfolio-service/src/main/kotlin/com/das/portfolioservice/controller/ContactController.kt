package com.das.portfolioservice.controller

import com.das.portfolioservice.model.Contact
import com.das.portfolioservice.service.ContactService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/portfolio/contact")
@CrossOrigin(origins = ["*"])
class ContactController(private val contactService: ContactService) {

    @PostMapping
    fun submitContact(@RequestBody contact: Contact): ResponseEntity<Map<String, String>> {
        contactService.saveContact(contact)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapOf("message" to "Contact message received successfully"))
    }

    @GetMapping
    fun getAllContacts(): List<Contact> {
        return contactService.getAllContacts()
    }
}
