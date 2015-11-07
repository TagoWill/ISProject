package crawler;

import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by Daniel Bastos e Tiago Andrade on 28/09/2015.
 * Trabalho: Integracao de Sistemas
 */

public class WebCrawler {

    public static StringWriter xmltext;
    public static String WEBSITE_PIXAMANIA = "http://www.pixmania.pt/telefones/telemovel/smartphone-19883-s.html";
    public static String WEBSITE_WORTEN = "https://www.worten.pt/inicio/worten-mobile/smartphones-1.html?cat=7459";
    public static ListOfSmartphones capsula;
    public static Scanner sc;

    public static void main(String[] args) throws IOException{

        File file = new File("./src/crawler/smartphones.xml");
        if(file.exists()){
            System.out.println("Ficheiro existe");
            try {
                System.out.println("Enviar xml já existente");
                Sender teste = new Sender();
                teste.send(readFile("./src/crawler/smartphones.xml", Charset.forName("UTF-8")));
                if(file.delete()){
                    System.out.println("Ficheiro apagado");
                }else{
                    System.out.println("Erro: Fcheiro nao apagado");
                }
            } catch (NamingException e) {
                //e.printStackTrace();
                System.out.println("JMS desligado");
            }

        }else {
            System.out.println("Escolha as opcoes:\n1.Pixmania 2.worten");
            sc = new Scanner(System.in);
            int escolha = sc.nextInt();
            AbstractFilter teste;
            if(escolha == 1) {
                teste = new PixmaniaFilter(WEBSITE_PIXAMANIA);
            }
            else {
                teste = new WortenFilter(WEBSITE_WORTEN);
            }
            capsula = teste.capsula;
            sendProcess();
        }
    }

    private static void sendProcess(){
        try {
            JAXBContext jc = JAXBContext.newInstance(ListOfSmartphones.class);
            Marshaller ms = jc.createMarshaller();
            ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            xmltext = new StringWriter();
            ms.marshal(capsula, xmltext);

            int contador = 0;
            boolean deu = false;
            while (!deu && contador < 5) {
                try {
                    System.out.println("Enviar para jmstopic");
                    Sender teste = new Sender();
                    teste.send(xmltext.toString());
                    deu = true;
                } catch (NamingException e) {
                    System.out.println("Falhou envio.. JMS desligado");
                    contador++;
                    //e.printStackTrace();
                }
            }
            if(!deu){
                writeXmlInFile(xmltext.toString());
            }
        }catch(JAXBException | IOException e){
            e.printStackTrace();
        }
    }

    private static String readFile(String path, Charset encoding) throws IOException {

        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static void writeXmlInFile(String s) throws IOException{

        System.out.println("Criar ficheiro");
        File file = new File("./src/crawler/smartphones.xml");
        OutputStream out = new FileOutputStream(file);
        out.write(s.getBytes(Charset.forName("UTF-8")));
        out.close();
    }

}
