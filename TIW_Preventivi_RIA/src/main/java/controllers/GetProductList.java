package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import dao.ProductDAO;
import packets.PacketProduct;
import utils.ConnectionHandler;
import beans.Option;
import beans.Product;

/**
 * Servlet implementation class GetProductList
 */
@WebServlet("/GetProductList")
public class GetProductList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;   
	
	
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
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetProductList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductDAO productDAO = new ProductDAO(connection);
		
		List<Product> productList;
		
		try {
			productList = productDAO.getAllProduct();
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(e.getMessage());
			return;
		}
		
		List<PacketProduct> packetProducts = new ArrayList<>();
		
		for(int i = 0; i<productList.size(); i++)
		{
			List<Option> options;
			try {
				options = productDAO.getProductOptions(productList.get(i).getId());
			}catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println(e.getMessage());
				return;
			}
			
			PacketProduct packetProduct = new PacketProduct(productList.get(i).getName(),productList.get(i).getId(), productList.get(i).getImgUrl(), options);
			packetProducts.add(packetProduct);
		}
		
		
		String json = new Gson().toJson(packetProducts);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

}
