public class ProcesadorMensajes {

    /**
     * Método principal. Detecta si el mensaje trae un comando
     * (MAYUS, REV, LEN) y aplica la operación correspondiente.
     * Si no hay comando reconocido, aplica el procesamiento básico.
     */
    public String procesar(String mensaje) {
        if (mensaje == null || mensaje.trim().isEmpty()) {
            return "ERROR: El mensaje estaba vacío.";
        }

        // Separamos en dos partes: "COMANDO: contenido"
        String[] partes = mensaje.split(":", 2);

        // Si tiene el formato COMANDO: texto
        if (partes.length == 2) {
            String comando  = partes[0].trim().toUpperCase();
            String contenido = partes[1].trim();

            switch (comando) {
                case "MAYUS":
                    return "[MAYUS] " + aMayusculas(contenido);

                case "REV":
                    return "[REV] " + invertir(contenido);

                case "LEN":
                    return "[LEN] " + longitud(contenido);

                default:
                    // El formato tiene ":" pero el comando no existe
                    return procesarBasico(mensaje);
            }
        }

        // Sin comando reconocido → procesamiento básico
        return procesarBasico(mensaje);
    }

    // --- Operaciones individuales ---

    private String aMayusculas(String texto) {
        return texto.toUpperCase();
    }

    private String invertir(String texto) {
        return new StringBuilder(texto).reverse().toString();
    }

    private String longitud(String texto) {
        return String.valueOf(texto.length());
    }

    // Procesamiento básico: mayúsculas + longitud (como en "En clase")
    private String procesarBasico(String texto) {
        return texto.toUpperCase() + " (Longitud: " + texto.length() + ")";
    }
}
