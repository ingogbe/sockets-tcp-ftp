import java.io.Serializable;

public class Client implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//Classe do cliente, contento nome e id unica dada pelo servidor
	private String name;
	private int id;
	
	public Client() {
		super();
		this.name = "";
		this.id = 0;
	}
	
	public Client(String name) {
		super();
		this.name = name;
		this.id = 0;
	}
	
	public Client(String name, int id) {
		super();
		this.name = name;
		this.id = id;
	}	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}
