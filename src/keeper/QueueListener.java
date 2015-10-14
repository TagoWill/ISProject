package keeper;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Created by Daniel Bastos e Tiago Andrade on 30/09/2015.
 * Trabalho: Integracao de Sistemas
 */

public class QueueListener extends Thread implements MessageListener {

    private FatherKeeper pai;
    private ConnectionFactory cf;
    private Destination d;

    public QueueListener(FatherKeeper pai) throws NamingException {
        super();
        this.pai = pai;
        this.cf = InitialContext.doLookup("jms/RemoteConnectionFactory");
        this.d = InitialContext.doLookup("jms/queue/PlayQueue");
    }

    @Override
    public void onMessage(Message msg) {

        TextMessage tmsg = (TextMessage) msg;
        //System.out.println("Got message: " + tmsg.getText());
        try {
            System.out.println("[QueueListener]Recebi isto:\n---Messagem: " + ((TextMessage) msg).getText());
            pai.addMessagem(tmsg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        //takeInformation(tmsg.getText());
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
            System.out.println("[QueueListener]JMS desligado");
            //re.printStackTrace();
        }
    }

    @SuppressWarnings("all")
    public void shutdown(){
        //sc.close();
        this.resume();
    }

    @Override
    public void run() {
        launch_and_wait();
    }
}
