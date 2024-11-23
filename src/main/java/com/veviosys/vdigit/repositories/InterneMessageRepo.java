package com.veviosys.vdigit.repositories;

import java.util.List; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.veviosys.vdigit.models.InterneMessage;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.sharedfolders.models.SharedFolder;
 
public interface InterneMessageRepo extends JpaRepository<InterneMessage, Long> {

//  Page<InterneMessage>findByReceivers_UserUserId(Long u, Pageable p);

//     @Query(value="select interne_message where id in (select i.id from interne_message i inner join recieve_message r where  i.id=r.message_id "
//     + " and r.user_id=:idU order by i.sent_date desc)",nativeQuery = true)
// Page<InterneMessage>getMessages(@Param("idU")Long idU,Pageable page);
 
// @Query(value="select count(*) from interne_message i inner join recieve_message r where  i.id=r.message_id and r.seen=0"
// + " and r.user_id=:idU",nativeQuery = true)
// int getCountMessages(@Param("idU")Long idU);

Page<InterneMessage>findByReceivers_UserUserId(Long u, Pageable p);


//Method to find messages where the sender is the given user or is a receiver
//Page<InterneMessage> findBySender_UserIdOrReceivers_User_FullNameContaining(Long userId, String fullName, Pageable pageable);
//@Query("SELECT im FROM InterneMessage im WHERE im.senderId = :userId OR im.receivers.user.fullName LIKE %:fullName%")
//Page<InterneMessage> findBySenderIdOrReceiver(
//    User user,
//    String fullName, 
//    Pageable pageable
//);
// get All Documents  by sender and fullName, and order by creation date descending
Page<InterneMessage> findDistinctBySenderAndReceiversUserFullNameContainingIgnoreCase( User user, String fullName, Pageable pageable);





@Query(value="select interne_message where id in (select i.id from interne_message i inner join recieve_message r where  i.id=r.message_id "
+ " and r.user_id=:idU order by i.sent_date desc)",nativeQuery = true)
Page<InterneMessage>getMessages(@Param("idU")Long idU,Pageable page);

@Query(value="select count(*) from interne_message i , recieve_message r where i.id=r.message_id and r.seen=0"
+ " and r.user_id=:idU",nativeQuery = true)
int getCountMessages(@Param("idU")Long idU);
}
