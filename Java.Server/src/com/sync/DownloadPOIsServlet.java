package com.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DPOI")
public class DownloadPOIsServlet extends HttpServlet {
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

	public DownloadPOIsServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		dba = new SyncingDatabaseHelper();
		String lasttime = convertStreamToString(request.getInputStream());
		System.out.println("sever has reveived the lasttime = " + lasttime);
		try {
			String poiList = dba.fetchNewPOIs(lasttime);
			System.out.println("this is the poiList will transpot to client: "
					+ poiList);
			PrintWriter out = response.getWriter();
			if (poiList.length() > 8) {
				out.println(poiList);
				out.close();
			} else {
				out.print("nothing");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
