package entity.oracle;

import entity.Category;
import main.UtilsOracle;
import oracle.kv.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CategoryAuthorOracle extends Category {

    /* ************************************ */
    /*              FIELDS                  */
    /* ************************************ */
    private ArrayList<AuthorOracle> authors;


    /* ************************************ */
    /*          CONSTRUCTORS                */
    /* ************************************ */
    public CategoryAuthorOracle() {
        this.authors = new ArrayList<>();
    }


    /* ************************************ */
    /*             FUNCTIONS                */
    /* ************************************ */
    /**
     * Get a kvStore key
     * Key build following the hierarchy:
     *      - CATEGORY
     *      - category (e.g. History, Manga...)
     *      - Book title letter
     * @return Key: kvstore key
     */
    public Key getKey(){
        ArrayList<String> tab_key = new ArrayList<>();

        // Add category type
        tab_key.add(Category.CATEGORY);
        tab_key.add(AuthorOracle.AUTHOR);

        // Add category
        tab_key.add(this.getCategory());

        return Key.createKey(tab_key);
    }

    public String serialize(){
        StringBuilder out = new StringBuilder(getCategory());
        for (AuthorOracle author: authors) {
            out.append("¤").append(author.serializePartial());
        }
        return out.toString();
    }

    /**
     * Fill the entity from a string
     * @param str_categories: string to deserialize
     */
    public void deserialize(String str_categories){
        List<String> fields = Arrays.asList(str_categories.split("¤"));
        if (fields.size() >= 1){
            this.setCategory(fields.get(0));
        }
        if (fields.size() > 1){
            for (int i = 1; i < (fields.size() - 1) / 3; i = i + 3){
                AuthorOracle author = new AuthorOracle();
                author.setlastName(fields.get(i));
                author.setfirstName(fields.get(i + 1));
                author.setlocation(fields.get(i + 2));
                authors.add(author);
            }
        }
    }

    public static void addToKvStore(KVStore kvStore, AuthorOracle author, String category){
        // Create entity
        CategoryAuthorOracle categoryAuthor = new CategoryAuthorOracle();
        Key categoryAuthorKey;
        Value value;

        // Create key (search for already existing CategoryBook)
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(Category.CATEGORY);
        tab_key.add(AuthorOracle.AUTHOR);
        tab_key.add(category);
        Key myKey = Key.createKey(tab_key);

        // Get iterator from a key
        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
        // Check if a categoryBook entity exist
        if (i.hasNext()){
            // Get categoryBook KEY
            categoryAuthorKey = i.next().getKey();
            // Fill entity from DataBase value
            String categoryAuthorStr = UtilsOracle.getValueFromKey(kvStore, categoryAuthorKey);

            // Add the author directly to the string kvstore value without deserialisation
            categoryAuthorStr = categoryAuthorStr + "¤" + author.serializePartial();

            // Add value to kvstore
            value = Value.createValue(categoryAuthorStr.getBytes());
        }
        // If there are no categoryBook for the given category and book title letter
        else{
            // Fill categoryAuthor entity
            categoryAuthor.setCategory(category);

            // Create the key
            categoryAuthorKey = categoryAuthor.getKey();

            // Add author to the entity
            categoryAuthor.addAuthor(author);

            // Add value to kvstore
            value = Value.createValue(categoryAuthor.serialize().getBytes());
        }

        // Update value in KVstore
        kvStore.put(categoryAuthorKey, value);
    }

    /* ************************************ */
    /*            Getter - setter           */
    /* ************************************ */
    public ArrayList<AuthorOracle> getAuthor(){
        return authors;
    }

    public void addAuthor(AuthorOracle author){
        this.authors.add(author);
    }

    public void removeAuthor(AuthorOracle author){
        this.authors.remove(author);
    }
}
