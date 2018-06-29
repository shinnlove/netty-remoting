/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.codec.pojo;

import java.io.Serializable;

/**
 * @author shinnlove.jinsheng
 * @version $Id: SubscribeResp.java, v 0.1 2018-06-29 下午1:48 shinnlove.jinsheng Exp $$
 */
public class SubscribeResp implements Serializable {

    /** 默认序列ID */
    private static final long serialVersionUID = 8529904618080884113L;

    private int               subReqID;

    private int               respCode;

    private String            desc;

    /**
     * @return the subReqID
     */
    public final int getSubReqID() {
        return subReqID;
    }

    /**
     * @param subReqID
     *            the subReqID to set
     */
    public final void setSubReqID(int subReqID) {
        this.subReqID = subReqID;
    }

    /**
     * @return the respCode
     */
    public final int getRespCode() {
        return respCode;
    }

    /**
     * @param respCode
     *            the respCode to set
     */
    public final void setRespCode(int respCode) {
        this.respCode = respCode;
    }

    /**
     * @return the desc
     */
    public final String getDesc() {
        return desc;
    }

    /**
     * @param desc
     *            the desc to set
     */
    public final void setDesc(String desc) {
        this.desc = desc;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SubscribeResp [subReqID=" + subReqID + ", respCode=" + respCode + ", desc=" + desc
               + "]";
    }

}
