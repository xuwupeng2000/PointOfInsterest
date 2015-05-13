/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package POI_Servlet;

import DAOs.poiDAO;
import com.google.gson.Gson;
import domain.PointOfInteresting;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Ray
 */
@WebServlet(name = "getPOIinRange", urlPatterns = {"/getPOIinRange"})
public class getPOIinRange extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<PointOfInteresting> listofPointsInRange =new ArrayList<PointOfInteresting>();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String range = request.getParameter("range");
        String lat = request.getParameter("lat");
        String lng = request.getParameter("lng");
        try {
            poiDAO poidao=new poiDAO();
            
            poidao.getPointsInRange(listofPointsInRange,Integer.valueOf(lat), Integer.valueOf(lng), Integer.valueOf(range));
            /* TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet getPOIinRange</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet getPOIinRange at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
             */
             Gson gson=new Gson();        
            String array=gson.toJson(listofPointsInRange);
            out.write(array);
            out.flush();
        } finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
