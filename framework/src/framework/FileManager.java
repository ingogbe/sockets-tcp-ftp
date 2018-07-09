package framework;
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
import java.math.BigDecimal;

import javax.swing.JTextField;

public class FileManager {
	public final static int FILE_SIZE = 10485760; // Em bytes. Limite do tamanho de arquivo (10 MB = 10485760 bytes)
	
	public final static int PACKAGE_SIZE = 100; // Em bytes.
	
	public final static int TYPE_RECEIVE = 1;
	public final static int TYPE_SEND = 2;
	
	public final static boolean PRINT_PACKAGE_DATA = false;
	
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
	
	//Função de envio do arquivo em modo binario
	public void sendFile(File file, JTextField jtfTax) throws IOException{
		
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
        
        int n_bytes = mybytearray.length;      
        
        //Envia o array de bytes com a informação do arquivo atraves do OutputStream do socket
        getOutput().flush();
        
        /**/
        System.out.println("Init Upload");
        
        float averageTax = 0;
        int packageCounter = 0;
        
        for(int offset = 0; offset < n_bytes; offset = offset + PACKAGE_SIZE) {
        	long initUpload = System.nanoTime();
        	
        	int currentPackageSize = 0;
        	
        	if((offset + PACKAGE_SIZE) > n_bytes) {
        		currentPackageSize = n_bytes - offset;
        		getOutput().write(mybytearray, offset, currentPackageSize);
        	}
        	else {
        		currentPackageSize = PACKAGE_SIZE;
        		getOutput().write(mybytearray, offset, currentPackageSize );
        	}
        	
        	long endUpload = System.nanoTime();
        	
        	
        	packageCounter++;
        	System.out.println("Package " + packageCounter + ":");
        	
        	float transferTax = calcTransferTax(n_bytes, offset, initUpload, endUpload, currentPackageSize);
        	
        	System.out.print("Current tax: " + new BigDecimal(transferTax).toPlainString() + " kbps | ");
        	System.out.println("Bytes write: " + currentPackageSize);
        	System.out.println("--------");
        	
        	averageTax = averageTax + transferTax;
        	
        }
        
        jtfTax.setText(String.format("%.2f", (averageTax / packageCounter)) + " kbps");
        
        
        //Escreve bytes do array "mybytearray" começando em 0 até mybytearray.length (total de bytes)
        //getOutput().write(mybytearray, 0, mybytearray.length);
        
        getOutput().flush();
        System.out.println("Done.");
        
        bis.close();
        fis.close();
	
	}
	
	//Função de recebimento do arquivo em modo binario
	public void receiveFile(String receiveFilepath, File f, JTextField jtfTax) throws IOException{
		
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
		
		System.out.println("Init Download");
        
        float averageTax = 0;
        int packageCounter = 0;
		
		int n_bytes = (int) f.length();
	
		do {
			long initDownload = System.nanoTime();
						
			//Lê pacotes até que não tenha mais conteudo a ser lido (-1)
			bytesRead = getInput().read(mybytearray, current, PACKAGE_SIZE);
			
			long endDownload = System.nanoTime();
        	
        	if(bytesRead >= 0) {
        		
        		packageCounter++;
    			System.out.println("Package " + packageCounter + ":");
    			
    			float transferTax = calcTransferTax(n_bytes, current, initDownload, endDownload, PACKAGE_SIZE);
            	
            	System.out.print("Current tax: " + new BigDecimal(transferTax).toPlainString() + " kbps | ");
            	System.out.println("Bytes read: " + bytesRead);
            	System.out.println("--------");
            	
            	averageTax = averageTax + transferTax;
        		
				current += bytesRead;
				
        	}
			
		} while(bytesRead > -1);
		
		jtfTax.setText(String.format("%.2f", (averageTax / packageCounter)) + " kbps");

		
		bos.flush();
		
		//Escreve arquivo fisicamente no computador (cria o arquivo)
		bos.write(mybytearray, 0 , current);
		
		bos.flush();
		System.out.println("File " + receiveFilepath + " downloaded (" + current + " bytes read)");
		
		bos.close();
		fos.close();
		
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
	
	//Funcão para pegar a extensão de um arquivo (File) de seu nome
	public String getFileExtension(File file) {
	    String name = file.getName();
	    try {
	        return name.substring(name.lastIndexOf(".") + 1);
	    } catch (Exception e) {
	        return "";
	    }
	}
	
	
	
	public float calcTransferTax(int n_bytes, int offset, long init, long end, int packageSize) {
		float tempoTransfer_ns = (end - init);
		
		float tempoTransfer_ms = tempoTransfer_ns / 1000000;
		
        float tempoTransfer_s = tempoTransfer_ms / 1000;
        
        float kb_package = ((float) packageSize) / 1024;
        
        float taxaTransfer = kb_package / tempoTransfer_s;
        
        if(PRINT_PACKAGE_DATA) {
        	System.out.println("Offset = " + offset + " | File Size (b) = " + n_bytes + " | File Size (kb) = " + (n_bytes / 1024));
        	
        	System.out.print("Init (ns) value: ");
    		System.out.print(init);
    		
    		System.out.print(" | End (ns) value: ");
    		System.out.println(end);
    		
    		System.out.print("Transfer Time (ns): ");
    		System.out.print(tempoTransfer_ns);
    		
    		System.out.print(" | Transfer Time (ms): ");
    		System.out.print(tempoTransfer_ms);
            
            System.out.print(" | Transfer Time (s): ");
            System.out.println(tempoTransfer_s);
            
            System.out.print("Package Size (b): ");
            System.out.print(packageSize);
            
            System.out.print(" | Package Size (kb): ");
            System.out.println(kb_package);
            
            System.out.print("Package Transfer Tax: ");
            System.out.print(taxaTransfer);
            System.out.println(" kbps");
            
            System.out.println();
        }
        
        
        return taxaTransfer;
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
