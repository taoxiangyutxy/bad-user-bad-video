package com.ttt.one.common.utils;

import sun.net.util.IPAddressUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class IpUtils {
    /**
     * 正则表达式: 匹配IPV4地址字符串
     */
    private final static String IPV4 = "^((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}$";
    private final static String IP_MASK = "^((128|192)|2(24|4[08]|5[245]))(\\.(0|(128|192)|2((24)|(4[08])|(5[245])))){3}$";

    /**
     * 判断字符串是否为IPV4地址格式
     *
     * @param ip 要验证的ip字符串
     * @return 是 true/ 否 false
     */
    public static boolean isIp(String ip) {
        if (ip == null) {
            return false;
        }
        Pattern patt = Pattern.compile(IPV4);
        return patt.matcher(ip).matches();
    }

    /**
     * 判断字符串是否为IPV4 子网掩码格式
     *
     * @param mask 要验证的子网掩码字符串
     * @return 是 true/ 否 false
     */
    public static boolean isIpMask(String mask) {
        if (mask == null) {
            return false;
        }
        Pattern patt = Pattern.compile(IP_MASK);
        return patt.matcher(mask).matches();
    }

    /**
     * 获取IP地址或掩码二进制数组
     *
     * @param ip IP或子网掩码
     * @return 二进制数组如[11111111, 11111111, 11111111, 11111111]
     */
    public static String[] getIpBinary(String ip) {
        String[] strs = ip.split("\\.");
        for (int i = 0; i < 4; i++) {
            strs[i] = Integer.toBinaryString(Integer.parseInt(strs[i]));
            if (strs[i].length() < 8) {
                StringBuilder zero = new StringBuilder();
                for (int j = 0; j < 8 - strs[i].length(); j++) {
                    zero.append("0");
                }
                strs[i] = zero.toString() + strs[i];
            }
        }
        return strs;
    }

    /**
     * 将二进制字符串数组转换为byte数组,长度由第一个值的长度决定
     *
     * @param binaryStrArr 二进制数组
     * @return 如[1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0]
     * @throws ArrayIndexOutOfBoundsException 如果数组二进制字符串长度不同,将会抛出异常
     */
    public static byte[] toBinary(String[] binaryStrArr) {
        int bl = binaryStrArr[0].length();
        byte[] bytes = new byte[bl * binaryStrArr.length];
        for (int i = 0; i < binaryStrArr.length; i++) {
            for (int j = 0; j < bl; j++) {
                bytes[i * bl + j] = (byte) (binaryStrArr[i].charAt(j) == '1' ? 1 : 0);
            }
        }
        return bytes;
    }

    /**
     * 对二进制数组增加指定值
     * <p>如果增加的值超出此数组长度二进制最大表示值, 数组将重置为0, 从0开始增加</p>
     *
     * @param binaryArray 二进制数组值应当全为1或0
     * @param plus        增加的数值10进制
     */
    public static void binaryArrayPlus(byte[] binaryArray, int plus) {
        binaryArrayPlus(binaryArray, binaryArray.length - 1, plus);
    }

    /**
     * 对二进制数组增加指定值
     * <p>如果增加的值超出此数组长度二进制最大表示值, 数组将重置为0, 从0开始增加</p>
     *
     * @param binaryArray 二进制数组值应当全为1或0
     * @param index       下标
     * @param plus        增加的数值10进制
     */
    private static void binaryArrayPlus(byte[] binaryArray, int index, int plus) {
        if (index < 0) {
            binaryArray[0] = 0;
            return;
        }
        binaryArray[index] = (byte) (binaryArray[index] + 1);
        plus--;
        //如果进位,则递归进位
        if (binaryArray[index] > 1) {
            binaryArrayPlus(binaryArray, index - 1, 1);
            binaryArray[index] = 0;
        }
        //如果增加的数超过1
        if (plus > 0) {
            binaryArrayPlus(binaryArray, index, plus);
        }
    }

    /**
     * 获取局域网内的所有IP, 包含参数地址, 包含首尾地址
     *
     * @param ip   用作查找基础的IP,返回此IP网段的地址列表
     * @param mask 子网掩码
     * @return IP list 或 null, 如果地址非法则返回null
     */
    public static List<String> getLocalAreaIpList(String ip, String mask) {
        return getLocalAreaIpList(ip, mask, false);
    }

    /**
     * 获取局域网内的所有IP, 包含首尾地址
     *
     * @param ip             用作查找基础的IP,返回此IP网段的地址列表
     * @param mask           子网掩码
     * @param containParamIp 返回结果是否包含传入参数的IP
     * @return IP list 或 null, 如果地址非法则返回null
     */
    public static List<String> getLocalAreaIpList(String ip, String mask, boolean containParamIp) {
        return getLocalAreaIpList(ip, mask, containParamIp, false);
    }

    /**
     * 获取局域网内的所有IP
     *
     * @param ip                   用作查找基础的IP,返回此IP网段的地址列表
     * @param mask                 子网掩码
     * @param containParamIp       返回结果是否包含传入参数的IP
     * @param ignoreFirstAndLastIp 是否忽略首尾IP,(网段地址与广播地址)
     * @return IP list 或 null, 如果地址非法则返回null
     */
    public static List<String> getLocalAreaIpList(String ip, String mask, boolean containParamIp, boolean ignoreFirstAndLastIp) {
        if (!isIp(ip) || !isIpMask(mask)) {
            return null;//非法ip或子网掩码
        }
        String[] maskBinary = getIpBinary(mask);//子网掩码二进制数组
        //[11000000, 10101000, 00000000, 11111110]
        String[] ipBinary = getIpBinary(ip);//IP地址二进制数组
        //取同网段部分
        byte[] maskArr = toBinary(maskBinary);//二进制掩码数组
        byte[] ipArr = toBinary(ipBinary);//二进制IP数组
        int maskLen = 0;//子网掩码长度
        for (int i = 0; i < maskArr.length; i++) {
            maskLen += maskArr[i];
        }
//        int maskNumberLen = maskLen % 8;//子网位数,若为0 则8位全为主机号
//        System.out.println("子网号位数:" + maskNumberLen);
        int hostNumberLen = 32 - maskLen;//主机IP位数
//        System.out.println("主机号位数:" + hostNumberLen);
        int maxHost = 1 << hostNumberLen;
//        System.out.println("支持主机个数:" + maxHost);
        byte[] mod = new byte[32];//同网段二进制数组
        for (int i = 0; i < 32; i++) {
            mod[i] = (byte) (maskArr[i] & ipArr[i]);
        }
        List<String> ipList = new ArrayList<>(maxHost);
        StringBuilder genIp = new StringBuilder();//生成的IP
        for (int i = 0; i < maxHost; i++) {
            //转换为IP地址
            int decimal = 0;
            for (int j = 0; j < 32; j++) {
                decimal += mod[j] << (7 - j % 8);
                if (j != 0 && (j + 1) % 8 == 0) {
                    if (genIp.length() == 0) {
                        genIp.append(decimal);
                    } else {
                        genIp.append(".").append(decimal);
                    }
                    decimal = 0;
                }
            }
            binaryArrayPlus(mod, 1);//从0开始增加maxHost次
//            System.out.println(genIp);//生成的IP
            String generateIp = genIp.toString();
            genIp.delete(0, genIp.length());//清空
            if (ignoreFirstAndLastIp && (i == 0 || i == maxHost - 1)) {
                continue;//跳过首位地址
            }
            if (containParamIp && generateIp.equals(ip)) {
                continue;//跳过相同地址
            }
            ipList.add(generateIp);
        }
        return ipList;
    }

    /**
     * IP地址转换为一个long整数
     *
     * @param ip XXX.XXX.XXX.XXX
     * @return long
     */
    public static long ipToNumeric(String ip) {
        String[] ips = ip.split("\\.");
        long ipNum = 0;
        for (int i = 0; i < ips.length; i++) {
            ipNum += (Long.valueOf(ips[i]) << 8 * (3 - i));
        }
        return ipNum;
    }

    /**
     * 将十进制整数形式转换成127.0.0.1形式的ip地址
     *
     * @param longIp
     * @return
     */
    public static String longToIP(long longIp) {
        StringBuffer sb = new StringBuffer("");
        // 直接右移24位
        sb.append(String.valueOf((longIp >>> 24)));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        // 将高24位置0
        sb.append(String.valueOf((longIp & 0x000000FF)));
        return sb.toString();
    }

    /**
     * 获取IP总数
     *
     * @param mask XX
     * @return
     */
    public static int getIpCount(String mask) {
        return BigDecimal.valueOf(Math.pow(2, 32 - Integer.parseInt(mask))).setScale(0, BigDecimal.ROUND_DOWN).intValue();//IP总数，去小数点
    }

    /**
     * 获取掩码
     *
     * @param maskLength 网络ID位数  XX
     * @return XXX.XXX.XXX.XXX
     */
    public static String getMask(int maskLength) {
        int binaryMask = 0xFFFFFFFF << (32 - maskLength);
        StringBuffer sb = new StringBuffer();
        for (int shift = 24; shift > 0; shift -= 8) {
            sb.append(Integer.toString((binaryMask >>> shift) & 0xFF));
            sb.append(".");
        }
        sb.append(Integer.toString(binaryMask & 0xFF));
        return sb.toString();
    }

    /**
     * 检查ip是否冲突
     * true 唯一（默认） 、false 不唯一
     *
     * @return
     */
    public static Boolean compareIP(Long ip1, Long ip2, Long ip3, Long ip4) {
        for (int i = 0; i < 4; i++) {
            if ((ip1 <= ip3) && (ip2 >= ip3 && ip2 < ip4)) {
                return false;
            } else if ((ip1 >= ip3) && (ip1 <= ip4 && ip2 > ip4)) {
                return false;
            } else if (ip1 <= ip3 && ip2 >= ip4) {
                return false;
            } else if (ip1 >= ip3 && ip2 <= ip4) {
                return false;
            }
        }
        return true;
    }

    /**
     * 16进制编码转为十进制
     *
     * @param str 0x...
     * @return long
     */
    public static long toTen(String str) {
        String code = str.substring(2, str.length());
        BigInteger bigint = new BigInteger(code, 16);
        return bigint.longValue();
    }

    /**
     * 获取ipPool的所有IP
     *
     * @param ipPool x.x.x.x/x
     * @return IP list 或 null, 如果地址非法则返回null
     */
    public static List<String> getIpList(String ipPool) {
        List<String> ipList = new ArrayList<>();
        //1.判断是ip段还是单个ip
        String[] ip = ipPool.split("\\/");
        String[] ipNum = ip[0].split("\\.");
        if (!isSingleIp(ipPool)) {
            //ip段
            //获取掩码
            String mask = IpUtils.getMask(Integer.valueOf(ip[1]));
            //获取ipList
            ipList = IpUtils.getLocalAreaIpList(ip[0], mask, false, false);
        } else {
            //单个ip
            ipList.add(ip[0]);
        }

        return ipList;
    }
    /**
     * 判断是否是单个Ip
     * @param ipPool x.x.x.x/x
     * @return  ture 是，false 不是
     */
    public static Boolean isSingleIp(String ipPool) {
        Boolean result=false;
        //1.判断是ip段还是单个ip
        String[] ip = ipPool.split("\\/");
        int mask = Integer.parseInt(ip[1]);
        if(mask == 32){
            return true;
        }
        String[] ipBinary = getIpBinary(ip[0]);//IP地址二进制数组
        byte[] ipArr = toBinary(ipBinary);//二进制IP数组
        //当主机位全是0时，为网段，其余全是单个IP
        for(int i=mask;i<ipArr.length;i++){
            if(ipArr[i] == 1){
                result =true;
                break;
            }
        }
        return result;
    }

    /**
     * 判断某IP是否在某一个IP段内
     *
     * @param ip   IP  x.x.x.x
     * @param cidr IP段  x.x.x.x/x
     * @return true/false
     */
    public static boolean isInRange(String ip, String cidr) {
        String[] ips = ip.split("\\.");
        int ipAddr = (Integer.parseInt(ips[0]) << 24)
                | (Integer.parseInt(ips[1]) << 16)
                | (Integer.parseInt(ips[2]) << 8) | Integer.parseInt(ips[3]);
        int type = Integer.parseInt(cidr.replaceAll(".*/", ""));
        int mask = 0xFFFFFFFF << (32 - type);
        String cidrIp = cidr.replaceAll("/.*", "");
        String[] cidrIps = cidrIp.split("\\.");
        int cidrIpAddr = (Integer.parseInt(cidrIps[0]) << 24)
                | (Integer.parseInt(cidrIps[1]) << 16)
                | (Integer.parseInt(cidrIps[2]) << 8)
                | Integer.parseInt(cidrIps[3]);

        return (ipAddr & mask) == (cidrIpAddr & mask);
    }

    /**
     * 根据 ip/掩码位 计算IP段的起始IP 如 IP串 218.240.38.69/30
     *
     * @param ip      给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 起始IP的字符串表示
     */
    public static String getBeginIpStr(String ip, String maskBit) {
        return longToIP(getBeginIpLong(ip, maskBit));
    }

    /**
     * 根据 ip/掩码位 计算IP段的起始IP 如 IP串 218.240.38.69/30
     *
     * @param ip      给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 起始IP的Long表示
     */
    public static Long getBeginIpLong(String ip, String maskBit) {
        return ipToNumeric(ip) & ipToNumeric(getMask(Integer.parseInt(maskBit)));
    }

    /**
     * 根据 ip/掩码位 计算IP段的终止IP 如 IP串 218.240.38.69/30
     *
     * @param ip      给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 终止IP的字符串表示
     */
    public static String getEndIpStr(String ip, String maskBit) {
        return longToIP(getEndIpLong(ip, maskBit));
    }

    /**
     * 根据 ip/掩码位 计算IP段的终止IP 如 IP串 218.240.38.69/30
     *
     * @param ip      给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 终止IP的Long表示
     */
    public static Long getEndIpLong(String ip, String maskBit) {
        return getBeginIpLong(ip, maskBit) + getIpCount(maskBit) - 1L;
    }

    /**
     * 一个大的IP池  根据掩码位平均分割成多个IP池   如：  10.0.0.0/8   --->  10.0.0.0/30 , 10.0.0.4/30 , ...
     *
     * @param ipPool  给定的IP池，如10.0.0.0/8
     * @param maskBit 给定的掩码位，如30
     * @return IP池  List  10.0.0.0/30 , 10.0.0.4/30 , ...
     */
    public static List<String> ipPoolToMultiIpPool(String ipPool, String maskBit) {

        List<String> ipPoolList = new ArrayList<>();
        String[] split = ipPool.split("\\/");
        Long beginIpLong = getBeginIpLong(split[0], split[1]);
        int total = getIpCount(split[1]);
        int ipCount = getIpCount(maskBit);
        for (int i = 0; i < total / ipCount; i++) {
            ipPoolList.add(longToIP(beginIpLong + i * ipCount) + "/" + maskBit);
        }

        return ipPoolList;
    }

    /**
     * 判断IP段是否冲突, 冲突为false, 不冲突为true
     * @param ipPool1 ip段1
     * @param ipPool2 ip段2
     * @return 冲突为false, 不冲突为true
     */
    public static boolean checkIpPoolConflict(String ipPool1, String ipPool2){
        List<String> ipList1 = IpUtils.getIpList(ipPool1);
        List<String> ipList2 = IpUtils.getIpList(ipPool2);
        String a1 = ipList1.get(0);
        String a2 = ipList1.get(ipList1.size() - 1);
        String b1 = ipList2.get(0);
        String b2 = ipList2.get(ipList2.size() - 1);
        long a1num = IpUtils.ipToNumeric(a1);
        long a2num = IpUtils.ipToNumeric(a2);
        long b1num = IpUtils.ipToNumeric(b1);
        long b2num = IpUtils.ipToNumeric(b2);
        if (a1num >= b1num && a1num <= b2num){
            return false;
        }
        if (a1num < b1num && a2num >= b2num){
            return false;
        }
        return true;
    }

    /**
     * 判断IP地址是否为内网IP,
     * @param ip  例如 ： 192.168.3.211  ,  202.3.2.1
     * @return  是否为内网IP, true/false
     */
    public static boolean internalIp(String ip) {
        byte[] addr = IPAddressUtil.textToNumericFormatV4(ip);
        return internalIp(addr);
    }

    public static boolean internalIp(byte[] addr) {
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        //10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        //172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        //192.168.x.x/16
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
     * 根据 request 请求获取用户的真实 Ip
     * @param httpRequest request 请求
     * @return  ip地址
     */
    public static String getIp(HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0
                || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = httpRequest.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0
                || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = httpRequest.getHeader("WL-Proxy-Client-IP");
        }
        if (null == ipAddress || 0 == ipAddress.length() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = httpRequest.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0
                || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = httpRequest.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress)
                    || ipAddress.equals("0:0:0:0:0:0:0:1")) {
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
        if (ipAddress != null && ipAddress.length() > 15) {
            // = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }
    public static void main(String[] args) {
        System.out.println(ipToNumeric("60.43.233.23"));
        System.out.println(longToIP(ipToNumeric("60.43.233.23")));
        System.out.println(ipToNumeric("0.0.1.1"));
        System.out.println(longToIP(ipToNumeric("0.0.1.1")));
        System.out.println(ipToNumeric("0.0.0.255"));
        System.out.println(longToIP(ipToNumeric("0.0.0.255")));
//        System.out.println(compareIP(33686016L,33686031L,33686018L,33686018L));
//        System.out.println(isSingleIp("2.2.0.255/31"));
//        List<String> ipList = getIpList("2.2.128.1/32");
//        ipList.forEach(s -> System.out.println(s));
//        System.out.println(ipList.size());
//        System.out.println(ipList.get(0));
//        System.out.println(getBeginIpLong("2.2.128.0","17"));
//        System.out.println(getEndIpLong("2.2.128.0","17"));
//        System.out.println(ipToNumeric("2.2.128.0"));
//        System.out.println(compareIP(1L,1L,3L,5L));
//        List<String> ipList = getIpList("2.2.2.252/30");
//
//        System.out.println(isInRange("10.10.10.1", "10.10.0.0/20"));
//
//        System.out.println(getIpCount("30"));
//
//        System.out.println(getBeginIpStr("192.13.2.0", "30"));
//
//        System.out.println(getEndIpStr("192.13.2.0", "30"));
//
//        ipPoolToMultiIpPool("10.0.0.0/28", "30").forEach(s -> System.out.println(s));
//
//        System.out.println(internalIp("192.169.180.50"));
//
//        System.out.println(getBeginIpStr("218.240.38.69", "30"));

//        System.out.println(getBeginIpStr("22.22.0.0","23"));
//        System.out.println(getEndIpStr("22.22.0.0","23"));
//
//



    }

}