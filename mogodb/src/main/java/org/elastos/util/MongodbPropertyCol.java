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

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.set;

public class MongodbPropertyCol {
    private static Logger logger = LoggerFactory.getLogger(MongodbPropertyCol.class);
    private String didTableIdField = "did_table_id";
    private String blockHeightField = "block_height";
    private String blockHeightCountField = "block_height_count";
    private String didTxidField = "did_txid";
    private String didTxidCountField = "did_txid_count";
    private MongoCollection<Document> collection;

    public void init(MongoCollection<Document> var) {
        collection = var;
        Document did = new Document("did", 1);
        collection.createIndex(did);
        Document propertyKey = new Document("propertyKey", 1);
        collection.createIndex(propertyKey);
    }

    public UpdateResult updateDidTableId(Long id) {
        UpdateResult ret = collection.updateOne(exists(didTableIdField),
                set(didTableIdField, id), new UpdateOptions().upsert(true));
        return ret;
    }

    public Long findDidTableId() {
        Document doc = collection.find(exists(didTableIdField)).first();
        if (null != doc) {
            return doc.getLong(didTableIdField);
        } else {
            return 0L;
        }
    }

    public UpdateResult updateBlockHeight(Integer blockHeight) {
        UpdateResult ret = collection.updateOne(exists(blockHeightField),
                set(blockHeightField, blockHeight), new UpdateOptions().upsert(true));
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

    public UpdateResult incBlockHeightCount() {
        UpdateResult ret = collection.updateOne(exists(blockHeightCountField),
                Updates.inc(blockHeightCountField, 1), new UpdateOptions().upsert(true));
        return ret;
    }

    public Integer findBlockHeightCount() {
        Document doc = collection.find(exists(blockHeightField)).first();
        if (null != doc) {
            return doc.getInteger(blockHeightField);
        } else {
            return 0;
        }
    }

    public UpdateResult updateDidTxid(String txid) {
        UpdateResult ret = collection.updateOne(exists(didTxidField),
                set(didTxidField, txid), new UpdateOptions().upsert(true));
        return ret;
    }

    public String findDidTxid() {
        Document doc = collection.find(exists(didTxidField)).first();
        if (null != doc) {
            return doc.getString(didTxidField);
        } else {
            return "";
        }
    }

    public UpdateResult incDidTxidCount() {
        UpdateResult ret = collection.updateOne(exists(didTxidCountField),
                Updates.inc(didTxidCountField, 1), new UpdateOptions().upsert(true));
        return ret;
    }

    public Integer findDidTxidCount() {
        Document doc = collection.find(exists(didTxidCountField)).first();
        if (null != doc) {
            return doc.getInteger(didTxidCountField);
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

    //todo too slow
    public List<PropertyDoc> findProperties(String propertyKey) {
        FindIterable<Document> docs = collection.find(eq("propertyKey", propertyKey));
        List<PropertyDoc> propertyDocs = new ArrayList<>();
        for (Document document : docs) {
            propertyDocs.add(docToProperty(document));
        }
        return propertyDocs;
    }

    public List<PropertyDoc> findPropertiesLike(String propertyKeyLike) {
        FindIterable<Document> docs = collection.find(regex("propertyKey", ".*" + propertyKeyLike + ".*"));
        List<PropertyDoc> propertyDocs = new ArrayList<>();
        for (Document document : docs) {
            propertyDocs.add(docToProperty(document));
        }
        return propertyDocs;
    }

    public List<PropertyDoc> findPropertiesLike(String did, String propertyKeyLike) {
        FindIterable<Document> docs = collection.find(and(eq("did", did), regex("propertyKey", ".*" + propertyKeyLike + ".*")));
        List<PropertyDoc> propertyDocs = new ArrayList<>();
        for (Document document : docs) {
            propertyDocs.add(docToProperty(document));
        }
        return propertyDocs;
    }

    public List<PropertyDoc> findAllPropertiesOfDid(String did) {
        FindIterable<Document> docs = collection.find(eq("did", did)).sort(descending("id"));
        List<PropertyDoc> propertyDocs = new ArrayList<>();
        for (Document document : docs) {
            propertyDocs.add(docToProperty(document));
        }
        return propertyDocs;
    }

    private PropertyDoc docToProperty(Document doc) {
        PropertyDoc propertyDoc = new PropertyDoc();
        propertyDoc.setId(doc.getLong("id"));
        propertyDoc.setDid(doc.getString("did"));
        propertyDoc.setDidStatus(doc.getInteger("didStatus"));
        propertyDoc.setPublicKey(doc.getString("publicKey"));
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
                .append("didStatus", property.getDidStatus())
                .append("publicKey", property.getPublicKey())
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
        if ((null == propertyKey) || ("".equals(propertyKey))) {
            logger.info("upsertProperty updateOne of did:" + did + " property key is null");
            return null;
        }
        Document doc = ChainPropertyToDoc(property);
        UpdateResult updateResult = collection.updateOne(and(eq("did", did), eq("propertyKey", propertyKey)),
                new Document("$set", doc),
                new UpdateOptions().upsert(true));
        if (!MongodbUtil.isModified(updateResult)) {
            logger.info("upsertProperty updateOne of did:" + did + " property:" + propertyKey);
        }
        return updateResult;
    }

    public UpdateResult delDidProperty(String did) {
        UpdateResult updateResult = collection.updateMany(eq("did", did), Updates.set("didStatus", 0));
        if (!MongodbUtil.isModified(updateResult)) {
            logger.info("delDidProperty updateOne of did:" + did);
        }
        return updateResult;

    }
}
