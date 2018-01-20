package com.theorangehub.dmlbrowser;

import com.theorangehub.dml.DML;
import com.theorangehub.dml.DMLBuilder;

public abstract class DMLBrowser {

    protected abstract String navigate(String pageId);

    protected abstract DMLBuilder notFound(String pageId);

    protected abstract void sendMessage(DMLBuilder builder);

    protected void handle(String page) {
        String content = navigate(page);
        DMLBuilder builder = content == null ? notFound(page) : parse(content);
        sendMessage(builder);
    }

    protected DMLBuilder newBuilder() {
        return new DMLBuilder();
    }

    protected DMLBuilder parse(String content) {
        return DML.parse(newBuilder(), content);
    }
}
