package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

import model.Amount;
import model.Employee;
import model.Product;

public class DaoImplJDBC implements Dao {
	Connection connection;

	@Override
	public void connect() {
		// Define connection parameters
		String url = "jdbc:mysql://localhost:3306/shop";
		String user = "root";
		String pass = "";
		try {
			this.connection = DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		Employee employee = null;
		String query = "select * from employee where employeeId= ? and password = ? ";
		
		try (PreparedStatement ps = connection.prepareStatement(query)) { 
    		ps.setInt(1,employeeId);
    	  	ps.setString(2,password);
    	  	//System.out.println(ps.toString());
            try (ResultSet rs = ps.executeQuery()) {
            	if (rs.next()) {
            		employee = new Employee(rs.getInt(1), rs.getString(2), rs.getString(3));
            	}
            }
        } catch (SQLException e) {
			// in case error in SQL
			e.printStackTrace();
		}
    	return employee;
	}

	@Override
	public ArrayList<Product> getInventory() {
		
		String query = "select * from inventory";
		
		try {
			if (connection == null || connection.isClosed()) {
				connect();
			}
			
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			
			ArrayList<Product> products = new ArrayList<>();
			
			while (rs.next()) {
				products.add(new Product(rs.getInt("id"), rs.getString("name"), new Amount(rs.getDouble("wholesalerPrice")), rs.getBoolean("available"), rs.getInt("stock")));
			}
					
			return products;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		
		return null;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> products) {
		String query = "insert into historical_inventory (id_product, name, wholesalerPrice, available, stock, created_at) values (?,?,?,?,?,?)";
		
		try {
			if (connection == null || connection.isClosed()) {
				connect();
			}
			
			for (Product product : products) {
				PreparedStatement ps = connection.prepareStatement(query);
				ps.setInt(1, product.getId());
				ps.setString(2, product.getName());
				ps.setDouble(3, product.getWholesalerPrice().getValue());
				ps.setBoolean(4, product.isAvailable());
				ps.setInt(5, product.getStock());
				ps.setObject(6, LocalDateTime.now());
				
				ps.executeUpdate();
			}
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		
		return false;
	}

	@Override
	public void addProduct(Product product) {
		
		String query = "insert into inventory (id, name, wholesalerPrice, available, stock) values (?,?,?,?,?)";
		
		
		try {
			if (connection == null || connection.isClosed()) {
				connect();
			}
			
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, product.getId());
			ps.setString(2, product.getName());
			ps.setDouble(3, product.getWholesalerPrice().getValue());
			ps.setBoolean(4, product.isAvailable());
			ps.setInt(5, product.getStock());
			
			ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
	}

	@Override
	public void updateProduct(Product product) {
		
		String query = "update inventory set stock=? where id=?";
		
		try {
			if (connection == null || connection.isClosed()) {
				connect();
			}
			
			PreparedStatement ps = connection.prepareStatement(query);;
			ps.setInt(1, product.getStock());
			ps.setInt(2, product.getId());
			
			ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
	}

	@Override
	public void deleteProduct(int productId) {
		String query = "delete from inventory where id=?";
		
		try {
			if (connection == null || connection.isClosed()) {
				connect();
			}
			
			PreparedStatement ps = connection.prepareStatement(query);;
			ps.setInt(1, productId);
			
			ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
	}
	
}
