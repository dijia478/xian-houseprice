package cn.dijia478.xianhouseprice.task;

import cn.dijia478.xianhouseprice.service.PriceService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * 获取房价定时任务
 *
 * @author dijia478
 * @date 2020-8-27 15:41:47
 */
@Component
@Slf4j
public class PriceTask {

    /** 面积[0,50)，价格集合 */
    private static final List<String> AREA_0_50 = new ArrayList<>();

    /** 面积[50,60)，价格集合 */
    private static final List<String> AREA_50_60 = new ArrayList<>();

    /** 面积[60,70)，价格集合 */
    private static final List<String> AREA_60_70 = new ArrayList<>();

    /** 面积[70,80)，价格集合 */
    private static final List<String> AREA_70_80 = new ArrayList<>();

    /** 面积[80,90)，价格集合 */
    private static final List<String> AREA_80_90 = new ArrayList<>();

    /** 面积[90,100)，价格集合 */
    private static final List<String> AREA_90_100 = new ArrayList<>();

    /** 面积[100,110)，价格集合 */
    private static final List<String> AREA_100_110 = new ArrayList<>();

    /** 面积[110,120)，价格集合 */
    private static final List<String> AREA_110_120 = new ArrayList<>();

    /** 面积[120,130)，价格集合 */
    private static final List<String> AREA_120_130 = new ArrayList<>();

    /** 面积[130,140)，价格集合 */
    private static final List<String> AREA_130_140 = new ArrayList<>();

    /** 面积[140,150)，价格集合 */
    private static final List<String> AREA_140_150 = new ArrayList<>();

    /** 面积[150,999)，价格集合 */
    private static final List<String> AREA_150_999 = new ArrayList<>();

    /** 所有价格集合的集合 */
    private static final Map<String, List<String>> AREA_MAP = new LinkedHashMap<>();

    /** 毛坯房 */
    private static final String ROUGHCAST = "毛坯";

    /** 精装房 */
    private static final String DECORATION = "精装";

    static {
        AREA_MAP.put("[0,50)", AREA_0_50);
        AREA_MAP.put("[50,60)", AREA_50_60);
        AREA_MAP.put("[60,70)", AREA_60_70);
        AREA_MAP.put("[70,80)", AREA_70_80);
        AREA_MAP.put("[80,90)", AREA_80_90);
        AREA_MAP.put("[90,100)", AREA_90_100);
        AREA_MAP.put("[100,110)", AREA_100_110);
        AREA_MAP.put("[110,120)", AREA_110_120);
        AREA_MAP.put("[120,130)", AREA_120_130);
        AREA_MAP.put("[130,140)", AREA_130_140);
        AREA_MAP.put("[140,150)", AREA_140_150);
        AREA_MAP.put("[150,999)", AREA_150_999);
    }

    @Autowired
    private PriceService priceService;

    /** 结果输出目录 */
    @Value("${output-dir}")
    private String dir;

    /**
     * 开启多线程的定时任务。程序启动后，一周执行一次。
     * 其实本程序的定时任务并不需要多线程，
     * 但是为了顺便演示下多线程的定时任务如何创建，在这里就创建成多线程的定时任务。
     */
    @Async("taskExecutor")
    @Scheduled(cron = "${task-cron}")
//    @Scheduled(fixedDelay = 120000)
    public void getNewPrice() {
        try {
            log.info("开始新一次的统计，时间：{}", DateUtil.now());
            String ipPort = "http://117.39.29.75:8085/";
            String url = "/pricePublic/house/public/index";

            // 获取所有楼盘的链接
            Set<String> urlSet = getAllHouseUrl(ipPort, url);

            // 获取所有毛坯房面积区间集合，每个集合中存储的是单价
            getAllPriceList(ipPort, urlSet, ROUGHCAST);

            // 将毛坯房结果写文件
            writeResult(ROUGHCAST);

            // 清空list中的值
            clearCache();

            // 获取所有精装房面积区间集合，每个集合中存储的是单价
            getAllPriceList(ipPort, urlSet, DECORATION);

            // 将精装房结果写文件
            writeResult(DECORATION);
        } catch (Exception e) {
            log.error("本次统计失败", e);
        } finally {
            // 清空list中的值
            clearCache();
            log.info("本次统计结束，时间：{}", DateUtil.now());
        }

    }

    /**
     * 清空list中的值
     */
    private void clearCache() {
        for (List<String> value : AREA_MAP.values()) {
            value.clear();
        }
    }

