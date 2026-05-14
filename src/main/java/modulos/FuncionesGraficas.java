package modulos;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class FuncionesGraficas {
   public static void mostrarDatos(String titulo, String datos) {
      UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));
      String[] opciones = new String[]{"Aceptar"};
      int opcion = JOptionPane.showOptionDialog((Component)null, datos, titulo, 1, -1, (Icon)null, opciones, opciones[0]);
      if (opcion != 0) {
         System.exit(0);
      }

   }

   public static void warning(String titulo, String datos) {
      UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));
      String[] opciones = new String[]{"Aceptar"};
      int opcion = JOptionPane.showOptionDialog((Component)null, datos, titulo, 1, 2, (Icon)null, opciones, opciones[0]);
      if (opcion != 0) {
         System.exit(0);
      }

   }

   public static void mostrarMensaje(String titulo, String mensaje, int icono) {
      UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));
      JOptionPane.showMessageDialog((Component)null, mensaje, titulo, icono);
   }

   public static String pedirDatos(String titulo, String pregunta) {
      UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));
      String[] var10000 = new String[]{"Aceptar", "Cancelar"};
      String opcion = JOptionPane.showInputDialog((Component)null, pregunta, titulo, 3);
      return opcion != null ? opcion : null;
   }

   public static int menu(String titulo, String[] opciones, int maxop) {
      UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));

      while(true) {
         String opcion = JOptionPane.showInputDialog((Component)null, opciones, titulo, 3);
         if (opcion == null) {
            return -1;
         }

         try {
            int num = Integer.parseInt(opcion);
            if (num <= maxop && num >= 0) {
               return num - 1;
            }

            mostrarDatos("Opción incorrecta", "La opción elegida no corresponde con ningún valor.");
         } catch (NumberFormatException var6) {
            mostrarDatos("Valor incorrecto", "Introduzca una opción válida.");
         }
      }
   }

   public static int menuDesplegable(String titulo, String texto, String[] opciones) {
      UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));
      String opcion = (String)JOptionPane.showInputDialog((Component)null, texto, titulo, 3, (Icon)null, opciones, opciones[0]);
      if (opcion != null) {
         for(int i = 0; i < opciones.length; ++i) {
            if (opcion.equals(opciones[i])) {
               return i;
            }
         }
      }

      return -1;
   }

   public static String desplegable(String titulo, String texto, String[] opciones) {
      UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));
      return (String)JOptionPane.showInputDialog((Component)null, texto, titulo, 3, (Icon)null, opciones, opciones[0]);
   }

   public static boolean pedirBool(String titulo, String texto) {
      JPanel panel = new JPanel();
      JLabel infor = new JLabel(texto);
      UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));
      panel.add(infor);
      String[] opcs = new String[]{"Si", "No"};
      int opcion = JOptionPane.showOptionDialog((Component)null, panel, titulo, 1, 3, (Icon)null, opcs, opcs[0]);
      if (opcion == 0) {
         return true;
      } else {
         if (opcion != 1) {
            System.exit(0);
         }

         return false;
      }
   }

   public static int pedirEntero(String titulo, String pregunta) {
      UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));

      while(true) {
         try {
            String conversion = pedirDatos(titulo, pregunta);
            if (conversion != null) {
               int numero = Integer.parseInt(conversion);
               return numero;
            }
         } catch (NumberFormatException var4) {
            mostrarDatos("Error", "Datos introducidos incorrectamente.");
         }
      }
   }

   public static float pedirFloat(String titulo, String pregunta) {
      UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));

      while(true) {
         try {
            String conversion = pedirDatos(titulo, pregunta);
            if (conversion != null) {
               float numero = Float.parseFloat(conversion);
               return numero;
            }
         } catch (NumberFormatException var4) {
            mostrarDatos("Error", "Datos introducidos incorrectamente.");
         }
      }
   }

   public static double pedirDouble(String titulo, String pregunta) {
      UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));

      while(true) {
         try {
            String conversion = pedirDatos(titulo, pregunta);
            if (conversion != null) {
               double numero = Double.parseDouble(conversion);
               return numero;
            }
         } catch (NumberFormatException var5) {
            mostrarDatos("Error", "Datos introducidos incorrectamente.");
         }
      }
   }

   public static String barraPorcentaje(int valor) {
      String porcentaje = new String();
      String aumento = "#";

      for(int i = 0; i < valor / 5; ++i) {
         porcentaje = porcentaje + aumento;
      }

      return porcentaje;
   }

   public static void FotoyMensaje(String titulo, String nomFichero, String texto, double factorEscalado, boolean full) {
      JPanel panel = new JPanel();
      if (full) {
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         int anchoPantalla = screenSize.width;
         int altoPantalla = screenSize.height - 50;
         UIManager.put("OptionPane.minimumSize", new Dimension(anchoPantalla, altoPantalla));
      } else {
         UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));
      }

      File fichero = new File(nomFichero);
      ImageIcon icon = new ImageIcon(fichero.toString());
      JLabel foto = new JLabel();
      Icon icono = new ImageIcon(icon.getImage().getScaledInstance((int)((double)icon.getIconWidth() * factorEscalado), (int)((double)icon.getIconHeight() * factorEscalado), 1));
      foto.setIcon(icono);
      panel.add(foto);
      JOptionPane.getRootFrame().setLocationRelativeTo((Component)null);
      JOptionPane.showMessageDialog(panel, texto, titulo, -1, icono);
   }

   public static String FotoYPedirDatos(String titulo, String nomFichero, String pregunta, double factorEscalado, boolean full) {
      JPanel panel = new JPanel();
      if (full) {
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         int anchoPantalla = screenSize.width;
         int altoPantalla = screenSize.height - 50;
         UIManager.put("OptionPane.minimumSize", new Dimension(anchoPantalla, altoPantalla));
      } else {
         UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));
      }

      JOptionPane.getRootFrame().setLocationRelativeTo((Component)null);
      File fichero = new File(nomFichero);
      ImageIcon icon = new ImageIcon(fichero.toString());
      JLabel foto = new JLabel();
      Icon icono = new ImageIcon(icon.getImage().getScaledInstance((int)((double)icon.getIconWidth() * factorEscalado), (int)((double)icon.getIconHeight() * factorEscalado), 1));
      foto.setIcon(icono);
      panel.add(foto);
      return (String)JOptionPane.showInputDialog(panel, pregunta, titulo, -1, icono, (Object[])null, (Object)null);
   }

   public static int FotoMensajeMenu(String titulo, String[] opciones, String nomFichero, String texto, double factorEscalado, boolean full) {
      JPanel panel = new JPanel();
      File fichero = new File(nomFichero);
      ImageIcon icon = new ImageIcon(fichero.toString());
      int valor = 0;
      JLabel foto = new JLabel();
      int i;
      if (full) {
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         int anchoPantalla = screenSize.width;
         i = screenSize.height - 50;
         UIManager.put("OptionPane.minimumSize", new Dimension(anchoPantalla, i));
      } else {
         UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));
      }

      JOptionPane.getRootFrame().setLocationRelativeTo((Component)null);
      Icon icono = new ImageIcon(icon.getImage().getScaledInstance((int)((double)icon.getIconWidth() * factorEscalado), (int)((double)icon.getIconHeight() * factorEscalado), 1));
      foto.setIcon(icono);
      panel.add(foto);
      Object selectedValue = JOptionPane.showInputDialog(panel, texto, titulo, 1, icono, opciones, opciones[0]);

      for(i = 0; i < opciones.length; ++i) {
         if (selectedValue.equals(opciones[i])) {
            valor = i;
         }
      }

      return valor;
   }

   public static int FotoMensajeMenuSonido(String titulo, String[] opciones, String nomFichero, String texto, String nombreAudio, double factorEscalado, int tipoSonido, boolean full) {
      JPanel panel = new JPanel();
      File fichero = new File(nomFichero);
      ImageIcon icon = new ImageIcon(fichero.toString());
      FuncionesSonidos sonido = new FuncionesSonidos(nombreAudio, tipoSonido);
      int valor = 0;
      JLabel foto = new JLabel();
      int i;
      if (full) {
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         int anchoPantalla = screenSize.width;
         i = screenSize.height - 50;
         UIManager.put("OptionPane.minimumSize", new Dimension(anchoPantalla, i));
      } else {
         UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));
      }

      JOptionPane.getRootFrame().setLocationRelativeTo((Component)null);
      Icon icono = new ImageIcon(icon.getImage().getScaledInstance((int)((double)icon.getIconWidth() * factorEscalado), (int)((double)icon.getIconHeight() * factorEscalado), 1));
      foto.setIcon(icono);
      panel.add(foto);
      if (tipoSonido == 0) {
         sonido.reproducirSonidoWav();
      } else {
         sonido.reproducirSonidoMp3(nombreAudio);
      }

      Object selectedValue = JOptionPane.showInputDialog(panel, texto, titulo, 1, icono, opciones, opciones[0]);
      if (selectedValue == null) {
         return -1;
      } else {
         for(i = 0; i < opciones.length; ++i) {
            if (selectedValue.equals(opciones[i])) {
               valor = i;
            }
         }

         if (tipoSonido == 0) {
            sonido.pararSonidoWav();
         } else {
            sonido.pausarSonidoMp3();
         }

         return valor;
      }
   }

   public static void FotoMensajeSonido(String titulo, String nomFichero, String texto, String nombreAudio, double factorEscalado, int tipoSonido, boolean full) {
      JPanel panel = new JPanel();
      File fichero = new File(nomFichero);
      ImageIcon icon = new ImageIcon(fichero.toString());
      FuncionesSonidos sonido = new FuncionesSonidos(nombreAudio, tipoSonido);
      JLabel foto = new JLabel();
      if (full) {
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         int anchoPantalla = screenSize.width;
         int altoPantalla = screenSize.height - 50;
         UIManager.put("OptionPane.minimumSize", new Dimension(anchoPantalla, altoPantalla));
      } else {
         UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));
      }

      Icon icono = new ImageIcon(icon.getImage().getScaledInstance((int)((double)icon.getIconWidth() * factorEscalado), (int)((double)icon.getIconHeight() * factorEscalado), 1));
      foto.setIcon(icono);
      panel.add(foto);
      if (tipoSonido == 0) {
         sonido.reproducirSonidoWav();
      } else {
         sonido.reproducirSonidoMp3(nombreAudio);
      }

      JOptionPane.showMessageDialog(panel, texto, titulo, -1, icono);
      if (tipoSonido == 0) {
         sonido.pararSonidoWav();
      } else {
         sonido.pararSonidoMp3();
      }

   }

   public static String FotoPedirDatosYSonido(String titulo, String nomFichero, String pregunta, String nombreAudio, double factorEscalado, int tipoSonido, boolean full) {
      JPanel panel = new JPanel();
      File fichero = new File(nomFichero);
      ImageIcon icon = new ImageIcon(fichero.toString());
      FuncionesSonidos sonido = new FuncionesSonidos(nombreAudio, tipoSonido);
      JLabel foto = new JLabel();
      if (full) {
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         int anchoPantalla = screenSize.width;
         int altoPantalla = screenSize.height - 50;
         UIManager.put("OptionPane.minimumSize", new Dimension(anchoPantalla, altoPantalla));
      } else {
         UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));
      }

      JOptionPane.getRootFrame().setLocationRelativeTo((Component)null);
      Icon icono = new ImageIcon(icon.getImage().getScaledInstance((int)((double)icon.getIconWidth() * factorEscalado), (int)((double)icon.getIconHeight() * factorEscalado), 1));
      foto.setIcon(icono);
      panel.add(foto);
      if (tipoSonido == 0) {
         sonido.reproducirSonidoWav();
      } else {
         sonido.reproducirSonidoMp3(nombreAudio);
      }

      String respuesta = (String)JOptionPane.showInputDialog(panel, pregunta, titulo, -1, icono, (Object[])null, (Object)null);
      if (tipoSonido == 0) {
         sonido.pararSonidoWav();
      } else {
         sonido.pararSonidoMp3();
      }

      return respuesta;
   }

   public static void FotoMensajeSonidoAutomatica(String titulo, String nomFichero, String texto, String nombreAudio, int segundos, double factorEscalado, int tipoSonido, boolean full) {
      if (full) {
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         int anchoPantalla = screenSize.width;
         int altoPantalla = screenSize.height - 50;
         UIManager.put("OptionPane.minimumSize", new Dimension(anchoPantalla, altoPantalla));
      } else {
         UIManager.put("OptionPane.minimumSize", new Dimension(0, 0));
      }

      JPanel panel = new JPanel();
      File fichero = new File(nomFichero);
      ImageIcon icon = new ImageIcon(fichero.toString());
      FuncionesSonidos sonido = new FuncionesSonidos(nombreAudio, tipoSonido);
      JLabel foto = new JLabel();
      Icon icono = new ImageIcon(icon.getImage().getScaledInstance((int)((double)icon.getIconWidth() * factorEscalado), (int)((double)icon.getIconHeight() * factorEscalado), 1));
      foto.setIcon(icono);
      panel.add(foto);
      if (tipoSonido == 0) {
         sonido.reproducirSonidoWav();
      } else {
         sonido.reproducirSonidoMp3(nombreAudio);
      }

      JOptionPane.getRootFrame().setLocationRelativeTo((Component)null);
      JOptionPane optionPane = new JOptionPane(panel, -1, -1, (Icon)null, new Object[]{"Omitir"}, (Object)null);
      final JDialog dialog = optionPane.createDialog(titulo);
      dialog.setModal(true);
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {
         public void run() {
            dialog.dispose();
         }
      }, (long)(segundos * 1000));
      dialog.setVisible(true);
      if (tipoSonido == 0) {
         sonido.pararSonidoWav();
      } else {
         sonido.pararSonidoMp3();
      }

   }
}
