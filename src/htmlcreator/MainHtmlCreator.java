package htmlcreator;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.XMLConstants;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Daniel Bastos e Tiago Andrade on 30/09/2015.
 * Trabalho: Integracao de Sistemas
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
            //System.out.println("Got message: " + tmsg.getText());
            try {
                createHtml(tmsg.getText());
            } catch (TransformerException | IOException e) {
                e.printStackTrace();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("all")
    public void launch_and_wait() {

        try (JMSContext jcontext = cf.createContext("tiago", "12")) {
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

    public void createHtml(String dataXML) throws TransformerException, IOException {
    	System.out.println("Validando xml com xsd");
    	if(validateXMLSchema(dataXML, "./src/crawler/smartphones.xsd"))
    	{
    		System.out.println("Validou");
    		System.out.println("Transformar em HTML");
            
            File xslStream = new File("./src/htmlcreator/xsl_stylesheet.xsl");
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xslStream));
            StringReader reader = new StringReader(dataXML);
            StreamResult out = new StreamResult("./src/htmlcreator/full_smartphones_list.html");
            transformer.transform(new javax.xml.transform.stream.StreamSource(reader), out);
            System.out.println("The generated HTML file is:" + " ./src/htmlcreator/full_smartphones_list.html");
    	} else{
    		System.out.println("NAO Validou");
    	}
    }

    public static void main(String[] args) throws NamingException {
        MainHtmlCreator r = new MainHtmlCreator();
        r.launch_and_wait();
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
