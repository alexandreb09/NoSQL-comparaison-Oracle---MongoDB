package main;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import entity.Author;
import entity.Book;
import entity.Category;
import entity.mongo.AuthorMongo;
import entity.mongo.BookMongo;
import entity.mongo.CategoryAuthorMongo;
import entity.mongo.CategoryBookMongo;
import entity.oracle.*;
import oracle.kv.Direction;
import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.KeyValueVersion;
import thread.AuthorBookInsertThreadMongo;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// In all the question1, we want to optimize the research of
public class QuestionMongo {

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
            AuthorBookInsertThreadMongo thread = new AuthorBookInsertThreadMongo(latch, Parameters.NB_AUTHOR_PER_THREAD*i,
                    Parameters.NB_AUTHOR_PER_THREAD*(i+1)-1);
            thread.start();
        }

        // Wait for threads finished
        boolean out = true;
        try {
            latch.await();
            System.out.println("Creation finished !");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Creation failed !");
            out = false;
        }
        System.out.println(Utils.getSeparatorLine());
        return out;
    }


    // =============================================================//
    // Operations:                                                  //
    //      - Generate random author name                           //
    //      - Find all books associated to this author              //
    // =============================================================//
    public static ArrayList<BookMongo> request_1_1(String author_name){
        // Get MongoDB Author collection
        DBCollection collection = UtilsMongo.getCollection(Author.AUTHOR);

        // Deserialize Author obj
        AuthorMongo author = UtilsMongo.getAuthorFromName(collection, author_name);

        // Return books
        return author.getBooks();
    }


    // =============================================================//
    // Operations:                                                  //
    //      - From a given author name                              //
    //      - Find all books written to this author                 //
    //      - For each of these books:                              //
    //              - Generate a random author name                 //
    //              - Find this author name                         //
    //              - Add this author to the author list in book    //
    //              - Add the book to the book list in this author  //
    // =============================================================//
    public static void request_1_2(String author_name){
        // Get MongoDB Book - Author collection
        DBCollection collection_book = UtilsMongo.getCollection(Book.BOOK);
        DBCollection collection_author = UtilsMongo.getCollection(Author.AUTHOR);

        // For all books written by this author
        for (BookMongo book: request_1_1(author_name)){
            // Reload the book from DB (authors are missing)
            // Create regex query object from author title
            BasicDBObject findQueryBook = new BasicDBObject("_id", BookMongo.getMongoId(book));
            // Get book (mongo db object)
            DBObject book_obj = Objects.requireNonNull(collection_book).findOne(findQueryBook);
            BookMongo book_full = BookMongo.deserialize(book_obj);

            // Generate random author to add (Author already exist in DB)
            String new_author_name = Utils.generateRandomAuthorName();
            // Get the Author entity associated to this name (full content)
            AuthorMongo author_new = UtilsMongo.getAuthorFromName(collection_author, new_author_name);

            // Add both relation (book to author and author to book)
            author_new.addBook(book_full);
            book_full.addAuthor(author_new);

            // Update value in DB
            BasicDBObject searchQuery = new BasicDBObject().append("id_", new_author_name);
            Objects.requireNonNull(collection_author).update(searchQuery, AuthorMongo.toDBObject(author_new));
            Objects.requireNonNull(collection_book).update(findQueryBook, BookMongo.toDBObject(book_full));
        }
    }


    // =============================================================//
    // Operations:                                                  //
    //      - Search all books with:                                //
    //          - "letter" in the title                             //
    //          - "price" rounded to the nearest ten                //
    //              -> 13 => [10, 20]                               //
    //              -> 29 => [20, 30]                               //
    //      - For all of these books:                               //
    //          - Increase price (10%)                              //
    // =============================================================//
    public static void request_1_3(String letter, int price ){
        DBCollection collection_book = UtilsMongo.getCollection(Book.BOOK);

        // Compute price range
        int price_range = Book.priceRangeCompute(price);

        BasicDBObject regex = new BasicDBObject("$regex", "^" + letter + "¤" + price_range);
        BasicDBObject findQuery = new BasicDBObject("_id", regex);

        // Get author (mongo db object)
        DBCursor cursor = Objects.requireNonNull(collection_book).find(findQuery);

        while (cursor.hasNext()){
            BookMongo book = BookMongo.deserialize(cursor.next());

            // ===== Update price ===== //
            // Save previous price
            String old_id = BookMongo.getMongoId(book);
            DBObject old_object = BookMongo.toDBObject(book);

            // Update price: +10%
            DecimalFormat newFormat = new DecimalFormat("#.##");
            book.setPrice(newFormat.format(Double.parseDouble(book.getPrice()) * 1.1));

            // If both price are different => the key is different
            if (!old_id.equals(BookMongo.getMongoId(book))){
                // Need to delete the old Book entity
                collection_book.remove(old_object);
                // Insert new one
                collection_book.insert(BookMongo.toDBObject(book));
            }else{
                // Update existing value
                collection_book.update(old_object, BookMongo.toDBObject(book));
            }
            // ===== END Update price ===== //
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
        CategoryBookMongo categoryBookMongo;

        // Get collections
        DBCollection collection_books = UtilsMongo.getCollection(Book.BOOK);
        DBCollection collection_category_book = UtilsMongo.getCollection(CategoryBookMongo.CATEGORY_BOOK);
        DBCollection collection_category_author = UtilsMongo.getCollection(CategoryAuthorMongo.CATEGORY_AUTHOR);

        DBCursor cursor = Objects.requireNonNull(collection_books).find();
        int cpt = 0;
        while (cursor.hasNext()){
            // Get book entity
            BookMongo book = BookMongo.deserialize(cursor.next());

            System.out.println(cpt++);

            // Generate random Category
            String category = Category.getRandomCategory();

            book.setCategory(category);

            // ====== ADD the BOOK in list of books for a given category and letter ====== //
            CategoryBookMongo.addBookToCategory(collection_category_book, book);
            // ====== FINISH ADD the BOOK in list of books for a given category and letter ====== //

            // ====== ADD the AUTHOR in list of authors for a given category ====== //
            // For all authors
            for(AuthorMongo author: book.getAuthors()){
                CategoryAuthorMongo.addAuthorToCategory(collection_category_author, author, book.getCategory());
            }
            // ====== FINISH ADD the AUTHOR in list of authors for a given category ====== //
        }
    }

    // =============================================================//
    // Operations:                                                  //
    //      - Select a random Category                              //
    //      - Get all books of this category                        //
    // =============================================================//
    public static ArrayList<BookMongo> request_2_1(String category){
        // Output
        ArrayList<BookMongo> books = new ArrayList<>();

        DBCollection collection_book = UtilsMongo.getCollection(CategoryBookMongo.CATEGORY_BOOK);

        // Create regex search
        BasicDBObject regex = new BasicDBObject("$regex", "^" + category);
        BasicDBObject findQuery = new BasicDBObject("_id", regex);
        DBCursor c = Objects.requireNonNull(collection_book).find(findQuery);

        while (c.hasNext()){
            // Unserialize the Category Book
            CategoryBookMongo categoryBookMongo = CategoryBookMongo.deserialize(c.next());
            // Add all books from this category to the output
            books.addAll(categoryBookMongo.getBooks());
        }

        return books;
    }

    // =============================================================//
    // Operations:                                                  //
    //      - Select a random Category                              //
    //      - Get all authors that have written a book from this    //
    //        category                                              //
    // =============================================================//
    public static ArrayList<AuthorMongo> request_2_2(String category){
        // Output
        ArrayList<AuthorMongo> authors = new ArrayList<>();

        DBCollection collection_authors = UtilsMongo.getCollection(CategoryAuthorMongo.CATEGORY_AUTHOR);

        // Create regex search
        BasicDBObject regex = new BasicDBObject("$regex", "^" + category);
        BasicDBObject findQuery = new BasicDBObject("_id", regex);
        DBCursor c = Objects.requireNonNull(collection_authors).find(findQuery);

        while (c.hasNext()){
            // Unserialize the Category Book
            CategoryAuthorMongo categoryAuthorMongo = CategoryAuthorMongo.deserialize(c.next());
            // Add all books from this category to the output
            authors.addAll(categoryAuthorMongo.getAuthor());
        }

        return authors;
    }

    // =============================================================//
    // Operations:                                                  //
    //      - Select a random Category                              //
    //      - Select all books from this category and having a      //
    //      "B" in its title                                        //
    // =============================================================//
    public static ArrayList<BookMongo> request_2_3(String category, String letter){
        // List of books (output)
        ArrayList<BookMongo> books = new ArrayList<>();

        DBCollection collection_book = UtilsMongo.getCollection(CategoryBookMongo.CATEGORY_BOOK);

        // Create query
        BasicDBObject findQuery = new BasicDBObject("_id", category + letter);
        DBCursor c = Objects.requireNonNull(collection_book).find(findQuery);

        while (c.hasNext()){
            // Unserialize the Category Book
            CategoryBookMongo categoryBookMongo = CategoryBookMongo.deserialize(c.next());
            // Add all books from this category to the output
            books.addAll(categoryBookMongo.getBooks());
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
        DBCollection collection_authors = UtilsMongo.getCollection(Author.AUTHOR);
        DBCollection collection_book = UtilsMongo.getCollection(Book.BOOK);
        DBCollection collection_category_book = UtilsMongo.getCollection(CategoryBookMongo.CATEGORY_BOOK);
        DBCollection collection_category_author = UtilsMongo.getCollection(CategoryAuthorMongo.CATEGORY_AUTHOR);

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
            AuthorMongo author = AuthorMongo.randomAuthor(n_authors);
            BookMongo book1 = BookMongo.createRandomBook();
            BookMongo book2 = BookMongo.createRandomBook();

            // Add relation between
            author.addBook(book1);
            author.addBook(book2);
            book1.addAuthor(author);
            book2.addAuthor(author);

            // Update BOOKS and AUTHORS in KVstore
            Objects.requireNonNull(collection_authors).insert(AuthorMongo.toDBObject(author));
            Objects.requireNonNull(collection_book).insert(BookMongo.toDBObject(book1));
            collection_book.insert(BookMongo.toDBObject(book2));

            // Add author for categoryAuthor:
            // There are two insertion if the categories are differents
            CategoryAuthorMongo.addAuthorToCategory(collection_category_author, author, book1.getCategory());
            // If the categories of the two books are different: 2 insertions (one for each categories)
            if (! book1.getCategory().equals(book2.getCategory())){
                CategoryAuthorMongo.addAuthorToCategory(collection_category_author, author, book2.getCategory());
            }

            // Add book for categoryBook
            CategoryBookMongo.addBookToCategory(collection_category_book, book1);
            CategoryBookMongo.addBookToCategory(collection_category_book, book2);

            n_authors++;
        }

        // Generate random author name
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
        DBCollection collection_author = UtilsMongo.getCollection(Author.AUTHOR);
        DBCollection collection_book = UtilsMongo.getCollection(Book.BOOK);
        DBCollection collection_category_book = UtilsMongo.getCollection(CategoryBookMongo.CATEGORY_BOOK);
        DBCollection collection_category_author = UtilsMongo.getCollection(CategoryAuthorMongo.CATEGORY_AUTHOR);

        // Create regex search
        BasicDBObject findQuery = new BasicDBObject("_id", author_name);
        DBCursor i = Objects.requireNonNull(collection_author).find(findQuery);

        while (i.hasNext()){
            // Get Author entity
            AuthorMongo author = AuthorMongo.deserialize(i.next());

            // Remove author from the books
            // If he's the only author, delete the book
            for (BookMongo book: author.getBooks()){
                // Load full book from database
                BasicDBObject bookQuery = new BasicDBObject("_id", BookMongo.getMongoId(book));
                DBObject book_obj = Objects.requireNonNull(collection_book).findOne(bookQuery);
                BookMongo book_full = BookMongo.deserialize(book_obj);

                // If there are several authors
                if (book_full.getAuthors().size() > 1){
                    // Remove the author from the list of authors
                    book_full.removeAuthor(author);

                    // Update databse
                    collection_book.update(bookQuery, BookMongo.toDBObject(book_full));
                }else{
                    // Delete book
                    collection_book.remove(bookQuery);

                    // Delete book from CategoryBook "table"
                    // Get iterator
                    BasicDBObject regex = new BasicDBObject("$regex", "^" + book.getCategory());
                    BasicDBObject categoryBookQuery = new BasicDBObject("_id", regex);
                    DBCursor j = Objects.requireNonNull(collection_category_book).find(categoryBookQuery);
                    while (j.hasNext()){
                        CategoryBookMongo categoryBook = CategoryBookMongo.deserialize(j.next());

                        // Remove the book from title
                        categoryBook.removeBook(book_full);


                        BasicDBObject query = new BasicDBObject("_id", categoryBook.getCategory() + categoryBook.getLetter());
                        // If there are no more books
                        if (categoryBook.getBooks().size() == 0){
                            collection_category_book.remove(query);
                        }else{
                            // Update changes in database
                            collection_category_book.update(query,
                                    CategoryBookMongo.toDBObject(categoryBook));
                        }
                    }
                }
            }

            // Delete author from CategoryAuthor "table"
            // Get iterator
            DBCursor j = Objects.requireNonNull(collection_category_author).find();
            while (j.hasNext()){
                // Get category Author
                CategoryAuthorMongo categoryAuthor = CategoryAuthorMongo.deserialize(j.next());

                // Remove the book from title
                categoryAuthor.removeAuthor(author);

                BasicDBObject query = new BasicDBObject("_id", categoryAuthor.getCategory());
                // If there are no more books
                if (categoryAuthor.getAuthor().size() == 0){
                    collection_category_author.remove(query);
                }else{
                    // Update changes in database
                    collection_category_author.update(query, CategoryAuthorMongo.toDBObject(categoryAuthor));
                }
            }

            // Delete author from the list of authors
            BasicDBObject query = new BasicDBObject("_id", author.getlastName());
            collection_author.remove(query);
        }
    }
}
