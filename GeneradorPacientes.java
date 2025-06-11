import java.util.*;

public class GeneradorPacientes {
    private static final String[] NOMBRES = {"Manuel", "Jose", "Pablo", "Goku", "Pedro", "Lucía", "Janinna", "Elena"};
    private static final String[] APELLIDOS = {"Medina", "Ocaranza", "Vidal", "Ibrahimovic", "Sánchez", "Ronaldo", "Yamal", "Suazo"};
    private static final double[] PROBABILIDADES_CATEGORIA = {0.10, 0.15, 0.18, 0.27, 0.30};
    private static final int INTERVALO_SEGUNDOS = 60;

    public List<Paciente> generarPacientes(int cantidad) {
        List<Paciente> pacientes = new ArrayList<>();
        Random random = new Random();

        long tiempoBase = System.currentTimeMillis() / 1000;

        for (int i = 0; i < cantidad; i++) {
            String nombre = NOMBRES[random.nextInt(NOMBRES.length)];
            String apellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
            String id = String.format("ID_%04d", i + 1);

            int categoria = obtenerCategoria(random);
            String estado = "en_espera";
            String area = asignarAreaPorCategoria(categoria);

            long tiempoLlegada = tiempoBase + (i * INTERVALO_SEGUNDOS);

            Paciente paciente = new Paciente(nombre, apellido, id, categoria, tiempoLlegada, estado, area);
            pacientes.add(paciente);
        }

        return pacientes;
    }

    private int obtenerCategoria(Random random) {
        double valor = random.nextDouble();
        double suma = 0.0;

        for (int i = 0; i < PROBABILIDADES_CATEGORIA.length; i++) {
            suma += PROBABILIDADES_CATEGORIA[i];
            if (valor <= suma) {
                return i + 1;
            }
        }
        return 5;
    }

    private String asignarAreaPorCategoria(int categoria) {
        if (categoria == 1 || categoria == 2) return "urgencia_adulto";
        else if (categoria == 3 || categoria == 4) return "infantil";
        else return "SAPU";
    }

    public void mostrarPacientes(List<Paciente> pacientes) {
        for (Paciente paciente : pacientes) {
            System.out.println(paciente.getNombre() + " " + paciente.getApellido() +
                    " - ID: " + paciente.getId() +
                    " - Cat: " + paciente.getCategoria() +
                    " - Área: " + paciente.getArea() +
                    " - Llegada (s): " + paciente.getTiempoLlegada());
        }
    }

    public static void main(String[] args) {
        GeneradorPacientes generador = new GeneradorPacientes();
        List<Paciente> pacientes = generador.generarPacientes(10);
        generador.mostrarPacientes(pacientes);
    }
}
