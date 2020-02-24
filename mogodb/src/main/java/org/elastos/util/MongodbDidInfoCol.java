package org.elastos.util;

import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.elastos.POJO.DidDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

public class MongodbDidInfoCol {
    private static Logger logger = LoggerFactory.getLogger(MongodbDidInfoCol.class);
    private MongoCollection<Document> collection;

    public void init(MongoCollection<Document> var) {
        collection = var;
    }

    private DidDoc docToProperty(Document doc){
        DidDoc did = new DidDoc();
        did.setDid(doc.getString("_id"));
        did.setPublicKey(doc.getString("publicKey"));
        did.setStatus(doc.getInteger("status"));
        return did;
    }

    //String did:,
    //String publicKey:;
    //Integer status:,
    public void upsertDidInfo(String did, String publicKey, Integer status) {
        UpdateResult updateResult = collection.updateOne(eq("_id", did),
                combine(Updates.set("publicKey", publicKey),Updates.set("status", status)),
                new UpdateOptions().upsert(true));
        if (!MongodbUtil.isModified(updateResult)) {
            logger.info("upsertDidInfo updateMany of did:"+ did);
        }
    }

    public DidDoc findDidInfo(String did){
        Document doc = collection.find(eq("_id", did)).first();
        if (null != doc) {
            return docToProperty(doc);
        } else {
            return null;
        }
    }

    public void delDidInfo(String did) {
        collection.deleteOne(eq("_id", did));
    }
}

