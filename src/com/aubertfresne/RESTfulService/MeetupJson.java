package com.aubertfresne.RESTfulService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.aubertfresne.model.Group;
import com.aubertfresne.model.User;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("meetupjson")
public class MeetupJson {
	
	private static Connection c;
	private static Statement stmt;
	
	@GET
	@Path("groups")
	@Produces(MediaType.APPLICATION_JSON)
	public String getgroupsJSON() throws JsonGenerationException, JsonMappingException, IOException, SQLException, ClassNotFoundException{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsgroup;" );
		ArrayList<Group> groupsList = new ArrayList<Group>();
		
		while ( rs.next() ) {
			ArrayList<User> groupMembers = new ArrayList<User>();
			ArrayList<String> dashList = new ArrayList<String>();
			String values = rs.getString("grp_members");
			String[] membersArray = values.split(",",-1);
			
			for(int i = 0 ; i < membersArray.length ; i++){
				Statement stmt2 = c.createStatement();
				ResultSet rs2 = stmt2.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail='"+membersArray[i]+"';" );
				while ( rs2.next()){
					groupMembers.add(new User(rs2.getString("usr_mail"),rs2.getString("usr_firstname"),rs2.getString("usr_lastname"),rs2.getString("usr_biography")));
				}
				rs2.close();
				stmt2.close();
			}
			String dashboard = rs.getString("grp_discboard");
			String[] dashboardArray = dashboard.split("\\|",-1);
			for(String s : dashboardArray) {
				if(!(s.equals("null")) && s != null && s.length() > 0) {
					dashList.add(s);
				}
			}
			Statement stmt2 = c.createStatement();
			ResultSet rs2 = stmt2.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail='"+rs+"';" );
			User admin = new User();
			while ( rs2.next()){
				admin.setMail(rs2.getString("usr_mail"));
				admin.setFirstName(rs2.getString("usr_firstname"));
				admin.setLastName(rs2.getString("usr_lastname"));
				admin.setBiography(rs2.getString("usr_biography"));
				
			}
			rs2.close();
			stmt2.close();
			groupsList.add(new Group(rs.getString("grp_name"),rs.getString("grp_description"),admin,groupMembers,dashList));
		}
		
		rs.close();
		stmt.close();
		c.close();
		
		ObjectMapper mapper = new ObjectMapper();
		String groupsJson = mapper.writeValueAsString(groupsList);
		return groupsJson;	
	}
	
	@GET
	@Path("groups/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getgroupJSON(@PathParam("name") String name) throws JsonGenerationException, JsonMappingException, IOException, SQLException, ClassNotFoundException{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsgroup WHERE wsgroup.grp_name='"+name+"';"  );
		ArrayList<Group> groupsList = new ArrayList<Group>();
		
		while ( rs.next() ) {
			ArrayList<User> groupMembers = new ArrayList<User>();
			ArrayList<String> dashList = new ArrayList<String>();
			String values = rs.getString("grp_members");
			String[] membersArray = values.split(",",-1);
			
			for(int i = 0 ; i < membersArray.length ; i++){
				Statement stmt2 = c.createStatement();
				ResultSet rs2 = stmt2.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail='"+membersArray[i]+"';" );
				while ( rs2.next()){
					groupMembers.add(new User(rs2.getString("usr_mail"),rs2.getString("usr_firstname"),rs2.getString("usr_lastname"),rs2.getString("usr_biography")));
				}
				rs2.close();
				stmt2.close();
			}
			String dashboard = rs.getString("grp_discboard");
			String[] dashboardArray = dashboard.split("\\|",-1);
			for(String s : dashboardArray) {
				if(!(s.equals("null")) && s != null && s.length() > 0) {
					dashList.add(s);
				}
			}
			Statement stmt2 = c.createStatement();
			ResultSet rs2 = stmt2.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail='"+rs+"';" );
			User admin = new User();
			while ( rs2.next()){
				admin.setMail(rs2.getString("usr_mail"));
				admin.setFirstName(rs2.getString("usr_firstname"));
				admin.setLastName(rs2.getString("usr_lastname"));
				admin.setBiography(rs2.getString("usr_biography"));
				
			}
			rs2.close();
			stmt2.close();
			groupsList.add(new Group(rs.getString("grp_name"),rs.getString("grp_description"),admin,groupMembers,dashList));
		}
		
		rs.close();
		stmt.close();
		c.close();
		
		ObjectMapper mapper = new ObjectMapper();
		String groupsJson = mapper.writeValueAsString(groupsList);
		return groupsJson;	
	}
	
	@GET
	@Path("json/user/{mail}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUserJson(@PathParam("mail") String mail) throws SQLException, ClassNotFoundException, JsonProcessingException{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail='"+mail+"';" );
		User user = new User();
		ArrayList<Group> groupList = new ArrayList<Group>();
		while ( rs.next()){
			user.setMail(rs.getString("usr_mail"));
			user.setFirstName(rs.getString("usr_firstname"));
			user.setLastName(rs.getString("usr_lastname"));
			user.setBiography(rs.getString("usr_biography"));
			
			String groups = rs.getString("usr_groups");
			String[] groupsArray = groups.split(",",-1);
			for(String s : groupsArray) {
				if(!(s.equals("null")) && s != null && s.length() > 0) {
					Statement stmt2 = c.createStatement();
					ResultSet rs2 = stmt2.executeQuery( "SELECT * FROM wsgroup WHERE wsgroup.grp_name='"+s+"';" );
					while ( rs2.next()){
						groupList.add(new Group(rs2.getString("grp_name"),rs2.getString("grp_description")));
					}
					rs2.close();
					stmt2.close();
				}
			}
			user.setGroupList(groupList);
		}
		rs.close();
		stmt.close();
		c.close();
		ObjectMapper mapper = new ObjectMapper();
		String userJson = mapper.writeValueAsString(user);
		return userJson;
	}

}
