package net.ttddyy.dsproxy.test;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public interface BatchExecutionEntry extends ParameterHolder {

    // TODO: implement values matcher??
    List<Object> getParamValues();

}