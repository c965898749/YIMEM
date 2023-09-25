package com.sy.tool;



import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 获取IP方法
 *
 * @author ruoyi
 */
@Slf4j

public class IpUtils {


    private static final String UNKOWN_ADDRESS = "未知位置";

    /**
     * 将整个xdb文件加载到内存中(11M左右),此种创建方式支持多线程,因此只需要加载一次
     */
    private final static Searcher SEARCHER;

    static {
        try {
            ClassPathResource resource = new ClassPathResource("ip2region.xdb");
            //获取真实文件路径
            String path = resource.getURL().getPath();
            byte[] cBuff = Searcher.loadContentFromFile(path);
            SEARCHER = Searcher.newWithBuffer(cBuff);
            log.info("加载了ip2region.xdb文件,Searcher初始化完成!");
        } catch (Exception e) {
            log.error("初始化ip2region.xdb文件失败,报错信息:[{}]", e.getMessage(), e);
            throw new RuntimeException("系统异常!");
        }
    }


    /**
     * 解析ip地址
     *
     * @param ipStr 字符串类型ip 例:192.168.0.1
     * @return 返回结果形式(国家|区域|省份|城市|ISP) 例 [中国, 0, 河北省, 衡水市, 电信]
     */
    public static List<String> parse( String ipStr) {
        return parse(ipStr, null);
    }

    /**
     * 自定义解析ip地址
     *
     * @param ipStr ip 字符串类型ip 例:1970753539(经过转换后的)
     * @param index 想要获取的区间 例如:只想获取 省,市 index = [2,3]
     * @return 返回结果例 [北京,北京市]
     */
    public static List<String> parse( String ipStr, int[] index) {
        try {
            long ip = Searcher.checkIP(ipStr);
            return parse(ip, index);
        } catch (Exception e) {
            log.error("ip解析为long错误,ipStr:[{}],错误信息:[{}]", ipStr, e.getMessage(), e);
            throw new RuntimeException("系统异常!");
        }
    }

    /**
     * 自定义解析ip地址
     *
     * @param ip    ip Long类型ip
     * @param index 想要获取的区间 例如:只想获取 省,市 index = [2,3]
     * @return 返回结果例 [河北省, 衡水市]
     */
    public static List<String> parse(Long ip, int[] index) {
        //获取xdb文件资源
        List<String> regionList = new ArrayList<>();
        try {
            String region = SEARCHER.search(ip);
            String[] split = region.split("\\|");
            if (index == null) {
                regionList = Arrays.asList(split);
            } else {
                for (int i : index) {
                    regionList.add(split[i]);
                }
            }
            //关闭资源
            SEARCHER.close();
        } catch (Exception e) {
            log.error("根据ip解析地址失败,ip:[{}],index[{}],报错信息:[{}]", ip, index, e.getMessage(), e);
            throw new RuntimeException("系统异常!");
        }
        return regionList;
    }


    public static void main(String[] args) {
        //只获取省,市
        int[] index = {0,1,2, 3,4};
        List<String> parse = parse("101.67.29.171", index);
        System.out.println(parse);
    }




    //----------------------------------------------------------------------------------------------\



    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;


    }

    public static boolean internalIp(String ip) {
        byte[] addr = textToNumericFormatV4(ip);
        return internalIp(addr) || "127.0.0.1".equals(ip);
    }

    private static boolean internalIp(byte[] addr) {
        if (StringUtils.isNull(addr) || addr.length < 2) {
            return true;
        }
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        // 10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        // 172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        // 192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0) {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    return true;
                }
            case SECTION_5:
                switch (b1) {
                    case SECTION_6:
                        return true;
                }
            default:
                return false;
        }
    }

    /**
     * 将IPv4地址转换成字节
     *
     * @param text IPv4地址
     * @return byte 字节
     */
    public static byte[] textToNumericFormatV4(String text) {
        if (text.length() == 0) {
            return null;
        }

        byte[] bytes = new byte[4];
        String[] elements = text.split("\\.", -1);
        try {
            long l;
            int i;
            switch (elements.length) {
                case 1:
                    l = Long.parseLong(elements[0]);
                    if ((l < 0L) || (l > 4294967295L)) {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l >> 24 & 0xFF);
                    bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 2:
                    l = Integer.parseInt(elements[0]);
                    if ((l < 0L) || (l > 255L)) {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l & 0xFF);
                    l = Integer.parseInt(elements[1]);
                    if ((l < 0L) || (l > 16777215L)) {
                        return null;
                    }
                    bytes[1] = (byte) (int) (l >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 3:
                    for (i = 0; i < 2; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    l = Integer.parseInt(elements[2]);
                    if ((l < 0L) || (l > 65535L)) {
                        return null;
                    }
                    bytes[2] = (byte) (int) (l >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 4:
                    for (i = 0; i < 4; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    break;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return bytes;
    }

    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
        }
        return "127.0.0.1";
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        }
        return "未知";
    }
}