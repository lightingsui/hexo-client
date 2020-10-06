package com.lightingsui.linuxwatcher.ssh;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.lightingsui.linuxwatcher.config.LogConfig;
import lombok.Data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;

/**
 * 连接处理
 *
 * @author ：隋亮亮
 * @since ：2020/9/22 15:01
 */
@Data
public class SSHHelper {

    private static final int CONNECT_TIMEOUT = 10000;
    private String host;
    private String user;
    private String password;
    private Integer port;
    private Session session;
    private ChannelExec openChannel;

    private static final Integer DEFAULT_PORT = 22;
    private static final String DEFAULT_USER = "root";



    public SSHHelper(String host, String password) {
        this(host, DEFAULT_USER, password, DEFAULT_PORT);
    }

    public SSHHelper(String host, String user, String password) {
        this(host, user, password, DEFAULT_PORT);
    }

    public SSHHelper(String host, String password, Integer port) {
        this(host, DEFAULT_USER, password, port);
    }

    public SSHHelper(String host, String user, String password, Integer port) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    /**
     * 获取ssh连接
     *
     * @return 连接成功返回{@code true}, 连接失败返回{@code false}
     * @throws JSchException 连接异常信息
     */
    public boolean getConnection() {
        JSch jsch = new JSch();
        try {
            session = jsch.getSession(user, host, port);
            java.util.Properties config = new java.util.Properties();

            //跳过公钥的询问
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(password);

            //设置连接的超时时间
            session.connect(10000);
        } catch (JSchException e) {

            e.printStackTrace();
            return false;
        }


        return true;
    }

