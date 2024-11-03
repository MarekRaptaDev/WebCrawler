import java.sql.*;

public class DBConn {
    //    DBConn - klasę do obsługi bazy danych (SQLite),
//    powinna udostępniać następujące metody: connect, createTables, insertRow, dropTables, disconnect.
    private  Connection conn;
    private  String link;
    public void  showRows(String name, String condition){
        try {
            Statement s = conn.createStatement();
            s.setQueryTimeout(30);
            if (condition.isEmpty())
            {
                condition ="id>=0";
            }
            ResultSet rs = s.executeQuery("Select * from "+name+" Where "+condition+" ;");
            while(rs.next())
            {
                System.out.println("id: " + rs.getInt("id"));
                System.out.println("hyperlink: " + rs.getString("hyperlink"));
                System.out.println("seen: "+ rs.getInt("seen"));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Something went wrong, try again.");
        }

    }
    public  void connect(){
        try{
            if(conn == null|| conn.isClosed())
            {
                conn = DriverManager.getConnection(link);
                System.out.println("Connection with database is open\n");
            }
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Something went wrong, try again.");
        }
    }
    public void createTables(String name, String cols){
        try{

            PreparedStatement s = conn.prepareStatement("CREATE TABLE  "+ name + " (" + cols + ");");

            s.setQueryTimeout(30);
            s.executeUpdate();
            System.out.println("Tabela została dodana \n");
        }catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println("Something went wrong, try again.");
        }
    }
    public void insertRow(String q, String name){
        try {
            PreparedStatement s = conn.prepareStatement("Update "+name+" Set seen = seen + 1 Where hyperlink = ? ");
            s.setString(1,q );
            s.setQueryTimeout(30);
            int rows=s.executeUpdate();
            if(rows == 0){
                PreparedStatement s2 = conn.prepareStatement("Insert into "+name+" (hyperlink) values(?);");
                s2.setString(1,q);
                s2.executeUpdate();
                System.out.println("row added to database\n");
            }else {
               // System.out.println("this link is allready ind database\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Something went wrong, try again.");
        }
    }
    public void dropTables(String name){
        try {
            PreparedStatement s = conn.prepareStatement("DROP TABLE "+name+";");

            s.setQueryTimeout(30);
            s.executeUpdate();
            System.out.println("Table "+name+" dropped\n");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Something went wrong, try again.");

        }
    }
    public void disconect(){
        try{
            if (conn != null && !(conn.isClosed())){
                conn.close();
                System.out.println("Database connection closed\n");
            }
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Something went wrong, try again.");

        }
    }

    public DBConn(String link)
    {
        this.link = "jdbc:sqlite:resources:"+link;
    }
}

