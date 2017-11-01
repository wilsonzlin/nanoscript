package in.wilsonl.nanoscript.Interpreting.Arguments;

import in.wilsonl.nanoscript.Exception.InternalStateError;
import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Utils.ROSet;

import java.util.Set;

public class NSParameter {
    private final boolean variableLength;
    private final boolean optional;
    private final String name; // Can't be null
    private final Set<NSData.Type> types = new ROSet<>(); // Can be empty
    private final String typesStringRep;

    // <types> can be null
    public NSParameter(boolean optional, boolean variableLength, String name, NSData.Type[] types) {
        if (name == null) {
            throw new InternalStateError("Parameter name is null");
        }

        this.optional = optional;
        this.variableLength = variableLength;
        this.name = name;

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
    }

    public NSParameter(boolean optional, boolean variableLength, String name) {
        this(optional, variableLength, name, null);
    }

    public NSParameter(String name, NSData.Type[] types) {
        this(false, false, name, types);
    }

    public NSParameter(String name, NSData.Type type) {
        this(false, false, name, new NSData.Type[]{type});
    }

    public NSParameter(String name) {
        this(false, false, name, null);
    }

    public NSParameter(boolean optional, String name, NSData.Type type) {
        this(optional, false, name, new NSData.Type[]{type});
    }

    public String getFriendlyTypesName() {
        return typesStringRep;
    }

    public String getName() {
        return name;
    }

    public boolean canAcceptType(NSData.Type type) {
        return types.isEmpty() || types.contains(type);
    }

    public boolean isVariableLength() {
        return variableLength;
    }

    public boolean isOptional() {
        return optional;
    }
}
