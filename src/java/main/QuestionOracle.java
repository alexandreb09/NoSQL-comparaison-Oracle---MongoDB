package main;

import entity.Book;
import entity.Category;
import entity.oracle.*;
import oracle.kv.*;
import thread.AuthorBookInsertThreadOracle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// In all the question1, we want to optimize the research of
public class QuestionOracle {

    // =============================================================//
    //  Populate the database:                                      //
    //      - Create several thread which create a series of BOOK   //
    //        and AUTHOR                                            //
    //      - Each Author wrote 2 books                             //
    // The number of books and authors is provided in               //
    // parameter file                                               //
    // =============================================================//
    public static boolean createData(){
        CountDownLatch latch = new CountDownLatch(Parameters.NB_THREAD_CREATION);

        // Creating 10 threads and insert DATA
        for (int i = 0; i < Parameters.NB_THREAD_CREATION; ++i){
            AuthorBookInsertThreadOracle thread = new AuthorBookInsertThreadOracle(latch, Parameters.NB_AUTHOR_PER_THREAD*i,
                    Parameters.NB_AUTHOR_PER_THREAD*(i+1));
            thread.start();
        }

        // Wait for threads finished
        boolean success = true;
        try {
            latch.await();
            System.out.println("Creation finished !");
            System.out.println(Utils.getSeparatorLine());
        } catch (InterruptedException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }


    // =============================================================//
    // Operations:                                                  //
    //      - Generate random author name                           //
    //      - Find all books associated to this author              //
    // =============================================================//
    public static ArrayList<BookOracle> request_1_1(String author_name){
        KVStore kvStore = UtilsOracle.getKvstore();

        ArrayList<BookOracle> books = new ArrayList<>();

        Iterator<KeyValueVersion> i = UtilsOracle.getAuthorIterator(kvStore, author_name);
        while (i.hasNext()){
            // Get key from iterator
            Key k = i.next().getKey();

            // Get string value associatd to the key
            String str_value = UtilsOracle.getValueFromKey(kvStore, k);
            AuthorOracle author = new AuthorOracle();
            author.deserialize(str_value);

            books = author.getBooks();
        }

        return books;
    }


    // =============================================================//
    // Operations:                                                  //
    //      - Generate random author name                           //
    //      - Find all books written to this author                 //
    //      - For each of these books:                              //
    //              - Generate a random integer                     //
    //              - Find associated author                        //
    //              - Add relation                                  //
    // =============================================================//
    public static void request_1_2(String author_name){
        KVStore kvStore = UtilsOracle.getKvstore();

        Iterator<KeyValueVersion> i = UtilsOracle.getAuthorIterator(kvStore, author_name);
        while (i.hasNext()){
            // Get key from iterator
            Key key_author = i.next().getKey();

            // Get string value associated to the key
            String author_str = UtilsOracle.getValueFromKey(kvStore, key_author);
            AuthorOracle author = new AuthorOracle();
            author.deserialize(author_str);

            // For each books:
            for (BookOracle book: author.getBooks()) {
                // Need to load the full Book object
                // The book stored in an author doesn't have the current list of authors (recursive definition)
                BookOracle full_book = UtilsOracle.getBookFromTitle(book.getTitle());

                // Generate random Author name (from existing author)
                String new_author_name = Utils.generateRandomAuthorName();
                // Search this new author
                Iterator<KeyValueVersion> j = UtilsOracle.getAuthorIterator(kvStore, new_author_name);
                while (j.hasNext()){
                    // Get key from iterator
                    Key key = j.next().getKey();

                    // Get string value associatd to the key
                    String new_author_str = UtilsOracle.getValueFromKey(kvStore, key);
//                    System.out.println("new_author_name: " + new_author_name);

                    // Add Book at the end of the serialized Author string
                    new_author_str = new_author_str + "¤" + book.serializePartial();

                    // Update value in KVStore
                    UtilsOracle.addKeyValue(kvStore, key, new_author_str);

                    // Add this new author in the book
                    full_book.addAuthor(new AuthorOracle().deserialize(new_author_str));
                }
                // Update Book in KVStore
                // The key isn't change => the previous value is overwritten
                UtilsOracle.addBookInKvstore(kvStore, full_book);
            }
        }
    }


    // =============================================================//
    // Operations:                                                  //
    //      - Search all books with:                                //
    //          - "A" in the title                                  //
    //          - price bellow 10                                   //
    //      For all of these books:                                 //
    //          - Increase price (10%)                              //
    // =============================================================//
    public static void request_1_3(String letter, int price){
        KVStore kvStore = UtilsOracle.getKvstore();

        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(BookOracle.BOOK);
        tab_key.add(letter);
        tab_key.add(Integer.toString(Book.priceRangeCompute(price)));
        Key myKey = Key.createKey(tab_key);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
        while (i.hasNext()){
            Key key_book = i.next().getKey();

            // Get string value associated to the key
            String book_str = UtilsOracle.getValueFromKey(kvStore, key_book);

            // Create book instance from string
            BookOracle book = new BookOracle();
            book.deserialize(book_str);

            // ===== Update price ===== //
            // Save previous price
            Key old_key = book.getKey();

            // Update price: +10%
            book.setPrice(Double.toString(Double.parseDouble(book.getPrice()) * 1.1));

            // If both price are different => the key is different
            // Need to delete the old Book entity
            if (!old_key.equals(book.getKey())){
                UtilsOracle.cleanOneKey(old_key);
            }
            // ===== END Update price ===== //

            // Update Book in KVStore
            // The previous value is overwritten
            UtilsOracle.addBookInKvstore(kvStore, book);
        }
    }

    // =============================================================//
    // Operations:                                                  //
    //      - For all books:                                        //
    //          - Randomly select a category                        //
    //          - Add category to the book                          //
    //          - Add the book to the list of the given categories  //
    //          - Add the author to the list of the given           //
    //            categories                                        //
    // =============================================================//
    public static void addCategoryAllBooks(){
        KVStore kvStore = UtilsOracle.getKvstore();

        // Get iterator on all books
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(BookOracle.BOOK);
        Key myKey = Key.createKey(tab_key);
        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);

        while (i.hasNext()) {
            // Get key from iterator
            Key key_book = i.next().getKey();

            // Get Book entity
            String book_str = UtilsOracle.getValueFromKey(kvStore, key_book);
            BookOracle book = new BookOracle();
            book.deserialize(book_str);

            // Generate random Category
            String category = Category.getRandomCategory();

            book.setCategory(category);

            // ====== ADD the BOOK in list of books for a given catgory and letter ====== //
            CategoryBookOracle.addToKvStore(kvStore, book);

            for (AuthorOracle author: book.getAuthors()){
                CategoryAuthorOracle.addToKvStore(kvStore, author, category);
            }
        }
    }

