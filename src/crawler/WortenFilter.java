package crawler;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Daniel Bastos e Tiago Andrade on 14/10/2015.
 * Trabalho: Integracao de Sistemas
 */
public class WortenFilter extends AbstractFilter{



    public WortenFilter(String string){
        try {
            capsula = new ListOfSmartphones();
            processWebPage(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processWebPage(String link) throws IOException {
        try {
            //do {
                Document dom = Jsoup.connect(link).timeout(0).get();
                Elements links = dom.getElementsByClass("product-name");
                for (Element e : links) {
                    capsula.getData().add(extractInformation(e.select("a").attr("href"), "worten"));
                }
                //link = dom.getElementsByClass("next").get(0).attr("href");
           // }while(capsula.getSize()<15);
        } catch (UnknownHostException e) {
            /*File input = new File("./pixmania/index.html");
            Document localdom = Jsoup.parse(input, "UTF-8", "http://pixmania.pt/");
            Elements locallinks = localdom.select("a[class=imgC]");
            for(Element e2: locallinks){
                capsula.getData().add(extractInformation(e2.attr("href"), "Pixmania"));
            }*/
            System.out.println("Nao ha paginas offline");
        }
    }

    @SuppressWarnings("all")
    private ListOfSmartphones.Info extractInformation(String url, String website) throws IOException{

        System.out.println("Popular xml");
        ListOfSmartphones.Info item = new ListOfSmartphones.Info();
        Document dompagina = null;
        try {
            dompagina = Jsoup.connect(url).timeout(0).get();
        } catch (UnknownHostException | IllegalArgumentException e) {
            //File input = new File("./pixmania/"+url);
            //dompagina = Jsoup.parse(input, "UTF-8", "http://pixmania.pt/");
        }


        item.setBrand("");


        item.setName(dompagina.getElementsByClass("product-name").get(0).text().trim());

        //Tem que ser em double nao String..
        String word = dompagina.getElementsByClass("price").get(0).text();


        word=word.replace(',','.');
        Double preco = Double.parseDouble(word.substring(1,word.length()));
        item.setPrice(preco);

        item.setWebsite(website);

        Elements elementos = dompagina.getElementsByClass("data-table");
        for (Element el : elementos) {

            Elements tagth = el.getElementsByTag("th");
            List<Element> category = tagth.subList(0, tagth.size());

            Elements tagtd = el.getElementsByTag("td");
            List<Element> description = tagtd.subList(0, tagtd.size());

            for (int i = 0; i < description.size(); i++) {
                //if(category.get(i).text().equalsIgnoreCase("sistema operativo") || category.get(i).text().equalsIgnoreCase("processador") || category.get(i).text().equalsIgnoreCase("Tamanho do ecrÃ£"))
                item.addInfo(new ListOfSmartphones.ExtraInfo(category.get(i).text().toLowerCase(), description.get(i).text().toLowerCase()));
            }
        }

        return item;
    }
}
