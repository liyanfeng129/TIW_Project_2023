package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import beans.User;

public class UserDAO {

	private Connection conn;
	
	public UserDAO(Connection conn) {
		this.conn = conn;
	}
	
	public User findUserByCredentials(String email, String password) throws SQLException{
		User user = null;
		String description = "finding a user by email and password";
		String query = "SELECT * FROM gestione_preventivi.users where email = ? and password = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, password);
			resultSet =  preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				user = new User();
				user.setId(resultSet.getInt("id"));
				user.setName(resultSet.getString("name"));
				user.setEmail(resultSet.getString("email"));
				user.setPermission(resultSet.getString("permission"));
				
			}
		}
		catch (SQLException e) {
			throw new SQLException("Error occorred when"+description);
		}
		finally {
			try {
				resultSet.close();
				preparedStatement.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new SQLException("Error occured in the colosing stage when" + description);
			}
		}
		return user;
	}
	
	//TODO signupUser
	public void signupUser(String name, String eamil, String password, String permission) throws SQLException{
		String description = "signing up a new user in database";
		String query = "INSERT INTO `gestione_preventivi`.`users` (`email`, `password`, `permission`, `name`) VALUES (?, ?, ?, ?)";
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, eamil);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, permission);
			preparedStatement.setString(4, name);
			preparedStatement.executeUpdate();
			
		} 	catch (SQLException e) {
			throw new SQLException("Error occorred when "+description);
		}
		finally {
			try {
				
				preparedStatement.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new SQLException("Error occured in the colosing stage when" + description);
			}
		}
	}
	
	//TODO getUserByEmail
	
	public User getUserByEmail(String email) throws SQLException
	{
		User user = null;
		String description = "get user by email";
		String query = "SELECT * FROM gestione_preventivi.users Where email = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, email);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next())
			{
				user = new User();
				user.setId(resultSet.getInt("id"));
				user.setName(resultSet.getString("name"));
				user.setEmail(resultSet.getString("email"));
				user.setPermission(resultSet.getString("permission"));
			}
		} catch (SQLException e) {
			throw new SQLException("Error occorred when "+description);
		}
		finally {
			try {
				if(resultSet != null)
					resultSet.close();
				preparedStatement.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new SQLException("Error occured in the colosing stage when " + description);
			}
		}
		return user;
	}
	
	//TODO getUserBYId
	public User getUserById(int id)
	{
		User user = null;
		
		return user;
	}
	
}
