/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DAOs;

import domain.PointOfInteresting;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ray
 */
public class poiDAO {

    private String jdbcurl = null;
    private String admin = null;
    private String password = null;

    public poiDAO() {
        //need to be the folder where the database is
        jdbcurl = "jdbc:h2:file:C:/db/POI/poi";
        admin = "sa";
        password = "";
    }

    public void getPointsInRange(List<PointOfInteresting> pois,
            int lat, int lng, int range) {
        //List<PointOfInteresting> pois =new ArrayList<PointOfInteresting>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(poiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            connection = DriverManager.getConnection(
                    jdbcurl, admin, password);
            statement = connection.createStatement();

            //statement = connection.prepareStatement("select * from poi");
            resultSet = statement.executeQuery("SELECT * FROM poi");
            if (!pois.isEmpty()) {
                    pois.clear();
                }

            while (resultSet.next()) {

                PointOfInteresting p = new PointOfInteresting();
                p.setAddress(resultSet.getString("address"));
                
                p.setLat(resultSet.getInt("latitude"));
                p.setLng(resultSet.getInt("longitude"));
                p.setTitle(resultSet.getString("title"));
                p.setBody(resultSet.getString("body"));
                p.setOwner(resultSet.getInt("owner"));
                p.setPOI_id(resultSet.getInt("poi_id"));
                if (p.distanceto(lat, lng) <= range) {
                   if (!pois.contains(p)) {
                        pois.add(p);
                   }
               }
            }

        } catch (SQLException ex) {
            Logger.getLogger(poiDAO.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
			try {
				resultSet.close();
				statement.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

        //return pois;

    }
}
