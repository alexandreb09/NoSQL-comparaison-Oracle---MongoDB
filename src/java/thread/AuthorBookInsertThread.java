package thread;

import entity.oracle.Author;
import entity.oracle.Book;
import main.Utils;
import oracle.kv.KVStore;

import java.util.concurrent.CountDownLatch;

public class AuthorBookInsertThread extends Thread {

    private int min;
    private int max;

    CountDownLatch latch;

    public AuthorBookInsertThread(CountDownLatch latch_, int min_, int max_){
        this.latch = latch_;
        this.min = min_;
        this.max = max_;
    }

    public void run() {
        KVStore kvStore = Utils.getKvstore();
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
        for (int i = this.min; i < this.max; ++i){
            insert(kvStore, i);
        }
    }

    public void addDecroissant(KVStore kvStore){
        for (int i = this.max; i > this.min; --i){
            insert(kvStore, i);
        }
    }

    public void insert(KVStore kvStore, int i){
        // Create entities
        Author author = Author.randomAuthor(i);
        Book book1 = Book.createRandomBook();
        Book book2 = Book.createRandomBook();

        // Add "relations"
        book1.addAuthor(author);
        book2.addAuthor(author);
        author.addBook(book1);
        author.addBook(book2);

        // Insert entities in Oracle Store
        Utils.addAuthorInKvstore(kvStore, author);
        Utils.addBookInKvstore(kvStore, book1);
        Utils.addBookInKvstore(kvStore, book2);
    }
}
