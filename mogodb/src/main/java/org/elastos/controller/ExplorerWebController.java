/**
 * Copyright (c) 2020-2021 The Elastos Developers
 * <p>
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.elastos.controller;

import com.alibaba.fastjson.JSON;
import org.elastos.service.ExplorerWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/2/didexplorer/block")
public class ExplorerWebController {

    @Autowired
    private ExplorerWebService explorerWebService;

    @RequestMapping(value = {"current/height"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getCurrentHeight() {
        return explorerWebService.getCurrentHeight();
    }

    @RequestMapping(value = {"info", "blocks_info"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getBlockInfoByHeight(@RequestParam(required = true, name = "height") Integer height) {
        return explorerWebService.getBlockInfoByHeight(height);
    }

    @RequestMapping(value = {"transactions/txids_height"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getTxidsByHeight(@RequestParam(required = true, name = "height") Integer height) {
        return explorerWebService.getAllByHeight(height);
    }

    @RequestMapping(value = {"transactions/height"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDidInfoByHeight(@RequestParam(required = true, name = "height") Integer height) {
        return explorerWebService.getDidInfoByHeight(height);
    }

    @RequestMapping(value = {"values"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getValueOfTxid(@RequestParam(required = true, name = "txid") String txid) {
        return explorerWebService.getValueOfTxid(txid);
    }

    @RequestMapping(value = {"transactions/txid"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getByTxid(@RequestParam(required = true, name = "txid") String txid) {
        return explorerWebService.getByTxid(txid);
    }

    @RequestMapping(value = {"transactions_info"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getMemoInfoByTxid(@RequestParam(required = true, name = "txid") String txid) {
        return explorerWebService.getMemoInfoByTxid(txid);
    }

    @RequestMapping(value = {"current"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findLastProperty() {
        return explorerWebService.findLastProperty();
    }

    @RequestMapping(value = {"height"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findCurrentPropertyHeight() {
        return explorerWebService.findCurrentPropertyHeight();
    }

    @RequestMapping(value = {"blocks/count"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findBlocksCount() {
        return explorerWebService.findBlocksCount();
    }

    @RequestMapping(value = {"blocks"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findBlocks(@RequestParam(required = true, name = "start") Integer start,
                             @RequestParam(required = true, name = "pagesize") Integer pagesize) {
        return explorerWebService.findBlocks(start, pagesize);
    }

    @RequestMapping(value = {"transactions_count"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getTransactionIds(@RequestParam(required = true, name = "height") Integer height) {
        return explorerWebService.getTransactionIds(height);
    }

    @RequestMapping(value = {"transactions"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findTransactions(@RequestParam(required = true, name = "start") Integer start,
                             @RequestParam(required = true, name = "pagesize") Integer pagesize) {
        return explorerWebService.findTransactions(start, pagesize);
    }

    @RequestMapping(value = {"transactions/count"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findTransactionsCount() {
        return explorerWebService.findTransactionsCount();
    }

    @RequestMapping(value = {"transactions/info"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findTransactionsInfo(@RequestParam(required = true, name = "txid") String txid) {
        return explorerWebService.findTransactionsInfo(txid);
    }

    @RequestMapping(value = {"transactions/did"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findTransactionsDid(@RequestParam(required = true, name = "did") String did) {
        return explorerWebService.findTransactionsDid(did);
    }

    @RequestMapping(value = {"properteis/did"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findPropertiesDid(@RequestParam(required = true, name = "did") String did) {
        return explorerWebService.findPropertiesDid(did);
    }

    @RequestMapping(value = {"properteis/history"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findPropertiesHistory(@RequestParam(required = true, name = "did") String did,
                                        @RequestParam(required = true, name = "key") String key,
                                        @RequestParam(required = true, name = "start") Integer start,
                                        @RequestParam(required = true, name = "pageSize") Integer pageSize) {
        return explorerWebService.findPropertiesHistory(did, key, start, pageSize);
    }

    @RequestMapping(value = {"properteis/history/count"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findPropertiesHistory(@RequestParam(required = true, name = "did") String did,
                                        @RequestParam(required = true, name = "key") String key){
        return explorerWebService.findPropertiesHistoryCount(did, key);
    }

    @RequestMapping(value = "echo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String echo(@RequestBody String body) {
        return body;
    }
}
