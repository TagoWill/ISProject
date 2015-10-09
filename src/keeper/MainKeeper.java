package keeper;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Tiago on 30/09/2015.
 * Trabalho: Interesacao de Sistemas
 */
public class MainKeeper{

    public static void main(String[] args) throws NamingException, IOException, InterruptedException {
        FatherKeeper fk = new FatherKeeper();
        System.out.print("Para desligar click enter\n");
        System.in.read();
        System.out.print("Isto e um teste: " + fk.getTeste());
        fk.shutdown();
    }
}
