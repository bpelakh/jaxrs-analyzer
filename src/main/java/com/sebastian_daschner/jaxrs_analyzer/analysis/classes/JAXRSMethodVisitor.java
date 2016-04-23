package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.*;
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.objectweb.asm.AnnotationVisitor;

import java.util.BitSet;
import java.util.List;

/**
 * @author Sebastian Daschner
 */
class JAXRSMethodVisitor extends ProjectMethodVisitor {

    private final String signature;
    private final List<String> parameters;
    private final BitSet annotatedParameters;

    JAXRSMethodVisitor(final ClassResult classResult, final String className, final String desc, final String signature) {
        super(new MethodResult(), className);
        this.signature = signature == null ? desc : signature;
        parameters = JavaUtils.getParameters(this.signature);
        methodResult.setOriginalMethodSignature(this.signature);
        classResult.add(methodResult);
        annotatedParameters = new BitSet(parameters.size());
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        switch (desc) {
            case Types.GET:
                methodResult.setHttpMethod(HttpMethod.GET);
                break;
            case Types.POST:
                methodResult.setHttpMethod(HttpMethod.POST);
                break;
            case Types.PUT:
                methodResult.setHttpMethod(HttpMethod.PUT);
                break;
            case Types.DELETE:
                methodResult.setHttpMethod(HttpMethod.DELETE);
                break;
            case Types.HEAD:
                methodResult.setHttpMethod(HttpMethod.HEAD);
                break;
            case Types.OPTIONS:
                methodResult.setHttpMethod(HttpMethod.OPTIONS);
                break;
            case Types.PATH:
                return new PathAnnotationVisitor(methodResult);
            case Types.CONSUMES:
                return new ConsumesAnnotationVisitor(methodResult);
            case Types.PRODUCES:
                return new ProducesAnnotationVisitor(methodResult);
        }
        return null;
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String annotationDesc, boolean visible) {
        final String parameterType = parameters.get(parameter);
        annotatedParameters.set(parameter);

        switch (annotationDesc) {
            case Types.PATH_PARAM:
                return new PathParamAnnotationVisitor(methodResult, parameterType);
            case Types.QUERY_PARAM:
                return new QueryParamAnnotationVisitor(methodResult, parameterType);
            case Types.HEADER_PARAM:
                return new HeaderParamAnnotationVisitor(methodResult, parameterType);
            case Types.FORM_PARAM:
                return new FormParamAnnotationVisitor(methodResult, parameterType);
            case Types.COOKIE_PARAM:
                return new CookieParamAnnotationVisitor(methodResult, parameterType);
            case Types.MATRIX_PARAM:
                return new MatrixParamAnnotationVisitor(methodResult, parameterType);
            case Types.SUSPENDED:
                throw new UnsupportedOperationException("Handling of " + annotationDesc + " not yet implemented");
            default:
                annotatedParameters.clear(parameter);
                return null;
        }
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        // determine request body parameter
        if (annotatedParameters.cardinality() != parameters.size()) {
            final String requestBodyType = parameters.get(annotatedParameters.nextClearBit(0));
            methodResult.setRequestBodyType(requestBodyType);
        }

        // TODO determine potential super methods which are annotated with JAX-RS annotations.
        if (methodResult.getHttpMethod() == null) {
            // method is a sub resource locator
            final ClassResult classResult = new ClassResult();
            methodResult.setSubResource(classResult);
        }
    }

}
