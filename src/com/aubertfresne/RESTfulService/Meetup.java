package com.aubertfresne.RESTfulService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.aubertfresne.model.Group;
import com.aubertfresne.model.User;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Path("meetup")
public class Meetup {
	private static Connection c;
	private static Statement stmt;
	private static User user;
	private static boolean usr_connected = false;



	@Path("group/groups")
	@GET
	public String viewAllGroups() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsgroup;" );
		String viewGroups = "";
		String newLine = System.getProperty("line.separator"); 
		while ( rs.next() ) {
			String values = rs.getString("grp_members");
			String[] membersArray = values.split(",",-1); 
			viewGroups = viewGroups+"Name : "+rs.getString("grp_name")+newLine+
					"Description : "+rs.getString("grp_description")+newLine + "Number of members : "+membersArray.length;
		}
		rs.close();
		stmt.close();
		c.close();
		return viewGroups;
	}
	
	@Path("group/groupmembers")
	@GET
	public String viewGroupMembers(@QueryParam("name") String name) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsgroup WHERE wsgroup.grp_name= '"+name+"';");
		String groupMembers = "";
		String newLine = System.getProperty("line.separator"); 
		ArrayList<String> membersList = new ArrayList<String>();
		while ( rs.next() ) {
			String values = rs.getString("grp_members");
			String[] membersArray = values.split(",",-1);
			for(String s : membersArray) {
				if(s != null && s.length() > 0) {
					membersList.add(s);
				}
			}
		}
		rs.close();
		stmt.close();
		for(int i = 0 ; i<membersList.size() ; i++){
			stmt = c.createStatement();
			rs = stmt.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail= '"+membersList.get(i)+"';");
			while ( rs.next() ) {
				groupMembers = groupMembers+"eMail : "+rs.getString("usr_mail")+newLine+"First name : "+rs.getString("usr_firstname")+newLine+
						"Last name : "+rs.getString("usr_lastname")+newLine + "Biography : "+rs.getString("usr_biography")+newLine +newLine ;
			}
			rs.close();
			stmt.close();
		}


		c.close();
		return groupMembers;
	}

	@Path("group/usergroups")
	@GET
	public String viewUserGroups() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail= '"+user.getMail()+"';");
		String viewGroups = "";
		String newLine = System.getProperty("line.separator"); 
		ArrayList<String> groupsList = new ArrayList<String>();
		while ( rs.next() ) {
			String values = rs.getString("usr_groups");
			String[] groupsArray = values.split(",",-1);


			for(String s : groupsArray) {
				if(s != null && s.length() > 0) {
					groupsList.add(s);
				}
			}
		}
		rs.close();
		stmt.close();
		for(int i = 0 ; i<groupsList.size() ; i++){
			stmt = c.createStatement();
			rs = stmt.executeQuery( "SELECT * FROM wsgroup WHERE wsgroup.grp_name= '"+groupsList.get(i)+"';");
			while ( rs.next() ) {
				String values = rs.getString("grp_members");
				String[] membersArray = values.split(",",-1);

				viewGroups = viewGroups+"Name : "+rs.getString("grp_name")+newLine+
						"Description : "+rs.getString("grp_description")+newLine + "Number of members : "+membersArray.length;
			}
			rs.close();
			stmt.close();
		}


		c.close();
		return viewGroups;
	}

	@Path("group/newgroup")
	@GET
	public Response createGroup(@QueryParam("name") String name, @QueryParam("description") String description) throws SQLException, ClassNotFoundException, URISyntaxException
	{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		String sql = "INSERT INTO wsgroup (grp_name,grp_description,grp_admin,grp_members) "
				+ "VALUES ('"+name+"','"+description+"','"+user.getMail()+"','"+user.getMail()+"');";
		stmt.executeUpdate(sql);
		stmt.close();
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail='"+user.getMail()+"';" );
		String usr_groups = "";
		while ( rs.next() ) {
			if(rs.getString("usr_groups")!=null){
				usr_groups= rs.getString("usr_groups");
			}

		}
		rs.close();
		stmt.close();
		stmt = c.createStatement();
		sql = "UPDATE wsuser set usr_groups = '"+usr_groups + ","+name+"' WHERE wsuser.usr_mail= '"+user.getMail()+"';";
		stmt.executeUpdate(sql);
		stmt.close();
		c.close();
		java.net.URI location = new java.net.URI("../meetupApp/meetup/group/groups");
		return Response.temporaryRedirect(location).build();
	}
	
	@Path("group/leavegroup")
	@GET
	public Response leaveGroup(@QueryParam("name") String name) throws SQLException, ClassNotFoundException, URISyntaxException
	{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail='"+user.getMail()+"';" );
		String usr_groups = "";
		while ( rs.next() ) {
			if(rs.getString("usr_groups")!=null){
				usr_groups= rs.getString("usr_groups");
			}

		}
		String groups = usr_groups.replace(","+name, "");
		rs.close();
		stmt.close();
		stmt = c.createStatement();
		String sql = "UPDATE wsuser set usr_groups = '"+groups+"' WHERE wsuser.usr_mail= '"+user.getMail()+"';";
		stmt.executeUpdate(sql);
		stmt.close();
		
		stmt = c.createStatement();
		rs = stmt.executeQuery( "SELECT * FROM wsgroup WHERE wsgroup.grp_name='"+name+"';" );
		String grp_members = "";
		while ( rs.next() ) {
			if(rs.getString("grp_members")!=null){
				grp_members= rs.getString("grp_members");
			}

		}
		String members = grp_members.replace(","+user.getMail(), "");
		rs.close();
		stmt.close();
		stmt = c.createStatement();
		sql = "UPDATE wsgroup set grp_members = '"+members+"' WHERE wsgroup.grp_name= '"+name+"';";
		stmt.executeUpdate(sql);
		stmt.close();
		
		c.close();
		java.net.URI location = new java.net.URI("../meetupApp/meetup/group/usergroups");
		return Response.temporaryRedirect(location).build();
	}
	
	
	
	@Path("group/joingroup")
	@GET
	public Response joinGroup(@QueryParam("name") String name) throws SQLException, ClassNotFoundException, URISyntaxException
	{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsgroup WHERE wsgroup.grp_name='"+name+"';" );
		String grp_members = "";
		while ( rs.next() ) {
			if(rs.getString("grp_members")!=null){
				grp_members= rs.getString("grp_members");
			}

		}
		rs.close();
		stmt.close();
		stmt = c.createStatement();
		rs = stmt.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail='"+user.getMail()+"';" );
		String usr_groups = "";
		while ( rs.next() ) {
			if(rs.getString("usr_groups")!=null){
				usr_groups= rs.getString("usr_groups");
			}

		}
		rs.close();
		stmt.close();
		stmt = c.createStatement();
		String sql = "UPDATE wsgroup set grp_members = '"+grp_members + ","+user.getMail()+"' WHERE wsgroup.grp_name= '"+name+"';";
		stmt.executeUpdate(sql);
		stmt.close();
		stmt = c.createStatement();
		sql = "UPDATE wsuser set usr_groups = '"+usr_groups + ","+name+"' WHERE wsuser.usr_mail= '"+user.getMail()+"';";
		stmt.executeUpdate(sql);
		stmt.close();
		c.close();
		java.net.URI location = new java.net.URI("../meetupApp/meetup/group/groups");
		return Response.temporaryRedirect(location).build();
	}

	@Path("group/changedescription")
	@GET
	public Response changeGroupDescription(@QueryParam("name") String name, @QueryParam("description") String description) throws SQLException, ClassNotFoundException, URISyntaxException
	{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsgroup WHERE wsgroup.grp_name='"+name+"';" );
		boolean isAuthorized = false;
		while ( rs.next() ) {
			if((user.getMail()).equals(rs.getString("grp_admin"))){
				isAuthorized = true;
			}
		}
		rs.close();
		stmt.close();

		if(isAuthorized==true){
			stmt = c.createStatement();
			String sql = "UPDATE wsgroup set grp_description = '"+description+"' WHERE wsgroup.grp_name= '"+name+"';";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
			java.net.URI location = new java.net.URI("../meetupApp/meetup/group/groups");
			return Response.temporaryRedirect(location).build();
		}else{
			java.net.URI location = new java.net.URI("../profile.html");
			return Response.temporaryRedirect(location).build();
		}



	}

	@Path("group/deletegroup")
	@GET
	public Response deleteGroup(@QueryParam("name") String name) throws SQLException, ClassNotFoundException, URISyntaxException
	{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsgroup WHERE wsgroup.grp_name='"+name+"';" );
		boolean isAuthorized = false;
		while ( rs.next() ) {
			if((user.getMail()).equals(rs.getString("grp_admin"))){
				isAuthorized = true;
			}
		}
		rs.close();
		stmt.close();

		if(isAuthorized==true){
			stmt = c.createStatement();
			String sql = "DELETE FROM wsgroup WHERE wsgroup.grp_name= '"+name+"';";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
			c.close();
			java.net.URI location = new java.net.URI("../meetupApp/meetup/group/groups");
			return Response.temporaryRedirect(location).build();
		}else{
			java.net.URI location = new java.net.URI("../profile.html");
			return Response.temporaryRedirect(location).build();
		}



	}

	@Path("user/signup")
	@GET
	public Response signup(@QueryParam("mail") String mail) throws SQLException, ClassNotFoundException, URISyntaxException
	{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail='"+mail+"';" );
		while ( rs.next() ) {
			user = new User(rs.getString("usr_mail"),rs.getString("usr_firstname"),rs.getString("usr_lastname"),rs.getString("usr_biography"));
		}
		rs.close();
		stmt.close();
		c.close();
		if(user.getFirstName()!=null){
			usr_connected = true;
			java.net.URI location = new java.net.URI("../profile.html");
			return Response.temporaryRedirect(location).build();
		}else{
			java.net.URI location = new java.net.URI("../signup.html");
			return Response.temporaryRedirect(location).build();
		}

	}

	@Path("user/changebiography")
	@GET
	public Response changeBiography(@QueryParam("biography") String biography) throws SQLException, ClassNotFoundException, URISyntaxException
	{
		if(usr_connected==false){
			java.net.URI location = new java.net.URI("../signup.html");
			return Response.temporaryRedirect(location).build();
		}else{
			Class.forName("org.postgresql.Driver");
			c = DriverManager
					.getConnection("jdbc:postgresql://localhost:5432/wsproject",
							"postgres", "postgres");
			stmt = c.createStatement();
			String sql = "UPDATE wsuser set usr_biography = '"+biography+"' WHERE wsuser.usr_mail= '"+user.getMail()+"';";
			stmt.executeUpdate(sql);
			ResultSet rs = stmt.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail='"+user.getMail()+"';" );
			while ( rs.next() ) {
				user = new User(rs.getString("usr_mail"),rs.getString("usr_firstname"),rs.getString("usr_lastname"),rs.getString("usr_biography"));
			}
			rs.close();
			stmt.close();
			c.close();
			usr_connected = true;
			java.net.URI location = new java.net.URI("../profile.html");
			return Response.temporaryRedirect(location).build();
		}

	}
	
	@Path("group/groupdashboard")
	@GET
	public String groupDashboard(@QueryParam("name") String name) throws SQLException, ClassNotFoundException, URISyntaxException
	{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsgroup WHERE wsgroup.grp_name='"+name+"';" );
		String dashboard = "";
		while ( rs.next() ) {
			dashboard = dashboard + rs.getString("grp_discboard");
		}
		ArrayList<String> dashList = new ArrayList<String>();
		String[] dashboardArray = dashboard.split("\\|",-1);
		for(String s : dashboardArray) {
			if(!(s.equals("null")) && s != null && s.length() > 0) {
				dashList.add(s);
			}
		}
		rs.close();
		stmt.close();
		c.close();
		String viewDashboard = "";
		String newLine = System.getProperty("line.separator"); 
		for(int i = 0 ; i < dashList.size() ; i++){
			viewDashboard = viewDashboard + dashList.get(i) + newLine;
		}
		return viewDashboard;

	}
	
	@Path("group/commentdashboard")
	@GET
	public Response commentBoard( @QueryParam("name") String name, @QueryParam("comment") String comment) throws SQLException, ClassNotFoundException, URISyntaxException
	{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsgroup WHERE wsgroup.grp_name='"+name+"';" );
		String dashboard = "";
		while ( rs.next() ) {
			dashboard = rs.getString("grp_discboard");
		}
		rs.close();
		stmt.close();
		stmt = c.createStatement();
		String newBoard = dashboard + "|" + user.getFirstName() +" " + user.getLastName() + " : "+comment;
		String sql = "UPDATE wsgroup set grp_discboard = '"+newBoard+"' WHERE wsgroup.grp_name= '"+name+"';";
		stmt.executeUpdate(sql);
		stmt.close();
		c.close();
		java.net.URI location = new java.net.URI("../profile.html");
		return Response.temporaryRedirect(location).build();

	}

	@Path("user/deleteuser")
	@GET
	public Response deleteUser() throws SQLException, ClassNotFoundException, URISyntaxException
	{
		if(usr_connected==false){
			java.net.URI location = new java.net.URI("../signup.html");
			return Response.temporaryRedirect(location).build();
		}else{
			Class.forName("org.postgresql.Driver");
			c = DriverManager
					.getConnection("jdbc:postgresql://localhost:5432/wsproject",
							"postgres", "postgres");
			stmt = c.createStatement();
			String sql = "DELETE FROM wsgroup WHERE wsgroup.grp_admin= '"+user.getMail()+"';";
			stmt.executeUpdate(sql);
			stmt.close();
			stmt = c.createStatement();
			sql = "DELETE FROM wsuser WHERE wsuser.usr_mail= '"+user.getMail()+"';";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
			user = new User();
			usr_connected = false;
			java.net.URI location = new java.net.URI("../index.html");
			return Response.temporaryRedirect(location).build();
		}

	}

	@Path("user/changename")
	@GET
	public Response changeName( @QueryParam("first_name") String first_name, @QueryParam("last_name") String last_name) throws SQLException, ClassNotFoundException, URISyntaxException
	{
		if(usr_connected==false){
			java.net.URI location = new java.net.URI("../signup.html");
			return Response.temporaryRedirect(location).build();
		}else{
			Class.forName("org.postgresql.Driver");
			c = DriverManager
					.getConnection("jdbc:postgresql://localhost:5432/wsproject",
							"postgres", "postgres");
			stmt = c.createStatement();
			String sql = "UPDATE wsuser set usr_firstname = '"+first_name+"', usr_lastname = '"+last_name+"' WHERE wsuser.usr_mail= '"+user.getMail()+"';";
			stmt.executeUpdate(sql);
			ResultSet rs = stmt.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail='"+user.getMail()+"';" );
			while ( rs.next() ) {
				user = new User(rs.getString("usr_mail"),rs.getString("usr_firstname"),rs.getString("usr_lastname"),rs.getString("usr_biography"));
			}
			rs.close();
			stmt.close();
			c.close();
			usr_connected = true;
			java.net.URI location = new java.net.URI("../profile.html");
			return Response.temporaryRedirect(location).build();
		}

	}
	
	@Path("user/lookmember")
	@GET
	public String lookmember(@QueryParam("mail") String mail) throws SQLException, ClassNotFoundException, URISyntaxException
	{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail= '"+mail+"';");
		String member = "";
		String newLine = System.getProperty("line.separator"); 
		while ( rs.next() ) {
			member = "eMail : "+rs.getString("usr_mail")+newLine+"First name : "+rs.getString("usr_firstname")+newLine+
					"Last name : "+rs.getString("usr_lastname")+newLine + "Biography : "+rs.getString("usr_biography")+newLine +newLine ;
		}
		rs.close();
		stmt.close();

		c.close();
		return member;

	}


	@Path("user/registration")
	@GET
	public String registerUser(@QueryParam("mail") String mail, @QueryParam("first_name") String first_name, @QueryParam("last_name") String last_name,@QueryParam("biography") String biography) throws SQLException, ClassNotFoundException
	{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		String sql = "INSERT INTO wsuser (usr_mail,usr_firstname,usr_lastname,usr_biography) "
				+ "VALUES ('"+mail+"','"+first_name+"','"+last_name+"','"+biography+"');";
		stmt.executeUpdate(sql);
		stmt.close();
		c.close();
		return "<message>Registration successful</message>";
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
			if(rs.getString("usr_groups")!=null){
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
	
	@GET
	@Path("json/group/discussion/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getGroupBoard(@PathParam("name") String name) throws JsonGenerationException, JsonMappingException, IOException, SQLException, ClassNotFoundException{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsgroup WHERE wsgroup.grp_name='"+name+"';"  );
		ArrayList<HashMap<String,String>> listMap = new ArrayList<HashMap<String,String>>();
		
		while ( rs.next() ) {
			String dashboard = rs.getString("grp_discboard");
			String[] dashboardArray = dashboard.split("\\|",-1);
			for(String s : dashboardArray) {
				if(!(s.equals("null")) && s != null && s.length() > 0) {
					String[] varS = s.split(":",-1);
					HashMap<String,String> dashMap = new HashMap<String,String>();
					dashMap.put("name", varS[0]);
					dashMap.put("message", varS[1]);
					listMap.add(dashMap);
				}
			}
		}
		
		rs.close();
		stmt.close();
		c.close();
		
		ObjectMapper mapper = new ObjectMapper();
		String groupsJson = mapper.writeValueAsString(listMap);
		return groupsJson;	
	}
	
	@GET
	@Path("json/groups/{name}")
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
	@Path("json/group/members/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getgroupMembersJSON(@PathParam("name") String name) throws JsonGenerationException, JsonMappingException, IOException, SQLException, ClassNotFoundException{
		Class.forName("org.postgresql.Driver");
		c = DriverManager
				.getConnection("jdbc:postgresql://localhost:5432/wsproject",
						"postgres", "postgres");
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM wsgroup WHERE wsgroup.grp_name='"+name+"';"  );

		ArrayList<User> groupMembers = new ArrayList<User>();
		while ( rs.next() ) {
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
		}
		
		rs.close();
		stmt.close();
		c.close();
		
		ObjectMapper mapper = new ObjectMapper();
		String groupsJson = mapper.writeValueAsString(groupMembers);
		return groupsJson;	
	}
	
	@GET
	@Path("json/groups")
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

			Statement stmt2 = c.createStatement();
			ResultSet rs2 = stmt2.executeQuery( "SELECT * FROM wsuser WHERE wsuser.usr_mail='"+rs.getString("grp_admin")+"';" );
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

}
