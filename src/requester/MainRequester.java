package requester;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Tiago on 01/10/2015.
 * Trabalho: Interesacao de Sistemas
 */
public class MainRequester implements MessageListener {

    private ConnectionFactory cf;
    private Topic d;
    private Scanner reader;

    public MainRequester() throws NamingException {

        this.cf = InitialContext.doLookup("jms/RemoteConnectionFactory");
        this.d = InitialContext.doLookup("jms/topic/PlayTopic");
        reader = new Scanner(System.in);
    }

    @Override
    public void onMessage(Message msg) {

        TextMessage tmsg = (TextMessage) msg;
        try {
            System.out.println("Got message: " + tmsg.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("all")
    public void launch_and_wait() {

        System.out.println("Escreva o que quer pesquisar: (para sair escreva 'q')");
        String lido = reader.nextLine();
        while(!lido.equals("q")){

            lido = reader.nextLine();
        }

        System.out.println("Bye bye.");


        try (JMSContext jcontext = cf.createContext("tiago", "12")) {
            jcontext.setClientID("keeper");
            JMSConsumer consumer = jcontext.createDurableConsumer(d, "keeper");
            consumer.setMessageListener(this);
            System.out.println("Press enter to finish...");
            System.in.read();
            consumer.close();
        } catch (JMSRuntimeException | IOException re) {
            re.printStackTrace();
        }
    }

    public static void main(String[] args)throws NamingException{

        System.out.print("Sou um requester\n");
        MainRequester r = new MainRequester();
        r.launch_and_wait();
    }
}
