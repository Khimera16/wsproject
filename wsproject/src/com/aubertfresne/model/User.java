package com.aubertfresne.model;

import java.util.ArrayList;

public class User {
	
	private String mail;
	private String firstName;
	private String lastName;
	private String biography;
	private ArrayList<Group> groupList;
	
	
	public User(){
		super();
	}
	
	public User(String mail, String firstName, String lastName,
			String biography) {
		super();
		this.mail = mail;
		this.firstName = firstName;
		this.lastName = lastName;
		this.biography = biography;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
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
	public String getBiography() {
		return biography;
	}
	public void setBiography(String biography) {
		this.biography = biography;
	}
	public ArrayList<Group> getGroupList() {
		return groupList;
	}
	public void setGroupList(ArrayList<Group> groupList) {
		this.groupList = groupList;
	}


}
