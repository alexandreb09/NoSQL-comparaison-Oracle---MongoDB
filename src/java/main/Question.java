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
        String author_name;

        // Try creating data
        if (createData()){
            // =============================================================//
            // Perform 1000 requests doing:                                 //
            //      - Generate random author name                           //
            //      - Find all books associated to this author              //
            // =============================================================//
//            long startTime, endTime, duration;
//            startTime = System.nanoTime();
//            for (int i = 0; i < Parameters.NUMBER_REQUEST; ++i){
//                // Create random author name
//                author_name = Utils.generateRandomAuthorName();
//
//                // Perform research
//                ArrayList<Book> books = request_1_1(author_name);
//
//                if (books.size() != 2){
//                    System.out.println("INFO: " + books.size() + " books found for author name: " + author_name);
//                }
//            }
//            endTime = System.nanoTime();
//            duration = (endTime - startTime)/1000000;
//            System.out.println("Time research (1000 requests): " + duration + "ms");
//            System.out.println("Time research per request: " + duration/Parameters.NUMBER_REQUEST + "ms");



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
            author_name = Utils.generateRandomAuthorName();

            request_1_2(author_name);

            // To check, call "request_1_1"
            ArrayList<Book> books =  request_1_1(author_name);
            System.out.println("");
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

        ArrayList<Book> books = new ArrayList<>();

        Iterator<KeyValueVersion> i = Utils.getAuthorIterator(kvStore, author_name);
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

    public static void request_1_2(String author_name){
        KVStore kvStore = Utils.getKvstore();

        Iterator<KeyValueVersion> i = Utils.getAuthorIterator(kvStore, author_name);
        while (i.hasNext()){
            // Get key from iterator
            Key key_author = i.next().getKey();

            // Get string value associated to the key
            String author_str = Utils.getValueFromKey(kvStore, key_author);
            Author author = new Author();
            author.deserialize(author_str);

            // For each books:
            // TODO : when the book is loaded, author are lost (need to search in DB for authors)
            for (Book book: author.getBooks()) {
                Book full_book = Utils.getBookFromTitle(book.getTitle());

                // Generate random Author name (from existing author)
                String new_author_name = Utils.generateRandomAuthorName();
                // Search this new author
                Iterator<KeyValueVersion> j = Utils.getAuthorIterator(kvStore, new_author_name);
                while (j.hasNext()){
                    // Get key from iterator
                    Key key = j.next().getKey();

                    // Get string value associatd to the key
                    String new_author_str = Utils.getValueFromKey(kvStore, key);
                    System.out.println("new_author_name: " + new_author_name);

                    // Add Book at the end of the serialized Author string
                    new_author_str = new_author_str + "¤" + book.serializePartial();

                    // Update value in KVStore
                    Utils.addKeyValue(kvStore, key, new_author_str);

                    // Add this new author in the book
                    full_book.addAuthor(new Author().deserialize(new_author_str));
                }
                // Update Book in KVStore
                // The key isn't change => the previous value is overwritten
                Utils.addBookInKvstore(kvStore, full_book);
            }
        }
    }
}
