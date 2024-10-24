package org.aurora.binary;

import org.aurora.compiler.AurCompiledCode;
import org.aurora.exception.AurException;
import org.aurora.pass.AurCompilationPass;
import org.aurora.type.AurValue;
import org.aurora.type.AurValueType;
import org.aurora.util.HashUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AurBytecodeEmissionPass extends AurCompilationPass<AurCompiledCode, AurBytecode> {

    private final DataOutputStream writer;
    private final String filePath = "/home/vitor/IdeaProjects/Aurora/project/build/test.abc";
    private BytecodeHeader header;
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "aurora_first_ver";

    public AurBytecodeEmissionPass() {
        try {
            writer = new DataOutputStream(new FileOutputStream(filePath));
        } catch (IOException e) {
            throw new AurException("Cannot open .abc file.");
        }
    }

    @Override
    public Class<AurCompiledCode> getInputType() {
        return AurCompiledCode.class;
    }

    @Override
    public Class<AurBytecode> getOutputType() {
        return AurBytecode.class;
    }

    @Override
    public String getDebugName() {
        return "Bytecode Emission Pass";
    }

    @Override
    protected AurBytecode pass(AurCompiledCode input) {
        List<Byte> rawCode = new ArrayList<>();

        try {
            writer.writeByte(AurBinaryCodes.HEADER);
            byte[] hashBytes = new byte[input.code.size()];

            for (int i = 0; i < hashBytes.length; i++) {
                if (input.code.get(i) != null) {
                    hashBytes[i] = ((Number) input.code.toArray()[i]).byteValue();
                }
            }

            String hash = HashUtil.generateHash(hashBytes);
            byte[] encryptedBytes = encrypt(hash);

            for (Byte aByte : encryptedBytes) {
                writer.writeByte(aByte);
            }

            writer.writeByte(AurBinaryCodes.MAIN);

            for (Byte byteCode : input.code) {
                writer.writeByte(byteCode);
                rawCode.add(byteCode);
            }

            writer.writeByte(AurBinaryCodes.STRING_POOL);

            for (Map.Entry<Byte, String> string : input.stringTable.entrySet()) {
                writer.writeByte(string.getValue().length());

                for (char c : string.getValue().toCharArray()) {
                    writer.writeByte((byte) c);
                }
            }

            writer.writeByte(AurBinaryCodes.TABLE);

            for (Map.Entry<Byte, AurValue> entry : input.constantTable.entrySet()) {
                writer.writeByte(entry.getValue().type);

                switch (entry.getValue().type) {
                    case AurValueType.STRING:
                        byte[] bytes = ((String) entry.getValue().value).getBytes();

                        writer.writeByte(bytes.length);

                        for (byte aByte : bytes) {
                            writer.writeByte(aByte);
                        }
                        break;

                    case AurValueType.INT:
                        writer.writeInt((Integer) entry.getValue().value);
                        break;

                    case AurValueType.FLOAT:
                        writer.writeFloat((Float) entry.getValue().value);
                        break;

                    case AurValueType.BOOL:
                        writer.writeByte((Boolean) entry.getValue().value ? 1 : 0);
                        break;

                    case AurValueType.CHAR:
                        writer.writeChar((Character) entry.getValue().value);
                        break;

                    default:
                        writer.writeBytes(entry.getKey().toString());
                        break;
                }
                writer.writeByte(entry.getKey());
            }

            writer.writeByte(AurBinaryCodes.END_OF_FILE);

            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new AurException("Error writing bytecode to file.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new AurBytecode(filePath, rawCode, input.constantTable, input.stringTable);
    }

    private byte[] encrypt(String hash) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(hash.getBytes());
    }
}
