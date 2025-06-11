import  java.util.*;
public class Paciente {
    private String nombre;
    private String apellido;
    private String id;
    private int categoria;
    private long tiempoLlegada;
    private long tiempoAtencion;
    private String estado;
    private String area;
    private Stack<String> historialCambios;

    public Paciente(String nombre, String apellido, String id, int categoria, long tiempoLlegada, String estado, String area) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.id = id;
        this.categoria = categoria;
        this.tiempoLlegada = System.currentTimeMillis() / 1000;
        this.estado = estado;
        this.area = area;
        this.historialCambios = new Stack<>();

    }
    public String getNombre() {
        return nombre;}


    public String getApellido() {
        return apellido;}


    public String getId() {
        return id;}


    public int getCategoria() {
        return categoria;}


    public long getTiempoLlegada() {
        return tiempoLlegada;}
    public long getTiempoAtencion() {
        return tiempoAtencion;
    }

    public String getEstado() {
        return estado;}


    public String getArea() {
        return area;}

    public Stack<String> getHistorialCambios() {
        return historialCambios;}
    public void setCategoria(int nuevaCategoria) {
        this.categoria = nuevaCategoria;
    }

    public void setEstado(String estado) { this.estado = estado; }

    public void setTiempoLlegada(long tiempoLlegada) { this.tiempoLlegada = tiempoLlegada; }
    public void setTiempoAtencion(long tiempoAtencion) { this.tiempoAtencion = tiempoAtencion; }

    public long tiempoEsperaActual(){
        long tiempoActual = System.currentTimeMillis() / 1000;
        long diferencia = tiempoActual - this.tiempoLlegada;
        return diferencia / 60;
    }
    public void registrarCambio(String descripcion){
        historialCambios.push(descripcion);

    }
    public String obtenerUltimoCambio(){
        if(historialCambios.isEmpty()) {
            return "No hay cambios";
        }else{
            return historialCambios.pop();
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paciente)) return false;
        Paciente p = (Paciente) o;
        return id.equals(p.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


}

