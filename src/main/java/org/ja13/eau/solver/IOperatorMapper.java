package org.ja13.eau.solver;

public interface IOperatorMapper {
    IOperator newOperator(String key, int depthDelta, java.util.List<Object> arg, int argOffset);
}
