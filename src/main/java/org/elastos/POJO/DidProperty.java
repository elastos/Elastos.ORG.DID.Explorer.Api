package org.elastos.POJO;

import org.elastos.entity.ChainDidProperty;

public class DidProperty {
    String key;
    String value;
    InputDidStatus status = InputDidStatus.normal;
    Long id;
    String did;
    String didStatus;
    String publicKey;
    String txid;
    Integer blockTime;
    Integer height;

    public void saveToDidProperty(ChainDidProperty p) {
        id = p.getId();
        did = p.getDid();
        didStatus = p.getDidStatus();
        publicKey = p.getPublicKey();
        if ("1".equals(p.getPropertyStatus())) {
            status = InputDidStatus.normal;
        } else {
            status = InputDidStatus.deprecated;
        }
        key = p.getPropertyKey();
        value = p.getPropertyValue();
        txid = p.getTxid();
        blockTime = p.getBlockTime();
        height = p.getHeight();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public InputDidStatus getStatus() {
        return status;
    }

    public void setStatus(InputDidStatus status) {
        this.status = status;
    }

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

    @Override
    public boolean equals(Object obj) {
        // must return false if the explicit parameter is null
        if (obj == null)
            return false;
        // a quick test to see if the objects are identical
        if (this == obj)
            return true;
        // if the class don't match,they can't be equal
        if (getClass() != obj.getClass())
            return false;
        // now we know obj is non-null Employee
        DidProperty other = (DidProperty) obj;
        // test whether the fields have identical values
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
