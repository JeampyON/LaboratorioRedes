import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainServidor {

    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        ProcesadorMensajes procesador = new ProcesadorMensajes();

        try {
            String ipLocal = InetAddress.getLocalHost().getHostAddress();
            System.out.println("==========================================");
            System.out.println("           SERVIDOR INICIADO              ");
            System.out.println("==========================================");
            System.out.println("Puerto de escucha : " + PUERTO);
            System.out.println("IP de este equipo : " + ipLocal);
            System.out.println("(Comparte esta IP con el otro equipo)");
            System.out.println("==========================================\n");
        } catch (IOException e) {
            System.out.println("Servidor iniciado en puerto " + PUERTO);
        }

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                System.out.println("Esperando conexion...\n");
                Socket socketCliente = serverSocket.accept();

                String horaConexion = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                System.out.println("=== NUEVA CONEXION ENTRANTE ===");
                System.out.println("Hora de conexion   : " + horaConexion);
                System.out.println("IP del cliente     : " + socketCliente.getInetAddress().getHostAddress());
                System.out.println("Puerto del cliente : " + socketCliente.getPort());
                System.out.println("Puerto del servidor: " + socketCliente.getLocalPort());
                System.out.println("===============================\n");

                atenderCliente(socketCliente, procesador);
            }
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    private static void atenderCliente(Socket socketCliente, ProcesadorMensajes procesador) {
        try {
            ManejadorConexion conexion = new ManejadorConexion(socketCliente);
            while (true) {
                String mensajeRecibido = conexion.recibir();
                if (mensajeRecibido == null || mensajeRecibido.equalsIgnoreCase("salir")) {
                    System.out.println("El cliente cerro la sesion.\n");
                    conexion.enviar("Sesion terminada. Hasta luego!");
                    break;
                }
                System.out.println("Mensaje recibido: " + mensajeRecibido);
                String respuesta = procesador.procesar(mensajeRecibido);
                conexion.enviar(respuesta);
                System.out.println("Respuesta enviada: " + respuesta + "\n");
            }
            conexion.cerrar();
            System.out.println("Conexion con el cliente finalizada.\n");
        } catch (IOException e) {
            System.out.println("Error atendiendo al cliente: " + e.getMessage());
        }
    }
}
