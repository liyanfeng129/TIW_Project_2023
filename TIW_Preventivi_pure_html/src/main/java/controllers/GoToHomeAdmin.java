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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import beans.Preventive;
import beans.User;
import dao.AdminDAO;
import utils.ConnectionHandler;
import utils.PathUtil;
import utils.TemplateHandler;

/**
 * Servlet implementation class GoToHomeAdmin
 */
@WebServlet("/GoToHomeAdmin")
public class GoToHomeAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToHomeAdmin() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private void forwardToErrorPage(HttpServletRequest request, HttpServletResponse response, String error) throws IOException {
    	request.setAttribute("error", error);
    	forward(request, response, PathUtil.pathToErrorPage);
    	return;
    }

	private void forward(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
		ServletContext servletContext = getServletContext();
		final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, webContext, response.getWriter());
		
	}

	@Override
	public void destroy() {
		try
		{
			ConnectionHandler.closeConnection(conn);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		this.templateEngine = TemplateHandler.getEngine(servletContext, ".html");
		this.conn = ConnectionHandler.getConnection(servletContext);
	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User currentUser = (User)session.getAttribute("currentUser");
		//prepare view objects
		AdminDAO adminDAO = new AdminDAO(conn, currentUser.getId());
		List<Preventive> listPreventiveOld ;
		List<Preventive> listPreventiveAvailable;
		try {
			listPreventiveOld = adminDAO.getAdminPreventives();
			listPreventiveAvailable = adminDAO.getAllAvailablePreventive();
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		
		request.setAttribute("listPreventiveOld", listPreventiveOld);
		request.setAttribute("listPreventiveAvailable", listPreventiveAvailable);
		forward(request, response, PathUtil.pathToHomeAdminPage);
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
