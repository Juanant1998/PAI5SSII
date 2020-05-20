package main;

public class Main {
	  public static void main(String[] args) throws Exception {
	    MySQLAccess dao = new MySQLAccess();
	    //dao.readDataBase();
	    dao.insertPedido(1, 2, 3, 4, 288, true);
	  }

	}