package com.veviosys.vdigit.services;

import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.search.FlagTerm;

import java.io.IOException;
import java.util.*;
@Service
public class CaptureMailService {

	public void readData() throws MessagingException, IOException {
		 System.out.println("entred to method");
		Properties props=new Properties();
		props.put("mail.smtp.socketFactory.fallback", "false");
		
	    Session session = Session.getDefaultInstance(props);
	    Store store = session.getStore("imap");
	    
	    store.connect("mail.veviosys.ma", 143, "noreply@veviosys.ma", "VevioSys123");
	    Folder inbox = store.getFolder( "INBOX" );
	    System.out.println("entred");
	    inbox.open( Folder.READ_ONLY );
	    
	    System.out.println("opened");
	    // Fetch unseen messages from inbox folder
	    Message[] messages = inbox.search(
	        new FlagTerm(new Flags(Flags.Flag.SEEN), false));
	    System.out.println("Size : "+messages.length);
	    // Sort messages from recent to oldest
	    Arrays.sort( messages, ( m1, m2 ) -> {
	      try {
	        return m2.getSentDate().compareTo( m1.getSentDate() );
	      } catch ( MessagingException e ) {
	        throw new RuntimeException( e );
	      }
	    } );

	    for ( Message message : messages ) {
	      System.out.println( 
	          "sendDate: " + message.getSentDate()
	          + " subject:" + message.getSubject() );
	      System.out.println( 
		          "sendDate: " + message.getSentDate()
		          + " subject:" + message.getSubject() );
	      System.out.println(       
		          "body: " +  message.getContent()    );
	    }
	  }

	}

