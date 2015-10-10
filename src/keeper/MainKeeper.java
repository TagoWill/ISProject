package keeper;

import javax.naming.NamingException;
import java.io.IOException;

/**
 * Created by Tiago on 30/09/2015.
 * Trabalho: Interesacao de Sistemas
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
