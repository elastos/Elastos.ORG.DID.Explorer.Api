package org.elastos.POJO;

public class PropertyOfDid {
    String did;
    String key;
    String value;
    InputDidStatus status = InputDidStatus.normal;

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
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
        PropertyOfDid other = (PropertyOfDid) obj;
        // test whether the fields have identical values
        return did.equals(other.did);
    }

    @Override
    public int hashCode() {
        return did.hashCode();
    }
}
