/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package POI_Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

import domain.PointOfInteresting;

/**
 * Servlet implementation class AndroidResponse
 */
@WebServlet("/AndroidResponse")
public class AndroidResponse extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private List<PointOfInteresting	> list =new ArrayList<PointOfInteresting>();//contains all points
	private List<PointOfInteresting	> listofPointsInRange =new ArrayList<PointOfInteresting>();
    /**
     * @see HttpServlet#HttpServlet()
     */
	public AndroidResponse() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 
		response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter(); 
        
        //to what action going to be depends on the value of action
        //could be create, update or delete a point
        String action=request.getParameter("action");
    	 
        if(action.equalsIgnoreCase("getPointsInRange")){
        	 
             String geoLat=request.getParameter("lat");
             String geoLng=request.getParameter("lng");
             String range=request.getParameter("range");
        	 sendPointsInRange(Integer.valueOf(geoLat),Integer.valueOf(geoLng),Integer.valueOf((range)));
         }
         if(action.equalsIgnoreCase("create")){
        	
             String geoLat=request.getParameter("lat");
             String geoLng=request.getParameter("lng");
        	 String title=request.getParameter("title");
        	 String body=request.getParameter("body");
        	 String address=request.getParameter("address");
        	 //JSONArray array=new JSONArray(response.toString());
        	 creatnewPoint(Integer.valueOf(geoLat),Integer.valueOf(geoLng),body, title, address);        	 
         }
         if(action.equalsIgnoreCase("update")){
        	
             String geoLat=request.getParameter("lat");
             String geoLng=request.getParameter("lng");
        	 String title=request.getParameter("title");
        	 String body=request.getParameter("body");
        	 String address=request.getParameter("address");
        	 updatePoint(Integer.valueOf(geoLat),Integer.valueOf(geoLng),body, title, address);
         }
         
         if(action.equalsIgnoreCase("delete")){
        	 
             String geoLat=request.getParameter("lat");
             String geoLng=request.getParameter("lng");
        	 delete(Integer.valueOf(geoLat),Integer.valueOf(geoLng));
         }
         
         Gson gson=new Gson();        
         String array=gson.toJson(listofPointsInRange);
         
         out.write(array);
         out.flush();
         out.close();
         
	}



	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}	

//	@Override
//	public void init() throws ServletException {
//		// TODO Auto-generated method stub
//		super.init();
//		if(list.isEmpty()){// you will dead by using "if(list.isempty())
//			 list = new ArrayList<PointOfInteresting>();
//			 PointOfInteresting np1 = new PointOfInteresting();
//	         np1.setLat((int) (-45.8677940*1E6));
//	         np1.setLng((int) (170.5148600*1E6));
//	         np1.setAddress("117 Albany St, Dunedin, New Zealand");
//	         np1.setTitle("Apple Lab");
//	         np1.setBody("This is INFO401");
//	         list.add(np1);
//	     
//	         PointOfInteresting np2 = new PointOfInteresting();
//	         np2.setLat((int) (-45.866352*1E6));
//	         np2.setLng((int) (170.516025*1E6));
//	         np2.setAddress("60 Clyde St, Dunedin, New Zealand");
//	         np2.setTitle("Commerce Building");
//	         np2.setBody("This is the department of information science");
//	         list.add(np2); 
//		}
//	}
	
	/**
	 * 
	 *put all points in the range then return to Andriod
	 *  
	 * @param lat: current lat of geopoint
	 * @param lng: current lng of geopoint
	 * @param range
	 */	
	public void sendPointsInRange(int lat, int lng,int range){		
		  
		if(!list.isEmpty()){
			if(!listofPointsInRange.isEmpty()){
				listofPointsInRange.clear();
			}
			for(int i=0; i<list.size();i++){
				
				if((list.get(i).distanceto(lat, lng))<=range){
					PointOfInteresting p = new PointOfInteresting();
					p.setAddress(list.get(i).getAddress());
					p.setLat(list.get(i).getLat());
					p.setLng(list.get(i).getLng());
					p.setTitle(list.get(i).getTitle());
					p.setBody(list.get(i).getBody());
					if(!listofPointsInRange.contains(p)){
						listofPointsInRange.add(p);
					}
				}
			}			
		}
	}
	/*
	 * create a new point in serve
	 */	
	public void creatnewPoint(int lat, int lng, String body, String title, String address){
		
		PointOfInteresting p = new PointOfInteresting();
		p.setAddress(address);
		p.setLat(lat);
		p.setLng(lng);
		p.setTitle(title);
		p.setBody(body);
		
		if(!list.contains(p)){
			list.add(p);
		}
	}
	/*
	 * delete a point in serve
	 */
	private void delete(Integer lat, Integer lng) {
		// TODO Auto-generated method stub
		int p=0;
		if(!list.isEmpty()){
			for(int i=0; i<list.size();i++){
				if(list.get(i).IsGeopointSame(lat, lng)){
					p=i;
					list.remove(p);
					break;
				}
			}
			
		}
		
	}
	/*
	 * update a point in the serve
	 */
	private void updatePoint(Integer lat, Integer lng, String body,
			String title, String address) {
		// TODO Auto-generated method stub
		int p=0;
		if(!list.isEmpty()){
			for(int i=0; i<list.size();i++){
				if(list.get(i).IsGeopointSame(lat, lng)){
					p=i;
					break;
				}
			}
			list.get(p).setAddress(address);
			list.get(p).setBody(body);
			list.get(p).setLat(lat);
			list.get(p).setLng(lng);
			list.get(p).setTitle(title);
		}
	}

}

