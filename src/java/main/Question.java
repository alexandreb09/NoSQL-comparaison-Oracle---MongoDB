package main;

import entity.oracle.Author;
import entity.oracle.Book;
import oracle.kv.*;
import thread.AuthorBookInsertThread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

// In all the question1, we want to optimize the research of
public class Question {

    public static void requete_1(){
        // Try creating data
        if (createData()){
            // =============================================================//
            // Perform 1000 requests doing:                                 //
            //      - Generate random author name                           //
            //      - Find all books associated to this author              //
            // =============================================================//
            long startTime, endTime, duration;
            startTime = System.nanoTime();
            for (int i = 0; i < Parameters.NUMBER_REQUEST; ++i){
                // Create random author name
                String author_name = Utils.generateRandomAuthorName();

                // Perform research
                ArrayList<Book> books = request_1_1(author_name);

                if (books.size() != 2){
                    System.out.println("INFO: " + books.size() + " books found for author name: " + author_name);
                }
            }
            endTime = System.nanoTime();
            duration = (endTime - startTime)/1000000;
            System.out.println("Time research (1000 requests): " + duration + "ms");
            System.out.println("Time research per request: " + duration/Parameters.NUMBER_REQUEST + "ms");



            // =============================================================//
            // Perform 1000 requests doing:                                 //
            //      - Generate random author name                           //
            //      - Find all books written to this author                 //
            //      - For each of these books:                              //
            //              - Generate a random integer                     //
            //              - Find associated author                        //
            //              - Add relation                                  //
            // =============================================================//
            // Create random author name
            String auteur_name = Utils.generateRandomAuthorName();


        }
    }

    public static boolean createData(){
        CountDownLatch latch = new CountDownLatch(Parameters.NB_THREAD_CREATION);

        // Creating 10 threads and insert DATA
        for (int i = 0; i < Parameters.NB_THREAD_CREATION; ++i){
            AuthorBookInsertThread thread = new AuthorBookInsertThread(latch, Parameters.NB_AUTHOR_PER_THREAD*i,
                    Parameters.NB_AUTHOR_PER_THREAD*(i+1));
            thread.start();
        }

        // Wait for threads finished
        boolean success = true;
        try {
            latch.await();
            System.out.println("Creation finished !");
        } catch (InterruptedException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public static ArrayList<Book> request_1_1(String author_name){
        KVStore kvStore = Utils.getKvstore();

        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(Author.AUTHOR);
        tab_key.add(author_name);
        Key myKey = Key.createKey(tab_key);
        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);

        ArrayList<Book> books = new ArrayList<>();

        while (i.hasNext()){
            // Get key from iterator
            Key k = i.next().getKey();

            // Get string value associatd to the key
            String str_value = Utils.getValueFromKey(kvStore, k);
            Author author = new Author();
            author.deserialize(str_value);

            books = author.getBooks();
        }

        return books;
    }

    public void request_1_2(String author_name){
        KVStore kvStore = Utils.getKvstore();

        ArrayList<Book> books = new ArrayList<>();

        Iterator<KeyValueVersion> i = getAuthorIterator(kvStore, author_name);
        while (i.hasNext()){
            // Get key from iterator
            Key k = i.next().getKey();

            // Get string value associatd to the key
            String str_value = Utils.getValueFromKey(kvStore, k);
            Author author = new Author();
            author.deserialize(str_value);

            books = author.getBooks();

            for (Book book: books) {
                // Generate random Author name (from existing author)
                String new_author_name = Utils.generateRandomAuthorName();
                // Search this author
                Iterator<KeyValueVersion> j = getAuthorIterator(kvStore, author_name);
                while (j.hasNext()){
                    // Get key from iterator
                    Key key = j.next().getKey();

                    // Get string value associatd to the key
                    String str_author = Utils.getValueFromKey(kvStore, k);
                    Author new_author = new Author();
                    new_author.deserialize(str_value);

                    // TODO : update the value
                    //  Author : add new Book
                    //  Book : add new Author

                }
            }
        }
    }


    public static Iterator<KeyValueVersion> getAuthorIterator(KVStore kvStore, String author_name){
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(Author.AUTHOR);
        tab_key.add(author_name);
        Key myKey = Key.createKey(tab_key);
        return kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
    }
}