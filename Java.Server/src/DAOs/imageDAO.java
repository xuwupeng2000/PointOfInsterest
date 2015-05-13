package DAOs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import domain.Image;

public class imageDAO {
	
	private String jdbcurl=null;
	private String admin=null;
	private String password=null;
	
	public imageDAO(){
		//need to be the folder where the database is
		jdbcurl="jdbc:h2:file:C:/db/POI/poi";
		admin="sa";
		password="";
	}

	public boolean creat(Image imageTosave) {

		boolean rs=false;
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			try {
				Class.forName("org.h2.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			connection = DriverManager.getConnection(
					jdbcurl, admin, password);
			statement = connection
					.prepareStatement("insert into images values(?,?)");
			statement.setString(1, imageTosave.getId());
			statement.setString(2, imageTosave.getImage());
			statement.execute();
			rs=true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return rs;
	}

}
