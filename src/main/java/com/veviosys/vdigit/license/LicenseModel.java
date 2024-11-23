package com.veviosys.vdigit.license;


import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class LicenseModel {
	
	public LicenseModel(int period, List<String> ipAddress, List<String> macAddress,
			String mainBoardSerial,String product) {
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, period);
		this.startDate = new Date();
		this.notBefore = c.getTime();
		this.period = period;
		this.ipAddress = ipAddress;
		this.macAddress = macAddress;
		this.mainBoardSerial = mainBoardSerial;
		this.product = product;
	}

	public String getProduct() {
		return product;
	}



	private Date startDate;
	private Date notBefore;
	private String product;
	private int period;
	private List<String> ipAddress;

	private  List<String> macAddress;
	
	private String mainBoardSerial;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getNotBefore() {
		return notBefore;
	}

	public void setNotBefore(Date notBefore) {
		this.notBefore = notBefore;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public List<String> getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(List<String> ipAddress) {
		this.ipAddress = ipAddress;
	}

	public List<String> getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(List<String> macAddress) {
		this.macAddress = macAddress;
	}

	public String getMainBoardSerial() {
		return mainBoardSerial;
	}

	public void setMainBoardSerial(String mainBoardSerial) {
		this.mainBoardSerial = mainBoardSerial;
	}
	
	
	
	
	

}
