package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import beans.Option;
import beans.Product;

public class ProductDAO {
	private Connection conn;

	public ProductDAO(Connection conn) {
		this.conn = conn;
	}
	

	public List<Product> getAllProduct() throws SQLException {
		List<Product> listProduct = new ArrayList<>();
		String description = "try getting all products from database";
		String query = "SELECT * FROM gestione_preventivi.products";
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		try {
			preparedStatement = conn.prepareStatement(query);
			resultset =  preparedStatement.executeQuery();
			while (resultset.next()) {
				Product product = new Product();
				product.setId(resultset.getInt("id"));
				product.setImgUrl(resultset.getString("imgUrl"));
				product.setName(resultset.getString("name"));
				listProduct.add(product);
			}
		} catch (SQLException e) {
			throw new SQLException("Error occured when " + description);
		}
		finally {
			try {
				resultset.close();
				preparedStatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2.getMessage());
			}
		}
		return listProduct;
	}


	public Product getProductById(int productId) throws SQLException{
		Product product = null;
		String description = "try get product information";
		String query = "Select * From gestione_preventivi.products where id = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, productId);
			resultset =  preparedStatement.executeQuery();
			
			while (resultset.next()) {
				product = new Product();
				product.setId(resultset.getInt("id"));
				product.setImgUrl(resultset.getString("imgUrl"));
				product.setName(resultset.getString("name"));
			}
		} catch (SQLException e) {
			throw new SQLException("Error occured when " + description);
		}
		finally {
			try {
				resultset.close();
				preparedStatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2.getMessage());
			}
		}
		return product;
	}


	public List<Option> getProductOptions(int productId) throws SQLException{
		List<Option> listOptions = new ArrayList<>();
		String description = "getting all options associated to this product";
		String query = "SELECT O.id, O.type, O.name, P.idProduct "
				+ "FROM gestione_preventivi.options as O, gestione_preventivi.product_ops as P "
				+ "where O.id = P.idOption and P.idProduct = ?";
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, productId);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				Option option = new Option();
				option.setId(resultSet.getInt("id"));
				option.setName(resultSet.getString("name"));
				option.setProductId(resultSet.getInt("idProduct"));
				option.setType(resultSet.getString("type"));
				listOptions.add(option);
			}
		} 
		catch (SQLException e) {
			throw new SQLException("Error when "+description);
		}
		finally {
			try {
				resultSet.close();
				preparedStatement.close();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return listOptions;
	}

}
