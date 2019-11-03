# replitedemo
爬虫（通过链接深度爬取）
爬虫心得：
爬取所需目标，需要根据特定网站网页结构进行设置相对应的规则，用于过滤

hostStartStr 域名  
rule 链接前缀  
EndStr，EndStr2链接后缀   
count 指定爬取数量
以及根据网页结构获取对应的dom
Element title = document.select(".bookname").first();
Element content = document.getElementById("content");

