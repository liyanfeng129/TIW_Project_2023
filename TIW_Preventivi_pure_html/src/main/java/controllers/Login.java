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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import beans.User;
import dao.UserDAO;
import utils.ConnectionHandler;
import utils.PathUtil;
import utils.TemplateHandler;



/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;
	private TemplateEngine templateEngine;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
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
		System.out.println("In Init");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* TODO doPost*/
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		if(email == null || password == null) 
		{
			forwardToErrorPage(request, response, "Email or password is empty");
			return;
		}
		
		UserDAO userDAO = new UserDAO(conn);
		User user = null;
		
		try {
			user = userDAO.findUserByCredentials(email, password);
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		
		if(user == null) {
			request.setAttribute("warning", "Email oppure password incoretto!");
			forward(request, response, PathUtil.pathToLoginPage);
			return;
		}
		
		HttpSession session = request.getSession();
		session.setAttribute("currentUser", user);
		if(user.getPermission().equals("admin"))
			response.sendRedirect(getServletContext().getContextPath() + PathUtil.goToHomeAdminServletPath);
		else
			response.sendRedirect(getServletContext().getContextPath() + PathUtil.goToHomeClientServletPath);
		
			
	}

}
