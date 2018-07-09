package framework;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final int TYPE_SERVER_MSG = 1;
	public static final int TYPE_UPLOAD_FILE = 2;
	public static final int TYPE_DOWNLOAD_FILE = 3;
	public static final int TYPE_LIST_USERS = 4;
	public static final int TYPE_LIST_FILES = 5;
	public static final int TYPE_USER_CONNECTION = 6;
	public static final int TYPE_CLIENT_MSG = 7;
	
	private String message;
	private int type;
	private Date serverDate;
	private ArrayList<Client> listUsers;
	private File[] listFiles;
	private Client sender;
	private File file;
	
	public Message(File file, int type, Client sender) {
		super();
		this.message = null;
		this.type = type;
		this.serverDate = null;
		this.listUsers = null;
		this.listFiles = null;
		this.file = file;
		this.sender = sender;
	}
	
	public Message(File file, int type) {
		super();
		this.message = null;
		this.type = type;
		this.serverDate = null;
		this.listUsers = null;
		this.listFiles = null;
		this.file = file;
		this.sender = null;
	}
	
	public Message(int type, Client sender) {
		super();
		this.message = null;
		this.type = type;
		this.serverDate = null;
		this.listUsers = null;
		this.listFiles = null;
		this.file = null;
		this.sender = sender;
	}
	
	//TYPE_SERVER_MSG
	public Message(String message, int type, Date serverDate) {
		super();
		this.message = message;
		this.type = type;
		this.serverDate = serverDate;
		this.listUsers = null;
		this.listFiles = null;
		this.file = null;
		this.sender = null;
	}
	
	//TYPE_CLIENT_MSG
	public Message(int type, String message) {
		super();
		this.message = message;
		this.type = type;
		this.serverDate = null;
		this.listUsers = null;
		this.listFiles = null;
		this.file = null;
		this.sender = null;
	}
	
	//TYPE_LIST_USERS
	public Message(ArrayList<Client> listUsers, int type, Date serverDate) {
		super();
		this.message = null;
		this.type = type;
		this.serverDate = serverDate;
		this.listUsers = listUsers;
		this.listFiles = null;
		this.file = null;
		this.sender = null;
	}
	
	//TYPE_LIST_FILES
	public Message(int type, Date serverDate, File[] listFiles) {
		super();
		this.message = null;
		this.type = type;
		this.serverDate = serverDate;
		this.listUsers = null;
		this.listFiles = listFiles;
		this.file = null;
		this.sender = null;
	}

	public ArrayList<Client> getListUsers() {
		return listUsers;
	}

	public void setListUsers(ArrayList<Client> listUsers) {
		this.listUsers = listUsers;
	}

	public Client getSender() {
		return sender;
	}

	public void setSender(Client sender) {
		this.sender = sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getServerDate() {
		return serverDate;
	}

	public void setServerDate(Date serverDate) {
		this.serverDate = serverDate;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public File[] getListFiles() {
		return listFiles;
	}

	public void setListFiles(File[] listFiles) {
		this.listFiles = listFiles;
	}

	//Formata data pro formatado que quero (dd-MM-yyyy HH:mm:ss)
	public String getFormattedServerDate() {
		String str = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(this.getServerDate());
		return str;
	}
	
	
	
	
	
	
	
	
	
}
