package com.craftinginterpreters.lox2.complete;

public class ObjClosure {
    public ObjFunction function;
    public ObjUpvalue[] upvalues;

    public ObjClosure(ObjFunction function) {
        this.function = function;
        this.upvalues = new ObjUpvalue[function.upvalueCount];
    }

    @Override
    public String toString() {
        return function.toString();
    }
}
