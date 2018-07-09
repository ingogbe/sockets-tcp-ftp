import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import framework.Client;
import framework.FileManager;
import framework.Message;
import framework.MessageManager;
import framework.ServerConsts;

public class ClientMessageThread extends Thread{
	
	private Client client;
	private MessageManager messageManager;
	private FileManager fileManager;
	
	private Socket messageSocket;
	private Socket fileSocket;
	
	private boolean running;
	private String hostAdress;

	public ClientMessageThread(String hostAdress, String clientName) {
		super();
		this.running = false;
		this.hostAdress = hostAdress;
		this.messageSocket = null;
		this.fileSocket = null;
		this.client = new Client(clientName);
		this.messageManager = null;
		this.fileManager = null;
	}
	
	public void run() {
		//Ao iniciar a thread do client ele já envia os dados do cliente para o servidor através
		//de uma mensagem do tipo updateClient que será retornada pelo servidor com o objeto client
		//completo já com o ID
		Message msg = new Message(Message.TYPE_USER_CONNECTION, getClient());
		getMessageManager().sendMessage(msg);
		
		//Enquanto o cliente está rodando fica lendo as mensagens que recebe
		while(isRunning()) {
			if(!getMessageSocket().isClosed()) {
				Message message = readMessage();
				if(message == null){
					break;
				}
			}	
			else {
				break;
			}
		}
	}
	
	public void connect() {
		try {
			//Conecta cliente ao server de mensagens
			setMessageSocket(new Socket(hostAdress, ServerConsts.MESSAGE_PORT));
			ObjectInputStream ois = new ObjectInputStream(getMessageSocket().getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(getMessageSocket().getOutputStream());
			setMessageManager(new MessageManager(ois, oos));
			
			//Muda o status para rodando = true
			setRunning(true);
			
			//Bloqueio os campos de texto e muda texto do botão de "Connect" para "Disconnect"
			MainClient.jtaChat.setText("");
			MainClient.jtfIp.setEditable(false);
			MainClient.jtfName.setEditable(false);
			MainClient.jbConnect.setText("Disconnect");
			
			//Inicia a thread do client
			this.start();
		} catch(ConnectException e) {
			//Se der algum erro de conexão, habilita campos, mostra mensagem de erro ao usuario
			//e muda o texto do botão para "Connect" novamente
			setRunning(false);
			
			MainClient.jtaChat.setText(e.getMessage());
			MainClient.jtfIp.setEditable(true);
			MainClient.jtfName.setEditable(true);
			MainClient.clientThread = null;
			MainClient.jbConnect.setText("Connect");
		} catch (IOException e) {
			System.out.println("Erro: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		//Muda o status para rodando = false
		setRunning(false);
		//Remove todos os usuários da lista de logados
		MainClient.removeUserRows();
		
		try {
			//Fecha ligações com o server de mensagens
			getMessageManager().close();
			getMessageSocket().close();
			
			//Limpa area de mensagens e escreve "Disconnected"
			MainClient.jtaChat.setText("");
			MainClient.jtaChat.append("SERVER [" + new Date() +"] => Disconnected\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Message readMessage() {
		Message message = null;
	
		//Le mensagem recebida do servidor
		message = getMessageManager().readMessage();
		
		//Se mensagem for nula, não faça nada
		if(message == null) {
			System.out.println("Mensagem nula recebida");
		}
		else if(message.getType() == Message.TYPE_LIST_USERS) {
			MainClient.updateUserTable(message.getListUsers());
		}
		else if(message.getType() == Message.TYPE_LIST_FILES) {
			MainClient.updateFilesTable(message.getListFiles());
		}
		else if(message.getType() == Message.TYPE_SERVER_MSG){
			MainClient.jtaChat.append("SERVER ["+ message.getFormattedServerDate() +"] => " + message.getMessage() + "\n");
		}
		
		return message;
	}
	
	//Envia arquivo para o servidor que então fara o encaminhamento para o destinatario
	public void sendFile(File file) {
		try {       
			Message message;
			
			//Cria mensagem de configuração para enviar ao servidor de arquivos, contendo os dados do arquivo (objeto File)
			message = new Message(file, Message.TYPE_UPLOAD_FILE, getClient());
			
			//Conecta ao servidor de mensagens 
        	setFileSocket(new Socket(hostAdress, ServerConsts.FILE_PORT));
    		InputStream is = getFileSocket().getInputStream();
    		OutputStream os = getFileSocket().getOutputStream();
    		setFileManager(new FileManager(is,os));
    		
    		//Envia mensagem de configuração que o server de arquivos está esperando
    		//E em seguida envia o arquivo
    		getFileManager().sendConfig(message);
    		getFileManager().sendFile(file, MainClient.jtfUpload);
    		
    		//Fecha ligações com o server de arquivos
			getFileManager().close();
			getFileSocket().close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void receiveFile(Message m) {
		
		try {
			//Conecta ao servidor de arquivos 
        	setFileSocket(new Socket(hostAdress, ServerConsts.FILE_PORT));
    		InputStream is = getFileSocket().getInputStream();
    		OutputStream os = getFileSocket().getOutputStream();
    		setFileManager(new FileManager(is,os));
    		
    		//Abre chooser para escolher onde será salvo o arquivo que recebeu
    		JFileChooser jc = new JFileChooser();
    		FileNameExtensionFilter filter = new FileNameExtensionFilter("Received File", getFileManager().getFileExtension(m.getFile()));
    		System.out.println(getFileManager().getFileExtension(m.getFile()));
    		jc.setFileFilter(filter);
    		jc.setSelectedFile(new File(m.getFile().getName()));
    		jc.setAcceptAllFileFilterUsed(false);
        	
        	int returnValue = jc.showSaveDialog(null);
    		
            if (returnValue == JFileChooser.APPROVE_OPTION) {
            	File f = jc.getSelectedFile();
            	
            	//Compara a extensão do arquivo escrito ou selecionado com o do arquivo da mensagem
            	//Se forem igual salva o arquivo
            	//Por padrão o filechooser já vem com o nome do arquivo populado
            	if(getFileManager().getFileExtension(f).equals(getFileManager().getFileExtension(m.getFile()))) {
            		
            		System.out.println(m.getFile());
            		
            		//Envia mensagem de configuração que o server de arquivos está esperando
            		//E em seguida recebe o arquivo
            		getFileManager().sendConfig(m);
            		getFileManager().receiveFile(f.getAbsolutePath(), m.getFile(), MainClient.jtfDownload);
            	}
            }
    		
    		//Fecha ligações com o server de arquivos
			getFileManager().close();
			getFileSocket().close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
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

	public Socket getMessageSocket() {
		return messageSocket;
	}

	public void setMessageSocket(Socket messageSocket) {
		this.messageSocket = messageSocket;
	}

	public MessageManager getMessageManager() {
		return messageManager;
	}

	public void setMessageManager(MessageManager messageManager) {
		this.messageManager = messageManager;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public Socket getFileSocket() {
		return fileSocket;
	}

	public void setFileSocket(Socket fileSocket) {
		this.fileSocket = fileSocket;
	}
	

}