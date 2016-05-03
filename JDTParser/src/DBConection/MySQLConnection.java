package DBConection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

private String status = "Not coneccted...";
	
	public MySQLConnection(){
		
	}
	
	public Connection getMySQLConnection() {
		Connection connection = null;
		
		try{
			String driverName = "com.mysql.jdbc.Driver"; 
			Class.forName(driverName).newInstance();
			
			String url = "";
			url += "jdbc:mysql://127.0.0.1/projects_methods_informations?";
			url += "user=root&password=assassinscreed3";
			url += "&autoReconnect=true&useSSL=false";
			connection = DriverManager.getConnection(url);
			
			if(connection != null){
				status = ("STATUS--->Conectado com sucesso!");
			}else{
				status = ("STATUS--->Não foi possivel realizar conexão");
			}
			System.out.println(status);
			
		}catch(ClassNotFoundException e){
			System.out.println("O driver expecificado nao foi encontrado.");
		}catch (SQLException e) {
			System.out.println("Nao foi possivel conectar ao Banco de Dados.");
		} catch (Exception e) {
			System.out.println("Erro");
		}
		
		return connection;
	}

	public String statusConnection() {
		return status;
	} 

	public boolean closeConnection(Connection con){
		try{
			con.close();
			System.out.println("Conexão fechada.");
			return true;
		}catch(SQLException e){
			return false;
		}
	}
	
}
