package in.wilsonl.nanoscript.Interpreting.Arguments;

import in.wilsonl.nanoscript.Interpreting.Data.NSData;
import in.wilsonl.nanoscript.Utils.ROList;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static in.wilsonl.nanoscript.Interpreting.Data.NSBoolean.TRUE;

public class ArgumentsValidatorTest {
    @Test
    public void testValidate() throws Exception {
        // fn (a, b, c, opt d, opt e, opt f, opt ...g, opt h, opt i, opt j, k, l, m)
        ArgumentsValidator validator = new ArgumentsValidator(new NSParameter[]{
                new NSParameter(false, false, "leftRequired1", null),
                new NSParameter(false, false, "leftRequired2", null),
                new NSParameter(false, false, "leftRequired3", null),

                new NSParameter(true, false, "optLeft1", null),
                new NSParameter(true, false, "optLeft2", null),
                new NSParameter(true, false, "optLeft3", null),

                new NSParameter(true, true, "optVarLen", null),

                new NSParameter(true, false, "optRight-3", null),
                new NSParameter(true, false, "optRight-2", null),
                new NSParameter(true, false, "optRight-1", null),

                new NSParameter(false, false, "rightRequired-3", null),
                new NSParameter(false, false, "rightRequired-2", null),
                new NSParameter(false, false, "rightRequired-1", null),
        });

        List<NSArgument> args = new ROList<>(13);
        args.add(new NSArgument(false, TRUE));
        args.add(new NSArgument(false, TRUE));
        args.add(new NSArgument(false, TRUE));

        args.add(new NSArgument(false, TRUE));
        args.add(new NSArgument(false, TRUE));
        args.add(new NSArgument(false, TRUE));

        args.add(new NSArgument(false, TRUE));
        args.add(new NSArgument(false, TRUE));
        args.add(new NSArgument(false, TRUE));
        args.add(new NSArgument(false, TRUE));
        args.add(new NSArgument(false, TRUE));

        args.add(new NSArgument(false, TRUE));
        args.add(new NSArgument(false, TRUE));
        args.add(new NSArgument(false, TRUE));

        args.add(new NSArgument(false, TRUE));
        args.add(new NSArgument(false, TRUE));
        args.add(new NSArgument(false, TRUE));

        Map<String, NSData> values = validator.match(args);

        System.out.println(values);
    }

}
