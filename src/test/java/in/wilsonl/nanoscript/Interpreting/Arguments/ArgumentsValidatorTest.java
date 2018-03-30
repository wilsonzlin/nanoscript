package in.wilsonl.nanoscript.Interpreting.Arguments;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Interpreting.Data.NSList;
import in.wilsonl.nanoscript.Utils.ROList;
import in.wilsonl.nanoscript.Utils.ROMap;
import in.wilsonl.nanoscript.Utils.SetOnce;
import in.wilsonl.nanoscript.Utils.Utils;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static in.wilsonl.nanoscript.Interpreting.Data.NSBoolean.TRUE;
import static org.junit.Assert.assertTrue;

public class ArgumentsValidatorTest {
  private void test (Builder config) {
    NSParameter[] paramArr = new NSParameter[config.parameters.size()];
    config.parameters.toArray(paramArr);

    ArgumentsValidator validator = new ArgumentsValidator(null, paramArr);
    NSValidatedArguments result = validator.match(config.arguments);

    Set<String> extraKeys = result.getNames();
    extraKeys.removeAll(config.expected.keySet());

    List<NSData> varLenResult = null;
    if (config.varLenName.isSet()) {
      extraKeys.remove(config.varLenName.get());
      varLenResult = ((NSList) result.get(config.varLenName.get())).getRawList();
    }

    if (!extraKeys.isEmpty()) {
      throw new AssertionError("Extra arguments found: " + Utils.join(", ", extraKeys));
    }

    Set<String> missingKeys = new HashSet<>(config.expected.keySet());
    missingKeys.removeAll(result.getNames());
    if (!missingKeys.isEmpty()) {
      throw new AssertionError("Missing arguments: " + Utils.join(", ", missingKeys));
    }

    for (Map.Entry<String, NSData> expectedArg : config.expected.entrySet()) {
      assertTrue(expectedArg
        .getValue()
        .nsTestEquality(result.get(expectedArg.getKey()))
        .isTrue());
    }
    if (varLenResult != null) {
      for (int i = 0; i < config.expectedVarLenArgs
        .get()
        .size(); i++) {
        assertTrue(i < varLenResult.size());
        assertTrue(config.expectedVarLenArgs
          .get()
          .get(i)
          .nsTestEquality(varLenResult.get(i))
          .isTrue());
      }
    }
  }

  @Test
  public void testOptionalArguments () {
    // lambda := fn (a) endfn
    // lambda(3, optional 4)
    test(new Builder()
      .addParameter(false, false, "a")

      .addArgument(false, TRUE)
      .addArgument(true, TRUE)

      .addExpected("a", TRUE)
    );
  }

  @Test
  public void testFullFat () {
    // fn (a, b, c, opt d, opt e, opt f, opt ...g, opt h, opt i, opt j, k, l, m)
    test(new Builder()
      .addParameter(false, false, "leftRequired1")
      .addParameter(false, false, "leftRequired2")
      .addParameter(false, false, "leftRequired3")

      .addParameter(true, false, "optLeft1")
      .addParameter(true, false, "optLeft2")
      .addParameter(true, false, "optLeft3")

      .addParameter(true, true, "optVarLen")

      .addParameter(true, false, "optRight-3")
      .addParameter(true, false, "optRight-2")
      .addParameter(true, false, "optRight-1")

      .addParameter(false, false, "rightRequired-3")
      .addParameter(false, false, "rightRequired-2")
      .addParameter(false, false, "rightRequired-1")


      .addArgument(false, TRUE)
      .addArgument(false, TRUE)
      .addArgument(false, TRUE)

      .addArgument(false, TRUE)
      .addArgument(false, TRUE)
      .addArgument(false, TRUE)

      .addArgument(false, TRUE)
      .addArgument(false, TRUE)
      .addArgument(false, TRUE)
      .addArgument(false, TRUE)
      .addArgument(false, TRUE)

      .addArgument(false, TRUE)
      .addArgument(false, TRUE)
      .addArgument(false, TRUE)

      .addArgument(false, TRUE)
      .addArgument(false, TRUE)
      .addArgument(false, TRUE)

      .addExpected("leftRequired1", TRUE)
      .addExpected("leftRequired2", TRUE)
      .addExpected("leftRequired3", TRUE)

      .addExpected("optLeft1", TRUE)
      .addExpected("optLeft2", TRUE)
      .addExpected("optLeft3", TRUE)

      .addExpected("optRight-3", TRUE)
      .addExpected("optRight-2", TRUE)
      .addExpected("optRight-1", TRUE)

      .addExpected("rightRequired-3", TRUE)
      .addExpected("rightRequired-2", TRUE)
      .addExpected("rightRequired-1", TRUE)

      .addExpectedVarLenArg(TRUE)
      .addExpectedVarLenArg(TRUE)
      .addExpectedVarLenArg(TRUE)
      .addExpectedVarLenArg(TRUE)
      .addExpectedVarLenArg(TRUE)
    );
  }

  private static class Builder {
    private final List<NSParameter> parameters = new ROList<>();
    private final List<NSArgument> arguments = new ROList<>();
    private final Map<String, NSData> expected = new ROMap<>();
    private final SetOnce<List<NSData>> expectedVarLenArgs = new SetOnce<>();
    private final SetOnce<String> varLenName = new SetOnce<>();

    public Builder addParameter (boolean optional, boolean variableLength, String name, NSData.Type... types) {
      if (variableLength) {
        expectedVarLenArgs.set(new ROList<>());
        varLenName.set(name);
      }
      parameters.add(new NSParameter(optional, variableLength, name, types, null));
      return this;
    }

    public Builder addArgument (boolean optional, NSData value) {
      arguments.add(new NSArgument(optional, value));
      return this;
    }

    public Builder addExpected (String name, NSData value) {
      expected.put(name, value);
      return this;
    }

    public Builder addExpectedVarLenArg (NSData value) {
      expectedVarLenArgs
        .get()
        .add(value);
      return this;
    }
  }

}
