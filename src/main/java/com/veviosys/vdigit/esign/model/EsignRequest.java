package com.veviosys.vdigit.esign.model;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.utility.RandomString;



@Entity
public class EsignRequest {

	
	
	
	public EsignRequest() {
		super();
		// TODO Auto-generated constructor stub
	}





	@Getter
	@org.hibernate.annotations.Type(type="uuid-char")
	@Id @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	private  UUID sigReqeustId;
	
	@Getter
	private UUID documentId;
	
	@JsonIgnore
	@Getter
	private String secrectCode;
	
	@JsonIgnore
	@Transient
	@Getter
	private String secrectCodeString;
	
	@JsonIgnore
	private Date requestDate;
	
	@JsonIgnore
	@Getter
	private Long singerId;
	
    @Getter
    @JsonIgnore
    private String singerName;
    
    @Getter
    @JsonIgnore
    private String documentName;
	
    @Getter
    private String singerEmail;
    @JsonIgnore
	@Getter
	private float xPos;
	@JsonIgnore
	@Getter
	private float yPos;
	
	@Getter
	@JsonIgnore
	private float sigHeight;
	
	@Getter
	@JsonIgnore
	private float sigWidth;
	
	@Getter
	@JsonIgnore
	private int sigPage;
	
	@JsonIgnore
	private Date validUntilDate;
	
	
	
	public EsignRequest(UUID documentId, Long singerId, float xPos, float yPos, float sigHeight, float sigWidth,String singerName,String singerEmail,int page,int validationTime,String documentName) {
	
		this.documentId = documentId;
		this.singerId = singerId;
		this.xPos = xPos;
		this.yPos = yPos;
		this.sigHeight = sigHeight;
		this.sigWidth = sigWidth;
		this.singerEmail = singerEmail;
		this.singerName = singerName;
		this.sigPage = page;
		this.validationTime = validationTime;
		this.documentName = documentName;
		
		
		
		
		
		
	    try {
			generate2FACode();
			setValidationDate();
			
			
		} catch (NoSuchAlgorithmException e) {

		}
		
		
	}
	
	
	
	
	
	
	
	
	@Getter
	@Setter
	private int validationTime;
	
	
	
	
	@JsonIgnore
	public Boolean getIsStillValid() {
		
		return (new Date()).before(validUntilDate);
	}
	
	public void generate2FACode() throws NoSuchAlgorithmException
	{
		
		RandomString randString = new RandomString(6);
		String str = randString.nextString();
		this.secrectCodeString = str;	
		this.secrectCode = Hashing.sha256().hashString(str, StandardCharsets.UTF_8).toString();
		
		
	}
	
	public void setValidationDate()
	{
		Calendar calendar = Calendar.getInstance();
		
		this.requestDate = calendar.getTime();
		calendar.add(Calendar.MINUTE, validationTime);
		this.validUntilDate = calendar.getTime(); 

		
	}
	
	
	public Boolean Verify(String code)
	{
		
		return this.secrectCode.equals(Hashing.sha256().hashString(code, StandardCharsets.UTF_8).toString());
		
	
	
	
	}
	
	
	
	
	
	
	
	
	
	
	


	
	
	
	
	
	
	
	
	
}
