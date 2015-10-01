package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Tiago on 28/09/2015.
 * Trabalho: Interesacao de Sistemas
 */
public class WebCrawler {

    public static StringWriter xmltext;

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
                e.printStackTrace();
            }

        }else {
            System.out.println("Ficheiro nao existe");
            processWebPage("http://www.pixmania.pt/telefones/telemovel/smartphone-19883-s.html");
        }
    }

    static String readFile(String path, Charset encoding) throws IOException {

        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static void processWebPage(String link) throws IOException{

        System.out.println("Parsing Site");
        ListOfThings capsula = new ListOfThings();

        Document dom = Jsoup.connect(link).timeout(0).get();

        /*Elements links = dom.select("http://www.pixmania.pt/telefones/telemovel/smartphone-19883-s.html");

        for(Element e: links){
            System.out.println(e.text());
            if (e.text().equals("Smartphone")){
                System.out.print("Encontrei   -  ");
                System.out.println(e.text());
            }
        }*/
        Elements links = dom.select("a[class=imgC]");

        for(Element e: links){
            capsula.getData().add(extractInformation(e.attr("href")));
        }

        try {
            JAXBContext jc = JAXBContext.newInstance(ListOfThings.class);
            Marshaller ms = jc.createMarshaller();
            ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            xmltext = new StringWriter();
            ms.marshal(capsula, xmltext);
            //System.out.print(xmltext);

            int contador = 0;
            boolean deu = false;
            while (!deu && contador < 5) {
                try {
                    System.out.println("Enviar para jmstopic");
                    //SE NAO HOUVER CONNECT HA QUE GRAVAR UM FICHEIRO
                    Sender teste = new Sender();
                    teste.send(xmltext.toString());
                    deu = true;
                } catch (NamingException e) {
                    System.out.println("Falhou envio..");
                    contador++;
                    e.printStackTrace();
                }
            }
            if(!deu){
                writeXmlInFile(xmltext.toString());
            }
        }catch(JAXBException e){
            e.printStackTrace();
        }


    }

    private static void writeXmlInFile(String s) throws IOException{

        System.out.println("Criar ficheiro");
        File file = new File("./src/crawler/smartphones.xml");
        OutputStream out = new FileOutputStream(file);
        out.write(s.getBytes(Charset.forName("UTF-8")));
        out.close();
    }

    private static ListOfThings.Info extractInformation(String url) throws IOException{

        System.out.println("Popular xml");
        ListOfThings.Info item = new ListOfThings.Info();

        Document dompagina = Jsoup.connect(url).timeout(0).get();

        item.setBrand(dompagina.getElementsByClass("pageTitle")
                .get(0).getElementsByAttributeValue("itemprop", "brand").text());

        item.setName(dompagina.getElementsByClass("pageTitle")
                .get(0).getElementsByAttributeValue("itemprop", "name").text());

        item.setPrice(dompagina.getElementsByClass("currentPrice")
                .get(0).getElementsByAttributeValue("itemprop", "price").text());

        dompagina.getElementsByClass("descTxt").text();

        dompagina.getElementsByClass("customList").get(0).getElementsByAttributeValue("itemprop", "description").text();

        //System.out.println(dompagina);

        return item;
    }
}
