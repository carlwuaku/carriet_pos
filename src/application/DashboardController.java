package application;


import java.io.IOException;

import db.SqliteConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {
private Main main;
	
	@FXML
	public Label errorPane;


	@FXML
	public void goHome() throws IOException {
		
			main.showMainItems();
		
	}
	
	public void setErrorText(String error) {
		errorPane.setText(error);
	}
	
	public void clearErrorText(String error) {
		
	}
	
	
	@FXML
	public void showProductList() {
		try {
			main.switchToPage("product_list");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	public void showDeletedProductsList() {
		try {
			main.switchToPage("deleted_items");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	public void showStartSales() {
		try {
			main.switchToPage("new_sales");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	public void runMigrations() {
//		SqliteConnection.runMigrations();
	}
}
