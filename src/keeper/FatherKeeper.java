package keeper;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Tiago on 30/09/2015.
 * Trabalho: Interesacao de Sistemas
 * jms/RemoteConnectionFactory", "jms/topic/PlayTopic"
 */
public class FatherKeeper extends Thread{

    private ListOfThings capsula;

    private TopicListener topic;
    private QueueListener queue;

    public FatherKeeper() throws NamingException {
        super();

        topic = new TopicListener(this);
        queue = new QueueListener(this);
        topic.start();
        queue.start();
        this.start();
    }

    @Override
    public void run(){
        this.suspend();
    }

    public synchronized void setCapsula(ListOfThings capsula){
        this.capsula = capsula;
    }

    public String getTeste(){
        if(capsula == null){
            return "nao posso enviar nada";
        }
        return capsula.getData().get(0).getBrand();
    }

    public void shutdown() throws InterruptedException {
        topic.shutdown();
        queue.shutdown();
        this.resume();
    }
}