    // =============================================================//
    // Operations:                                                  //
    //      - Select a random Category                              //
    //      - Get all books of this category                        //
    // =============================================================//
    public static ArrayList<BookOracle> request_2_1(String category){
        // List of books (output)
        ArrayList<BookOracle> books = new ArrayList<>();

        KVStore kvStore = UtilsOracle.getKvstore();

        // Create KEY
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(Category.CATEGORY);
        tab_key.add(BookOracle.BOOK);
        tab_key.add(category);
        Key myKey = Key.createKey(tab_key);

        // Get iterator
        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
        // For all books
        while (i.hasNext()) {
            // Get value from the kay
            Key key_book = i.next().getKey();
            String book_str = UtilsOracle.getValueFromKey(kvStore, key_book);

            // Create Book instance and add it to the list of books
            BookOracle book = new BookOracle();
            book.deserialize(book_str);
            books.add(book);
        }
        return books;
    }

    // =============================================================//
    // Operations:                                                  //
    //      - Select a random Category                              //
    //      - Get all authors that have written a book from this    //
    //        category                                              //
    // =============================================================//
    public static ArrayList<AuthorOracle> request_2_2(String category){
        // List of books (output)
        ArrayList<AuthorOracle> authors = new ArrayList<>();

        KVStore kvStore = UtilsOracle.getKvstore();

        // Create KEY
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(Category.CATEGORY);
        tab_key.add(AuthorOracle.AUTHOR);
        tab_key.add(category);
        Key myKey = Key.createKey(tab_key);

        // Get iterator
        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
        // For all books
        while (i.hasNext()) {
            // Get value from the kay
            Key key_book = i.next().getKey();
            String author_str = UtilsOracle.getValueFromKey(kvStore, key_book);

            // Create Book instance and add it to the list of books
            AuthorOracle author = new AuthorOracle();
            author.deserialize(author_str);
            authors.add(author);
        }
        return authors;
    }

