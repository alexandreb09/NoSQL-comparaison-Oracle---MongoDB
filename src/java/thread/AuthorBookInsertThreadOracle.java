package thread;

import entity.oracle.AuthorOracle;
import entity.oracle.BookOracle;
import main.UtilsOracle;
import oracle.kv.KVStore;

import java.util.concurrent.CountDownLatch;

public class AuthorBookInsertThreadOracle extends Thread {

    private int min;
    private int max;

    CountDownLatch latch;

    public AuthorBookInsertThreadOracle(CountDownLatch latch_, int min_, int max_){
        this.latch = latch_;
        this.min = min_;
        this.max = max_;
    }

    public void run() {
        KVStore kvStore = UtilsOracle.getKvstore();
//        System.out.println("min: " + this.min + "\tmax: "+ max);

        // In ONE thread, insertion follows a "for" loop => items are introduced ordered
        // Here we either add them in increasing or decreasing order
        if (Math.random() > 0.5){
            addCroissant(kvStore);
        }else{
            addDecroissant(kvStore);
        }

        latch.countDown();
    }

    public void addCroissant(KVStore kvStore){
        for (int i = this.min; i <= this.max; ++i){
            insert(kvStore, i);
        }
    }

    public void addDecroissant(KVStore kvStore){
        for (int i = this.max; i >= this.min; --i){
            insert(kvStore, i);
        }
    }

    public void insert(KVStore kvStore, int i){
        // Create entities
        AuthorOracle author = AuthorOracle.randomAuthor(i);
        BookOracle book1 = BookOracle.createRandomBook();
        BookOracle book2 = BookOracle.createRandomBook();

        // Add "relations"
        book1.addAuthor(author);
        book2.addAuthor(author);
        author.addBook(book1);
        author.addBook(book2);

        // Insert entities in Oracle Store
        UtilsOracle.addAuthorInKvstore(kvStore, author);
        UtilsOracle.addBookInKvstore(kvStore, book1);
        UtilsOracle.addBookInKvstore(kvStore, book2);
    }
}
