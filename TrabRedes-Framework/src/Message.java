import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final int TYPE_PLAINTEXT = 1;
	public static final int TYPE_FILE = 2;
	public static final int TYPE_UPDATECLIENT = 3;
	public static final int TYPE_UPDATEUSERS = 4;
	
	public static final int TYPE_FILE_SEND = 1;
	public static final int TYPE_FILE_RECEIVE = 2;
	
	private String message;
	private int type;
	private int type_file;
	private Date senderDate;
	private Date serverDate;
	private File file;
	
	private Client receiver;
	private Client sender;
	private Client update;
	private ArrayList<Client> users;
	
	//Construtor para atualização de usuario
	public Message(Client update, int type) {
		super();
		this.message = "";
		this.type = type;
		this.senderDate = null;
		this.serverDate = null;
		this.sender = null;
		this.receiver = null;
		this.update = update;
		this.users = new ArrayList<Client>();
		this.file = null;
		this.type_file = 0;
	}
	
	//Construtor para atualização de lista de usuarios logados
	public Message(ArrayList<Client> users, int type) {
		super();
		this.message = "";
		this.type = type;
		this.senderDate = null;
		this.serverDate = null;
		this.sender = null;
		this.receiver = null;
		this.update = null;
		this.users = users;
		this.file = null;
		this.type_file = 0;
	}
	
	//Construtor para mensagens do servidor (ex: usuario X conectado)
	public Message(String message, int type, Date serverDate) {
		super();
		this.message = message;
		this.type = type;
		this.senderDate = null;
		this.serverDate = serverDate;
		this.sender = null;
		this.receiver = null;
		this.update = null;
		this.users = new ArrayList<Client>();
		this.file = null;
		this.type_file = 0;
	}
	
	//Construtor para mensagem simples do remetente para todos
	public Message(String message, int type, Date senderDate, Client sender) {
		super();
		this.message = message;
		this.type = type;
		this.senderDate = senderDate;
		this.serverDate = null;
		this.sender = sender;
		this.receiver = null;
		this.update = null;
		this.users = new ArrayList<Client>();
		this.file = null;
		this.type_file = 0;
	}
	
	//Construtor para mensagem simples do remetente para destinatario especifico
	public Message(String message, int type, Date senderDate, Client sender, Client receiver) {
		super();
		this.message = message;
		this.type = type;
		this.senderDate = senderDate;
		this.serverDate = null;
		this.sender = sender;
		this.receiver = receiver;
		this.update = null;
		this.users = new ArrayList<Client>();
		this.file = null;
		this.type_file = 0;
	}
	
	//Construtor de mensagem de configuração para envio de arquivo para todos
	public Message(int type, Date senderDate, Client sender, File file, int type_file) {
		super();
		this.message = "";
		this.type = type;
		this.senderDate = senderDate;
		this.serverDate = null;
		this.sender = sender;
		this.receiver = null;
		this.update = null;
		this.users = new ArrayList<Client>();
		this.file = file;
		this.type_file = type_file;
	}
	
	//Construtor de mensagem de configuração para envio de arquivo para destinatario especifico
	public Message(int type, Date senderDate, Client sender, Client receiver, File file, int type_file) {
		super();
		this.message = "";
		this.type = type;
		this.senderDate = senderDate;
		this.serverDate = null;
		this.sender = sender;
		this.receiver = receiver;
		this.update = null;
		this.users = new ArrayList<Client>();
		this.file = file;
		this.type_file = type_file;
	}

	public ArrayList<Client> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<Client> users) {
		this.users = users;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getSenderDate() {
		return senderDate;
	}

	public void setSenderDate(Date senderDate) {
		this.senderDate = senderDate;
	}

	public Date getServerDate() {
		return serverDate;
	}
	
	//Formata data pro formatado que quero (dd-MM-yyyy HH:mm:ss)
	public String getFormattedServerDate() {
		String str = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(this.getServerDate());
		return str;
	}

	public void setServerDate(Date serverDate) {
		this.serverDate = serverDate;
	}

	public Client getReceiver() {
		return receiver;
	}

	public void setReceiver(Client receiver) {
		this.receiver = receiver;
	}

	public Client getSender() {
		return sender;
	}

	public void setSender(Client sender) {
		this.sender = sender;
	}

	public Client getUpdate() {
		return update;
	}

	public void setUpdate(Client update) {
		this.update = update;
	}
	
	public boolean hasSender() {
		return this.sender != null;
	}
	
	public boolean hasReceiver() {
		return this.receiver != null;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getType_file() {
		return type_file;
	}

	public void setType_file(int type_file) {
		this.type_file = type_file;
	}
	
	
	
	
}
