package com.veviosys.vdigit.repositories;

import com.veviosys.vdigit.models.NotifMail;
import com.veviosys.vdigit.models.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotifMailRepo extends JpaRepository<NotifMail,Long> {
	NotifMail findByUser(User master);
	NotifMail findByUserUserId(Long master);

} 