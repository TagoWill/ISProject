package keeper;

import crawler.ListOfSmartphones;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Daniel Bastos e Tiago Andrade on 30/09/2015.
 * Trabalho: Integracao de Sistemas
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

    private void takeInformation(String dataXML) {
       
    	System.out.println("[TopicListener]Validando xml com xsd");
    	if(validateXMLSchema(dataXML, "./src/crawler/smartphones.xsd"))
    	{
    		System.out.println("[TopicListener]Validou");
    		System.out.println("[TopicListener]Unmarshal");
            StringReader reader = new StringReader(dataXML);
            ListOfSmartphones items = JAXB.unmarshal(reader, ListOfSmartphones.class);
            System.out.println("[TopicListener]Guardar no pai");
            pai.setCapsula(items);
            //System.out.println("Oi: "+items.getData().get(0).getName()+" - "+items.getData().get(0).getPrice());
    	} else{
    		System.out.println("NAO Validou");
    	}
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
    
    public static boolean validateXMLSchema(String xml, String xsd){ 
        try {
            SchemaFactory factory = 
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xml)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
        return true;
    }
}
