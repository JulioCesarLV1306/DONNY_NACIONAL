package com.ncpp.asistenteexpedientes.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.w3c.dom.*;
import javax.xml.parsers.*;

import com.ncpp.asistenteexpedientes.sij.entity.Deposito;

import java.io.*;
import java.util.ArrayList;

public class DepositoWS {

    private final String wsURL = "http://172.18.13.68/DepositoJudicialWS/services/DepositoJudicialServicioSOAP";
    private final String headerUsername = "SIJ_USER_DJ";
    private final String headerPassword = "68ad28c532b2b5c676f9c826d6e913ff10cd9393f6317887dcda9d40a51e5c35bc71c037d10162ce07f35e5067bb94e11b315ea1dd66a93a76c4bb5e3d5c502a";

    private final String segCodCliente = "PJ";
    private final String segCodAplicativo = "SIJ";
    private final String segCodRol = "ADM";
    private final String audPcIp = "192.168.1.1";
    private final String audMacAddressPc = "E4:54:E8:73:C8:06";
    private final String audPcName = "SECE_PC";
    private final String audUsuarioSis = "SECE";                              
    private final String audUsuarioRed = "SECE";
    private final String audNombreSO = "WINDOWS";
    private final String codDistrito = "14"; 
 
    public List<Deposito> buscarPorExpediente(String nUnico, int nIncidente) throws MalformedURLException, IOException {
        String response = getXMLResponse( nUnico, nIncidente);
        return xmlToObject(response);
    }

    private String getXMLResponse( String nUnico, int nIncidente) throws MalformedURLException, IOException {

        //Code to make a webservice HTTP request
        String responseString = "";
        String outputString = "";
        URL url = new URL(wsURL);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        String xmlInput = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
                + "xmlns:ws=\"http://ws.depositoJudicialWS.pj.gob.pe\"> <soapenv:Header>\n"
                + "<wsse:Security soapenv:mustUnderstand=\"1\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n"
                + "<wsse:UsernameToken wsu:Id=\"UsernameToken-1\"\n"
                + "xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">\n"
                + "<wsse:Username>" + headerUsername + "</wsse:Username>\n"
                + "<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">" + headerPassword + "</wsse:Password>\n"
                + "</wsse:UsernameToken> </wsse:Security> </soapenv:Header>\n"
                + "<soapenv:Body>\n"
                + "<ws:consultarDepositoExpediente>\n"
                + "<requestSeguridad>\n"
                + "<cod_cliente>" + segCodCliente + "</cod_cliente>\n"
                + "<cod_aplicativo>" + segCodAplicativo + "</cod_aplicativo>\n"
                + "<cod_rol>" + segCodRol + "</cod_rol>\n"
                + "</requestSeguridad>\n"
                + "<requestAuditoria>\n"
                + "<ipPc>" + audPcIp + "</ipPc>\n"
                + "<macAddressPc>" + audMacAddressPc + "</macAddressPc>\n"
                + "<pcName>" + audPcName + "</pcName>\n"
                + "<usuarioSis>" + audUsuarioSis + "</usuarioSis>\n"
                + "<usuarioRed>" + audUsuarioRed + "</usuarioRed>\n"
                + "<nombreSo>" + audNombreSO + "</nombreSo>\n"
                + "</requestAuditoria>\n"
                + "<requestConsultarDepositoExpediente>\n"
                + "<req_cDistrito>" + codDistrito + "</req_cDistrito>\n"
                + "<req_numUnico>" + nUnico + "</req_numUnico>\n"
                + "<req_numIncidente>" + nIncidente + "</req_numIncidente>\n"
                + "</requestConsultarDepositoExpediente>\n"
                + "</ws:consultarDepositoExpediente>\n"
                + "</soapenv:Body>\n"
                + "</soapenv:Envelope>";

        byte[] buffer = new byte[xmlInput.length()];
        buffer = xmlInput.getBytes();
        bout.write(buffer);
        byte[] b = bout.toByteArray();
        //String SOAPAction = "<SOAP action of the webservice to be consumed>";
        // Set the appropriate HTTP parameters.
        httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
        httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        //httpConn.setRequestProperty("SOAPAction", SOAPAction);
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        OutputStream out = httpConn.getOutputStream();
        //Write the content of the request to the outputstream of the HTTP Connection.
        out.write(b);
        out.close();
        //Ready with sending the request.

        //Read the response.
        InputStreamReader isr = null;
        if (httpConn.getResponseCode() == 200) {
            isr = new InputStreamReader(httpConn.getInputStream());
        } else {
            isr = new InputStreamReader(httpConn.getErrorStream());
        }

        BufferedReader in = new BufferedReader(isr);

        //Write the SOAP message response to a String.
        while ((responseString = in.readLine()) != null) {
            outputString = outputString + responseString;
        }

        return outputString;

    }

    private List<Deposito> xmlToObject(String xmlString) {
        List<Deposito> listDepositos = new ArrayList<>();
        try {

            DocumentBuilderFactory dbFactory  = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            
            StringBuilder xmlStringBuilder = new StringBuilder();
            xmlStringBuilder.append(xmlString);
            ByteArrayInputStream input = new ByteArrayInputStream(
               xmlStringBuilder.toString().getBytes("UTF-8"));
            Document doc = dBuilder.parse(input);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("res_datosDepositoJudicial");

            for (int i = 0; i < nList.getLength(); i++) {
                listDepositos.add(createDeposito((Element) nList.item(i))); 
            }

        } catch (Exception e) {
            listDepositos=null;
            System.out.println(e);
        }
        return listDepositos;
    }
    
    private Deposito createDeposito(Element element){
        
        Deposito deposito=new Deposito();
        try{
            deposito.setDepositante( (element.getElementsByTagName("res_apellidoPaterno").item(0).getTextContent() + " "
            + element.getElementsByTagName("res_apellidoMaterno").item(0).getTextContent() +" "
            + element.getElementsByTagName("res_nombre").item(0).getTextContent()).trim());
            deposito.setMonto(Float.parseFloat(element.getElementsByTagName("res_monto").item(0).getTextContent()));
            deposito.setNumDeposito(element.getElementsByTagName("res_numDepositoJudicial").item(0).getTextContent());
            String resEstado=element.getElementsByTagName("res_estado").item(0).getTextContent();
            switch(resEstado){
                case "C":
                    deposito.setEstado("COBRADO");
                    break;
                case "D":
                    deposito.setEstado("BN");
                    break;
                case "P":
                    deposito.setEstado("CDG");
                    break;
                default:
                    deposito.setEstado(resEstado); 
                    break;
            }
            String resMoneda = element.getElementsByTagName("res_moneda").item(0).getTextContent();
             switch(resMoneda){
                case "01":
                    deposito.setMoneda("S/.");
                    break;
                default:
                    deposito.setMoneda("$"); 
                    break;
            }
        }catch(Exception e){
            deposito=null;
            System.out.println(e);
        }
        
        return deposito;
    }

}
