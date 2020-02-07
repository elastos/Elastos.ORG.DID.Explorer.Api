/**
 * Copyright (c) 2018-2019 The Elastos Developers
 * <p>
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */

package org.elastos.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("node")
public class NodeConfiguration {
    private String didPrefix;

    public String getDidPrefix() {
        return didPrefix;
    }

    public void setDidPrefix(String didPrefix) {
        this.didPrefix = didPrefix;
    }
}
