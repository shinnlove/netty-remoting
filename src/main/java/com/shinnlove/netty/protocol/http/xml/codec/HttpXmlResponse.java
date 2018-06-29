/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.http.xml.codec;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author shinnlove.jinsheng
 * @version $Id: HttpXmlResponse.java, v 0.1 2018-06-29 下午12:03 shinnlove.jinsheng Exp $$
 */
public class HttpXmlResponse {

    private FullHttpResponse httpResponse;
    private Object           result;

    public HttpXmlResponse(FullHttpResponse httpResponse, Object result) {
        this.httpResponse = httpResponse;
        this.result = result;
    }

    /**
     * @return the httpResponse
     */
    public final FullHttpResponse getHttpResponse() {
        return httpResponse;
    }

    /**
     * @param httpResponse
     *            the httpResponse to set
     */
    public final void setHttpResponse(FullHttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    /**
     * @return the body
     */
    public final Object getResult() {
        return result;
    }

    /**
     * @param body
     *            the body to set
     */
    public final void setResult(Object result) {
        this.result = result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HttpXmlResponse [httpResponse=" + httpResponse + ", result=" + result + "]";
    }

}