package com.gimnasio.gympjv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de la aplicación JavaFX.
 *
 * @author Ayoub Atidi Belbaz 1º DAM
 */
public class App extends Application {

    private static Stage primaryStage;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        Parent root = cargarVista("InicioSesion");
        scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource("/com/gimnasio/gympjv/css/estilos.css").toExternalForm());
        stage.setTitle("Pedro J. Villa App - Gestion Integral de Gimnasio");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    /** Carga un FXML de la carpeta fxml/ por nombre (sin extensión). */
    public static Parent cargarVista(String nombre) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/com/gimnasio/gympjv/fxml/" + nombre + ".fxml"));
        return loader.load();
    }

    /** Cambia la vista del Stage sin recargar la ventana. */
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
