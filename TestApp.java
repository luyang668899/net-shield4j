import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestApp {
    public static void main(String[] args) {
        System.out.println("TestApp started!");
        
        // 调用String.intern()方法，这是我们插件钩子的方法
        String s1 = new String("test");
        String s2 = s1.intern();
        
        System.out.println("s1: " + s1);
        System.out.println("s2: " + s2);
        System.out.println("s1 == s2: " + (s1 == s2));
        
        // 测试CONTAINS_ANY规则
        System.out.println("\nTesting CONTAINS_ANY rules:");
        testContainsAnyRules();
        
        // 测试文件系统监控功能
        System.out.println("\nTesting file system monitoring:");
        testFileSystemMonitoring();
        
        // 测试网络监控功能
        System.out.println("\nTesting network monitoring:");
        testNetworkMonitoring();
        
        System.out.println("\nTestApp finished!");
    }
    
    private static void testContainsAnyRules() {
        // 测试CONTAINS_ANY规则，匹配apple, banana, orange中的任意一个
        String[] fruits = {"I like apple", "I like banana", "I like orange", "I like grape", "I like pineapple"};
        
        for (String fruit : fruits) {
            System.out.println("Fruit: " + fruit);
        }
        
        // 测试CONTAINS_ANY_IC规则，不区分大小写匹配CAT, DOG, BIRD中的任意一个
        String[] animals = {"I have a cat", "I have a dog", "I have a bird", "I have a fish", "I have a turtle"};
        
        for (String animal : animals) {
            System.out.println("Animal: " + animal);
        }
    }
    
    private static void testFileSystemMonitoring() {
        try {
            // 创建临时文件
            File tempFile = new File("test.txt");
            System.out.println("Creating temp file: " + tempFile.getAbsolutePath());
            
            // 测试exists方法
            System.out.println("File exists? " + tempFile.exists());
            
            // 测试createNewFile方法
            boolean created = tempFile.createNewFile();
            System.out.println("File created? " + created);
            
            // 再次测试exists方法
            System.out.println("File exists? " + tempFile.exists());
            
            // 测试delete方法
            boolean deleted = tempFile.delete();
            System.out.println("File deleted? " + deleted);
            
            // 最后测试exists方法
            System.out.println("File exists? " + tempFile.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void testNetworkMonitoring() {
        try {
            // 创建一个简单的HTTP请求
            URL url = new URL("https://example.com");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            // 设置请求方法
            conn.setRequestMethod("GET");
            
            // 连接到服务器
            conn.connect();
            
            // 获取响应码
            int responseCode = conn.getResponseCode();
            System.out.println("HTTP Response Code: " + responseCode);
            
            // 读取响应内容
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                // 输出响应长度
                System.out.println("Response Length: " + response.length() + " characters");
            }
            
            // 断开连接
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}