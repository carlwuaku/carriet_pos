package application;
	

import java.io.IOException;
import java.util.prefs.Preferences;

import db.SqliteConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {
	private static Stage primaryStage;
	private static BorderPane mainLayout;
//	Preferences
	Preferences pref;	

	@Override
	public void start(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Products App");
		showMainView();
//		showMainItems();
//		check the database for schema version. if the latest is more than the last saved, do the migration

//		get saved settings like autosave
		pref = Preferences.userNodeForPackage(Main.class);
		String last_schema = pref.get("schema_version", "0");
		int version = Integer.parseInt(last_schema);
		
		SqliteConnection.runMigrations(version);
		
	}
	
	public static void switchToPage(String page) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		switch (page) {
		
		case "product_list":
			loader.setLocation(Main.class.getResource("../products/ProductList.fxml"));
			BorderPane pPage = loader.load();
			mainLayout.setCenter(pPage);
			break;
		case "deleted_items":
			loader.setLocation(Main.class.getResource("../products/DeletedItems.fxml"));
			BorderPane dPage = loader.load();
			mainLayout.setCenter(dPage);
			break;
		case "new_sales":
			loader.setLocation(Main.class.getResource("../sales/StartSales.fxml"));
			BorderPane sPage = loader.load();
			mainLayout.setCenter(sPage);
			break;

		default:
			break;
		}
	}
	
	public static void showMainView() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("Dashboard.fxml"));
		try {
			mainLayout = loader.load();
			Scene scene = new Scene(mainLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public static void showMainItems() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("products/MainItems.fxml"));
		
			BorderPane mainItems = loader.load();
			mainLayout.setCenter(mainItems);
		
	}
	
	public static void showAddStage() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("../products/AddNewProduct.fxml"));
		
			BorderPane addNew;
			try {
				addNew = loader.load();
				Stage addStage = new Stage();
				addStage.setTitle("Add new product");
				addStage.initModality(Modality.WINDOW_MODAL);
				addStage.initOwner(primaryStage);
				Scene sc = new Scene(addNew);
				addStage.setScene(sc);
				addStage.showAndWait();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
