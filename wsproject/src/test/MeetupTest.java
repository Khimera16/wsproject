package test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.aubertfresne.RESTfulService.Meetup;
import com.aubertfresne.model.Group;
import com.aubertfresne.model.User;

public class MeetupTest {
	
	@Mock private Connection mockConnection;
	@Mock private Statement mockStatement;
	@Mock private ResultSet mockResultSet;
	@Mock private Response mockResponse;
	@Mock private User mockUser;
	@Mock private Group mockGroup;
	@Mock private Meetup mockMeetup;

	@Before
	public void setUp() throws SQLException {
		MockitoAnnotations.initMocks(this);
		Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);
		Mockito.when(mockConnection.createStatement().executeQuery((String) Mockito.any())).thenReturn(mockResultSet);
		mockUser = new User("test_mail", "test_firstname", "test_lastname" ,"test_biography");
		mockGroup = new Group("test_name", "test_description");
	}
	
	@After
	public void verifyAfter() throws SQLException {
		Mockito.verify(mockConnection, Mockito.atLeastOnce()).createStatement();
	}
	
	@Test
	public void testRegistration() throws ClassNotFoundException, SQLException {
		String expected = "<message>Registration successful</message>";
		String actual = new Meetup().registerUser(mockUser.getMail(), mockUser.getFirstName(), mockUser.getLastName(), mockUser.getBiography());
		Assert.assertEquals(expected,  actual);
	}
	
	@Test
	public void testSignUP() throws Exception {
		Mockito.when(mockMeetup.signup(Mockito.anyString())).thenReturn(mockResponse);
		Response resp = new Meetup().signup(mockUser.getMail());
		Assert.assertNotEquals(mockResponse, resp);
	}
	
	@Test
	public void testViewAllGroups() throws SQLException, Exception {
		String newLine = System.getProperty("line.separator");
		String values = "";
		String[] membersArray = values.split(",",-1); 
		String expectedViewGroups =  "Name : " + mockGroup.getName() +newLine+
				"Description : " + mockGroup.getDescription() + newLine + "Number of members : " + membersArray.length;
		Mockito.when(mockMeetup.viewAllGroups()).thenReturn(expectedViewGroups);
		String actual = mockMeetup.viewAllGroups();
		Assert.assertEquals(expectedViewGroups, actual);
	}
	
}
