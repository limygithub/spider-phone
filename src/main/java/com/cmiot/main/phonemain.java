package main.java.com.cmiot.main;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by limengyang on 2018/11/22.
 */

public class phonemain {

    static List<String> list = new ArrayList<>();

    public static void main(String[] args) {
        phonemain.read();
        phonemain.write();
    }

    /**
     * 获取需要爬取的手机号号段和打算开始爬取手机号的起始位置
     *  如：1770001
     *  则：与jar同一路径下的phone_msg.txt 内容为：
     *  phone:177
     *  start:1
     */
    public static void read() {
        try {
            String pathname = System.getProperty("user.dir") + "\\" + "phone_msg.txt";
//            String pathname = "D:\\Desktop\\phone_msg.txt";
            File filename = new File(pathname); // 要读取以上路径的input。txt文件
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            String line = "";
            while ((line = br.readLine()) != null) {
                list.add(line.substring(6));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 爬取手机号信息的方法
     * 1、通过read方法把需要爬取的信息加载到write中（phone:手机号号段 如：177   start:开始爬取位置 如:1   爬取时候会拼接成:1770001的格式 ）
     * 2、url 为爬取的网站，可自行修改，td.tdc2 是爬取网站的 div标签 属性，参考Jsoup解析
     * 3、httpGet.setHeader 为本机通过浏览器返回url的请求头信息 ，设置了 谷歌和IE的请求头，可自行修改
     * 4、msg 为获取到的div的text，即我们需要的信息，可进行信息修改，便于后续利用
     */
    public static void write() {
        int phone = Integer.parseInt(list.get(0));
        int start = Integer.parseInt(list.get(1));

        int i = start;
        StringBuffer phonemsg = null;
        try {
            File file = new File(System.getProperty("user.dir") + "\\" + phone + "msg.txt");
//            File file = new File("D:\\Desktop\\"+num+"msg.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter fw = null;
            Boolean result = false;
            while (!result) {
                if (i % 100 == 0) {//每100条数据休眠5秒，可修改
                    System.out.println("防止断开连接，休眠！");
                    Thread.sleep(5000); //设置暂停的时间 0.01 秒
                }
                CloseableHttpClient httpClient = HttpClients.createDefault();
                String url = "";
                if (i % 2 == 0) {
                    url = "http://www.ip138.com:8080/search.asp?mobile=" + (phone * 10000 + i) + "&action=mobile";
                } else {
                    url = "http://www.ip138.com:8080/search.asp?action=mobile&mobile=" + (phone * 10000 + i);
                }
                HttpGet httpGet = new HttpGet(url); // 创建httpget实例
                if (i % 2 == 0) {//设置请求头
                    httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                    httpGet.setHeader("Accept-Encoding", "gzip, deflate");
                    httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
                    httpGet.setHeader("Connection", "keep-alive");
                    httpGet.setHeader("Cookie", "pgv_pvi=4842811392; ASPSESSIONIDQACBSCAQ=MPBAIGCBFJHPGKLGLOMFFLOH");
                    httpGet.setHeader("Host", "www.ip138.com:8080");
                    httpGet.setHeader("Upgrade-Insecure-Requests", "1");
                    httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
                } else {
                    httpGet.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                    httpGet.setHeader("Accept-Language", "zh-CN");
                    httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko"); // 设置请求头消息User-Agent
                    httpGet.setHeader("Accept-Encoding", "gzip, deflate");
                    httpGet.setHeader("Host", "www.ip138.com:8080");
                    httpGet.setHeader("DNT", "1");
                    httpGet.setHeader("Connection", "Keep-Alive");
                    httpGet.setHeader("Cookie", "ASPSESSIONIDCCTBTCTT=MKOIJPGBGOPNCBPIBEGJJIMI");
                }
                CloseableHttpResponse response = httpClient.execute(httpGet); // 执行http get请求
                int StatusCode = response.getStatusLine().getStatusCode(); //获取响应状态码
                if (StatusCode == 200) {//如果状态响应码为200，则获取html实体内容或者json文件
                    phonemsg = new StringBuffer();
                    HttpEntity entity = response.getEntity(); // 获取返回实体
                    String html = EntityUtils.toString(entity, "GBK"); // 获取网页内容
                    Document doc = Jsoup.parse(html);//Jsoup解析html
                    Elements elements = doc.select("td.tdc2");//获取html标签中的内容
                    for (Element ele : elements) {//遍历指定的标签集合
                        String msg = ele.ownText();//只获取本div的text，不包括 div下的子div的text
                        if (null == msg) {//信息为空,用 "," 留出位置
                            phonemsg.append(",");
                        } else {//信息不为空，需处理
                            if (msg.contains("北京")){//信息为地市信息
                                msg = "北京,北京";
                            }else if (msg.contains("上海")){
                                msg = "上海,上海";
                            }else if (msg.contains("天津")){
                                msg = "天津,天津";
                            }else if (msg.contains("重庆")){
                                msg = "重庆,重庆";
                            }else if (msg.contains("重庆")){
                                msg = "重庆,重庆";
                            }else if (msg.contains("移动") && !msg.contains("虚拟")){
                                msg = "中国移动";
                            }else if (msg.contains("联通") && !msg.contains("虚拟")){
                                msg = "中国联通";
                            }else if (msg.contains("电信") && !msg.contains("虚拟")){
                                msg = "中国电信";
                            }else if (msg.contains("虚拟")){
                                msg = "虚拟运营商";
                            }else if (msg.contains("未知")){
                                msg = "";
                            }else if (msg.contains(" ")){
                                msg = msg.replace(" ",",");
                            }
                        }
                        phonemsg.append("," + msg);
                    }
                    fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "GBK"));
                    fw.append(i + phonemsg.toString() + "\r\n");
                    fw.flush();
                    if (i % 10 == 0) {
                        System.out.println("写入第" + i + "条数据:" + i + phonemsg.toString());
                    }
                    i++;
                    if (i > 9999) {
                        result = true;
                        break;
                    }

                    EntityUtils.consume(response.getEntity());//成功写入数据，消耗掉实体
                    response.close(); // response关闭
                    httpClient.close();
                } else {
                    EntityUtils.consume(response.getEntity());//失败，消耗掉实体
                }
            }
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("在" + i + "条数据报错:" + i + phonemsg.toString());
        }
    }

}
