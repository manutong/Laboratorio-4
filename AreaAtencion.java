
import java.util.*;
import java.io.*;
public class AreaAtencion {
    private String nombreArea;
    private PriorityQueue<Paciente> pacientesHeap;
    private int capacidadMaxima;


    public AreaAtencion(String nombreArea, int capacidadMaxima) {

        this.nombreArea = nombreArea;
        this.capacidadMaxima = capacidadMaxima;
        this.pacientesHeap = new PriorityQueue<>(new Comparator<Paciente>() {
            @Override
            public int compare(Paciente o1, Paciente o2) {
                if (o1.getCategoria() != o2.getCategoria()) {
                    return Integer.compare(o1.getCategoria(), o2.getCategoria());
                }
                return Long.compare(o1.getTiempoLlegada(), o2.getTiempoLlegada());

            }
        });
    }

    public boolean estaSaturada() {
        return pacientesHeap.size() >= capacidadMaxima;
    }

    public void ingresarPaciente(Paciente p) {
        if (estaSaturada() == false) {
            pacientesHeap.add(p);
        } else {
            System.out.println("El área ya esta saturada");
        }
    }

    public Paciente atenderPaciente() {
        return pacientesHeap.poll();
    }

    public List<Paciente> obtenerPacientesPorHeapSort() {
        PriorityQueue<Paciente> copia = new PriorityQueue<>(pacientesHeap);
        List<Paciente> listaOrdenada = new ArrayList<>();
        while (!copia.isEmpty()) {
            listaOrdenada.add(copia.poll());
        }
        return listaOrdenada;
    }

    public int getCantidadPacientes() {
        return pacientesHeap.size();
    }

    public String getNombreArea() {
        return nombreArea;
    }

    public void setPacientesHeap(PriorityQueue<Paciente> pacientesHeap) {
        this.pacientesHeap = pacientesHeap;
    }

}