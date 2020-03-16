package main;

import com.mongodb.*;
import entity.Author;
import entity.mongo.AuthorMongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;

import static main.Parameters.*;

public class UtilsMongo {

    public static void cleanDataBase(){
        try {
            MongoClient mongoClient = new MongoClient(MONGO_HOST_NAME, MONGO_PORT);
            mongoClient.getDB(MONGO_DATABASE_NAME).dropDatabase();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static DBCollection getCollection(String collection_name){
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(MONGO_HOST_NAME, MONGO_PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (mongoClient != null){
            return mongoClient.getDB(MONGO_DATABASE_NAME).getCollection(collection_name);
        }
        return null;
    }

    public static AuthorMongo getAuthorFromName(DBCollection collection, String author_name){
        BasicDBObject findQuery = new BasicDBObject("_id", author_name);

        // Get author (mongo db object)
        DBObject author_obj = Objects.requireNonNull(collection).findOne(findQuery);

        // Deserialize Author obj
        return AuthorMongo.deserialize(author_obj);
    }


    public static ArrayList<String> getAllBooksTitle(){
        ArrayList<String> authors_name = new ArrayList<>();

        DBCollection collection = getCollection(Author.AUTHOR);
        DBCursor c = Objects.requireNonNull(collection).find();

        while (c.hasNext()){
            authors_name.add(c.next().get("firstName").toString());
        }
        return authors_name;
    }
}
