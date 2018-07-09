import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

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
			
			if(message == null) {
				//Caso receba uma mensagem nula, ele desconecta do server 
				disconnect();
				break;
			}
			else if(message.getType() == Message.TYPE_UPDATECLIENT) {
				//Quando recebe uma mensagem TYPE_UPDATECLIENT é de atualização do client, seja do lado do server ou do lado do client
				
				//Pega o nome do client antes de atualizar
				String tempName = this.client.getName();
				
				//Atualiza client para o servidor
				setClient(message.getUpdate());
				
				//Se a thread foi recem criada, o client antes da atualização ainda não vai ter nome, portanto não possui ID tbm
				if(tempName.equals("")) {
					//Cria um ID unico para o client
					getClient().setId(MainServer.getNewID());
					//Cria mensagem de retorno para o client
					Message msgUpdate = new Message(getClient(), Message.TYPE_UPDATECLIENT);
					//Envia mensagem de update
					getMessageManager().sendMessage(msgUpdate);
					
					//Mostra todas as mensagens salvas no historico para o client novo
					for(Message msg : MainServer.messageHistoric) {
						if(msg.hasReceiver()) {
							if(this.getClient().getId() == msg.getReceiver().getId()) {
								getMessageManager().sendMessage(message);
								break;
							}
						}
						else {
							getMessageManager().sendMessage(msg);
						}
					}
					
					//Cria mensagem de novo usuario conectado
					Message msgConnect = new Message("Client connected (" + getClient().getName() + "). ID: " + getClient().getId() + "!", Message.TYPE_PLAINTEXT, new Date());
					MainServer.jtaChat.append("[" + msgConnect.getFormattedServerDate() + "] " + msgConnect.getMessage() + "!\n");
					//MainServer.messageHistoric.add(msgConnect);
					
					//Carrega a lista de usuarios ja logados no server
					ArrayList<Client> clients = new ArrayList<Client>();
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						clients.add(tc.getClient());
					}
					
					//Cria mensagem de atualização de lista de usuarios
					Message updateUsers = new Message(clients, Message.TYPE_UPDATEUSERS);
					
					//Dispara as duas mensagens criadas (atualização de lista de usuarios e mensagem de conexão de deste usuario)
					//para todos os usuarios logados
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						tc.getMessageManager().sendMessage(msgConnect);
						tc.getMessageManager().sendMessage(updateUsers);
					}
				}
				else if(!tempName.equals(this.client.getName())) {
					//Caso o tempName não esteja zerado e for diferente ao nome do usuario atualizado quer dizer que o usuario
					//trocou de nome (apesar dessa função não ser possivel, ja que bloqueei os campos para alterar os dados)
					
					//Cria mensagem de troca de nome e adiciona a msm as mensagens do historico
					Message msg = new Message(tempName + " has changed his name to " + getClient().getName() + "!", Message.TYPE_PLAINTEXT, new Date());
					MainServer.jtaChat.append("[" + msg.getFormattedServerDate() + "] " + msg.getMessage() + "\n");
					MainServer.messageHistoric.add(msg);
					
					//Carrega a lista de usuarios ja logados no server
					ArrayList<Client> clients = new ArrayList<Client>();
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						clients.add(tc.getClient());
					}
					
					//Cria mensagem de atualização de lista de usuarios
					Message updateUsers = new Message(clients, Message.TYPE_UPDATEUSERS);
					
					//Dispara as duas mensagens criadas (atualização de lista de usuarios e mensagem de troca de nome do usuario)
					//para todos os usuarios logados
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						tc.getMessageManager().sendMessage(msg);
						tc.getMessageManager().sendMessage(updateUsers);
					}
					
				}
			}
			else if(message.getType() == Message.TYPE_PLAINTEXT) {
				//Caso seja uma mensagem simples de texto, adiciona a data do servidor e retransmite para os destinarios corretos
				message.setServerDate(new Date());
				
				//Adiciona mensagem ao historico
				MainServer.messageHistoric.add(message);
				
				//Caso tenha um destinario especifico, procura a thread dele e envia somente para ele
				if(message.hasReceiver()) {
					MainServer.jtaChat.append("["+ message.getFormattedServerDate() + "] " + message.getSender().getName() + "(ID: " + message.getSender().getId() + ") TO " + message.getReceiver().getName() + "(ID: " + message.getReceiver().getId() + ") >> " + message.getMessage() + "\n");
					
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						if(tc.getClient().getId() == message.getReceiver().getId() || tc.getClient().getId() == message.getSender().getId()) {
							tc.getMessageManager().sendMessage(message);
						}
					}
				}
				//Caso contrario envia para todos os usuarios logados
				else {
					MainServer.jtaChat.append("["+ message.getFormattedServerDate() + "] " + message.getSender().getName() + "(ID: " + message.getSender().getId() + ") >> " + message.getMessage() + "\n");
					
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						tc.getMessageManager().sendMessage(message);
					}
				}
				
				
			}
			else if(message.getType() == Message.TYPE_FILE) {
				//Não é mais utilizado, pois agora quem trata isso é a thread especifica de arquivo
				System.out.println("Recebeu mensagem que tem arquivo pra chegar");
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
		Message msg = new Message("Client " + getClient().getName() + " [ID:" + getClient().getId() + "] disconnected!", Message.TYPE_PLAINTEXT, new Date());
		MainServer.jtaChat.append("[" + msg.getFormattedServerDate() + "] " + msg.getMessage() + "!\n");
		//MainServer.messageHistoric.add(msg);
		
		//Carrega a lista de usuarios ja logados no server
		ArrayList<Client> clients = new ArrayList<Client>();
		for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
			clients.add(tc.getClient());
		}
		
		//Cria mensagem de atualização de lista de usuarios
		Message updateUsers = new Message(clients, Message.TYPE_UPDATEUSERS);
		
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
