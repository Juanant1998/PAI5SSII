package main;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MySQLAccess_T2 {
	
  private Connection connect = null;
  private Statement statement = null;
  private PreparedStatement preparedStatement = null;
  private ResultSet resultSet = null;

  final private String host = "localhost:3306";
  final private String user = "root";
  final private String passwd = "I$PP-C1ubby";
  
  
  public void readDataBase() throws Exception {
    try {
      // This will load the MySQL driver, each DB has its own driver
      Class.forName("com.mysql.jdbc.Driver");
      
      // Setup the connection with the DB
      connect = DriverManager
          .getConnection("jdbc:mysql://" + host + "/pai5?"
              + "user=" + user + "&password=" + passwd );

      // Statements allow to issue SQL queries to the database
      statement = connect.createStatement();
      // Result set get the result of the SQL query
      resultSet = statement
          .executeQuery("select * from pai5.comments");
      writeResultSet(resultSet);

      // PreparedStatements can use variables and are more efficient
      preparedStatement = connect
          .prepareStatement("insert into  pai5.comments values (default, ?, ?, ?, ? , ?, ?)");
      // "myuser, webpage, datum, summary, COMMENTS from pai5.comments");
      // Parameters start with 1
      preparedStatement.setString(1, "Test");
      preparedStatement.setString(2, "TestEmail");
      preparedStatement.setString(3, "TestWebpage");
      preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
      preparedStatement.setString(5, "TestSummary");
      preparedStatement.setString(6, "TestComment");
      preparedStatement.executeUpdate();

      preparedStatement = connect
          .prepareStatement("SELECT myuser, webpage, datum, summary, COMMENTS from pai5.comments");
      resultSet = preparedStatement.executeQuery();
      writeResultSet(resultSet);

      // Remove again the insert comment
      preparedStatement = connect
      .prepareStatement("delete from pai5.comments where myuser= ? ; ");
      preparedStatement.setString(1, "Test");
      preparedStatement.executeUpdate();
      
      resultSet = statement
      .executeQuery("select * from pai5.comments");
      writeMetaData(resultSet);
      
    } catch (Exception e) {
      throw e;
    } finally {
      close();
    }

  }
  
  public void generaLog() throws Exception {
	  Class.forName("com.mysql.jdbc.Driver");
	       
	       // Setup the connection with the DB
	       connect = DriverManager
	           .getConnection("jdbc:mysql://" + host + "/pai5?"
	               + "user=" + user + "&password=" + passwd );

	       // Statements allow to issue SQL queries to the database
	       statement = connect.createStatement();
	       // Result set get the result of the SQL query
	       resultSet = statement
	           .executeQuery("SELECT * FROM pai5.pedidos\r\n" + 
	           		"WHERE YEAR(fecha) = YEAR(CURRENT_DATE - INTERVAL 0 MONTH)\r\n" + 
	           		"AND MONTH(fecha) = MONTH(CURRENT_DATE - INTERVAL 0 MONTH)");
	       double tact = calculaTendencia (resultSet); 
	       
	       resultSet = statement
	              .executeQuery("SELECT * FROM pai5.pedidos\r\n" + 
	              		"WHERE YEAR(fecha) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH)\r\n" + 
	              		"AND MONTH(fecha) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH)");
	       double tm1 = calculaTendencia (resultSet); 
	           


	       resultSet = statement
	               .executeQuery("SELECT * FROM pai5.pedidos\r\n" + 
	                		"WHERE YEAR(fecha) = YEAR(CURRENT_DATE - INTERVAL 2 MONTH)\r\n" + 
	                		"AND MONTH(fecha) = MONTH(CURRENT_DATE - INTERVAL 2 MONTH)");
	       
	       
	       double tm2 = calculaTendencia (resultSet); 
	       System.out.println("Actual: " + tact);
	       System.out.println("-1: " + tm1);
	       System.out.println("-2: " + tm2);

	       String tendencia = "";
	       
	       if ((tact > tm1 && tact > tm2) || (tact > tm1 && tact == tm2) || (tact == tm1 && tact > tm2)) {
	     	  tendencia = "POSITIVA";
	       } else if ((tact < tm1) || (tact < tm2)) {
	     	  tendencia = "NEGATIVA";
	       } else {
	     	  tendencia = "NULA";
	       }
	       
	       resultSet = statement
	               .executeQuery("SELECT * FROM pai5.pedidos ORDER BY id DESC LIMIT 0, 1");
	       
	       System.out.println(tendencia);
	       

	       while(resultSet.next()) {

	     	  Integer id = resultSet.getInt("ID");
	 	      String usuario = resultSet.getString("usuario");
	 	      String camas = resultSet.getString("camas");
	 	      String sillas = resultSet.getString("sillas");
	 	      Date date = resultSet.getDate("fecha");
	 	      String sillones = resultSet.getString("sillones");
	 	      String mesas = resultSet.getString("mesas");
	 	      Boolean accepted = resultSet.getBoolean("accepted");
	           PrintWriter writer = new PrintWriter("LOG - P" + id + " - " + date + ".txt", "UTF-8");

	 	      writer.println("///////PEDIDO NUMERO "+ id+ "///////////");
	 	      writer.println("TENDENCIA ACTUAL: " + tendencia);
	 	      writer.println("ULTIMO PEDIDO: ");
	 	      writer.println("	Usuario: " + usuario);
	 	      writer.println("	Fecha: " + date);
	 	      writer.println("	Camas: " + camas);
	 	      writer.println("	Sillas: " + sillas);
	 	      writer.println("	Sillones: " + sillones);
	 	      writer.println("	Mesas: " + mesas);
	 	      writer.println("	Aprobado: " + accepted);
	 	      writer.println("//////////");
	 	      writer.close();

	 	      
	       }
	   }
  
  public boolean checkSobrecarga() throws Exception {
	  Class.forName("com.mysql.jdbc.Driver");
	       
	       // Setup the connection with the DB
	       connect = DriverManager
	           .getConnection("jdbc:mysql://" + host + "/pai5?"
	               + "user=" + user + "&password=" + passwd );

	       // Statements allow to issue SQL queries to the database
	       statement = connect.createStatement();
	       // Result set get the result of the SQL query
	       resultSet = statement
	           .executeQuery("SELECT * FROM pai5.pedidos\r\n" + 
	           		"WHERE hora >= DATE_SUB(NOW(), INTERVAL 4 HOUR)");
	       
	       int counter = 0;
	       while (resultSet.next()) {
	    	   counter++;
	       }
	       
	       if (counter >= 3) {
	    	   return true;
	       } else {
	    	   return false;
	       }
	   }
  
   
  private double calculaTendencia(ResultSet resultSet) throws SQLException {
	 Double aceptados = 0.0;
	 Double totales = 0.0;
	  while (resultSet.next()) {
	      Boolean accepted = resultSet.getBoolean("accepted");
	      if (accepted) {
	    	  aceptados = aceptados + 1.0;
	      } 
	    	  totales = totales + 1.0;
	      
	  }
	  return aceptados/totales;
  }
  
  
  
  public void insertPedido(int mesas, int sillas, int sillones, int camas, int usuario, boolean verified) throws Exception {
	  try {
	      // This will load the MySQL driver, each DB has its own driver
	      Class.forName("com.mysql.jdbc.Driver");
	      java.sql.Date sqlDate = new java.sql.Date(new Date().getTime());
	      java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());

	      // Setup the connection with the DB
	      connect = DriverManager
	          .getConnection("jdbc:mysql://" + host + "/pai5?"
	              + "user=" + user + "&password=" + passwd );

	      // PreparedStatements can use variables and are more efficient
	      preparedStatement = connect
	          .prepareStatement("insert into  pai5.pedidos values (default, ?, ?, ?, ? , ?, ?, ?, ?)");

	      preparedStatement.setInt(1, usuario);
	      preparedStatement.setInt(2, mesas);
	      preparedStatement.setInt(3, sillas);
	      preparedStatement.setInt(4, camas);
	      preparedStatement.setInt(5, sillones);
	      preparedStatement.setDate(6, sqlDate);
	      preparedStatement.setTimestamp(7, date);
	      preparedStatement.setBoolean(8, verified);
	      preparedStatement.executeUpdate();

	     	      
	    } catch (Exception e) {
	      throw e;
	    } finally {
	      close();
	    }
  }

  private void writeMetaData(ResultSet resultSet) throws SQLException {
    //   Now get some metadata from the database
    // Result set get the result of the SQL query
    
    System.out.println("The columns in the table are: ");
    
    System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
    for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
      System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
    }
  }

  private void writeResultSet(ResultSet resultSet) throws SQLException {
    // ResultSet is initially before the first data set
    while (resultSet.next()) {
      // It is possible to get the columns via name
      // also possible to get the columns via the column number
      // which starts at 1
      // e.g. resultSet.getSTring(2);
      String user = resultSet.getString("myuser");
      String website = resultSet.getString("webpage");
      String summary = resultSet.getString("summary");
      Date date = resultSet.getDate("datum");
      String comment = resultSet.getString("comments");
      System.out.println("User: " + user);
      System.out.println("Website: " + website);
      System.out.println("Summary: " + summary);
      System.out.println("Date: " + date);
      System.out.println("Comment: " + comment);
    }
  }
  
  private void writePedidos(ResultSet resultSet) throws SQLException {
	  while (resultSet.next()) {
	      // It is possible to get the columns via name
	      // also possible to get the columns via the column number
	      // which starts at 1
	      // e.g. resultSet.getSTring(2);
	      String usuario = resultSet.getString("usuario");
	      String camas = resultSet.getString("camas");
	      String sillas = resultSet.getString("sillas");
	      Date date = resultSet.getDate("fecha");
	      String sillones = resultSet.getString("sillones");
	      String mesas = resultSet.getString("mesas");
	      Boolean accepted = resultSet.getBoolean("mesas");
	      System.out.println("User: " + user);
	      System.out.println("camas: " + camas);
	      System.out.println("sillas: " + sillas);
	      System.out.println("Date: " + date);
	      System.out.println("sillones: " + sillones);
	      System.out.println("Mesas: " + mesas);
	      System.out.println("Aprobado: " + accepted);
	    }
  }

  // You need to close the resultSet
  private void close() {
    try {
      if (resultSet != null) {
        resultSet.close();
      }

      if (statement != null) {
        statement.close();
      }

      if (connect != null) {
        connect.close();
      }
    } catch (Exception e) {

    }
  }

  public Integer getToken(String idUser, String idVotacion) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
	    // Setup the connection with the DB
	    connect = DriverManager
	        .getConnection("jdbc:mysql://" + host + "/pai5?"
	            + "user=" + user + "&password=" + passwd );

	    // Statements allow to issue SQL queries to the database
	    statement = connect.createStatement();
	    // Result set get the result of the SQL query
	    resultSet = statement
	        .executeQuery("SELECT token FROM pai5.censo WHERE censo.usuario =" + idUser +  " AND censo.votacion =" + idVotacion + " ;");

	    Integer token = -1;
	    while (resultSet.next()) {
	    	
	    	token = resultSet.getInt("token");
	    	System.out.println(token);
	    }
	    
	    
		
		return token;
	}
  
  public Map<String, String> getVotacion(String idVotacion) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
	    Map <String, String> res = new HashMap <String, String> ();
		Integer id2 = Integer.valueOf(idVotacion);
	    // Setup the connection with the DB
	    connect = DriverManager
	        .getConnection("jdbc:mysql://" + host + "/pai5?"
	            + "user=" + user + "&password=" + passwd );

	    // Statements allow to issue SQL queries to the database
	    statement = connect.createStatement();
	    // Result set get the result of the SQL query
	    resultSet = statement
	        .executeQuery("SELECT * from pai5.votacion where votacion.ID =" + id2 + ";");

	    String titulo = "";
	    String op1 = "";
	    String op2 = "";
	    while (resultSet.next()) {
	    	titulo = resultSet.getString("titulo");
	    	op1 = resultSet.getString("opcion1");
	    	op2 = resultSet.getString("opcion2");
	    	
	    	res.put("titulo", titulo);
	    	res.put("op1", op1);
	    	res.put("op2", op2);
	    }
	    
	    
		
		return res;
	}
  
  public void insertVoto(String idVotacion, String voto) throws ClassNotFoundException, SQLException {
	  Class.forName("com.mysql.jdbc.Driver");
	    // Setup the connection with the DB
	    connect = DriverManager
	        .getConnection("jdbc:mysql://" + host + "/pai5?"
	            + "user=" + user + "&password=" + passwd );

	    // Statements allow to issue SQL queries to the database
	    statement = connect.createStatement();
	    preparedStatement = connect
	            .prepareStatement("insert into  pai5.votos values (default, ?, ?)");
	        // "myuser, webpage, datum, summary, COMMENTS from pai5.comments");
	        // Parameters start with 1
	        preparedStatement.setString(1, idVotacion);
	        preparedStatement.setString(2, voto);
	        preparedStatement.executeUpdate();

  }

}