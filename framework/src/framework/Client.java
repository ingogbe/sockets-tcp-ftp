package framework;
import java.io.Serializable;

public class Client implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//Classe do cliente, contento nome e id unica dada pelo servidor
	private String name;
	
	public Client() {
		super();
		this.name = "";
	}
	
	public Client(String name) {
		super();
		this.name = name;
	}
	
	public Client(String name, int id) {
		super();
		this.name = name;
	}	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
