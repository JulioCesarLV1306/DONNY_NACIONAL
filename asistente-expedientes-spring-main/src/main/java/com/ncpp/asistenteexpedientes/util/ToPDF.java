package com.ncpp.asistenteexpedientes.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.ncpp.asistenteexpedientes.sij.payload.request.RequestResponseBase64;

import org.apache.commons.io.IOUtils;
import java.util.Base64;


public class ToPDF {
    
   public static RequestResponseBase64 convertDocToPDF(RequestResponseBase64 requestB64){
        HttpURLConnection connection = null;
        RequestResponseBase64 response=null;
        if(!validarArchivo()){
            throw new RuntimeException("ERROR: ARCHIVO NO ENCONTRADO "+getRutaArchivo());
        }

        try {
            String url_str = Files.readString(Paths.get(getRutaArchivo()));
            //Create connection
            URL url = new URL(url_str);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String input = "{ \"extension\": \""+requestB64.getExtension()+"\",\"data\":\""+requestB64.getData()+"\"}";

            OutputStream os = connection.getOutputStream();
            os.write(input.getBytes());
            os.flush();

          
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));
 
            String output;
            String JSONResponse="";
            while ((output = br.readLine()) != null) {
                JSONResponse+=output;
            }

            connection.disconnect();

            response= jsonToModel(JSONResponse);
        
          } catch (MalformedURLException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
     
         }
        return response;
    }

    public static String inputStreamToBase64(InputStream inputStream) {
        String b64="";
            try {
                byte[] data=null;
                data = IOUtils.toByteArray(inputStream);

                b64= Base64.getEncoder().encodeToString(data);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //Base64Encoder encoder=new Base64Encoder();
        return b64;

    }

    public static InputStream base64ToInputStream(String base64){
        byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(base64);
        InputStream is = new ByteArrayInputStream(decodedBytes);
        return is;
    }

    private static RequestResponseBase64 jsonToModel(String json){
        RequestResponseBase64 model = new Gson().fromJson(json, RequestResponseBase64.class);
        return model;
    }

    private static boolean validarArchivo(){
        File f = new File(getRutaArchivo());
        return f.exists() && !f.isDirectory();
    }

    private static String getRutaArchivo(){
        //return Constants.RUTA_ARCHIVOS_CONF+"/"+Constants.NOMBRE_ARCHIVO_TO_PDF;
        return "";
    }

}
