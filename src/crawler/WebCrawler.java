package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Tiago on 28/09/2015.
 */
public class WebCrawler {


    public static void main(String[] args) throws IOException{

        processWebPage("http://www.pixmania.pt/telefones/telemovel/smartphone-19883-s.html");
    }

    private static void processWebPage(String link) throws IOException{

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
            extractInformation(e.attr("href"));
        }

    }

    private static void extractInformation(String url) throws IOException{
        Document dompagina = Jsoup.connect(url).get();

        dompagina.getElementsByClass("pageTitle")
                .get(0).getElementsByAttributeValue("itemprop", "brand").text();

        dompagina.getElementsByClass("pageTitle")
                .get(0).getElementsByAttributeValue("itemprop", "name").text();

        dompagina.getElementsByClass("currentPrice")
                .get(0).getElementsByAttributeValue("itemprop", "price").text();

        dompagina.getElementsByClass("descTxt").text();

        System.out.println(dompagina.getElementsByClass("customList").get(0).getElementsByAttributeValue("itemprop", "description").text());

        //System.out.println(dompagina);
    }
}
