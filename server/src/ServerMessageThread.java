import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import framework.Client;
import framework.Message;
import framework.MessageManager;

public class ServerMessageThread extends Thread{
	Client client;
	private MessageManager messageManager;
	
	private boolean running;
	private Socket messageSocket;
	
	public ServerMessageThread(Socket messageSocket) {
		super();
		this.messageSocket = messageSocket;
		this.running = true;
		this.client = new Client();
		
		this.messageManager = null;
		
		this.start();
	}
	
	public void run() {
		//Ao iniciar a thread do client no lado do servidor ja cria a saida e entrada de dados
		try {
			ObjectOutputStream oos = new ObjectOutputStream(getMessageSocket().getOutputStream());
			ObjectInputStream ois  = new ObjectInputStream(getMessageSocket().getInputStream());
			setMessageManager(new MessageManager(ois, oos));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(isRunning()) {
			//Enquanto estiver rodando fica lendo as mensagens
			Message message = getMessageManager().readMessage();
						
			//Caso receba uma mensagem nula, ele desconecta do server 
			if(message == null) {
				disconnect();
				break;
			}
			
			
			
			else if(message.getType() == Message.TYPE_SERVER_MSG) {
				
			}
			
			else if(message.getType() == Message.TYPE_CLIENT_MSG) {
				
			}
			
			
			else if(message.getType() == Message.TYPE_LIST_USERS) {
				message.setServerDate(new Date());
				
				//Carrega a lista de usuarios ja logados no server
				ArrayList<Client> clients = new ArrayList<Client>();
				for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
					clients.add(tc.getClient());
				}
				
				String msg = this.getClient().getName() + " solicitou lista de usuarios!";
				MainServer.jtaChat.append("[" + message.getFormattedServerDate() + "] " + msg + "\n");
				
				getMessageManager().sendMessage(new Message(clients, Message.TYPE_LIST_USERS, new Date()));
				getMessageManager().sendMessage(new Message("Lista de usuários atualizada!", Message.TYPE_SERVER_MSG, new Date()));
			}
			
			
			
			else if(message.getType() == Message.TYPE_LIST_FILES) {
				message.setServerDate(new Date());
				
				File folder = new File(ServerFileThread.SERVER_STORAGE);
				File[] listOfFiles = folder.listFiles();
				
				String msg = this.getClient().getName() + " solicitou lista de arquivos!";
				MainServer.jtaChat.append("[" + message.getFormattedServerDate() + "] " + msg + "\n");
				
				getMessageManager().sendMessage(new Message(Message.TYPE_LIST_FILES, new Date(), listOfFiles));
				getMessageManager().sendMessage(new Message("Lista de arquivos atualizada!", Message.TYPE_SERVER_MSG, new Date()));
			}
			
			
			
			else if(message.getType() == Message.TYPE_USER_CONNECTION) {
				message.setServerDate(new Date());
				this.setClient(message.getSender());
				
				String msg = this.getClient().getName() + " conectado!";
				MainServer.jtaChat.append("[" + message.getFormattedServerDate() + "] " + msg + "\n");
				
				//Carrega a lista de usuarios ja logados no server
				ArrayList<Client> clients = new ArrayList<Client>();
				for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
					clients.add(tc.getClient());
				}
				
				File folder = new File(ServerFileThread.SERVER_STORAGE);
				File[] listOfFiles = folder.listFiles();
				
				for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
					tc.getMessageManager().sendMessage(new Message(msg, Message.TYPE_SERVER_MSG, new Date()));
					tc.getMessageManager().sendMessage(new Message(clients, Message.TYPE_LIST_USERS, new Date()));
					
					if(tc.equals(this)) {
						tc.getMessageManager().sendMessage(new Message(Message.TYPE_LIST_FILES, new Date(), listOfFiles));
					}
				}
				
				
			}
			
			
			
		}
	}
	
	//Disconecta o usuario
	public void disconnect() {
		//Para a thread setando o running para false
		setRunning(false);
		
		//Fecha ligações com o client
		try {
			getMessageManager().close();
			getMessageSocket().close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		//Remove usuario da lista de usuario conectados
		MainServer.getConnectedMessageThreads().remove(this);
		
		//Cria mensagem de desconexão deste usuario para os outros usuarios logados
		Message msg = new Message("Client " + getClient().getName() + " disconnected!", Message.TYPE_SERVER_MSG, new Date());
		MainServer.jtaChat.append("[" + msg.getFormattedServerDate() + "] " + msg.getMessage() + "!\n");
		
		//Carrega a lista de usuarios ja logados no server
		ArrayList<Client> clients = new ArrayList<Client>();
		for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
			clients.add(tc.getClient());
		}
		
		//Cria mensagem de atualização de lista de usuarios
		Message updateUsers = new Message(clients, Message.TYPE_LIST_USERS, new Date());
		
		//Dispara as duas mensagens criadas (atualização de lista de usuarios e mensagem de desconexão deste usuario)
		//para todos os usuarios logados
		for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
			tc.getMessageManager().sendMessage(msg);
			tc.getMessageManager().sendMessage(updateUsers);
		}
	}
	
	public MessageManager getMessageManager() {
		return messageManager;
	}

	public void setMessageManager(MessageManager messageManager) {
		this.messageManager = messageManager;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public Socket getMessageSocket() {
		return messageSocket;
	}

	public void setMessageSocket(Socket messageSocket) {
		this.messageSocket = messageSocket;
	}
	
	
	
	
	
}
