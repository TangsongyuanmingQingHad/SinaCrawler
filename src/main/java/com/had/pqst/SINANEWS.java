package com.had.pqst;

import java.math.BigInteger;
import java.util.Date;

public class SINANEWS {
    private BigInteger id;
    private String title;
    private String content;
    private Date createAt;
    private Date modifyAt;

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

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    @Override
    public String toString() {
        return "SINANEWS{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createAt=" + createAt +
                ", modifyAt=" + modifyAt +
                '}';
    }
}
