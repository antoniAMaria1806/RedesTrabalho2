import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("---- CLIENTE DE CHAT ----");

        System.out.print("Endereço do servidor (vazio para localhost): ");
        String address = scanner.nextLine().trim();
        if (address.isEmpty()) address = "localhost";

        System.out.print("Porta do servidor (vazio para 10000): ");
        String portInput = scanner.nextLine().trim();
        int port = portInput.isEmpty() ? 10000 : Integer.parseInt(portInput);

        try (Socket socket = new Socket(address, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            // Thread para receber mensagens do servidor
            Thread receiver = new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        System.out.println("[Servidor] " + message);
                        System.out.print("");  // deixa o prompt limpo para digitar
                    }
                } catch (IOException e) {
                    System.out.println("Conexão encerrada pelo servidor.");
                }
            });
            receiver.setDaemon(true);
            receiver.start();

            while (true) {
                // Sem prefixo [Você], só um prompt simples
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("sair")) {
                    System.out.println("Encerrando cliente.");
                    break;
                }

                out.println(message);
            }

        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
        }

        scanner.close();
    }
}
