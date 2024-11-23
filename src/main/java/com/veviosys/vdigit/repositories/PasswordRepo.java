package com.veviosys.vdigit.repositories;

import com.veviosys.vdigit.models.Password;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface PasswordRepo extends JpaRepository<Password, Long> {


    @Query(value="select * from password where user=:id",nativeQuery = true)
    Password findPw(@Param("id") Long id);

}
