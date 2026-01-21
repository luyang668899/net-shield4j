package com.janetfilter.network;

import com.janetfilter.core.plugin.MyTransformer;
import com.janetfilter.core.plugin.PluginConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.security.ProtectionDomain;

public class NetworkMonitorTransformer implements MyTransformer {
    private final PluginConfig config;
    
    public NetworkMonitorTransformer(PluginConfig config) {
        this.config = config;
    }
    
    @Override
    public String getHookClassName() {
        // 要钩子的类名，使用斜杠分隔包名
        return "java/net/HttpURLConnection";
    }

    @Override
    public byte[] transform(ClassLoader loader, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, String className, byte[] classBytes, int order) throws Exception {
        // 使用ASM库修改字节码
        ClassReader cr = new ClassReader(classBytes);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        // 遍历所有方法
        for (MethodNode mn : cn.methods) {
            // 监控setRequestMethod方法
            if (mn.name.equals("setRequestMethod") && mn.desc.equals("(Ljava/lang/String;)V")) {
                System.out.println("Found HttpURLConnection.setRequestMethod() method, modifying...");
                addRequestMethodLogging(mn);
            }
            
            // 监控connect方法
            if (mn.name.equals("connect") && mn.desc.equals("()V")) {
                System.out.println("Found HttpURLConnection.connect() method, modifying...");
                addConnectLogging(mn);
            }
            
            // 监控getResponseCode方法
            if (mn.name.equals("getResponseCode") && mn.desc.equals("()I")) {
                System.out.println("Found HttpURLConnection.getResponseCode() method, modifying...");
                addResponseCodeLogging(mn);
            }
            
            // 监控getInputStream方法
            if (mn.name.equals("getInputStream") && mn.desc.equals("()Ljava/io/InputStream;")) {
                System.out.println("Found HttpURLConnection.getInputStream() method, modifying...");
                addResponseLogging(mn);
            }
        }

        // 生成修改后的字节码
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }
    
    /**
     * 向setRequestMethod方法添加日志记录指令
     */
    private void addRequestMethodLogging(MethodNode mn) {
        // 创建新的指令列表
        InsnList newInstructions = new InsnList();
        
        // 先执行原有方法
        newInstructions.add(mn.instructions);
        
        // 添加请求方法日志
        newInstructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        newInstructions.add(new LdcInsnNode("[NetworkMonitor] Request Method: "));
        // 加载方法参数（请求方法）
        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        // 调用println方法
        newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
        
        // 替换原有的指令
        mn.instructions = newInstructions;
    }
    
    /**
     * 向connect方法添加日志记录指令
     */
    private void addConnectLogging(MethodNode mn) {
        // 创建新的指令列表
        InsnList newInstructions = new InsnList();
        
        // 添加调用前的日志打印
        newInstructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        newInstructions.add(new LdcInsnNode("[NetworkMonitor] Connecting to: "));
        
        // 获取URL
        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/net/URLConnection", "getURL", "()Ljava/net/URL;", false));
        newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/net/URL", "toString", "()Ljava/lang/String;", false));
        
        // 调用println方法
        newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
        
        // 添加原有的指令
        newInstructions.add(mn.instructions);
        
        // 替换原有的指令
        mn.instructions = newInstructions;
    }
    
    /**
     * 向getResponseCode方法添加日志记录指令
     */
    private void addResponseCodeLogging(MethodNode mn) {
        // 创建新的指令列表
        InsnList newInstructions = new InsnList();
        
        // 先执行原有方法，获取返回值（响应状态码）
        newInstructions.add(mn.instructions);
        
        // 保存响应状态码到局部变量
        newInstructions.add(new VarInsnNode(Opcodes.ISTORE, mn.maxLocals));
        
        // 添加响应状态码日志
        newInstructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        newInstructions.add(new LdcInsnNode("[NetworkMonitor] Response Code: "));
        // 加载响应状态码
        newInstructions.add(new VarInsnNode(Opcodes.ILOAD, mn.maxLocals));
        // 转换为字符串
        newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false));
        // 调用println方法
        newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
        
        // 恢复返回值
        newInstructions.add(new VarInsnNode(Opcodes.ILOAD, mn.maxLocals));
        
        // 替换原有的指令
        mn.instructions = newInstructions;
    }
    
    /**
     * 向getInputStream方法添加响应日志记录指令
     */
    private void addResponseLogging(MethodNode mn) {
        // 创建新的指令列表
        InsnList newInstructions = new InsnList();
        
        // 先执行原有方法，获取返回值
        newInstructions.add(mn.instructions);
        
        // 保存返回值到局部变量
        newInstructions.add(new VarInsnNode(Opcodes.ASTORE, mn.maxLocals));
        
        // 添加响应日志打印
        newInstructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        newInstructions.add(new LdcInsnNode("[NetworkMonitor] Response received, reading input stream"));
        newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
        
        // 恢复返回值
        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, mn.maxLocals));
        
        // 替换原有的指令
        mn.instructions = newInstructions;
    }
}