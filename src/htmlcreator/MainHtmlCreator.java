package htmlcreator;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;

/**
 * Created by Tiago on 30/09/2015.
 */
public class MainHtmlCreator implements MessageListener {

    private ConnectionFactory cf;
    private Topic d;

    public MainHtmlCreator() throws NamingException {
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
            jcontext.setClientID("htmlcreator");
            JMSConsumer consumer = jcontext.createDurableConsumer(d, "htmlcreator");
            consumer.setMessageListener(this);
            System.out.println("Press enter to finish...");
            System.in.read();
            consumer.close();
        } catch (JMSRuntimeException | IOException re) {
            re.printStackTrace();
        }
    }

    public void createHtml(){
        /*try {
            File stylesheet = new File(argv[0]);
            File datafile = new File(argv[1]);

            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(datafile);
            // ...
            StreamSource stylesource = new StreamSource(stylesheet);
            Transformer transformer = Factory.newTransformer(stylesource);
        }*/
    }
    public static void main(String[] args) throws NamingException {
        MainHtmlCreator r = new MainHtmlCreator();
        r.launch_and_wait();
    }
}
