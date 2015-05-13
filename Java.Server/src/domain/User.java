package domain;

public class User {
	
	private String id;
	private String name;
	private String password;
	//private String email;
	
	public User(String name,String password,String id) throws Exception{
		
		setName(name);
		setPassword(password);		
		setId(id);
		
	}
	public boolean compareTo (User another){
		
		return this.id.equals(another.getId());
	}
	public String getId() {
		return id;
	}
	private void setId(String id) throws Exception {
		if(id==null||id.trim().isEmpty()){
			throw new Exception("ID can not be null");
		}
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) throws Exception {
		
		if(name==null||name.trim().isEmpty()){
			throw new Exception("Name can not be empty");
		}
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) throws Exception {
		if(password==null||password.trim().isEmpty()){
			throw new Exception("Password can not be empty");
		}
		this.password = password;
	}
		

}
