package products;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import application.Main;
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

public class ProductListController implements Initializable {
//initialize to empty array
	ObservableList<ProductModel> products = FXCollections.observableArrayList();
//	keep track of removed products so they can be added
	ObservableList<RemovedItems> deletedItems = FXCollections.observableArrayList();
	FilteredList<ProductModel> filteredData;
//	keep track of all changes made
	ObservableList<String> changes = FXCollections.observableArrayList();
//	Preferences
	Preferences pref;
	
	@FXML
	private TableView<ProductModel> tableview;
	
	@FXML
	private BorderPane root;
	
	@FXML
	private Label label;
	
	@FXML
	private Button deleteButton;
	@FXML
	private Button undoButton;
	@FXML
	private CheckBox autosave;
	@FXML
	private Button saveChangesButton;
	
	@FXML
	private VBox sidebar;
	
	@FXML
	private TextField search;
	
	private Main main;
	public int changesCount = 0;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
//		disable the undo and delete buttons by default
		deleteButton.setDisable(true);
		undoButton.setDisable(true);
		saveChangesButton.setDisable(true);
		
//		get saved settings like autosave
		pref = Preferences.userNodeForPackage(ProductListController.class);
		
		String autosavePref = pref.get("autosave", "no");
		if(Objects.equals(autosavePref, "yes")) {
			autosave.setSelected(true);
		}
		else {
			autosave.setSelected(false);
		}
		

		
		
		
//		draw table
//		columns
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
//		FILTERING
//		initially set the list to all data
//		set the filter predicate when the textproperty of the filtertextfield changes
		search.textProperty().addListener((observable, oldValue, newValue) -> {
//			set the filter conditions
			filteredData.setPredicate(product -> {
//				if text is empty, display all
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
		
//		tableview = new TableView<>();
		
		tableview.getColumns().addAll(nameColumn,priceColumn,categoryColumn);
//		root.getChildren().add(tableview);
//		root.setCenter(tableview);
		
		
		
		tableview.setEditable(true);
		
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		nameColumn.setOnEditCommit(event -> {
			String newname = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			ProductModel p = (ProductModel) event.getTableView().getItems().get(event.getTablePosition().getRow());
			p.setName(newname);
//			if autosave is selected, save
			if(autosave.isSelected()) {
				try {
					SqliteConnection.updateProduct(p);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
//			else keep the changes for salvation later
			else {
				changes.add("update "+SqliteConnection.productsTable+" set name = '"+p.getName()+"' where id = "+p.getId()+";");
				saveChangesButton.setText("Save changes ("+changes.size()+")");
				saveChangesButton.setDisable(false);
			}
		});
		
		categoryColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		categoryColumn.setOnEditCommit(event -> {
			String newcategory = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			ProductModel p = (ProductModel) event.getTableView().getItems().get(event.getTablePosition().getRow());
			p.setCategory(newcategory);
			if(autosave.isSelected()) {
				try {
					SqliteConnection.updateProduct(p);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				changes.add("update "+SqliteConnection.productsTable+" set category = '"+p.getCategory()+"' where id = "+p.getId()+";");
				saveChangesButton.setText("Save changes ("+changes.size()+")");
				saveChangesButton.setDisable(false);
			}
		});
		
		priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		priceColumn.setOnEditCommit(event -> {
			Double newprice = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			ProductModel p = (ProductModel) event.getTableView().getItems().get(event.getTablePosition().getRow());
			p.setPrice(newprice);
			if(autosave.isSelected()) {
				try {
					SqliteConnection.updateProduct(p);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				changes.add("update "+SqliteConnection.productsTable+" set price = "+p.getPrice()+" where id = "+p.getId()+";");
				saveChangesButton.setText("Save changes ("+changes.size()+")");
				saveChangesButton.setDisable(false);
			}
		});
		
//		make user able to select multiple items
		tableview.getSelectionModel().selectedItemProperty().addListener((obs, oldselection, newselection) -> {
			if(newselection != null) {
				deleteButton.setDisable(false);
			}
		});
		
		
//		set context menu on table rows
		tableview.setRowFactory(
				new Callback<TableView<ProductModel>,TableRow<ProductModel>>(){
					@Override
			        public TableRow<ProductModel> call(TableView<ProductModel> tableView) {
			            final TableRow<ProductModel> row = new TableRow<>();
			            final ContextMenu rowMenu = new ContextMenu();
			            
			            MenuItem editItem = new MenuItem("Edit Name");
			            MenuItem removeItem = new MenuItem("Delete");
			            removeItem.setOnAction(new EventHandler<ActionEvent>() {

			                @Override
			                public void handle(ActionEvent event) {
			                	ProductModel item = (ProductModel) tableview.getSelectionModel().getSelectedItem();
			                	delete(products.indexOf(item));
			                    System.out.println("Selected item: " + item);
			                    
			                }
			            });
			            rowMenu.getItems().addAll(editItem, removeItem);

			            // only display context menu for non-null items:
			            row.contextMenuProperty().bind(
			              Bindings.when(Bindings.isNotNull(row.itemProperty()))
			              .then(rowMenu)
			              .otherwise((ContextMenu)null));
			            return row;
			    }
				});

//		get products from db
		try {
			
			products = SqliteConnection.getProducts(0);
			filteredData = new FilteredList<ProductModel>(products, p -> true);	
//			tableview.setItems(products);
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
//		remove them from the table
		selected.forEach(s -> {
			RemovedItems r = new RemovedItems(all.indexOf(s), s);
			deletedItems.add(r);
			all.remove(s);
//			soft delete the item
			try {
				SqliteConnection.softDelete(s.getId(), SqliteConnection.productsTable);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
//		enable the undo button
		filteredData = new FilteredList<ProductModel>(products, p -> true);	
		undoButton.setDisable(false);
		deleteButton.setDisable(true);
		tableview.getSelectionModel().clearSelection();
	}
	
	public void delete(int index) {
		ObservableList<ProductModel>  all;
		all = products;
		ProductModel current =  all.get(index);
//		soft delete the item
		try {
			SqliteConnection.softDelete(current.getId(), SqliteConnection.productsTable);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		all.remove(index);
		RemovedItems r = new RemovedItems(all.indexOf(current), current);
		deletedItems.add(r);
//		enable the undo button
		filteredData = new FilteredList<ProductModel>(products, p -> true);	
		undoButton.setDisable(false);
		deleteButton.setDisable(true);
		tableview.getSelectionModel().clearSelection();
	}
	
	public void delete(ProductModel s) {
		ObservableList<ProductModel>  all;
		try {
		all = products;
		RemovedItems r = new RemovedItems(all.indexOf(s), s);
		deletedItems.add(r);
		System.out.println(r.toString());
		all.remove(s);
		try {
			SqliteConnection.softDelete(s.getId(), SqliteConnection.productsTable);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		filteredData = new FilteredList<ProductModel>(products, p -> true);	
//		enable the undo button
		undoButton.setDisable(false);
		deleteButton.setDisable(true);
		tableview.getSelectionModel().clearSelection();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void undoDelete() {
		ObservableList<ProductModel>  all;
		all = products;
//		get the last item deleted. that is what we will restore
		int index = deletedItems.size() - 1;
		RemovedItems r = deletedItems.get(index);
		all.add(r.getIndex(), r.getItem());
		filteredData = new FilteredList<ProductModel>(products, p -> true);	
//		the item will have been soft deleted. update the deleted status and restore it
		ProductModel p = r.getItem();
		p.setDeleted(0);
		try {
			SqliteConnection.updateProduct(p);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		deletedItems.remove(index);
//		if there aren't any more to delete, disable the undo button
		if(deletedItems.size() < 1) {
			undoButton.setDisable(true);
		}
	}
	
	@FXML
	public void showAddNew() {
		main.showAddStage();
	}
	
	@FXML
	public void back() {
		main.showMainView();
	}
	
	public class RemovedItems{
		private int index;
		private ProductModel item;
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public ProductModel getItem() {
			return item;
		}
		public void setItem(ProductModel item) {
			this.item = item;
		}
		public RemovedItems(int index, ProductModel item) {
			super();
			this.index = index;
			this.item = item;
		}
		
		
	}
	@FXML
	public void saveChanges() {
		try {
			SqliteConnection.runBatchUpdate(changes);
			saveChangesButton.setText("Save changes");
			saveChangesButton.setDisable(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void setAutosavePref() {
		String val = autosave.isSelected() ? "yes" : "no";
		pref.put("autosave", val);
	}
	

}
