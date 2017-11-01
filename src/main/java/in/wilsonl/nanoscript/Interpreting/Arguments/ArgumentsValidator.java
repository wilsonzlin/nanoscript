package in.wilsonl.nanoscript.Interpreting.Arguments;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSList;
import in.wilsonl.nanoscript.Interpreting.VMError;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.ROMap;

import java.lang.reflect.MalformedParametersException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass.ArgumentsError;
import static in.wilsonl.nanoscript.Interpreting.Builtin.BuiltinClass.TypeError;
import static java.lang.String.format;

public class ArgumentsValidator {
    public static final ArgumentsValidator ANY = new ArgumentsValidator(new NSParameter(true, true, "values"));
    public static final ArgumentsValidator ZERO = new ArgumentsValidator();
    private final NSParameter[] parameters;
    private boolean hasOptional = false;
    private boolean hasVarLen = false;
    // <posOfVarLen> should be parameters.length if not hasVarLen
    private int posOfVarLen = -1;
    // <posOfFirstOptional> and <posOfLastOptional> should be parameters.length
    // if not hasOptional
    private int posOfFirstOptional = -1;
    private int posOfLastOptional = -1;
    // This must be reset on every call to .match
    private Map<String, NSData> matchedValues = new HashMap<>();

    public ArgumentsValidator(NSParameter[] parameters) {
        this.parameters = parameters;
        int paramsCount = parameters.length;
        boolean hasOptionalVarLen = false;
        for (int i = 0; i < paramsCount; i++) {
            NSParameter parameter = parameters[i];
            if (parameter == null) {
                throw new InternalStateError("Parameter is null");
            }
            if (parameter.isOptional()) {
                if (hasVarLen && !hasOptionalVarLen) {
                    throw new MalformedParametersException("A non-optional variable length parameter exists with optional parameters");
                }
                if (posOfFirstOptional == -1) {
                    posOfFirstOptional = i;
                    hasOptional = true;
                }
            } else {
                if (hasOptional && posOfLastOptional == -1) {
                    posOfLastOptional = i - 1;
                }
            }
            if (parameter.isVariableLength()) {
                if (hasVarLen) {
                    throw new MalformedParametersException("More than one variable length parameter");
                } else {
                    if (hasOptional && !parameter.isOptional()) {
                        throw new MalformedParametersException("A non-optional variable length parameter exists with optional parameters");
                    }
                    hasVarLen = true;
                    hasOptionalVarLen = parameter.isOptional();
                }
                posOfVarLen = i;
            }
        }
        if (hasOptional) {
            if (posOfLastOptional == -1) {
                posOfLastOptional = paramsCount - 1;
            }
        } else {
            posOfFirstOptional = posOfLastOptional = paramsCount;
        }
        if (!hasVarLen) {
            posOfVarLen = paramsCount;
        }
    }

    public ArgumentsValidator(NSParameter parameter) {
        this(new NSParameter[]{parameter});
    }

    public ArgumentsValidator() {
        this(new NSParameter[0]);
    }

    private void validateArgument(int argNo, NSParameter p, NSArgument a) {
        NSData.Type argType = a.getValue().getType();
        if (!p.canAcceptType(argType)) {
            throw VMError.from(TypeError, format("Argument %d is of type %s but should be of type %s", argNo, argType, p.getFriendlyTypesName()));
        }
    }

    private int matchArguments(int fakeFromInc, int fakeToInc, int realArgBoundary, List<NSArgument> args, boolean required) {
        int argsCount = args.size();

        int direction = fakeFromInc < 0 ? -1 : 1;
        int realParamFrom = direction == -1 ? parameters.length + fakeFromInc : fakeFromInc;
        int realParamTo = direction == -1 ? parameters.length + fakeToInc : fakeToInc;
        int realArgsFrom = direction == -1 ? argsCount + fakeFromInc : fakeFromInc;
        int realArgsTo = direction == -1 ? argsCount + fakeToInc : fakeToInc;

        if ((fakeFromInc - fakeToInc) * direction > 0) {
            return realArgsFrom - direction;
        }

        int fakeIdx = fakeFromInc;
        int realParamIdx = realParamFrom;
        int realArgIdx = realArgsFrom;
        while ((realArgIdx - realArgsTo) * direction <= 0) {
            if (realArgIdx >= argsCount || realArgIdx < 0 || (realArgIdx - realArgBoundary) * direction >= 0) {
                if (required) {
                    throw VMError.from(ArgumentsError, format("Argument %d is missing", fakeIdx));
                } else {
                    break;
                }
            }
            NSParameter p = parameters[realParamIdx];
            NSArgument a = args.get(realArgIdx);

            validateArgument(fakeIdx, p, a);
            matchedValues.put(p.getName(), a.getValue());

            fakeIdx += direction;
            realParamIdx += direction;
            realArgIdx += direction;
        }

        // The last parameter and index was not consumed, so back up
        return realArgIdx - direction;
    }

