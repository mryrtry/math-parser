package parser;

import java.util.HashMap;
import java.util.Map;

class VariableTracker {
    private final Map<String, Integer> variableIndices = new HashMap<>();
    private int nextIndex = 0;

    public int getIndexForVariable(String name) {
        return variableIndices.computeIfAbsent(name, k -> nextIndex++);
    }

    public int getTotalVariables() {
        return nextIndex;
    }
}