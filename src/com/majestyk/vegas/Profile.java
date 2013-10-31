package com.majestyk.vegas;

public class Profile {
	
	private String user_id;
	private String user_img;
	private String user_name;

	public Profile (String s1, String s2, String s3) {
		this.user_id = s1;
		this.user_img = s2;
		this.user_name = s3;
	}

	public String getUserId() {
		return user_id;
	}

	public void setUserId(String user_id) {
		this.user_id = user_id;
	}

	public String getUserImg() {
		return user_img;
	}

	public void setUserImg(String user_img) {
		this.user_img = user_img;
	}

	public String getUserName() {
		return user_name;
	}

	public void setUserName(String user_name) {
		this.user_name = user_name;
	}

}
