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
                System.out.println("Servidor iniciado na porta " + port + ".");

                // Thread para entrada de mensagens do servidor
                Thread inputThread = new Thread(() -> {
                    while (true) {
                        String message = scanner.nextLine();
                        if (message.equalsIgnoreCase("sair")) {
                            broadcastMessage("Servidor desconectou todos.");
                            System.exit(0);
                        }
                        System.out.println("[Você] " + message);
                        broadcastMessage("Servidor: " + message);
                    }
                });
                inputThread.start();

                // Aguarda clientes
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Cliente conectado: " + clientSocket.getRemoteSocketAddress());
                    new ClientHandler(clientSocket).start();
                }

            } catch (IOException e) {
                System.out.println("Erro no servidor: " + e.getMessage());
            }

        } catch (NumberFormatException e) {
            System.out.println("Porta inválida.");
        }
    }

    private static void broadcastMessage(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                System.err.println("Erro ao configurar cliente: " + e.getMessage());
            }
        }

        public void run() {
            clientWriters.add(writer);
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    String formatted = "Cliente: " + message;
                    System.out.println(formatted);
                    broadcastMessage(formatted);
                }
            } catch (IOException e) {
                System.out.println("Cliente desconectado: " + clientSocket.getRemoteSocketAddress());
            } finally {
                clientWriters.remove(writer);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Erro ao fechar conexão com cliente.");
                }
            }
        }
    }
}
