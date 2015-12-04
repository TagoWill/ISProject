package keeper;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Created by Daniel Bastos e Tiago Andrade on 09/10/2015.
 * Trabalho: Integracao de Sistemas
 */

public class Responder extends Thread{
    private FatherKeeper pai;
    private ConnectionFactory cf;
    private Message dst;

    public Responder(FatherKeeper pai, Message dst) throws NamingException, JMSException {
        super();
        this.pai = pai;
        this.cf = InitialContext.doLookup("jms/RemoteConnectionFactory");
        this.dst = dst;
        //this.d = InitialContext.doLookup(dst.getJMSReplyTo());
    }

    @SuppressWarnings("all")
    public void launch_and_wait() {
        // Vai buscar a resposta ao pedido ao Father Keeper e envia-a para o resquester através do buffer de resposta do pedido.
        try (Connection connection = cf.createConnection("tiago", "12")) {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer mp = session.createProducer(null);
            TextMessage message = session.createTextMessage();
            message.setText(search(((TextMessage) dst).getText()));
            mp.send(dst.getJMSReplyTo(), message);
            System.out.println("[Resposta] enviado");

            mp.close();
            session.close();
            connection.close();
        } catch (JMSRuntimeException re) {
            re.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private String search(String pesquisa){
        // Vai buscar o resultado do pedido ao Father Keeper.
        return pai.getSearch(pesquisa);
    }

    @Override
    public void run() {
        System.out.println("[Resposta] cheguei aqui");
        launch_and_wait();
    }
}
