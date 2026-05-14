package modulos;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FuncionesFechas {
   public static GregorianCalendar crearFecha(String titulo) {
      JPanel panel = new JPanel();
      JLabel dia = new JLabel("Día : ");
      JTextField d = new JTextField(2);
      JLabel mes = new JLabel("   Mes : ");
      JTextField m = new JTextField(2);
      JLabel año = new JLabel("   Año : ");
      JTextField a = new JTextField(4);
      int numd = 0;
      int numm = 0;
      int numa = 0;
      panel.add(dia);
      panel.add(d);
      panel.add(mes);
      panel.add(m);
      panel.add(año);
      panel.add(a);
      String[] opciones = new String[]{"Aceptar", "Cancelar"};
      boolean correcta = false;

      do {
         int opcion = JOptionPane.showOptionDialog((Component)null, panel, titulo, 1, -1, (Icon)null, opciones, opciones[0]);
         if (opcion == 0) {
            try {
               numd = Integer.parseInt(d.getText());
               numm = Integer.parseInt(m.getText());
               numa = Integer.parseInt(a.getText());
               if (fechaCorrecta(numd, numm, numa)) {
                  correcta = true;
               } else {
                  FuncionesGraficas.mostrarDatos("Fecha inválida", "La fecha introducida no es válida.");
               }
            } catch (NumberFormatException var15) {
               FuncionesGraficas.mostrarDatos("Error de conversión", "La fecha introducida no es correcta.");
            }

            d.setText("");
            m.setText("");
            a.setText("");
         } else {
            if (opcion == 1) {
               return null;
            }

            System.exit(0);
         }
      } while(!correcta);

      GregorianCalendar fecha = new GregorianCalendar(numa, numm - 1, numd);
      return fecha;
   }

   public static int calculaEdad(GregorianCalendar fecha) {
      GregorianCalendar fechaactual = new GregorianCalendar();
      int años = fechaactual.get(1) - fecha.get(1);
      if (fechaactual.get(2) < fecha.get(2)) {
         --años;
      } else if (fechaactual.get(2) == fecha.get(2) && fechaactual.get(5) < fecha.get(5)) {
         --años;
      }

      return años;
   }

   public static boolean fechaRango(GregorianCalendar fechaInicio, GregorianCalendar fechaFin, GregorianCalendar fecha) {
      if (fechaInicio.after(fechaFin)) {
         return false;
      } else {
         return fechaInicio.before(fecha) && fechaFin.after(fecha) || fecha.get(1) == fechaInicio.get(1) && fecha.get(2) == fechaInicio.get(2) && fecha.get(5) == fechaInicio.get(5);
      }
   }

   public static boolean fechaCorrecta(int dia, int mes, int año) {
      if (año > 0) {
         if (mes == 2) {
            if (dia > 0 && dia <= 28) {
               return true;
            } else {
               return dia == 29 ? esBisiesto(año) : false;
            }
         } else {
            if (mes != 1 && mes != 3 && mes != 5 && mes != 7 && mes != 8 && mes != 10 && mes != 12) {
               if ((mes == 4 || mes == 6 || mes == 9 || mes == 11) && dia > 0 && dia <= 30) {
                  return true;
               }
            } else if (dia > 0 && dia <= 31) {
               return true;
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean esBisiesto(int año) {
      return año % 4 == 0 && (año % 100 != 0 || año % 400 == 0);
   }

   public static String formatoFecha(GregorianCalendar fecha) {
      SimpleDateFormat ffecha = new SimpleDateFormat("dd-MM-yyyy");
      String cadena = ffecha.format(fecha.getTime());
      return cadena;
   }
}
