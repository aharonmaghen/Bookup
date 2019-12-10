package bookup;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.*;

/** Provides helper methods for interacting with the database */
class SQLHelper {

    /** Adds specified book to database */
    static void add(Book book) {
        Connection con = connect();
        try {
            Statement statement = con.createStatement();
            String SQLAddStatement = String.format("insert into books values(%s, '%s', '%s', %s, %s, %s);", book.getISBN(), book.getTitle(), book.getAuthor(),book.getPages(), book.getYear(), book.getImageURL());
            statement.executeUpdate(SQLAddStatement);
            con.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /** Returns a Book with the specified isbn or null if the book does not exist in the database */
    static Book getBook(String isbn) {
        Connection con = connect();
        ResultSet results;
        Book book = null;
        try {
            Statement statement = con.createStatement();
            results = statement.executeQuery(String.format("select * from books where isbn = %s;", isbn));
            book = createBook(results);
            con.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return book;
    }

    /** Returns a List of books by the specified author */
    static List<Book> getBooks(Author author){
        Connection con = connect();
        ResultSet results = null;
        List<Book> list = null;
        try {
            Statement statement = con.createStatement();
            results = statement.executeQuery(String.format("select * from books where author = '%s';", author.getName()));
            if (results == null) { return null; }
            list = new ArrayList<>();
            while(results.next()) {
                list.add(createBook(results));
            }
            con.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    /** Creates a Book object form the first row of a ResultSet */
    private static Book createBook(ResultSet results) {
        if (results == null) return null;
        Book book = null;
        try {
            if (results.isBeforeFirst()) results.next();
            book = new Book(results.getString(1));
            book.setTitle(results.getString(2));
            book.setAuthor(new Author(results.getString(3)));
            book.setPages(results.getInt(4));
            book.setYear(results.getInt(5));
            book.setImageURL(results.getURL(6));
        } catch (SQLException e) {
            System.out.println(e);
        }
        return book;
    }

    /** Returns a Connection that connects to database */
    private static Connection connect() {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BookDatabase?useSSL=false", "root", "");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
        return con;
    }
}