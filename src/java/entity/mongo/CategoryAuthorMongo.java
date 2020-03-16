package entity.mongo;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import entity.Author;
import entity.Category;
import entity.oracle.AuthorOracle;
import main.UtilsOracle;
import oracle.kv.*;

import java.util.*;

public class CategoryAuthorMongo extends Category {
    public static final String CATEGORY_AUTHOR = "CATEGORY_AUTHOR";

    /* ************************************ */
    /*              FIELDS                  */
    /* ************************************ */
    private ArrayList<AuthorMongo> authors;


    /* ************************************ */
    /*          CONSTRUCTORS                */
    /* ************************************ */
    public CategoryAuthorMongo() {
        this.authors = new ArrayList<>();
    }
    public CategoryAuthorMongo(String category) {
        this.authors = new ArrayList<>();
        this.setCategory(category);
    }


    /* ************************************ */
    /*             FUNCTIONS                */
    /* ************************************ */
    /**
     * Return an Array of DBObject for the current author list
     * @return ArrayList<DBObject>
     */
    public ArrayList<DBObject> getAuthorsDBObject(){
        ArrayList<DBObject> list_authors = new ArrayList<>();
        for (AuthorMongo author : authors) {
            list_authors.add(AuthorMongo.toDBObjectPartial(author));
        }
        return list_authors;
    }

    /* ************************************ */
    /*            STATIC method             */
    /* ************************************ */
    /**
     * Create a BasicDBObject for the current category
     * @param categoryAuthorMongo: category Authors
     * @return BasicDBObject
     */
    public static DBObject toDBObject(CategoryAuthorMongo categoryAuthorMongo){
        return new BasicDBObject("_id", categoryAuthorMongo.getCategory())
                .append("category", categoryAuthorMongo.getCategory())
                .append("authors", categoryAuthorMongo.getAuthorsDBObject());
    }

    /**
     * Deserialise CategoyAuthors
     * @param category_author_obj: book to deserialize
     * @return AuthorCategory Entity
     */
    public static CategoryAuthorMongo deserialize(DBObject category_author_obj){
        return new Gson().fromJson(category_author_obj.toString(), CategoryAuthorMongo.class);
    }


    public static void addAuthorToCategory(DBCollection collection_category_author, AuthorMongo author, String category){
        CategoryAuthorMongo categoryAuthorMongo;

        // Find category author matching the category
        BasicDBObject findQuery = new BasicDBObject("_id", category);
        DBObject category_author_obj = Objects.requireNonNull(collection_category_author).findOne(findQuery);
        // Check if the category already exists
        if (null == category_author_obj){
            categoryAuthorMongo = new CategoryAuthorMongo(category);
            // Add the book to the category
            categoryAuthorMongo.addAuthor(author);
            // Insert new categoryBook
            collection_category_author.insert(CategoryAuthorMongo.toDBObject(categoryAuthorMongo));
        }else{
            categoryAuthorMongo = CategoryAuthorMongo.deserialize(category_author_obj);
            // Add the book to the category
            categoryAuthorMongo.addAuthor(author);
            // Update the content
            collection_category_author.update(findQuery, CategoryAuthorMongo.toDBObject(categoryAuthorMongo));
        }
    }


    /* ************************************ */
    /*            Getter - setter           */
    /* ************************************ */
    public ArrayList<AuthorMongo> getAuthor(){
        return authors;
    }

    public void addAuthor(AuthorMongo author){
        this.authors.add(author);
    }

    public void removeAuthor(AuthorMongo author){
        this.authors.remove(author);
    }
}
