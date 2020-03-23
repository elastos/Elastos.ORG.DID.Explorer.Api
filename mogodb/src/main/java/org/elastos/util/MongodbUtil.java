package org.elastos.util;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.*;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MongodbUtil {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    public static void init(String host, Integer port, String dbName){
        mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(builder ->
                                builder.hosts(Arrays.asList(new ServerAddress(host,port))))
                        .build());
        database = mongoClient.getDatabase(dbName);

    }

    public static MongoCollection<Document>getCollection(String collectionName){
        return database.getCollection(collectionName);
    }

    public static boolean isModified(UpdateResult updateResult) {
        if ((updateResult.getModifiedCount() != 0)
                || (updateResult.getUpsertedId() != null)) {
            return true;
        } else {
            return false;
        }
    }

    public static BulkWriteResult bulkWriteUpdate(MongoCollection<Document> collection, List<WriteModel<Document>> requests){
        BulkWriteResult bulkWriteResult = collection.bulkWrite(requests);
        return bulkWriteResult;
    }
}
