package com.veviosys.vdigit.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.veviosys.vdigit.models.FoldersRelations;
import com.veviosys.vdigit.models.pk.FoldersRelationsPk;

public interface FoldersRelationsRepo extends JpaRepository<FoldersRelations,FoldersRelationsPk> , JpaSpecificationExecutor<FoldersRelations> {

}
