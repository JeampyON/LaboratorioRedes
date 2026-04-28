import java.io.*;
import java.net.Socket;

public class ManejadorConexion {
    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;

    // El constructor inicializa los flujos de entrada y salida
    public ManejadorConexion(Socket socket) throws IOException {
        this.socket = socket;
        this.salida = new PrintWriter(socket.getOutputStream(), true);
        this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // Método para enviar texto
    public void enviar(String mensaje) {
        salida.println(mensaje);
    }

    // Método para recibir texto
    public String recibir() throws IOException {
        return entrada.readLine();
    }

    // Método para cerrar los recursos de forma segura
    public void cerrar() {
        try {
            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar: " + e.getMessage());
        }
    }
}
