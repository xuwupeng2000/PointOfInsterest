package DAOs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import domain.User;


//I changed something there
//share one conn and stmt res
//add a openConn() method and closeConn() method
//the problem is that it seems everyone is able to enterd the system
//the login didnt checking and return a result
public class userDAO {
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet res = null;
	public userDAO(){

	}
	
	private void openConnection() throws Exception{
		Class.forName("org.h2.Driver");
		conn = DriverManager.getConnection("jdbc:h2:tcp//localhost/~/POI_SERVER_DATABASE", "jackw", "");
	}
	
	private void closeConnection(){
		try{
			stmt.close();
			res.close();
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String findID(String username,String psword){
		String rs="exsisted";
		try {
			openConnection();
			stmt = conn
				.prepareStatement("select userid from users where username=? and password=?");
			stmt.setString(1, username);
			stmt.setString(2,psword);
			res = stmt.executeQuery();
			if (res.next()) {
				rs = res.getString("userid");
				System.out.println("FindID "+ rs+" with "+ username +" " +psword);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {			
			closeConnection();
		}
		
		return rs;
	}
	/**
	 * @param id
	 * @return true if found, or false if not found
	 */
	public boolean findById(String id){
		boolean rs=false;//not found
		
		try {
			openConnection();
			// Query database
			stmt = conn
					.prepareStatement("select userid from users where userid=?");
			stmt.setString(1, id);
			res = stmt.executeQuery();
			if (!res.isBeforeFirst()) {
				rs=true;//not found
				System.out.println("found ID ,you can not create new account");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
		
		return rs;
	}

	/**
	 * create a new user after  checking	 * 
	 * @param userTocreate
	 */
	public boolean creat(User userTocreate) {
		boolean rs=false;
		
		try {
			openConnection();
			
			stmt = conn
					.prepareStatement("insert into users values(?,?,?)");
			stmt.setString(1, userTocreate.getId());
			stmt.setString(2, userTocreate.getName());
			stmt.setString(3, userTocreate.getPassword());
			stmt.execute();
			rs=true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
		return rs;
	}

}
