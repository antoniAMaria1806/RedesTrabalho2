import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[]args){
        System.out.println("---- CLIENTE DE CHAT ----");
        Scanner scanner = new Scanner(System.in); 
        System.out.println("Digite o endereço do servidor(deixe em branco para localhost): ");
        String address = scanner.nextLine();

        if(address.isEmpty()){ // Pede ao usuário o endereço do servidor se não digitar nada assume localhost
            address="localhost";
        }

        System.out.println("Digite a porta do servidor(deixe em branco para '10000'): ");
        String portInput =  scanner.nextLine(); 
        int port = portInput.isEmpty()? 10000 : Integer.parseInt(portInput); // o valor digitado é transformado em int 

        try(Socket client = new Socket(address, port)){
           System.out.printf("Conexão estabelecida com o SERVIDOR, quando desejar encerra-la digite SAIR: %s:%d%n", address, port) ;

            OutputStream out = client.getOutputStream(); // enviar dados 
            InputStream in = client.getInputStream(); // canal de entrada para receber dados
            
            while(true){
                System.out.print("CLIENTE > "); 
                String message = scanner.nextLine(); 

                if(message.equalsIgnoreCase("sair")){
                    System.out.println("Comando 'SAIR' foi recebido. Encerrando Cliente.");
                    break;
                }
                out.write(message.getBytes()); // converte a mensagem para bytes 
                out.flush(); // garante que o sdados sejam enviados imediatamente 

                
                byte [] buffer = new byte[1024]; 
                int bytesRead = in.read(buffer); 
                String response = new String(buffer, 0, bytesRead); 
                System.out.println("SERVIDOR> "+response);

            }

        } catch (IOException e){
            System.out.println("Erro ao fechar o ClientSocket: "+e.getMessage());
        } 
        scanner.close(); 
    }
}