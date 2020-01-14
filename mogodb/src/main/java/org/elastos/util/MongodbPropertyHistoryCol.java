package org.elastos.util;

import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;

public class MongodbPropertyHistoryCol {
    private static Logger logger = LoggerFactory.getLogger(MongodbPropertyHistoryCol.class);
    private MongoCollection<Document> collection;

    public void init(MongoCollection<Document> var) {
        collection = var;
    }

    public void delHistory(String did, String propertyKey) {
        collection.deleteOne(and(eq("did", did), exists(propertyKey)));
    }

    public List<Long> findHistory(String did, String propertyKey) {
        Document doc = collection.find(and(eq("did", did), exists(propertyKey))).first();
        if (null != doc) {
            List<Long> ids = doc.getList(propertyKey, Long.class);
            return ids;
        } else {
            return new ArrayList<>();
        }
    }

    public UpdateResult addsertHistory(String did, String propertyKey, Long id) {
        try {
            UpdateResult updateResult = collection.updateOne(and(eq("did", did),exists(propertyKey)),
                    Updates.addToSet(propertyKey, id),
                    new UpdateOptions().upsert(true));
            if (!MongodbUtil.isModified(updateResult)) {
                logger.info("addsertHistory updateOne of did:" + did + " property:" + propertyKey);
            }
            return updateResult;
        } catch (Exception e) {
            logger.error("addsertHistory exception of did:"+ did
                    +" propertyKey:" + propertyKey
                    +" id:" + id
                    + "exception msg:" + e.getMessage());
            return null;
        }
    }
}
