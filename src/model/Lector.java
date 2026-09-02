package model;

public class Lector {

    private int idLector;
    private String nombre;
    private String apellido;
    private String telefono;

    public Lector(int idLector, String nombre, String apellido, String telefono) {
        this.idLector = idLector;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
    }

    public int getIdLector() {
        return idLector;
    }

    public void setIdLector(int idLector) {
        this.idLector = idLector;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // Genera la línea en formato CSV: id,nombre,apellido,telefono
    @Override
    public String toString() {
        return idLector + "," + nombre + "," + apellido + "," + telefono;
    }
}
