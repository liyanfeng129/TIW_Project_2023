package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import beans.*;

import beans.Preventive;

public class PreventiveDAO {
	private Connection connection;
	private int preventiveId;
	public PreventiveDAO(Connection connection, int preventiveId) {
		this.connection = connection;
		this.preventiveId = preventiveId;
	}
	
	public Preventive getPreventiveById()throws SQLException
	{
		/*
		 * Two sql query put together with an union, one query for when there's a operator associated to preventive, one for when there's not
		 * */
		Preventive preventive = null ;
		String description = "getting preventive by his id";
		String query = "SELECT P.id, U.id as idUser, U.name as clientName, O.name as operatorName, o.id as operatorId, "
				+ "PP.name as productName, P.price, P.creationDate, PP.imgUrl as imgUrl "
				+ "FROM gestione_preventivi.preventives as P, "
				+ "gestione_preventivi.users as U, "
				+ "gestione_preventivi.users as O, "
				+ "gestione_preventivi.products as PP "
				+ "Where P.id = ? And "
				+ "P.idClient = U.id And "
				+ "P.idOperator = O.id And "
				+ "P.idProduct = PP.id "
				+ "union "
				+ "SELECT P.id, U.id as idUser, U.name as clientName, null as operatorName, null as operatorId, "
				+ "PP.name as productName, P.price, P.creationDate, PP.imgUrl as imgUrl "
				+ "FROM gestione_preventivi.preventives as P, "
				+ "gestione_preventivi.users as U, "
				+ "gestione_preventivi.users as O, "
				+ "gestione_preventivi.products as PP "
				+ "Where P.id = ? And "
				+ "P.idClient = U.id And "
				+ "P.idOperator is null And "
				+ "P.idProduct = PP.id ";
		PreparedStatement preparedStatement = null;
		
		ResultSet resultSet = null;
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, preventiveId);
			preparedStatement.setInt(2, preventiveId);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {

				preventive = new Preventive();
				preventive.setProductName(resultSet.getString("productName"));
				preventive.setClientName(resultSet.getString("clientName"));
				preventive.setId(resultSet.getInt("id"));
				preventive.setOperatorName(resultSet.getString("operatorNAme"));
				preventive.setPrice(resultSet.getFloat("price"));
				preventive.setCreationDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(resultSet.getTimestamp("creationDate").getTime()));
				
				preventive.setImgUrl(resultSet.getString("imgUrl"));
				preventive.setClientId(resultSet.getInt("idUser"));
				preventive.setOperatorId(resultSet.getInt("operatorId"));
			}
		}
		catch (SQLException e) {
			throw new SQLException("Error when "+description);
		}
		finally {
			try {
				resultSet.close();
				preparedStatement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return preventive;
	}
	
	public List<Option> getOptionsById() throws SQLException
	{
		List<Option> listOptions = new ArrayList<>();
		String description = "getting all options associated to this preventive";
		String query = "SELECT O.id, O.type, O.name, P.idPreventive "
				+ "FROM gestione_preventivi.options as O, gestione_preventivi.preventive_ops as P "
				+ "where O.id = P.idOption and P.idPreventive = ?";
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, preventiveId);
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				Option option = new Option();
				option.setName(resultSet.getString("name"));
				option.setPreventiveId(resultSet.getInt("idPreventive"));
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
