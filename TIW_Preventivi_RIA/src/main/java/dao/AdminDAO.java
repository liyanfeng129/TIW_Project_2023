package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import beans.Preventive;

public class AdminDAO {
	private Connection conn;
	private int adminId;
	public AdminDAO(Connection conn, int adminId)
	{
		this.conn = conn;
		this.adminId = adminId;
	}
	public List<Preventive> getAdminPreventives() throws SQLException{
		List<Preventive> preventives = new ArrayList<>();
		String description = "Try getting list of preventive associated to admin id";
		
		String query = "SELECT P.id, PP.name as productName, P.creationDate, U.name as clientName"
				+ " FROM gestione_preventivi.preventives as P,"
				+ " gestione_preventivi.products as PP,"
				+ " gestione_preventivi.users as U"
				+ " Where P.idOperator = ? And"
				+ " P.idProduct = PP.id And"
				+ "	P.idClient = U.id;";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, adminId);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next())
			{
				Preventive preventive = new Preventive();
				preventive.setProductName(resultSet.getString("productName"));
				preventive.setId(resultSet.getInt("id"));
				preventive.setCreationDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(resultSet.getTimestamp("creationDate").getTime()));
				preventive.setClientName(resultSet.getString("clientName"));
				preventives.add(preventive);
				
			}
		} catch (SQLException e) {
			throw new SQLException("Error occurred when " + description);
		}
		finally {
			try {
				resultSet.close();
				preparedStatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2.getMessage());
			}
		}
		return preventives;
	}
	public List<Preventive> getAllAvailablePreventive() throws SQLException{
		List<Preventive> preventives = new ArrayList<>();
		String description = "Try getting list of preventive available";
		
		String query = "SELECT P.id, PP.name as productName, P.creationDate, U.name as clientName"
				+ " FROM gestione_preventivi.preventives as P,"
				+ " gestione_preventivi.products as PP,"
				+ "	gestione_preventivi.users as U"
				+ " Where P.idOperator is null And"
				+ " P.idProduct = PP.id And"
				+ " P.idClient = U.id;";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = conn.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next())
			{
				Preventive preventive = new Preventive();
				preventive.setProductName(resultSet.getString("productName"));
				preventive.setId(resultSet.getInt("id"));
				preventive.setCreationDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(resultSet.getTimestamp("creationDate").getTime()));
				preventive.setClientName(resultSet.getString("clientName"));
				preventives.add(preventive);
				
			}
		} catch (SQLException e) {
			throw new SQLException("Error occurred when " + description);
		}
		finally {
			try {
				resultSet.close();
				preparedStatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2.getMessage());
			}
		}
		return preventives;
	}
	public void sendPreventive(int preventiveId, double price) throws SQLException {
		String description = "send price and operator id to database";
		String query = "UPDATE `gestione_preventivi`.`preventives` SET `idOperator` = ?, `price` = ? WHERE (`id` = ?)";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, adminId);
			preparedStatement.setDouble(2, price);
			preparedStatement.setInt(3, preventiveId);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException("Error occurred when " + description);
		}
		finally {
			try {
				preparedStatement.close();
			}
			catch (Exception e) {
				throw new SQLException("Error occoured closing statement when " + description);
			}
		}
		
	}
	
	
	

}
