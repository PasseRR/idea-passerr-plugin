package com.github.passerr.idea.plugins.spring.web.json5;

import java.io.StringWriter;

/**
 * com.google.gson.stream.JsonScope
 * @author xiehai
 * @date 2021/07/08 14:43
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
interface JsonScope {
    /**
     * An array with no elements requires no separators or newlines before
     * it is closed.
     */
    int EMPTY_ARRAY = 1;

    /**
     * A array with at least one value requires a comma and newline before
     * the next element.
     */
    int NONEMPTY_ARRAY = 2;

    /**
     * An object with no name/value pairs requires no separators or newlines
     * before it is closed.
     */
    int EMPTY_OBJECT = 3;

    /**
     * An object whose most recent element is a key. The next element must
     * be a value.
     */
    int DANGLING_NAME = 4;

    /**
     * An object with at least one name/value pair requires a comma and
     * newline before the next element.
     */
    int NONEMPTY_OBJECT = 5;

    /**
     * No object or array has been started.
     */
    int EMPTY_DOCUMENT = 6;

    /**
     * A document with at an array or object.
     */
    int NONEMPTY_DOCUMENT = 7;

    public static void main(String[] args) throws Exception {
        StringWriter stringWriter = new StringWriter();
        Json5Writer writer = Json5Writer.json5(stringWriter);
        writer.setIndent("  ");
        writer.comment("这是注释")
            .beginObject()
            .name("key")
            .value(1L)
            .endObject();
        System.out.println(stringWriter);

//        Json5Writer json5Writer = Json51);
    }
}