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

import com.google.gson.Gson;

import beans.User;
import dao.UserDAO;
import packets.PacketUser;
import utils.ConnectionHandler;


/**
 * Servlet implementation class Signup
 */
@WebServlet("/Signup")
public class Signup extends HttpServlet {
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
    public Signup() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String permission = request.getParameter("permission");
		if(name == null || email == null || password == null || permission == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Mancano i dati");
			return;
		}
		
		UserDAO userDAO = new UserDAO(connection);
		
		User user = null;
		
		try {
			user = userDAO.getUserByEmail(email);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		if(user != null)
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("this email is used!");
			return;
		}
		
		try {
			userDAO.signupUser(name, email, password, permission);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		try {
			user = userDAO.getUserByEmail(email);
		}
		catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		HttpSession session = request.getSession();
		session.setAttribute("currentUser", user);
		
		String packetUser = new Gson().toJson(new PacketUser(user.getName(), user.getId(), user.getPermission()));
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(packetUser);
		
		
		
	}
}
