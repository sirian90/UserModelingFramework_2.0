package org.rm3umf.persistenza.postgreSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.rm3umf.persistenza.*;

public class DataSourcePostgreSQL implements DataSource{
	   private static DataSourcePostgreSQL instance = null;
	   private DataSourcePostgreSQL() {}
	   
	   public static DataSourcePostgreSQL getInstance() {
	      if(instance == null) {
	         instance = new DataSourcePostgreSQL();
	      }
	      return instance;
	   }
	   
		public Connection getConnection() throws PersistenceException {
			String driver = "com.mysql.jdbc.Driver";
			String dbURI = "jdbc:mysql://localhost/usermodeling";
			String userName = "root";
			String password = "";

			Connection connection;
			try {
			    Class.forName(driver);
			    connection = DriverManager.getConnection(dbURI,userName, password);
			} catch (ClassNotFoundException e) {
				throw new PersistenceException(e.getMessage());
			} catch(SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
			catch(Exception e) {
				throw new PersistenceException(e.getMessage());
			}
			return connection;
		}
	   
}