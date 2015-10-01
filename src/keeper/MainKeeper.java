package keeper;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;

/**
 * Created by Tiago on 30/09/2015.
 * Trabalho: Interesacao de Sistemas
 */
public class MainKeeper implements MessageListener{

    private ConnectionFactory cf;
    private Topic d;

    public MainKeeper() throws NamingException {

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

        MainKeeper r = new MainKeeper();
        r.launch_and_wait();
    }
}
