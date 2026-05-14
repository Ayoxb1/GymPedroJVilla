package modulos;

public class Calculadora {
   private static final double PI = 3.141592D;

   public static int sumar(int a, int b) {
      return a + b;
   }

   public static int multiplicar(int a, int b) {
      return a * b;
   }

   public static int restar(int a, int b) {
      return a - b;
   }

   public static int aleatorio(int a, int b) {
      return (int)(Math.random() * (double)(b + 1 - a) + (double)a);
   }

   public static double longitudC(double r) {
      return 3.141592D * Math.pow(r, 2.0D);
   }
}
