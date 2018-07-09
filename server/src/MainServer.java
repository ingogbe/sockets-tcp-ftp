import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import framework.ServerConsts;

public class MainServer extends JFrame{
	private static final long serialVersionUID = 1L;

	private static ArrayList<ServerMessageThread> connectedMessageThreads = new ArrayList<ServerMessageThread>();
	
	private static int clientID;
	
	private static ServerSocket messageServer;
	private static ServerSocket fileServer;
	
	private Container C;
	private JMenuBar jmBarraMenu;
	private JMenu jmArquivo;
	private JMenuItem jmiArquivoSair;
	public static JTextArea jtaChat;
	
	public MainServer() {
		super("Server");
		setSize(800,600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(null);
		
		C = getContentPane();
		C.setLayout(null);
		
		initComponents();
		
		MainServer.clientID = 1;
	}
	
	public void initComponents(){
		jmBarraMenu = new JMenuBar();
		jmBarraMenu.setBounds(0, 0, 800, 20);
		C.add(jmBarraMenu);
		
		//BEGIN - ARQUIVO
		jmArquivo = new JMenu("File");
		jmBarraMenu.add(jmArquivo);
		
		jmiArquivoSair = new JMenuItem("Exit");
		jmiArquivoSair.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(EXIT_ON_CLOSE);
			}
		});
		jmArquivo.add(jmiArquivoSair);
		//END - ARQUIVO
		
		JPanel jpChat = new JPanel();
		jpChat.setBounds(7, 25, 780, 540);
		jpChat.setBackground(Color.GRAY);
		jpChat.setLayout(null);
		C.add(jpChat);
		
		jtaChat = new JTextArea();
		jtaChat.setEditable(false);
		jtaChat.setLineWrap(true);
		
		DefaultCaret caret = (DefaultCaret) jtaChat.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JScrollPane jsp = new JScrollPane(jtaChat);
		jsp.setBounds(5, 5, 770, 495);
		jpChat.add(jsp);
		
		JTextField jtfMessageBox = new JTextField();
		jtfMessageBox.setBounds(5, 505, 680, 30);
		jtfMessageBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO: Send message from server
			}
		});
		jpChat.add(jtfMessageBox);
		
		JButton jbSend = new JButton("Send");
		jbSend.setBounds(690, 505, 85, 30);
		jbSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO: Send message from server
			}
		});
		jpChat.add(jbSend);
		
	}
	
	public static int getNewID() {
		int id = MainServer.clientID;
		MainServer.clientID++;
		
		return id;
	}
	
	public static void initProgram() {
		new MainServer().setVisible(true);
		
		//Inicia Thread do servidor de mensagens
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					messageServer = new ServerSocket(ServerConsts.MESSAGE_PORT);
					jtaChat.append("Message server created and listening. Ip: "+ InetAddress.getLocalHost() +". Port: " + ServerConsts.MESSAGE_PORT + "\n");
					
					while(true) {
			        	Socket socket = messageServer.accept();
			        	
			        	ServerMessageThread tc = new ServerMessageThread(socket);
			        	//Salva todos os clients conectados em um array
			        	connectedMessageThreads.add(tc);
			        } 
					
				} catch(IOException e) {
					jtaChat.append("Erro: " + e.getMessage() + "\n");
					e.printStackTrace();
				}
			}
		}).start();
		
		//Inicia Thread do servidor de arquivos
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					fileServer = new ServerSocket(ServerConsts.FILE_PORT);
					jtaChat.append("File server created and listening. Ip: "+ InetAddress.getLocalHost() +". Port: " + ServerConsts.FILE_PORT + "\n");
					
					while(true) {
			        	Socket socket = fileServer.accept();
			        	new ServerFileThread(socket);
			        } 
					
				} catch(Exception e) {
					jtaChat.append("Erro: " + e.getMessage() + "\n");
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public static void main(String[] x) {
		initProgram();
	}

	public static ArrayList<ServerMessageThread> getConnectedMessageThreads() {
		return connectedMessageThreads;
	}

	public void setConnectedMessageThreads(ArrayList<ServerMessageThread> connectedMessageThreads) {
		MainServer.connectedMessageThreads = connectedMessageThreads;
	}

	public ServerSocket getMessageServer() {
		return messageServer;
	}

	public void setMessageServer(ServerSocket servidor) {
		MainServer.messageServer = servidor;
	}

	

	
	
	
	
}

