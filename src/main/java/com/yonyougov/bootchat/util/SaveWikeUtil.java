package com.yonyougov.bootchat.util;

import com.yonyougov.bootchat.util.vo.PageVo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.ArrayList;

@Slf4j
public class SaveWikeUtil {

    String url = "https://zw-wiki.yyrd.com/plugins/pagetree/naturalchildren.action?decorator=none&excerpt=false&sort=position&reverse=false&disableLinks=false&expandCurrent=true&placement=sidebar&hasRoot=true&pageId=21631934&treeId=0&startDepth=0&mobile=false&ancestors=21631934&treePageId=21631934&_=1718088009917";

    public void saveWike(String cookie, String filePath) throws Exception {
        File rootFile = new File(filePath);
        SimpleHttpClient client = new SimpleHttpClient();
        String content = client.doGet(url, cookie);
        Document doc = Jsoup.parse(content, "UTF-8");
        Elements spanEle = doc.getElementsByClass("plugin_pagetree_children_span");
        PageVo currentPage = new PageVo("21631934", "首页", url, new ArrayList<>());
        downloadPage(currentPage, cookie, rootFile);
        for (Element element : spanEle) {
            Elements aEle = element.getElementsByTag("a");
            String href = aEle.attr("href");
            String hrefText = aEle.text();
            System.out.println(href + " " + hrefText);
            String pageId = href.split("=")[1];
            String subUrl = "https://zw-wiki.yyrd.com/plugins/pagetree/naturalchildren.action?hasRoot=true&pageId=" + pageId;
            PageVo subPage = new PageVo(pageId, hrefText, subUrl, new ArrayList<>());
            currentPage.getPageVoList().add(subPage);


            downloadPage(subPage, cookie, rootFile);
            crawlSubPages(subPage, cookie, rootFile);
        }
        StringBuilder sb = new StringBuilder();
        printPage(1, currentPage, sb);
        System.out.println(sb);
    }

    public static void crawlSubPages(PageVo currentPage, String cookie, File rootFile) throws Exception {
        SimpleHttpClient client = new SimpleHttpClient();
        String content = client.doGet(currentPage.getUrl(), cookie);
        Document doc = Jsoup.parse(content, "UTF-8");
        Elements spanEle = doc.getElementsByClass("plugin_pagetree_children_span");
        for (Element element : spanEle) {
            Elements aEle = element.getElementsByTag("a");
            String href = aEle.attr("href");
            String hrefText = aEle.text();
            System.out.println(href + " " + hrefText);
            if (!href.contains("=")) {
                continue;
            }
            String pageId = href.split("=")[1];
            String subUrl = "https://zw-wiki.yyrd.com/plugins/pagetree/naturalchildren.action?hasRoot=true&pageId=" + pageId;
            PageVo subPage = new PageVo(pageId, hrefText, subUrl, new ArrayList<>());
            currentPage.getPageVoList().add(subPage);

            downloadPage(subPage, cookie, rootFile);
            crawlSubPages(subPage, cookie, rootFile);
        }
    }

    public static StringBuilder printPage(int level, PageVo page, StringBuilder sb) {
        sb.append("|").append("__".repeat(Math.max(0, level))).append(page.getName()).append("\n");
        for (PageVo subPage : page.getPageVoList()) {
            printPage(level + 1, subPage, sb);
        }
        return sb;
    }

    public static void downloadPage(PageVo page, String cookie, File rootFile) {

        String subPdfUrl = "https://zw-wiki.yyrd.com/exportword?pageId=" + page.getId();
        try {
            SimpleHttpClient.download(subPdfUrl, new File(rootFile, page.getName() + ".doc"), cookie);
        } catch (Exception ex) {
            log.error("download sub page error", ex);
        }
    }
}
