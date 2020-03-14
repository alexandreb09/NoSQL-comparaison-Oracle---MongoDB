package entity.oracle;

import main.Utils;
import oracle.kv.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CategoryBook extends Category {

    private ArrayList<Book> books;

    private String letter;

    public CategoryBook() {
        this.books = new ArrayList<>();
    }

    public String serialize(){
        StringBuilder out = new StringBuilder(getCategory() + "¤" + getLetter());
        for (Book book: books) {
            out.append("¤").append(book.serializePartial());
        }
        return out.toString();
    }

    /**
     * Fill the entity from a string
     * @param str_categories: string to deserialize
     */
    public void deserialize(String str_categories){
        List<String> fields = Arrays.asList(str_categories.split("¤"));
        if (fields.size() >= 2){
            this.setCategory(fields.get(0));
            this.setLetter(Book.getTitleLetter(fields.get(1)));
        }
        if (fields.size() > 2){
            for (int i = 2; i < (fields.size() - 2) / 3; i = i + 3){
                Book book = new Book();
                book.setTitle(fields.get(i));
                book.setPrice(fields.get(i + 1));
                book.setCategory(fields.get(i + 2));
                books.add(book);
            }
        }
    }

    public static void addToKvStore(KVStore kvStore, Book book){
        // Create entity
        CategoryBook categoryBook = new CategoryBook();
        Key categoryBookKey;
        Value value;

        // Create key (search for already existing CategoryBook)
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(Category.CATEGORY);
        tab_key.add(Book.BOOK);
        tab_key.add(book.getCategory());
        tab_key.add(Book.getTitleLetter(book.getTitle()));
        Key myKey = Key.createKey(tab_key);

        // Get iterator from a key
        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
        // Check if a categoryBook entity exist
        if (i.hasNext()){
            // Get categoryBook KEY
            categoryBookKey = i.next().getKey();
            // Fill entity from DataBase value
            String categoryBookStr = Utils.getValueFromKey(kvStore, categoryBookKey);

            // Add the author direclty to the string kvstore value without deserialisation
            categoryBookStr = categoryBookStr + "¤" + book.serializePartial();

            // Add value to kvstore
            value = Value.createValue(categoryBookStr.getBytes());
        }
        // If there are no categoryBook for the given category and book title letter
        else{
            // Fill categoryBook entity
            categoryBook.setCategory(book.getCategory());
            categoryBook.setLetter(Book.getTitleLetter(book.getTitle()));

            // Create the key
            categoryBookKey = categoryBook.getKey();

            // Add book to the entity
            categoryBook.addBook(book);

            // Add value to kvstore
            value = Value.createValue(categoryBook.serialize().getBytes());
        }

        // Update value in KVstore
        kvStore.put(categoryBookKey, value);
    }

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
        tab_key.add(Book.BOOK);

        // Add category
        tab_key.add(this.getCategory());

        // Add letter
        tab_key.add(this.getLetter());

        return Key.createKey(tab_key);
    }

    /* ************************************ */
    /*            Getter - setter           */
    /* ************************************ */
    public ArrayList<Book> getBooks(){
        return books;
    }

    public void addBook(Book book){
        this.books.add(book);
    }

    public void removeBook(Book book){
        this.books.remove(book);
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }
}
