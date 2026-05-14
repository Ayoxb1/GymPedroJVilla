package modulos;

import clases.Usuario;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class GestionUsuarios {
   private ArrayList<Usuario> usuarios = new ArrayList();
   private Usuario activo;

   public ArrayList<Usuario> getUsuarios() {
      return this.usuarios;
   }

   public void AñadirUsuario(String us, String p, int nivel) {
      Usuario usa = new Usuario(us, p, nivel);
      this.getUsuarios().add(usa);
   }

   public void altaUsuario() {
      String usuario = FuncionesGraficas.pedirDatos("Dar de Alta", "Usuario : ");
      if (usuario != null) {
         if (this.busquedaNombre(usuario) == -1) {
            String contra = this.pedirContra("Dar de Alta", "Contraseña : ");
            if (contra != null) {
               int nivel = this.pedirNivel("Dar de Alta");
               if (nivel != -1) {
                  Usuario nuevo = new Usuario(usuario, contra, nivel);
                  this.getUsuarios().add(nuevo);
               } else {
                  FuncionesGraficas.mostrarDatos("Alta no realizada", "Operación cancelada.");
               }
            } else {
               FuncionesGraficas.mostrarDatos("Alta no realizada", "Operación cancelada.");
            }
         } else {
            FuncionesGraficas.mostrarDatos("Alta no realizada", "El nombre de usuario no se encuentra disponible.");
         }
      } else {
         FuncionesGraficas.mostrarDatos("Alta no realizada", "Operación cancelada.");
      }

   }

   public void bajaUsuario() {
      String usuario = FuncionesGraficas.pedirDatos("Dar de baja", "Usuario : ");
      if (usuario != null) {
         int posicion = this.busquedaNombre(usuario);
         if (posicion != -1) {
            this.getUsuarios().remove(posicion);
            FuncionesGraficas.mostrarDatos("Baja realizada", "Usuario " + usuario + " eliminado con éxito.");
         } else {
            FuncionesGraficas.mostrarDatos("Baja no realizada", "El nombre de usuario no se encuentra en la base de datos.");
         }
      }

      FuncionesGraficas.mostrarDatos("Baja no realizada", "Operación cancelada.");
   }

   public void bajaUsuario(String nick) {
      int posicion = this.busquedaNombre(nick);
      if (posicion != -1) {
         String usuario = ((Usuario)this.usuarios.get(posicion)).getNombre();
         this.usuarios.remove(posicion);
         FuncionesGraficas.mostrarDatos("Baja realizada", "Usuario " + usuario + " eliminado con éxito.");
      } else {
         FuncionesGraficas.mostrarDatos("Baja no realizada", "El nombre de usuario no se encuentra en la base de datos.");
      }

   }

   public int pedirNivel(String titulo) {
      JPanel panel = new JPanel();
      JLabel preg = new JLabel("Selecciona un nivel: ");
      JRadioButton admin = new JRadioButton("Administrador", true);
      JRadioButton user = new JRadioButton("Usuario", false);
      panel.add(preg);
      panel.add(admin);
      panel.add(user);
      ButtonGroup grupobotones = new ButtonGroup();
      grupobotones.add(admin);
      grupobotones.add(user);
      String[] opciones = new String[]{"Aceptar", "Cancelar"};
      int opcion = JOptionPane.showOptionDialog((Component)null, panel, titulo, 1, 3, (Icon)null, opciones, opciones[0]);
      if (opcion == 0) {
         if (admin.isSelected()) {
            return 0;
         }

         if (user.isSelected()) {
            return 1;
         }
      } else if (opcion != 1) {
         System.exit(0);
      }

      return -1;
   }

   public void cambioUsuario() {
      String nuevousuario = FuncionesGraficas.pedirDatos("Introduce el nuevo usuario", " Usuario : ");
      if (nuevousuario != null) {
         int posicion = this.busquedaNombre(nuevousuario);
         if (posicion != -1) {
            String contra = this.pedirContra("Introduce la contraseña del nuevo usuario", " Contraseña : ");
            if (contra != null) {
               if (((Usuario)this.getUsuarios().get(posicion)).getContraseña().equals(contra)) {
                  this.activo = (Usuario)this.getUsuarios().get(posicion);
                  FuncionesGraficas.mostrarDatos("Nuevo usuario: " + this.getActivo().getNombre(), "Cambio realizado con éxito");
               } else {
                  FuncionesGraficas.mostrarDatos("Error", "La contraseña no coincide.");
               }
            } else {
               FuncionesGraficas.mostrarDatos("Cambio no realizado", "Operación cancelada.");
            }
         } else {
            FuncionesGraficas.mostrarDatos("Error", "El usuario no existe");
         }
      } else {
         FuncionesGraficas.mostrarDatos("Cambio no realizado", "Operación cancelada.");
      }

   }

   public void modificar() {
      String usuario = FuncionesGraficas.pedirDatos("Consultar Usuario", "Usuario : ");
      if (usuario != null) {
         int posicion = this.busquedaNombre(usuario);
         if (posicion != -1) {
            String nuevacontra = this.nuevaContra();
            if (nuevacontra != null) {
               int nivel = this.pedirNivel("Nuevo nivel");
               if (nivel != -1) {
                  ((Usuario)this.getUsuarios().get(posicion)).setContraseña(nuevacontra);
                  ((Usuario)this.getUsuarios().get(posicion)).setNivel(nivel);
                  FuncionesGraficas.mostrarDatos("Modificación realizada", "Usuario " + usuario + " modificado con éxito.");
               } else {
                  FuncionesGraficas.mostrarDatos("Modificación no realizada", "Operación cancelada.");
               }
            } else {
               FuncionesGraficas.mostrarDatos("Modificación no realizada", "Operación cancelada.");
            }
         } else {
            FuncionesGraficas.mostrarDatos("Error", "El nombre de usuario no se encuentra registrado.");
         }
      } else {
         FuncionesGraficas.mostrarDatos("Modificación no realizada", "Operación cancelada.");
      }

   }

   public int busquedaNombre(String nombre) {
      for(int i = 0; i < this.getUsuarios().size(); ++i) {
         if (((Usuario)this.getUsuarios().get(i)).getNombre().equals(nombre)) {
            return i;
         }
      }

      return -1;
   }

   public int busqueda(String nombre, String contra) {
      for(int i = 0; i < this.getUsuarios().size(); ++i) {
         if (((Usuario)this.getUsuarios().get(i)).getNombre().equals(nombre) && ((Usuario)this.getUsuarios().get(i)).getContraseña().equals(contra)) {
            return i;
         }
      }

      return -1;
   }

   public void usuarioActivo() {
      if (this.getActivo() == null) {
         FuncionesGraficas.mostrarDatos("Usuario activo", "En estos momentos, ningún usuario esta activo.");
      } else if (this.getActivo().getNivel() == 0) {
         FuncionesGraficas.mostrarDatos("Usuario activo", "Usuario :" + this.getActivo().getNombre() + "  Nivel: Administrador");
      } else {
         FuncionesGraficas.mostrarDatos("Usuario activo", "Usuario :" + this.getActivo().getNombre() + "  Nivel: Usuario");
      }

   }

   public boolean contraseñaIntentos() {
      int contador = 0;
      JPanel panel = new JPanel();
      JLabel etiquetanombre = new JLabel("Nombre: ");
      JTextField nom = new JTextField(10);
      JLabel etiquetacontra = new JLabel("Contraseña: ");
      JPasswordField password = new JPasswordField(10);
      JLabel mensaje = new JLabel("(Quedan 3 intentos)");
      panel.add(etiquetanombre);
      panel.add(nom);
      panel.add(etiquetacontra);
      panel.add(password);
      panel.add(mensaje);
      String[] opciones = new String[]{"Aceptar"};

      do {
         while(true) {
            int opcion = JOptionPane.showOptionDialog((Component)null, panel, "Introduzca la contraseña", 1, -1, (Icon)null, opciones, opciones[0]);
            if (opcion == 0) {
               String nombre = new String(nom.getText());
               String psswd = new String(password.getPassword());
               int posicion = this.busqueda(nombre, psswd);
               if (posicion != -1) {
                  this.activo = (Usuario)this.getUsuarios().get(posicion);
                  return true;
               }

               ++contador;
               nom.setText("");
               password.setText("");
               if (contador != 2) {
                  mensaje.setText("(Quedan " + (3 - contador) + " intentos)");
               } else {
                  mensaje.setText("(Queda " + (3 - contador) + " intento)");
               }
               break;
            }

            System.exit(0);
         }
      } while(contador != 3);

      return false;
   }

   public String pedirContra(String titulo, String texto) {
      JPanel panel = new JPanel();
      JLabel ps1 = new JLabel(texto);
      JPasswordField pass1 = new JPasswordField(10);
      panel.add(ps1);
      panel.add(pass1);
      String[] opciones = new String[]{"Aceptar", "Cancelar"};

      while(true) {
         while(true) {
            int opcion = JOptionPane.showOptionDialog((Component)null, panel, titulo, 1, -1, (Icon)null, opciones, opciones[0]);
            if (opcion == 0) {
               String contra1 = new String(pass1.getPassword());
               if (!contra1.isEmpty()) {
                  return contra1;
               }

               FuncionesGraficas.mostrarDatos("Error", "Introduzca una contraseña por favor.");
            } else {
               if (opcion == 1) {
                  return null;
               }

               System.exit(0);
            }
         }
      }
   }

   public String nuevaContra() {
      JPanel panel = new JPanel();
      JLabel ps1 = new JLabel("Contraseña : ");
      JPasswordField pass1 = new JPasswordField(10);
      JLabel ps2 = new JLabel("Repite contraseña : ");
      JPasswordField pass2 = new JPasswordField(10);
      panel.add(ps1);
      panel.add(pass1);
      panel.add(ps2);
      panel.add(pass2);
      String[] opciones = new String[]{"Aceptar", "Cancelar"};
      int opcion = JOptionPane.showOptionDialog((Component)null, panel, "Nueva contraseña de usuario", 1, -1, (Icon)null, opciones, opciones[0]);
      if (opcion == 0) {
         char[] password1 = pass1.getPassword();
         String contra1 = new String(password1);
         char[] password2 = pass2.getPassword();
         String contra2 = new String(password2);
         if (contra1.equals(contra2)) {
            return contra1;
         } else {
            FuncionesGraficas.mostrarDatos("Error", "No coinciden las contraseñas");
            return null;
         }
      } else {
         if (opcion != 1) {
            System.exit(0);
         }

         return null;
      }
   }

   public int acceso(String titulo) {
      JPanel panel = new JPanel();
      JLabel etiquetanombre = new JLabel("Nombre: ");
      JTextField nom = new JTextField(10);
      JLabel etiquetacontra = new JLabel("Contraseña: ");
      JPasswordField password = new JPasswordField(10);
      panel.add(etiquetanombre);
      panel.add(nom);
      panel.add(etiquetacontra);
      panel.add(password);
      String[] opciones = new String[]{"Aceptar", "Cancelar"};
      int opcion = JOptionPane.showOptionDialog((Component)null, panel, titulo, 1, -1, (Icon)null, opciones, opciones[0]);
      if (opcion == 0) {
         String nombre = nom.getText();
         String psswd = new String(password.getPassword());
         if (nombre != null && psswd != null) {
            return this.busqueda(nombre, psswd);
         } else {
            FuncionesGraficas.mostrarDatos("Error", "No se han introducido datos.");
            return -2;
         }
      } else {
         if (opcion != 1) {
            System.exit(0);
         }

         return -1;
      }
   }

   public String nombreUsuActivo() {
      return this.getActivo().getNombre();
   }

   public int nivelUsuActivo() {
      return this.getActivo().getNivel();
   }

   public int posUsuarioActivo() {
      return this.busquedaNombre(this.getActivo().getNombre());
   }

   public Usuario getActivo() {
      return this.activo;
   }

   public void setActivo(int n) {
      this.activo = (Usuario)this.getUsuarios().get(n);
   }

   public int posActivo() {
      return this.getUsuarios().indexOf(this.nombreUsuActivo());
   }

   public void setUsuarios(ArrayList<Usuario> usuarios) {
      this.usuarios = usuarios;
   }
}
