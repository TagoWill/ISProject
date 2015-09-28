package jmstopic;

/**
 * Created by Tiago on 28/09/2015.
 */
import java.io.IOException;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class Receiver implements MessageListener {
    private ConnectionFactory cf;
    private Destination d;

    public Receiver() throws NamingException {
        this.cf = InitialContext.doLookup("jms/RemoteConnectionFactory");
        this.d = InitialContext.doLookup("jms/topic/PlayTopic");
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

    public void launch_and_wait() {
        try (JMSContext jcontext = cf.createContext("tiago", "12");) {
            JMSConsumer consumer = jcontext.createConsumer(d);
            consumer.setMessageListener(this);
            System.out.println("Press enter to finish...");
            System.in.read();
        } catch (JMSRuntimeException | IOException re) {
            re.printStackTrace();
        }
    }

    public static void main(String[] args) throws NamingException {
        Receiver r = new Receiver();
        r.launch_and_wait();
    }



}