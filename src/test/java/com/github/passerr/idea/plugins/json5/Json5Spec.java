package com.github.passerr.idea.plugins.json5;

import com.github.passerr.idea.plugins.spring.web.json5.Json5Writer;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

/**
 * {@link Json5Writer}
 * @author xiehai
 * @date 2021/07/20 11:37
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class Json5Spec {
    @Test
    public void json5() throws IOException {
        StringWriter stringWriter = new StringWriter();
        Json5Writer writer = Json5Writer.json5(stringWriter);
        writer.setIndent("  ");
        writer.comment("这是注释")
            .beginObject()
            .name("key")
            .value(1L)
            .endObject();
        System.out.println(stringWriter);
    }
}
