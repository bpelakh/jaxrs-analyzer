package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.resource.object;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Type;

import java.util.Collections;
import java.util.Set;

public class TestClass2 {

    public String method() {
        if ("".equals(""))
            return "Hi World!";
        return "Hello World!";
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();
        result.getEntityTypes().add(new Type("java.lang.String"));

        return Collections.singleton(result);
    }

}
