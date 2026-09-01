public class Lector {
    private int id;
    private String nombre;
    private String apellido;
    private int telefono;
    

    public Lector(int id, String nombre, String apellido, int telefono) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return id + "," + nombre + "," + apellido + "," + telefono;
    }
}