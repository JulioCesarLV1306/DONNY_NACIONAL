package controller;

import UI.MasterUI;
import service.DriveService;
import com.google.gson.Gson;
import model.Drive;
import model.Modulo;
import static spark.Spark.*;

public class DriveController {
      DriveService driveService;
    public DriveController(){
         driveService = new  DriveService();
    }

    public  void activarServer() {
        put_verificarUsb();
        get_verificarCliente();
        put_expulsarUsb();
    }

    private void put_verificarUsb() {

        put("/drive/verificar-usb", (req, res) -> {
            MasterUI.aumentarContadorPeticiones();
            Drive drive = driveService.verificarUsb();
            return drive != null ? new Gson().toJson(drive) : drive;
        });

    }
    
    private void get_verificarCliente(){
        
        get("/drive/verificar-cliente", (req, res) -> {
            Modulo  modulo= MasterUI.conectarModo();
            System.out.println(new Gson().toJson(modulo));
            return new Gson().toJson(modulo);
        });
    }
    
    private void put_expulsarUsb(){
         put("/drive/expulsar-usb", (req, res) -> {
             String letter= req.queryParams("letra");  
            boolean response = driveService.expulsarUsb(letter);
            return response;
        });
    }

}
