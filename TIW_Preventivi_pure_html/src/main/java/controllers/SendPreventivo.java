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
import dao.ClientDao;
import utils.ConnectionHandler;
import utils.PathUtil;
import utils.TemplateHandler;

/**
 * Servlet implementation class SendPreventivo
 */
@WebServlet("/SendPreventivo")
public class SendPreventivo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;
	private TemplateEngine templateEngine;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendPreventivo() {
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
		String []optionStrings = request.getParameterValues("selectedOptions[]");
		String productIdString = request.getParameter("productId");
		
		if(productIdString == null)
		{
			forwardToErrorPage(request, response, "empty product id");
			return;
		}
		
		if(optionStrings == null)
		{
			forwardToErrorPage(request, response, "No option selected");
			return;
		}
		int productId = 0;
		int options[]; 
		options = new int[optionStrings.length];
		try {
			productId = Integer.parseInt(productIdString);
			for(int i = 0; i < optionStrings.length; i++)
				options[i] = Integer.parseInt(optionStrings[i]);
		} catch (NumberFormatException e) {
			
			forwardToErrorPage(request, response, "Error occoured when converting parameters into integer");
		}
		
		HttpSession session = request.getSession(false);
		User currentUser = (User)session.getAttribute("currentUser");
		
		ClientDao clientDao = new ClientDao(conn, currentUser.getId());
		
		try {
			clientDao.sendPreventive(productId, options);
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
		}
		
		response.sendRedirect(getServletContext().getContextPath() + PathUtil.goToHomeClientServletPath);
		
		
		
	}

}
