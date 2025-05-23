package function;

import java.math.BigDecimal;

public abstract class MathFunction {
    private final int varNumber;

    public MathFunction(int varNumber) {
        this.varNumber = varNumber;
    }

    public abstract BigDecimal calculate(BigDecimal[] values);

    public int getVarNumber() {
        return varNumber;
    }
}
