import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

public class MainClient extends JFrame{
	
	private static final long serialVersionUID = 1L;

	private JMenuBar jmBarraMenu;
	private JMenu jmArquivo;
	private JMenuItem jmiArquivoSair;
	
	public static JTextArea jtaChat;
	public static JTextField jtfName, jtfIp;
	public static JButton jbConnect;
	private JTextField jtfMessageBox;
	
	private  Container C;
	
	public static ClientMessageThread clientThread;
	
	private static DefaultTableModel dtmUsers;
	private static JTable jtTableUsers;
	public static ArrayList<Client> connectedClients = new ArrayList<Client>();
	
	public MainClient() {
		super("Client");
		setSize(800,600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(null);
		
		C = getContentPane();
		C.setLayout(null);
		
		initComponents();
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
		
		TitledBorder tbConfiguration = new TitledBorder("Configuration");
		
		JPanel jpConfiguration = new JPanel();
		jpConfiguration.setLayout(null);
		jpConfiguration.setBounds(5, 25, 785, 60);
		jpConfiguration.setBorder(tbConfiguration);
		C.add(jpConfiguration);
		
		JLabel jlName = new JLabel("Name:");
		jlName.setBounds(10, 20, 40, 30);
		jpConfiguration.add(jlName);
		
		jtfName = new JTextField();
		jtfName.setBounds(50, 20, 295, 30);
		jtfName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectAction();
			}
		});
		jpConfiguration.add(jtfName);
		
		JLabel jlIp = new JLabel("IP:");
		jlIp.setBounds(355, 20, 20, 30);
		jpConfiguration.add(jlIp);
		
		jtfIp = new JTextField();
		jtfIp.setBounds(375, 20, 300, 30);
		jtfIp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectAction();
			}
		});
		jpConfiguration.add(jtfIp);
		
		jbConnect = new JButton("Connect");
		jbConnect.setBounds(680, 20, 95, 30);
		jbConnect.setMargin(new Insets(0, 0, 0, 0));
		jbConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectAction();
			}
		});
		jpConfiguration.add(jbConnect);
		
		JPanel jpChat = new JPanel();
		jpChat.setBounds(7, 90, 600, 475);
		jpChat.setBackground(Color.GRAY);
		jpChat.setLayout(null);
		C.add(jpChat);
		
		jtaChat = new JTextArea();
		jtaChat.setEditable(false);
		jtaChat.setLineWrap(true);
		
		DefaultCaret caret = (DefaultCaret) jtaChat.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JScrollPane jsp = new JScrollPane(jtaChat);
		jsp.setBounds(5, 5, 590, 430);
		jpChat.add(jsp);
		
		jtfMessageBox = new JTextField();
		jtfMessageBox.setBounds(5, 440, 500, 30);
		jtfMessageBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendAction();
			}
		});
		jpChat.add(jtfMessageBox);
		
		JButton jbSend = new JButton("Send");
		jbSend.setBounds(510, 440, 85, 30);
		jbSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendAction();
			}
		});
		jpChat.add(jbSend);
		
		
		dtmUsers = new DefaultTableModel();
		dtmUsers.addColumn("Usuários");
		
		jtTableUsers = new JTable(dtmUsers){
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column){  
				return false;  
			}  
		};
		JScrollPane jspUsers = new JScrollPane(jtTableUsers);
		
		jspUsers.setBounds(615, 90, 175, 435);
		C.add(jspUsers);
		
		
		JButton jbSendFile = new JButton("Send File");
		jbSendFile.setBounds(615, 530, 175, 35);
		jbSendFile.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				sendActionFile();
			}
		});
		C.add(jbSendFile);
		
		
		
	}
	
	public static void removeUserRows() {
		for(int i = dtmUsers.getRowCount()-1; i >= 0; i--) {
			dtmUsers.removeRow(i);
		}
	}
	
	public static void updateUserTable(ArrayList<Client> clients) {
		removeUserRows();
		connectedClients = clients;
		
		for(Client c :connectedClients) {
			dtmUsers.addRow(new Object[]{"[" + c.getId() + "] " + c.getName()});
		}
	}
	
	/**
	 * Envia um arquivo qualquer selecionado através do JFileChooser para todos os usuários ou os usuários logados selecionados.
	 */
	public void sendActionFile() {
		
		//Abre o File Chooser
		JFileChooser jc = new JFileChooser();
		int returnValue = jc.showOpenDialog(null);
		
        if (returnValue == JFileChooser.APPROVE_OPTION) {
        	File selectedFile = jc.getSelectedFile();
        	
        	//Pega os usuários selecionados na tabela de usuários logados
        	int selectedUsers[] = jtTableUsers.getSelectedRows();
			boolean himself = false;
			
			//Verifica se somente ele mesmo (o client) está selecionado
			if(selectedUsers.length == 1) {
				for(int j :selectedUsers) {
					if(connectedClients.get(j).getId() == clientThread.getClient().getId()) {
						himself = true;
						break;
					}
				}
			}
			
			//Caso somente ele mesmo estiver selecionado
			//Ou nenhum usuário estiver selecionado
			//Ou todos estiverem selecionados
			//Envia mensagem a todos
			if(himself || selectedUsers.length == 0 || selectedUsers.length == dtmUsers.getRowCount()) {
				clientThread.sendFile(selectedFile, null);
			}
			else {
				//Caso contrário envia a mensagem apenas aos selecionados
				for(int j :selectedUsers) {
					if(connectedClients.get(j).getId() != clientThread.getClient().getId()) {
						clientThread.sendFile(selectedFile, connectedClients.get(j));
					}
				}
				
				jtfMessageBox.setText("");
			}
			
        }
	}
	
	/**
	 * Envia uma mensagem de texto para todos os usuários ou os usuários logados selecionados.
	 */
	public void sendAction() {
		//Verifica se o campo de mensagem não está vazio
		if(!jtfMessageBox.getText().isEmpty()) {
			//Pega os usuários selecionados na tabela de usuários logados
			int selectedUsers[] = jtTableUsers.getSelectedRows();
			boolean himself = false;
			
			//Verifica se somente ele mesmo (o client) está selecionado
			if(selectedUsers.length == 1) {
				for(int j :selectedUsers) {
					if(connectedClients.get(j).getId() == clientThread.getClient().getId()) {
						himself = true;
						break;
					}
				}
			}
			
			//Caso somente ele mesmo estiver selecionado
			//Ou nenhum usuário estiver selecionado
			//Ou todos estiverem selecionados
			//Envia mensagem a todos
			if(himself || selectedUsers.length == 0 || selectedUsers.length == dtmUsers.getRowCount()) {
				Message msg = new Message(jtfMessageBox.getText(), Message.TYPE_PLAINTEXT, new Date(), clientThread.getClient());
				clientThread.getMessageManager().sendMessage(msg);
				jtfMessageBox.setText("");
			}
			else {
				//Caso contrário envia a mensagem apenas aos selecionados
				for(int j :selectedUsers) {
					if(connectedClients.get(j).getId() != clientThread.getClient().getId()) {
						Message msg = new Message(jtfMessageBox.getText(), Message.TYPE_PLAINTEXT, new Date(), clientThread.getClient(), connectedClients.get(j));
						clientThread.getMessageManager().sendMessage(msg);
					}
				}
				
				jtfMessageBox.setText("");
			}
			
		}
		
	}
	
	/**
	 * Faz a conexão com o servidor.
	 * Pega os dados dos campos de textos (ip e name), cria a thread do client e chama o metodo de conexão da mesma.
	 * Se já houver uma thread, chama a função disconnect() da mesma, e habilita novamente os campos
	 */
	public void connectAction() {
		String ip = jtfIp.getText();
		String name = jtfName.getText();
		
		if(clientThread == null) {
			clientThread = new ClientMessageThread(ip, name);
			clientThread.connect();
		}
		else {
			//Libera os campos de texto e muda texto do botão de "Disconnect" para "Connect"
			clientThread.disconnect();
			jtfIp.setEditable(true);
			jtfName.setEditable(true);
			clientThread = null;
			jbConnect.setText("Connect");
		}
		
	}
	
	public static void main(String[] args) {
		new MainClient().setVisible(true); 
	}

}
