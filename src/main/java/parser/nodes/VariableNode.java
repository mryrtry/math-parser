package parser.nodes;


import function.MathFunction;

import java.math.BigDecimal;

public record VariableNode(String name, int index) implements ASTNode {

    @Override
    public MathFunction toMathFunction() {
        return new MathFunction(1) {
            @Override
            public BigDecimal calculate(BigDecimal[] values) {
                if (index >= values.length) {
                    throw new IllegalArgumentException("Not enough values provided for variable " + name);
                }
                return values[index];
            }
        };
    }
}