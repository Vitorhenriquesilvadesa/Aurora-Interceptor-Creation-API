package org.aurora.emulator;

import org.aurora.binary.AurBytecode;
import org.aurora.compiler.AurInstructionCode;
import org.aurora.component.AurNullIOComponent;
import org.aurora.exception.AurException;
import org.aurora.pass.AurCompilationPass;
import org.aurora.type.AurValue;
import org.aurora.type.AurValueType;

import java.util.*;

public class AurVirtualMachine extends AurCompilationPass<AurBytecode, AurNullIOComponent> {

    private final Stack<AurValue> stack;
    private int ip;
    private List<Byte> code;
    private List<AurValue> constantTable;
    private Map<Byte, AurValue> variables;
    private List<String> stringPool;

    public AurVirtualMachine() {
        stack = new Stack<>();
    }

    private void execute(AurBytecode bytecode) {
        code = new ArrayList(bytecode.rawCode);
        constantTable = bytecode.constantTable.values().stream().toList();
        variables = new HashMap<>();
        stringPool = new ArrayList<>();

        stringPool.addAll(bytecode.stringTable.values());

        long start = System.currentTimeMillis();

        for (ip = 0; ip < bytecode.code.length; ) {
            byte b;

            switch (b = readByte()) {
                case AurInstructionCode.LOAD: {
                    byte index = readByte();
                    stack.push(variables.get(index));
                    break;
                }

                case AurInstructionCode.LOAD_CONST:
                    loadConst();
                    break;

                case AurInstructionCode.ADD:
                case AurInstructionCode.SUB:
                case AurInstructionCode.MUL:
                case AurInstructionCode.DIV:
                    binaryExpression(b);
                    break;

                case AurInstructionCode.LESS:
                case AurInstructionCode.LESS_EQUAL:
                case AurInstructionCode.GREATER:
                case AurInstructionCode.GREATER_EQUAL:
                case AurInstructionCode.EQUAL_EQUAL:
                case AurInstructionCode.MARK_EQUAL:
                    comparisonExpression(b);
                    break;

                case AurInstructionCode.AND:
                case AurInstructionCode.OR:
                    logicalExpression(b);
                    break;

                case AurInstructionCode.NEGATE:
                    negate();
                    break;

                case AurInstructionCode.INVERSE:
                    inverse();
                    break;

                case AurInstructionCode.PRINT:
                    print();
                    break;

                case AurInstructionCode.JUMP: {
                    byte lowByte = readByte();
                    byte highByte = readByte();

                    short offset = (short) ((highByte << 8) | (lowByte & 0xFF));
                    ip += offset;
                    break;
                }

                case AurInstructionCode.LOOP: {
                    byte lowByte = readByte();
                    byte highByte = readByte();

                    short offset = (short) ((highByte << 8) | (lowByte & 0xFF));
                    ip -= offset;
                    break;
                }

                case AurInstructionCode.JUMP_IF_FALSE: {
                    byte lowByte = readByte();
                    byte highByte = readByte();

                    short offset = (short) ((highByte << 8) | (lowByte & 0xFF));

                    AurValue value = stack.pop();

                    if (!((boolean) value.value)) {
                        ip += offset;
                    }

                    break;
                }

                case AurInstructionCode.DEFINE: {
                    variables.put(readByte(), stack.pop());
                    break;
                }

                case AurInstructionCode.STORE: {
                    Byte index = readByte();
                    if (variables.containsKey(index)) {
                        variables.put(index, stack.pop());
                    } else {
                        throw new AurException("Variable '" + index + "' not found");
                    }
                    break;
                }

                case AurInstructionCode.RETURN:
                    // System.out.println("Return with code 0.");
                    long end = System.currentTimeMillis();
                    long delta = end - start;
                    System.out.println(delta / 1000f + " seconds.");
                    return;
            }
        }
    }

    private void print() {
        AurValue value = stack.pop();
        System.out.println(value.toString());
    }

