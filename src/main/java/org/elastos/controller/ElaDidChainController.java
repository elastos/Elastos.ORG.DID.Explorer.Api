/**
 * Copyright (c) 2017-2018 The Elastos Developers
 * <p>
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.elastos.controller;

import com.alibaba.fastjson.JSON;
import org.elastos.POJO.InputDidStatus;
import org.elastos.entity.ChainType;
import org.elastos.entity.RawTxEntity;
import org.elastos.entity.ReturnMsgEntity;
import org.elastos.service.ElaDidChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * clark
 * <p>
 * 9/20/18
 */
@RestController
@RequestMapping("/api/1/didexplorer")
public class ElaDidChainController {

    @Autowired
    private ElaDidChainService didChainService;

    @RequestMapping(value = "asset/utxos/{chain}/{address}", method = RequestMethod.GET)
    @ResponseBody
    public String getUtxosByAddr(@PathVariable("address") String address, @PathVariable("chain") ChainType type) {
        return didChainService.getUtxosByAddr(address, type);
    }

    @RequestMapping(value = "transaction/{chain}/{txid}", method = RequestMethod.GET)
    @ResponseBody
    public String getTransaction(@PathVariable("txid") String txid, @PathVariable("chain") ChainType type) {
        return didChainService.getTransaction(txid, type);
    }

    @RequestMapping(value = "transaction/{chain}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String sendRawTransaction(@RequestAttribute String reqBody, @PathVariable("chain") ChainType type) {
        RawTxEntity tx = JSON.parseObject(reqBody, RawTxEntity.class);
        return didChainService.sendTransaction(tx, type);

    }

    @RequestMapping(value = {"did/{did}", "did/{did}/status/{status}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDProperties(@PathVariable("did") String did,
                                   @PathVariable(required = false, name = "status") InputDidStatus status,
                                   @RequestParam(required = false, name = "page") Integer page,
                                   @RequestParam(required = false, name = "size") Integer size,
                                   @RequestParam(required = false, name = "detailed") Optional<Boolean> detailed) {
        ReturnMsgEntity ret;
        if (detailed.isPresent() && detailed.get()) {
            ret = didChainService.getDIDDetailedProperties(did,
                    (null == status) ? InputDidStatus.normal : status, page, size);
        } else {
            ret = didChainService.getDIDProperties(did,
                    (null == status) ? InputDidStatus.normal : status, page, size);
        }
        return JSON.toJSONString(ret);
    }

    @RequestMapping(value = "did/{did}/property/{property_key}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDPropertyValue(@PathVariable("did") String did,
                                      @PathVariable("property_key") String propertyKey) {
        ReturnMsgEntity ret = didChainService.getDIDPropertyValue(did, propertyKey);
        return JSON.toJSONString(ret);
    }

    @RequestMapping(value = "did/{did}/property_history/{property_key}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDPropertyHistory(@PathVariable("did") String did,
                                        @PathVariable("property_key") String propertyKey,
                                        @RequestParam(required = false, name = "page") Integer page,
                                        @RequestParam(required = false, name = "size") Integer size) {
        ReturnMsgEntity ret = didChainService.getDIDPropertyHistory(did, propertyKey, page, size);
        return JSON.toJSONString(ret);
    }

    @RequestMapping(value = "did/{did}/property", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDPropertyValueByParam(@PathVariable("did") String did,
                                      @RequestParam(name ="key") String propertyKey) {
        ReturnMsgEntity ret = didChainService.getDIDPropertyValue(did, propertyKey);
        return JSON.toJSONString(ret);
    }

    @RequestMapping(value = "did/{did}/property_history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDPropertyHistoryByParam(@PathVariable("did") String did,
                                        @RequestParam(name = "key") String propertyKey,
                                        @RequestParam(required = false, name = "page") Integer page,
                                        @RequestParam(required = false, name = "size") Integer size) {
        ReturnMsgEntity ret = didChainService.getDIDPropertyHistory(did, propertyKey, page, size);
        return JSON.toJSONString(ret);
    }

    @RequestMapping(value = "echo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String echo(@RequestBody String body) {
        return body;
    }
}
