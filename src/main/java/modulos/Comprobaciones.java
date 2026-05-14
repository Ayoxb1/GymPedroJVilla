package modulos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Comprobaciones {
   public static char calcularLetra(int dni) {
      char[] caracteres = new char[]{'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};
      int resto = dni % 23;
      return caracteres[resto];
   }

   public static boolean comprobarDni(String cad) {
      new String();
      if (cad.length() != 9) {
         return false;
      } else {
         String trozo = cad.substring(0, 8);
         char l = cad.charAt(8);
         if (esEntero(trozo)) {
            return calcularLetra(Integer.parseInt(trozo)) == l;
         } else {
            return false;
         }
      }
   }

   public static boolean comprobarEmail(String email) {
      return email.matches("[a-zA-Z0-9._-]+[@][a-z]+[.][a-z]{2,3}");
   }

   public static boolean esEntero(String entero) {
      return entero.matches("-?[0-9]+");
   }

   public static boolean esReal(String real) {
      return real.matches("-?[0-9]+.[0-9]+");
   }

   public static boolean esPrimo(int n) {
      return true;
   }

   public static boolean esPerfecto(int n) {
      return true;
   }

   public static boolean ipValida(String cadIP) {
      Pattern patron = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
      Matcher resultado = patron.matcher(cadIP);
      return resultado.matches();
   }
}
