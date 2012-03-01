/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.taxonomy;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import org.openide.util.Exceptions;

/**
 *
 * @author kec
 */
public class FutureHelper implements Runnable {
   static final Thread                futureHelperThread = new Thread(new FutureHelper(), "FutureHelper");
   static LinkedBlockingQueue<Future> futures            = new LinkedBlockingQueue<Future>();

   //~--- static initializers -------------------------------------------------

   static {
      futureHelperThread.setDaemon(true);
      futureHelperThread.start();
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public void run() {
      while (true) {
         try {
            Future f = futures.take();

            f.get();
         } catch (Throwable ex) {
            Exceptions.printStackTrace(ex);
         }
      }
   }
   
   public static void addFuture(Future f) {
        try {
            futures.put(f);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
   }
}
