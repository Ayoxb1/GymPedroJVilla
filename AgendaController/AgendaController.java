/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.aplicacionjavafx2526;

import com.mycompany.aplicacionjavafx2526.clases.Persona;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modulos.FuncionesGraficas;
import java.sql.ResultSet;

/**
 * FXML Controller class
 *
 * @author juanfco
 */
public class AgendaController implements Initializable {

    @FXML
    private Button btnSalir;
    @FXML
    private Label labelUsuario;
    @FXML
    private TextField mensaje;
    @FXML
    private TextField campoNumero;
    @FXML
    private Button btnBuscar;
    @FXML
    private TextField campoNombre;
    @FXML
    private TextField campoEmail;
    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnSiguiente;
    @FXML
    private Button btnPrimero;
    @FXML
    private Button btnUltimo;
    @FXML
    private TextField campoRegistro;
    @FXML
    private Button btnAlta;
    @FXML
    private Button btnBaja;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnAyuda;
    
    private static boolean modificaciones=false;
    
    private static String usuario;
    private static ArrayList<Persona> personas;
    private static ArrayList<Persona> personasSQL;
    private static File fichero;
    private static String mens;
    
    private static String valorDevueltoNumAbuscar; 
    
    private static int contReg=0;
    @FXML
    private Label usuarioLogueado;
    @FXML
    private Button bntLimpiar;
    
    @FXML
    private TableView<Persona> tablaPersonas;
   
    @FXML
    private TableColumn<Persona,Integer> colNum;
     @FXML
    private TableColumn<Persona,String> colNombre;
    @FXML
    private TableColumn<Persona,String> colEmail;
    
    private static ObservableList<Persona> datos;
    
    private static Statement insSQL=null;
    private static ResultSet resultadoSQL=null;
    private static Connection c=null;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
     usuarioLogueado.setText(usuario);
          
     try {
          personasSQL=App.cargarDatosBBDD();
         } catch (SQLException ex) {
           System.getLogger(AgendaController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
         }
     
     try {
          insSQL=c.createStatement();
         } catch (SQLException ex) {
           System.getLogger(AgendaController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
         }
          
     //Consulta a la BBDD
     
     consultar("SELECT * FROM personas;");
        
     
     //Poner información en formulario
     if (personasSQL.size()!=0)
     {
      campoNumero.setText(String.valueOf(personasSQL.get(contReg).getNumero()));
      campoRegistro.setText(String.valueOf(contReg) + "/" + String.valueOf(personas.size()-1));
      campoNombre.setText(personasSQL.get(contReg).getNombre());
      campoEmail.setText(personasSQL.get(contReg).getEmail());
     }
     
    //Vincular columnnas con propiedades : TABLA
    colNum.setCellValueFactory(cellData-> 
     new javafx.beans.property.SimpleObjectProperty(cellData.getValue().getNumero())
     );
     
    colNombre.setCellValueFactory(cellData-> 
     new javafx.beans.property.SimpleObjectProperty(cellData.getValue().getNombre())
     );
      
    colEmail.setCellValueFactory(cellData-> 
     new javafx.beans.property.SimpleObjectProperty(cellData.getValue().getEmail())
     );
       
     datos=FXCollections.observableArrayList(personasSQL);
    
     tablaPersonas.setItems(datos);
     
     tablaPersonas.getSelectionModel().selectedItemProperty().addListener(
     (observable, oldValue, personaSeleccionada) -> {

        if (personaSeleccionada != null) {
            int indice=tablaPersonas.getSelectionModel().getSelectedIndex();
            campoNumero.setText(String.valueOf(personaSeleccionada.getNumero()));
            campoNombre.setText(personaSeleccionada.getNombre());
            campoEmail.setText(personaSeleccionada.getEmail());
            campoRegistro.setText(String.valueOf(indice) + "/" + String.valueOf(personas.size()-1));
        }
    }
    );
     
    colNum.setResizable(false);
         
          
    }    

    @FXML
    private void salirAgenda(ActionEvent event) 
    {
     //si modificaciones : Grabar datos en ED   
     if (modificaciones) SalirController.grabarDatos(personas, fichero);

     Stage ventana=(Stage) this.btnSalir.getScene().getWindow();
     ventana.close();
    }

    @FXML
    private void buscarBtn(ActionEvent event) throws IOException 
    {
     ejecutarBusqueda();
    }
    
    @FXML
    private void buscarMouse(MouseEvent event) throws IOException 
    {
     ejecutarBusqueda();
    }
    
    
    private void ejecutarBusqueda() throws IOException
    {
      NumeroAbuscarController.datosEntrada(usuario);
     //Abrir una ventana y pedir un número
     FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("NumeroAbuscar" + ".fxml"));
     Scene escena = new Scene(fxmlLoader.load());
        
     Stage escenario=new Stage();
     escenario.setResizable(false);
     escenario.setScene(escena);
            
     //Evitamos que esta ventana se cierre pulsando X 
     escenario.setOnCloseRequest(ev-> {
       ev.consume();
       FuncionesGraficas.warning("Atención","No puede cerrar asi");
     }); 
     
     //Desactiva la venta actual
     escenario.initModality(Modality.APPLICATION_MODAL);
     escenario.showAndWait();
         
     campoNumero.setText(valorDevueltoNumAbuscar);
        
     try{
         int num=Integer.parseInt(campoNumero.getText());
         
         //Buscar en personas
         int i=buscarPersona(personas,num);
         if(i!=-1)
          {
           campoNombre.setText(personas.get(i).getNombre());
           campoEmail.setText(personas.get(i).getEmail());
           campoRegistro.setText(String.valueOf(i));
           mensaje.setText("");
          }
         else mensaje.setText("No se ha encontrado : " + num);
     }catch ( NumberFormatException e)
        {
         mensaje.setText("Formato de número incorrecto, sólo enteros");
        }        
   
    }
    
    
    public static void valorSalidaFormulario(String salida)
    {
     valorDevueltoNumAbuscar=salida; 
    }
    
