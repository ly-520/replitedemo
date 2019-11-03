package com.replite.replitedemo.Example;/**
 * @author ljt
 * @ProjectName replitedemo
 * @Description:
 * @Version:
 * @date 2019/11/2 15:47
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Repliteone4 {
    /**
     *  小说爬虫：
     *  hostStartStr 域名  rule 链接前缀  EndStr，EndStr2链接后缀   count 指定爬取数量（如果未知，尽量选择大于总章节数的数字）
     */
    
    private static String hostStartStr ="https://www.xxx.cm";
    private static String rule ="/8/8332";
    private static String EndStr =".htm";
    private static String EndStr2 =".html";
    private static int count = 100000;
   // private static int i=1;

    public static void main(String[] args) throws IOException {
        List<TargetInfo> targetInfoList = new ArrayList<>();
        List<String> watchedUrlList = new ArrayList<>();
        LinkedList<String> unUsedUrlList = new LinkedList<>();
        String url = "https://www.xxx.cm/8/8332/5857180.html";
        unUsedUrlList.add(url);
        int num =0;

        while(num < unUsedUrlList.size()){
            try {
                doRepliteNextPage(unUsedUrlList.get(num),targetInfoList,watchedUrlList,unUsedUrlList);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (num >=count){
                break;
            }
            num++;
        }

        System.out.println("==================================================================================================");
        System.out.println("任务汇报：");
        System.out.println("unUsedUrlList的条数为：" + unUsedUrlList.size());
        System.out.println("watchedUrlList的条数为：" + watchedUrlList.size());
        System.out.println("TargetInfo的条数为：" + targetInfoList.size());
        if (targetInfoList.size() < count){
            System.out.println("已爬取：" + targetInfoList.size() + "条数据，未达到指定数量数据");
        }else {
            System.out.println("已爬取：" + targetInfoList.size() + "条数据，任务已完成");
        }

    }

    public static void doRepliteNextPage(String currentUrl,List<TargetInfo> targetInfoList,List<String> watchedUrlList,List<String> unUsedUrlList) throws IOException {
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        TargetInfo targetInfo = new TargetInfo();
        Document document = null;
        URL url = new URL(currentUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(2000);
        if (connection.getResponseCode() == 200) {
            //在此设定和页面设置的字符编码保持一致，防止乱码
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"gbk"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            document = Jsoup.parse(result.toString());
            if (document != null){
                Element title = document.select(".bookname").first();
                Element content = document.getElementById("content");
                if (title != null && content != null){
                    String webstr = title.text();
                    String[] strList = webstr.split(" ");
                    String titleStr = strList[0]+" "+strList[1];
                    System.out.println(titleStr);
                    targetInfo.setTitle(titleStr);
                    targetInfo.setContent(content.text());
                }
                //遍历当前页面链接，存放到list中
                Elements elements = document.select("a[href]");
                if (elements != null){
                    for(Element element: elements) {
                        String addrUrl = element.attr("href");
                        if (addrUrl.startsWith(hostStartStr)){
                            if ( (addrUrl.endsWith(EndStr) || addrUrl.endsWith(EndStr2))
                                    && !watchedUrlList.contains(addrUrl) && !unUsedUrlList.contains(addrUrl)){
                                unUsedUrlList.add(addrUrl);
                            }
                        }
                        if (addrUrl.startsWith(rule) && ((addrUrl.endsWith(EndStr) || addrUrl.endsWith(EndStr2)))){
                            addrUrl = hostStartStr + addrUrl;
                            if ( !watchedUrlList.contains(addrUrl) && !unUsedUrlList.contains(addrUrl)){
                                unUsedUrlList.add(addrUrl);
                            }
                        }
                    }
                }
            }
        }
        if (targetInfo != null){
            targetInfoList.add(targetInfo);
            watchedUrlList.add(currentUrl);
        }
    }
}
