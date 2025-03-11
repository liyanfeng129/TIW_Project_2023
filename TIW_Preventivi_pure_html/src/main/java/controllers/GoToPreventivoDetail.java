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

import beans.Option;
import beans.Preventive;
import beans.User;
import dao.PreventiveDAO;
import utils.ConnectionHandler;
import utils.PathUtil;
import utils.TemplateHandler;

/**
 * Servlet implementation class GoToPreventivoDetail
 */
@WebServlet("/GoToPreventivoDetail")
public class GoToPreventivoDetail extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;
	private TemplateEngine templateEngine;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToPreventivoDetail() {
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
		String preventiveIdString = request.getParameter("preventiveId");
		
		if(preventiveIdString == null) {
			forwardToErrorPage(request, response, "empty preventive id");
		}
		
		int preventiveId;
		try {
				preventiveId = Integer.parseInt(preventiveIdString);
		} catch (NumberFormatException e) {
			forwardToErrorPage(request, response, "invalid preventive id");
			return;
		}
		
		HttpSession session = request.getSession(false);
		User currenUser = (User)session.getAttribute("currentUser");
		
		PreventiveDAO preventiveDAO = new PreventiveDAO(conn, preventiveId);
		Preventive preventive;
		
		try {
			preventive = preventiveDAO.getPreventiveById();
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		
		if(preventive == null || (currenUser.getPermission().equals("client") ? preventive.getClientId() != currenUser.getId() : preventive.getOperatorId() != currenUser.getId()))
		{
			forwardToErrorPage(request, response, "Preventive doesn't exist or is not yours");
			return;
		}
		
		List<Option> listOption;
		try {
			listOption = preventiveDAO.getOptionsById();
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		
		for(Option op : listOption)
			if(op.getPreventiveId() != preventive.getId() )
			{
				forwardToErrorPage(request, response,"Error occurred when gettion product options");
				return;
			}
		
		request.setAttribute("preventive", preventive);
		request.setAttribute("listOption", listOption);
		forward(request, response, PathUtil.pathToPreventiveDetail);
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
