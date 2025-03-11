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
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import beans.Preventive;
import beans.User;
import dao.ClientDao;
import utils.ConnectionHandler;

/**
 * Servlet implementation class GetClientPreventivoList
 */
@WebServlet("/GetClientPreventivoList")
public class GetClientPreventivoList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;   
    /**
     * @see HttpServlet#HttpServlet()
     */
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
	 
    public GetClientPreventivoList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User currentUser = (User)session.getAttribute("currentUser");
		
		ClientDao clientDao = new ClientDao(connection, currentUser.getId());
		List<Preventive> preventivos;
		
		try {
			preventivos = clientDao.getPreventivesByUserId();
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		String json = new Gson().toJson(preventivos);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		
		
	}

}
