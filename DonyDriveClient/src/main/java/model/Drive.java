package model;


public class Drive {
    
    private String letraUnidad;
    private String nombre;
    private String tipo;
    private String espacioTotalHR;
    private String espacioLibreHR;
    private long espacioTotal;
    private long espacioLibre;

    @Override
    public String toString() {
        return "Drive{" + "letraUnidad=" + letraUnidad + ", nombre=" + nombre + ", tipo=" + tipo + ", espacioTotalHR=" + espacioTotalHR + ", espacioLibreHR=" + espacioLibreHR + ", espacioTotal=" + espacioTotal + ", espacioLibre=" + espacioLibre + '}';
    }    

    public String getLetraUnidad() {
        return letraUnidad;
    }

    public void setLetraUnidad(String letraUnidad) {
        this.letraUnidad = letraUnidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEspacioTotalHR() {
        return espacioTotalHR;
    }

    public void setEspacioTotalHR(String espacioTotalHR) {
        this.espacioTotalHR = espacioTotalHR;
    }

    public String getEspacioLibreHR() {
        return espacioLibreHR;
    }

    public void setEspacioLibreHR(String espacioLibreHR) {
        this.espacioLibreHR = espacioLibreHR;
    }

    public long getEspacioTotal() {
        return espacioTotal;
    }

    public void setEspacioTotal(long espacioTotal) {
        this.espacioTotal = espacioTotal;
    }

    public long getEspacioLibre() {
        return espacioLibre;
    }

    public void setEspacioLibre(long espacioLibre) {
        this.espacioLibre = espacioLibre;
    }
    
    
    
}
