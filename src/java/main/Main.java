package main;

import entity.oracle.Author;
import entity.oracle.Book;
import oracle.kv.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Main {

    public static void main(String[] args) {
        // Clear DataStore
        Utils.cleanDataStore();

        // Run question 1
        Question.requete_1();
//        Author author = Utils.getAuthorFromName("Author_55");
//        Book book = Utils.getBookFromTitle(author.getBooks().get(2).getTitle());
//
//        Question.createData();
//        Main main = new Main();
//        main.showAllAuthors();

    }

    public void showAllAuthors(){
        KVStore kvStore = Utils.getKvstore();

        ArrayList<String> tab_key = new ArrayList<>();
//        tab_key.add(Author.AUTHOR);
        tab_key.add(Book.BOOK);
        tab_key.add("title_C");
        tab_key.add("78");
        Key myKey = Key.createKey(tab_key);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
        int cpt = 0;
        while (i.hasNext()){
            Key k = i.next().getKey();

            String valeur = Utils.getValueFromKey(kvStore, k);

            System.out.println("cle = " + k.toString());
            System.out.println("valeur = " + valeur);
            cpt++;

//            Author author = new Author();
//            author.deserialize(valeur);
//            author.toString();

            Book book = new Book();
            book.deserialize(valeur);
            book.toString();
        }

        System.out.println("Nb: " + cpt);
    }
}
