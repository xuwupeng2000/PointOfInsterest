package com.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UPOI")
public class UpdatePOIsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static SyncingDatabaseHelper dba;

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

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	// public void init() throws ServletException {
	// super.init();
	// dba = new SyncingDatabaseHelper();
	// try {
	// dba.initDatabase();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// System.out.println("init executed");
	// }

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String poiStr = convertStreamToString(request.getInputStream());
		System.out.println(poiStr);
		try {
			dba = new SyncingDatabaseHelper();
			dba.savePOItoDatabase(poiStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
