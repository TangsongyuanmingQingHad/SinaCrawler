package com.had.pqst;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;

public class SINANEWS {
    private BigInteger id;
    private String title;
    private String content;
    private String url;
    private Instant createAt;
    private Instant modifyAt;

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public Instant getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Instant modifyAt) {
        this.modifyAt = modifyAt;
    }

    public SINANEWS(BigInteger id, String title, String content, String url, Instant createAt, Instant modifyAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.url = url;
        this.createAt = createAt;
        this.modifyAt = modifyAt;
    }

    public SINANEWS(String title, String content, String url) {
        this.title = title;
        this.content = content;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
