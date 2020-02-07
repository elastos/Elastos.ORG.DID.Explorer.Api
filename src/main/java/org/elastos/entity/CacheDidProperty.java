package org.elastos.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="cache_did_property",
        indexes = {@Index(name = "did_index", columnList = "did"),
                   @Index(name = "property_key_index", columnList = "property_key"),
                   @Index(name = "txid_index", columnList = "txid")})
public class CacheDidProperty {
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
    String status;
    @Column(name="property_key")
    String key;
    @Column(name="property_value")
    String value;
    @Column(name="txid")
    String txid;
    @CreatedDate
    @Column(name = "create_time")
    private Date createTime;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }
}
