package org.elastos.util;

import com.alibaba.fastjson.JSON;
import org.elastos.constant.ServerResponseCode;

public class ServerResponse {
    private int state;
    private String msg;
    private Object data;

    public int getState() {
        return state;
    }

    public ServerResponse setState(int state) {
        this.state = state;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ServerResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ServerResponse setData(Object data) {
        this.data = data;
        return this;
    }

    public String toJsonString(){
        return JSON.toJSONString(this);
    }

    public static String retOk(Object data) {
        return new ServerResponse().setState(ServerResponseCode.SUCCESS).setData(data).toJsonString();
    }

    public static  String retErr(int state , String msg) {
        return new ServerResponse().setState(state).setMsg(msg).toJsonString();
    }
}
