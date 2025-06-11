import java.util.*;

public class Hospital {
    Map<String, Paciente> pacientesTotales;
    PriorityQueue<Paciente> colaAtencion;
    Map<String, AreaAtencion> areasAtencion;
    List<Paciente> pacientesAtendidos;
    public Hospital(List<AreaAtencion> areas) {
        pacientesTotales = new HashMap<>();
        pacientesAtendidos = new ArrayList<>();
        areasAtencion = new HashMap<>();
        for (AreaAtencion area : areas) {
            areasAtencion.put(area.getNombreArea(), area);
        }
    colaAtencion = new PriorityQueue<>(new Comparator<Paciente>() {
        @Override
        public int compare(Paciente o1, Paciente o2) {
            if (o1.getCategoria() != o2.getCategoria()) {
                return Integer.compare(o1.getCategoria(), o2.getCategoria());
            }
        return Long.compare(o1.getTiempoLlegada(), o2.getTiempoLlegada());
        }
    });
    }
public void registrarPaciente(Paciente p) {
        pacientesTotales.put(p.getId(), p);
        colaAtencion.add(p);

    AreaAtencion area = asignarArea(p.getCategoria());
    if (area != null) {
        p.registrarCambio("Asignado a: " + area.getNombreArea());
        area.ingresarPaciente(p);
    }
    }
private AreaAtencion asignarArea(int categoria) {
        if (categoria == 1 || categoria == 2) {
            return areasAtencion.get("Urgencia Adultos");
        }else if (categoria == 3 || categoria == 4) {
            return areasAtencion.get("Urgencia Infantil");
        }else {
            return areasAtencion.get("SAPU");

        }
}
public void reasignarCategoria(String id, int nuevaCategoria){
        Paciente paciente = pacientesTotales.get(id);
        if(paciente != null) {
        paciente.registrarCambio("Cambio de categoria de: " + paciente.getCategoria() + " a " + nuevaCategoria );
        paciente = actualizarCategoriaPaciente(paciente, nuevaCategoria);
        colaAtencion.remove(paciente);
        colaAtencion.add(paciente);
        for(AreaAtencion area : areasAtencion.values()){
            if(area.obtenerPacientesPorHeapSort().contains(paciente)){
                areaRemoverPaciente(area,paciente);
            }
        }
    AreaAtencion nuevaArea = asignarArea(nuevaCategoria);
        if(nuevaArea != null){
    nuevaArea.ingresarPaciente(paciente);
    paciente.registrarCambio("Reasisgnado al area " + nuevaArea.getNombreArea());
        }


}
}
private Paciente actualizarCategoriaPaciente(Paciente p, int nuevaCategoria){
        p.setCategoria(nuevaCategoria);
        return p;

}
    private void areaRemoverPaciente(AreaAtencion area, Paciente p) {
        PriorityQueue<Paciente> nuevaCola = new PriorityQueue<>(new Comparator<Paciente>() {
            @Override
            public int compare(Paciente o1, Paciente o2) {
                if (o1.getCategoria() != o2.getCategoria()) {
                    return Integer.compare(o1.getCategoria(), o2.getCategoria());
                }
                return Long.compare(o1.getTiempoLlegada(), o2.getTiempoLlegada());
            }
        });
        for (Paciente paciente : area.obtenerPacientesPorHeapSort()) {
            if (!paciente.getId().equals(p.getId())) {
                nuevaCola.offer(paciente);
            }
        }
        area.setPacientesHeap(nuevaCola);
    }
    public Paciente atenderSiguiente() {
        Paciente siguiente = colaAtencion.poll();
        if(siguiente != null){
            pacientesAtendidos.add(siguiente);
            for(AreaAtencion area : areasAtencion.values()){
                areaRemoverPaciente(area, siguiente);

            }
        siguiente.registrarCambio("Paciente atendido");
        }
    return siguiente;
}
public List<Paciente> obtenerPacientesPorCategoria(int categoria) {
        List<Paciente> resultado = new ArrayList<>();
        for(Paciente p  : colaAtencion){
            if(p.getCategoria() == categoria){
                resultado.add(p);
            }

        }
return resultado;

    }
public boolean hayPacientesEnCola() {
        return !colaAtencion.isEmpty();
    }

    public AreaAtencion obtenerArea(String nombre) {
        return  areasAtencion.get(nombre);
}
public PriorityQueue<Paciente>  getColaAtencion() {
        return colaAtencion;
}
public Map<String, AreaAtencion> getAreasAtencion() {
        return areasAtencion;
}
public List<Paciente> getPacientesAtendidos() {
        return pacientesAtendidos;
}

public Map<String, AreaAtencion> getAreas() { return areasAtencion; }
}
