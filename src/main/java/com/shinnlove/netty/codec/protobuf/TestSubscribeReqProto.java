/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.codec.protobuf;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * `Google ProtoBuf`序列化就是支持对象.toByteArray -> 字节数组，反序列化就是支持对象.parseFrom(字节数组)。
 *
 * @author shinnlove.jinsheng
 * @version $Id: TestSubscribeReqProto.java, v 0.1 2018-06-29 下午1:52 shinnlove.jinsheng Exp $$
 */
public class TestSubscribeReqProto {

    /**
     * `Google ProtoBuf`序列化就是支持对象.toByteArray -> 字节数组
     *
     * @param req
     * @return
     */
    private static byte[] encode(SubscribeReqProto.SubscribeReq req) {
        return req.toByteArray();
    }

    /**
     * `Google ProtoBuf`反序列化就是支持对象.parseFrom(字节数组)。
     *
     * @param body
     * @return
     * @throws InvalidProtocolBufferException
     */
    private static SubscribeReqProto.SubscribeReq decode(byte[] body)
                                                                     throws InvalidProtocolBufferException {
        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }

    /**
     * 生成支持的对象。
     *
     * @return
     */
    private static SubscribeReqProto.SubscribeReq createSubscribeReq() {
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq
            .newBuilder();
        builder.setSubReqID(1);
        builder.setUserName("Lilinfeng");
        builder.setProductName("Netty Book");
        List<String> address = new ArrayList<>();
        address.add("NanJing YuHuaTai");
        address.add("BeiJing LiuLiChang");
        address.add("ShenZhen HongShuLin");
        builder.addAllAddress(address);
        return builder.build();
    }

    /**
     * 测试main函数。
     *
     * @param args
     * @throws InvalidProtocolBufferException
     */
    public static void main(String[] args) throws InvalidProtocolBufferException {
        SubscribeReqProto.SubscribeReq req = createSubscribeReq();
        System.out.println("Before encode : " + req.toString());
        // 序列化再反序列化
        SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
        System.out.println("After decode : " + req2.toString());
        // 前后码流一致
        System.out.println("Assert equal (first) before compare (second) after : --> "
                           + req.equals(req2));
    }

}