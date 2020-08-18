/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symboliclinkcreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;
import java.nio.file.Files;
import java.util.Scanner;

/**
 *
 * @author Andrea
 */
public class SymbolicLinkCreator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        
        Scanner in = new Scanner(System.in);
        
        if(!AdministratorChecker.IS_RUNNING_AS_ADMINISTRATOR){
            System.out.println("\nIl programma per funzionare deve essere avviato con i permessi di amministratore\nPremi invio per chiudere il programma");
            in.nextLine();
            return;
        }
        
        setLookAndFeel();
        
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Scegli i file da spostare...");
        fc.setMultiSelectionEnabled(true);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == 0) {
            File[] filesScelti = fc.getSelectedFiles();
            System.out.println("\nFiles scelti:");
            for(File f : filesScelti){
                System.out.println(f.getAbsolutePath());
            }
            
            JFileChooser fc2 = new JFileChooser();
            fc2.setDialogTitle("Scegli la cartella in cui spostare i file scelti...");
            fc2.setMultiSelectionEnabled(false);
            fc2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc2.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
            int returnVal2 = fc2.showOpenDialog(null);
            if (returnVal2 == 0) {
                File cartellaScelta = fc2.getSelectedFile();
                System.out.println("\nCartella destinazione scelta:\n" + cartellaScelta.getAbsolutePath() + "\n");
                
                
                //COPY INTO NEW FOLDER
                System.out.println("Spostamento nella nuova cartella in corso...");
                for(File f : filesScelti){
                    System.out.print(f.getAbsolutePath() + "...");
                    moveFile(f, new File(cartellaScelta.getAbsolutePath()+"\\"+f.getName()));
                    System.out.println(" OK");
                }
                
                
                //DELETE OLD FILES
                /*System.out.println("\nCancellazione vecchi file in corso...");
                for(File f : filesScelti){
                    System.out.print(f.getAbsolutePath() + "...");
                    f.delete();
                    System.out.println(" OK");
                }*/
                
                //LINK CREATION
                System.out.println("\nCreazione link in corso...");
                for(File f : filesScelti){
                    createLink(f.getAbsolutePath(), cartellaScelta.getAbsolutePath()+"\\"+f.getName());
                }
                
                System.out.println("\n\nI collegamenti sono stati creati con successo\nPremi invio per chiudere il programma");
                in.nextLine();
            }
        }
        
    }
    
    public static void moveFile(File source, File dest) throws IOException {
        Files.move(source.toPath(), dest.toPath());
    }
    
    public static void createLink(String target, String realFile) throws Exception{
        String command = "cmd /c mklink \"" + target.replace("\\", "\\\\") + "\" \"" + realFile.replace("\\", "\\\\") + "\"";
        
        Process p;
        p = Runtime.getRuntime().exec(command);

        p.waitFor(); 
        BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
        String line; 
        while((line = reader.readLine()) != null) { 
            System.out.println(line);
        }
    }
    
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {}
    }
}
