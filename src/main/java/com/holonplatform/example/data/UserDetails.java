package com.holonplatform.example.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserDetails implements Serializable {

	private String userId;
	private boolean role1;
	private String firstName;
	private String lastName;
	private String email;

	public UserDetails() {
		super();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isRole1() {
		return role1;
	}

	public void setRole1(boolean role1) {
		this.role1 = role1;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
