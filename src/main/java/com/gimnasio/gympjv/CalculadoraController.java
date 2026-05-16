package com.gimnasio.gympjv;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * 
 *
 * @author Ayoub Atidi Belbaz  1º DAM
 */
public class CalculadoraController implements Initializable {

    @FXML private TextField campoPantalla;
    @FXML private Label     etiquetaError;
    @FXML private Button    botonSeno;
    @FXML private Button    botonCoseno;
    @FXML private Button    botonTangente;
    @FXML private Button    botonRaiz;
    @FXML private Button    botonLogaritmo;
    @FXML private Button    botonPotencia;
    @FXML private Button    botonPi;
    @FXML private Button    botonEuler;
    @FXML private Button    botonPorcentaje;
    @FXML private Button    botonLimpiar;
    @FXML private Button    botonSiete;
    @FXML private Button    botonOcho;
    @FXML private Button    botonNueve;
    @FXML private Button    botonCuatro;
    @FXML private Button    botonCinco;
    @FXML private Button    botonSeis;
    @FXML private Button    botonUno;
    @FXML private Button    botonDos;
    @FXML private Button    botonTres;
    @FXML private Button    botonCero;
    @FXML private Button    botonDivision;
    @FXML private Button    botonMultiplicacion;
    @FXML private Button    botonResta;
    @FXML private Button    botonSuma;
    @FXML private Button    botonDecimal;
    @FXML private Button    botonIgual;

    private double  valorAcumulado      = 0;
    private String  operadorPendiente   = "";
    private boolean esperandoNuevoValor = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reiniciarEstado();
    }

    @FXML
    private void pulsarNumero(ActionEvent e) {
        String digito = ((Button) e.getSource()).getText();
        if (esperandoNuevoValor) {
            campoPantalla.setText(digito);
            esperandoNuevoValor = false;
        } else {
            if (campoPantalla.getText().equals("0")) {
                campoPantalla.setText(digito);
            } else {
                campoPantalla.setText(
                    campoPantalla.getText() + digito);
            }
        }
    }

    @FXML
    private void pulsarDecimal(ActionEvent e) {
        if (esperandoNuevoValor) {
            campoPantalla.setText("0.");
            esperandoNuevoValor = false;
        } else {
            if (!campoPantalla.getText().contains(".")) {
                campoPantalla.setText(
                    campoPantalla.getText() + ".");
            }
        }
    }

    @FXML
    private void pulsarOperador(ActionEvent e) {
        String op = ((Button) e.getSource()).getText();
        boolean ok = true;
        try {
            double valorActual = Double.parseDouble(
                campoPantalla.getText());
            if (!operadorPendiente.isEmpty()) {
                double resultado = calcular(
                    valorAcumulado, valorActual,
                    operadorPendiente);
                mostrarResultado(resultado);
                valorAcumulado = resultado;
            } else {
                valorAcumulado = valorActual;
            }
        } catch (NumberFormatException ex) {
            mostrarError("Valor no valido");
            ok = false;
        } catch (ArithmeticException ex) {
            mostrarError(ex.getMessage());
            ok = false;
        }
        if (ok) {
            operadorPendiente   = op;
            esperandoNuevoValor = true;
        }
    }

    @FXML
    private void pulsarIgual(ActionEvent e) {
        if (!operadorPendiente.isEmpty()) {
            try {
                double valorActual = Double.parseDouble(
                    campoPantalla.getText());
                double resultado = calcular(
                    valorAcumulado, valorActual,
                    operadorPendiente);
                mostrarResultado(resultado);
                operadorPendiente   = "";
                esperandoNuevoValor = true;
            } catch (NumberFormatException ex) {
                mostrarError("Valor no valido");
            } catch (ArithmeticException ex) {
                mostrarError(ex.getMessage());
            }
        }
    }

    @FXML
    private void pulsarLimpiar(ActionEvent e) {
        reiniciarEstado();
    }

    @FXML
    private void pulsarPorcentaje(ActionEvent e) {
        try {
            double valor = Double.parseDouble(
                campoPantalla.getText());
            mostrarResultado(valor / 100.0);
            esperandoNuevoValor = true;
        } catch (NumberFormatException ex) {
            mostrarError("Valor no valido para porcentaje");
        }
    }

    @FXML
    private void pulsarConstante(ActionEvent e) {
        String identificador =
            (String) ((Button) e.getSource()).getUserData();
        double  valor = 0.0;
        boolean ok    = true;
        switch (identificador) {
            case "pi":
                valor = Math.PI;
                break;
            case "euler":
                valor = Math.E;
                break;
            default:
                mostrarError(
                    "Constante desconocida: "
                    + identificador);
                ok = false;
        }
        if (ok) {
            mostrarResultado(valor);
            esperandoNuevoValor = true;
        }
    }

    @FXML
    private void pulsarFuncion(ActionEvent e) {
        String funcion =
            (String) ((Button) e.getSource()).getUserData();
        try {
            double valor = Double.parseDouble(
                campoPantalla.getText());
            double resultado = aplicarFuncion(valor, funcion);
            mostrarResultado(resultado);
            esperandoNuevoValor = true;
        } catch (NumberFormatException ex) {
            mostrarError("Valor no valido");
        } catch (ArithmeticException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private double calcular(double a, double b,
            String operador) {
        double resultado = 0.0;
        switch (operador) {
            case "+":
                resultado = a + b;
                break;
            case "-":
                resultado = a - b;
                break;
            case "*":
                resultado = a * b;
                break;
            case "/":
                if (b == 0) {
                    throw new ArithmeticException(
                        "Division por cero");
                }
                resultado = a / b;
                break;
            default:
                throw new ArithmeticException(
                    "Operador desconocido: " + operador);
        }
        return resultado;
    }

    private double aplicarFuncion(double valor,
            String funcion) {
        double resultado = 0.0;
        switch (funcion) {
            case "sen":
                resultado = Math.sin(
                    Math.toRadians(valor));
                break;
            case "cos":
                resultado = Math.cos(
                    Math.toRadians(valor));
                break;
            case "tan":
                if (Math.cos(Math.toRadians(valor)) == 0) {
                    throw new ArithmeticException(
                        "Tangente indefinida para este angulo");
                }
                resultado = Math.tan(
                    Math.toRadians(valor));
                break;
            case "raiz":
                if (valor < 0) {
                    throw new ArithmeticException(
                        "Raiz cuadrada de numero negativo");
                }
                resultado = Math.sqrt(valor);
                break;
            case "logaritmo":
                if (valor <= 0) {
                    throw new ArithmeticException(
                        "Logaritmo de numero no positivo");
                }
                resultado = Math.log(valor);
                break;
            case "potencia":
                resultado = Math.pow(valor, 2);
                break;
            default:
                throw new ArithmeticException(
                    "Funcion desconocida: " + funcion);
        }
        return resultado;
    }

    private void reiniciarEstado() {
        campoPantalla.setText("0");
        etiquetaError.setText("");
        valorAcumulado      = 0;
        operadorPendiente   = "";
        esperandoNuevoValor = true;
    }

    private void mostrarResultado(double valor) {
        String texto = String.valueOf(valor);
        if (texto.endsWith(".0")) {
            texto = texto.substring(0, texto.length() - 2);
        }
        campoPantalla.setText(texto);
        etiquetaError.setText("");
    }

    private void mostrarError(String mensaje) {
        etiquetaError.setText(mensaje);
    }
}
