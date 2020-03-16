package entity.oracle;


import entity.Author;
import oracle.kv.Key;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuthorOracle extends Author {
    /* ************************************ */
    /*              FIELDS                  */
    /* ************************************ */
    protected ArrayList<BookOracle> books;

    /* ************************************ */
    /*          CONSTRUCTORS                */
    /* ************************************ */
    public AuthorOracle(){
        books = new ArrayList<>();
    }

    public AuthorOracle(String lastName_, String firstName_, String location_) {
        super(lastName_, firstName_, location_);
        books = new ArrayList<>();
    }


    /* ************************************ */
    /*             FUNCTIONS                */
    /* ************************************ */
    /**
     * Serialize the Author entity: insert directly in DB
     * Delimiter: "¤"
     * Fields:
     *      - LASTNAME
     *      - FIRSTNAME
     *      - LOCATION
     *      - list of book partially serialized (TITLE¤PRICE¤CATGORY)
     * @return String (serialized entity fields)
     */
    public String serialize(){
        return lastName + "¤" + firstName + "¤" + location + "¤" + serializeBooks();
    }

    /**
     * Partial serialisation ignoring the list of books
     * Used in BookOracle serialization: prevent infinite serialization
     * @return String (serialized entity fields)
     */
    public String serializePartial(){
        return lastName + "¤" + firstName + "¤" + location;
    }

    /**
     * Serialize the list of Books
     * Delimiter: "¤"
     * Fields:
     *      - TITLE
     *      - PRICE
     *      - CATGORY
     * @return String (serialized books)
     */
    public String serializeBooks(){
        StringBuilder str = new StringBuilder();
        for (BookOracle book: books) {
            str.append(book.serializePartial()).append("¤");
        }
        // Remove the last delimiter
        if (books.size() > 0){
            return str.toString().substring(0, str.length() - 1);
        }
        return str.toString();
    }

    /**
     * Unserialize an author string
     * Delimiter used: "¤"
     * Follow the same scheme as for serialization
     * @return String (serialized books)
     */
    public AuthorOracle deserialize(String str_data){
        List<String> parts = Arrays.asList(str_data.split("¤"));

        // Unserialized Authors fields
        if (parts.size() >= 3){
            this.setlastName(parts.get(0));
            this.setfirstName(parts.get(1));
            this.setlocation(parts.get(2));

            // Unserialized Books
            if (parts.size() > 3 && (parts.size() - 3) % 3 == 0){
                for (int i = 3; i < parts.size(); i = i + 3){
                    BookOracle book = new BookOracle(parts.get(i),parts.get(i+1), parts.get(i+2));
                    this.addBook(book);
                }
            }
        }
        return this;
    }

    /**
     * Return a full key from the fields
     * Hierarchy:
     *      - AUTHOR
     *      - LAST_NAME
     *      - FIRST_NAME
     * @return KVStore key
     */
    public Key getKey(){
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(AUTHOR);
        tab_key.add(this.getlastName());
        tab_key.add(this.getfirstName());
        return Key.createKey(tab_key);
    }

    /* ************************************ */
    /*            STATIC method             */
    /* ************************************ */
    /**
     * Create an Author from a given integer
     * @param i: Author number
     * @return Author
     */
    public static AuthorOracle randomAuthor(int i){
        return new AuthorOracle("Author_" + i, "firstName_" + i, "location_" + i);
    }


    /* ************************************ */
    /*          GETTER - SETTER             */
    /* ************************************ */
    public ArrayList<BookOracle> getBooks() {
        return books;
    }

    public void addBook(BookOracle book_){
        books.add(book_);
    }

    public void removeBook(BookOracle book_){
        books.remove(book_);
    }
}
