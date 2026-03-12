package controller;

import service.DownloaderService;
import static spark.Spark.*;
import com.google.gson.Gson;
import model.Descarga;

public class DownloaderController {

    DownloaderService downloaderService;

    public DownloaderController() {
        downloaderService = new DownloaderService();
    }

    public void activarServer() {
        put_copiar();
        put_consultar();
    }

    private void put_copiar() {
        put("/downloader/copiar", (req, res) -> {
            String nUnico = req.queryParams("nUnico");
            int nIncidente = Integer.parseInt(req.queryParams("nIncidente"));
            String eleccion = req.queryParams("eleccion");
            String dniPersona = req.queryParams("dniPersona");
            String moduloIP = req.queryParams("moduloIP");
            String driveLetra = req.queryParams("driveLetra");
            String[] fechas = req.queryParams("fechas").split(",");

            String nombrePersona = req.queryParams("nombrePersona");
            String usuarioModulo = req.queryParams("usuarioModulo");
            String expediente = req.queryParams("expediente");

            Descarga response = downloaderService.copiar(nUnico, nIncidente, fechas, eleccion, driveLetra, moduloIP, dniPersona, nombrePersona, usuarioModulo, expediente);
            return new Gson().toJson(response);
        });
    }

    private void put_consultar() {
        put("/downloader/consultar", (req, res) -> {
            String moduloIP = req.queryParams("moduloIP");
            String dniPersona = req.queryParams("dniPersona");
            String nUnico = req.queryParams("nUnico");
            int nIncidente = Integer.parseInt(req.queryParams("nIncidente"));
            String eleccion = req.queryParams("eleccion");
            String[] fechas = req.queryParams("fechas").split(",");
            Descarga response = downloaderService.consultar(moduloIP, dniPersona, nUnico, nIncidente, eleccion, fechas);
            return new Gson().toJson(response);
        });
    }
}