    public int buscarPersona(ArrayList<Persona> p, int n)
    {
     for(int i=0;i<p.size();i++)
      if(p.get(i).getNumero()==n) return(i);
     
     return(-1);
    }
    
    public void actualizarCampos()
    {
     campoNumero.setAlignment(Pos.CENTER_LEFT);
     campoRegistro.setText(String.valueOf(contReg) + "/" + String.valueOf(personas.size()-1));
     campoNumero.setText(String.valueOf(personas.get(contReg).getNumero()));
     campoNombre.setText(personas.get(contReg).getNombre());
     campoEmail.setText(personas.get(contReg).getEmail());
    }
    
      
    @FXML
    private void irAanterior(ActionEvent event) 
    {
     if (contReg!=0) contReg--;
     else contReg=personas.size()-1;   
     
     actualizarCampos();
    }

    @FXML
    private void irAsiguiente(ActionEvent event) 
    {
     if (contReg<personas.size()-1) contReg++;
     else contReg=0;
     
     actualizarCampos();
    }

    @FXML
    private void irAprimero(ActionEvent event) 
    {
     contReg=0;
     actualizarCampos();
    }

    @FXML
    private void irAultimo(ActionEvent event) 
    {
     contReg=personas.size()-1;
     actualizarCampos();   
    }

    @FXML
    private void darDeAlta(ActionEvent event) 
    {
     if(campoNumero.getText().isEmpty()) mensaje.setText("Mete un número(CLAVE)");
     else
      {
       try
        {
         int i = buscarPersona(personas, Integer.parseInt(campoNumero.getText()));
             
         if (i==-1)
          { //dar de alta
           if (campoNombre.getText().isEmpty() || campoEmail.getText().isEmpty())   
           mensaje.setText("Faltan datos");
           else
           {
            personas.add(new Persona(Integer.parseInt(campoNumero.getText()),
                                   campoNombre.getText(),
                                   campoEmail.getText()));
            
            //Dar de alta en la base de datos con JDBC    
            System.out.println("insert into personas (numero,nombre,email)"
                      + " VALUES (" + Integer.parseInt(campoNumero.getText())
                      + ",'" + campoNombre.getText() 
                      + "','"+ campoEmail.getText() + "');");
            
            
            try
             {
              insSQL.execute("insert into personas (numero,nombre,email)"
                      + " VALUES (" + Integer.parseInt(campoNumero.getText())
                      + ",'" + campoNombre.getText() 
                      + "','"+ campoEmail.getText() + "');");
                             
              }catch(SQLException e)
             {
              mensaje.setText("No se pudo insertar : " + e.getMessage());   
             }
                        
            datos.add(new Persona(Integer.parseInt(campoNumero.getText()),
                                   campoNombre.getText(),
                                   campoEmail.getText()));
                       
            mensaje.setText("Se ha dado de alta el número : " + 
                             campoNumero.getText());
            //datos=FXCollections.observableArrayList(personas);
            //tablaPersonas.setItems(datos);
     
            modificaciones=true;
           }
          }
         else 
         {
          campoNombre.setText(personas.get(i).getNombre());
          campoEmail.setText(personas.get(i).getEmail());
          mensaje.setText("Ya existe esa persona");
         }
       }catch(NumberFormatException e)
         {
          mensaje.setText("CLAVE no válida ( entero )");
         }
       }
              
      }
         

