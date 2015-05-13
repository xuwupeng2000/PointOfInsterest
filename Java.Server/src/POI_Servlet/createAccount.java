package POI_Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAOs.userDAO;

import com.google.gson.Gson;
import com.sync.SyncingDatabaseHelper;

import domain.User;

/**
 * Servlet implementation class createAccount
 */
@WebServlet(description = "return true if created, or false if not been created", urlPatterns = { "/createAccount" })
public class createAccount extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public createAccount() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		userDAO userdao = new userDAO();
		String id = "exsisted";
		Gson gson = new Gson();
		// add a new account
		try {

			String rs = userdao.findID(username, password);
			if (rs.equals("exsisted")) {
				UUID uuid = UUID.randomUUID();
				id = String.valueOf(uuid);
				User newuser = new User(username, password, id);
				if (userdao.creat(newuser)) {
					System.out.println("new account has been create!!");
					// String array=gson.toJson(result);
					System.out.println(id);
					out.write(gson.toJson(id));
					// out.write(array);
					out.flush();
					out.close();
				}
			} else {
				System.out.println(id);
				out.write(gson.toJson(id));
				// out.write(array);
				out.flush();
				out.close();
				System.out.println("this is already exsisted");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);

	}
	
	//when the auth servlet start at the first time it will create the data schema
	//if we want to save the data comment it
	//cheers
	public void init() throws ServletException {
			super.init();
			SyncingDatabaseHelper dba = new SyncingDatabaseHelper();
			try {
				dba.initDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("init executed");
}
	
	
	
}
