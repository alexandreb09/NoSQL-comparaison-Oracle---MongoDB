package main;

import entity.oracle.Author;
import entity.oracle.Book;
import oracle.kv.*;

import java.util.ArrayList;
import java.util.Iterator;

public class Main {

    public static void main(String[] args) {
        // Clear DataStore
        Utils.cleanDataStore();

        // Run question 1
        Question.requete_1();


//        Main main = new Main();
//        main.showAllAuthors();

    }

    public void showAllAuthors(){
        KVStore kvStore = Utils.getKvstore();

        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(Author.AUTHOR);
        Key myKey = Key.createKey(tab_key);

        Iterator<KeyValueVersion> i = kvStore.storeIterator(Direction.UNORDERED, 0, myKey, null, null);
        int cpt = 0;
        while (i.hasNext()){
            Key k = i.next().getKey();

            ValueVersion valueVersion = kvStore.get(k);
            Value value = valueVersion.getValue();
            byte[] tab_bytes = value.getValue();
            String valeur = new String(tab_bytes);
            kvStore.delete(k);

            System.out.println("cle = " + k.toString());
            System.out.println("valeur = " + valeur);
            cpt++;
        }

        System.out.println("Nb d'auteurs: " + cpt);
    }
}
