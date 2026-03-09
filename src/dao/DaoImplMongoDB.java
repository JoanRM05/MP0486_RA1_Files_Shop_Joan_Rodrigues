package dao;

import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import model.Amount;
import model.Employee;
import model.Product;

public class DaoImplMongoDB implements Dao {
	
	String uri = "mongodb://localhost:27017";
	
	MongoClient mongoClient;
	MongoDatabase mongoDatabase;
	ObjectId id;
	
	@Override
	public void connect() {
		MongoClientURI mongoClientURI = new MongoClientURI(uri);
		this.mongoClient = new MongoClient(mongoClientURI);
		this.mongoDatabase = mongoClient.getDatabase("shop");
		
		mongoDatabase.runCommand(new Document("ping", 1));
	}

	@Override
	public void disconnect() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		
		try	{
			
			connect();
			
			MongoCollection<Document> collection = mongoDatabase.getCollection("users");
			
			Document document = collection.find(
					Filters.and(
							Filters.eq("employeeId", employeeId), 
							Filters.eq("password", password)
					)
				).first();
			
			if (document != null) {
		        Employee emp = new Employee(document.getInteger("employeeId"), document.getString("name"), document.getString("password"));
		        
		        return emp;
		    }
			
			return null;
			
		} catch (Exception e) {
			System.out.println("Error connecting to MongoDB: " + e.getMessage());
			return null;
		} finally {
			disconnect();
		}
	}

	@Override
	public ArrayList<Product> getInventory() {
		
		ArrayList<Product> inventory = new ArrayList<>();
		
		try {
			connect();
			
			MongoCollection<Document> collection = mongoDatabase.getCollection("inventory");
			
			Iterable<Document> documents = collection.find();
			
			for (Document document : documents) {
				
				int productId = document.getInteger("id");
				String name = document.getString("name");
				boolean available = document.getBoolean("available");
				int stock = document.getInteger("stock");
				
				Document priceDoc = (Document) document.get("wholesalerPrice");
		        Amount wholesalerPriceAmount = null;
		        
		        if (priceDoc != null) {

		            double priceValue = priceDoc.getDouble("value").doubleValue();
		            
		            wholesalerPriceAmount = new Amount(priceValue);
		        }
		        
		        Product product = new Product(productId, name, wholesalerPriceAmount, available, stock);
		        
		        inventory.add(product);
			}
			
			return inventory;
			
		} catch (Exception e) {
			System.out.println("Error connecting to MongoDB: " + e.getMessage());
			return inventory;
		} finally {
			disconnect();
		}
	}

	@Override
	public boolean writeInventory(ArrayList<Product> products) {
		try {
	        connect();
	        
	        MongoCollection<Document> collection = mongoDatabase.getCollection("historical_inventory");
	        
	        Date now = new Date();
	        
	        ArrayList<Document> documentsToInsert = new ArrayList<>();

	        for (Product p : products) {
	        	
	        	Document wholesalerPriceDoc = new Document()
	                    .append("value", p.getWholesalerPrice().getValue())
	                    .append("currency", "€");
	        	
	            Document historyDoc = new Document()
	                    .append("id_product", p.getId())
	                    .append("name", p.getName())
	                    .append("wholesalerPrice", wholesalerPriceDoc)
	                    .append("available", p.isAvailable())
	                    .append("stock", p.getStock())
	                    .append("created_at", now);

	            documentsToInsert.add(historyDoc);
	        }

	        if (!documentsToInsert.isEmpty()) {
	            collection.insertMany(documentsToInsert);
	            return true;
	        }

	        return false;

	    } catch (Exception e) {
	    	System.out.println("Error connecting to MongoDB: " + e.getMessage());
	        return false;
	    } finally {
	        disconnect();
	    }
	}

	@Override
	public void addProduct(Product product) {
		try {
	        connect();
	        
	        MongoCollection<Document> collection = mongoDatabase.getCollection("inventory");

	        Document wholesalerPriceDoc = new Document()
	                .append("value", product.getWholesalerPrice().getValue())
	                .append("currency", "€");

	        Document productDoc = new Document()
	                .append("id", product.getId())
	                .append("name", product.getName())
	                .append("available", product.isAvailable())
	                .append("stock", product.getStock())
	                .append("wholesalerPrice", wholesalerPriceDoc);

	        collection.insertOne(productDoc);

	    } catch (Exception e) {
	        System.out.println("Error connecting to MongoDB: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        disconnect();
	    }
		
	}

	@Override
	public void updateProduct(Product product) {
		try {
	        connect();
	        
	        MongoCollection<Document> collection = mongoDatabase.getCollection("inventory");

	        Document wholesalerPriceDoc = new Document()
	                .append("value", product.getWholesalerPrice().getValue())
	                .append("currency", "€");

	        Document updatedDoc = new Document()
	                .append("id", product.getId())
	                .append("name", product.getName())
	                .append("available", product.isAvailable())
	                .append("stock", product.getStock())
	                .append("wholesalerPrice", wholesalerPriceDoc);

	        collection.replaceOne(Filters.eq("id", product.getId()), updatedDoc);

	    } catch (Exception e) {
	        System.out.println("Error connecting to MongoDB: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        disconnect();
	    }
	}

	@Override
	public void deleteProduct(int productId) {
		try {
	        connect();
	        
	        MongoCollection<Document> collection = mongoDatabase.getCollection("inventory");

	        collection.deleteOne(Filters.eq("id", productId));
	        
	    } catch (Exception e) {
	        System.out.println("Error connecting to MongoDB: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        disconnect();
	    }
		
	}
	
}
