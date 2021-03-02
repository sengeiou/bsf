package com.weking.core.sensitive;

import com.wekingframework.core.LibSysUtils;

import java.util.*;


/**
 * 思路：	创建一个FilterSet，枚举了0~65535的所有char是否是某个敏感词开头的状态
 * <p>
 * 判断是否是 敏感词开头
 * |			|
 * 是			不是
 * 获取头节点			OK--下一个字
 * 然后逐级遍历，DFA算法
 *
 * @author pangjs ~ 2015-4-29 下午06:30:29
 */
public class WordFilter {

    private static FilterSet set;
    private static Map<Integer, WordNode> nodes;


    public static void init(List<String> list) {
        set = new FilterSet();
        nodes = new HashMap<Integer, WordNode>(1024, 1);
        Set<String> words = new HashSet<String>(1200);

        for (String word: list) {
            words.add(word);
        }
        //获取敏感词
        addSensitiveWord(words);
    }

    private static void addSensitiveWord(final Set<String> words) {
        char[] chs;
        int fchar;
        int lastIndex;
        WordNode fnode;
        for (String curr : words) {
            chs = curr.toCharArray();
            fchar = chs[0];
            if (!set.contains(fchar)) {//没有首字定义
                set.add(fchar);//首字标志位	可重复add,反正判断了，不重复了
                fnode = new WordNode(fchar, chs.length == 1);
                nodes.put(fchar, fnode);
            } else {
                fnode = nodes.get(fchar);
                if (!fnode.isLast() && chs.length == 1)
                    fnode.setLast(true);
            }
            lastIndex = chs.length - 1;
            for (int i = 1; i < chs.length; i++) {
                fnode = fnode.addIfNoExist(chs[i], i == lastIndex);
            }
        }
    }

    private static final char SIGN = '*';

    public static final String doFilterWithSpace(String src) {
        if (LibSysUtils.isNullOrEmpty(src)) {
            return "";
        }
        src = src.replace(" ","");
        return doFilter(src);
    }

    public static final String doFilter(final String src) {
        if (LibSysUtils.isNullOrEmpty(src)) {
            return "";
        }
        char[] chs = src.toCharArray();
        int length = chs.length;
        int currc;
        int k;
        WordNode node;
        for (int i = 0; i < length; i++) {
            currc = chs[i];
            if (!set.contains(currc)) {
                continue;
            }
//			k=i;//日	2
            node = nodes.get(currc);//日	2
            if (node == null)//其实不会发生，习惯性写上了
                continue;
            boolean couldMark = false;
            int markNum = -1;
            if (node.isLast()) {//单字匹配（日）
                couldMark = true;
                markNum = 0;
            }
            //继续匹配（日你/日你妹），以长的优先
            // 你-3	妹-4		夫-5
            k = i;
            for (; ++k < length; ) {

                node = node.querySub(chs[k]);
                if (node == null)//没有了
                    break;
                if (node.isLast()) {
                    couldMark = true;
                    markNum = k - i;//3-2
                }
            }
            if (couldMark) {
                for (k = 0; k <= markNum; k++) {
                    chs[k + i] = SIGN;
                }
                i = i + markNum;
            }
        }

        return new String(chs);
    }

    public static final boolean doFilter(String src, int ii) {
        if (LibSysUtils.isNullOrEmpty(src)) {
            return false;
        }
        boolean change = false;
        char[] chs = src.toCharArray();
        int length = chs.length;
        int currc;
        int k;
        WordNode node;
        for (int i = 0; i < length; i++) {
            currc = chs[i];
            if (!set.contains(currc)) {
                continue;
            }
//			k=i;//日	2
            node = nodes.get(currc);//日	2
            if (node == null)//其实不会发生，习惯性写上了
                continue;
            boolean couldMark = false;
            int markNum = -1;
            if (node.isLast()) {//单字匹配（日）
                couldMark = true;
                markNum = 0;
            }
            //继续匹配（日你/日你妹），以长的优先
            // 你-3	妹-4		夫-5
            k = i;
            for (; ++k < length; ) {

                node = node.querySub(chs[k]);
                if (node == null)//没有了
                    break;
                if (node.isLast()) {
                    couldMark = true;
                    markNum = k - i;//3-2
                }
            }
            if (couldMark) {
                for (k = 0; k <= markNum; k++) {
                    chs[k + i] = SIGN;
                }
                i = i + markNum;
                change = true;
            }
        }

        src =  new String(chs);
        return change;
    }

    public static void main(String[] args) {
            boolean b = true;
            boolean b2 = false;
//        System.out.println(b|b2);
//        System.out.println(b&b2);

        String s = "干死renquan";
        System.out.println("解析字数 : " + s.length());
        boolean re;
        long nano = System.nanoTime();
        re = WordFilter.doFilter(s, 11);
        nano = (System.nanoTime() - nano);
        System.out.println("解析时间 : " + nano + "ns");
        System.out.println("解析时间 : " + nano / 1000000 + "ms");
        System.out.println(re);
        System.out.println(s);
    }

}
