package com.das.portfolioservice.service

import com.das.portfolioservice.model.Contact
import com.das.portfolioservice.repository.ContactRepository
import org.springframework.stereotype.Service

@Service
class ContactService(private val contactRepository: ContactRepository) {

    fun saveContact(contact: Contact): Contact {
        return contactRepository.save(contact)
    }

    fun getAllContacts(): List<Contact> {
        return contactRepository.findAll()
    }
}
