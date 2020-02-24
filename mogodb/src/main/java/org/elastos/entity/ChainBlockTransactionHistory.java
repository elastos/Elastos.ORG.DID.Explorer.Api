package org.elastos.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="chain_block_transaction_history")
public class ChainBlockTransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name="address")
    String address;
    @Column(name="txid")
    String txid;
    @Column(name="type")
    String type;
    @Column(name="value")
    Integer value;
    @Column(name="createTime")
    Integer createTime;
    @Column(name="height")
    Integer height;
    @Column(name="fee")
    Integer fee;
    @Column(name="inputs")
    String inputs;
    @Column(name="outputs")
    String outputs;
    @Column(name="memo")
    String memo;
    @Column(name="txType")
    String txType;
    @Column(name="publicKey")
    String publicKey;
    @Column(name="local_system_time")
    Date local_system_time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public String getInputs() {
        return inputs;
    }

    public void setInputs(String inputs) {
        this.inputs = inputs;
    }

    public String getOutputs() {
        return outputs;
    }

    public void setOutputs(String outputs) {
        this.outputs = outputs;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getTxType() {
        return txType;
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Date getLocal_system_time() {
        return local_system_time;
    }

    public void setLocal_system_time(Date local_system_time) {
        this.local_system_time = local_system_time;
    }
}
