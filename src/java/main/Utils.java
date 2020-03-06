package main;

import entity.oracle.Author;
import entity.oracle.Book;
import oracle.kv.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Utils {


    /* ******************************** */
    /*      CONFIG CONNEXION  - ORACLE  */
    /* ******************************** */
    public static KVStore getKvstore() {
        KVStoreConfig config = new KVStoreConfig(Parameters.STORE_NAME,
                Parameters.HOST_NAME + ":" + Parameters.HOST_PORT);
        return KVStoreFactory.getStore(config);
    }

    public static void cleanDataStore(){
        // Clean All Auteurs
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(Author.AUTHOR);
        Key myKey = Key.createKey(tab_key);
        cleanOneKey(myKey);

        // Clean All Livres
        tab_key = new ArrayList<>();
        tab_key.add(Book.BOOK);
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

    public static void addAuthorInKvstore(KVStore kvStore, Author author){
        Value value = Value.createValue(author.serialize().getBytes());
        kvStore.put(author.getKey(), value);
    }
    public static void addBookInKvstore(KVStore kvStore, Book book){
        Value value = Value.createValue(book.serialize().getBytes());
        kvStore.put(book.getKey(), value);
    }
    public static void addKeyValue(KVStore kvStore, Key key, String str_value){
        Value value = Value.createValue(str_value.getBytes());
        kvStore.put(key, value);
    }

    public static String getValueFromKey(KVStore kvStore, Key k){
        ValueVersion valueVersion = kvStore.get(k);
        Value value = valueVersion.getValue();
        byte[] tab_bytes = value.getValue();
        return new String(tab_bytes);
    }

    public static Iterator<KeyValueVersion> getAuthorIterator(KVStore kvStore, String author_name){
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(Author.AUTHOR);
        tab_key.add(author_name);
        Key myKey = Key.createKey(tab_key);
        return kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
    }
    public static Iterator<KeyValueVersion> getBookIterator(KVStore kvStore, String title){
        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(Book.BOOK);
        tab_key.add(title);
        Key myKey = Key.createKey(tab_key);
        return kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
    }

    public static Author getAuthorFromName(String name){
        KVStore kvStore = getKvstore();
        Author author = new Author();
        Iterator<KeyValueVersion> i = getAuthorIterator(kvStore, name);
        if (i.hasNext()){
            Key k = i.next().getKey();
            String author_str = Utils.getValueFromKey(kvStore, k);
            author.deserialize(author_str);
        }
        return author;
    }

    public static Book getBookFromTitle(String title){
        KVStore kvStore = getKvstore();
        Book book = new Book();
        Iterator<KeyValueVersion> i = getBookIterator(kvStore, title);
        if (i.hasNext()){
            Key k = i.next().getKey();
            String book_str = Utils.getValueFromKey(kvStore, k);
            book.deserialize(book_str);
        }
        return book;
    }

    /**
     * Get value from a given key
     * @param store: KVStore
     * @param key: key to find
     * @return : String
     */
    public String getValueFromStore(KVStore store, Key key){
        ValueVersion valueVersion = store.get(key);
        Value value = valueVersion.getValue();
        byte[] table = value.getValue();
        return new String(table);
    }


    /**
     * Get  random integer in prodided range
     * @param min_: minimal value
     * @param max_: maximal value
     * @return: random integer
     */
    public static int getRandomInteger(int min_, int max_){
        return (new Random()).nextInt((max_ - min_) + 1) + min_;
    }

    /**
     * Generate a random author name
     * @return author name (String)
     */
    public static String generateRandomAuthorName(){
        int num = Utils.getRandomInteger(0, Parameters.NB_AUTHOR_PER_THREAD*Parameters.NB_THREAD_CREATION);
        return  "Author_" + num;
    }
}
