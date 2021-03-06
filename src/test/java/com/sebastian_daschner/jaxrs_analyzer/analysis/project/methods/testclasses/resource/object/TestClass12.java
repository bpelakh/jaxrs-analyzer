package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.resource.object;

import com.sebastian_daschner.jaxrs_analyzer.builder.HttpResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Types;

import java.util.Collections;
import java.util.Set;

public class TestClass12<T> {

    private T object;

    public String method(final T body) {
        final T object = this.object;
        return "hello " + object + ", " + body.toString();
    }

    public static Set<HttpResponse> getResult() {
        return Collections.singleton(HttpResponseBuilder.newBuilder().andEntityTypes(Types.STRING).build());
    }

}
