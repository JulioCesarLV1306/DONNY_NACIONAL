
package util;

import javax.swing.JOptionPane;

public class Mensajes {
    
    public static final int ERROR_CAMPOVACIO = 2;
    public static final int ERROR_USUARIO_INVALIDO =3;
    public static final int SUCESS_TODO = 8;
    public static final int ERROR_GENERAL = 9;
    

    private Mensajes() {

    }

    public static void mostrarMensaje(int tipo) {
        switch (tipo) {
            case ERROR_CAMPOVACIO:
                advertenciaMensaje("El campo no debe estar vacio");
                break;
            case ERROR_USUARIO_INVALIDO:
                errorMensaje("Esta máquina no esta permitida, contáctese con el administrador");
                break;
            case SUCESS_TODO:
                validoMensaje("Todo se cargo correctamente");
                break;
            case ERROR_GENERAL:
                errorMensaje("Ocurrió un error!");
                break;
                    
                    
        }
    }
    
    
    
    public static void advertenciaMensaje(String  mensaje){
        JOptionPane.showMessageDialog(null, mensaje,"Advertencia!",JOptionPane.WARNING_MESSAGE);
    }
    
    public static void errorMensaje(String mensaje){
         JOptionPane.showMessageDialog(null, mensaje,"Error!",JOptionPane.ERROR_MESSAGE);
    }
    
    public static void validoMensaje(String mensaje){
        JOptionPane.showMessageDialog(null, mensaje);
    }
    
}
