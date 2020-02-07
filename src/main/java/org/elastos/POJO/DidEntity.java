/**
 * Copyright (c) 2017-2018 The Elastos Developers
 * <p>
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */

package org.elastos.POJO;

import java.util.List;

public class DidEntity {

    public static final String DID_TAG = "DID Property";
    public static final String NORMAL = "Normal";
    public static final String DEPRECATED = "Deprecated";

    public static class DidProperty {
        String Key;
        String Value;
        String Status = NORMAL;

        public String getKey() {
            return Key;
        }

        public void setKey(String key) {
            this.Key = key;
        }

        public String getValue() {
            return Value;
        }

        public void setValue(String value) {
            this.Value = value;
        }

        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            this.Status = status;
        }
    }

    String Tag = DID_TAG;
    String Did;
    String Status = NORMAL;
    String Ver = "1.0";
    List<DidProperty> Properties;

    public String getDid() {
        return Did;
    }

    public void setDid(String did) {
        this.Did = did;
    }

    public String getTag() {
        return Tag;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public String getVer() {
        return Ver;
    }

    public void setVer(String ver) {
        this.Ver = ver;
    }

    public List<DidProperty> getProperties() {
        return Properties;
    }

    public void setProperties(List<DidProperty> properties) {
        this.Properties = properties;
    }

}
