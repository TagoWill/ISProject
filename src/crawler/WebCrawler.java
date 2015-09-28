package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;

/**
 * Created by Tiago on 28/09/2015.
 */
public class WebCrawler {


    public static void main(String[] args) throws IOException{

        processWebPage("http://www.pixmania.pt/telefones/telemovel/smartphone-19883-s.html");
    }

    private static void processWebPage(String link) throws IOException{

        ListOfThings capsula = new ListOfThings();

        Document dom = Jsoup.connect(link).get();

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
            ms.marshal(capsula, System.out);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

    private static ListOfThings.Info extractInformation(String url) throws IOException{
        ListOfThings.Info item = new ListOfThings.Info();

        Document dompagina = Jsoup.connect(url).get();

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
