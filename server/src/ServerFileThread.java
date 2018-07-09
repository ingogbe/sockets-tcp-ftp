import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import javax.swing.JTextField;

import framework.FileManager;
import framework.Message;

public class ServerFileThread extends Thread{
	
	private Socket fileSocket;
	private FileManager fileManager;
	
	public static final String SERVER_STORAGE = "C:/Users/Celegma/Desktop/serverStorage/";
	
	public ServerFileThread(Socket fileSocket) {
		super();
		this.fileSocket = fileSocket;
		this.fileManager = null;
		
		this.start();
	}
	
	public void run() {
		System.out.println("Conexão de dados aberta!");
		
		try {
			//Inicia gerenciador de arquivos
			setFileManager(new FileManager(getFileSocket().getInputStream(), getFileSocket().getOutputStream()));
			
			//Recebe mensagem de configuração com dados do arquivo
			Message m = getFileManager().readConfig();
			m.setServerDate(new Date());
			
			//Se for o servidor receber um arquivo de um client
			if(Message.TYPE_UPLOAD_FILE == m.getType()) {				
				//Recebe arquivo no caminho e nome especificado
				//TODO: Include JTF tax
				getFileManager().receiveFile(SERVER_STORAGE + m.getFile().getName(), m.getFile(), new JTextField());
				
				//Mensagem que usuario upou um arquivo
				String msg = m.getSender().getName() + " >> Upload a file: '" + m.getFile().getName() + "[" + (m.getFile().length() / 1024) + "kb]";
				System.out.println(msg);
				
				//Mensagem alertado que há um arquivo recebido
				MainServer.jtaChat.append("["+ m.getFormattedServerDate() + "] " + msg + "'\n");
				
				File folder = new File(ServerFileThread.SERVER_STORAGE);
				File[] listOfFiles = folder.listFiles();
				
				for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
					tc.getMessageManager().sendMessage(new Message(msg, Message.TYPE_SERVER_MSG, new Date()));
					tc.getMessageManager().sendMessage(new Message(Message.TYPE_LIST_FILES, new Date(), listOfFiles));
				}
			}
			//Se for para enviar um arquivo a um client
			//Mensagens desse tipo são a confirmação que o cliente está pronto para receber o arquivo e começa a enviar
			else if(Message.TYPE_DOWNLOAD_FILE == m.getType()) {
				//Envia o arquivo
				//TODO: Include JTF tax
	    		getFileManager().sendFile(m.getFile(), new JTextField());
	    		
				//Mensagem que usuario baixou um arquivo
	    		
				String msg = m.getSender().getName() + " >> Download a file: '" + m.getFile().getName() + "[" + (m.getFile().length() / 1024) + "kb]";
				System.out.println(msg);
				
				//Mensagem alertado que há um arquivo recebido
				MainServer.jtaChat.append("["+ m.getFormattedServerDate() + "] " + msg + "'\n");
				
				for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
					tc.getMessageManager().sendMessage(new Message(msg, Message.TYPE_SERVER_MSG, new Date()));
				}
				
			}
			
			//Fecha ligações com o client
			getFileManager().close();
			getFileSocket().close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Conexão de dados finalizada!");
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