    // =============================================================//
    // Operations:                                                  //
    //      - Select a random Category                              //
    //      - Select all books from this category and having a      //
    //      "B" in its title                                        //
    // =============================================================//
    public static ArrayList<BookOracle> request_2_3(String category, String letter){
        // List of books (output)
        ArrayList<BookOracle> books = new ArrayList<>();

        KVStore kvStore = UtilsOracle.getKvstore();

        // Create KEY
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(Category.CATEGORY);
        tab_key.add(BookOracle.BOOK);
        tab_key.add(category);
        tab_key.add(letter);
        Key myKey = Key.createKey(tab_key);

        // Get iterator
        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
        // For all books
        while (i.hasNext()) {
            // Get value from the kay
            Key key_book = i.next().getKey();
            String book_str = UtilsOracle.getValueFromKey(kvStore, key_book);

            // Create Book instance and add it to the list of books
            BookOracle book = new BookOracle();
            book.deserialize(book_str);
            books.add(book);
        }
        return books;
    }


    // =============================================================//
    // Operations:                                                  //
    //      - Select a random number N between 1 and 10             //
    //      - Wait for a random time between 0.01 and 0.5s          //
    //      - Insert N authors in database                          //
    //           - Each author write to new books                   //
    //      - Generate random author name                           //
    //      - Search all books of this author                       //
    //      - Search all books from History category and with a     //
    //        "B" in the title                                      //
    // =============================================================//
    public static int request_3(int n_authors){
        KVStore kvStore = UtilsOracle.getKvstore();

        // Random sleep time
        try {
            int delay_time = Utils.getRandomInteger(Parameters.SLEEPING_TIME_MIN, Parameters.SLEEPING_TIME_MAX);
            TimeUnit.MILLISECONDS.sleep(delay_time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create N authors
        for (int i = 0; i < Utils.getRandomInteger(1,10); i++) {
            // Create random Authors and Books
            AuthorOracle author = AuthorOracle.randomAuthor(n_authors);
            BookOracle book1 = BookOracle.createRandomBook();
            BookOracle book2 = BookOracle.createRandomBook();

            // Add relation between
            author.addBook(book1);
            author.addBook(book2);
            book1.addAuthor(author);
            book2.addAuthor(author);

            // Update BOOKS and AUTHORS in KVstore
            UtilsOracle.addAuthorInKvstore(kvStore, author);
            UtilsOracle.addBookInKvstore(kvStore, book1);
            UtilsOracle.addBookInKvstore(kvStore, book2);

            // Add author for categoryAuthor:
            // There are two insertion if the categories are differents
            CategoryAuthorOracle.addToKvStore(kvStore, author, book1.getCategory());
            // If the categories of the two books are different: 2 insertions (one for each categories)
            if (! book1.getCategory().equals(book2.getCategory())){
                CategoryAuthorOracle.addToKvStore(kvStore, author, book2.getCategory());
            }

            // Add book for categoryBook
            CategoryBookOracle.addToKvStore(kvStore, book1);
            CategoryBookOracle.addToKvStore(kvStore, book2);

            n_authors++;
        }

        String author_name = "Author_" + Utils.getRandomInteger(1, n_authors);

        request_1_1(author_name);

        request_2_3(Category.getRandomCategory(), Arrays.asList("A", "B", "C").get(new Random().nextInt(3)));

        return n_authors;
    }



    // =============================================================//
    // Delete one author from all the kvstore                       //
    //      - Delete author from "Author" table                     //
    //      - For all book (written by this author):                //
    //          - If the author is the only writter:                //
    //              -> delete the book                              //
    //          - Else:                                             //
    //              -> remove the author and update DB              //
    //              -> Delete the book from the "Book categrory"    //
    //      - Delete the author from the "Author category"          //
    // =============================================================//
    public static void request_4_1(String author_name){
        KVStore kvStore = UtilsOracle.getKvstore();

        // Clean Author from Author table
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(AuthorOracle.AUTHOR);
        tab_key.add(author_name);
        Key myKey = Key.createKey(tab_key);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
        while (i.hasNext()){
            Key author_key = i.next().getKey();

            AuthorOracle author = new AuthorOracle().deserialize(UtilsOracle.getValueFromKey(kvStore, author_key));

            // Remove author from the books
            // If he's the only author, delete the book
            for (BookOracle book: author.getBooks()){
                // Get the book value from kvstore
                Key key_book = book.getKey();
                String book_str = UtilsOracle.getValueFromKey(kvStore, key_book);

                // Load full book from database to get the list of authors
                BookOracle book_full = new BookOracle();
                book_full.deserialize(book_str);

                // If there are several authors
                if (book_full.getAuthors().size() > 1){
                    // Remove the author from the list of authors
                    book_full.removeAuthor(author);

                    // Update book in kvstore
                    UtilsOracle.addBookInKvstore(kvStore, book_full);
                }else{
                    // Delete book
                    kvStore.delete(key_book);

                    // Delete book from CategoryBook "table"
                    Iterator<KeyValueVersion> j = UtilsOracle.getCategoryBookIterator(kvStore);
                    while (j.hasNext()){
                        Key categoryKey = j.next().getKey();
                        String categoryStr = UtilsOracle.getValueFromKey(kvStore, categoryKey);

                        CategoryBookOracle categoryBook = new CategoryBookOracle();
                        categoryBook.deserialize(categoryStr);

                        // Remove the book from title
                        categoryBook.removeBook(book_full);

                        // If there are no more books
                        if (categoryBook.getBooks().size() == 0){
                            kvStore.delete(categoryKey);
                        }else{
                            // Update changes in database
                            UtilsOracle.addKeyValue(kvStore, categoryKey, categoryBook.serialize());
                        }
                    }
                }
            }

            // Delete author from CategoryAuthor "table"
            Iterator<KeyValueVersion> j = UtilsOracle.getCategoryBookIterator(kvStore);
            while (j.hasNext()){
                Key categoryKey = j.next().getKey();
                String categoryStr = UtilsOracle.getValueFromKey(kvStore, categoryKey);

                CategoryAuthorOracle categoryAuthor = new CategoryAuthorOracle();
                categoryAuthor.deserialize(categoryStr);

                // Remove the book from title
                categoryAuthor.removeAuthor(author);

                // If there are no more books
                if (categoryAuthor.getAuthor().size() == 0){
                    kvStore.delete(categoryKey);
                }else{
                    // Update changes in database
                    UtilsOracle.addKeyValue(kvStore, categoryKey, categoryAuthor.serialize());
                }

            }

            kvStore.delete(author_key);
        }
    }

    // =============================================================//
    // Delete one Book from all the kvstore                         //
    //      - Delete book from "Book" table                         //
    //      - For all authors (of the book):                        //
    //          -> remove the book from the book's author           //
    //      - Delete the book from the "Book category"              //
    //                                                              //
    // Note: if an author has only written this book, the author    //
    //       is NOT removed                                         //
    // =============================================================//
    public static void request_4_2(String title){
        KVStore kvStore = UtilsOracle.getKvstore();

        // Clean Author from Author table
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(BookOracle.BOOK);
        tab_key.add(title);
        Key myKey = Key.createKey(tab_key);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
        while (i.hasNext()){
            Key book_key = i.next().getKey();

            BookOracle book = new BookOracle();
            book.deserialize(UtilsOracle.getValueFromKey(kvStore, book_key));

            // Remove book from the authors
            for (AuthorOracle author: book.getAuthors()){
                // Get the author value from kvstore
                Key author_key = author.getKey();
                String author_str = UtilsOracle.getValueFromKey(kvStore, author_key);

                // Load author from database to get the list of books
                AuthorOracle author_full = new AuthorOracle();
                author_full.deserialize(author_str);

                // Remove the book from the list of books
                author_full.removeBook(book);

                // Update the DB
                UtilsOracle.addKeyValue(kvStore, author_key, author.serialize());
            }

            kvStore.delete(book_key);

            // Delete book from CategoryBook "table"
            Iterator<KeyValueVersion> j = UtilsOracle.getCategoryBookIterator(kvStore);
            while (j.hasNext()){
                Key categoryKey = j.next().getKey();
                String categoryStr = UtilsOracle.getValueFromKey(kvStore, categoryKey);

                CategoryBookOracle categoryBook = new CategoryBookOracle();
                categoryBook.deserialize(categoryStr);

                // Remove the book from title
                categoryBook.removeBook(book);

                // If there are no more books
                if (categoryBook.getBooks().size() == 0){
                    kvStore.delete(categoryKey);
                }else{
                    // Update changes in database
                    UtilsOracle.addKeyValue(kvStore, categoryKey, categoryBook.serialize());
                }
            }
        }
    }
}
