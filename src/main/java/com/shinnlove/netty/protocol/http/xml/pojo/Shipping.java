/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.http.xml.pojo;

/**
 * Supported shipment methods. The "INTERNATIONAL" shipment methods can only be
 * used for orders with shipping addresses outside the U.S., and one of these
 * methods is required in this case.
 *
 * @author shinnlove.jinsheng
 * @version $Id: Shipping.java, v 0.1 2018-06-29 下午12:05 shinnlove.jinsheng Exp $$
 */
public enum Shipping {
    STANDARD_MAIL, PRIORITY_MAIL, INTERNATIONAL_MAIL, DOMESTIC_EXPRESS, INTERNATIONAL_EXPRESS
}