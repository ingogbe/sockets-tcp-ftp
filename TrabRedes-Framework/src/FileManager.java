import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class FileManager {
	public final static int FILE_SIZE = 10485760; // Em bytes. Limite do tamanho de arquivo (10 MB = 10485760 bytes)
	public final static int TYPE_RECEIVE = 1;
	public final static int TYPE_SEND = 2;
	
	private int type;
	
	private InputStream input;
	private OutputStream output;
	private BufferedInputStream bis;
	private BufferedOutputStream bos;
	
	public FileManager(InputStream is, OutputStream os){
		this.type = 0;
		
		this.input = is;
		this.output = os;
		this.bis = null;
		this.bos = null;
	}
	
	//Essa mensagem de configuração pelos metodos (sendConfig() e readConfig()) é necessaria para saber quem enviou o arquivo e o nome original 
	//do arquivo (com extensão de tipo)
	//Assim é salvo uma copia no servidor com o prefixo do id do usuario + nome arquivo original (Ex.: idUsuario_nomeArquivo.txt; 13_poesia.txt)
	//ja que cada usuario tem um ID unico em tempo de execução do servidor
	
	//Função de envio da mensagem de configuração através do output
	//Para enviar um objeto ele tem de ter implementado o Serializable
	public void sendConfig(Message message) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(getOutput());
			oos.flush();
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Função de recebimento da mensagem de configuração através do input
	public Message readConfig() {
		
		Message message = null;
		
		try {
			ObjectInputStream ois = new ObjectInputStream(getInput());
			message = (Message) ois.readObject();
			
			return message;
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//Função de envio do arquivo em modo binario
	public void sendFile(File file) throws IOException{
		
		//Cria um array de byts do tamanho do arquivo passado (File)
		byte [] mybytearray  = new byte [(int)file.length()];
		//Abre um FileInputStream para envio do arquivo, passando o arquivo (File) como parametro
		//para saber onde o arquivo está fisicamente no computador
		FileInputStream fis = new FileInputStream(file);
		
		//Cria o objeto para leitura do arquivo em buffer
        bis = new BufferedInputStream(fis);
        //Le o arquivo armazenando os dados no array de bytes
        bis.read(mybytearray,0,mybytearray.length);
        System.out.println("Sending " + file.getAbsolutePath() + "(" + mybytearray.length + " bytes)");
        
        //Envia o array de bytes com a informação do arquivo atraves do OutputStream do socket
        getOutput().flush();
        getOutput().write(mybytearray,0,mybytearray.length);
        getOutput().flush();
        System.out.println("Done.");
	
	}
	
	//Função de recebimento do arquivo em modo binario
	public void receiveFile(String receiveFilepath) throws IOException{
		
	    int bytesRead;
	    int current = 0;
		
		//Cria array de bytes com o tamanho limite de arquivo especificado (10 MB)
		byte [] mybytearray  = new byte [FILE_SIZE];
		//Abre um FileOutputStream para recebimento do arquivo, passando o arquivo (File) como parametro
		//para saber onde o arquivo será salvo fisicamente no computador
		FileOutputStream fos = new FileOutputStream(receiveFilepath);
		fos.flush();
		
		//Cria o objeto para escrita do arquivo em buffer
		bos = new BufferedOutputStream(fos);
		bos.flush();
		
		//Le o array de bytes com a informação do arquivo atraves do InputStream do socket
		bytesRead = getInput().read(mybytearray,0,mybytearray.length);
		current = bytesRead;
	
		do {
			//Continua a ler até que não tenha mais conteudo a ser lido
			bytesRead = getInput().read(mybytearray, current, (mybytearray.length-current));
			if(bytesRead >= 0)
				current += bytesRead;
		} while(bytesRead > -1);

		bos.flush();
		//Escreve arquivo fisicamente no computador (cria o arquivo)
		bos.write(mybytearray, 0 , current);
		bos.flush();
		System.out.println("File " + receiveFilepath + " downloaded (" + current + " bytes read)");
		
	}
	
	//Funcão para pegar a extensão de um arquivo (File) de seu nome
	public String getFileExtension(File file) {
	    String name = file.getName();
	    try {
	        return name.substring(name.lastIndexOf(".") + 1);
	    } catch (Exception e) {
	        return "";
	    }
	}
	
	//Fecha ligações com o client ou servidor (depende de que lado esta usando, ja que esse objeto server para os dois lados)
	public void close() {
		try {
			getInput().close();
			getOutput().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public InputStream getInput() {
		return input;
	}

	public void setInput(InputStream input) {
		this.input = input;
	}

	public OutputStream getOutput() {
		return output;
	}

	public void setOutput(OutputStream output) {
		this.output = output;
	}
	
	
	
	
}
