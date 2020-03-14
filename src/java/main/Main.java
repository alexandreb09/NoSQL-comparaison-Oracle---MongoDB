package main;

import entity.oracle.*;
import oracle.kv.*;

import java.util.ArrayList;
import java.util.Iterator;

import static main.Question.createData;

public class Main {

    public static void main(String[] args) {
        // Clear DataStore
        Utils.cleanDataStore();

        // Try creating data
        if (createData()) {
            // =============================================================//
            // Perform 1000 requests doing:                                 //
            //      - Generate random author name                           //
            //      - Find all books associated to this author              //
            // =============================================================//
            RequestStats.measureRequest(RequestStats::request_1_1_Loops);

            // =============================================================//
            // Perform 1000 requests doing:                                 //
            //      - Generate random author name                           //
            //      - Find all books written to this author                 //
            //      - For each of these books:                              //
            //              - Generate a random integer                     //
            //              - Find associated author                        //
            //              - Add relation                                  //
            // =============================================================//
            RequestStats.measureRequest(RequestStats::request_1_2_Loops);

            // =============================================================//
            // Operations:                                                  //
            //      - Search all books with:                                //
            //          - "A" in the title                                  //
            //          - price bellow 10                                   //
            //      For all of these books:                                 //
            //          - Increase price (10%)                              //
            // =============================================================//
            RequestStats.measureRequest(RequestStats::request_1_3_Loops);


            // =============================================================//
            // Operations:                                                  //
            //      - For all books:                                        //
            //          - Randomly select a category                        //
            //          - Add category to the book                          //
            //          - Add the book to the list of the given categories  //
            //          - Add the author to the list of the given           //
            //            categories                                        //
            // =============================================================//
            Question.addCategoryAllBooks();


            // =============================================================//
            // Operations:                                                  //
            //      - Select a random Category                              //
            //      - Get all books of this category                        //
            // =============================================================//
            RequestStats.measureRequest(RequestStats::request_2_1_Loops);

            // =============================================================//
            // Operations:                                                  //
            //      - Select a random Category                              //
            //      - Get all authors that have written a book from this    //
            //        category                                              //
            // =============================================================//
            RequestStats.measureRequest(RequestStats::request_2_2_Loops);

            // =============================================================//
            // Operations:                                                  //
            //      - Select a random Category                              //
            //      - Select a random Letter                                //
            //      - Select all books from this category and having this   //
            //        letter in the title                                   //
            // =============================================================//
            RequestStats.measureRequest(RequestStats::request_2_3_Loops);

            // =============================================================//
            // Operations:                                                  //
            //      - Select a random number N between 1 and 10             //
            //      - Wait for a random time between 0.01 and 0.5s          //
            //      - Insert N authors in database                          //
            //           - Each author write to new books                   //
            //      - Generate random author name                           //
            //      - Search all books of this author                       //
            //      - Search all books from History category and with a     //
            //        "B" in the title                                      //
            // =============================================================//
            RequestStats.measureRequest(RequestStats::request_3_Loops);

            // =============================================================//
            // Delete one author from all the kvstore                       //
            //      - Delete author from "Author" table                     //
            //      - For all book (written by this author):                //
            //          - If the author is the only writter:                //
            //              -> delete the book                              //
            //          - Else:                                             //
            //              -> remove the author and update DB              //
            //              -> Delete the book from the "Book categrory"    //
            //      - Delete the author from the "Author category"          //
            // =============================================================//
            RequestStats.measureRequest(RequestStats::request_4_1);

            // =============================================================//
            // Delete one Book from all the kvstore                         //
            //      - Delete book from "Book" table                         //
            //      - For all authors (of the book):                        //
            //          -> remove the book from the book's author           //
            //      - Delete the book from the "Book category"              //
            //                                                              //
            // Note: if an author has only written this book, the author    //
            //       is NOT removed                                         //
            // =============================================================//
            RequestStats.measureRequest(RequestStats::request_4_2);
        }


        /* ******************************************** */
        /*              VERIFICATION                    */
        /* ******************************************** */

//        Main main = new Main();
//        main.showAllAuthors();
    }

    public void showAllAuthors(){
        KVStore kvStore = Utils.getKvstore();

        ArrayList<String> tab_key = new ArrayList<>();
        tab_key.add(Category.CATEGORY);
        tab_key.add(Author.AUTHOR);
        tab_key.add(Parameters.BOOKS_CATEGORY.get(0));
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

//            Book book = new Book();
//            book.deserialize(valeur);
//            book.toString();

//            CategoryBook categoryBook = new CategoryBook();
//            categoryBook.deserialize(valeur);
//            categoryBook.toString();

//            CategoryAuthor categoryAuthor = new CategoryAuthor();
//            categoryAuthor.deserialize(valeur);
//            categoryAuthor.toString();
        }

        System.out.println("Nb: " + cpt);
    }
}
