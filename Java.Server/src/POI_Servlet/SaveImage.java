package POI_Servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAOs.imageDAO;
import DAOs.userDAO;

import com.google.gson.Gson;

import domain.Image;
import domain.User;

/**
 * Servlet implementation class saveImage
 */
@WebServlet(description = "return true if saved, or false if not", urlPatterns = {"/SaveImage"})
public class SaveImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveImage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		System.out.println("get into my class");
		response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String image = request.getParameter("image");
        String id = request.getParameter("id");

        boolean result = false;
        if(image.equals("image")){
            result=true;
        }
//        imageDAO imagedao = new imageDAO();
////        if("imgage".equals(image)){
////            result=true;
////        }
//        //add a new account
//        try {
//            //if(!id.isEmpty()&&!image.isEmpty()){
//            Image newimage = new Image(id, image);
//            result = imagedao.creat(newimage);
//            
//            if (result) {
//                System.out.println("image has been saved");
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//        	System.out.println("image not saved");
//            e.printStackTrace();
//        }

        Gson gson = new Gson();
        out.println(result);
        out.write(gson.toJson(result));
        out.flush();
        out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
