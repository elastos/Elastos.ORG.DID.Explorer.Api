package org.elastos.entity;

import javax.persistence.*;

@Entity
@Table(name="chain_did_app")
public class ChainDidApp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name="did")
    String did;
    @Column(name="did_status")
    String didStatus;
    @Column(name="public_key")
    String publicKey;
    @Column(name="property_key_status")
    String propertyStatus;
    @Column(name="property_key")
    String propertyKey;
    @Column(name="property_value")
    String propertyValue;
    @Column(name="info_type")
    String infoType;
    @Column(name="info_value")
    String infoValue;
    @Column(name="txid")
    String txid;
    @Column(name="block_time")
    Integer blockTime;
    @Column(name="height")
    Integer height;

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

    public String getDidStatus() {
        return didStatus;
    }

    public void setDidStatus(String didStatus) {
        this.didStatus = didStatus;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPropertyStatus() {
        return propertyStatus;
    }

    public void setPropertyStatus(String propertyStatus) {
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

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getInfoValue() {
        return infoValue;
    }

    public void setInfoValue(String infoValue) {
        this.infoValue = infoValue;
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
}
