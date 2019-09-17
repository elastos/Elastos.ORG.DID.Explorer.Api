/**
 * Copyright (c) 2017-2018 The Elastos Developers
 * <p>
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.elastos.controller;

import com.alibaba.fastjson.JSON;
import org.elastos.POJO.InputDidStatus;
import org.elastos.entity.ReturnMsgEntity;
import org.elastos.service.ElaDidChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/1/didexplorer")
public class ElaDidChainController {

    @Autowired
    private ElaDidChainService didChainService;

    @RequestMapping(value = {"did/{did}", "did/{did}/status/{status}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getPropertiesOfDid(@PathVariable("did") String did,
                                     @PathVariable(required = false, name = "status") InputDidStatus status,
                                     @RequestParam(required = false, name = "page") Integer page,
                                     @RequestParam(required = false, name = "size") Integer size,
                                     @RequestParam(required = false, name = "detailed") Optional<Boolean> detailed) {
        ReturnMsgEntity ret;
        if (detailed.isPresent() && detailed.get()) {
            ret = didChainService.getDetailedPropertiesOfDid(did,
                    (null == status) ? InputDidStatus.normal : status, page, size);
        } else {
            ret = didChainService.getPropertiesOfDid(did,
                    (null == status) ? InputDidStatus.normal : status, page, size);
        }
        return JSON.toJSONString(ret);
    }

    @RequestMapping(value = "did/{did}/property/{property_key}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Deprecated
    public String getDIDProperty_old(@PathVariable("did") String did,
                                      @PathVariable("property_key") String propertyKey) {
        ReturnMsgEntity ret = didChainService.getDIDProperty(did, propertyKey);
        return JSON.toJSONString(ret);
    }

    @RequestMapping(value = "did/{did}/property_history/{property_key}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Deprecated
    public String getDIDPropertyHistory_old(@PathVariable("did") String did,
                                        @PathVariable("property_key") String propertyKey,
                                        @RequestParam(required = false, name = "page") Integer page,
                                        @RequestParam(required = false, name = "size") Integer size) {
        ReturnMsgEntity ret = didChainService.getDIDPropertyHistory(did, propertyKey, page, size);
        return JSON.toJSONString(ret);
    }

    @RequestMapping(value = "did/{did}/property", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDProperty(@PathVariable("did") String did,
                                             @RequestParam(name = "key") String propertyKey) {
        ReturnMsgEntity ret = didChainService.getDIDProperty(did, propertyKey);
        return JSON.toJSONString(ret);
    }

    @RequestMapping(value = "did/{did}/property_history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDPropertyHistory(@PathVariable("did") String did,
                                               @RequestParam(name = "key") String propertyKey,
                                               @RequestParam(required = false, name = "page") Integer page,
                                               @RequestParam(required = false, name = "size") Integer size) {
        ReturnMsgEntity ret = didChainService.getDIDPropertyHistory(did, propertyKey, page, size);
        return JSON.toJSONString(ret);
    }

    @RequestMapping(value = "did/{did}/property_like", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDPropertyLike(@PathVariable("did") String did,
                                             @RequestParam(name = "key") String propertyKey,
                                             @RequestParam(required = false, name = "blockheightmin") Integer blockHeightMin) {
        ReturnMsgEntity ret = didChainService.getDIDPropertyLike(did, propertyKey, blockHeightMin);
        return JSON.toJSONString(ret);
    }

    @RequestMapping(value = "did/{did}/property_history_like", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDIDPropertyHistoryLike(@PathVariable("did") String did,
                                               @RequestParam(name = "key") String propertyKey,
                                               @RequestParam(required = false, name = "blockheightmin") Integer blockHeightMin,
                                               @RequestParam(required = false, name = "page") Integer page,
                                               @RequestParam(required = false, name = "size") Integer size) {
        ReturnMsgEntity ret = didChainService.getDIDPropertyHistoryLike(did, propertyKey, blockHeightMin, size, page);
        return JSON.toJSONString(ret);
    }

    @RequestMapping(value = "property", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getPropertiesCrossDID(@RequestParam(name = "key") String propertyKey,
                                        @RequestParam(required = false, name = "page") Integer page,
                                        @RequestParam(required = false, name = "size") Integer size) {
        ReturnMsgEntity ret = didChainService.getPropertiesCrossDID(propertyKey, page, size);
        return JSON.toJSONString(ret);
    }

    @RequestMapping(value = "did_app", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getPropertiesCrossDID(@RequestParam(name = "appid") String appId) {
        ReturnMsgEntity ret = didChainService.getPropertiesOfAppId(appId);
        return JSON.toJSONString(ret);
    }

    @RequestMapping(value = "echo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String echo(@RequestBody String body) {
        return body;
    }
}
