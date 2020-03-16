package entity.mongo;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import entity.Book;
import entity.Category;

import java.util.ArrayList;

public class BookMongo extends Book {

    /* ************************************ */
    /*              FIELDS                  */
    /* ************************************ */
    protected ArrayList<AuthorMongo> authors;


    /* ************************************ */
    /*          CONSTRUCTORS                */
    /* ************************************ */
    public BookMongo(){
        super();
        authors = new ArrayList<>();
    }
    public BookMongo(String title_, String price_, String category_) {
        super(title_, price_, category_);
        authors = new ArrayList<>();
    }


    /* ************************************ */
    /*             FUNCTIONS                */
    /* ************************************ */
    /**
     * Return an Array of DBObject for the current author
     * @return ArrayList<DBObject>
     */
    public ArrayList<DBObject> getAuthorsDBObject(){
        ArrayList<DBObject> list_author_db = new ArrayList<>();
        for (AuthorMongo author : authors) {
            list_author_db.add(AuthorMongo.toDBObjectPartial(author));
        }
        return list_author_db;
    }

    /* ************************************ */
    /*            STATIC method             */
    /* ************************************ */
    /**
     * Create a BasicDBObject with the authors
     * @param bookMongo: book
     * @return BasicDBObject
     */
    public static DBObject toDBObject(BookMongo bookMongo){
        return toDBObjectPartial(bookMongo)
                .append("authors", bookMongo.getAuthorsDBObject());
    }

    /**
     * Create a BasicDBObject without the authors (prevent infinite recursion)
     * @param bookMongo: book
     * @return BasicDBObject
     */
    public static BasicDBObject toDBObjectPartial(BookMongo bookMongo){
        return new BasicDBObject("_id", getMongoId(bookMongo))
                .append("title", bookMongo.getTitle())
                .append("price", bookMongo.getPrice())
                .append("category", bookMongo.getCategory());
    }

    /**
     * Get the MongoId from a fill book
     * @param bookMongo: book
     * @return mongo id_
     */
    public static String getMongoId(BookMongo bookMongo){
        return Book.getTitleLetter(bookMongo.getTitle()) + "¤"
                + BookMongo.priceRangeCompute(bookMongo) + "¤"
                + bookMongo.getPrice() + "¤" + bookMongo.getTitle();
    }

    /**
     * Deserialise Book
     * @param book_obj: book to deserialize
     * @return BookMongo Entity
     */
    public static BookMongo deserialize(DBObject book_obj){
        return new Gson().fromJson(book_obj.toString(), BookMongo.class);
    }

    /**
     * Create a random Livre from a given integer
     * @return Livre
     */
    public static BookMongo createRandomBook(){
        BookMongo book = new BookMongo();
        book.setRandomTitle();
        book.setRandomprice();
        book.setCategory(Category.getRandomCategory());

        return book;
    }

    /* ************************************ */
    /*          GETTER - SETTER             */
    /* ************************************ */
    public ArrayList<AuthorMongo> getAuthors() {
        return authors;
    }

    public void addAuthor(AuthorMongo author){
        authors.add(author);
    }

    public void removeAuthor(AuthorMongo author){
        authors.remove(author);
    }
}
