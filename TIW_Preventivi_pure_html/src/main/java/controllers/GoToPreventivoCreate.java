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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;


import beans.Option;
import beans.Product;
import dao.ProductDAO;
import utils.ConnectionHandler;
import utils.PathUtil;
import utils.TemplateHandler;

/**
 * Servlet implementation class GoToPreventivoCreate
 */
@WebServlet("/GoToPreventivoCreate")
public class GoToPreventivoCreate extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn;
	private TemplateEngine templateEngine;    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToPreventivoCreate() {
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
		String productIdString = request.getParameter("productId");
		
		if(productIdString == null)
		{
			forwardToErrorPage(request, response, "empty product id");
		}
		
		int productId;
		try {
			productId = Integer.parseInt(productIdString);
		} catch (NumberFormatException e) {
			forwardToErrorPage(request, response, "invalid product id");
			return;
		}
		
		
		ProductDAO productDAO = new ProductDAO(conn);
		Product product;
		
		try {
			product = productDAO.getProductById(productId);
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		
		if(product == null)
		{
			forwardToErrorPage(request, response, "Product doesn't exist");
			return;
		}
		
		List<Option> listOption;
		try {
			listOption = productDAO.getProductOptions(productId);
		} catch (SQLException e) {
			forwardToErrorPage(request, response, e.getMessage());
			return;
		}
		
		if(listOption.size() < 1)
		{
			forwardToErrorPage(request, response, "Product doesn't have option");
			return;
		}
		
		for(Option op : listOption)
		{
			if(op.getProductId() != productId)
			{
				forwardToErrorPage(request, response, "Product has a option does not belong to it");
				return;
			}
		}
		
		request.setAttribute("product", product);
		request.setAttribute("listOption", listOption);
		forward(request, response, PathUtil.pathToFormPreventivo);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
