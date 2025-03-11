package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.User;
import dao.AdminDAO;
import utils.ConnectionHandler;

/**
 * Servlet implementation class SendPrezzo
 */
@WebServlet("/SendPrezzo")
public class SendPrezzo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendPrezzo() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init() throws ServletException{
    	ServletContext servletContext = getServletContext();
    	this.connection = ConnectionHandler.getConnection(servletContext);
    }
    
    @Override
    public void destroy()
    {
    	try {
    		ConnectionHandler.closeConnection(connection);
    	}
    	catch (SQLException e) {
			e.printStackTrace();
		}
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String preventiveIdString = request.getParameter("preventivoId");
		String priceString = request.getParameter("price");
		if(preventiveIdString == null || priceString == null)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("value of parameter is null");
		}
		
		int preventiveId = 0;
		double price = 0;
		try {
			preventiveId = Integer.parseInt(preventiveIdString);
			price = Double.parseDouble(priceString);
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		HttpSession session = request.getSession(false);
		User currentUser = (User)session.getAttribute("currentUser");
		AdminDAO adminDAO = new AdminDAO(connection, currentUser.getId());
		
		try {
			adminDAO.sendPreventive(preventiveId, price);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		
	}

}
