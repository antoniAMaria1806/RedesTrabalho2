import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Servidor {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("---- SERVIDOR DE CHAT ----");

        System.out.print("Digite a porta do servidor: ");
        int port = Integer.parseInt(scanner.nextLine());

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor aguardando conexão na porta " + port + "...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clientSocket.getRemoteSocketAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

            // Thread para receber mensagens do cliente
            Thread receiver = new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        System.out.println("[Cliente] " + message);
                        System.out.print(""); // prompt limpo para digitar
                    }
                } catch (IOException e) {
                    System.out.println("Cliente desconectado.");
                }
            });
            receiver.setDaemon(true);
            receiver.start();

            while (true) {
                // Sem prefixo [Você], só um prompt simples
                String response = scanner.nextLine();

                if (response.equalsIgnoreCase("sair")) {
                    System.out.println("Encerrando servidor.");
                    break;
                }

                out.println(response);
            }

        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }

        scanner.close();
    }
}
