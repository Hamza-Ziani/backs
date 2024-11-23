package com.veviosys.vdigit.esign.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.esign.model.EsignRequest;




public interface ESignReqeustRepository extends JpaRepository<EsignRequest, UUID> {

}
