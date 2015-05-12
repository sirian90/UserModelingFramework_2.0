package org.rm3umf.persistenza.postgreSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import util.Cronometro;

import java.sql.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Signal;
import org.rm3umf.domain.User;
import org.rm3umf.persistenza.ConceptDAO;
import org.rm3umf.persistenza.PersistenceException;
import org.rm3umf.persistenza.SignalDAO;

import com.mysql.jdbc.Blob;

public class SignalDAOpostgreSQL implements SignalDAO{

	private ConceptDAO conceptDAO=new ConceptDAOpostgreSQL();

	public void save(Signal signal) throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();

		PreparedStatement statement = null;
		try {			
			String insert = "insert into usermodeling.signal(conceptid,userid,`signal`) values (?,?,?)";
			statement = connection.prepareStatement(insert);
			statement.setString(1, signal.getConcept().getId());
			statement.setLong(2, signal.getUser().getIduser());
			//statement.setBlob(3, signal);

			double[] arrayFloat=signal.getSignal();
			
			String arr = arrayToString(arrayFloat);
			statement.setString(3,arr);
			
			// ERRORE SUL CREATEOF ARRAY
			
//			Double[] array=new Double[arrayFloat.length];
//			//trasformo l'array di float in un array di Float
//			for(int i=0;i<arrayFloat.length;i++){
//				array[i]=Double.valueOf(arrayFloat[i]);
//			}
			
			
			
			
			
			
			
			statement.executeUpdate();		
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw new PersistenceException(e.getMessage());
		}
		finally {
			try {
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}
	
	
	public static String arrayToString(double[] arrayFloat) {
		String separator = ",";
		
	    StringBuilder result = new StringBuilder();
	    if (arrayFloat.length > 0) {
	        result.append(arrayFloat[0]);
	        for (int i=1; i<arrayFloat.length; i++) {
	            result.append(separator);
	            result.append(arrayFloat[i]);
	        }
	    }
	    return result.toString();
	}
	
	
    
	/**
	 * Cancella la relazione signal
	 */

	public void deleteAll() throws PersistenceException {
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = ds.getConnection();
		PreparedStatement statement = null;
		try {
			String update = "delete from usermodeling.signal ";
			statement = connection.prepareStatement(update);
			statement.executeUpdate();
		}
		catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
		finally {
			try {
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	

	public List<Signal> doRetrieveByUser(User user) throws PersistenceException {
		Signal signal=null;
		List<Signal> signalUser=new LinkedList<Signal>();
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "select conceptid,`signal` from usermodeling.`signal` where userid=?";
			statement = connection.prepareStatement(retrieve);
			statement.setLong(1, user.getIduser());
			result = statement.executeQuery();
			while (result.next()) {
				//System.out.println("creo oggetti signal:"+Cronometro.getInstance());
				signal = new Signal();
				//Setto il concept
				Concept concept=new ConceptProxy();
				concept.setId(result.getString(1));
				signal.setConcept(concept);
				//setto l'utente
				signal.setUser(user);
				
				//XXX vorrei trovare un modon po pi� efficiente ma non so come fare
				
				String arraySignal = result.getString(2);
				
				String[] signalSplit = arraySignal.split(",");
				
				double arrayFloat[] = new double[signalSplit.length];
				
				for(int i=0; i<signalSplit.length; i++){
					
					arrayFloat[i] = Double.valueOf(signalSplit[i]);
					
					
					
				}
				
				
				// ARRU STYLE
				
//				Array array=result.getArray(2);
//				ResultSet rs=array.getResultSet();
//				List<Float> list= new LinkedList<Float>(); 
//				while(rs.next()){
//					list.add(rs.getFloat(2));
//				}
//				double arrayFloat[]=new double[list.size()];
//
//				for(int i=0; i<list.size();i++){
//					arrayFloat[i]=list.get(i);
//				}
				
				
				signal.setSignal(arrayFloat);
				signalUser.add(signal);
			}
		}

		catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
		finally {
			try {
				if (result != null)
					result.close();
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return signalUser;
	}

	@Override
	public void delete(Signal signal) throws PersistenceException {
		//Cronometro.getInstance().avanza();

		//Identifica il sengnale da cancellare
		long userid = signal.getUser().getIduser();
		String conceptid=signal.getConcept().getId();
		
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "DELETE FROM usermodeling.signal " +
				              "WHERE userid=? and conceptid=?";
			statement = connection.prepareStatement(retrieve);
			statement.setLong(1, userid);
			statement.setString(2, conceptid);
			statement.executeUpdate();
		}catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
		finally {
			try {
				if (result != null)
					result.close();
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	
	public List<Long> retriveUserid() throws PersistenceException {
		List<Long> users=new LinkedList<Long>();
		DataSourcePostgreSQL ds = DataSourcePostgreSQL.getInstance();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			connection = ds.getConnection();
			String retrieve = "SELECT distinct( userid ) " +
					          "FROM usermodeling.signal ";
			statement = connection.prepareStatement(retrieve);
			result = statement.executeQuery();
			
			while (result.next()) {
				Long userid  = result.getLong(1);
				users.add(userid);
			}
		}
		catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}
		finally {
			try {
				if (result != null)
					result.close();
				if (statement != null) 
					statement.close();
				if (connection!= null)
					connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return users;
	}
	
	public static void main(String[] args) throws PersistenceException {
		SignalDAOpostgreSQL s = new SignalDAOpostgreSQL();
		
		User u = new User();
		u.setIduser(12345);
		
		
		Signal signal = new Signal();
		
		
		Concept c = new Concept();
		
		c.setId("abc123");
		c.setNameConcept("abc");
		
		
		signal.setUser(u);
		double[] asd = {21.24, 22.45};
		signal.setSignal(asd);
		signal.setConcept(c);
		s.save(signal);
		
		
		List<Signal> asljd = s.doRetrieveByUser(u);
		
		for (Signal sig : asljd){
			
			System.out.println(sig.toString());
			
			
		}
		
		
		
		
	}


}
