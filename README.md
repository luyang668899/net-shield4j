# ja-netfilter 2025.3.0

### A Java Instrumentation Framework

## Usage

* download from the [releases page](https://gitee.com/ja-netfilter/ja-netfilter/releases)
* add `-javaagent:/absolute/path/to/ja-netfilter.jar` argument (**Change to your actual path**)
    * add as an argument of the `java` command. eg: `java -javaagent:/absolute/path/to/ja-netfilter.jar -jar executable_jar_file.jar`
    * some apps support the `JVM Options file`, you can add as a line of the `JVM Options file`.
    * **WARNING: DO NOT put some unnecessary whitespace characters!**
* or execute `java -jar /path/to/ja-netfilter.jar` to use `attach mode`.

* edit your plugin config files: `${lower plugin name}.conf` file in the `config` dir where `ja-netfilter.jar` is located.
* the `config`, `logs` and `plugins` directories can be specified through **the javaagent args**.
  * eg: `-javaagent:/path/to/ja-netfilter.jar=appName`, your config, logs and plugins directories will be `config-appname`, `logs-appname` and `plugins-appname`.
  * if no javaagent args, they default to `config`, `logs` and `plugins`.
  * this mechanism will avoid extraneous and bloated `config`, `logs` and `plugins`.

* run your java application and enjoy~

## Config file format

```
[ABC]
# for the specified section name

# for example
[URL]
EQUAL,https://someurl

[DNS]
EQUAL,somedomain

# EQUAL       Use `equals` to compare
# EQUAL_IC    Use `equals` to compare, ignore case
# KEYWORD     Use `contains` to compare
# KEYWORD_IC  Use `contains` to compare, ignore case
# PREFIX      Use `startsWith` to compare
# PREFIX_IC   Use `startsWith` to compare, ignore case
# SUFFIX      Use `endsWith` to compare
# SUFFIX_IC   Use `endsWith` to compare, ignore case
# CONTAINS_ANY Use `contains` to compare any of the keywords (comma-separated)
# CONTAINS_ANY_IC Use `contains` to compare any of the keywords, ignore case
# REGEXP      Use regular expressions to match
```


## Debug info

* the `ja-netfilter` will **NOT** output debugging information by default
* add environment variable `JANF_DEBUG=1` (log level) and start to enable it
* or add system property `-Djanf.debug=1` (log level) to enable it
* log level: `NONE=0`, `DEBUG=1`, `INFO=2`, `WARN=3`, `ERROR=4`

## Debug output

* the `ja-netfilter` will output debugging information to the `console` by default
* add environment variable `JANF_OUTPUT=value` and start to change output medium
* or add system property `-Djanf.output=value` to change output medium
* output medium value: [`NONE=0`, `CONSOLE=1`, `FILE=2`, `CONSOLE+FILE=3`, `WITH_PID=4`]
* eg: `console` + `file` + `pid file name` = 1 + 2 + 4 = 7, so the `-Djanf.output=7`

## Plugin system

* for developer:
    * view the [scaffold project](https://gitee.com/ja-netfilter/ja-netfilter-sample-plugin) written for the plugin system
    * compile your plugin and publish it
    * just use your imagination~

* for user:
    * download the jar file of the plugin
    * put it in the subdirectory called `plugins` where the ja-netfilter.jar file is located
    * enjoy the new capabilities brought by the plugin
    * if the file suffix is `.disabled.jar`, the plugin will be disabled
   
## New Features in 2025.3.0

### 1. New Rule Types

#### CONTAINS_ANY
Matches content that contains any of the specified keywords (comma-separated).

```
[SECTION]
CONTAINS_ANY,keyword1,keyword2,keyword3
```

#### CONTAINS_ANY_IC
Case-insensitive version of CONTAINS_ANY.

```
[SECTION]
CONTAINS_ANY_IC,KEYWORD1,KEYWORD2,KEYWORD3
```

### 2. File System Monitoring Plugin

A sample plugin that monitors file system operations by hooking into the `java.io.File` class.

#### Features
- Monitors File constructor calls
- Tracks file existence checks
- Records file creation operations
- Logs file deletion events

#### Usage
1. Build the fs-monitor-plugin
2. Place the plugin jar in the `plugins` directory
3. Configure the plugin via `fsmonitor.conf` in the `config` directory

### 3. Network Monitoring Plugin

A sample plugin that monitors HTTP network requests by hooking into the `java.net.HttpURLConnection` class.

#### Features
- Monitors HTTP request methods (GET, POST, PUT, DELETE)
- Tracks connection establishment
- Records response status codes
- Logs response data access

#### Usage
1. Build the network-monitor-plugin
2. Place the plugin jar in the `plugins` directory
3. Configure the plugin via `networkmonitor.conf` in the `config` directory

#### Example Configuration
```
[NETWORK]
# Network Monitor Configuration
ENABLE=true
LOG_LEVEL=INFO
INCLUDE_DOMAINS=example.com,github.com
EXCLUDE_DOMAINS=localhost,127.0.0.1
MONITOR_GET=true
MONITOR_POST=true
```

### 4. Multiple Plugin Support

ja-netfilter now supports loading multiple plugins simultaneously. Each plugin runs in its own classloader and can have its own configuration file.

#### Example Plugin Configuration
```
[FSMONITOR]
# File System Monitor Configuration
MONITOR_CREATE=true
MONITOR_EXISTS=true
MONITOR_DELETE=true
```

## Development Examples

### Creating a Simple Plugin

1. **Create Plugin Entry Class**
   ```java
   public class MyPluginEntry implements PluginEntry {
       @Override
       public String getName() { return "myplugin"; }
       @Override
       public List<MyTransformer> getTransformers() {
           return Collections.singletonList(new MyTransformerImpl());
       }
   }
   ```

2. **Implement Transformer**
   ```java
   public class MyTransformerImpl implements MyTransformer {
       @Override
       public String getHookClassName() { return "java/lang/String"; }
       @Override
       public byte[] transform(...) {
           // Use ASM to modify bytecode
           return classFileBuffer;
       }
   }
   ```

3. **Configure Plugin**
   ```
   [MYPLUGIN]
   KEYWORD,test
   CONTAINS_ANY,apple,banana,orange
   ```

## Building from Source

### Prerequisites
- JDK 8 or later
- Maven 3.6.0 or later

### Build Steps

1. **Build the main project**
   ```bash
   ./mvnw clean package -DskipTests
   ```

2. **Build a plugin**
   ```bash
   cd sample-plugin
   mvn clean package -DskipTests
   ```

3. **Run tests**
   ```bash
   java -javaagent:target/ja-netfilter-jar-with-dependencies.jar -cp . TestApp
   ```

## License

GNU General Public License v3.0