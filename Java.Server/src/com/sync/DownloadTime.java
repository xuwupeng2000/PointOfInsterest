package com.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;


@WebServlet("/DT")
public class DownloadTime extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static SyncingDatabaseHelper dba;
       
  
    public DownloadTime() {
        super();
        // TODO Auto-generated constructor stub
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("DT DT");
		dba = new SyncingDatabaseHelper();
		String token = convertStreamToString(request.getInputStream());
		System.out.println("TIME TOKEN FROM Client: " +token);
		String time = Long.toString(fetchTime());
		PrintWriter out = response.getWriter();
		JsonFactory fac = new JsonFactory();
		StringWriter sw = new StringWriter();
		JsonGenerator gen = fac.createJsonGenerator(sw);
		gen.writeStartObject();
		gen.writeObjectField("time", time);
		gen.writeEndObject();
		gen.close();
		//
		out.println(sw.toString());
		System.out.print("TIME to client: "+sw.toString());
		out.close();
		
	}
	
	public long fetchTime(){
		long newUpdateTimeToClient = Calendar.getInstance().getTimeInMillis();
		return newUpdateTimeToClient;
	}

	
	private static String convertStreamToString(InputStream is) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
