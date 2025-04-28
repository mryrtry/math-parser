package Function;

import java.math.BigDecimal;

@FunctionalInterface
public interface MathFunction {
    BigDecimal calculate(BigDecimal[] values);
}
