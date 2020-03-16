package main;

import java.util.Random;

public class Utils {
    /**
     * Get  random integer in prodided range
     * @param min_: minimal value
     * @param max_: maximal value
     * @return random integer
     */
    public static int getRandomInteger(int min_, int max_){
        return (new Random()).nextInt((max_ - min_) + 1) + min_;
    }

    /**
     * Generate a random author name
     * @return author name (String)
     */
    public static String generateRandomAuthorName(){
        int num = getRandomInteger(0, Parameters.NB_AUTHOR_PER_THREAD*Parameters.NB_THREAD_CREATION);
        return  "Author_" + num;
    }

    /**
     * Return a separator line
     * @return String ("=" repeated 50 times)
     */
    public static String getSeparatorLine(){
        return new String(new char[50]).replace("\0", "=");
    }
}
