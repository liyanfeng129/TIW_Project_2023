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
import dao.AdminDAO;
import utils.ConnectionHandler;
import utils.PathUtil;
import utils.TemplateHandler;

/**
 * Servlet implementation class SendPrezzo
 */
@WebServlet("/SendPrezzo")
public class SendPrezzo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendPrezzo() {
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
		String preventiveIdString = request.getParameter("preventiveId");
		String priceString = request.getParameter("price");
		if(preventiveIdString == null || priceString == null)
		{
			forwardToErrorPage(request, response, "value of parameter is null");
			return;
		}
		int preventiveId = 0;
		double price = 0;
		try {
			preventiveId = Integer.parseInt(preventiveIdString);
			price = Double.parseDouble(priceString);
		} catch (NumberFormatException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		
		if(price <= 0.0 )
		{
			forwardToErrorPage(request, response, "Price must be a positive number");
			return;
		}
		
		HttpSession session = request.getSession(false);
		User currentUser = (User)session.getAttribute("currentUser");
		AdminDAO adminDAO = new AdminDAO(conn, currentUser.getId());
		
		try {
			adminDAO.sendPreventive(preventiveId, price);
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		
		response.sendRedirect(getServletContext().getContextPath() + PathUtil.goToHomeAdminServletPath);
	}

}
