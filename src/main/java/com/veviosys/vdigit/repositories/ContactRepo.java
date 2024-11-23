package com.veviosys.vdigit.repositories;

import com.veviosys.vdigit.models.Contact;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepo  extends JpaRepository<Contact,Long>{

Contact findContactByUserUserId(Long id);
}