    /**
     * 将结果写文件
     *
     * @param type
     */
    private void writeResult(String type) {
        // 创建文件
        String fileUrl = createFile(type);

        try (ExcelReader reader = ExcelUtil.getReader(fileUrl); ExcelWriter writer = ExcelUtil.getWriter(fileUrl)) {
            List<Map<String, Object>> read = reader.readAll();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("日期", DateUtil.today());
            log.info("{}房：", type);
            for (Map.Entry<String, List<String>> entry : AREA_MAP.entrySet()) {
                double averPrice = entry.getValue().stream().mapToDouble(Double::parseDouble).average().orElse(0D);
                String averPriceStr = NumberUtil.roundStr(averPrice, 2);
                log.info("{}房面积区间在 {} 中的有 {} 套，均价是 {}", type, entry.getKey(), entry.getValue().size(), averPriceStr);
                result.put(entry.getKey(), averPriceStr);
            }
            read.add(result);
            writer.write(read, true);
        }
    }

    /**
     * 如果没有，则创建文件
     * 对配置文件中的dir文件名做了修改，返回要创建的文件目录
     *
     * @param type
     */
    private String createFile(String type) {
        String substring = dir.substring(0, dir.length() - 6);
        String fileUrl = substring + "(" + type + ")" + ".xlsx";
        File file = FileUtil.file(fileUrl);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            ExcelWriter excelWriter = new ExcelWriter(fileUrl);
            excelWriter.writeRow(new ArrayList<>());
            excelWriter.flush();
            excelWriter.close();
        }
        return fileUrl;
    }

    /**
     * 获取所有面积区间的价格集合，每个集合中存储的是单价
     *
     * @param ipPort
     * @param urlSet
     * @param type
     */
    private void getAllPriceList(String ipPort, Set<String> urlSet, String type) {

        for (String houseUrl : urlSet) {
            int page = 1;
            while (true) {
                String[] split = houseUrl.split("\\?id=");
                String result = priceService.getHousePrice(ipPort, split[0], split[1], page++);
                Document parse = Jsoup.parse(result);
                Elements tbody = parse.getElementsByTag("tbody");
                // 获取房屋类型，精装或毛坯
                Element elementType = tbody.get(0);
                Elements trType = elementType.getElementsByTag("tr");
                String houseType = trType.get(0).getElementsByTag("td").get(3).text();
                if (!type.equals(houseType)) {
                    break;
                }

                // 获取房屋面积，价格
                Element element = tbody.get(1);
                Elements tr = element.getElementsByTag("tr");
                if (tr.size() == 0) {
                    break;
                }

                for (Element value : tr) {
                    Elements td = value.getElementsByTag("td");
                    String area = td.get(2).text();
                    String price = td.get(3).text();
                    addPriceToList(area, price);
                }

                try {
                    // 防止把政府网站给调崩了
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 添加价格到不同的面积区间集合中
     *
     * @param area
     * @param price
     */
    private void addPriceToList(String area, String price) {
        double doubleArea = Double.parseDouble(area);
        if (0 <= doubleArea && doubleArea < 50) {
            AREA_0_50.add(price);
        } else if (50 <= doubleArea && doubleArea < 60) {
            AREA_50_60.add(price);
        } else if (60 <= doubleArea && doubleArea < 70) {
            AREA_60_70.add(price);
        } else if (70 <= doubleArea && doubleArea < 80) {
            AREA_70_80.add(price);
        } else if (80 <= doubleArea && doubleArea < 90) {
            AREA_80_90.add(price);
        } else if (90 <= doubleArea && doubleArea < 100) {
            AREA_90_100.add(price);
        } else if (100 <= doubleArea && doubleArea < 110) {
            AREA_100_110.add(price);
        } else if (110 <= doubleArea && doubleArea < 120) {
            AREA_110_120.add(price);
        } else if (120 <= doubleArea && doubleArea < 130) {
            AREA_120_130.add(price);
        } else if (130 <= doubleArea && doubleArea < 140) {
            AREA_130_140.add(price);
        } else if (140 <= doubleArea && doubleArea < 150) {
            AREA_140_150.add(price);
        } else if (150 <= doubleArea && doubleArea < 999) {
            AREA_150_999.add(price);
        }
    }

    /**
     * 获取所有楼盘的url
     *
     * @param ipPort
     * @param url
     */
    private Set<String> getAllHouseUrl(String ipPort, String url) {
        Set<String> urlSet = new LinkedHashSet<>();
        int page = 1;
        while (true) {
            String result = priceService.getHouseInfo(ipPort, url, page++);

            Document parse = Jsoup.parse(result);
            Elements listTable = parse.getElementsByClass("listTable");
            Elements select = listTable.select("a[href]");

            if (select.size() == 0) {
                break;
            }
            for (Element element : select) {
                String houseUrl = element.attributes().get("href");
                urlSet.add(houseUrl);
            }

            try {
                // 防止把政府网站给调崩了
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return urlSet;
    }

}
