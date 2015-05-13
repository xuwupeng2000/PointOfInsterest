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

@WebServlet("/DRAT")
public class DownloadRateServlet extends HttpServlet {
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

	public DownloadRateServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		dba = new SyncingDatabaseHelper();
		String lastTime = convertStreamToString(request.getInputStream());

		try {
			String ratesListStr = dba.fetchNewRates(lastTime);
			PrintWriter out = response.getWriter();
			if (ratesListStr.length() > 8) {
				out.println(ratesListStr);
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
