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
import dao.ClientDao;
import utils.ConnectionHandler;


/**
 * Servlet implementation class SendPreventivo
 */
@WebServlet("/SendPreventivo")
public class SendPreventivo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
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
     * @see HttpServlet#HttpServlet()
     */
    public SendPreventivo() {
        super();
        // TODO Auto-generated constructor stub
    }

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String []optionStrings = request.getParameterValues("selectedOptions");
		String productIdString = request.getParameter("productId");
		
		if(productIdString == null)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Empty product id");
		}
		
		if(optionStrings.length < 1)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("No option selected");
		}
		int productId = 0;
		int options[]; 
		options = new int[optionStrings.length];
		try {
			productId = Integer.parseInt(productIdString);
			for(int i = 0; i < optionStrings.length; i++)
				options[i] = Integer.parseInt(optionStrings[i]);
		} catch (NumberFormatException e) {
			
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		HttpSession session = request.getSession(false);
		User currentUser = (User)session.getAttribute("currentUser");
		
		ClientDao clientDao = new ClientDao(connection, currentUser.getId());
		
		try {
			clientDao.sendPreventive(productId, options);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
	}

}
