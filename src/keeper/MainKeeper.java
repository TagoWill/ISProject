package keeper;

import javax.naming.NamingException;
import java.io.IOException;

/**
 * Created by Daniel Bastos e Tiago Andrade on 30/09/2015.
 * Trabalho: Integracao de Sistemas
 */

public class MainKeeper{

    @SuppressWarnings("all")
    public static void main(String[] args) throws NamingException, IOException, InterruptedException {
        FatherKeeper fk = new FatherKeeper();
        System.out.print("Para desligar click enter\n");
        System.in.read();
        //System.out.println("Isto e um teste: " + fk.getTeste());
        //fk.shutdown();
        fk.setshutdown();
    }
}
