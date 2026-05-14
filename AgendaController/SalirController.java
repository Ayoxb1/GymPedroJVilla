/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.aplicacionjavafx2526;

import com.mycompany.aplicacionjavafx2526.clases.Persona;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import modulos.Ficheros;
import modulos.FuncionesGraficas;

/**
 * FXML Controller class
 *
 * @author juanfco
 */
public class SalirController implements Initializable {

    @FXML
    private Button btnSi;
    @FXML
    private Button btnNo;
    
    private static ArrayList<Persona> personas;
    private static File fichero;
    private static String usuario;
    @FXML
    private Label labelUsuario;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
       labelUsuario.setText(usuario);
    }    

    @FXML
    private void salirApp(ActionEvent event) {
      grabarDatos(personas, fichero);
      System.exit(0);     
    }

    public static void datosEntrada(ArrayList<Persona> p, 
                                    File f,String us)
    {
     personas=p;
     fichero=f;
     usuario=us;
    }
    
    
    public static void grabarDatos(ArrayList<Persona> p, File f)
    {
      try
      {   
       Ficheros.escribirTabla(p,f);
      }catch (IOException e)
      {
          FuncionesGraficas.warning("Atención", "Hubo un problema : " + 
                                     e.getMessage());
      }
        
    }
    
    @FXML
    private void cancelarSalida(ActionEvent event) 
    {
     Stage ventana=(Stage) this.btnNo.getScene().getWindow();
     ventana.close();
    }
        
    
}
