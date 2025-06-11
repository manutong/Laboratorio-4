import java.time.LocalTime;
import java.util.*;

public class SimuladorUrgencia {
    private Hospital hospital;
    private GeneradorPacientes generador;
    private List<Paciente> historialAtencion;
    private Map<Integer, Integer> pacientesPorCategoria;
    private Map<Integer, Long> acumuladoTiempoEspera;
    private List<Paciente> pacientesExcedidos;
    private Queue<Paciente> salaEspera = new LinkedList<>();

    private final Map<Integer, Integer> tiempoMaximoPorCategoria = Map.of(
            1, 600, 2, 1200, 3, 1800, 4, 2400, 5, 3000
    );

    public SimuladorUrgencia() {
        List<AreaAtencion> areas = List.of(
                new AreaAtencion("Urgencia Adultos", 25),
                new AreaAtencion("Urgencia Infantil", 20),
                new AreaAtencion("SAPU", 30)
        );
        this.hospital = new Hospital(areas);
        this.generador = new GeneradorPacientes();
        this.historialAtencion = new ArrayList<>();
        this.pacientesPorCategoria = new HashMap<>();
        this.acumuladoTiempoEspera = new HashMap<>();
        this.pacientesExcedidos = new ArrayList<>();
    }

    public void simular(int pacientesPorDia) {
        List<Paciente> pacientes = generador.generarPacientes(pacientesPorDia);

        int minutoActual = 0;
        int totalMinutos = 24 * 60;
        long tiempoBase = System.currentTimeMillis() / 1000;

        while (minutoActual < totalMinutos) {
            long tiempoSimulado = tiempoBase + minutoActual * 60;

            // Llegada de paciente cada 10 minutos
            if (minutoActual % 10 == 0 && !pacientes.isEmpty()) {
                Paciente p = pacientes.remove(0);

                p.setEstado("en_espera");
                p.setTiempoLlegada(tiempoSimulado);
                salaEspera.add(p); // se agrega a la sala de espera

                p.registrarCambio("Paciente llegó y está en espera a las " + LocalTime.ofSecondOfDay(tiempoSimulado % 86400));
            }

            // Atender un paciente cada minuto si hay pacientes esperando
            if (!salaEspera.isEmpty()) {
                atenderPaciente(tiempoSimulado);
            }

            minutoActual++;
        }

        mostrarEstadisticas();
    }

    private void atenderPaciente(long tiempoActual) {
        Paciente paciente = salaEspera.poll(); // se atiende al primero de la cola

        if (paciente != null) {
            Random rand = new Random();
            int carga = salaEspera.size();
            long esperaBase = 60;
            long esperaExtra = rand.nextInt(carga * 60 + 1);
            long esperaTotal = esperaBase + esperaExtra;

            long tiempoAtencion = paciente.getTiempoLlegada() + esperaTotal;
            paciente.setTiempoAtencion(tiempoAtencion);

            paciente.setEstado("en_atencion");
            paciente.registrarCambio("Paciente comenzó atención a las " + LocalTime.ofSecondOfDay(tiempoActual % 86400));

            // Calculamos la espera en segundos
            long espera = tiempoAtencion - paciente.getTiempoLlegada();

            paciente.setEstado("atendido");
            paciente.registrarCambio("Paciente fue atendido a las " + LocalTime.ofSecondOfDay(tiempoAtencion % 86400));

            historialAtencion.add(paciente);
            pacientesPorCategoria.merge(paciente.getCategoria(), 1, Integer::sum);
            acumuladoTiempoEspera.merge(paciente.getCategoria(), espera, Long::sum);

            System.out.println("Nombre: " + paciente.getNombre() + " " + paciente.getApellido() +
                    " | Categoría: " + paciente.getCategoria() +
                    " | Hora llegada: " + LocalTime.ofSecondOfDay(paciente.getTiempoLlegada() % 86400) +
                    " | Hora atención: " + LocalTime.ofSecondOfDay(paciente.getTiempoAtencion() % 86400) +
                    " | Tiempo de espera: " + espera + " segundos");

            if (espera > tiempoMaximoPorCategoria.get(paciente.getCategoria())) {
                pacientesExcedidos.add(paciente);
            }
        }
    }

    private void mostrarEstadisticas() {
        System.out.println("\n--- Estadísticas de la jornada ---");
        for (int cat = 1; cat <= 5; cat++) {
            int cantidad = pacientesPorCategoria.getOrDefault(cat, 0);
            long esperaTotal = acumuladoTiempoEspera.getOrDefault(cat, 0L);
            double promedio = cantidad == 0 ? 0 : (double) esperaTotal / cantidad;
            System.out.printf("Categoría %d: Atendidos %d | Promedio espera: %.2f s\n", cat, cantidad, promedio);
        }

        System.out.println("\nPacientes que excedieron tiempo máximo:");
        for (Paciente p : pacientesExcedidos) {
            System.out.printf("  - %s %s (Categoria %d)\n", p.getNombre(), p.getApellido(), p.getCategoria());
        }
    }
}
