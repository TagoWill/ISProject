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
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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

    private static String readFile(String path, Charset encoding) throws IOException {

        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static void processWebPage(String link) throws IOException{

        System.out.println("Parsing Site");
        ListOfThings capsula = new ListOfThings();

        try {
            do {
                Document dom = Jsoup.connect(link).timeout(0).get();
                Elements links = dom.select("a[class=imgC]");
                for (Element e : links) {
                    System.out.println("Tamanho da lista: " + capsula.getSize());
                    capsula.getData().add(extractInformation(e.attr("href"), "Pixmania"));
                }
                link = dom.getElementsByClass("next").get(0).attr("href");
            }while(capsula.getSize()<15);
        } catch (UnknownHostException e) {
        	System.out.print("cheguei aqui");
        	File input = new File("./pixmania/index.html");
        	Document localdom = Jsoup.parse(input, "UTF-8", "http://pixmania.pt/");
        	Elements locallinks = localdom.select("a[class=imgC]");
        	for(Element e2: locallinks){
                System.out.println("Tamanho da lista: " +capsula.getSize() );
                capsula.getData().add(extractInformation(e2.attr("href"), "Pixmania"));
            }
    	}

        try {
            JAXBContext jc = JAXBContext.newInstance(ListOfThings.class);
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

    private static ListOfThings.Info extractInformation(String url, String website) throws IOException{

        System.out.println("Popular xml");
        ListOfThings.Info item = new ListOfThings.Info();
        Document dompagina;
        try {
        	dompagina = Jsoup.connect(url).timeout(0).get();
        } catch (UnknownHostException | IllegalArgumentException e) {
        	File input = new File("./pixmania/"+url);
        	dompagina = Jsoup.parse(input, "UTF-8", "http://pixmania.pt/");
        }

        item.setBrand(dompagina.getElementsByClass("pageTitle")
                .get(0).getElementsByAttributeValue("itemprop", "brand").text());

        String words[] = dompagina.getElementsByClass("pageTitle")
                .get(0).getElementsByAttributeValue("itemprop", "name").text().split("-");

        String word = words[0];
        item.setName(word);

        //Tem que ser em double nao String..
        word = dompagina.getElementsByClass("currentPrice")
                .get(0).getElementsByAttributeValue("itemprop", "price").text();

        word=word.replace(',','.');
        Double preco = Double.parseDouble(word.substring(0,word.length()-2));
        item.setPrice(preco);

        item.setWebsite(website);

        Elements elementos = dompagina.getElementsByClass("simpleTable");
        for (Element el : elementos) {

            Elements tagth = el.getElementsByTag("th");
            List<Element> category = tagth.subList(0, tagth.size());

            Elements tagtd = el.getElementsByTag("td");
            List<Element> description = tagtd.subList(0, tagtd.size());

            for (int i = 0; i < description.size(); i++) {
                //if(category.get(i).text().equalsIgnoreCase("sistema operativo") || category.get(i).text().equalsIgnoreCase("processador") || category.get(i).text().equalsIgnoreCase("Tamanho do ecrÃ£"))
                    item.addInfo(new ListOfThings.ExtraInfo(category.get(i).text().toLowerCase(), description.get(i).text().toLowerCase()));
            }
        }

        return item;
    }
}
