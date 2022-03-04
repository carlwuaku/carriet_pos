package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.prefs.Preferences;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import models.Migration;
import models.ProductModel;

public class SqliteConnection {
	
	public static Connection connection = null;
	public static String productsTable = "products";
	public static String[] productsSearchFields = {"name", "description"};
	public static String salesTable = "sales";
	
	
	public static int dbversion = 2;
	public static Preferences pref;
	
	public static Connection connect() {
		
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:ProductDb.db");
			System.out.println("connected to db");
			return connection;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * run database migrations to update the db schema.
	 * the current_version shows the current structure, and 
	 * where the migration should continue from
	 * @param current_version
	 */
	public static void runMigrations(int current_version) {
		System.out.println("running migrations");
		connect();
		ArrayList<Migration> migrations = new ArrayList<Migration>();
		pref = Preferences.userNodeForPackage(SqliteConnection.class);
		

		String v1 = 
		"CREATE TABLE IF NOT EXISTS "+ productsTable+ " (\n"
                + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
                + "	name text NOT NULL,\n"
                + "	price real \n"
                + ");";
		
		Migration m1 = new Migration(1, v1);
//		VERSION 2
		String v2 = 
		"CREATE TABLE IF NOT EXISTS "+ salesTable+ " (\n"
                + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
                + "	customer text default NULL,\n"
                + "	code text not null, \n"
                + "	created_by integer default null, \n"
                + "	product text not null, \n"
                + "	price real not null, \n"
                + "	quantity real not null, \n"
                + "	date timestamp default current_timestamp \n"
                + ");";
		Migration m2 = new Migration(2, v2);
		
		
		String v3 = "ALTER TABLE "+productsTable+" add column category text default null;";
		Migration m3 = new Migration(3, v3);
		
		String v4 = 
				"ALTER TABLE "+productsTable+" add column deleted integer default 0;";
		Migration m4 = new Migration(4, v4);
		
		String v5 = 
				"ALTER TABLE "+productsTable+" add column description text default null;";
		Migration m5 = new Migration(5, v5);
		
		migrations.add(m1);
		migrations.add(m2);
		migrations.add(m3);
		migrations.add(m4);
		migrations.add(m5);
		
		for(int i = 0; i < migrations.size(); i++) {
			
			Migration q = migrations.get(i);
			int version = q.getVersion();
			if(version > current_version) {
				String query = q.getQuery();
		
					try {
						Statement s = connection.createStatement();
						s.execute(query);
						pref.put("schema_version", String.valueOf(version));
						System.out.println("schema set to "+version);
					} catch (SQLException e) {
						// colum probly already exists
						e.printStackTrace();
						System.out.println(query+" failed");
					}
				
				
			}
			
			
		}
		try {
			connection.close();
			connection = null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 
	 
	 public static Connection getConnection() {
		 if(connection == null) {
			 return connect();
		 }
		 return connection;
	 }
	 
	 public static void insertProduct(ProductModel p) throws SQLException  {
		 connect();
		 
			Statement s = connection.createStatement();
			String sql = "insert into "+ productsTable+ " (name, price, category) values ("+p.dbString() +")";
			 s.executeUpdate(sql );
			
			s.close();
			connection.close();
			connection  = null;		 
	 }
	 
	 public static void updateProduct(ProductModel p) throws SQLException  {
		 connect();
		 
			Statement s = connection.createStatement();
			String sql = "update "+ productsTable+ 
					" set name = '" + p.getName()+ "'," 
					+" price = "+p.getPrice()+","
					+" category = '"+p.getCategory()+"', "
					+" deleted = '"+p.getDeleted()+"' "
					+ " where id = "+p.getId();
			System.out.println(sql);
			 s.executeUpdate(sql );
			
			s.close();
			connection.close();
			connection  = null;		 
	 }
	 
	 /**
	  * run a batch of sql insert or update queries in an array
	  * @param ObservableList queries
	 * @throws SQLException 
	  */
	 public static void runBatchUpdate(ObservableList<String> queries) throws SQLException {
		  String sql = "";
		 for(int i = 0; i < queries.size(); i++) {
			 sql += queries.get(i);
		 }
		 
		 connect();
		 
			Statement s = connection.createStatement();
			
			System.out.println(sql);
			 s.executeUpdate(sql );
			
			s.close();
			connection.close();
			connection  = null;	
		 
	 }
	 
	 
	 public static void delete(int id, String table) throws SQLException {
		 connect();
		 
			Statement s = connection.createStatement();
			String sql = "delete from "+ table
					+ " where id = "+id;
			 s.executeUpdate(sql );
			
			s.close();
			connection.close();
			connection  = null;	
	 }
	 
	 public static void softDelete(int id, String table) throws SQLException {
		 connect();
		 
			Statement s = connection.createStatement();
			String sql = "update "+ table
					+ " set deleted = 1 where id = "+id;
			 s.executeUpdate(sql );
			
			s.close();
			connection.close();
			connection  = null;	
	 }
	 
	 public static void deleteBatch(String ids, String table) throws SQLException {
		 connect();
		 
			Statement s = connection.createStatement();
			String sql = "delete from "+ table
					+ " where id in ("+ids+")";
			 s.executeUpdate(sql );
			
			s.close();
			connection.close();
			connection  = null;	
	 }
	 
	 public static void softDeleteBatch(String ids, String table) throws SQLException {
		 connect();
		 
			Statement s = connection.createStatement();
			String sql = "update "+ table
					+ " set deleted = 1 where id in  ("+ids+")";
			 s.executeUpdate(sql );
			
			s.close();
			connection.close();
			connection  = null;	
	 }
	 
	 public static ArrayList<String> getDistinctColumn(String col, String table) throws SQLException {
		 connect();
		 ArrayList<String> items = new ArrayList<String>();
		 String sql = "select distinct "+col+" from "+table;
		 Statement s = connection.createStatement();
		 ResultSet rs = s.executeQuery(sql);
		 
//		 get the results
		 while(rs.next()) {
			 items.add(rs.getString(col));
		 }
		 
		 s.close();
			connection.close();
			connection  = null;
			return items;
	 }
	 
//	 the status tells whether we want to get only active or deleted items
	 public static ObservableList<ProductModel> getProducts(int status) throws SQLException{
		 connect();
		 
		 ObservableList<ProductModel> items =  FXCollections.observableArrayList();
		 String sql = "select *  from "+productsTable +" where deleted = "+status;
		 Statement s = connection.createStatement();
		 ResultSet rs = s.executeQuery(sql);
		 
//		 get the results
		 while(rs.next()) {
			 ProductModel productModel = new ProductModel(
					 rs.getString("name"),
					 rs.getDouble("price"),
					 rs.getString("category"),
					 rs.getInt("id")
					 );
			 items.add(productModel);
		 }
		 
		 s.close();
			connection.close();
			connection  = null;
			return items;
	 }
	 
	 
	 public static ObservableList<ProductModel> getProducts() throws SQLException{
		 connect();
		 
		 ObservableList<ProductModel> items =  FXCollections.observableArrayList();
		 String sql = "select *  from "+productsTable;
		 Statement s = connection.createStatement();
		 ResultSet rs = s.executeQuery(sql);
		 
//		 get the results
		 while(rs.next()) {
			 ProductModel productModel = new ProductModel(
					 rs.getString("name"),
					 rs.getDouble("price"),
					 rs.getString("category"),
					 rs.getInt("id")
					 );
			 items.add(productModel);
		 }
		 
		 s.close();
			connection.close();
			connection  = null;
			return items;
	 }
	 
	 public static ObservableList<ProductModel> searchProducts(String param) throws SQLException{
		 connect();
//		 split the words by commas, in case the person was searching for a bunch or items
//		 split the string by commas
		 String[] words_array = param.split(",");
		 ArrayList<String> combined_where = new ArrayList<>();
		//assuming a user searches for kofi mensa, akosua kobi, hpa 4041
	        //we have to implode this one too to produce a query like:
	        //where(
	        //( (first_name like 'kofi' or last_name like 'kofi') and (first_name like 'mensa' or last_name like 'mensah') ) or
	        // ( (first_name like 'akosua' or last_name like 'akosua') and (first_name like 'kobi' or last_name like 'kobi') ) or
	        // ((first_name like 'hpa' or last_name like 'hpa') and (first_name like '4041' or last_name like '4041')) 
	        // )
		 
		 for(int i = 0; i < words_array.length; i++) {
			 String word = words_array[i].trim();
//			 make sure the word is not empty
			 if(!Objects.equals(word, "") && word != null) {
				//the format we need for every word to be used in the sql query is like this:
	                //( (first_name like 'kofi' or last_name like 'kofi') and (first_name like 'mensa' or last_name like 'mensah') )
	                String processed_word = "(";
	                processed_word += processSearch(word);
	                processed_word += ")";
	                combined_where.add(processed_word);
			 }
			 
		 }
		 String final_where = String.join(" or ", combined_where);
		 
		 ObservableList<ProductModel> items =  FXCollections.observableArrayList();
		 String sql = "select *  from "+productsTable +" where "+final_where;
		 Statement s = connection.createStatement();
		 ResultSet rs = s.executeQuery(sql);
		 
//		 get the results
		 while(rs.next()) {
			 ProductModel productModel = new ProductModel(
					 rs.getString("name"),
					 rs.getDouble("price"),
					 rs.getString("category"),
					 rs.getInt("id"),
					 rs.getString("description")
					 );
			 items.add(productModel);
		 }
		 
		 s.close();
			connection.close();
			connection  = null;
			return items;
	 }
	 
	 /**
	  * say someone searches for 'akosua kobi'
	  * return a string like this
      *(first_name like 'akosua' or last_name like 'akosua') and (first_name like 'kobi' or last_name like 'kobi')
        
	  * @param param
	  * @return
	  */
	 public static String processSearch(String param) {
		 String final_where = "";
//		 split the string by spaces
		 String[] split_param = param.split(" ");
		 
		 ArrayList<String> where = new ArrayList<String>() ;
		 
		 for(int i = 0; i < split_param.length; i++) {
			 String word = split_param[i];
			 String temp_where = "(";
			 
			 ArrayList<String> inner_temp = new ArrayList<String>();
			 int k  = 0;
			 while(k < productsSearchFields.length) {
				 inner_temp.add(productsSearchFields[k]+ " like " +
					 "'%"+ word+  "%' ");
				 k++;
			 }
//			 join  the innertemp items  with 'or'
			 String joined_inner_temp = String.join(" or ", inner_temp);
//			 add this to the open bracket temp_where created earlier
			 temp_where += joined_inner_temp;
			 temp_where += ")";
//			 add this (temp_where) statement to the where statements
			 where.add(temp_where);
					 
		 }
		 
		 final_where = String.join(" and ", where);
		 System.out.println(final_where);
		 return final_where;
		 
	 }


}
