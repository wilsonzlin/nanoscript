package in.wilsonl.nanoscript.Interpreting;

import in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Syntax.Operator;
import in.wilsonl.nanoscript.Utils.ROSet;

import java.util.List;
import java.util.Set;

import static java.lang.String.format;

public class ArgumentsValidator {
    public static final ArgumentsValidator ZERO = new ArgumentsValidator(0);
    public static final ArgumentsValidator ONE = new ArgumentsValidator(1);
    public static final ArgumentsValidator TWO = new ArgumentsValidator(2);
    public static final ArgumentsValidator THREE = new ArgumentsValidator(3);
    private final Parameter[] parameters; // Put null in array to denote that arguments after are optional

    public ArgumentsValidator(Parameter[] parameters) {
        this.parameters = parameters;
    }

    public ArgumentsValidator(Parameter parameter) {
        this(new Parameter[]{parameter});
    }

    public ArgumentsValidator(int count) {
        Parameter[] arr = new Parameter[count];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new Parameter();
        }
        // WARNING: Doesn't call main constructor
        this.parameters = arr;
    }

    private static VMError buildMismatchArgumentsError(int expected, int actual) {
        String message = format("Function has %d parameter%s but %d argument%s provided", expected, expected != 1 ? "s" : "", actual, actual != 1 ? "s" : "");
        return VMError.from(BuiltinClass.ArgumentsError, message);
    }

    public static void assertArgumentsMatch(int expected, List<?> actual) {
        int argsCount = actual.size();
        if (argsCount != expected) {
            throw buildMismatchArgumentsError(expected, argsCount);
        }
    }

    public void validate(List<NSData<?>> arguments) {
        int current = -1;

        boolean startedOptional = false;
        for (Parameter param : parameters) {
            current++;
            if (param == null) {
                current--;
                startedOptional = true;
                continue;
            }
            if (arguments.size() <= current) {
                if (startedOptional) {
                    break;
                } else {
                    throw VMError.from(BuiltinClass.ArgumentsError, format("Argument %d is missing", current));
                }
            }
            NSData<?> arg = arguments.get(current);
            if (!param.hasType(arg.getType())) {
                throw VMError.from(BuiltinClass.TypeError, format("Argument %d is of type %s but should be of type %s", current, arg.getType(), param.getTypesStringRep()));
            }
            if (param.getRelations() != null) {
                for (Parameter.Relation r : param.getRelations()) {
                    NSData<?> result = arg.nsApplyBinaryOperator(r.getOperator(), r.getValue());
                    if (!result.nsToBoolean().getRawValue()) {
                        throw VMError.from(BuiltinClass.ValueError, format("Argument %d is invalid", current));
                    }
                }
            }
        }
        if (current < arguments.size() - 1) {
            throw VMError.from(BuiltinClass.ArgumentsError, "Too many arguments");
        }
    }

    public static class Parameter {
        private final Set<NSData.Type> types = new ROSet<>(); // Can be empty
        private final String typesStringRep;
        private final Relation[] relations; // Can be null

        public Parameter(NSData.Type[] types, Relation[] relations) {
            if (types != null) {
                StringBuilder typesStringRep = new StringBuilder();
                for (int i = 0; i <= types.length - 1; i++) {
                    NSData.Type type = types[i];
                    this.types.add(type);
                    typesStringRep.append(type);
                    if (types.length > 2) {
                        typesStringRep.append(", ");
                    } else {
                        typesStringRep.append(' ');
                    }
                }
                NSData.Type lastType = types[types.length - 1];
                this.types.add(lastType);
                if (types.length > 1) {
                    typesStringRep.append("or ");
                }
                typesStringRep.append(lastType);
                this.typesStringRep = typesStringRep.toString();
            } else {
                typesStringRep = null;
            }
            this.relations = relations;
        }

        public Parameter(NSData.Type[] types) {
            this(types, null);
        }

        public Parameter(NSData.Type type) {
            this(new NSData.Type[]{type}, null);
        }

        public Parameter() {
            this(null, null);
        }

        public String getTypesStringRep() {
            return typesStringRep;
        }

        public boolean hasType(NSData.Type type) {
            return types.isEmpty() || types.contains(type);
        }

        public Relation[] getRelations() {
            return relations;
        }

        public static class Relation {
            private final Operator operator;
            private final NSData<?> value;

            private Relation(Operator operator, NSData<?> value) {
                this.operator = operator;
                this.value = value;
            }

            public Operator getOperator() {
                return operator;
            }

            public NSData<?> getValue() {
                return value;
            }
        }
    }
}
