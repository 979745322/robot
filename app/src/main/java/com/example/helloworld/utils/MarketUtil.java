package com.example.helloworld.utils;

import java.util.Random;

public class MarketUtil {
    private static Random random = new Random();
    // 介绍
    private static final String[] RESULT_JIESHAO = {"扇子有风，拿在手中，有人来借，等到立冬",
            "孝道园的扇子正面是百家姓，看了学文化，背面是一百位皇帝，用了添福气",
            "你买了扇子我会记住你的，下次你来我会和你打招呼的呦，不信你买一把试试嘛"};
    // 价格
    private static final String[] RESULT_JIAGE = {"我们孝道园的扇子物美价廉，只要三十",
            "改革开放这么多年，谁家也不差三十块钱，三十块钱你不会吃亏，也不会上当",
            "我们机器人最老实了，卖东西，不骗人的，只要三十块",
            "买不了吃亏，买不了上当，只要三十块，通通三十块"};
    // 质量
    private static final String[] RESULT_ZHILIANG = {"我们孝道园的扇子质量特别好",
            "我们孝道园的扇子质量那肯定是没的说啊，您就放心吧",
            "我们孝道园的扇子里面有孝心，只要小心用就不会坏",
            "我们孝道园的扇子质量就是顶呱呱，三十块就可以带回家，还可以做纪念"};
    // 功能
    private static final String[] RESULT_GONGNENG = {"我们的扇子能让您度过一个凉爽的夏天",
            "我们孝道园的扇子上面有百家姓和中国历代的一百位皇帝，不仅能给您带来凉爽，还能学到知识呢",
            "孝道园的扇子正面是百家姓，看了学文化，背面是一百皇帝，用了添福气"};
    // 交钱/付钱
    private static final String[] RESULT_FUQIAN = {"我只管吆喝，收钱找我旁边的美女姐姐",
            "我暂时没有收钱的本领哦，要找我旁边的美女姐姐才可以呢"};
    // 吆喝
    private static final String[] RESULT_YAOHE = {"东汉蔡伦造纸张，要问这纸啊有啥用，听我来慢慢的说端详，南京用它包绸缎，北京用它来包文章，此纸落在我的手，我便用它来做扇子，要说此扇有多好，它可是我们孝道园的文化扇，正是中华百家姓，反是一百位皇帝",
            "碰到是运气，买到是福气",
            "该出手时就出手，机会不是天天有",
            "要买要带，赶紧赶快；好机会不要错过，机会不是天天有，该出手时就出手",
            "我们这里没有最华丽的金银珠宝，却有藏有孝心的孝道园文化扇",
            "来来来，南来的，北往的，走过路过不要错过",
            "我们的扇子可好用了",
            "物美价廉的扇子啊",
            "瞧一瞧看一看呐",
            "机不可失，时不再来"};
    // 无关
    private static final String[] RESULT_WUGUAN = {"我现在很忙呢，等会儿再找您聊天吧",
            "我正在工作呢，您买几把扇子我再找您聊天吧，不然我要被骂了",
            "现在可是工作时间呢，我要努力卖扇子，您要买几把吗？很好用的。",
            "你们都不买扇子，老板就不给我充电，明天我就不能陪你们玩了"};
    // 未识别
    private static final String[] RESULT_UNDEFI = {"请您说普通话，我没有听清您说什么",
            "我听不懂，您说普通话，我才能陪您聊天"};


    public static String containsStr(String content) {
        String returnStr = null;
        if (content.contains("扇子")) {
            if (content.contains("介绍")) {
                returnStr = RESULT_JIESHAO[randomIndex(RESULT_JIESHAO)];
            } else if (content.contains("价钱") || content.contains("多少钱") || content.contains("多钱") || content.contains("价格")) {
                returnStr = RESULT_JIAGE[randomIndex(RESULT_JIAGE)];
            } else if (content.contains("交钱") || content.contains("付钱") || content.contains("收钱") || content.contains("买")) {
                returnStr = RESULT_FUQIAN[randomIndex(RESULT_FUQIAN)];
            } else if (content.contains("功能") || content.contains("作用") || content.contains("有什么用")) {
                returnStr = RESULT_GONGNENG[randomIndex(RESULT_GONGNENG)];
            } else if (content.contains("质量") || content.contains("怎么样")) {
                returnStr = RESULT_ZHILIANG[randomIndex(RESULT_ZHILIANG)];
            } else if (content.contains("无关")) {
                returnStr = RESULT_WUGUAN[randomIndex(RESULT_WUGUAN)];
            } else if (content.contains("吆喝")) {
                returnStr = RESULT_YAOHE[randomIndex(RESULT_YAOHE)];
            } else { // 只说一个扇子，介绍
                returnStr = RESULT_JIESHAO[randomIndex(RESULT_JIESHAO)];
            }
        }
        return returnStr;
    }

    public static int randomIndex(String[] arrStr) {
        return random.nextInt(arrStr.length);
    }
}
