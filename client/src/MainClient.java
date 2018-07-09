import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

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
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

import framework.Client;
import framework.Message;

public class MainClient extends JFrame{
	
	private static final long serialVersionUID = 1L;

	private JMenuBar jmBarraMenu;
	private JMenu jmArquivo;
	private JMenuItem jmiArquivoSair;
	
	public static JTextArea jtaChat;
	public static JTextField jtfName, jtfIp, jtfUpload, jtfDownload;
	public static JButton jbConnect;
	private JTextField jtfMessageBox;
	
	private  Container C;
	
	public static ClientMessageThread clientThread;
	
	private static DefaultTableModel dtmUsers;
	private static JTable jtTableUsers;
	public static ArrayList<Client> connectedClients = new ArrayList<Client>();
	
	
	private static DefaultTableModel dtmFiles;
	private static JTable jtTableFiles;
	private static File[] currentFiles = {};
	
	public MainClient() {
		super("Client");
		setSize(1024,600);
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
		jmBarraMenu.setBounds(0, 0, 1024, 20);
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
		
		
		TitledBorder tbUpload = new TitledBorder("Upload");
		
		JPanel jpUpload = new JPanel();
		jpUpload.setLayout(null);
		jpUpload.setBounds(800, 25, 105, 60);
		jpUpload.setBorder(tbUpload);
		C.add(jpUpload);
		
		jtfUpload = new JTextField();
		jtfUpload.setBounds(9, 20, 88, 30);
		jtfUpload.setEditable(false);
		jtfUpload.setText("0 kbps");
		jpUpload.add(jtfUpload);
		
		TitledBorder tbDownload = new TitledBorder("Download");
		
		JPanel jpDownload = new JPanel();
		jpDownload.setLayout(null);
		jpDownload.setBounds(905, 25, 105, 60);
		jpDownload.setBorder(tbDownload);
		C.add(jpDownload);
		
		jtfDownload = new JTextField();
		jtfDownload.setBounds(9, 20, 88, 30);
		jtfDownload.setEditable(false);
		jtfDownload.setText("0 kbps");
		jpDownload.add(jtfDownload);
		
		
		
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
			
			//Implement table cell tool tips.           
			public String getToolTipText(MouseEvent e) {
				String tip = null;
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);
				
				try {
				    tip = getValueAt(rowIndex, colIndex).toString();
				} catch (RuntimeException e1) {
				    //catch null pointer exception if mouse is over an empty line
				}
				
				return tip;
			}
		};
		JScrollPane jspUsers = new JScrollPane(jtTableUsers);
		
		jspUsers.setBounds(615, 90, 175, 435);
		C.add(jspUsers);
		
		
		
		
		dtmFiles = new DefaultTableModel();
		dtmFiles.addColumn("Arquivos");
		
		jtTableFiles = new JTable(dtmFiles){
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column){  
				return false;  
			}  
			
			//Implement table cell tool tips.           
			public String getToolTipText(MouseEvent e) {
				String tip = null;
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);
				
				try {
				    tip = getValueAt(rowIndex, colIndex).toString();
				} catch (RuntimeException e1) {
				    //catch null pointer exception if mouse is over an empty line
				}
				
				return tip;
			}
		};
		jtTableFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane jspFiles = new JScrollPane(jtTableFiles);
		
		jspFiles.setBounds(800, 90, 210, 435);
		C.add(jspFiles);
		
		
		
		
		JButton jbUploadFile = new JButton("Upload File");
		jbUploadFile.setBounds(615, 530, 175, 35);
		jbUploadFile.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				uploadActionFile();
			}
		});
		C.add(jbUploadFile);
		
		JButton jbDownloadFile = new JButton("Download File");
		jbDownloadFile.setBounds(800, 530, 210, 35);
		jbDownloadFile.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				downloadActionFile();
			}
		});
		C.add(jbDownloadFile);
		
		
		
	}
	
	public static void updateUploadTax(String s) {
		jtfUpload.setText(s);
	}
	
	public static void removeUserRows() {
		for(int i = dtmUsers.getRowCount()-1; i >= 0; i--) {
			dtmUsers.removeRow(i);
		}
	}
	
	public static void removeFilesRows() {
		for(int i = dtmFiles.getRowCount()-1; i >= 0; i--) {
			dtmFiles.removeRow(i);
		}
	}
	
	public static void updateUserTable(ArrayList<Client> clients) {
		removeUserRows();
		connectedClients = clients;
		
		for(Client c :connectedClients) {
			dtmUsers.addRow(new Object[]{c.getName()});
		}
	}
	
	public static void updateFilesTable(File[] files) {
		removeFilesRows();
		currentFiles = files;
		
		for(File f :currentFiles) {
			dtmFiles.addRow(new Object[]{f.getName() + "[" + (f.length() / 1024) + "kb]"});
		}
	}
	
	/**
	 * Envia um arquivo qualquer selecionado através do JFileChooser para todos os usuários ou os usuários logados selecionados.
	 */
	public void uploadActionFile() {
		
		//Abre o File Chooser
		JFileChooser jc = new JFileChooser();
		int returnValue = jc.showOpenDialog(null);
		
        if (returnValue == JFileChooser.APPROVE_OPTION) {
        	File selectedFile = jc.getSelectedFile();
			
			//Envia mensagem a todos
			clientThread.sendFile(selectedFile);
			
        }
	}
	
	public void downloadActionFile() {
		
		int row = jtTableFiles.getSelectedRow();
		
		//Possui arquivo selecionado
		if(row >= 0) {
			clientThread.receiveFile(new Message(currentFiles[jtTableFiles.getSelectedRow()], Message.TYPE_DOWNLOAD_FILE, clientThread.getClient()));
		}       
		     
		
	}
	
	/**
	 * Envia uma mensagem de texto para todos os usuários ou os usuários logados selecionados.
	 */
	public void sendAction() {
		
		String cmd = jtfMessageBox.getText().trim();
		
		if(cmd.equals("list users")) {
			Message msg = new Message(Message.TYPE_LIST_USERS, clientThread.getClient());
			clientThread.getMessageManager().sendMessage(msg);
			System.out.println("list users");
		}
		else if(cmd.equals("list files")) {
			Message msg = new Message(Message.TYPE_LIST_FILES, clientThread.getClient());
			clientThread.getMessageManager().sendMessage(msg);
			System.out.println("list files");
		}
		
		
		jtfMessageBox.setText("");
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
