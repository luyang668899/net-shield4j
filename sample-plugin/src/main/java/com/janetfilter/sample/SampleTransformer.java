package com.janetfilter.sample;

import com.janetfilter.core.plugin.MyTransformer;
import com.janetfilter.core.plugin.PluginConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.security.ProtectionDomain;

public class SampleTransformer implements MyTransformer {
    private final PluginConfig config;
    
    public SampleTransformer(PluginConfig config) {
        this.config = config;
    }
    
    @Override
    public String getHookClassName() {
        // 要钩子的类名，使用斜杠分隔包名
        return "java/lang/String";
    }

    @Override
    public byte[] transform(ClassLoader loader, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, String className, byte[] classBytes, int order) throws Exception {
        // 使用ASM库修改字节码
        ClassReader cr = new ClassReader(classBytes);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        // 遍历所有方法
        for (MethodNode mn : cn.methods) {
            // 找到intern方法
            if ("intern".equals(mn.name) && "()Ljava/lang/String;".equals(mn.desc)) {
                System.out.println("Found intern method, modifying...");

                // 创建新的指令列表
                InsnList newInstructions = new InsnList();

                // 添加调用前的日志打印
                newInstructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                newInstructions.add(new LdcInsnNode("[SamplePlugin] String.intern() called!"));
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));

                // 添加原有的指令
                newInstructions.add(mn.instructions);

                // 添加调用后的日志打印
                newInstructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                newInstructions.add(new LdcInsnNode("[SamplePlugin] String.intern() returned!"));
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));

                // 替换原有的指令
                mn.instructions = newInstructions;
                break;
            }
        }

        // 生成修改后的字节码
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }
}