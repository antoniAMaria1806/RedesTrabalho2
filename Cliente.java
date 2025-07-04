import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        System.out.println("---- CLIENTE DE CHAT ----");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o endereço do servidor (deixe em branco para localhost): ");
        String address = scanner.nextLine();
        if (address.isEmpty()) address = "localhost";

        System.out.print("Digite a porta do servidor (deixe em branco para 10000): ");
        String portInput = scanner.nextLine();
        int port = portInput.isEmpty() ? 10000 : Integer.parseInt(portInput);

        try (Socket client = new Socket(address, port)) {
            System.out.printf("Conectado ao servidor em %s:%d%n", address, port);

            OutputStream out = client.getOutputStream();
            InputStream in = client.getInputStream();

            Thread receiver = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        String message = new String(buffer, 0, bytesRead);
                        System.out.print("\n" + message);
                        System.out.print("[Você] ");
                    }
                } catch (IOException e) {
                    System.out.println("Conexão encerrada pelo servidor.");
                }
            });
            receiver.start();

            while (true) {
                System.out.print("[Você] ");
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("sair")) {
                    System.out.println("Encerrando cliente.");
                    break;
                }

                // CORRIGIDO: envia com quebra de linha
                out.write((message + "\n").getBytes());
                out.flush();
            }

        } catch (IOException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
        }

        scanner.close();
    }
}
