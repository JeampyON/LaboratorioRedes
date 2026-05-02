import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ServidorOK {

    private static final int PUERTO = 6000;

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("         SERVIDOR OK - PUERTO 6000        ");
        System.out.println("==========================================");
        System.out.println("Este servidor responde OK a todo mensaje.");
        System.out.println("==========================================\n");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                System.out.println("Esperando conexion en puerto " + PUERTO + "...\n");
                Socket socketCliente = serverSocket.accept();

                String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                System.out.println("=== NUEVA CONEXION ===");
                System.out.println("Hora              : " + hora);
                System.out.println("IP del cliente    : " + socketCliente.getInetAddress().getHostAddress());
                System.out.println("Puerto del cliente: " + socketCliente.getPort());
                System.out.println("Puerto servidor   : " + socketCliente.getLocalPort());
                System.out.println("======================\n");

                atenderCliente(socketCliente);
            }
        } catch (IOException e) {
            System.out.println("Error en ServidorOK: " + e.getMessage());
        }
    }

    private static void atenderCliente(Socket socketCliente) {
        try {
            ManejadorConexion conexion = new ManejadorConexion(socketCliente);
            while (true) {
                String mensaje = conexion.recibir();
                if (mensaje == null || mensaje.equalsIgnoreCase("salir")) {
                    conexion.enviar("Sesion terminada. Hasta luego!");
                    System.out.println("Cliente desconectado.\n");
                    break;
                }
                System.out.println("Mensaje recibido: " + mensaje);
                conexion.enviar("OK");
                System.out.println("Respuesta enviada: OK\n");
            }
            conexion.cerrar();
        } catch (IOException e) {
            System.out.println("Error atendiendo cliente: " + e.getMessage());
        }
    }
}
