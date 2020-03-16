package main;

import entity.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

public class RequestStatsOracle {

    /**
     * Time a function:
     *      - Repeat the same function execution many times
     *
     * @param ftc: function to measure
     */
    public static void measureRequest(Supplier<Integer> ftc) {
        Integer n_request;
        long startTime, endTime, duration;

        // Start timer
        startTime = System.nanoTime();

        n_request = ftc.get();

        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;

        // Display
        System.out.println("Total time operation (" + n_request + " requests): " + duration + "ms");
        System.out.println("Time operation per request: " + duration / n_request + "ms");
        System.out.println(Utils.getSeparatorLine());
    }

    /**
     * Measure the request "request_1_1":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static Integer request_1_1_Loops(){
        System.out.println("\t\t\t ORACLE NoSQL DB : REQUEST 1.1");
        String author_name;
        int n_request = Parameters.NUMBER_REQUEST;
        // Repeat the same operation
        for (int i = 0; i < n_request; ++i) {
            // Create random author name
            author_name = Utils.generateRandomAuthorName();

            // Perform function execution
            QuestionOracle.request_1_1(author_name);
        }
        return n_request;
    }

    /**
     * Measure the request "request_1_2":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static Integer request_1_2_Loops(){
        System.out.println("\t\t\t ORACLE NoSQL DB : REQUEST 1.2");
        String author_name;
        int n_request = Parameters.NUMBER_REQUEST;
        // Repeat the same operation
        for (int i = 0; i < n_request; ++i) {
            // Create random author name
            author_name = Utils.generateRandomAuthorName();

            // Perform function execution
            QuestionOracle.request_1_2(author_name);
        }
        return n_request;
    }

    /**
     * Measure the request "request_1_2":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static Integer request_1_3_Loops(){
        System.out.println("\t\t\t ORACLE NoSQL DB : REQUEST 1.3");
        int n_request = Parameters.NUMBER_REQUEST/10;
        // Repeat the same operation
        for (int i = 0; i < n_request; ++i) {
            // Generate random letter - price
            String letter = Arrays.asList("A", "B", "C").get(new Random().nextInt(3));
            int price = Utils.getRandomInteger(0,100);

            // Perform function execution
            QuestionOracle.request_1_3(letter, price);
        }
        return n_request;
    }

    /**
     * Measure the request "request_2_1":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static Integer request_2_1_Loops(){
        System.out.println("\t\t\t ORACLE NoSQL DB : REQUEST 2.1");
        // Repeat the same operation
        int n_request = Parameters.NUMBER_REQUEST;
        // Repeat the same operation
        for (int i = 0; i < n_request; ++i) {
            String category = Category.getRandomCategory();
            // Perform function execution
            QuestionOracle.request_2_1(category);
        }
        return n_request;
    }

    /**
     * Measure the request "request_2_2":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static Integer request_2_2_Loops(){
        System.out.println("\t\t\t ORACLE NoSQL DB : REQUEST 2.2");
        int n_request = Parameters.NUMBER_REQUEST;
        // Repeat the same operation
        for (int i = 0; i < n_request; ++i) {
            String category = Category.getRandomCategory();
            // Perform function execution
            QuestionOracle.request_2_2(category);
        }
        return n_request;
    }

    /**
     * Measure the request "request_2_3":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static Integer request_2_3_Loops(){
        System.out.println("\t\t\t ORACLE NoSQL DB : REQUEST 2.3");
        int n_request = Parameters.NUMBER_REQUEST;
        // Repeat the same operation
        for (int i = 0; i < n_request; ++i) {
            String category = Category.getRandomCategory();
            String letter = Arrays.asList("A", "B", "C").get(new Random().nextInt(3));
            // Perform function execution
            QuestionOracle.request_2_3(category, letter);
        }
        return n_request;
    }

    /**
     * Measure the request "request_3":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static Integer request_3_Loops(){
        System.out.println("\t\t\t ORACLE NoSQL DB : REQUEST 3");
        int n_authors = Parameters.NB_THREAD_CREATION * Parameters.NB_AUTHOR_PER_THREAD;

        int n_request = Parameters.NUMBER_REQUEST/10;
        // Repeat the same operation
        for (int i = 0; i < n_request; ++i) {
            // Perform function execution
            n_authors =  QuestionOracle.request_3(n_authors);
        }
        return n_request;
    }

    /**
     * Measure the request "request_4_1":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static Integer request_4_1(){
        System.out.println("\t\t\t ORACLE NoSQL DB : REQUEST 4.1");
        String book_title;

        // Load all books title
        ArrayList<String> books_title = UtilsOracle.getAllBooksTitle(UtilsOracle.getKvstore());

        int n_request = Parameters.NUMBER_REQUEST/10;
        // Repeat deletion
        for (int i = 0; i < n_request && books_title.size() > 0; ++i) {
            book_title = books_title.get(Utils.getRandomInteger(0, books_title.size()-1));
            books_title.remove(book_title);

            // Perform function execution
            QuestionOracle.request_4_1(book_title);
        }
        return n_request;
    }

    /**
     * Measure the request "request_4_1":
     *      - The operation is repeated the number of time provided in Parameters.NUMBER_REQUEST parameters
     */
    public static Integer request_4_2(){
        System.out.println("\t\t\t ORACLE NoSQL DB : REQUEST 4.2");
        String author_name;

        // Load all books title
        ArrayList<String> authors_name = UtilsOracle.getAllAuthorsName(UtilsOracle.getKvstore());

        int n_request = Parameters.NUMBER_REQUEST/10;
        // Repeat deletion
        for (int i = 0; i < n_request && authors_name.size() > 0; ++i) {
            author_name = authors_name.get(Utils.getRandomInteger(0, authors_name.size()-1));
            authors_name.remove(author_name);

            // Perform function execution
            QuestionOracle.request_4_2(author_name);
        }
        return n_request;
    }
}
