package products;


import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import db.SqliteConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import models.ProductModel;

public class AddNewProductController implements Initializable {
//	the database connection
	
	ObservableList<String> categories =
			FXCollections.observableArrayList("Original Fish", "Original Meat", "Original Vegetables");
	
	@FXML
	private TextField name;
	
	@FXML
	private TextField price;
	
	@FXML
	private ChoiceBox categorySelect;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		System.out.println("init 2");
		categorySelect.setItems(categories);
//		get categories from db
		try {
			
			ArrayList<String> dbItems = SqliteConnection.getDistinctColumn("category", "products");
			dbItems.forEach((i) -> {
				categories.add(i);
			});
			categorySelect.setItems(categories);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void save() {
		String pname = name.getText();
		
		ProductModel p = new ProductModel(
				name.getText(), Double.parseDouble(price.getText()), categorySelect.getValue().toString());
				try {
					SqliteConnection.insertProduct(p);
					System.out.println("product inserted");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
}
