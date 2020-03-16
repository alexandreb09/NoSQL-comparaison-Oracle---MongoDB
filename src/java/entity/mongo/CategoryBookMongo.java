package entity.mongo;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import entity.Category;
import entity.oracle.BookOracle;
import main.UtilsOracle;
import oracle.kv.*;

import java.util.*;

public class CategoryBookMongo extends Category {
    public static final String CATEGORY_BOOK = "CATEGORY_BOOK";

    /* ************************************ */
    /*              FIELDS                  */
    /* ************************************ */
    private ArrayList<BookMongo> books;

    private String letter;


    /* ************************************ */
    /*          CONSTRUCTORS                */
    /* ************************************ */
    public CategoryBookMongo() {
        this.books = new ArrayList<>();
    }
    public CategoryBookMongo(String category, String letter) {
        this.setCategory(category);
        this.setLetter(letter);
        this.books = new ArrayList<>();
    }


    /* ************************************ */
    /*             FUNCTIONS                */
    /* ************************************ */
    /**
     * Return an Array of DBObject for the current book list
     * @return ArrayList<DBObject>
     */
    public ArrayList<DBObject> getBooksDBObject(){
        ArrayList<DBObject> list_books = new ArrayList<>();
        for (BookMongo book : books) {
            list_books.add(BookMongo.toDBObjectPartial(book));
        }
        return list_books;
    }

    /* ************************************ */
    /*            STATIC method             */
    /* ************************************ */
    /**
     * Create a BasicDBObject for the current category
     * @param categoryBookMongo: category Book
     * @return BasicDBObject
     */
    public static DBObject toDBObject(CategoryBookMongo categoryBookMongo){
        return new BasicDBObject("_id", categoryBookMongo.getCategory() + categoryBookMongo.getLetter())
                .append("category", categoryBookMongo.getCategory())
                .append("letter", categoryBookMongo.getLetter())
                .append("books", categoryBookMongo.getBooksDBObject());
    }

    /**
     * Deserialise CategoyBook
     * @param category_book_obj: book to deserialize
     * @return BookMongo Entity
     */
    public static CategoryBookMongo deserialize(DBObject category_book_obj){
        return new Gson().fromJson(category_book_obj.toString(), CategoryBookMongo.class);
    }

    public static void addBookToCategory(DBCollection collection_category_book, BookMongo book){
        CategoryBookMongo categoryBookMongo;

        // Find category book matching the category and book title letter
        BasicDBObject findQuery = new BasicDBObject("_id", book.getCategory() + BookMongo.getTitleLetter(book.getTitle()));
        DBObject category_book_obj = Objects.requireNonNull(collection_category_book).findOne(findQuery);
        // Check if the category already exists
        if (null == category_book_obj){
            categoryBookMongo = new CategoryBookMongo(book.getCategory(), BookMongo.getTitleLetter(book.getTitle()));
            // Add the book to the category
            categoryBookMongo.addBook(book);
            // Insert new categoryBook
            collection_category_book.insert(CategoryBookMongo.toDBObject(categoryBookMongo));
        }else{
            categoryBookMongo = CategoryBookMongo.deserialize(category_book_obj);
            // Add the book to the category
            categoryBookMongo.addBook(book);
            // Update the content
            collection_category_book.update(findQuery, CategoryBookMongo.toDBObject(categoryBookMongo));
        }
    }

    /* ************************************ */
    /*            Getter - setter           */
    /* ************************************ */
    public ArrayList<BookMongo> getBooks(){
        return books;
    }

    public void addBook(BookMongo book){
        this.books.add(book);
    }

    public void removeBook(BookMongo book){
        this.books.remove(book);
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }
}