    private void inverse() {
        AurValue value = stack.pop();
        stack.push(new AurValue(!((boolean) value.value), AurValueType.BOOL));
    }

    private void logicalExpression(byte type) {
        AurValue b = stack.pop();
        AurValue a = stack.pop();

        switch (type) {
            case AurInstructionCode.AND: {
                AurValue value = new AurValue((boolean) a.value && (boolean) b.value, AurValueType.BOOL);
                stack.push(value);
                break;
            }

            case AurInstructionCode.OR: {
                AurValue value = new AurValue((boolean) a.value || (boolean) b.value, AurValueType.BOOL);
                stack.push(value);
                break;
            }
        }
    }

    private void comparisonExpression(byte type) {

        AurValue b = stack.pop();
        AurValue a = stack.pop();

        switch (type) {
            case AurInstructionCode.LESS: {
                AurValue value = new AurValue((int) a.value < (int) b.value, AurValueType.BOOL);
                stack.push(value);
                break;
            }

            case AurInstructionCode.LESS_EQUAL: {
                AurValue value = new AurValue((int) a.value <= (int) b.value, AurValueType.BOOL);
                stack.push(value);
                break;
            }

            case AurInstructionCode.GREATER: {
                AurValue value = new AurValue((int) a.value > (int) b.value, AurValueType.BOOL);
                stack.push(value);
                break;
            }

            case AurInstructionCode.GREATER_EQUAL: {
                AurValue value = new AurValue((int) a.value >= (int) b.value, AurValueType.BOOL);
                stack.push(value);
                break;
            }

            case AurInstructionCode.EQUAL_EQUAL: {
                AurValue value = new AurValue(a.value.equals(b.value), AurValueType.BOOL);
                stack.push(value);
                break;
            }

            case AurInstructionCode.MARK_EQUAL: {
                AurValue value = new AurValue(!(a.value.equals(b.value)), AurValueType.BOOL);
                stack.push(value);
                break;
            }
        }
    }

    private void negate() {
        AurValue value = stack.pop();
        stack.push(new AurValue(-(int) value.value, AurValueType.INT));
    }

    private void binaryExpression(byte type) {

        AurValue b = stack.pop();
        AurValue a = stack.pop();

        switch (type) {
            case AurInstructionCode.ADD: {
                AurValue result = new AurValue((int) a.value + (int) b.value, AurValueType.INT);
                stack.push(result);
                break;
            }

            case AurInstructionCode.SUB: {
                AurValue result = new AurValue((int) a.value - (int) b.value, AurValueType.INT);
                stack.push(result);
                break;
            }

            case AurInstructionCode.MUL: {
                AurValue result = new AurValue((int) a.value * (int) b.value, AurValueType.INT);
                stack.push(result);
                break;
            }
            case AurInstructionCode.DIV: {
                AurValue result = new AurValue((int) a.value / (int) b.value, AurValueType.INT);
                stack.push(result);
                break;
            }
        }
    }

    private byte readByte() {
        return code.get(ip++);
    }

    private void loadConst() {
        byte constantIndex = readByte();
        AurValue value = constantTable.get(constantIndex);
        stack.push(value);
    }

    private int getInt() {
        int value = (code.get(ip) & 0xFF) << 24 |
                (code.get(ip + 1) & 0xFF) << 16 |
                (code.get(ip + 2) & 0xFF) << 8 |
                (code.get(ip + 3) & 0xFF);

        ip += 4;
        return value;
    }

    @Override
    public Class<AurBytecode> getInputType() {
        return AurBytecode.class;
    }

    @Override
    public Class<AurNullIOComponent> getOutputType() {
        return AurNullIOComponent.class;
    }

    @Override
    public String getDebugName() {
        return "Virtual Machine";
    }

    @Override
    protected AurNullIOComponent pass(AurBytecode input) {
        execute(input);
        return new AurNullIOComponent();
    }
}
