package framework;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MessageManager {
	
	private ObjectInputStream objectInput;
	private ObjectOutputStream objectOutput;	
	
	public MessageManager(ObjectInputStream ois, ObjectOutputStream oos) {
		this.objectInput = ois;
		this.objectOutput = oos;
	}
	
	//Função que le uma mensagem atraves do ObjectInputStream do socket
	public Message readMessage() {
		Message message = null;
	
		try {
			message = (Message) getObjectInput().readObject();
		} catch (IOException | ClassNotFoundException e) {
			this.close();
		}
		
		return message;
	}
	
	//Função que envia uma mensage atraves do ObjectOutputStream do socket
	//Para enviar um objecto ele tem de ter implementado o Serializable
	public void sendMessage(Message message) {
		try {
			getObjectOutput().flush();
			getObjectOutput().writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Fecha ligações com o client ou servidor (depende de que lado esta usando, ja que esse objeto server para os dois lados)
	public void close() {
		try {
			getObjectInput().close();
			getObjectOutput().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public ObjectInputStream getObjectInput() {
		return objectInput;
	}

	public void setObjectInput(ObjectInputStream objectInput) {
		this.objectInput = objectInput;
	}

	public ObjectOutputStream getObjectOutput() {
		return objectOutput;
	}

	public void setObjectOutput(ObjectOutputStream objectOutput) {
		this.objectOutput = objectOutput;
	}
	
	
}
