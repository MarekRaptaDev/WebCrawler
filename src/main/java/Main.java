import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Main {
    static String url="";
    static String dataBase="";
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Scanner s = new Scanner(System.in);
        while (url.isEmpty()) {
            System.out.println("Provide url of website to scan: ");
            url = s.nextLine();
        }
        while (dataBase.isEmpty()) {
            System.out.println("Provide SQLite database name:");
            dataBase = s.nextLine();
        }
        Elements links = null;
        if (args.length == 1) {
            url = args[0];
        }

        DBConn db = new DBConn(dataBase);
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(cores);
        db.connect();
        boolean exit = false;
        do {
            System.out.println("What do you wanna do?\n 1.Drop table \n 2.Insert data in table\n 3.Show data from table \n 4.Start webCrawler\n 5.Create table \n 6.Exit \nRespond: ");
            switch (s.nextInt())
            {
                case 6: {
                    exit = true;
                    break;
                }
                case 5:{
                    System.out.println("Provide table name: ");
                    s.nextLine();
                    String name = s.nextLine();
                    db.createTables(name,"id INTEGER PRIMARY KEY AUTOINCREMENT, hyperlink TEXT NOT NULL, seen BOOLEAN NOT NULL DEFAULT 1");
                    break;
                }
                case 4:
                {
                    System.out.println("Provide table name: ");
                    s.nextLine();
                    String name = s.nextLine();
                    try {
                        Document doc = Jsoup.connect(url).get();
                        links = doc.select("a[href]");
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    for (int i = 0 ; i< cores;i++)
                    {
                        int start = i * links.size()/cores;
                        int end = (i == cores - 1) ? links.size() : start + (links.size()/cores);
                        Runnable crawler = new CrawlerThread(new Elements(links.subList(start,end)),name);
                        service.execute(crawler);
                    }
                    service.shutdown();
                    while (true) {
                        try {
                            if (service.awaitTermination(1, TimeUnit.SECONDS)) {
                                break;
                            }
                        } catch (InterruptedException e) {
                            service.shutdownNow();
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                break;
                } case 3:{
                System.out.println("Provide table name: ");
                s.nextLine();
                String name = s.nextLine();
                System.out.println("Provide searching arguments: ");
                String arg = s.nextLine();
                db.showRows(name,arg);
                break;
            } case 2:{
                s.nextLine();
                System.out.println("Provide hyperlink: ");
                String hyperlink = s.nextLine();
                System.out.println("Provide table name: ");
                String name = s.nextLine();
                db.insertRow(hyperlink,name);
                break;
            } case 1:
            {
                System.out.println("Provide table name: ");
                s.nextLine();
                String name = s.nextLine();
                db.dropTables(name);
                break;
            } default:
            {
                System.out.println("Not valuable answer try again \n ");
                break;
            }

            }

        }while (!exit);








        db.disconect();

    }


}