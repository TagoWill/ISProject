package crawler;

/**
 * Created by Daniel Bastos e Tiago Andrade on 28/09/2015.
 * Trabalho: Integracao de Sistemas
 */

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.JMSRuntimeException;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class Sender {
    private ConnectionFactory cf;
    private Destination d;

    public Sender() throws NamingException {

        this.cf = InitialContext.doLookup("jms/RemoteConnectionFactory");
        this.d = InitialContext.doLookup("jms/topic/PlayTopic");
    }

    public void send(String text) {

        try (JMSContext jcontext = cf.createContext("tiago", "12")) {
            JMSProducer mp = jcontext.createProducer();
            mp.send(d, text);
        } catch (JMSRuntimeException re) {
            re.printStackTrace();
        }
    }
}
