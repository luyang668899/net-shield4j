package com.janetfilter.fsmonitor;

import com.janetfilter.core.plugin.MyTransformer;
import com.janetfilter.core.plugin.PluginConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.security.ProtectionDomain;

public class FSMonitorTransformer implements MyTransformer {
    private final PluginConfig config;
    
    public FSMonitorTransformer(PluginConfig config) {
        this.config = config;
    }
    
    @Override
    public String getHookClassName() {
        // 要钩子的类名，使用斜杠分隔包名
        return "java/io/File";
    }

    @Override
    public byte[] transform(ClassLoader loader, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, String className, byte[] classBytes, int order) throws Exception {
        // 使用ASM库修改字节码
        ClassReader cr = new ClassReader(classBytes);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        // 遍历所有方法
        for (MethodNode mn : cn.methods) {
            // 监控File构造函数
            if (mn.name.equals("<init>") && (mn.desc.equals("(Ljava/lang/String;)V"))) {
                System.out.println("Found File constructor, modifying...");
                addLoggingInstructions(mn, "File created: ", "Ljava/lang/String;");
            }
            
            // 监控exists方法
            if (mn.name.equals("exists") && mn.desc.equals("()Z")) {
                System.out.println("Found File.exists() method, modifying...");
                addLoggingInstructions(mn, "File.exists() called: ", "");
            }
            
            // 监控createNewFile方法
            if (mn.name.equals("createNewFile") && mn.desc.equals("()Z")) {
                System.out.println("Found File.createNewFile() method, modifying...");
                addLoggingInstructions(mn, "File.createNewFile() called: ", "");
            }
            
            // 监控delete方法
            if (mn.name.equals("delete") && mn.desc.equals("()Z")) {
                System.out.println("Found File.delete() method, modifying...");
                addLoggingInstructions(mn, "File.delete() called: ", "");
            }
        }

        // 生成修改后的字节码
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }
    
    /**
     * 向方法中添加日志记录指令
     */
    private void addLoggingInstructions(MethodNode mn, String logPrefix, String paramDesc) {
        // 创建新的指令列表
        InsnList newInstructions = new InsnList();
        
        // 添加调用前的日志打印
        newInstructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        newInstructions.add(new LdcInsnNode("[FSMonitor] " + logPrefix));
        
        // 如果有参数，获取this对象的路径
        if (!paramDesc.isEmpty()) {
            // 对于构造函数，参数是第一个参数
            if (mn.name.equals("<init>") && paramDesc.equals("Ljava/lang/String;")) {
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1)); // 加载第一个参数
            } else {
                // 对于实例方法，获取this对象的路径
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // 加载this
                newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/File", "getPath", "()Ljava/lang/String;", false));
            }
        }
        
        // 调用println方法
        newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
        
        // 添加原有的指令
        newInstructions.add(mn.instructions);
        
        // 替换原有的指令
        mn.instructions = newInstructions;
    }
}