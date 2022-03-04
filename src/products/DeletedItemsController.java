package products;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import application.Main;
import products.ProductListController.RemovedItems;
import db.SqliteConnection;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import models.ProductModel;

public class DeletedItemsController implements Initializable{
	//initialize to empty array
		ObservableList<ProductModel> products = FXCollections.observableArrayList();
//		keep track of removed products so they can be added
		ObservableList<RemovedItems> deletedItems = FXCollections.observableArrayList();
		FilteredList<ProductModel> filteredData;
//		keep track of all changes made
		ObservableList<String> changes = FXCollections.observableArrayList();

		
		@FXML
		private TableView<ProductModel> tableview;
		
		@FXML
		private BorderPane root;
		
		@FXML
		private Label label;
		
		@FXML
		private Button restoreButton;
		@FXML
		private VBox sidebar;
		
		@FXML
		private TextField search;
		
		private Main main;
		public int changesCount = 0;
		@Override
		public void initialize(URL arg0, ResourceBundle arg1) {
//			disable the undo and delete buttons by default
			restoreButton.setDisable(true);
			
			

			
			
			
//			draw table
//			columns
			TableColumn<ProductModel, String> nameColumn = new TableColumn<>("Name");
			nameColumn.setMinWidth(200);
			nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
			
			TableColumn<ProductModel, Double> priceColumn = new TableColumn<>("Price");
			priceColumn.setMinWidth(100);
			priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
			
			TableColumn<ProductModel, String> categoryColumn = new TableColumn<>("Category");
			categoryColumn.setMinWidth(200);
			categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
			tableview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//			FILTERING
//			initially set the list to all data
//			set the filter predicate when the textproperty of the filtertextfield changes
			search.textProperty().addListener((observable, oldValue, newValue) -> {
//				set the filter conditions
				filteredData.setPredicate(product -> {
//					if text is empty, display all
					if(newValue == null || newValue.isEmpty()) {
						return true;
					}
					
					//compare the name and category to the parameter
					String lowercaseFilter = newValue.toLowerCase();
					if(product.getCategory().toLowerCase().contains(lowercaseFilter)) {
						return true;
					}
					else if(product.getName().toLowerCase().contains(lowercaseFilter)) {
						return true;
					}
					return false;
				});
			});
			
//			tableview = new TableView<>();
			
			tableview.getColumns().addAll(nameColumn,priceColumn,categoryColumn);
//			root.getChildren().add(tableview);
//			root.setCenter(tableview);
			
			
			
			tableview.setEditable(false);
			
			nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
			
			
			categoryColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		
			
			priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
			
			
//			make user able to select multiple items
			tableview.getSelectionModel().selectedItemProperty().addListener((obs, oldselection, newselection) -> {
				if(newselection != null) {
					restoreButton.setDisable(false);
				}
			});
			
			
//			set context menu on table rows
			tableview.setRowFactory(
					new Callback<TableView<ProductModel>,TableRow<ProductModel>>(){
						@Override
				        public TableRow<ProductModel> call(TableView<ProductModel> tableView) {
				            final TableRow<ProductModel> row = new TableRow<>();
				            final ContextMenu rowMenu = new ContextMenu();
				            
				            MenuItem removeItem = new MenuItem("Restore");
				            removeItem.setOnAction(new EventHandler<ActionEvent>() {

				                @Override
				                public void handle(ActionEvent event) {
				                	ProductModel item = (ProductModel) tableview.getSelectionModel().getSelectedItem();
				                	delete(products.indexOf(item));
				                    System.out.println("Selected item: " + item);
				                    
				                }
				            });
				            rowMenu.getItems().addAll( removeItem);

				            // only display context menu for non-null items:
				            row.contextMenuProperty().bind(
				              Bindings.when(Bindings.isNotNull(row.itemProperty()))
				              .then(rowMenu)
				              .otherwise((ContextMenu)null));
				            return row;
				    }
					});

//			get products from db which have been deleted
			try {
				
				products = SqliteConnection.getProducts(1);
				filteredData = new FilteredList<ProductModel>(products, p -> true);	
//				tableview.setItems(products);
				tableview.setItems(filteredData);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		public void delete() {
			ObservableList<ProductModel> selected, all;
			all = products;
			selected = tableview.getSelectionModel().getSelectedItems();
//			remove them from the table
			selected.forEach(s -> {
				s.setDeleted(0);
				all.remove(s);
//				soft delete the item
				try {
					SqliteConnection.updateProduct(s);;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
//			enable the undo button
			filteredData = new FilteredList<ProductModel>(products, p -> true);	
			restoreButton.setDisable(true);
			tableview.getSelectionModel().clearSelection();
		}
		
		public void delete(int index) {
			ObservableList<ProductModel>  all;
			all = products;
			ProductModel current =  all.get(index);
//			undo soft delete the item
			current.setDeleted(0);
			try {
				SqliteConnection.updateProduct(current);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			all.remove(index);
			filteredData = new FilteredList<ProductModel>(products, p -> true);	
			restoreButton.setDisable(true);
			tableview.getSelectionModel().clearSelection();
		}
		
		public void delete(ProductModel s) {
			ObservableList<ProductModel>  all;
			try {
			all = products;
			all.remove(s);
			s.setDeleted(0);
			try {
				SqliteConnection.updateProduct(s);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			filteredData = new FilteredList<ProductModel>(products, p -> true);	
			restoreButton.setDisable(true);
			tableview.getSelectionModel().clearSelection();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		
		@FXML
		public void back() {
			main.showMainView();
		}
		
	
}
