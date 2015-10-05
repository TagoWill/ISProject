package keeper;

import crawler.*;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

/**
 * Created by Tiago on 30/09/2015.
 * Trabalho: Interesacao de Sistemas
 */
public class MainKeeper implements MessageListener, Runnable{

    private ConnectionFactory cf;
    private Topic d;
    private ListOfThings items;

    public MainKeeper(String connactionFactory, String playqueue) throws NamingException {

        items = new ListOfThings();
        this.cf = InitialContext.doLookup(connactionFactory);
        this.d = InitialContext.doLookup(playqueue);
    }

    @Override
    public void onMessage(Message msg) {

        TextMessage tmsg = (TextMessage) msg;
        try {
            System.out.println("Got message: " + tmsg.getText());
            takeInformation(tmsg.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void takeInformation(String text) {
        System.out.println("unmarshal");

        StringReader reader = new StringReader(text);
        keeper.ListOfThings items = JAXB.unmarshal(reader, keeper.ListOfThings.class);

        System.out.println("Oi: "+items.getData().get(0).getName()+" - "+items.getData().get(0).getPrice());
    }

    @SuppressWarnings("all")
    public void launch_and_wait() {

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

    public static void main(String[] args) throws NamingException {

        (new Thread(new MainKeeper("jms/RemoteConnectionFactory", "jms/topic/PlayTopic"))).start();
        //(new Thread(new MainKeeper("jms/RemoteConnectionFactory", "jms/topic/PlayQueue"))).start();
    }

    @Override
    public void run() {
        launch_and_wait();
    }
}
