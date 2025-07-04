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

            // Usar PrintWriter para enviar mensagens e BufferedReader para receber
            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            while (true) {
                System.out.print("CLIENTE > ");
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("SAIR")) {
                    System.out.println("Comando 'SAIR' foi recebido. Encerrando Cliente.");
                    break;
                }

                writer.println(message); // envia a mensagem com quebra de linha
                String response = reader.readLine(); // lê a resposta do servidor
                if (response == null) {
                    System.out.println("Servidor desconectado.");
                    break;
                }
                System.out.println(response);
            }

        } catch (IOException e) {
            System.err.println("Erro de comunicação com o servidor: " + e.getMessage());
        }

        scanner.close();
    }
}
