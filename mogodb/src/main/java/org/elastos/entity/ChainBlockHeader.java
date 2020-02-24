package org.elastos.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="chain_block_header")
public class ChainBlockHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name="hash")
    String hash;
    @Column(name="weight")
    Integer weight;
    @Column(name="height")
    Integer height;
    @Column(name="version")
    Integer version;
    @Column(name="merkleroot")
    String merkleroot;
    @Column(name="time")
    Integer time;
    @Column(name="nonce")
    Integer nonce;
    @Column(name="bits")
    Integer bits;
    @Column(name="difficulty")
    String difficulty;
    @Column(name="chainwork")
    String chainwork;
    @Column(name="previous_block_hash")
    String previous_block_hash;
    @Column(name="next_block_hash")
    String next_block_hash;
    @Column(name="miner_info")
    String miner_info;
    @Column(name="size")
    Integer size;
    @Column(name="local_system_time")
    Date localSystemTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getMerkleroot() {
        return merkleroot;
    }

    public void setMerkleroot(String merkleroot) {
        this.merkleroot = merkleroot;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getNonce() {
        return nonce;
    }

    public void setNonce(Integer nonce) {
        this.nonce = nonce;
    }

    public Integer getBits() {
        return bits;
    }

    public void setBits(Integer bits) {
        this.bits = bits;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getChainwork() {
        return chainwork;
    }

    public void setChainwork(String chainwork) {
        this.chainwork = chainwork;
    }

    public String getPrevious_block_hash() {
        return previous_block_hash;
    }

    public void setPrevious_block_hash(String previous_block_hash) {
        this.previous_block_hash = previous_block_hash;
    }

    public String getNext_block_hash() {
        return next_block_hash;
    }

    public void setNext_block_hash(String next_block_hash) {
        this.next_block_hash = next_block_hash;
    }

    public String getMiner_info() {
        return miner_info;
    }

    public void setMiner_info(String miner_info) {
        this.miner_info = miner_info;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Date getLocalSystemTime() {
        return localSystemTime;
    }

    public void setLocalSystemTime(Date localSystemTime) {
        this.localSystemTime = localSystemTime;
    }
}
