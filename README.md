# README #

Trabalho de Redes usando ServerSocket em Java

FTP (Servidor/Cliente)

### Projetos inclusos no repositório

```sh
1 client
2 framework
3 server
```

### Alterações antes do uso

É necessário fazer alteração na constante `SERVER_STORAGE` no arquivo `ServerFileThread.java` no projeto `server`, para o local onde deseja que o servidor utilize par armazenar os arquivos.

```sh
/server/src/ServerFileThread.java
```

```java
public static final String SERVER_STORAGE = "C:/Users/SEU_PC/Desktop/serverStorage/";
```

### Configuração antes do uso (Eclipse IDE)

- Crie todos os projetos
    * Vá em **File**
    * Em seguida em **New**
    * E clique na opção **Java Project**
    * Deselecione a opção **Use default location**
    * Clique em **Browse**
    * Navega para a pasta onde deu clone ou salvou o repositorio
    * Selecione o projeto que deseja criar (Projetos 1, 2 e 3 citados anteriormente)
    * Clique em **Finish**
    * Faça isso para os três projetos
    

- Faça a ligação do projeto `framework` com os projetos `client` e `server`
    * Clique com o botão direito sobre o projeto **`client`**
    * Vá até a opção **Build path**
    * Clique na opção **Configure Build Path...**
    * Na janela que se abriu. Clique na aba **Projects**
    * Clique no botão **Add**
    * Selecione o projeto **`framework`**
    * Clique em **Ok** e em seguida em **Apply and Close**
    * Repita o procedimento com o projeto **`server`**

### Uso

* Inicie primeiro o Server (`MainServer.java`)
* Inicie quantos Clientes desejar (`MainClient.java`)
* Para conectar ao servidor usando o Cliente, insera o IP mostrado na tela do Servidor no campo IP do Cliente.
