package com.veviosys.vdigit.repositories;

import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.delayMail;

import org.springframework.data.jpa.repository.JpaRepository;

public interface delayMailRepo extends JpaRepository<delayMail,Long>
{
  delayMail findByUser(User master);
  delayMail findByUserUserId(Long master);


}