    /**
     * 执行 Linux 命令
     *
     * @param command Linux命令
     * @return 命令结果
     * @throws JSchException
     * @throws IOException
     */
    public String execCommand(String command, boolean enableEnter) {
        StringBuffer res = new StringBuffer();
        try {
            openChannel = (ChannelExec) session.openChannel("exec");
            //执行命令
            openChannel.setCommand(command);

            //退出状态为-1，直到通道关闭
            openChannel.getExitStatus();

            // 下面是得到输出的内容
            openChannel.connect();
            InputStream in = openChannel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String buf = null;

            while ((buf = reader.readLine()) != null) {
                res.append(buf);
                if(enableEnter) {
                    res.append("\n");
                }
            }
//            disConnect();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


        return res.toString();
    }

    public long scpTo(String source, Session session, String destination) {
        FileInputStream fileInputStream = null;
        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();
            boolean ptimestamp = false;
            String command = "scp";
            if (ptimestamp) {
                command += " -p";
            }
            command += " -t " + destination;
            channel.setCommand(command);
            channel.connect(CONNECT_TIMEOUT);
            if (checkAck(in) != 0) {
                return -1;
            }
            File _lfile = new File(source);
            if (ptimestamp) {
                command = "T " + (_lfile.lastModified() / 1000) + " 0";
                // The access time should be sent here,
                // but it is not accessible with JavaAPI ;-<
                command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
                out.write(command.getBytes());
                out.flush();
                if (checkAck(in) != 0) {
                    return -1;
                }
            }
            //send "C0644 filesize filename", where filename should not include '/'
            long fileSize = _lfile.length();
            command = "C0644 " + fileSize + " ";
            if (source.lastIndexOf('/') > 0) {
                command += source.substring(source.lastIndexOf('/') + 1);
            } else {
                command += source;
            }
            command += "\n";
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                return -1;
            }
            //send content of file
            fileInputStream = new FileInputStream(source);
            byte[] buf = new byte[1024];
            long sum = 0;
            while (true) {
                int len = fileInputStream.read(buf, 0, buf.length);
                if (len <= 0) {
                    break;
                }
                out.write(buf, 0, len);
                sum += len;
            }
            //send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            if (checkAck(in) != 0) {
                return -1;
            }
            return sum;
        } catch (JSchException e) {
            LogConfig.log("scp to catched jsch exception, " + e, LogConfig.ERROR);
        } catch (IOException e) {
            LogConfig.log("scp to catched exception, " + e, LogConfig.ERROR);
        } catch (Exception e) {
            LogConfig.log("scp to error, " + e, LogConfig.ERROR);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception e) {
                    LogConfig.log("File input stream close error, " + e, LogConfig.ERROR);
                }
            }
        }
        return -1;
    }


    public long scpFrom(String source, String destination) {
        FileOutputStream fileOutputStream = null;
        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("scp -f " + source);
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] buf = new byte[1024];
            //send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            while (true) {
                if (checkAck(in) != 'C') {
                    break;
                }
            }
            //read '644 '
            in.read(buf, 0, 4);
            long fileSize = 0;
            while (true) {
                if (in.read(buf, 0, 1) < 0) {
                    break;
                }
                if (buf[0] == ' ') {
                    break;
                }
                fileSize = fileSize * 10L + (long) (buf[0] - '0');
            }
            String file = null;
            for (int i = 0; ; i++) {
                in.read(buf, i, 1);
                if (buf[i] == (byte) 0x0a) {
                    file = new String(buf, 0, i);
                    break;
                }
            }
            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            // read a content of lfile
            if (Files.isDirectory(Paths.get(destination))) {
                fileOutputStream = new FileOutputStream(destination + File.separator + file);
            } else {
                fileOutputStream = new FileOutputStream(destination);
            }
            long sum = 0;
            while (true) {
                int len = in.read(buf, 0, buf.length);
                if (len <= 0) {
                    break;
                }
                sum += len;
                if (len >= fileSize) {
                    fileOutputStream.write(buf, 0, (int) fileSize);
                    break;
                }
                fileOutputStream.write(buf, 0, len);
                fileSize -= len;
            }
            return sum;
        } catch (JSchException e) {
            LogConfig.log("scp to catched jsch exception, " + e, LogConfig.ERROR);
        } catch (IOException e) {
            LogConfig.log("scp to catched io exception, " + e, LogConfig.ERROR);
        } catch (Exception e) {
            LogConfig.log("scp to error, " + e, LogConfig.ERROR);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    LogConfig.log("File output stream close error, " + e, LogConfig.ERROR);
                }
            }
        }
        return -1;
    }


    public boolean remoteEdit(String source, Function<String, String> process) {
        InputStream in = null;
        OutputStream out = null;
        try {
            String fileName = source;
            int index = source.lastIndexOf('/');
            if (index >= 0) {
                fileName = source.substring(index + 1);
            }
            //backup source
//            execCommand(String.format("cp %s %s", source, source + ".bak." + System.currentTimeMillis()));
            //scp from remote
            String tmpSource = System.getProperty("java.io.tmpdir") + session.getHost() + "-" + fileName;
            scpFrom(source, tmpSource);
            in = new FileInputStream(tmpSource);
            //edit file according function process
            String tmpDestination = tmpSource + ".des";
            out = new FileOutputStream(tmpDestination);
            String inputLines = new String();

            String s = process.apply(inputLines);

            out.write(s.getBytes());
            out.flush();
            //scp to remote
            scpTo(tmpDestination, session, source);
            return true;
        } catch (Exception e) {
            LogConfig.log("remote edit error, " + e, LogConfig.ERROR);
            return false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    LogConfig.log("input stream close error" + e, LogConfig.ERROR);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    LogConfig.log("output stream close error" + e, LogConfig.ERROR);
                }
            }
        }
    }


    /**
     * 断开连接
     */
    public void disConnect() {
        if (openChannel != null && !openChannel.isClosed()) {
            openChannel.disconnect();
        }

        session.disconnect();
    }

    private int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) return b;
        if (b == -1) return b;
        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) { // error
                LogConfig.log(sb.toString(), LogConfig.INFO);
            }
            if (b == 2) { // fatal error
                LogConfig.log(sb.toString(), LogConfig.INFO);
            }
        }
        return b;
    }


    public static void main(String args[]) {

        SSHHelper sshHelper = new SSHHelper("172.21.18.202", "root", "suiroot", 22);

        sshHelper.getConnection();

        /*String s = "---\n" +
                "title: 二分查找优化\n" +
                "---\n" +
                "\n" +
                "二分查找在顺序查找时时间复杂度为O(logn)，是一种高效的查找算法\n" +
                "\n" +
                "## 传统二分查找\n" +
                "\n" +
                "```java\n" +
                "public class BinarySearch {\n" +
                "    public static void main(String[] args) {\n" +
                "        int[] arr = new int[]{1, 2, 3, 4, 5};\n" +
                "        int i = binarySearch(arr, 0);\n" +
                "        System.out.println(\"i = \" + i);\n" +
                "    }\n" +
                "\n" +
                "    public static int binarySearch(int[] arr, int target) {\n" +
                "        int left = 0;\n" +
                "        int right = arr.length - 1;\n" +
                "\n" +
                "        while (left < right) {\n" +
                "            int mid = (left + right) / 2;\n" +
                "\n" +
                "            if (arr[mid] > target) right = mid - 1;\n" +
                "            else if (arr[mid] < target) left = mid + 1;\n" +
                "            else return mid;\n" +
                "        }\n" +
                "\n" +
                "        return -1;\n" +
                "    }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "## 存在的问题\n" +
                "\n" +
                "二分查找的流程\n" +
                "\n" +
                "+ 找到数组中点的位置\n" +
                "+ 判断待查找的元素在中点值的左面还是右面\n" +
                "  + 在中点值的左面时就让右边界等于中点\n" +
                "  + 在中点值的右面时就让左边界等于中点 + 1\n" +
                "+ 重复此过程，直到待查找的值等于终点值时退出，否则返回-1\n" +
                "\n" +
                "这里可以看到，由于查找时是从中点进行比较的，如果数组中的元素为1-1000时，待查找的值为10，此时再从中点开始查找时，二分查找的性能就远不如顺序查找来的快。\n" +
                "\n" +
                "解决办法就是每次不是从中点进行查找，而是根据key来确定key右面的区间，从这个右区间进行查找。因此，就有一个确定这个区间右节点的公式\n" +
                "\n" +
                "$$mid = left + \\frac{key - arr[left]}{arr[right] - key} \\times (right - left)$$\n" +
                "\n" +
                "此种查找方式又称之为插值查找，但是此种方式适用于查找的序列非常大，而且数据分布又比较均匀的情况。\n" +
                "\n" +
                "## 优化后的代码\n" +
                "\n" +
                "```java\n" +
                "public class BinarySearchOptimize {\n" +
                "    public static void main(String[] args) {\n" +
                "        int[] arr = new int[]{1, 2, 3, 4, 5};\n" +
                "        int i = binarySearch(arr, 2);\n" +
                "        System.out.println(\"i = \" + i);\n" +
                "    }\n" +
                "\n" +
                "    public static int binarySearch(int[] arr, int key) {\n" +
                "        int left = 0;\n" +
                "        int right = arr.length - 1;\n" +
                "        while(left < right) {\n" +
                "            int mid = left + (key - arr[left]) / (arr[right] - key) * (right - left);\n" +
                "\n" +
                "            if(arr[mid] > key) {\n" +
                "                right = mid;\n" +
                "            } else if(arr[mid] < key) {\n" +
                "                left = mid + 1;\n" +
                "            } else {\n" +
                "                return mid;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        return -1;\n" +
                "    }\n" +
                "}\n" +
                "```";

        sshHelper.remoteEdit("/opt/blog/source/_posts/二分查找.md", (inputLines) -> {
            String outputLines = new String(s);
            return outputLines;
        });*/

        String s = sshHelper.execCommand("cd /opt/blog && hexo g", false);

        System.out.println("s = " + s);
//        sshHelper.getConnection();


        String s1 = sshHelper.execCommand("cd /opt/blog && hexo d", true);

        System.out.println(s1);


    }


}
