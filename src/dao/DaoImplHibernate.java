package dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import model.Amount;
import model.Employee;
import model.Product;
import model.ProductHistory;

public class DaoImplHibernate implements Dao {
	
	private Session session;
	private Transaction tx;
	
	@Override
	public void connect() {
		Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
		org.hibernate.SessionFactory sessionFactory = configuration.buildSessionFactory();
    	session = sessionFactory.openSession();
	}

	@Override
	public void disconnect() {
		session.close();
		
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Product> getInventory() {
		try {
			connect();
			
			tx = session.beginTransaction();
			
			Query<Product> q = session.createQuery("from Product", Product.class);
			
			List <Product> result = q.list();
			
			ArrayList<Product> inventory = new ArrayList<>(result);
			
			for (Product p : inventory) {
				double wholesalerPriceValue = p.getPrice();
				Amount wholesalerPrice = new Amount(wholesalerPriceValue);
				p.setWholesalerPrice(wholesalerPrice);
				
				double publicPriceValue = wholesalerPriceValue * 2;
				Amount publicPrice = new Amount(publicPriceValue);
				p.setPublicPrice(publicPrice);
			}
			
			tx.commit();
			
			return inventory;
			
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			disconnect();
		}
		
		return new ArrayList<Product>();
	}

	@Override
	public boolean writeInventory(ArrayList<Product> products) {
		try {
			connect();
			
			tx = session.beginTransaction();
			
			for (Product p : products) {
				ProductHistory register_log = new ProductHistory(p.getId(), p.getName(), p.getPrice(), p.isAvailable(), p.getStock());
				session.save(register_log);
			}
			
			tx.commit();
			
			return true;
			
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			disconnect();
		}
		return false;
	}

	@Override
	public void addProduct(Product product) {
		try {
			connect();
			
			tx = session.beginTransaction();
			
			session.save(product);
			
			tx.commit();
			
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			disconnect();
		}
		
	}

	@Override
	public void updateProduct(Product product) {
		try {
			connect();
			
			tx = session.beginTransaction();
			
			session.update(product);
			
			tx.commit();
			
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			disconnect();
		}
	}

	@Override
	public void deleteProduct(int productId) {
		try {
			connect();
			
			tx = session.beginTransaction();
			
			Product product = session.get(Product.class, productId);
			
			if (product != null) {
				session.delete(product);
			}
			
			tx.commit();
			
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			disconnect();
		}
	}

	
	
}
