package com.gimnasio.gympjv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto de entrada de la aplicacion Pedro J. Villa App.
 *
 * @author Ayoub Atidi Belbaz - 1 DAM
 */
public class App extends Application {

    private static Stage primaryStage;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        Parent root = cargarVista("InicioSesion");
        scene = new Scene(root);
        // Aplicar la hoja de estilos
        scene.getStylesheets().add(
                getClass().getResource("/com/gimnasio/gympjv/css/estilos.css").toExternalForm());
        stage.setTitle("Pedro J. Villa App - Gestion Integral de Gimnasio");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    /**
     * Carga una vista FXML por nombre (sin extension).
     * Las vistas viven en /com/gimnasio/gympjv/fxml/.
     */
    public static Parent cargarVista(String nombre) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/com/gimnasio/gympjv/fxml/" + nombre + ".fxml"));
        return loader.load();
    }

    /**
     * Cambia la vista raiz de la escena actual (navegacion entre pantallas).
     */
    public static void cambiarVista(String nombre) throws IOException {
        Parent root = cargarVista(nombre);
        scene.setRoot(root);
    }

    @Override
    public void stop() {
        ConexionBBDD.cerrarConexion();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
