import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class ServerFileThread extends Thread{
	
	private Client client;
	private Socket fileSocket;
	private FileManager fileManager;
	
	public static final String SERVER_STORAGE = "C:/Users/Celegma/Desktop/serverStorage/";;
	
	public ServerFileThread(Socket fileSocket) {
		super();
		this.fileSocket = fileSocket;
		this.client = new Client();
		this.fileManager = null;
		
		this.start();
	}
	
	public void run() {
		try {
			//Inicia gerenciador de arquivos
			setFileManager(new FileManager(getFileSocket().getInputStream(), getFileSocket().getOutputStream()));
			//Recebe mensagem de configuração com dados do arquivo
			Message m = getFileManager().readConfig();
			m.setServerDate(new Date());
			
			//Se for o servidor receber um arquivo de um client
			if(Message.TYPE_FILE_SEND == m.getType_file()) {
				//Atualiza client da thread
				setClient(m.getSender());
				
				if(getClient() == null) {
					System.out.println("Erro ao receber arquivo no servidor");
				}
				else {
					System.out.println("Recebeu cliente: " + getClient().getName() + " ID = " + getClient().getId());
				}
				
				//Recebe arquivo no caminho e nome especificado
				getFileManager().receiveFile(SERVER_STORAGE + getClient().getId() + "_" + m.getFile().getName());
				
				//Altera tipo da mensagem antes de encaminhar para cliente destinatario
				//Mensagem alertado que há um arquivo recebido a ser baixado
				m.setType_file(Message.TYPE_FILE_RECEIVE);
				m.setFile(new File(SERVER_STORAGE + getClient().getId() + "_" + m.getFile().getName()));
				
				//Se houver destinatario, envia para o mesmo
				if(m.hasReceiver()) {
					MainServer.jtaChat.append("["+ m.getFormattedServerDate() + "] " + m.getSender().getName() + "(ID: " + m.getSender().getId() + ") TO " + m.getReceiver().getName() + "(ID: " + m.getReceiver().getId() + ") >> Send a file: '" +m.getFile().getName() + "'\n");
					
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						if(tc.getClient().getId() == m.getReceiver().getId() || tc.getClient().getId() == m.getSender().getId()) {
							tc.getMessageManager().sendMessage(m);
						}
					}
				}
				//Se não houver destinatario, envia para todos
				else {
					MainServer.jtaChat.append("["+ m.getFormattedServerDate() + "] " + m.getSender().getName() + "(ID: " + m.getSender().getId() + ") >> Send a file: '" + m.getFile().getName() + "'\n");
					
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						tc.getMessageManager().sendMessage(m);
					}
				}
				
				
			}
			//Se for para enviar um arquivo a um client
			//Mensagens desse tipo são a confirmação que o cliente está pronto para receber o arquivo e começa a enviar
			else if(Message.TYPE_FILE_RECEIVE == m.getType_file()) {
				//Atualiza client da thread
				setClient(m.getSender());
				
				if(getClient() == null) {
					System.out.println("Erro ao enviar arquivo ao cliente");
				}
				else {
					System.out.println("Recebeu cliente: " + getClient().getName() + " ID = " + getClient().getId());
				}
				
				//Envia o arquivo
	    		getFileManager().sendFile(m.getFile());
				
			}
			
			//Fecha ligações com o client
			getFileManager().close();
			getFileSocket().close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Socket getFileSocket() {
		return fileSocket;
	}

	public void setFileSocket(Socket fileSocket) {
		this.fileSocket = fileSocket;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	
	
	
}
