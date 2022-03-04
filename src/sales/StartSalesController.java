package sales;


import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import db.SqliteConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import models.ProductModel;
import sales.SalesModel;

public class StartSalesController implements Initializable {

	@FXML
	private TableView<SalesModel> items;
	
	
	@FXML
	private Label label;
	
	@FXML
	private TextField search_param;
	
	@FXML
	private ListView search_results;
	//initialize to empty array
			ObservableList<ProductModel> products = FXCollections.observableArrayList();
			
			//initialize to empty array
			ObservableList<SalesModel> sales = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		search_results.setCellFactory(param -> new ListCell<ProductModel>() {

			@Override
			protected void updateItem(ProductModel item, boolean empty) {
				// TODO Auto-generated method stub
				super.updateItem(item, empty);
				if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.getName() +" [GHS "+item.getPrice()+"]");
                }
			} 
			
		});
		
		search_results.getSelectionModel().selectedItemProperty().addListener((obs, oldselection, newselection) -> {
			if(newselection != null) {
				ProductModel p = (ProductModel) search_results.getSelectionModel().getSelectedItem();
				SalesModel s = new SalesModel();
				s.setProductName(p.getName());
				s.setPrice(p.getPrice());
				s.setQuantity(1);
				s.setTotal(p.getPrice());
				s.setOnhand(0);
				s.setRemove(new Button("remove"));
				s.setIncrement(new Button("+"));
				s.setDecrement(new Button("-"));
				
				sales.add(s);
				s.getRemove().setOnAction(event -> {
					sales.remove(sales.indexOf(s));
				});
				s.getIncrement().setOnAction(event -> {
					s.setQuantity(s.getQuantity() +1);
				});
				s.getDecrement().setOnAction(event -> {
					s.setQuantity(s.getQuantity() -1);
				});
//				System.out.println(p.getName()+" selected");
			}
		});
		
//		draw table
//		columns
		TableColumn<SalesModel, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setMinWidth(200);
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
		
		TableColumn<SalesModel, Double> priceColumn = new TableColumn<>("Price");
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		
		TableColumn<SalesModel, Integer> quantityColumn = new TableColumn<>("Quantity");
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		
		TableColumn<SalesModel, Double> totalColumn = new TableColumn<>("Total");
		totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
		
		TableColumn<SalesModel, Integer> onhandColumn = new TableColumn<>("On-hand");
		onhandColumn.setCellValueFactory(new PropertyValueFactory<>("onhand"));
		
		TableColumn<SalesModel, Button> removeColumn = new TableColumn<>("");
		removeColumn.setCellValueFactory(new PropertyValueFactory<>("remove"));
		
		TableColumn<SalesModel, Button> incColumn = new TableColumn<>("");
		incColumn.setCellValueFactory(new PropertyValueFactory<>("increment"));
		
		TableColumn<SalesModel, Button> decColumn = new TableColumn<>("");
		decColumn.setCellValueFactory(new PropertyValueFactory<>("decrement"));
		
		
		
		items.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		
//		tableview = new TableView<>();
		
		
		items.getColumns().addAll(nameColumn,priceColumn,quantityColumn, 
				totalColumn, onhandColumn,incColumn, decColumn, removeColumn);
//		root.getChildren().add(tableview);
//		root.setCenter(tableview);
		
		
		
		items.setEditable(true);
		
		quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		
		quantityColumn.setOnEditCommit(event -> {
			int newquantity = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			
			SalesModel p = (SalesModel) event.getTableView().getItems().get(event.getTablePosition().getRow());
			p.setQuantity(newquantity);
			p.setTotal(p.getPrice() * newquantity);
			items.refresh();
			System.out.println(p.getTotal());
		});
		
		items.setItems(sales);
		
	}
	
	public void search() {
		String param = search_param.getText();
		if(!param.isEmpty()) {
			try {
			products =	SqliteConnection.searchProducts(param);
			search_results.setItems(products);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
//			clear the results
			products = FXCollections.observableArrayList();
			search_results.setItems(products);
		}
	}
	
	

}