package entity.oracle;

import entity.Book;
import entity.Category;
import oracle.kv.Key;

import java.util.*;

public class BookOracle extends Book {

    /* ************************************ */
    /*              FIELDS                  */
    /* ************************************ */
    protected ArrayList<AuthorOracle> authors;

    /* ************************************ */
    /*          CONSTRUCTORS                */
    /* ************************************ */
    public BookOracle(){
        this.authors = new ArrayList<>();
    }
    public BookOracle(String title_, String price_, String category_) {
        super(title_, price_, category_);
        this.authors = new ArrayList<>();
    }

    /* ************************************ */
    /*             FUNCTIONS                */
    /* ************************************ */
    /**
     * Serialize the Book entity: insert directly in DB
     * Delimiter: "¤"
     * Fields:
     *      - TITLE
     *      - PRICE
     *      - CATEGORY
     *      - list of author partially serialized (LASTNAME¤FIRSTNAME¤LOCATION)
     * @return String (serialized entity fields)
     */
    public String serialize(){
        return title + "¤" + price + "¤" + category + "¤" + serializeAuthors();
    }

    /**
     * Partial serialisation ignoring the list of authors
     * Used in AuthorOracle serialization: prevent infinite serialization
     * @return String (serialized entity fields)
     */
    public String serializePartial(){
        return title + "¤" + price + "¤" + category;
    }

    /**
     * Serialize the list of Authors
     * Delimiter: "¤"
     * Fields:
     *      - LASTNAME
     *      - FIRSTNAME
     *      - LOCATION
     * @return String (partially serialized authors)
     */
    public String serializeAuthors(){
        StringBuilder str = new StringBuilder();
        for (AuthorOracle author: authors) {
            str.append(author.serializePartial()).append("¤");
        }
        if (authors.size() > 0){
            return str.toString().substring(0, str.length() - 1);
        }
        return str.toString();
    }

    /**
     * Unserialize an BookOracle string
     * Delimiter used: "¤"
     * Follow the same scheme as for serialization
     */
    public void deserialize(String str_book){
        List<String> parts = Arrays.asList(str_book.split("¤"));
        int n_fields_book = 3;
        int n_fields_author = 3;

        // Unserialized Books fields
        if (parts.size() >= n_fields_book){
            this.setTitle(parts.get(0));
            this.setPrice(parts.get(1));
            this.setCategory(parts.get(2));

            // Unserialized Authors
            if (parts.size() > n_fields_book && (parts.size() - n_fields_book) % n_fields_author == 0){
                for (int i = n_fields_book; i < parts.size(); i = i + n_fields_author){
                    AuthorOracle author = new AuthorOracle(parts.get(i),parts.get(i+1), parts.get(i+2));
                    this.addAuthor(author);
                }
            }
        }
    }

    /**
     * Get a kvStore key
     * Key build following the hierarchy:
     *      - BOOK
     *      - TITLE LETTER
     *      - PRICE RANGE
     *      - PRICE
     * @return Key: kvstore key
     */
    public Key getKey(){
        ArrayList<String> tab_key = new ArrayList<>();

        // Add book type
        tab_key.add(BOOK);

        // Add all important name as a key in th title
        tab_key.add(getTitleLetter(this.getTitle()));

        // compute price range
        tab_key.add(Integer.toString(priceRangeCompute(this)));

        // Add full price
        tab_key.add(this.getPrice());
        return Key.createKey(tab_key);
    }



    /* ************************************ */
    /*            STATIC method             */
    /* ************************************ */
    /**
     * Create a random Livre from a given integer
     * @return Livre
     */
    public static BookOracle createRandomBook(){
        BookOracle book = new BookOracle();
        book.setRandomTitle();
        book.setRandomprice();
        book.setCategory(Category.getRandomCategory());

        return book;
    }

    /* ************************************ */
    /*          GETTER - SETTER             */
    /* ************************************ */
    public ArrayList<AuthorOracle> getAuthors() {
        return authors;
    }

    public void addAuthor(AuthorOracle author){
        authors.add(author);
    }

    public void removeAuthor(AuthorOracle author){
        authors.remove(author);
    }
}
