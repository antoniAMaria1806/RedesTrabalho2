import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        System.out.println("---- CLIENTE DE CHAT ----");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o endereço do servidor (deixe em branco para 'localhost'): ");
        String address = scanner.nextLine();
        if (address.isEmpty()) {
            address = "localhost";
        }

        System.out.print("Digite a porta do servidor (deixe em branco para '10000'): ");
        String portInput = scanner.nextLine();
        int port = portInput.isEmpty() ? 10000 : Integer.parseInt(portInput);

        try (Socket client = new Socket(address, port)) {
            System.out.printf("Conexão estabelecida com o SERVIDOR: %s:%d%n", address, port);

            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            // Thread para ouvir o servidor
            Thread listener = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = reader.readLine()) != null) {
                        System.out.println("\n[SERVIDOR >] " + serverMessage);
                        System.out.print("CLIENTE > "); // reaparece o prompt de entrada
                    }
                } catch (IOException e) {
                    System.out.println("Conexão encerrada pelo servidor.");
                }
            });
            listener.start();

            // Thread principal envia mensagens
            while (true) {
                System.out.print("CLIENTE > ");
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("SAIR")) {
                    System.out.println("Encerrando cliente...");
                    break;
                }

                writer.println(message);
            }

        } catch (IOException e) {
            System.err.println("Erro de comunicação com o servidor: " + e.getMessage());
        }

        scanner.close();
    }
}
