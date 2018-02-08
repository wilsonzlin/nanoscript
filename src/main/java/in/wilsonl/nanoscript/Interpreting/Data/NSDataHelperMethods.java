package in.wilsonl.nanoscript.Interpreting.Data;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Interpreting.Arguments.ArgumentsValidator;
import in.wilsonl.nanoscript.Interpreting.Arguments.NSParameter;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinMethod;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinMethodParameter;
import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinMethods;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Utils.ROMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class NSDataHelperMethods {
    private final Class<? extends NSData> target;
    private final Map<String, HelperMethod> methods = new ROMap<>();

    private static class HelperMethod {
        private final Method javaMethod;
        private final ArgumentsValidator nsParams;
        private final String[] nsParamToJavaParam;
        private final String nsVarLenParam;

        private HelperMethod(Method javaMethod, ArgumentsValidator nsParams, String[] nsParamToJavaParam, String nsVarLenParam) {
            this.javaMethod = javaMethod;
            this.nsParams = nsParams;
            this.nsParamToJavaParam = nsParamToJavaParam;
            this.nsVarLenParam = nsVarLenParam;
        }

        public Method getJavaMethod() {
            return javaMethod;
        }

        public ArgumentsValidator getNsParams() {
            return nsParams;
        }

        public String[] getNsParamToJavaParam() {
            return nsParamToJavaParam;
        }

        public String getNsVarLenParam() {
            return nsVarLenParam;
        }
    }

    public NSDataHelperMethods(Class<? extends NSData> target, Class<?> helperMethodsClass) {
        this.target = target;

        BuiltinMethods ca = helperMethodsClass.getAnnotation(BuiltinMethods.class);
        if (ca == null) {
            throw new InternalStateError("The class does not have the BuiltinMethods annotation");
        }

        if (!target.equals(ca.target())) {
            throw new InternalStateError("The helper methods class does not target the provided target");
        }

        for (Method method : helperMethodsClass.getDeclaredMethods()) {
            BuiltinMethod methodAnnotation = method.getAnnotation(BuiltinMethod.class);
            if (methodAnnotation != null) {
                Annotation[][] methodParamAnnotations = method.getParameterAnnotations();
                // Excludes <self> parameter
                int nsParamsCount = method.getParameterCount() - 1;
                NSParameter[] nsParamConstraints = new NSParameter[nsParamsCount];
                String[] nsParamToJavaParam = new String[nsParamsCount];
                String nsVarLenParam = null;

                for (int i = 0; i < nsParamsCount; i++) {
                    if (methodParamAnnotations[i + 1].length == 1) {
                        Annotation paramAnno = methodParamAnnotations[i + 1][0];
                        if (!(paramAnno instanceof BuiltinMethodParameter)) {
                            throw new InternalStateError("Helper methods class method has a parameter with a non-BuiltinMethodParameter annotation");
                        }
                        BuiltinMethodParameter nsParam = (BuiltinMethodParameter) paramAnno;
                        nsParamConstraints[i] = new NSParameter(nsParam.optional(), nsParam.variableLength(), nsParam.name(), nsParam.types(), null);
                        nsParamToJavaParam[i] = nsParam.name();
                        if (nsParam.variableLength()) {
                            nsVarLenParam = nsParam.name();
                        }

                    } else if (methodParamAnnotations[i + 1].length != 0) {
                        throw new InternalStateError("Helper methods class method has more than one annotation for a parameter");
                    }
                }

                methods.put(methodAnnotation.name(), new HelperMethod(method, new ArgumentsValidator(null, nsParamConstraints), nsParamToJavaParam, nsVarLenParam));
            }
        }
    }

    public NSNativeCallable buildMethod(NSData selfValue, String name) throws NoSuchMethodException {
        HelperMethod method = methods.get(name);
        if (method == null) {
            throw new NoSuchMethodException("No such method");
        }
        return new NSNativeCallable(method.getNsParams(), arguments -> {
            Method javaMethod = method.getJavaMethod();
            String[] nsParamToJavaParam = method.getNsParamToJavaParam();
            Object[] javaMethodArgs = new Object[nsParamToJavaParam.length + 1];
            javaMethodArgs[0] = selfValue;
            for (int i = 0; i < nsParamToJavaParam.length; i++) {
                String nsParamName = nsParamToJavaParam[i];
                NSData nsParamValue = arguments.get(nsParamName);
                if (nsParamName.equals(method.getNsVarLenParam())) {
                    javaMethodArgs[i + 1] = ((NSList) nsParamValue).getRawList();
                } else {
                    javaMethodArgs[i + 1] = nsParamValue;
                }
            }
            try {
                return (NSData) javaMethod.invoke(null, javaMethodArgs);
            } catch (IllegalAccessException e) {
                throw new InternalStateError("Failed to invoke native Java helper method: " + e);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof VMError) {
                    throw (VMError) cause;
                }
                throw new InternalStateError("Failed to invoke native Java helper method: " + cause);
            }
        });
    }
}
