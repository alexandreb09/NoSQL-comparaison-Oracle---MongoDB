package main;

import static main.QuestionOracle.createData;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();

        // Run all tests with Mongo DB
        main.mongoPerformanceTests();

        // Run all tests with Oracle NoSQL DB
        main.oraclePerformanceTests();
    }

    public void mongoPerformanceTests(){
        // Clear DataBase
        UtilsMongo.cleanDataBase();

        // Try creating data
        if (QuestionMongo.createData()){
            // =============================================================//
            // Perform 1000 requests doing:                                 //
            //      - Generate random author name                           //
            //      - Find all books associated to this author              //
            // =============================================================//
            RequestStatsMongo.measureRequest(RequestStatsMongo::request_1_1_Loops);

            // =============================================================//
            // Operations:                                                  //
            //      - From a given author name                              //
            //      - Find all books written to this author                 //
            //      - For each of these books:                              //
            //              - Generate a random author name                 //
            //              - Find this author name                         //
            //              - Add this author to the author list in book    //
            //              - Add the book to the book list in this author  //
            // =============================================================//
            RequestStatsMongo.measureRequest(RequestStatsMongo::request_1_2_Loops);

            // =============================================================//
            // Operations:                                                  //
            //      - Search all books with:                                //
            //          - "letter" in the title                             //
            //          - "price" rounded to the nearest ten                //
            //              -> 13 => [10, 20]                               //
            //              -> 29 => [20, 30]                               //
            //      - For all of these books:                               //
            //          - Increase price (10%)                              //
            // =============================================================//
            RequestStatsMongo.measureRequest(RequestStatsMongo::request_1_3_Loops);

            // =============================================================//
            // Operations:                                                  //
            //      - For all books:                                        //
            //          - Randomly select a category                        //
            //          - Add category to the book                          //
            //          - Add the book to the list of the given categories  //
            //          - Add the author to the list of the given           //
            //            categories                                        //
            // =============================================================//
            QuestionMongo.addCategoryAllBooks();

            // =============================================================//
            // Operations:                                                  //
            //      - Select a random Category                              //
            //      - Get all books of this category                        //
            // =============================================================//
            RequestStatsMongo.measureRequest(RequestStatsMongo::request_2_1_Loops);

            // =============================================================//
            // Operations:                                                  //
            //      - Select a random Category                              //
            //      - Get all authors that have written a book from this    //
            //        category                                              //
            // =============================================================//
            RequestStatsMongo.measureRequest(RequestStatsMongo::request_2_2_Loops);

            // =============================================================//
            // Operations:                                                  //
            //      - Select a random Category                              //
            //      - Select all books from this category and having a      //
            //      "B" in its title                                        //
            // =============================================================//
            RequestStatsMongo.measureRequest(RequestStatsMongo::request_2_3_Loops);

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
            RequestStatsMongo.measureRequest(RequestStatsMongo::request_3_Loops);

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
            RequestStatsMongo.measureRequest(RequestStatsMongo::request_4_1_Loops);
        }
    }

    public void oraclePerformanceTests(){
        // Clear DataStore
        UtilsOracle.cleanDataStore();

        // Try creating data
        if (createData()) {
            // =============================================================//
            // Perform 1000 requests doing:                                 //
            //      - Generate random author name                           //
            //      - Find all books associated to this author              //
            // =============================================================//
            RequestStatsOracle.measureRequest(RequestStatsOracle::request_1_1_Loops);

            // =============================================================//
            // Perform 1000 requests doing:                                 //
            //      - Generate random author name                           //
            //      - Find all books written to this author                 //
            //      - For each of these books:                              //
            //              - Generate a random integer                     //
            //              - Find associated author                        //
            //              - Add relation                                  //
            // =============================================================//
            RequestStatsOracle.measureRequest(RequestStatsOracle::request_1_2_Loops);

            // =============================================================//
            // Operations:                                                  //
            //      - Search all books with:                                //
            //          - "A" in the title                                  //
            //          - price bellow 10                                   //
            //      For all of these books:                                 //
            //          - Increase price (10%)                              //
            // =============================================================//
            RequestStatsOracle.measureRequest(RequestStatsOracle::request_1_3_Loops);


            // =============================================================//
            // Operations:                                                  //
            //      - For all books:                                        //
            //          - Randomly select a category                        //
            //          - Add category to the book                          //
            //          - Add the book to the list of the given categories  //
            //          - Add the author to the list of the given           //
            //            categories                                        //
            // =============================================================//
            QuestionOracle.addCategoryAllBooks();


            // =============================================================//
            // Operations:                                                  //
            //      - Select a random Category                              //
            //      - Get all books of this category                        //
            // =============================================================//
            RequestStatsOracle.measureRequest(RequestStatsOracle::request_2_1_Loops);

            // =============================================================//
            // Operations:                                                  //
            //      - Select a random Category                              //
            //      - Get all authors that have written a book from this    //
            //        category                                              //
            // =============================================================//
            RequestStatsOracle.measureRequest(RequestStatsOracle::request_2_2_Loops);

            // =============================================================//
            // Operations:                                                  //
            //      - Select a random Category                              //
            //      - Select a random Letter                                //
            //      - Select all books from this category and having this   //
            //        letter in the title                                   //
            // =============================================================//
            RequestStatsOracle.measureRequest(RequestStatsOracle::request_2_3_Loops);

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
            RequestStatsOracle.measureRequest(RequestStatsOracle::request_3_Loops);

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
            RequestStatsOracle.measureRequest(RequestStatsOracle::request_4_1);

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
            RequestStatsOracle.measureRequest(RequestStatsOracle::request_4_2);
        }
    }
}
