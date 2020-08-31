package cn.dijia478.xianhouseprice.service;

/**
 * 价格服务层接口
 *
 * @author dijia478
 * @date 2020-8-31 17:15
 */
public interface PriceService {

    /**
     * 获取楼盘信息
     *
     * @param ipPort
     * @param url
     * @param page
     * @return
     */
    String getHouseInfo(String ipPort, String url, int page);

    /**
     * 获取楼盘价格
     *
     * @param ipPort
     * @param url
     * @param page
     * @return
     */
    String getHousePrice(String ipPort, String url, String id, int page);

}
