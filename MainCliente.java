import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MainCliente {

    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // --- Extra del ingeniero: el usuario escribe la IP del servidor ---
        // Si están en la misma PC usan "localhost".
        // Si están en red local, escriben la IP del otro equipo (ej: 192.168.1.10)
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║              CLIENTE LISTO               ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("Comandos disponibles:");
        System.out.println("  MAYUS: <texto>  → convierte a mayúsculas");
        System.out.println("  REV: <texto>    → invierte el texto");
        System.out.println("  LEN: <texto>    → calcula la longitud");
        System.out.println("  salir           → cierra la sesión\n");

        System.out.print("Ingrese la IP del servidor (Enter para 'localhost'): ");
        String host = scanner.nextLine().trim();
        if (host.isEmpty()) {
            host = "localhost";
        }

        try {
            Socket socket = new Socket(host, PUERTO);

            System.out.println("\n=== CONECTADO AL SERVIDOR ===");
            System.out.println("Puerto local  (cliente)  : " + socket.getLocalPort());
            System.out.println("Puerto remoto (servidor) : " + socket.getPort());
            System.out.println("IP del servidor          : " + socket.getInetAddress().getHostAddress());
            System.out.println("=============================\n");

            ManejadorConexion conexion = new ManejadorConexion(socket);

            // --- Extensión 2: bucle de múltiples mensajes ---
            while (true) {
                System.out.print("Mensaje > ");
                String mensaje = scanner.nextLine();

                // Enviamos el mensaje al servidor
                conexion.enviar(mensaje);

                // Leemos la respuesta
                String respuesta = conexion.recibir();
                System.out.println("Servidor: " + respuesta + "\n");

                // Si el usuario escribió "salir", terminamos
                if (mensaje.equalsIgnoreCase("salir")) {
                    break;
                }
            }

            conexion.cerrar();
            scanner.close();
            System.out.println("Sesión cerrada correctamente.");

        } catch (IOException e) {
            System.out.println("No se pudo conectar al servidor (" + host + ":" + PUERTO + ")");
            System.out.println("¿El servidor está encendido? Error: " + e.getMessage());
        }
    }
}
