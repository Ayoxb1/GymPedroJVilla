package modulos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class Ficheros {
   public static List<?> leerTabla(File fich) throws FileNotFoundException, IOException, ClassNotFoundException {
      List<?> tabla = null;
      if (fich.exists()) {
         FileInputStream fIn = new FileInputStream(fich);

         try {
            ObjectInputStream regUsuario = new ObjectInputStream(fIn);

            try {
               tabla = (List)regUsuario.readObject();
               fIn.close();
               regUsuario.close();
            } catch (Throwable var8) {
               try {
                  regUsuario.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            regUsuario.close();
         } catch (Throwable var9) {
            try {
               fIn.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }

            throw var9;
         }

         fIn.close();
      } else {
         fich.createNewFile();
      }

      return tabla;
   }

   public static void escribirTabla(List<?> tabla, File fich) throws FileNotFoundException, IOException {
      FileOutputStream fOut = new FileOutputStream(fich);

      try {
         ObjectOutputStream regUsOut = new ObjectOutputStream(fOut);

         try {
            regUsOut.writeObject(tabla);
            regUsOut.close();
            fOut.close();
         } catch (Throwable var8) {
            try {
               regUsOut.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         regUsOut.close();
      } catch (Throwable var9) {
         try {
            fOut.close();
         } catch (Throwable var6) {
            var9.addSuppressed(var6);
         }

         throw var9;
      }

      fOut.close();
   }
}
