package strat.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnection {
	private Connection connection;
	private Statement statement;
	
	public SQLConnection(String url, String user, String password)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection(url, user, password);
		statement = connection.createStatement();
	}
	
	public ResultSet executeQuery(String query) throws SQLException {
		return statement.executeQuery(query);
	}
	
	public int executeUpdate(String query) throws SQLException {
		return statement.executeUpdate(query);
	}
	
	public PreparedStatement prepareStatement(String statement) throws SQLException {
		return connection.prepareStatement(statement);
	}
}
