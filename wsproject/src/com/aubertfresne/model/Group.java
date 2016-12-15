package com.aubertfresne.model;

import java.util.ArrayList;

public class Group {

	private String name;
	private String description;
	private User admin;
	private ArrayList<User> members;
	private ArrayList<String> discussionBoard;
	
	
	public Group(){
		super();
	}
	
	public Group(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}
	
	public Group(String name, String description, User admin,
			ArrayList<User> members, ArrayList<String> discussionBoard) {
		super();
		this.name = name;
		this.description = description;
		this.admin = admin;
		this.members = members;
		this.discussionBoard = discussionBoard;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public User getAdmin() {
		return admin;
	}
	public void setAdmin(User admin) {
		this.admin = admin;
	}
	public ArrayList<User> getMembers() {
		return members;
	}
	public void setMembers(ArrayList<User> members) {
		this.members = members;
	}
	public ArrayList<String> getDiscussionBoard() {
		return discussionBoard;
	}
	public void setDiscussionBoard(ArrayList<String> discussionBoard) {
		this.discussionBoard = discussionBoard;
	}
	
	
	
}
