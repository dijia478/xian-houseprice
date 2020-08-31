# xian-houseprice

一个自用娱乐向小程序，会定时(默认是一周一次)的从西安市发改委官网上，拉取最新的全市商品房公示信息。每次发送请求都会间隔100ms，防止把政府官网给调崩了。。。

最后会计算出当前全市房屋的面积区间数量和均价，并输出一份均价excel

```
2020-08-31 15:30:16.335  INFO 16448 --- [eTaskExecutor-1] c.d.xianhouseprice.task.PriceTask        : 开始新一次的统计，时间：2020-08-31 15:30:16
2020-08-31 15:30:43.946  INFO 16448 --- [eTaskExecutor-1] c.d.xianhouseprice.task.PriceTask        : 面积区间在 [0,50) 中的有 1 套，均价是 12340.00
2020-08-31 15:30:43.946  INFO 16448 --- [eTaskExecutor-1] c.d.xianhouseprice.task.PriceTask        : 面积区间在 [50,60) 中的有 0 套，均价是 0.00
2020-08-31 15:30:43.946  INFO 16448 --- [eTaskExecutor-1] c.d.xianhouseprice.task.PriceTask        : 面积区间在 [60,70) 中的有 3 套，均价是 12260.83
2020-08-31 15:30:43.946  INFO 16448 --- [eTaskExecutor-1] c.d.xianhouseprice.task.PriceTask        : 面积区间在 [70,80) 中的有 0 套，均价是 0.00
2020-08-31 15:30:43.946  INFO 16448 --- [eTaskExecutor-1] c.d.xianhouseprice.task.PriceTask        : 面积区间在 [80,90) 中的有 65 套，均价是 12656.66
2020-08-31 15:30:43.946  INFO 16448 --- [eTaskExecutor-1] c.d.xianhouseprice.task.PriceTask        : 面积区间在 [90,100) 中的有 293 套，均价是 14382.78
2020-08-31 15:30:43.947  INFO 16448 --- [eTaskExecutor-1] c.d.xianhouseprice.task.PriceTask        : 面积区间在 [100,110) 中的有 246 套，均价是 14821.22
2020-08-31 15:30:43.947  INFO 16448 --- [eTaskExecutor-1] c.d.xianhouseprice.task.PriceTask        : 面积区间在 [110,120) 中的有 598 套，均价是 14486.07
2020-08-31 15:30:43.947  INFO 16448 --- [eTaskExecutor-1] c.d.xianhouseprice.task.PriceTask        : 面积区间在 [120,130) 中的有 373 套，均价是 13392.52
2020-08-31 15:30:43.947  INFO 16448 --- [eTaskExecutor-1] c.d.xianhouseprice.task.PriceTask        : 面积区间在 [130,140) 中的有 234 套，均价是 16358.39
2020-08-31 15:30:43.947  INFO 16448 --- [eTaskExecutor-1] c.d.xianhouseprice.task.PriceTask        : 面积区间在 [140,150) 中的有 511 套，均价是 16315.75
2020-08-31 15:30:43.947  INFO 16448 --- [eTaskExecutor-1] c.d.xianhouseprice.task.PriceTask        : 面积区间在 [150,999) 中的有 290 套，均价是 16543.78
2020-08-31 15:30:44.039  INFO 16448 --- [eTaskExecutor-1] c.d.xianhouseprice.task.PriceTask        : 本次统计结束，时间：2020-08-31 15:30:44
```

| 日期       | [0,50)   | [50,60) | [60,70)  | [70,80) | [80,90)  | [90,100) | [100,110) | [110,120) | [120,130) | [130,140) | [140,150) | [150,999) |
| ---------- | -------- | ------- | -------- | ------- | -------- | -------- | --------- | --------- | --------- | --------- | --------- | --------- |
| 2020-08-31 | 12340.00 | 0.00    | 12260.83 | 0.00    | 12656.66 | 14382.78 | 14821.22  | 14486.07  | 13392.52  | 16358.39  | 16315.75  | 16543.78  |