    public Map<String, NSData> match(List<NSArgument> args) {
        matchedValues.clear();

        /*
         *   If hasOptional, this will collect args before the first optional,
         *     regardless of hasVarLen
         *   If hasVarLen but not hasOptional, this will collect args before the
         *     variable length
         *   If neither, this will collect all args
         */
        int upperBoundFromLeft = (hasOptional ? posOfFirstOptional : posOfVarLen) - 1;
        int lastArgIdxFromLeft = matchArguments(0, upperBoundFromLeft, upperBoundFromLeft + 1, args, true);


        /*
         *   If hasOptional, this will collect args from the right after the last optional,
         *     regardless of hasVarLen
         *   If hasVarLen but not hasOptional, this will collect args from the right after
         *    the variable length
         *   If neither, this will not run
         */
        int lowerBoundFromRight = (hasOptional ? posOfLastOptional : posOfVarLen) - parameters.length + 1;
        int lastArgIdxFromRight = matchArguments(-1, lowerBoundFromRight, lastArgIdxFromLeft, args, true);

        if (hasOptional) {
            int optionalUpperBoundFromLeft = hasVarLen ? posOfVarLen - 1 : posOfLastOptional;
            lastArgIdxFromLeft = matchArguments(posOfFirstOptional, optionalUpperBoundFromLeft, lastArgIdxFromRight, args, false);

            if (hasVarLen) {
                // Also means there might be args to the right of the varlen
                lastArgIdxFromRight = matchArguments(posOfLastOptional - parameters.length, posOfVarLen - parameters.length + 1, lastArgIdxFromLeft, args, false);
            }
        }

        if (hasVarLen) {
            NSParameter p = parameters[posOfVarLen];
            List<NSData> collected = new ROList<>();
            for (int j = lastArgIdxFromLeft + 1; j < lastArgIdxFromRight; j++) {
                NSArgument a = args.get(j);
                validateArgument(j, p, a);
                collected.add(a.getValue());
            }
            if (collected.isEmpty() && !hasOptional) {
                throw VMError.from(ArgumentsError, "At least one argument is required for the variable length parameter");
            }
            // Put value even if empty
            matchedValues.put(p.getName(), NSList.from(collected));
        } else {
            for (int j = lastArgIdxFromLeft + 1; j < lastArgIdxFromRight; j++) {
                if (!args.get(j).isOptional()) {
                    throw VMError.from(ArgumentsError, format("Argument %d is extraneous", j));
                }
            }
        }

        return new ROMap<>(matchedValues);
    }

    /*
    public void validate(List<NSArgument> arguments) {
        /*
         *   If hasOptional, this will collect args before the first optional,
         *     regardless of hasVarLen
         *   If hasVarLen but not hasOptional, this will collect args before the
         *     variable length
         *   If neither, this will collect all args
         *
        int upperBoundFromLeft = (hasOptional ? posOfFirstOptional : posOfVarLen) - 1;
        int lastArgIdxFromLeft = validateArguments(0, upperBoundFromLeft, upperBoundFromLeft, arguments, true);
        for (int i = 0; i < upperBoundFromLeft; i++) {
            ParameterConstraint p = parameters[i];
            NSArgument a;
            try {
                a = arguments.get(i);
            } catch (IndexOutOfBoundsException ioobe) {
                throw VMError.from(ArgumentsError, format("Argument %d is missing", i));
            }
            validateArgument(i, p, a);
            lastArgIdxFromLeft++;
        }


        /*
         *   If hasOptional, this will collect args from the right after the last optional,
         *     regardless of hasVarLen
         *   If hasVarLen but not hasOptional, this will collect args from the right after
         *    the variable length
         *   If neither, this will not run
         *
        int lowerBoundFromRight = hasOptional ? posOfLastOptional : posOfVarLen;
        int lastArgIdxFromRight = arguments.size();
        int reqArgFromRight = 0;
        while (reqArgFromRight > lowerBoundFromRight - parameters.length + 1) {
            reqArgFromRight--;
            lastArgIdxFromRight--;
            ParameterConstraint p = parameters[parameters.length + reqArgFromRight];
            // This is correct
            // Whether it invalidates varlen params is checked later on
            if (lastArgIdxFromRight <= lastArgIdxFromLeft) {
                throw VMError.from(ArgumentsError, format("Argument %d is missing", reqArgFromRight));
            }
            NSArgument a = arguments.get(lastArgIdxFromRight);
            validateArgument(reqArgFromRight, p, a);
        }

        int optArgFromRight = 0;

        if (hasOptional) {
            int optionalUpperBound = hasVarLen ? posOfVarLen : posOfLastOptional + 1;
            for (int i = posOfFirstOptional; i < optionalUpperBound; i++) {
                if (i >= lastArgIdxFromRight) {
                    // WARNING: Ends here
                    return;
                }
                ParameterConstraint p = parameters[i];
                NSArgument a = arguments.get(i);
                validateArgument(i, p, a);
                lastArgIdxFromLeft++;
            }

            if (hasVarLen) {
                // Also means there might be args to the right of the varlen
                for (; optArgFromRight > posOfVarLen - posOfLastOptional; optArgFromRight--) {
                    ParameterConstraint p = parameters[posOfLastOptional + optArgFromRight];
                    int argNo = lastArgIdxFromRight + optArgFromRight;
                    if (argNo <= posOfVarLen) {
                        allArgsConsumed = true;
                        break;
                    }
                    NSArgument a = arguments.get(argNo);
                    validateArgument(argNo, p, a);
                }

                if (allArgsConsumed) {
                    // WARNING: Ends here
                    return;
                }
            }
        }

        if (hasVarLen) {
            ParameterConstraint p = parameters[posOfVarLen];
            int collected = 0;
            for (int j = posOfVarLen; j < lastArgIdxFromRight + optArgFromRight; j++) {
                NSArgument a = arguments.get(j);
                validateArgument(j, p, a);
                collected++;
            }
            if (collected == 0 && !hasOptional) {
                throw VMError.from(ArgumentsError, "At least one argument is required for the variable length parameter");
            }
        } else {
            for (int j = upperBoundFromLeft; j < arguments.size(); j++) {
                if (!arguments.get(j).isOptional()) {
                    throw VMError.from(ArgumentsError, format("Argument %d is extraneous", j));
                }
            }
        }
    }
    */

}
