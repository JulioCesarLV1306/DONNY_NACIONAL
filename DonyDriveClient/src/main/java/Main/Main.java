package Main;

import UI.LoadingUI;
import UI.MasterUI;
import controller.DownloaderController;
import service.ModuloService;
import controller.DriveController;
import controller.VideoFileController;
import model.Bitacora;
import model.Modulo;
import model.Usuario;
import service.BitacoraService;
import service.UsuarioService;
import static spark.Spark.*;
import util.CorsFilter;
import util.Mensajes;
import util.Util;

public class Main {

    public static void main(String[] args) {
        try {
            LoadingUI loadUI = new LoadingUI();
            loadUI.setVisible(true);
            setInfo("Obteniendo IP...");
            ModuloService moduloService = new ModuloService();
            String localIp = Util.getLocalIp();
            setInfo("Buscando IP en la base de datos...");
            Modulo modulo = moduloService.findModuloByIp(localIp);
             BitacoraService bitacoraService = new BitacoraService();
            if (modulo != null) {
                setInfo("Ejecutando servicios...");
                
                port(4568);
                
                 VideoFileController videoFileController = new VideoFileController();
                videoFileController.activarServer();
                
                DriveController driveController = new DriveController();
               
                CorsFilter.apply();
                driveController.activarServer();
                
                DownloaderController downloaderController = new DownloaderController();
                downloaderController.activarServer();
                
               
                setInfo("Registrando ingreso...");
                
                // Crear/obtener usuario del módulo
                UsuarioService usuarioService = new UsuarioService();
                String dniModulo = "MODULO_" + modulo.getCPcUsuario();
                String nombreModulo = "Módulo " + modulo.getXDescripcion();
                Usuario usuario = usuarioService.createIfNotExists(dniModulo, nombreModulo);
                
                Bitacora bitacora = new Bitacora();
                bitacora.setCIpModulo(modulo.getCPcIp());
                bitacora.setNIdUsuario(usuario.getNIdUsuario());
                bitacora.setCCodigoAccion("LOGIN_MODULO");
                bitacora.setTDescripcionAccion("EL MODULO " + modulo.getCPcUsuario() + " (" + localIp + ") INICIALIZO CON EXITO EL CLIENTE");
                bitacora.setCAudUid(dniModulo);
                bitacoraService.create(bitacora);
                setInfo("Cargando interfaz...");
                
                new MasterUI(modulo).setVisible(true);
                loadUI.setVisible(false);
            } else {
                // Caso de fallo: IP no autorizada
                UsuarioService usuarioService = new UsuarioService();
                String dniSistema = "NO_IDENTIFICADO";
                String nombreSistema = "Sistema No Identificado";
                Usuario usuario = usuarioService.createIfNotExists(dniSistema, nombreSistema);
                
                Bitacora bitacora = new Bitacora();
                bitacora.setCIpModulo(localIp);
                bitacora.setNIdUsuario(usuario.getNIdUsuario());
                bitacora.setCCodigoAccion("FAIL_LOGIN_MODULO");
                bitacora.setTDescripcionAccion("EL PC CON IP " + localIp + " INTENTO INICIALIZAR SIN EXITO EL CLIENTE");
                bitacora.setCAudUid(dniSistema);
                bitacoraService.create(bitacora);
                Mensajes.errorMensaje("Esta IP " + localIp + " no esta permitida. Contáctese con el administrador");
                System.exit(0);
            }

        } catch (Exception e) {
            
            System.out.println("Error: " + e);
            Mensajes.errorMensaje("ERROR: " + e);
            System.exit(0);
        }
    }
    
    private static void setInfo(String info){
        LoadingUI.txt_inf.setText(info);
    }

}
