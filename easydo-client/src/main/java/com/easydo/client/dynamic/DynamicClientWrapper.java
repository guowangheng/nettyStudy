package com.easydo.client.dynamic;

import com.easydo.common.RegisterTable.AbstractWrapper;
import com.easydo.common.url.URL;

public class DynamicClientWrapper<T> extends AbstractWrapper {

    private T invoker;

    private URL url;        // 不同的消费端

    public T getInvoker() {
        return invoker;
    }

    public void setInvoker(T invoker) {
        this.invoker = invoker;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
