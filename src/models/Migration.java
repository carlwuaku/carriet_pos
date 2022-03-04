package models;


public class Migration {
	private int version;
	private String query;
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public Migration(int version, String query) {
		super();
		this.version = version;
		this.query = query;
	}
	
	
	
}
