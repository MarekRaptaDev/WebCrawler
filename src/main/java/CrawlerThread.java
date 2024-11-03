import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CrawlerThread implements Runnable{
    private Elements list;
    private String name;
    public CrawlerThread(Elements list,String name){


        this.list=list;
        this.name=name;
    }
    public  void startCrawler(Elements list,String name) {

        DBConn db = new DBConn(Main.dataBase);
        db.connect();

        for(Element link : list){
            String addr = link.attr("abs:href");
            db.insertRow(addr,name);
        }
        db.disconect();
    }

    @Override
    public void run() {
        startCrawler(list,name);
    }

}
