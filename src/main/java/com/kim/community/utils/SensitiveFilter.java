package com.kim.community.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private static final String REPLACEMENT = "***";
    private TrieNode root = new TrieNode();

    // 當IOC容器實例化Bean時候, 自動調用 init() 方法
    @PostConstruct
    public void init() {
        // getClass().getClassLoader() 在編譯後的target文件夾下的classes文件夾獲取資源
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream("sensitive-word.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                this.addKeyword(keyword);
            }

        } catch (IOException e) {
            logger.error("加載敏感詞文件失敗" + e.getMessage());
        }
    }

    private void addKeyword(String keyword) {
        TrieNode tempNode = this.root;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode child = tempNode.getChild(c);
            if (child == null) {
                tempNode.addChild(c,new TrieNode());
            }
            tempNode = tempNode.getChild(c);
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        TrieNode tempNode = root;
        int begin = 0;
        int end = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while (end < text.length()) {
            char c = text.charAt(end);
            // 跳過特殊符號
            if (isSymbol(c)) {
                if (tempNode == root) {
                    stringBuilder.append(c);
                    begin++;
                }
                end++;
                continue;
            }
            tempNode = tempNode.getChild(c);
            if (tempNode == null) {
                // 以begin 開頭的字符串不是敏感詞
                stringBuilder.append(text.charAt(begin));
                // begin 和 end指針同時前進
                end = ++begin;
                tempNode = root;
            } else if (tempNode.isKeywordEnd()) {
                // 以begin為開頭 end為結尾的字符串 是敏感詞
                // 添加 替換符 到 stringBuilder 裡
                stringBuilder.append(REPLACEMENT);
                // begin 更新 end + 1後面
                // end++
                begin = ++end;
                tempNode = root;
            } else {
                end++;
            }
        }
        // 如果 end到達了末尾, 但是[begin, end]不含有 敏感詞
        // 直接添加到stringBuilder末尾
        stringBuilder.append(text.substring(begin));
        return stringBuilder.toString();
    }

    private boolean isSymbol(Character c) {
        // 0x2E80 ~ 0x9FFF 是東亞文字範圍
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    private class TrieNode {
        private boolean isKeywordEnd=false;

        private Map<Character, TrieNode> children = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addChild(Character c, TrieNode node) {
            children.put(c, node);
        }

        public TrieNode getChild(Character c) {
            return children.get(c);
        }
    }
}
