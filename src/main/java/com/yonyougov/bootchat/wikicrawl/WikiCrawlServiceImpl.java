package com.yonyougov.bootchat.wikicrawl;

import com.yonyougov.bootchat.util.SaveWikeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WikiCrawlServiceImpl implements WikiCrawlService {
    @Value("${yondif.file.path}")
    String filePath;

    @Override
    public void saveFile(String cookie) throws Exception {
        SaveWikeUtil saveWikeUtil = new SaveWikeUtil();
        saveWikeUtil.saveWiki(cookie, filePath);
    }
}
