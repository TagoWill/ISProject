package keeper;

import crawler.ListOfSmartphones;

import javax.jms.*;
import javax.naming.NamingException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Daniel Bastos e Tiago Andrade on 30/09/2015.
 * Trabalho: Integracao de Sistemas
 */

public class FatherKeeper extends Thread{

    private ListOfSmartphones capsula;

    private TopicListener topic;
    private QueueListener queue;
    private boolean exit = false;
    private boolean shuttingDown = false;
    private List<TextMessage> msglist;

    public FatherKeeper() throws NamingException {
        // Cria 3 Threads: Ele próprio, a Topic e a Queue para estarem constantemente a tratar de informacao.
        super();
        msglist = new LinkedList<>();
        topic = new TopicListener(this);
        queue = new QueueListener(this);
        topic.start();
        queue.start();
        this.start();
    }

    @Override
    public void run(){
        while(!exit){
            //this.suspend();

            synchronized (this) {
                if (shuttingDown) {
                    try {
                        System.out.println("Start shutting down...");
                        shutdown();
                        exit = true;
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }
            }
            // Verifica se existem pedidos a tratar. Se existirem entao cria uma thread Responder para ser enviada a resposta.
            if(msglist.size() > 0 ){
                //System.out.println("[PAI] recebi isto: "+msglist.size()+" mensagem "+msglist.remove(0));
                Responder rp;
                try {
                    rp = new Responder(this, msglist.remove(0));
                    rp.start();
                } catch (NamingException | JMSException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public synchronized void setCapsula(ListOfSmartphones capsula){
        this.capsula = capsula;
    }

    public String getSearch(String pesquisa){
        // Pedido de pesquisa recebido por um requester que vai ser tratado através da
        // chamada do método getSmartphone do objecto capsula, que é do tipo ListOfSmartphones
        // com a string pesquisa que contém o input do resquester
        System.out.println("[PAI] vou pesquisar por " + pesquisa);
        if(capsula == null){
            return "O crawler nunca correu";
        }
        return capsula.getSmartphone(pesquisa);
    }

    public void setshutdown(){
        this.shuttingDown = true;
    }

    public void shutdown() throws InterruptedException {
        topic.shutdown();
        queue.shutdown();
        exit = true;
        //this.resume();
    }

    public synchronized void addMessagem(TextMessage msg){
        // Adiciona pedido do resquester à lista de pedidos a responder.
        this.msglist.add(msg);
        System.out.println("[PAI] Size: "+msglist.size());
    }
}
