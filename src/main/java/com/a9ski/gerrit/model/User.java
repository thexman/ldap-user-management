package com.a9ski.gerrit.model;

public class User {
	private final String id;
	private final String firstName;
	private final String lastName;
	private final String fullName;
	private final String displayName;
	private final String email;
	private final String password;
	
	
	public User(final String id, final String firstName, final String lastName, final String email, final String password) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullName = String.format("%s %s", firstName, lastName);
		this.displayName = this.fullName;
		this.email = email;
		this.password = password;
	}


	public String getId() {
		return id;
	}


	public String getFirstName() {
		return firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public String getFullName() {
		return fullName;
	}


	public String getDisplayName() {
		return displayName;
	}


	public String getEmail() {
		return email;
	}


	public String getPassword() {
		return password;
	}
}
