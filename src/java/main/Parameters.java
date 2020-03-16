package main;

import java.util.Arrays;
import java.util.List;

public class Parameters {

    /* ******************************** */
    /*      CONFIG ORACLE               */
    /* ******************************** */
    public static final String STORE_NAME = "kvstore";
    public static final String HOST_NAME = "ALEX-PC";
    public static final String HOST_PORT = "5000";

    /* ******************************** */
    /*      CONFIG MONGODB              */
    /* ******************************** */
    public static final String MONGO_HOST_NAME      = "localhost";
    public static final int MONGO_PORT              = 27017;
    public static final String MONGO_DATABASE_NAME  = "MongoPerformanceTest";


    /* ******************************** */
    /*      PARAMETERS                  */
    /* ******************************** */
    public static final int NB_AUTHOR_PER_THREAD    = 1000;         // Nombre d'auteurs créés dans une thread
    public static final int NB_THREAD_CREATION      = 10;           // Nombre de thread dans la création des auteurs et des livres

    public static final int BOOK_PRICE_MIN          = 1;            // Min price
    public static final int BOOK_PRICE_MAX          = 100;          // Max price

    public static final int NUMBER_REQUEST          = 500;

    // Here we use 10 as we want all books with a price lower than 10
    public static final int BOOK_PRICE_RANGE        = 10;

    public static final List<String> BOOKS_CATEGORY = Arrays.asList("HISTORY", "MANGA", "COMICS", "SCIENCES", "MUSIC", "SPORT", "NATURE");

    // Sleeping time in MS
    public static final int SLEEPING_TIME_MIN       = 1;
    public static final int SLEEPING_TIME_MAX       = 50;
}
