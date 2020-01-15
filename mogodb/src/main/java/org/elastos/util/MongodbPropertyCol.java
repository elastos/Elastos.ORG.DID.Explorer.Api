package org.elastos.util;

import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.elastos.POJO.PropertyDoc;
import org.elastos.entity.ChainDidProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;

public class MongodbPropertyCol {
    private static Logger logger = LoggerFactory.getLogger(MongodbPropertyCol.class);
    private String blockHeightField = "block_height";
    private MongoCollection<Document> collection;

    public void init(MongoCollection<Document> var) {
        collection = var;
    }

    public UpdateResult updateBlockHeight(Integer blockHeight) {
        UpdateResult ret = collection.updateOne(exists(blockHeightField),
                Updates.set(blockHeightField, blockHeight), new UpdateOptions().upsert(true));
        return ret;
    }

    public Integer findBlockHeight() {
        Document doc = collection.find(exists(blockHeightField)).first();
        if (null != doc) {
            return doc.getInteger(blockHeightField);
        } else {
            return 0;
        }
    }

    public PropertyDoc findProperty(String did, String propertyKey) {
        Document doc = collection.find(and(eq("did", did), exists(propertyKey))).first();
        if (null != doc) {
            return docToProperty(doc);
        } else {
            return null;
        }
    }

    public List<PropertyDoc> findAllProperties(String did) {
        FindIterable<Document> docs = collection.find(eq("did", did));
        List<PropertyDoc> propertyDocs = new ArrayList<>();
        for (Document document : docs) {
            propertyDocs.add(docToProperty(document));
        }
        return propertyDocs;
    }

    private PropertyDoc docToProperty(Document doc){
        PropertyDoc propertyDoc = new PropertyDoc();
        propertyDoc.setId(doc.getLong("id"));
        propertyDoc.setDid(doc.getString("did"));
        propertyDoc.setPropertyKey(doc.getString("propertyKey"));
        propertyDoc.setPropertyValue(doc.getString(propertyDoc.getPropertyKey()));
        propertyDoc.setStatus(doc.getInteger("status"));
        propertyDoc.setTxid(doc.getString("txid"));
        propertyDoc.setBlockTime(doc.getInteger("blockTime"));
        propertyDoc.setHeight(doc.getInteger("height"));
        propertyDoc.setLocalSystemTime(doc.getDate("localSystemTime"));
        return propertyDoc;
    }

    private Document ChainPropertyToDoc(ChainDidProperty property) {
        Document propertyDoc = new Document("id", property.getId())
                .append(property.getPropertyKey(), property.getPropertyValue())
                .append("status", property.getPropertyStatus())
                .append("txid", property.getTxid())
                .append("blockTime", property.getBlockTime())
                .append("height", property.getHeight())
                .append("localSystemTime", property.getLocalSystemTime());
        return propertyDoc;
    }

    //String did:;
    //String propertyKey: value;
    //Long id:;
    //Integer status:;
    //String txid:;
    //Integer blockTime:;
    //Integer height:;
    public UpdateResult upsertProperty(ChainDidProperty property) {
        String did = property.getDid();
        String propertyKey = property.getPropertyKey();
        Document doc = ChainPropertyToDoc(property);
        UpdateResult updateResult = collection.updateOne(and(eq("did", did), eq("propertyKey", propertyKey)),
                new Document("$set", doc),
                new UpdateOptions().upsert(true));
        if (!MongodbUtil.isModified(updateResult)) {
            logger.info("upsertProperty updateOne of did:" + did + " property:" + propertyKey);
        }
        return updateResult;
    }

    public void delProperty(String did, String propertyKey) {
        collection.deleteOne(and(eq("did", did), exists(propertyKey)));
    }
}