package main;

import entity.oracle.AuthorOracle;
import entity.oracle.BookOracle;
import entity.Category;
import oracle.kv.*;

import java.util.ArrayList;
import java.util.Iterator;

public class UtilsOracle {
    /* ******************************** */
    /*      CONFIG CONNEXION  - ORACLE  */
    /* ******************************** */
    /**
     * Connect to KVStore
     * @return kvstore
     */
    public static KVStore getKvstore() {
        KVStoreConfig config = new KVStoreConfig(Parameters.STORE_NAME,
                Parameters.HOST_NAME + ":" + Parameters.HOST_PORT);
        return KVStoreFactory.getStore(config);
    }

    // ============================================ //
    //                  DELETION                    //
    // ============================================ //

    /**
     * Delete all AUTHORS - BOOKS - CATEGORY from KVStore
     */
    public static void cleanDataStore(){
        // Clean All Auteurs
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(AuthorOracle.AUTHOR);
        Key myKey = Key.createKey(tab_key);
        cleanOneKey(myKey);

        // Clean All Books
        tab_key = new ArrayList<>();
        tab_key.add(BookOracle.BOOK);
        myKey = Key.createKey(tab_key);
        cleanOneKey(myKey);

        // Clean All Categories
        tab_key = new ArrayList<>();
        tab_key.add(Category.CATEGORY);
        myKey = Key.createKey(tab_key);
        cleanOneKey(myKey);
    }

    /**
     * Delete all element matching a given key
     * @param key: element key to delete
     */
    public static void cleanOneKey(Key key){
        KVStore kvStore = getKvstore();

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, key, null, null);
        while (i.hasNext()){
            kvStore.delete(i.next().getKey());
        }
    }

    // ============================================ //
    //                  INSERTION                   //
    // ============================================ //
    /**
     * Insert AuthorOracle in kvstore
     * @param kvStore: Database
     * @param author: AuthorOracle to insert
     */
    public static void addAuthorInKvstore(KVStore kvStore, AuthorOracle author){
        addKeyValue(kvStore, author.getKey(), author.serialize());
    }

    /**
     * Insert BookOracle in kvstore
     * @param kvStore: database
     * @param book: BookOralce to insert
     */
    public static void addBookInKvstore(KVStore kvStore, BookOracle book){
        addKeyValue(kvStore, book.getKey(), book.serialize());
    }

    /**
     * Insert one element in KVStore from key and string value
     * @param kvStore: database
     * @param key: key of the element to insert
     * @param str_value: value to insert
     */
    public static void addKeyValue(KVStore kvStore, Key key, String str_value){
        Value value = Value.createValue(str_value.getBytes());
        kvStore.put(key, value);
    }

    // ============================================ //
    //                     READ                     //
    // ============================================ //
    /**
     * Get String value of a Key
     * @param kvStore: database
     * @param k: kvstore key
     * @return String value
     */
    public static String getValueFromKey(KVStore kvStore, Key k){
        ValueVersion valueVersion = kvStore.get(k);
        Value value = valueVersion.getValue();
        byte[] tab_bytes = value.getValue();
        return new String(tab_bytes);
    }

    /**
     * Get a book from a Title
     * If book not found, empty book
     * @param title: book title (to search)
     * @return Book
     */
    public static BookOracle getBookFromTitle(String title){
        KVStore kvStore = getKvstore();
        BookOracle book = new BookOracle();
        Iterator<KeyValueVersion> i = getBookIterator(kvStore, title);
        if (i.hasNext()){
            Key k = i.next().getKey();
            String book_str = UtilsOracle.getValueFromKey(kvStore, k);
            book.deserialize(book_str);
        }
        return book;
    }

    /**
     * Collect all the book title in DB (iterate over all the DB)
     * @param kvStore: database
     * @return List of all book title
     */
    public static ArrayList<String> getAllBooksTitle(KVStore kvStore){
        ArrayList<String> list_titles = new ArrayList<>();
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(BookOracle.BOOK);
        Key key = Key.createKey(tab_key);
        Iterator<KeyValueVersion> i =  kvStore.storeIterator(Direction.UNORDERED, 0, key, null, null);
        while(i.hasNext()){
            Key k = i.next().getKey();

            String book_str = UtilsOracle.getValueFromKey(kvStore, k);

            BookOracle book = new BookOracle();
            book.deserialize(book_str);

            list_titles.add(book.getTitle());
        }

        return list_titles;
    }

    /**
     * Collect all the authors name in DB (iterate over all the DB)
     * @param kvStore: database
     * @return List of all authors name
     */
    public static ArrayList<String> getAllAuthorsName(KVStore kvStore){
        ArrayList<String> list_titles = new ArrayList<>();
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(AuthorOracle.AUTHOR);
        Key key = Key.createKey(tab_key);
        Iterator<KeyValueVersion> i =  kvStore.storeIterator(Direction.UNORDERED, 0, key, null, null);
        while(i.hasNext()){
            Key k = i.next().getKey();

            String author_str = UtilsOracle.getValueFromKey(kvStore, k);

            AuthorOracle author = new AuthorOracle();
            author.deserialize(author_str);

            list_titles.add(author.getlastName());
        }
        return list_titles;
    }


    // ============================================ //
    //                 ITERATORS                    //
    // ============================================ //
    /**
     * Authors iterator from Author Name
     * @param kvStore: database
     * @param author_name: author name
     * @return kvstore iterator
     */
    public static Iterator<KeyValueVersion> getAuthorIterator(KVStore kvStore, String author_name){
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(AuthorOracle.AUTHOR);
        tab_key.add(author_name);
        Key myKey = Key.createKey(tab_key);
        return kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
    }

    /**
     * Books iterator from book title
     * @param kvStore: database
     * @param title: book title
     * @return kvstore iterator
     */
    public static Iterator<KeyValueVersion> getBookIterator(KVStore kvStore, String title){
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(BookOracle.BOOK);
        // Add the title as a list of important key's words
        tab_key.add(BookOracle.getTitleLetter(title));
        Key myKey = Key.createKey(tab_key);
        return kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
    }
    /**
     * CategoryBook iterator
     * @param kvStore: database
     * @return kvstore iterator
     */
    public static Iterator<KeyValueVersion> getCategoryBookIterator(KVStore kvStore){
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(Category.CATEGORY);
        tab_key.add(BookOracle.BOOK);
        Key key = Key.createKey(tab_key);
        return kvStore.storeIterator(Direction.UNORDERED, 0, key, null, null);
    }
}
