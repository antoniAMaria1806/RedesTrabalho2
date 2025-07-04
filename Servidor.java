import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Servidor {

private static final List<PrintWriter> clientWriters = Collections.synchronizedList(new ArrayList<>());

  public static void main(String[] args) {
    System.out.println("---- SERVIDOR DE CHAT ----");

    try (Scanner scanner = new Scanner(System.in)) {
      System.out.print("Digite a porta para o servidor (ex: 12345): ");
      int port = Integer.parseInt(scanner.nextLine());

      try (ServerSocket serverSocket = new ServerSocket(port)) {
        System.out.println("Servidor iniciado. Aguardando conexões de clientes na porta " + port + "...");

        Thread serverInputThread = new Thread(() -> {
          String serverMessage;
          while (true) {
            serverMessage = scanner.nextLine();
            if (serverMessage.equalsIgnoreCase("SAIR")) {
              System.out.println("Comando 'SAIR' recebido. Encerrando o servidor.");
              broadcastMessage("SERVIDOR: Servidor está sendo encerrado. Desconectando todos os clientes.");
              try {
                serverSocket.close();
              } catch (IOException e) {
                System.err.println("Erro ao fechar o ServerSocket: " + e.getMessage());
              }
              System.exit(0);
            }
            broadcastMessage("SERVIDOR: " + serverMessage);
          }
        });
        serverInputThread.start();

        while (true) {
          Socket clientSocket = serverSocket.accept();
          System.out.println("Cliente conectado: " + clientSocket.getRemoteSocketAddress());
          new ClientHandler(clientSocket).start();
        }

      } catch (IOException e) {
        System.out.println("Servidor encerrado ou ocorreu um erro de I/O: " + e.getMessage());
      }

    } catch (NumberFormatException e) {
      System.err.println("Erro: Porta inválida. Por favor, insira um número.");
    }
  }

  private static void broadcastMessage(String message) {
    for (PrintWriter writer : new ArrayList<>(clientWriters)) {
      try {
        writer.println(message);
      } catch (Exception e) {
        System.err.println("Erro ao enviar mensagem para um cliente (provavelmente desconectado): " + e.getMessage());
      }
    }
  }

  private static class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;

    public ClientHandler(Socket socket) {
      this.clientSocket = socket;
      try {
        writer = new PrintWriter(clientSocket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      } catch (IOException e) {
        System.err.println("Erro ao configurar streams para o cliente: " + e.getMessage());
      }
    }

    @Override
    public void run() {
      clientWriters.add(writer);
      broadcastMessage("Novo cliente conectado: " + clientSocket.getRemoteSocketAddress());

      String clientMessage;
      try {
        while ((clientMessage = reader.readLine()) != null) {
          System.out.println("CLIENTE " + clientSocket.getRemoteSocketAddress() + ": " + clientMessage);
          broadcastMessage("CLIENTE " + clientSocket.getRemoteSocketAddress() + ": " + clientMessage);
        }
      } catch (IOException e) {
        System.out.println("Cliente " + clientSocket.getRemoteSocketAddress() + " desconectou-se.");
      } finally {
        if (writer != null) {
          clientWriters.remove(writer);
        }
        try {
          if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
          System.err.println("Erro ao fechar o socket do cliente: " + e.getMessage());
        }
        broadcastMessage("Cliente " + clientSocket.getRemoteSocketAddress() + " saiu do chat.");
      }
    }
  }
}