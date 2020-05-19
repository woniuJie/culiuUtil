/**
 * Project Name:culiulib File Name:Assert.java Package Name:com.culiu.core.utils.common Date:2014-10-15上午12:50:10 Copyright (c)
 * 2014, adison All Rights Reserved.
 */
package com.culiu.core.utils.common;


/**
 * @describe 断言工具类.
 * @author adison
 * @date: 2014-10-15 上午12:50:10 <br/>
 */
public class Assert {

    private static final String MESSAGE_FORMAT = "\"%s\" 参数不能为空.";

    /**
     * notNull:判断对象是否为空. <br/>
     * <p>
     * 空则抛出NullPointerException异常
     * </p>
     * @author adison
     * @param object
     * @param argName
     */
    public static void notNull(Object object, String argName) {
        if(object == null) {

            throw new NullPointerException(String.format(MESSAGE_FORMAT, argName));
        }
    }

    /**
     * notNull:判断对象是否为空. <br/>
     * <p/>
     * 空则抛出NullPointerException异常
     *
     * @param object
     * @param argName
     * @param description
     */
    public static void notNull(Object object, String argName, String description) {
        if (object == null) {
            throw new NullPointerException(
                    String.format(MESSAGE_FORMAT, argName) + ", " + description);
        }
    }

}
