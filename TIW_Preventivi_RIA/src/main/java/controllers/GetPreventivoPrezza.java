package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import beans.Option;
import beans.Preventive;
import dao.PreventiveDAO;
import packets.PacketPreventivoDetails;
import utils.ConnectionHandler;

/**
 * Servlet implementation class GetPreventivoPrezza
 */
@WebServlet("/GetPreventivoPrezza")
public class GetPreventivoPrezza extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPreventivoPrezza() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
	public void init() throws ServletException{
		ServletContext servletContext = getServletContext();
    	this.connection = ConnectionHandler.getConnection(servletContext);
	}
    
    @Override
    public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String preventiveIdString = request.getParameter("preventivoId");
		
		if(preventiveIdString == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Empty preventiveId");
			return;
		}
		
		int preventiveId;
		try {
				preventiveId = Integer.parseInt(preventiveIdString);
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);		
			response.getWriter().println("Parameter is not an integer");
			return;
		}
		
		PreventiveDAO preventiveDAO = new PreventiveDAO(connection, preventiveId);
		Preventive preventive;
		
		try {
			preventive = preventiveDAO.getPreventiveById();
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		if(preventive == null || preventive.getOperatorId() != 0)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Preventive doesn't exist or it's not available");
			return;
		}
		
		List<Option> listOption;
		try {
			listOption = preventiveDAO.getOptionsById();
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		for(Option op : listOption)
			if(op.getPreventiveId() != preventive.getId() )
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Error occurred when gettion product options");
				return;
			}
		
		String json = new Gson().toJson(new PacketPreventivoDetails(preventive, listOption));
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		
		
		
	}

}
