package clases;

import java.io.Serializable;

public class Usuario implements Serializable {
   private String nombre;
   private String contraseña;
   private int nivel;

   public Usuario(String nombre, String contraseña, int nivel) {
      this.nombre = nombre;
      this.contraseña = contraseña;
      this.nivel = nivel;
   }

   public String getNombre() {
      return this.nombre;
   }

   public void setNombre(String nombre) {
      this.nombre = nombre;
   }

   public String getContraseña() {
      return this.contraseña;
   }

   public void setContraseña(String contraseña) {
      this.contraseña = contraseña;
   }

   public int getNivel() {
      return this.nivel;
   }

   public void setNivel(int nivel) {
      this.nivel = nivel;
   }
}