    @FXML
    private void darDeBaja(ActionEvent event) throws IOException 
    {
     if(campoNumero.getText().isEmpty()) mensaje.setText("Mete un número(CLAVE)");
     else
      {
       try
        {
         int i = buscarPersona(personas, Integer.parseInt(campoNumero.getText()));
             
         if (i==-1) //No se encuentra
          { 
           mensaje.setText("No existe ese número de persona");
          }
         else 
         { //Confirmar baja
          ConfirmarBorradoController.datosEntrada(personas, i);
          
          FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("ConfirmarBorrado" + ".fxml"));
          Scene escena = new Scene(fxmlLoader.load());
        
          Stage escenario=new Stage();
          escenario.setResizable(false);
          escenario.setScene(escena);
          escenario.initModality(Modality.APPLICATION_MODAL);
          escenario.showAndWait();
     
          //Evitamos que esta ventana se cierre pulsando X 
          escenario.setOnCloseRequest(ev-> {
          ev.consume();
          });   
          
          mensaje.setText(mens);
          limpiarCampos(event);
          
         }
       }catch(NumberFormatException e)
         {
          mensaje.setText("CLAVE no válida ( entero )");
         }
       }
       datos=FXCollections.observableArrayList(personas);
       tablaPersonas.setItems(datos);
       tablaPersonas.refresh();
        
    }

    @FXML
    private void modificarDatos(ActionEvent event) 
    {
     //después de set() poner modificaciones a true   
     if(campoNumero.getText().isEmpty()) mensaje.setText("Mete un número(CLAVE)");
     else
      {
       try
        {
         int i = buscarPersona(personas, Integer.parseInt(campoNumero.getText()));
             
         if (i==-1) //No la encuentra
          {
           mensaje.setText("No existe ese número");
          }
         else 
         {
          personas.get(i).setNombre(campoNombre.getText());
          personas.get(i).setEmail(campoEmail.getText());
          modificaciones=true;         
          mensaje.setText("Modificaciones hechas con éxito en el número : " + 
                          campoNumero.getText());
          tablaPersonas.refresh();
         }
       }catch(NumberFormatException e)
         {
          mensaje.setText("CLAVE no válida ( entero )");
         }
       }
         

     
    }

    @FXML
    private void abrirAyudaAgenda(ActionEvent event) throws IOException 
    {
      //Abrir una ventana y pedir un número
     FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("AyudaAgenda" + ".fxml"));
     Scene escena = new Scene(fxmlLoader.load());
        
     Stage escenario=new Stage();
     escenario.setResizable(false);
     escenario.setScene(escena);
            
     //Evitamos que esta ventana se cierre pulsando X 
     escenario.setOnCloseRequest(ev-> {
       ev.consume();
       FuncionesGraficas.warning("Atención","No puede cerrar asi");
     }); 
     
     //Desactiva la venta actual
     escenario.initModality(Modality.APPLICATION_MODAL);
     escenario.showAndWait();
            
    }
    
    public static void datosEntrada(ArrayList<Persona> p, File f,String us,
                                    ArrayList<Persona> psql,
                                    Connection con)
    {
     usuario=us;
     personas=p;
     personasSQL=psql;
     fichero=f;
     c=con;
    }

    @FXML
    private void limpiarCampos(ActionEvent event) 
    {
     campoNumero.setText("");
     campoNombre.setText("");
     campoEmail.setText("");
     campoRegistro.setText("");
    }
    
    public static void salida(String m,boolean borrado)
    {
     mens=m;   
     modificaciones=borrado;
    }


     public static void consultar(String consulta)
    {
      try
      {
          
       resultadoSQL=insSQL.executeQuery(consulta);
      
       ResultSetMetaData columnas=resultadoSQL.getMetaData();
      
       int numCol=columnas.getColumnCount(); //Nº de columnas de la tabla
      
       //System.out.println("Columnas : " + numCol);
      
       for(int i=1;i<=numCol;i++)
          System.out.printf("%-16s\t",columnas.getColumnName(i));
     
       System.out.println("");
     
       for(int i=1;i<=numCol;i++)
       {
        for(int j=0;j<columnas.getColumnName(i).length();j++)
        System.out.print("-");
       
        System.out.printf("%-16s\t", " ");
       }      
      
       System.out.println("");
      
       while(resultadoSQL.next())
        {
         for(int i=1;i<=numCol;i++)
         System.out.printf("%-16s\t",resultadoSQL.getObject(i));   
         System.out.println("");
        } 
     }catch(SQLException e)
     {
      e.printStackTrace();
     }
        
    }
    
    
}
