package service;

import java.io.BufferedReader;
import java.util.List;
import model.Drive;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.filechooser.FileSystemView;
import util.Constants;
import util.Memory;

import util.Util;

public class DriveService {

    public List<Drive> getListaDiscos() {
        List<Drive> lista = new ArrayList<>();
        FileSystemView fsv = FileSystemView.getFileSystemView();
        File[] drives = File.listRoots();
        String discosExcluidos = "";
        if (drives != null && drives.length > 0) {
            for (File aDrive : drives) {
                if (fsv.getSystemTypeDescription(aDrive).contains("Disco")) {
                    Drive drive = new Drive();
                    drive.setLetraUnidad(String.valueOf(aDrive));
                    drive.setTipo(fsv.getSystemTypeDescription(aDrive));
                    drive.setNombre(fsv.getSystemDisplayName(aDrive));
                    drive.setEspacioLibreHR(Util.bytesToHumanReadable(aDrive.getFreeSpace()));
                    drive.setEspacioTotalHR(Util.bytesToHumanReadable(aDrive.getTotalSpace()));
                    drive.setEspacioLibre(aDrive.getFreeSpace());
                    drive.setEspacioTotal(aDrive.getTotalSpace());
                    lista.add(drive);
                    discosExcluidos += drive.getLetraUnidad().substring(0, 1) + " ";
                }
            }
        }
        Memory.put(Constants.KEY_DISCOS_EXCLU, discosExcluidos);
        return lista;
    }

    public boolean expulsarUsb(String letter) {
        
        try {
            String command = "powershell \"$driveEject = New-Object -comObject Shell.Application; $driveEject.Namespace(17).ParseName(\\\"" 
                    + letter + ":\\\").InvokeVerb(\\\"Eject\\\"); start-sleep -s 3\"";
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("cmd","/c",command);
            Process p;
            p=pb.start();
            p.waitFor();
            System.out.println("p.exitValue! " + p.exitValue());
            
            return true;
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            return false;
        }
    }

    public Drive verificarUsb() {
        Drive drive = null;
        try {
            FileSystemView fsv = FileSystemView.getFileSystemView();
            File[] drives = File.listRoots();
            if (drives != null && drives.length > 0) {
                for (File aDrive : drives) {
                    if (fsv.getSystemTypeDescription(aDrive).contains("USB")) {
                        drive = new Drive();
                        drive.setLetraUnidad(String.valueOf(aDrive));
                        drive.setTipo(fsv.getSystemTypeDescription(aDrive));
                        drive.setNombre(fsv.getSystemDisplayName(aDrive));
                        drive.setEspacioLibreHR(Util.bytesToHumanReadable(aDrive.getFreeSpace()));
                        drive.setEspacioTotalHR(Util.bytesToHumanReadable(aDrive.getTotalSpace()));
                        drive.setEspacioLibre(aDrive.getFreeSpace());
                        drive.setEspacioTotal(aDrive.getTotalSpace());
                        break;
                    }
                }
                if (drive == null) {
                    String discosExcluidos = (String) Memory.get(Constants.KEY_DISCOS_EXCLU);
                    for (File aDrive : drives) {
                        String type = fsv.getSystemTypeDescription(aDrive);
                        if (type.contains("Disco") && !discosExcluidos.contains(String.valueOf(aDrive).substring(0, 1))) {
                            drive = new Drive();
                            drive.setLetraUnidad(String.valueOf(aDrive));
                            drive.setTipo(fsv.getSystemTypeDescription(aDrive));
                            drive.setNombre(fsv.getSystemDisplayName(aDrive));
                            drive.setEspacioLibreHR(Util.bytesToHumanReadable(aDrive.getFreeSpace()));
                            drive.setEspacioTotalHR(Util.bytesToHumanReadable(aDrive.getTotalSpace()));
                            drive.setEspacioLibre(aDrive.getFreeSpace());
                            drive.setEspacioTotal(aDrive.getTotalSpace());
                            break;
                        }
                    }

                }
            }

        } catch (Exception e) {
            drive = null;
            System.out.println(e);
        }
        return drive;
    }

    private void executeCommand(Process powerShellProcess) {
        try {
            powerShellProcess.getOutputStream().close();
            String line;
            System.out.println("Standard Output:");
            BufferedReader stdout = new BufferedReader(new InputStreamReader(
                    powerShellProcess.getInputStream()));
            while ((line = stdout.readLine()) != null) {
                System.out.println(line);
            }
            stdout.close();
            System.out.println("Standard Error:");
            BufferedReader stderr = new BufferedReader(new InputStreamReader(
                    powerShellProcess.getErrorStream()));
            while ((line = stderr.readLine()) != null) {
                System.out.println(line);
            }
            stderr.close();
            System.out.println("Done");
        } catch (Exception e) {
            System.out.println(e);
        }

    }

}
