package keeper;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXB;
import java.io.StringReader;

/**
 * Created by Tiago on 30/09/2015.
 * Trabalho: Interesacao de Sistemas
 */
public class TopicListener extends Thread implements MessageListener {

    private FatherKeeper pai;
    private ConnectionFactory cf;
    private Topic d;

    public TopicListener(FatherKeeper pai) throws NamingException {
        super();
        this.pai = pai;
        this.cf = InitialContext.doLookup("jms/RemoteConnectionFactory");
        this.d = InitialContext.doLookup("jms/topic/PlayTopic");
    }

    @Override
    public void onMessage(Message msg) {

        TextMessage tmsg = (TextMessage) msg;
        try {
            //System.out.println("Got message: " + tmsg.getText());
            takeInformation(tmsg.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void takeInformation(String text) {
        System.out.println("[TopicListener]unmarshal");

        StringReader reader = new StringReader(text);
        ListOfThings items = JAXB.unmarshal(reader, ListOfThings.class);

        System.out.println("[TopicListener]Guardar no pai");
        pai.setCapsula(items);
        //System.out.println("Oi: "+items.getData().get(0).getName()+" - "+items.getData().get(0).getPrice());
    }

    @SuppressWarnings("all")
    public void launch_and_wait() {

        try (JMSContext jcontext = cf.createContext("tiago", "12")) {
            jcontext.setClientID("keeper");
            JMSConsumer consumer = jcontext.createDurableConsumer(d, "keeper");
            consumer.setMessageListener(this);
            this.suspend();
            //sc.next();

            consumer.close();
        } catch (JMSRuntimeException re) {
            re.printStackTrace();
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
