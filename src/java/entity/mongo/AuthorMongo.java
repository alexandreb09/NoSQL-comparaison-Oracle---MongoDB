package entity.mongo;


import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import entity.Author;

import java.util.ArrayList;

public class AuthorMongo extends Author {
    /* ************************************ */
    /*              FIELDS                  */
    /* ************************************ */
    protected ArrayList<BookMongo> books;


    /* ************************************ */
    /*          CONSTRUCTORS                */
    /* ************************************ */
    public AuthorMongo() {
        books = new ArrayList<>();
    }
    public AuthorMongo(String lastName_, String firstName_, String location_) {
        super(lastName_, firstName_, location_);
        books = new ArrayList<>();
    }


    /* ************************************ */
    /*             FUNCTIONS                */
    /* ************************************ */
    /**
     * Return an Array of DBObject for the current books
     * @return ArrayList<DBObject>
     */
    public ArrayList<DBObject> getBooksDBObject(){
        ArrayList<DBObject> list_books_db = new ArrayList<>();
        for (BookMongo book : books) {
            list_books_db.add(BookMongo.toDBObjectPartial(book));
        }
        return list_books_db;
    }


    /* ************************************ */
    /*            STATIC method             */
    /* ************************************ */
    /**
     * Create a BasicDBObject with the books
     * @param authorMongo: author
     * @return BasicDBObject
     */
    public static DBObject toDBObject(AuthorMongo authorMongo){
        return toDBObjectPartial(authorMongo)
                .append("books", authorMongo.getBooksDBObject());
    }

    /**
     * Create a BasicDBObject without the books (prevent infinite recursion)
     * @param authorMongo: author
     * @return BasicDBObject
     */
    public static BasicDBObject toDBObjectPartial(AuthorMongo authorMongo){
        return new BasicDBObject("_id", authorMongo.getlastName())
                .append("firstName", authorMongo.getfirstName())
                .append("lastName", authorMongo.getlastName())
                .append("location", authorMongo.getlocation());
    }

    /**
     * Deserialise Author
     * @param author_obj: author to deserialize
     * @return AuthorMongo Entity
     */
    public static AuthorMongo deserialize(DBObject author_obj){
        return new Gson().fromJson(author_obj.toString(), AuthorMongo.class);
    }

    /**
     * Create an Author from a given integer
     * @param i: Author number
     * @return Author
     */
    public static AuthorMongo randomAuthor(int i){
        return new AuthorMongo("Author_" + i, "firstName_" + i, "location_" + i);
    }


    /* ************************************ */
    /*          GETTER - SETTER             */
    /* ************************************ */
    public ArrayList<BookMongo> getBooks() {
        return books;
    }

    public void addBook(BookMongo book_){
        books.add(book_);
    }

    public void removeBook(BookMongo book_){
        books.remove(book_);
    }
}
