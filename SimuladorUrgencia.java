import java.util.*;

public class SimuladorUrgencia {
    private Hospital hospital;
    private GeneradorPacientes generador;
    private List<Paciente> historialAtencion;
    private Map<Integer, Integer> pacientesPorCategoria;
    private Map<Integer, Long> acumuladoTiempoEspera;
    private Map<Integer, Long> peorTiempoEsperaPorCategoria; // NUEVO
    private List<Paciente> pacientesExcedidos;

    private final Map<Integer, Integer> tiempoMaximoPorCategoria = Map.of(
            1, 600, 2, 1200, 3, 1800, 4, 2400, 5, 3000
    );

    public SimuladorUrgencia() {
        List<AreaAtencion> areas = List.of(
                new AreaAtencion("Urgencia Adultos", 25),
                new AreaAtencion("Urgencia Infantil", 15),
                new AreaAtencion("SAPU", 10)
        );
        this.hospital = new Hospital(areas);
        this.generador = new GeneradorPacientes();
        this.historialAtencion = new ArrayList<>();
        this.pacientesPorCategoria = new HashMap<>();
        this.acumuladoTiempoEspera = new HashMap<>();
        this.peorTiempoEsperaPorCategoria = new HashMap<>(); // NUEVO
        this.pacientesExcedidos = new ArrayList<>();
    }

    public void simular(int pacientesPorDia) {
        List<Paciente> pacientes = generador.generarPacientes(pacientesPorDia);
        for (Paciente p : pacientes) {
            hospital.registrarPaciente(p);
        }

        int nuevosDesdeUltimaAtencionInmediata = 0;
        int minutoActual = 0;
        int totalMinutos = 24 * 60;
        long tiempoBase = System.currentTimeMillis() / 1000;

        while (minutoActual < totalMinutos) {
            long tiempoSimulado = tiempoBase + minutoActual * 60;

            if (minutoActual % 10 == 0 && !pacientes.isEmpty()) {
                Paciente p = pacientes.remove(0);
                p.setTiempoLlegada(tiempoSimulado);
                hospital.registrarPaciente(p);
                nuevosDesdeUltimaAtencionInmediata++;
            }

            if (minutoActual % 15 == 0) {
                atenderPaciente(tiempoSimulado);
            }

            if (nuevosDesdeUltimaAtencionInmediata >= 3) {
                atenderPaciente(tiempoSimulado);
                atenderPaciente(tiempoSimulado);
                nuevosDesdeUltimaAtencionInmediata = 0;
            }

            minutoActual++;
        }

        mostrarEstadisticas();
    }

    private void atenderPaciente(long tiempoActual) {
        Paciente paciente = hospital.atenderSiguiente();
        if (paciente != null) {
            long espera = tiempoActual - paciente.getTiempoLlegada();
            paciente.setEstado("atendido");

            historialAtencion.add(paciente);
            pacientesPorCategoria.merge(paciente.getCategoria(), 1, Integer::sum);
            acumuladoTiempoEspera.merge(paciente.getCategoria(), espera, Long::sum);

            peorTiempoEsperaPorCategoria.merge(paciente.getCategoria(), espera, Math::max);

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
            long peor = peorTiempoEsperaPorCategoria.getOrDefault(cat, 0L);

            System.out.printf("Categoría %d: Atendidos %d | Promedio espera: %.2f s | Peor espera: %d s\n",
                    cat, cantidad, promedio, peor);
        }

        System.out.println("\nPacientes que excedieron tiempo máximo:");
        for (Paciente p : pacientesExcedidos) {
            System.out.printf("  - %s %s (Categoría %d)\n", p.getNombre(), p.getApellido(), p.getCategoria());
        }
    }
}
