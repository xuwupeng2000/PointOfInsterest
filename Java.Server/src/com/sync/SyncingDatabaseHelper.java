package com.sync;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class SyncingDatabaseHelper {
//	static Connection conn = null;
//	static Statement stmt = null;

	public SyncingDatabaseHelper() {

	}

	public String fetchNewComs(String lastTimeStr) throws Exception {
		Connection conn = null;
		conn = openConnection(conn);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(lastTimeStr, JsonNode.class);
		ArrayList<String> list = new ArrayList<String>();
		long lasttime = rootNode.path("lasttime").getLongValue();
		PreparedStatement selectStmt = conn.prepareStatement(
		// "SELECT * FROM com");//testing
				"SELECT * FROM COM WHERE updatetime > ?");
		selectStmt.setLong(1, lasttime);
		ResultSet res = selectStmt.executeQuery();
		while (res.next()) {
			String comid = res.getString("comid");
			String poiid = res.getString("poiid");
			String username = res.getString("username");
			String content = res.getString("content");
			long updatetime = res.getLong("updatetime");
			JsonFactory fac = new JsonFactory();
			StringWriter sw = new StringWriter();
			JsonGenerator gen = fac.createJsonGenerator(sw);
			gen.writeStartObject();
			gen.writeObjectField("comid", comid);
			gen.writeObjectField("poiid", poiid);
			gen.writeObjectField("username", username);
			gen.writeObjectField("content", content);
			gen.writeObjectField("updatetime", updatetime);
			gen.writeEndObject();
			gen.close();

			list.add(sw.toString());
		}
		StringWriter sw = new StringWriter();
		mapper.writeValue(sw, list);
		String comsListStr = sw.toString();
		conn.close();
		return comsListStr;
	}

	public String fetchNewKeys(String lastTimeStr) throws Exception {
		Connection conn = null;
		conn = openConnection(conn);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(lastTimeStr, JsonNode.class);
		ArrayList<String> list = new ArrayList<String>();
		long lasttime = rootNode.path("lasttime").getLongValue();
		PreparedStatement selectStmt = conn
				.prepareStatement("SELECT * FROM KEY WHERE updatetime > ?");
		selectStmt.setLong(1, lasttime);
		ResultSet res = selectStmt.executeQuery();
		while (res.next()) {
			String tagid = res.getString("tagid");
			String content = res.getString("content");
			long updatetime = res.getLong("updatetime");
			JsonFactory fac = new JsonFactory();
			StringWriter sw = new StringWriter();
			JsonGenerator gen = fac.createJsonGenerator(sw);
			gen.writeStartObject();
			gen.writeObjectField("tagid", tagid);
			gen.writeObjectField("content", content);
			gen.writeObjectField("updatetime", updatetime);
			gen.writeEndObject();
			gen.close();
			list.add(sw.toString());
		}
		StringWriter sw = new StringWriter();
		mapper.writeValue(sw, list);
		String tagsListStr = sw.toString();
		conn.close();
		return tagsListStr;
	}

	public String fetchNewPics(String lastTimeStr) throws Exception {
		Connection conn = null;
		conn = openConnection(conn);
		openConnection(conn);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(lastTimeStr, JsonNode.class);
		ArrayList<String> list = new ArrayList<String>();
		long lasttime = rootNode.path("lasttime").getLongValue();
		PreparedStatement selectStmt = conn
				.prepareStatement("SELECT * FROM PIC WHERE updatetime > ?");
		selectStmt.setLong(1, lasttime);
		ResultSet res = selectStmt.executeQuery();
		while (res.next()) {
			String picid = res.getString("picid");
			String poiid = res.getString("poiid");
			String username = res.getString("username");
			String smallpic = res.getString("smallpic");
			String bigpic = res.getString("bigpic");
			long updatetime = res.getLong("updatetime");
			JsonFactory fac = new JsonFactory();
			StringWriter sw = new StringWriter();
			JsonGenerator gen = fac.createJsonGenerator(sw);
			gen.writeStartObject();
			gen.writeObjectField("picid", picid);
			gen.writeObjectField("poiid", poiid);
			gen.writeObjectField("username", username);
			gen.writeObjectField("smallpic", smallpic);
			gen.writeObjectField("bigpic", bigpic);
			gen.writeObjectField("updatetime", updatetime);
			gen.writeEndObject();
			gen.close();
			list.add(sw.toString());
		}
		StringWriter sw = new StringWriter();
		mapper.writeValue(sw, list);
		String picsListStr = sw.toString();
		conn.close();
		return picsListStr;
	}

	public String fetchNewPOIs(String lastStr) throws Exception {
		Connection conn = null;
		conn = openConnection(conn);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(lastStr, JsonNode.class);
		ArrayList<String> list = new ArrayList<String>();
		long lasttime = rootNode.path("lasttime").getLongValue();
		PreparedStatement selectStmt = conn.prepareStatement(
		// "SELECT * FROM poi");//testing
				"SELECT * FROM POI WHERE updatetime > ?");// tes
		selectStmt.setLong(1, lasttime);
		ResultSet res = selectStmt.executeQuery();
		while (res.next()) {
			String poiid = res.getString("poiid");
			String title = res.getString("title");
			String des = res.getString("des");
			int lat = res.getInt("lat");
			int lng = res.getInt("lng");
			String state = res.getString("state");
			System.out.println("this is the state from server db: " + state);
			String username = res.getString("username");
			long updatetime = res.getLong("updatetime");
			JsonFactory fac = new JsonFactory();
			StringWriter sw = new StringWriter();
			JsonGenerator gen = fac.createJsonGenerator(sw);
			gen.writeStartObject();
			gen.writeObjectField("lat", lat);
			gen.writeObjectField("lng", lng);
			gen.writeObjectField("title", title);
			gen.writeObjectField("des", des);
			gen.writeObjectField("poiid", poiid);
			gen.writeObjectField("username", username);
			gen.writeObjectField("updatetime", updatetime);
			gen.writeObjectField("state", state);
			gen.writeEndObject();
			gen.close();
			list.add(sw.toString());
		}
		StringWriter sw = new StringWriter();
		mapper.writeValue(sw, list);
		String listStr = sw.toString();
		conn.close();
		return listStr;
	}

	public String fetchNewRates(String lastTimeStr) throws Exception {
		Connection conn = null;
		conn = openConnection(conn);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(lastTimeStr, JsonNode.class);
		ArrayList<String> list = new ArrayList<String>();
		long lasttime = rootNode.path("lasttime").getLongValue();
		PreparedStatement selectStmt = conn.prepareStatement(
		// "SELECT * FROM com");//testing
				"SELECT * FROM RAT WHERE updatetime > ?");
		selectStmt.setLong(1, lasttime);
		ResultSet res = selectStmt.executeQuery();
		while (res.next()) {
			String ratid = res.getString("ratid");
			String poiid = res.getString("poiid");
			String username = res.getString("username");
			int rate = res.getInt("rate");
			long updatetime = res.getLong("updatetime");
			JsonFactory fac = new JsonFactory();
			StringWriter sw = new StringWriter();
			JsonGenerator gen = fac.createJsonGenerator(sw);
			gen.writeStartObject();
			gen.writeObjectField("ratid", ratid);
			gen.writeObjectField("poiid", poiid);
			gen.writeObjectField("username", username);
			gen.writeObjectField("rate", rate);
			gen.writeObjectField("updatetime", updatetime);
			gen.writeEndObject();
			gen.close();
			list.add(sw.toString());
		}
		StringWriter sw = new StringWriter();
		mapper.writeValue(sw, list);
		String ratesListStr = sw.toString();
		conn.close();
		return ratesListStr;
	}

	public String fetchNewTags(String lastTimeStr) throws Exception {
		Connection conn = null;
		conn = openConnection(conn);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(lastTimeStr, JsonNode.class);
		ArrayList<String> list = new ArrayList<String>();
		long lasttime = rootNode.path("lasttime").getLongValue();
		PreparedStatement selectStmt = conn.prepareStatement(
		// "SELECT * FROM com");//testing
				"SELECT * FROM TAG WHERE updatetime > ?");
		selectStmt.setLong(1, lasttime);
		ResultSet res = selectStmt.executeQuery();
		while (res.next()) {
			String tagid = res.getString("tagid");
			String poiid = res.getString("poiid");
			long updatetime = res.getLong("updatetime");
			JsonFactory fac = new JsonFactory();
			StringWriter sw = new StringWriter();
			JsonGenerator gen = fac.createJsonGenerator(sw);
			gen.writeStartObject();
			gen.writeObjectField("tagid", tagid);
			gen.writeObjectField("poiid", poiid);
			gen.writeObjectField("updatetime", updatetime);
			gen.writeEndObject();
			gen.close();
			list.add(sw.toString());
		}
		StringWriter sw = new StringWriter();
		mapper.writeValue(sw, list);
		String tagsListStr = sw.toString();
		conn.close();
		return tagsListStr;
	}
	
	

	public void initDatabase() throws Exception {
		Connection conn = null;
		conn = openConnection(conn);
		Statement stmt = null;
		stmt = conn.createStatement();
		stmt.execute("drop table if exists COM;");
		stmt.execute("drop table if exists KEY;");
		stmt.execute("drop table if exists POI;");
		stmt.execute("drop table if exists TAG;");
		stmt.execute("drop table if exists USERS;");
		stmt.execute("drop table if exists RAT;");
		stmt.execute("drop table if exists PIC;");
		stmt.execute("CREATE TABLE POI(" + "poiid varchar(200) not null,"
				+ "lat int not null," + "lng int not null," + "title text,"
				+ "des text," + "username varchar(200) not null,"
				+ "state varchar(50) not null," + "updatetime long not null);");

		stmt.execute("CREATE TABLE USERS(" + "userid varchar(200) not null,"
				+ "username varchar(200) not null,"
				+ "password varchar(200) not null);");

		stmt.execute("CREATE TABLE PIC(" + "picid varchar(200) not null,"
				+ "poiid varchar(200) not null," + "smallpic text,"
				+ "username varchar(200) ," + "updatetime long not null,"
				+ "bigpic text);");

		stmt.execute("CREATE TABLE RAT(" + "ratid varchar(200) not null,"
				+ "poiid varchar(200) not null," + "username varchar(200) ,"
				+ "rate int," + "updatetime long not null);");
		// "FOREIGN KEY(usernamefieldName) references USERS(username)," +
		// "FOREIGN KEY(poiid) references POI(poiid));");

		stmt.execute("CREATE TABLE COM(" + "comid varchar(200) not null,"
				+ "poiid varchar(200) not null," + "username varchar(200) ,"
				+ "content text," + "updatetime long not null);");
		// "FOREIGN KEY(username) references USERS(username)," +
		// "FOREIGN KEY(poiid) references POI(poiid));");

		stmt.execute("CREATE TABLE TAG(" + "tagid varchar(200) not null,"
				+ "poiid varchar(200) not null," + "updatetime long not null);");
		// "FOREIGN KEY(username) references USERS(username)," +
		// "FOREIGN KEY(poiid) references POI(poiid));");

		stmt.execute("CREATE TABLE KEY(" + "tagid varchar(200) not null,"
				+ "content text not null," + "updatetime long not null);");

		stmt.close();
		conn.close();

	}

	private Connection openConnection(Connection conn) throws Exception {
		Class.forName("org.h2.Driver");
		return conn = DriverManager.getConnection(
				"jdbc:h2:tcp//localhost/~/POI_SERVER_DATABASE", "jackw", "");
	}

	public void saveCommentstoDatabase(String comList) throws Exception {
		Connection conn = null;
		conn = openConnection(conn);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(comList, JsonNode.class);
		Iterator<JsonNode> iterator = rootNode.getElements();
		while (iterator.hasNext()) {
			String comsList = iterator.next().getTextValue();
			JsonNode comsNode = mapper.readValue(comsList, JsonNode.class);

			String comid = comsNode.path("comid").getTextValue();
			String poiid = comsNode.path("poiid").getTextValue();
			String username = comsNode.path("username").getTextValue();
			String content = comsNode.path("content").getTextValue();
			long updatetime = Calendar.getInstance().getTimeInMillis();

			PreparedStatement selectStmt = conn
					.prepareStatement("SELECT comid FROM COM WHERE comid=?");
			selectStmt.setString(1, comid);
			ResultSet res = selectStmt.executeQuery();
			if (!res.next()) {

				PreparedStatement insertStmt = conn
						.prepareStatement("INSERT INTO COM(comid, poiid, username,content,updatetime) VALUES (?,?,?,?,?)");
				insertStmt.setString(1, comid);
				insertStmt.setString(2, poiid);
				insertStmt.setString(3, username);
				insertStmt.setString(4, content);
				insertStmt.setLong(5, updatetime);
				insertStmt.execute();
				System.out.println(insertStmt.toString());
			}
			// this is for update
			else {

			}
		}
		conn.close();
	}

	public void saveKeystoDatabase(String keysList) throws Exception {
		Connection conn = null;
		conn = openConnection(conn);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(keysList, JsonNode.class);
		Iterator<JsonNode> iterator = rootNode.getElements();
		while (iterator.hasNext()) {
			String keyStr = iterator.next().getTextValue();
			JsonNode keyNode = mapper.readValue(keyStr, JsonNode.class);
			String tagid = keyNode.path("tagid").getTextValue();
			String content = keyNode.path("content").getTextValue();
			long updatetime = Calendar.getInstance().getTimeInMillis();
			PreparedStatement selectStmt = conn
					.prepareStatement("SELECT content FROM KEY WHERE content=?");
			selectStmt.setString(1, content);
			ResultSet res = selectStmt.executeQuery();
			// insert
			if (!res.next()) {
				PreparedStatement insertStmt = conn
						.prepareStatement("INSERT INTO KEY(tagid, content, updatetime) VALUES (?,?,?)");
				insertStmt.setString(1, tagid);
				insertStmt.setString(2, content);
				insertStmt.setLong(3, updatetime);
				insertStmt.execute();
				System.out.println(insertStmt.toString());
			}
			// update
			else {

			}
		}
		conn.close();
	}

	public void savePhototoDatabase(String picList) throws Exception {
		Connection conn = null;
		conn = openConnection(conn);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(picList, JsonNode.class);
		Iterator<JsonNode> iterator = rootNode.getElements();
		while (iterator.hasNext()) {
			String picStr = iterator.next().getTextValue();
			JsonNode picNode = mapper.readValue(picStr, JsonNode.class);
			String picid = picNode.path("picid").getTextValue();
			String poiid = picNode.path("poiid").getTextValue();
			String username = picNode.path("username").getTextValue();
			String smallpic = picNode.path("smallpic").getTextValue();
			String bigpic = picNode.path("bigpic").getTextValue();
			long updatetime = Calendar.getInstance().getTimeInMillis();
			;
			PreparedStatement selectStmt = conn
					.prepareStatement("SELECT picid FROM PIC WHERE picid=?");
			selectStmt.setString(1, picid);
			ResultSet res = selectStmt.executeQuery();
			// insert
			if (!res.next()) {

				PreparedStatement insertStmt = conn
						.prepareStatement("INSERT INTO PIC(picid, poiid, username,smallpic,bigpic,updatetime) VALUES (?,?,?,?,?,?)");
				insertStmt.setString(1, picid);
				insertStmt.setString(2, poiid);
				insertStmt.setString(3, username);
				insertStmt.setString(4, smallpic);
				insertStmt.setString(5, bigpic);
				insertStmt.setLong(6, updatetime);
				insertStmt.execute();
				System.out.println(insertStmt.toString());
			}
			// update
			else {

			}

		}
		conn.close();
	}

	// this method will check this poi and may insert update or delete it
	public void savePOItoDatabase(String poiList) throws Exception {
		Connection conn = null;
		conn = openConnection(conn);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(poiList, JsonNode.class);
		Iterator<JsonNode> iterator = rootNode.getElements();
		while (iterator.hasNext()) {
			String poiStr = iterator.next().getTextValue();
			JsonNode poiNode = mapper.readValue(poiStr, JsonNode.class);
			String poiid = poiNode.path("poiid").getTextValue();
			String state = poiNode.path("state").getTextValue();
			// delete everything with this delted poi
			// if(state.equalsIgnoreCase("deleted")){
			// //can you believe that H2 doesnt support delete from muti-tables
			// with one clue, if you know how please tell me
			// PreparedStatement deleteCommentsStmt = conn.prepareStatement(
			// "DELETE FROM com WHERE poiid=?");
			// deleteCommentsStmt.setString(1, poiid);
			// deleteCommentsStmt.execute();
			// System.out.println("The revelant comments was deleted");
			//
			// //try to delete all the key with this poi
			// PreparedStatement selectTagStmt = conn.prepareStatement(
			// "SELECT tagid FROM tag WHERE poiid=?");
			// selectTagStmt.setString(1, poiid);
			// ResultSet res = selectTagStmt.executeQuery();
			// if(res.next()){
			// String tagid=res.getString("tagid");
			// PreparedStatement deleteKeyStmt = conn.prepareStatement(
			// "DELETE FROM key WHERE tagid=?");
			// deleteKeyStmt.setString(1, tagid);
			// System.out.println("The revelant keys was deleted");
			// }
			//
			//
			// PreparedStatement deleteTagsStmt = conn.prepareStatement(
			// "DELETE FROM tag WHERE poiid=?");
			// deleteTagsStmt.setString(1, poiid);
			// deleteTagsStmt.execute();
			// System.out.println("The revelant tags was deleted!");
			//
			// PreparedStatement deletePicsStmt = conn.prepareStatement(
			// "DELETE FROM pic WHERE poiid=?");
			// deletePicsStmt.setString(1, poiid);
			// deletePicsStmt.execute();
			// System.out.println("The revelant pics was deleted");
			//
			// PreparedStatement deleteRatesStmt = conn.prepareStatement(
			// "DELETE FROM rat WHERE poiid=?");
			// deleteRatesStmt.setString(1, poiid);
			// deleteRatesStmt.execute();
			// System.out.println("The revelant rates was deleted");
			//
			// PreparedStatement deletePOIStmt = conn.prepareStatement(
			// "DELETE FROM poi WHERE poiid=?");
			// deletePOIStmt.setString(1, poiid);
			// deletePOIStmt.execute();
			// System.out.println(poiid+"poi entity has been deleted!");
			//
			// }
			// else{
			System.out.println("this is the state of poi: " + state);
			String title = poiNode.path("title").getTextValue();
			String des = poiNode.path("des").getTextValue();
			int lat = poiNode.path("lat").getIntValue();
			int lng = poiNode.path("lng").getIntValue();
			String username = poiNode.path("username").getTextValue();
			long timestamp = Calendar.getInstance().getTimeInMillis();
			// System.out.println(new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp));
			PreparedStatement checkStmt = conn
					.prepareStatement("SELECT updatetime FROM POI WHERE poiid = ?");
			checkStmt.setString(1, poiid);
			ResultSet res = checkStmt.executeQuery();
			if (!res.next()) {
				PreparedStatement insertStmt = conn
						.prepareStatement("INSERT INTO POI(poiid, lat, lng, title,des,username, updatetime,state) VALUES (?,?,?,?,?,?,?,?)");
				insertStmt.setString(1, poiid);
				insertStmt.setInt(2, lat);
				insertStmt.setInt(3, lng);
				insertStmt.setString(4, title);
				insertStmt.setString(5, des);
				insertStmt.setString(6, username);
				insertStmt.setLong(7, timestamp);
				insertStmt.setString(8, state);
				insertStmt.execute();
				System.out.println(insertStmt.toString());
			} else {
				if (state.equalsIgnoreCase("deleted")) {
					PreparedStatement updateStmt = conn
							.prepareStatement("UPDATE POI SET lat=?, lng=?, title=?, des=?, username =?, updatetime=?, state=? WHERE poiid=?");
					updateStmt.setInt(1, lat);
					updateStmt.setInt(2, lng);
					updateStmt.setString(3, title);
					updateStmt.setString(4, des);
					updateStmt.setString(5, username);
					updateStmt.setLong(6, timestamp);
					updateStmt.setString(7, state);
					updateStmt.setString(8, poiid);
					updateStmt.execute();
					System.out.println(updateStmt.toString());
				} else {
					PreparedStatement selectStmt = conn
							.prepareStatement("SELECT state FROM poi WHERE poiid=?");
					selectStmt.setString(1, poiid);
					ResultSet resState = selectStmt.executeQuery();
					if (resState.next()) {
						String stateInDB = resState.getString("state");
						if (stateInDB.equalsIgnoreCase("deleted")) {
							System.out
									.println("it has been deleted, you cant recover it");
						} else {
							PreparedStatement updateStmt = conn
									.prepareStatement("UPDATE POI SET lat=?, lng=?, title=?, des=?, username =?, updatetime=?, state=? WHERE poiid=?");
							updateStmt.setInt(1, lat);
							updateStmt.setInt(2, lng);
							updateStmt.setString(3, title);
							updateStmt.setString(4, des);
							updateStmt.setString(5, username);
							updateStmt.setLong(6, timestamp);
							updateStmt.setString(7, state);
							updateStmt.setString(8, poiid);
							updateStmt.execute();
							System.out.println(updateStmt.toString());
						}
					}
				}
			}
			// }
		}
		conn.close();
	}

	public void saveRatestoDatabase(String ratesList) throws Exception {
		Connection conn = null;
		conn = openConnection(conn);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(ratesList, JsonNode.class);
		Iterator<JsonNode> iterator = rootNode.getElements();
		while (iterator.hasNext()) {
			String ratStr = iterator.next().getTextValue();
			JsonNode ratNode = mapper.readValue(ratStr, JsonNode.class);
			String ratid = ratNode.path("ratid").getTextValue();
			String poiid = ratNode.path("poiid").getTextValue();
			String username = ratNode.path("username").getTextValue();
			int rate = ratNode.path("rate").getIntValue();
			long updatetime = Calendar.getInstance().getTimeInMillis();
			PreparedStatement selectStmt = conn
					.prepareStatement("SELECT ratid FROM RAT WHERE ratid=?");
			selectStmt.setString(1, ratid);
			ResultSet res = selectStmt.executeQuery();
			// insert
			if (!res.next()) {

				PreparedStatement insertStmt = conn
						.prepareStatement("INSERT INTO RAT(ratid, poiid, username,rate,updatetime) VALUES (?,?,?,?,?)");
				insertStmt.setString(1, ratid);
				insertStmt.setString(2, poiid);
				insertStmt.setString(3, username);
				insertStmt.setInt(4, rate);
				insertStmt.setLong(5, updatetime);
				insertStmt.execute();
				System.out.println(insertStmt.toString());
			}
			// update
			else {

			}

		}
		conn.close();
	}

	public void saveTagstoDatabase(String tagsList) throws Exception {
		Connection conn = null;
		conn = openConnection(conn);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(tagsList, JsonNode.class);
		Iterator<JsonNode> iterator = rootNode.getElements();
		while (iterator.hasNext()) {
			String tagStr = iterator.next().getTextValue();
			JsonNode tagNode = mapper.readValue(tagStr, JsonNode.class);
			String tagid = tagNode.path("tagid").getTextValue();
			String poiid = tagNode.path("poiid").getTextValue();
			long updatetime = Calendar.getInstance().getTimeInMillis();
			PreparedStatement selectStmt = conn
					.prepareStatement("SELECT tagid FROM TAG WHERE tagid=?");
			selectStmt.setString(1, tagid);
			ResultSet res = selectStmt.executeQuery();
			// insert
			if (!res.next()) {
				PreparedStatement insertStmt = conn
						.prepareStatement("INSERT INTO TAG(tagid, poiid, updatetime) VALUES (?,?,?)");
				insertStmt.setString(1, tagid);
				insertStmt.setString(2, poiid);
				insertStmt.setLong(3, updatetime);
				insertStmt.execute();
				System.out.println(insertStmt.toString());
			}
			// update
			else {

			}
		}
		conn.close();
	}

}
