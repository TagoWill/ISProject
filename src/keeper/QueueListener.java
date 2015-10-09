package keeper;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXB;
import java.io.StringReader;
import java.util.Scanner;

/**
 * Created by Tiago on 30/09/2015.
 * Trabalho: Interesacao de Sistemas
 */
public class QueueListener extends Thread implements MessageListener {

    private FatherKeeper pai;
    private ConnectionFactory cf;
    private Destination d;
    private Scanner sc;
    private Object monitor = new Object();

    public QueueListener(FatherKeeper pai) throws NamingException {
        super();
        this.pai = pai;
        sc = new Scanner(System.in);
        this.cf = InitialContext.doLookup("jms/RemoteConnectionFactory");
        this.d = InitialContext.doLookup("jms/queue/PlayQueue");
    }

    @Override
    public void onMessage(Message msg) {

        TextMessage tmsg = (TextMessage) msg;
        //System.out.println("Got message: " + tmsg.getText());
        try {
            System.out.println("[QueueListener]Recebi isto:\nMessagem: " + ((TextMessage) msg).getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
        //takeInformation(tmsg.getText());
    }

    private void takeInformation(String text) {
        System.out.println("[QueueListener]Um requester falou comigo e disse me assim: "+text);
        System.out.println("[QueueListener]Eu vou lhe enviar esta treta: "+pai.getTeste());
    }

    @SuppressWarnings("all")
    public void launch_and_wait() {

        try (JMSContext jcontext = cf.createContext("tiago", "12")) {
            JMSConsumer consumer = jcontext.createConsumer(d);
            consumer.setMessageListener(this);
            this.suspend();
            //sc.next();

            consumer.close();
        } catch (JMSRuntimeException re) {
            re.printStackTrace();
        }
    }

    public void shutdown(){
        //sc.close();
        this.resume();
    }

    @Override
    public void run() {
        launch_and_wait();
    }
}
