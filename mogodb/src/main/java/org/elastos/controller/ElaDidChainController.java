/**
 * Copyright (c) 2017-2018 The Elastos Developers
 * <p>
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.elastos.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elastos.service.ElaDidChainService;
import org.elastos.service.ElaDidMongoDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController

@RequestMapping("/api/2/didexplorer")
public class ElaDidChainController {

    @Autowired
    private ElaDidChainService didChainService;

    @Autowired
    private ElaDidMongoDbService elaDidMongoDbService;

    @RequestMapping(value = {"did/{did}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getPropertiesOfDid(@PathVariable("did") String did,
                                 @RequestParam(required = false, name = "page") Integer page,
                                 @RequestParam(required = false, name = "size") Integer size) {
        return didChainService.getDidProperties(did, page, size);
    }

    @RequestMapping(value = "did/{did}/property", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDProperty(@PathVariable("did") String did,
                                 @RequestParam(name = "key") String propertyKey) {
        return didChainService.getDidProperty(did, propertyKey);
    }

    @RequestMapping(value = "did/{did}/property_history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDPropertyHistory(@PathVariable("did") String did,
                                        @RequestParam(name = "key") String propertyKey,
                                        @RequestParam(required = false, name = "page") Integer page,
                                        @RequestParam(required = false, name = "size") Integer size) {
        return didChainService.getDidPropertyHistory(did, propertyKey, page, size);
    }

    @RequestMapping(value = "did/{did}/property_history/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDPropertyHistoryCount(@PathVariable("did") String did,
                                        @RequestParam(name = "key") String propertyKey) {
        return didChainService.getDIDPropertyHistoryCount(did, propertyKey);
    }

    @RequestMapping(value = "did/{did}/property_like", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDPropertyLike(@PathVariable("did") String did,
                                     @RequestParam(name = "key") String propertyKey,
                                        @RequestParam(required = false, name = "page") Integer page,
                                        @RequestParam(required = false, name = "size") Integer size) {
        return didChainService.getDIDPropertyLike(did, propertyKey, page, size);
    }

    @RequestMapping(value = "did/{did}/property_history_like", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDPropertyHistoryLike(@PathVariable("did") String did,
                                            @RequestParam(name = "key") String propertyKey,
                                            @RequestParam(required = false, name = "page") Integer page,
                                            @RequestParam(required = false, name = "size") Integer size) {
        return didChainService.getDIDPropertyHistoryLike(did, propertyKey, size, page);
    }

    @RequestMapping(value = "property", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getPropertiesCrossDID(@RequestParam(name = "key") String propertyKey,
                                        @RequestParam(required = false, name = "page") Integer page,
                                        @RequestParam(required = false, name = "size") Integer size) {
        return didChainService.getPropertiesCrossDID(propertyKey, page, size);
    }

    @RequestMapping(value = "property/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getPropertiesCrossDidCount(@RequestParam(name = "key") String propertyKey) {
        return didChainService.getPropertiesCrossDidCount(propertyKey);
    }

    @RequestMapping(value = "property_like", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getPropertiesLikeCrossDID(@RequestParam(name = "key") String propertyKey,
                                        @RequestParam(required = false, name = "page") Integer page,
                                        @RequestParam(required = false, name = "size") Integer size) {
        return didChainService.getPropertiesLikeCrossDID(propertyKey, page, size);
    }

    @RequestMapping(value = "did_app", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getPropertiesCrossDID(@RequestParam(name = "appid") String appId) {
        return didChainService.getPropertiesOfAppId(appId);
    }

    @RequestMapping(value = "serverInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getServerInfo() {
        Date time = new Date();
        Map<String, Object> data = new HashMap<>();
        data.put("code", 1);
        data.put("s_time", time.getTime());
        return JSON.toJSONString(data);
    }

    @RequestMapping(value = "dids/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDidSum() {
        return elaDidMongoDbService.getDidSum();
    }

    @RequestMapping(value = "dids", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDidList(@RequestParam(name = "start") Integer start, @RequestParam(name = "pageSize") Integer pageSize ) {
        return elaDidMongoDbService.getDidList(start, pageSize);
    }

    @RequestMapping(value = "cache", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String setPropertyCache(@RequestAttribute String reqBody) {
        JSONObject ob = JSON.parseObject(reqBody);
        String raw = ob.getString("raw");
        String txid = ob.getString("txid");

        return didChainService.setPropertyCache(raw, txid);
    }

    @RequestMapping(value = "cache/did/{did}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getPropertiesOfDidFromCache(@PathVariable("did") String did) {
        return didChainService.getPropertiesOfDidFromCache(did);
    }

    @RequestMapping(value = "cache/did/{did}/property", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Deprecated
    public String getDIDPropertyFromCache(@PathVariable("did") String did,
                                          @RequestParam(name = "key") String propertyKey) {
        return didChainService.getDIDPropertyFromCache(did, propertyKey);
    }

    @RequestMapping(value = "cache/did/{did}/property_history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDPropertyHistoryFromCache(@PathVariable("did") String did,
                                                 @RequestParam(name = "key") String propertyKey) {
        return didChainService.getPropertyHistoryFromCache(did, propertyKey);
    }

    @RequestMapping(value = "echo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String echo(@RequestBody String body) {
        return body;
    }
}
