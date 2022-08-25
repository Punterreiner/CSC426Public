package com.craftinginterpreters.lox2.ch18;

import static com.craftinginterpreters.lox2.ch18.OpCode.OP_ADD;
import static com.craftinginterpreters.lox2.ch18.OpCode.OP_CONSTANT;
import static com.craftinginterpreters.lox2.ch18.OpCode.OP_DIVIDE;
import static com.craftinginterpreters.lox2.ch18.OpCode.OP_EQUAL;
import static com.craftinginterpreters.lox2.ch18.OpCode.OP_FALSE;
import static com.craftinginterpreters.lox2.ch18.OpCode.OP_GREATER;
import static com.craftinginterpreters.lox2.ch18.OpCode.OP_LESS;
import static com.craftinginterpreters.lox2.ch18.OpCode.OP_MULTIPLY;
import static com.craftinginterpreters.lox2.ch18.OpCode.OP_NEGATE;
import static com.craftinginterpreters.lox2.ch18.OpCode.OP_NIL;
import static com.craftinginterpreters.lox2.ch18.OpCode.OP_NOT;
import static com.craftinginterpreters.lox2.ch18.OpCode.OP_RETURN;
import static com.craftinginterpreters.lox2.ch18.OpCode.OP_SUBTRACT;
import static com.craftinginterpreters.lox2.ch18.OpCode.OP_TRUE;

import java.util.Objects;
import java.util.Stack;

import com.craftinginterpreters.lox2.ch14.Chunk;

public class VM {
    public enum Result {
        INTERPRET_OK, INTERPRET_COMPILE_ERROR, INTERPRET_RUNTIME_ERROR
    }

    private Chunk chunk;
    private int ip;
    private Stack<Object> stack = new Stack<>();
    private boolean debugTraceExecution = false;
    private boolean debugPrintCode = false;

    public Result interpret(Chunk chunk) {
        this.chunk = chunk;
        this.ip = 0;

        try {
            return run();
        } catch (RuntimeError e) {
            return Result.INTERPRET_RUNTIME_ERROR;
        }
    }

    public Result interpret(String source) {
        Chunk chunk = new Chunk();
        Compiler compiler = new Compiler();

        if (!compiler.compile(source, chunk)) {
            return Result.INTERPRET_COMPILE_ERROR;
        }

        if (debugPrintCode) {
            Debug.disassemble(chunk, "code");
        }

        return interpret(chunk);
    }

    public void setDebugTraceExecution(boolean b) {
        debugTraceExecution = b;
    }

    public void setDebugPrintCode(boolean b) {
        debugPrintCode = b;
    }

    private Result run() {
        for (;;) {
            if (debugTraceExecution) {
                printStack();
                Debug.disassembleInstruction(chunk, ip);
            }
            byte instruction = readByte();
            switch (instruction) {
            case OP_CONSTANT: {
                Object constant = readConstant();
                stack.push(constant);
                break;
            }
            case OP_NIL: {
                stack.push(null);
                break;
            }
            case OP_TRUE: {
                stack.push(true);
                break;
            }
            case OP_FALSE: {
                stack.push(false);
                break;
            }
            case OP_EQUAL: {
                Object b = stack.pop();
                Object a = stack.pop();
                stack.push(Objects.equals(a, b));
                break;
            }
            case OP_GREATER: {
                double b = popNumber();
                double a = popNumber();
                stack.push(a > b);
                break;
            }
            case OP_LESS: {
                double b = popNumber();
                double a = popNumber();
                stack.push(a < b);
                break;
            }
            case OP_ADD: {
                double b = popNumber();
                double a = popNumber();
                stack.push(a + b);
                break;
            }
            case OP_SUBTRACT: {
                double b = popNumber();
                double a = popNumber();
                stack.push(a - b);
                break;
            }
            case OP_MULTIPLY: {
                double b = popNumber();
                double a = popNumber();
                stack.push(a * b);
                break;
            }
            case OP_DIVIDE: {
                double b = popNumber();
                double a = popNumber();
                stack.push(a / b);
                break;
            }
            case OP_NOT: {
                Object a = stack.pop();
                stack.push(isFalsey(a));
                break;
            }
            case OP_NEGATE: {
                double a = popNumber();
                stack.push(-a);
                break;
            }
            case OP_RETURN: {
                System.out.println(showValue(stack.pop()));
                return Result.INTERPRET_OK;
            }
            }
        }
    }

    private double popNumber() {
        Object value = stack.pop();
        if (value instanceof Double d) {
            return d;
        } else {
            runtimeError("Operand must be a number.");
            return 0; // Unused.
        }
    }

    private boolean isFalsey(Object value) {
        if (value == null) {
            return true;
        } else if (value instanceof Boolean b) {
            return !b;
        } else {
            return false;
        }
    }

    private void printStack() {
        System.out.print("          ");
        for (int i = 0; i < stack.size(); i++) {
            System.out.print("[ " + showValue(stack.get(i)) + " ]");
        }
        System.out.println();
    }

    private String showValue(Object value) {
        if (value == null) {
            return "nil";
        } else {
            return value.toString();
        }
    }

    private void runtimeError(String format, Object... args) {
        System.err.printf(format, args);
        System.err.println();

        int instruction = ip - 1;
        int line = chunk.lines.get(instruction);
        System.err.printf("[line %d] in script\n", line);
        throw new RuntimeError();
    }

    private byte readByte() {
        return chunk.code.get(ip++);
    }

    private Object readConstant() {
        // bitwise-and with 0xff to convert signed byte into
        // an int in the range 0 to 255
        return chunk.constants.get(readByte() & 0xff);
    }
}
