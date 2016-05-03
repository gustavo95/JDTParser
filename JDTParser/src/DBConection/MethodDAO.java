package DBConection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import Parse.MethodInformations;

public class MethodDAO {
	
private Connection con;
	
	public MethodDAO(Connection con){
		 setCon(con);
		}
	
	public Connection getCon() {
		 return con;
		}
	
	public void setCon(Connection con) {
		 this.con = con;
	} 
	
	public String insert(MethodInformations mi) {
		String sql = "insert into xerces(id,name,class,start_line,end_line,length)values(?,?,?,?,?,?)";
		
		try{
			PreparedStatement ps = getCon().prepareStatement(sql);
			ps.setInt(1, mi.getId());
			ps.setString(2, mi.getName());
			ps.setString(3, mi.getMethodClass());
			ps.setInt(4, mi.getStartLine());
			ps.setInt(5, mi.getEndLine());
			ps.setInt(6, mi.getLength());

			if (ps.executeUpdate() > 0) {
				return "Inserido com sucesso.";
			} else {
				return "Erro ao inserir";
			}
		} catch (SQLException e) {
			return e.getMessage();
		}
	}

}
