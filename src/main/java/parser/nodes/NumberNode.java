package parser.nodes;


import function.MathFunction;

import java.math.BigDecimal;

public class NumberNode implements ASTNode {
    private final BigDecimal value;

    public NumberNode(BigDecimal value) {
        this.value = value;
    }

    @Override
    public MathFunction toMathFunction() {
        return new MathFunction(0) {
            @Override
            public BigDecimal calculate(BigDecimal[] values) {
                return value;
            }
        };
    }
}