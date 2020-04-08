package org.elastos.util;

import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.elastos.POJO.DidDoc;
import org.elastos.POJO.PropertyDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

public class MongodbDidInfoCol {
    private static Logger logger = LoggerFactory.getLogger(MongodbDidInfoCol.class);
    private MongoCollection<Document> collection;

    public void init(MongoCollection<Document> var) {
        collection = var;
    }

    private DidDoc docToProperty(Document doc) {
        DidDoc did = new DidDoc();
        did.setDid(doc.getString("_id"));
        did.setPublicKey(doc.getString("publicKey"));
        did.setStatus(doc.getInteger("status"));
        return did;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public void setCollection(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    //String did:,
    //String publicKey:;
    //Integer status:,
    public void upsertDidInfo(String did, String publicKey, Integer status) {
        UpdateResult updateResult = collection.updateOne(eq("_id", did),
                combine(Updates.set("publicKey", publicKey), Updates.set("status", status)),
                new UpdateOptions().upsert(true));
        if (!MongodbUtil.isModified(updateResult)) {
            logger.info("upsertDidInfo updateMany of did:" + did);
        }
    }

    public UpdateOneModel<Document> upsertDidInfoDoc(String did, String publicKey, Integer status) {
        UpdateOneModel<Document> uom = new UpdateOneModel<>(eq("_id", did), combine(Updates.set("publicKey", publicKey), Updates.set("status", status)), new UpdateOptions().upsert(true));
        return uom;
    }

    public DidDoc findDidInfo(String did) {
        Document doc = collection.find(eq("_id", did)).first();
        if (null != doc) {
            return docToProperty(doc);
        } else {
            return null;
        }
    }

    public long getDidSum() {
        long count = collection.countDocuments();
        return count;
    }

    public List<String> getDidList(int start, int size) {
        FindIterable<Document> docs = collection.find().limit(size).skip(start);
        List<String> didList = new ArrayList<>();
        for (Document doc : docs) {
            String did = doc.getString("_id");
            didList.add(did);
        }
        return didList;
    }

    public void delDidInfo(String did) {
        collection.deleteOne(eq("_id", did));
    }
}

