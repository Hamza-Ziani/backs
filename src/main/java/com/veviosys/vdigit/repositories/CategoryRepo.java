package com.veviosys.vdigit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.Category;

public interface CategoryRepo extends JpaRepository<Category, Long> {

}
