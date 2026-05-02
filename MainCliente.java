import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class MainCliente {

    private static final int TIMEOUT_CONEXION = 5000; // 5 segundos maximos para conectar

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        mostrarBienvenida();

        // Bucle externo: permite conectarse a distintos servidores sin cerrar el programa
        while (true) {
            String host = pedirIP(scanner);
            if (host == null) {
                // El usuario eligio salir desde el menu de IP
                System.out.println("\nCerrando el programa. Hasta luego!");
                break;
            }

            int puerto = pedirPuerto(scanner);

            System.out.println("\nConectando a " + host + ":" + puerto + "...");

            Socket socket = intentarConexion(host, puerto);

            if (socket == null) {
                // La conexion fallo, volvemos al inicio del bucle para pedir otra IP
                continue;
            }

            // Conexion exitosa
            System.out.println("\n=== CONECTADO AL SERVIDOR ===");
            System.out.println("Puerto local  (cliente)  : " + socket.getLocalPort());
            System.out.println("Puerto remoto (servidor) : " + socket.getPort());
            System.out.println("IP del servidor          : " + socket.getInetAddress().getHostAddress());
            System.out.println("=============================\n");
            System.out.println("Escribe 'salir'     -> termina esta sesion y elige otro servidor");
            System.out.println("Escribe 'cerrar'    -> cierra el programa completamente\n");

            boolean cerrarPrograma = false;

            try {
                ManejadorConexion conexion = new ManejadorConexion(socket);

                // Bucle interno: sesion de mensajes con el servidor actual
                while (true) {
                    System.out.print("Mensaje > ");
                    String mensaje = scanner.nextLine().trim();

                    if (mensaje.equalsIgnoreCase("cerrar")) {
                        // Avisamos al servidor que nos vamos y cerramos todo
                        conexion.enviar("salir");
                        String respuesta = conexion.recibir();
                        if (respuesta != null) System.out.println("Servidor: " + respuesta);
                        cerrarPrograma = true;
                        break;
                    }

                    if (mensaje.equalsIgnoreCase("salir")) {
                        // Terminamos esta sesion pero el programa sigue
                        conexion.enviar("salir");
                        String respuesta = conexion.recibir();
                        if (respuesta != null) System.out.println("Servidor: " + respuesta);
                        System.out.println("\nSesion cerrada. Puedes conectarte a otro servidor.\n");
                        break;
                    }

                    if (mensaje.isEmpty()) continue;

                    conexion.enviar(mensaje);
                    String respuesta = conexion.recibir();
                    System.out.println("Servidor: " + respuesta + "\n");
                }

                conexion.cerrar();

            } catch (IOException e) {
                System.out.println("Error durante la sesion: " + e.getMessage());
                System.out.println("La conexion se interrumpio. Puedes intentar con otro servidor.\n");
            }

            if (cerrarPrograma) {
                System.out.println("\nCerrando el programa. Hasta luego!");
                break;
            }
        }

        scanner.close();
    }

    // Muestra el menu inicial
    private static void mostrarBienvenida() {
        System.out.println("==========================================");
        System.out.println("              CLIENTE LISTO               ");
        System.out.println("==========================================");
        System.out.println("Comandos disponibles en sesion:");
        System.out.println("  MAYUS: <texto>  -> convierte a mayusculas");
        System.out.println("  REV: <texto>    -> invierte el texto");
        System.out.println("  LEN: <texto>    -> calcula la longitud");
        System.out.println("  salir           -> termina sesion, elige otro servidor");
        System.out.println("  cerrar          -> cierra el programa");
        System.out.println("==========================================\n");
    }

    // Pide la IP con opcion de salir
    private static String pedirIP(Scanner scanner) {
        while (true) {
            System.out.println("Opciones:");
            System.out.println("  - Escribe una IP (ej: 192.168.56.1)");
            System.out.println("  - Presiona Enter para usar 'localhost'");
            System.out.println("  - Escribe 'salir' para cerrar el programa");
            System.out.print("IP del servidor: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("salir")) return null;
            if (input.isEmpty()) return "localhost";
            return input;
        }
    }

    // Pide el puerto con las opciones disponibles
    private static int pedirPuerto(Scanner scanner) {
        while (true) {
            System.out.println("Seleccione el servicio:");
            System.out.println("  1 -> Puerto 5000 (procesamiento de texto)");
            System.out.println("  2 -> Puerto 6000 (responde solo OK)");
            System.out.print("Opcion: ");
            String opcion = scanner.nextLine().trim();

            if (opcion.equals("1")) return 5000;
            if (opcion.equals("2")) return 6000;
            System.out.println("Opcion no valida, ingrese 1 o 2.\n");
        }
    }

    // Intenta conectar con timeout. Retorna el socket o null si falla
    private static Socket intentarConexion(String host, int puerto) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, puerto), TIMEOUT_CONEXION);
            return socket;
        } catch (IOException e) {
            System.out.println("----------------------------------------------");
            System.out.println("  No se pudo conectar a " + host + ":" + puerto);
            System.out.println("  Causa: " + obtenerCausaAmigable(e.getMessage()));
            System.out.println("  Verifica que el servidor este encendido");
            System.out.println("  y que la IP y el puerto sean correctos.");
            System.out.println("----------------------------------------------\n");
            return null;
        }
    }

    // Convierte el mensaje de error tecnico a uno mas legible
    private static String obtenerCausaAmigable(String mensajeError) {
        if (mensajeError == null) return "Error desconocido";
        if (mensajeError.contains("timed out") || mensajeError.contains("timeout")) return "Tiempo de espera agotado (IP no encontrada)";
        if (mensajeError.contains("Connection refused")) return "Conexion rechazada (puerto incorrecto o servidor apagado)";
        if (mensajeError.contains("unreachable") || mensajeError.contains("Network")) return "Red inaccesible (IP no existe en esta red)";
        if (mensajeError.contains("Name or service not known")) return "Nombre de host no encontrado";
        return mensajeError;
    }
}
