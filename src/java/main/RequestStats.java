package main;

import entity.oracle.Book;
import entity.oracle.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class RequestStats {

    /**
     * Time a function:
     *      - Repeat the same function execution many times
     *
     * @param ftc: function to measure
     */
    public static void measureRequest(Runnable ftc) {

        long startTime, endTime, duration;

        // Start timer
        startTime = System.nanoTime();

        ftc.run();

        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;

        // Display
        System.out.println("Total time operation (" + Parameters.NUMBER_REQUEST + " requests): " + duration + "ms");
        System.out.println("Time operation per request: " + duration / Parameters.NUMBER_REQUEST + "ms");
        System.out.println("=".repeat(50));
    }

    /**
     * Measure the request "request_1_1":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static void request_1_1_Loops(){
        String author_name;
        // Repeat the same operation
        for (int i = 0; i < Parameters.NUMBER_REQUEST; ++i) {
            // Create random author name
            author_name = Utils.generateRandomAuthorName();

            // Perform function execution
            Question.request_1_1(author_name);
        }
    }

    /**
     * Measure the request "request_1_2":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static void request_1_2_Loops(){
        String author_name;
        // Repeat the same operation
        for (int i = 0; i < Parameters.NUMBER_REQUEST; ++i) {
            // Create random author name
            author_name = Utils.generateRandomAuthorName();

            // Perform function execution
            Question.request_1_2(author_name);
        }
    }

    /**
     * Measure the request "request_1_2":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static void request_1_3_Loops(){
        // Repeat the same operation
        for (int i = 0; i < Parameters.NUMBER_REQUEST; ++i) {
            // Perform function execution
            Question.request_1_3();
        }
    }

    /**
     * Measure the request "request_2_1":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static void request_2_1_Loops(){
        // Repeat the same operation
        for (int i = 0; i < Parameters.NUMBER_REQUEST; ++i) {
            String category = Category.getRandomCategory();
            // Perform function execution
            Question.request_2_1(category);
        }
    }

    /**
     * Measure the request "request_2_2":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static void request_2_2_Loops(){
        // Repeat the same operation
        for (int i = 0; i < Parameters.NUMBER_REQUEST; ++i) {
            String category = Category.getRandomCategory();
            // Perform function execution
            Question.request_2_2(category);
        }
    }

    /**
     * Measure the request "request_2_3":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static void request_2_3_Loops(){
        // Repeat the same operation
        for (int i = 0; i < Parameters.NUMBER_REQUEST; ++i) {
            String category = Category.getRandomCategory();
            String letter = Arrays.asList("A", "B", "C").get(new Random().nextInt(3));
            // Perform function execution
            Question.request_2_3(category, letter);
        }
    }

    /**
     * Measure the request "request_3":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static void request_3_Loops(){
        int n_authors = Parameters.NB_THREAD_CREATION * Parameters.NB_AUTHOR_PER_THREAD;

        // Repeat the same operation
        for (int i = 0; i < Parameters.NUMBER_REQUEST; ++i) {
            // Perform function execution
            n_authors =  Question.request_3(n_authors);
        }
    }

    /**
     * Measure the request "request_4_1":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static void request_4_1(){
        String book_title;

        // Load all books title
        ArrayList<String> books_title = Utils.getAllBooksTitle(Utils.getKvstore());

        // Repeat deletion
        for (int i = 0; i < Parameters.NUMBER_REQUEST && books_title.size() > 0; ++i) {
            book_title = books_title.get(Utils.getRandomInteger(0, books_title.size()-1));
            books_title.remove(book_title);

            // Perform function execution
            Question.request_4_1(book_title);
        }
    }

    /**
     * Measure the request "request_4_1":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static void request_4_2(){
        String author_name;

        // Load all books title
        ArrayList<String> authors_name = Utils.getAllAuthorsName(Utils.getKvstore());

        // Repeat deletion
        for (int i = 0; i < Parameters.NUMBER_REQUEST && authors_name.size() > 0; ++i) {
            author_name = authors_name.get(Utils.getRandomInteger(0, authors_name.size()-1));
            authors_name.remove(author_name);

            // Perform function execution
            Question.request_4_2(author_name);
        }
    }
}
