package org.elastos.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name="chain_did_property")
public class ChainDidProperty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name="did")
    String did;
    @Column(name="did_status")
    Integer didStatus;
    @Column(name="public_key")
    String publicKey;
    @Column(name="property_key_status")
    Integer propertyStatus;
    @Column(name="property_key")
    String propertyKey;
    @Column(name="property_value")
    String propertyValue;
    @Column(name="txid")
    String txid;
    @Column(name="block_time")
    Integer blockTime;
    @Column(name="height")
    Integer height;
    @Column(name="local_system_time")
    Date localSystemTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public Integer getDidStatus() {
        return didStatus;
    }

    public void setDidStatus(Integer didStatus) {
        this.didStatus = didStatus;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Integer getPropertyStatus() {
        return propertyStatus;
    }

    public void setPropertyStatus(Integer propertyStatus) {
        this.propertyStatus = propertyStatus;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public Integer getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(Integer blockTime) {
        this.blockTime = blockTime;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Date getLocalSystemTime() {
        return localSystemTime;
    }

    public void setLocalSystemTime(Date localSystemTime) {
        this.localSystemTime = localSystemTime;
    }
}
