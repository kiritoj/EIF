package com.example.mifans.eif.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 歌词解析类
 */
public class ParsingLyric {
    private static List<String> wordslist;//存放歌词
    private List<Integer> timelist = new ArrayList<>();//存放时间
    private String lyric;//歌词源文件

    public ParsingLyric() {

    }
    public static String parsin(String lyric){
        String  s= lyric.replaceAll("\\[\\d{2}:\\d{2}[\\.:]\\d{2,3}?\\]","");
//        String regex1 = "\\[.+\\]";
//        System.out.println(wordslist.get(0).matches(regex1));
//        for (int i = 0; i < 5; i++) {
//
//        }
//        String regex = "\\[\\d{2}:\\d{2}[\\.:]\\d{3}?\\]";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(test);
//        while (matcher.find()){
//            wordslist.add(matcher.group());
//        }
        return s;
    }


}
