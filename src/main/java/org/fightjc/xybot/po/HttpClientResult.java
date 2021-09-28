package org.fightjc.xybot.po;

import java.io.Serializable;

public class HttpClientResult implements Serializable {
    public int code;

    public String content;

    public HttpClientResult(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public HttpClientResult(int code) {
        this.code = code;
    }
}
