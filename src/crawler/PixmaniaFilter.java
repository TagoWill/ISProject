package crawler;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by tagow on 14/10/2015.
 */
public class PixmaniaFilter extends AbstractFilter{

    public PixmaniaFilter(String string){
        try {
            capsula = new ListOfSmartphones();
            processWebPage(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processWebPage(String link) throws IOException {
        try {
            do {
                Document dom = Jsoup.connect(link).timeout(0).get();
                Elements links = dom.select("a[class=imgC]");
                for (Element e : links) {
                    capsula.getData().add(extractInformation(e.attr("href"), "Pixmania"));
                }
                link = dom.getElementsByClass("next").get(0).attr("href");
            }while(capsula.getSize()<15);
        } catch (UnknownHostException e) {
            File input = new File("./pixmania/index.html");
            Document localdom = Jsoup.parse(input, "UTF-8", "http://pixmania.pt/");
            Elements locallinks = localdom.select("a[class=imgC]");
            for(Element e2: locallinks){
                capsula.getData().add(extractInformation(e2.attr("href"), "Pixmania"));
            }
        }
    }

    private ListOfSmartphones.Info extractInformation(String url, String website) throws IOException{

        System.out.println("Popular xml");
        ListOfSmartphones.Info item = new ListOfSmartphones.Info();
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
        //item.setName(word.trim());
        item.setName(dompagina.getElementsByClass("pageTitle")
                .get(0).getElementsByAttributeValue("itemprop", "name").text().trim());

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
                //if(category.get(i).text().equalsIgnoreCase("sistema operativo") || category.get(i).text().equalsIgnoreCase("processador") || category.get(i).text().equalsIgnoreCase("Tamanho do ecrã"))
                item.addInfo(new ListOfSmartphones.ExtraInfo(category.get(i).text().toLowerCase(), description.get(i).text().toLowerCase()));
            }
        }

        return item;
    }
}
