package thread;

import com.mongodb.DBCollection;
import entity.Author;
import entity.Book;
import entity.mongo.AuthorMongo;
import entity.mongo.BookMongo;
import main.UtilsMongo;

import java.util.concurrent.CountDownLatch;

public class AuthorBookInsertThreadMongo extends Thread {

    private int min;
    private int max;

    CountDownLatch latch;

    public AuthorBookInsertThreadMongo(CountDownLatch latch_, int min_, int max_){
        this.latch = latch_;
        this.min = min_;
        this.max = max_;
    }

    public void run() {
        DBCollection collection_author = UtilsMongo.getCollection(Author.AUTHOR);
        DBCollection collection_book = UtilsMongo.getCollection(Book.BOOK);
//        System.out.println("min: " + this.min + "\tmax: "+ max);

        // In ONE thread, insertion follows a "for" loop => items are introduced ordered
        // Here we either add them in increasing or decreasing order
        if (Math.random() > 0.5){
            addCroissant(collection_author, collection_book);
        }else{
            addDecroissant(collection_author, collection_book);
        }

        latch.countDown();
    }

    public void addCroissant(DBCollection collection_author, DBCollection collection_book){
        for (int i = this.min; i <= this.max; ++i){
            insert(collection_author, collection_book, i);
        }
    }

    public void addDecroissant(DBCollection collection_author, DBCollection collection_book){
        for (int i = this.max; i >= this.min; --i){
            insert(collection_author, collection_book, i);
        }
    }

    public void insert(DBCollection collection_author, DBCollection collection_book, int i){
        // Create entities
        AuthorMongo author = AuthorMongo.randomAuthor(i);
        BookMongo book1 = BookMongo.createRandomBook();
        BookMongo book2 = BookMongo.createRandomBook();

        // Add "relations"
        book1.addAuthor(author);
        book2.addAuthor(author);
        author.addBook(book1);
        author.addBook(book2);

        // Insert entities into MongoDB database
        collection_author.insert(AuthorMongo.toDBObject(author));
        collection_book.insert(BookMongo.toDBObject(book1));
        collection_book.insert(BookMongo.toDBObject(book2));
    }
}
