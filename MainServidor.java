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

        // Mostramos la IP local para que el otro equipo sepa a dónde conectarse
        try {
            String ipLocal = InetAddress.getLocalHost().getHostAddress();
            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║           SERVIDOR INICIADO              ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  Puerto de escucha : " + PUERTO + "                  ║");
            System.out.println("║  IP de este equipo : " + ipLocal + "          ║");
            System.out.println("║  (Comparte esta IP con el otro equipo)   ║");
            System.out.println("╚══════════════════════════════════════════╝\n");
        } catch (IOException e) {
            System.out.println("Servidor iniciado en puerto " + PUERTO);
        }

        // try-with-resources: cierra el ServerSocket automáticamente al terminar
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {

            // El servidor puede atender múltiples clientes uno por uno
            while (true) {
                System.out.println("Esperando conexión...\n");

                Socket socketCliente = serverSocket.accept();

                // --- Extensión 5: mostramos datos del cliente + hora de conexión ---
                String horaConexion = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                System.out.println("=== NUEVA CONEXIÓN ENTRANTE ===");
                System.out.println("Hora de conexión   : " + horaConexion);
                System.out.println("IP del cliente     : " + socketCliente.getInetAddress().getHostAddress());
                System.out.println("Puerto del cliente : " + socketCliente.getPort());
                System.out.println("Puerto del servidor: " + socketCliente.getLocalPort());
                System.out.println("===============================\n");

                // Atendemos a este cliente con su bucle de mensajes
                atenderCliente(socketCliente, procesador);
            }

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    /**
     * Atiende a un cliente con un bucle:
     * recibe mensajes hasta que el cliente mande "salir".
     */
    private static void atenderCliente(Socket socketCliente, ProcesadorMensajes procesador) {
        try {
            ManejadorConexion conexion = new ManejadorConexion(socketCliente);

            // --- Extensión 2: bucle de múltiples mensajes ---
            while (true) {
                String mensajeRecibido = conexion.recibir();

                // Si el cliente cerró la conexión o mandó "salir"
                if (mensajeRecibido == null || mensajeRecibido.equalsIgnoreCase("salir")) {
                    System.out.println("El cliente cerró la sesión.\n");
                    conexion.enviar("Sesión terminada. ¡Hasta luego!");
                    break;
                }

                System.out.println("Mensaje recibido: " + mensajeRecibido);

                // --- Extensión 1: procesamos con comandos ---
                String respuesta = procesador.procesar(mensajeRecibido);
                conexion.enviar(respuesta);
                System.out.println("Respuesta enviada: " + respuesta + "\n");
            }

            conexion.cerrar();
            System.out.println("Conexión con el cliente finalizada.\n");

        } catch (IOException e) {
            System.out.println("Error atendiendo al cliente: " + e.getMessage());
        }
    }
}
