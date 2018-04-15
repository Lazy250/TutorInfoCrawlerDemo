package processor;


import pipeline.PagePipelineDemo;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;


/**
 * @Author: Wei.Jun
 * @Date: 2018/4/13 22:24
 */

public class PageProcessorDemo implements PageProcessor{

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    public void process(Page page) {

            //列表页
        if(page.getUrl().regex("http://hxhgxy.gxu.edu.cn/szdw/jsql.htm").match()){
            page.addTargetRequests(page.getHtml().xpath("/html/body/div[2]/table/tbody/tr[2]/td[2]/*/tbody//a/@href").all());
        }else {
            String name = page.getHtml().xpath("//*[@id='mainbox']/div/table/tbody/tr[1]/td[2]/text()").toString();
            String tutorType = page.getHtml().xpath("//*[@id=\"mainbox\"]/div/table/tbody/tr[10]/td[2]/text()").toString();

            if(name == null || tutorType == null){
                page.setSkip(true);
            }else {
                tutorType = tutorType.replace(" ", "");
                name = name.replace(" ", "");
            }

            if(!("".equals(name)) && !("".equals(tutorType))){

                page.putField("name", page.getHtml().xpath("//*[@id='mainbox']/div/table/tbody/tr[1]/td[2]/text()").toString());
                page.putField("url", page.getUrl().toString());
                page.putField("title", page.getHtml().xpath("//*[@id='mainbox']/div/table/tbody/tr[3]/td[4]/text()").toString());
                String tutorTypeString = page.getHtml().xpath("//*[@id=\"mainbox\"]/div/table/tbody/tr[10]/td[2]/text()").toString();
                ArrayList<String> tutorTypeList = stringToList(tutorTypeString);

                page.putField("tutor_type", tutorTypeList);
                if (!(tutorTypeString.contains("导师") && tutorTypeString.length() < 50)){
                    //设置skip之后，这个页面的结果不会被Pipeline处理
                    page.setSkip(true);
                }
                page.putField("fields", page.getHtml().xpath("//*[@id=\"mainbox\"]/div/table/tbody/tr[11]/td[2]/table/tbody/tr/td[3]/text()").toString());
                page.putField("img_url", page.getHtml().xpath("//*[@id=\"preview\"]/@src").toString());
                page.putField("school","Guangxi University");
                page.putField("dpt","school of chemistry & chemical engineering in Guangxi University");

            }
        }
    }

    public Site getSite() {
        return site;
    }

    private ArrayList<String> stringToList(String str){
        ArrayList<String> list = new ArrayList<String>();
        //去空格，拆分
        str = str.replace(" ", "");
        String[] strings = str.split(",");

        for (String s : strings){
            list.add(s);
        }

        return list;
    }



    public static void main(String[] args) {

        Spider.create(new PageProcessorDemo())
                //从"http://hxhgxy.gxu.edu.cn/szdw/jsql.htm"开始抓
                .addUrl("http://hxhgxy.gxu.edu.cn/szdw/jsql.htm")
//                .addUrl("http://210.36.22.97/tcms/teachInfo!queryTeachInfoById.action?gh=FCFB2334118B11E69C550050569949B5")
                .addPipeline(new PagePipelineDemo())
//                .addPipeline(new JsonFilePipeline("D:\\webmagic"))
//                .addPipeline(new ConsolePipeline())
                //开启5个线程抓取
                .thread(5)
                //启动爬虫
                .run();
    }
}
