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

public class Repliteone3 {

    private static String hostStartStr ="";
    private static String EndStr =".htm";
    private static String EndStr2 =".html";
    private static int count = 1000;
   // private static int i=1;

    public static void main(String[] args) throws IOException {
        List<TargetInfo> targetInfoList = new ArrayList<>();
        List<String> watchedUrlList = new ArrayList<>();
        LinkedList<String> unUsedUrlList = new LinkedList<>();
        String url = "";
        unUsedUrlList.add(url);
        int num =0;

        while(num < unUsedUrlList.size()){
            //System.out.println("unUsedUrlList:"+unUsedUrlList.size());
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
        //watchedUrlList.stream().forEach( e-> System.out.println(e));
        System.out.println("==================================================================================================");
        System.out.println("任务汇报：");
        System.out.println("unUsedUrlList的条数为："+unUsedUrlList.size());
        System.out.println("watchedUrlList的条数为："+watchedUrlList.size());
        System.out.println("TargetInfo的条数为："+targetInfoList.size());

        if (targetInfoList.size() < count){
            System.out.println("已爬取："+targetInfoList.size()+"条数据，未达到指定数量数据");
        }else {
            System.out.println("已爬取："+targetInfoList.size()+"条数据，任务已完成");
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
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            document = Jsoup.parse(result.toString());
            if (document != null){
                Element title = document.select(".bttitle").first();
                Element anthor = document.select(".laiy").first();
                Element content = document.select(".zhengwen").first();
                if (title != null && anthor != null && content != null){
                    targetInfo.setTitle(title.text());
                    targetInfo.setAnthor(anthor.text());
                    targetInfo.setContent(content.text());
                }
             //   System.out.println(targetInfo.toString());
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
                        if (addrUrl.startsWith("../") && (addrUrl.endsWith(EndStr) || addrUrl.endsWith(EndStr2))){
                            addrUrl = subStr(addrUrl);
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
    public static String subStr(String str){
        if (str.startsWith("../")){
            str = str.substring(3);
            str = subStr(str);
        }
        return str;
    }
}
