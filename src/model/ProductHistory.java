package model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "historical_inventory")
public class ProductHistory {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;
	
	@Column
	private int id_product;
	
	@Column
	private String name;
	
	@Column
	private double price;
	
	@Column
	private boolean available;
	
	@Column
	private int stock;
	
	@Column
	private LocalDateTime created_at = LocalDateTime.now();
	
	public ProductHistory() {}
	
	public ProductHistory(int id_product, String name, double price, boolean available, int stock) {
		super();
		this.id_product = id_product;
		this.name = name;
		this.price = price;
		this.available = available;
		this.stock = stock;
	}
	
}
