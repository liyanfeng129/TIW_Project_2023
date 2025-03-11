package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;



import beans.*;

public class ClientDao {
	private int clientId;
	private Connection conn;
	
	public ClientDao(Connection conn, int clientId)
	{
		this.conn = conn;
		this.clientId = clientId;
	}
	
	public List<Preventive> getPreventivesByUserId () throws SQLException // for list view in clientHomeHtml
	{
		List<Preventive> preventives = new ArrayList<>();
		String description = "Try getting list of preventive associated to user id";
		
		String query = "SELECT P.id, PP.name as productName, P.creationDate"
				+ " FROM gestione_preventivi.preventives as P,"
				+ " gestione_preventivi.products as PP"
				+ " Where P.idClient = ? And"
				+ " P.idProduct = PP.id;";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, clientId);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next())
			{
				Preventive preventive = new Preventive();
				preventive.setProductName(resultSet.getString("productName"));
				preventive.setId(resultSet.getInt("id"));
				preventive.setCreationDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(resultSet.getTimestamp("creationDate").getTime()));
				//preventive.setCreationDate(resultSet.getDate("creationDate"));
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

	public void sendPreventive(int productId, int[] options) throws SQLException{
		String description = "Insert a new preventive for client";
		
		String queryPreventive = "INSERT INTO `gestione_preventivi`.`preventives` (`idClient`, `idProduct`) VALUES (?, ?)";
		String preventiveIdString = "SELECT id FROM gestione_preventivi.preventives "
				+ "where creationDate = (select Max(creationDate) FROM gestione_preventivi.preventives) and "
				+ "idClient = ?";
		String queryOptionString = String.format("INSERT INTO `gestione_preventivi`.`preventive_ops` (`idPreventive`, `idOption`) VALUES ( (%s) , ?)", preventiveIdString);
		PreparedStatement preventivePreparedStatement = null;
		PreparedStatement optionPreparedStatement = null;
		
		try {
			conn.setAutoCommit(false);
			preventivePreparedStatement = conn.prepareStatement(queryPreventive);
			preventivePreparedStatement.setInt(1, clientId);
			preventivePreparedStatement.setInt(2, productId);
			preventivePreparedStatement.executeUpdate();
			for(int i = 0; i < options.length ; i++)
			{
				optionPreparedStatement = conn.prepareStatement(queryOptionString);
				optionPreparedStatement.setInt(1, clientId);
				optionPreparedStatement.setInt(2, options[i]);
				optionPreparedStatement.executeUpdate();
			}
			conn.commit();
			
		} catch (Exception e) {
			conn.rollback();
			throw new SQLException("Error occoured when " + description);
		}
		finally {
			conn.setAutoCommit(true);
			try {
				preventivePreparedStatement.close();
				optionPreparedStatement.close();
			}
			catch (Exception e) {
				throw new SQLException("Error occoured closing statement when " + description);
			}
			
		}
	}
	

}
