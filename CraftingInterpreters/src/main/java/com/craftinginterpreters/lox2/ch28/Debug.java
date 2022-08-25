package com.craftinginterpreters.lox2.ch28;

import static com.craftinginterpreters.lox2.ch28.OpCode.OP_ADD;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_CALL;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_CLASS;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_CLOSE_UPVALUE;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_CLOSURE;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_CONSTANT;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_DEFINE_GLOBAL;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_DIVIDE;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_EQUAL;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_FALSE;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_GET_GLOBAL;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_GET_LOCAL;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_GET_PROPERTY;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_GET_UPVALUE;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_GREATER;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_INVOKE;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_JUMP;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_JUMP_IF_FALSE;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_LESS;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_LOOP;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_METHOD;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_MULTIPLY;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_NEGATE;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_NIL;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_NOT;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_POP;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_PRINT;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_RETURN;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_SET_GLOBAL;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_SET_LOCAL;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_SET_PROPERTY;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_SET_UPVALUE;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_SUBTRACT;
import static com.craftinginterpreters.lox2.ch28.OpCode.OP_TRUE;

import com.craftinginterpreters.lox2.ch14.Chunk;

public class Debug {
    public static void disassemble(Chunk chunk, String name) {
        System.out.printf("== %s ==\n", name);

        for (int offset = 0; offset < chunk.code.size();) {
            offset = disassembleInstruction(chunk, offset);
        }
    }

    public static int disassembleInstruction(Chunk chunk, int offset) {
        System.out.printf("%04d ", offset);
        if (offset > 0 && chunk.lines.get(offset) == chunk.lines.get(offset - 1)) {
            System.out.printf("   | ");
        } else {
            System.out.printf("%4d ", chunk.lines.get(offset));
        }

        byte instruction = chunk.code.get(offset);
        switch (instruction) {
        case OP_CONSTANT:
            return constantInstruction("OP_CONSTANT", chunk, offset);
        case OP_NIL:
            return simpleInstruction("OP_NIL", offset);
        case OP_TRUE:
            return simpleInstruction("OP_TRUE", offset);
        case OP_FALSE:
            return simpleInstruction("OP_FALSE", offset);
        case OP_POP:
            return simpleInstruction("OP_POP", offset);
        case OP_GET_LOCAL:
            return byteInstruction("OP_GET_LOCAL", chunk, offset);
        case OP_SET_LOCAL:
            return byteInstruction("OP_SET_LOCAL", chunk, offset);
        case OP_GET_GLOBAL:
            return constantInstruction("OP_GET_GLOBAL", chunk, offset);
        case OP_DEFINE_GLOBAL:
            return constantInstruction("OP_DEFINE_GLOBAL", chunk, offset);
        case OP_SET_GLOBAL:
            return constantInstruction("OP_SET_GLOBAL", chunk, offset);
        case OP_GET_UPVALUE:
            return byteInstruction("OP_GET_UPVALUE", chunk, offset);
        case OP_SET_UPVALUE:
            return byteInstruction("OP_SET_UPVALUE", chunk, offset);
        case OP_GET_PROPERTY:
            return constantInstruction("OP_GET_PROPERTY", chunk, offset);
        case OP_SET_PROPERTY:
            return constantInstruction("OP_SET_PROPERTY", chunk, offset);
        case OP_EQUAL:
            return simpleInstruction("OP_EQUAL", offset);
        case OP_GREATER:
            return simpleInstruction("OP_GREATER", offset);
        case OP_LESS:
            return simpleInstruction("OP_LESS", offset);
        case OP_ADD:
            return simpleInstruction("OP_ADD", offset);
        case OP_SUBTRACT:
            return simpleInstruction("OP_SUBTRACT", offset);
        case OP_MULTIPLY:
            return simpleInstruction("OP_MULTIPLY", offset);
        case OP_DIVIDE:
            return simpleInstruction("OP_DIVIDE", offset);
        case OP_NOT:
            return simpleInstruction("OP_NOT", offset);
        case OP_NEGATE:
            return simpleInstruction("OP_NEGATE", offset);
        case OP_PRINT:
            return simpleInstruction("OP_PRINT", offset);
        case OP_JUMP:
            return jumpInstruction("OP_JUMP", 1, chunk, offset);
        case OP_JUMP_IF_FALSE:
            return jumpInstruction("OP_JUMP_IF_FALSE", 1, chunk, offset);
        case OP_LOOP:
            return jumpInstruction("OP_LOOP", -1, chunk, offset);
        case OP_CALL:
            return byteInstruction("OP_CALL", chunk, offset);
        case OP_INVOKE:
            return invokeInstruction("OP_INVOKE", chunk, offset);
        case OP_CLOSURE: {
            offset++;
            byte constant = chunk.code.get(offset++);
            ObjFunction function = (ObjFunction) chunk.constants.get(constant);

            System.out.printf("%-16s %4d ", "OP_CLOSURE", constant);
            System.out.print(function);
            System.out.printf("\n");

            for (int j = 0; j < function.upvalueCount; j++) {
                int isLocal = chunk.code.get(offset++);
                int index = chunk.code.get(offset++);
                System.out.printf("%04d      |                     %s %d\n", offset - 2,
                        (isLocal == 1) ? "local" : "upvalue", index);
            }

            return offset;
        }
        case OP_CLOSE_UPVALUE:
            return simpleInstruction("OP_CLOSE_UPVALUE", offset);
        case OP_RETURN:
            return simpleInstruction("OP_RETURN", offset);
        case OP_CLASS:
            return constantInstruction("OP_CLASS", chunk, offset);
        case OP_METHOD:
            return constantInstruction("OP_METHOD", chunk, offset);
        default:
            System.out.printf("Unknown opcode %d\n", instruction);
            return offset + 1;
        }
    }

    static int constantInstruction(String name, Chunk chunk, int offset) {
        byte constant = chunk.code.get(offset + 1);
        System.out.printf("%-16s %4d '", name, constant);
        System.out.print(chunk.constants.get(constant));
        System.out.printf("'\n");
        return offset + 2;
    }

    static int invokeInstruction(String name, Chunk chunk, int offset) {
        byte constant = chunk.code.get(offset + 1);
        byte argCount = chunk.code.get(offset + 2);
        System.out.printf("%-16s (%d args) %4d '", name, argCount, constant);
        System.out.print(chunk.constants.get(constant));
        System.out.printf("'\n");
        return offset + 3;
    }

    static int simpleInstruction(String name, int offset) {
        System.out.printf("%s\n", name);
        return offset + 1;
    }

    static int byteInstruction(String name, Chunk chunk, int offset) {
        byte slot = chunk.code.get(offset + 1);
        System.out.printf("%-16s %4d\n", name, slot & 0xff);
        return offset + 2;
    }

    static int jumpInstruction(String name, int sign, Chunk chunk, int offset) {
        byte high = chunk.code.get(offset + 1);
        byte low = chunk.code.get(offset + 2);
        int jump = (high << 8 | low) & 0xffff;
        System.out.printf("%-16s %4d -> %d\n", name, offset, offset + 3 + sign * jump);
        return offset + 3;
    }
}
