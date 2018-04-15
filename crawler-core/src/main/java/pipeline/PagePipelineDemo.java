package pipeline;


import com.alibaba.fastjson.JSONObject;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import com.alibaba.fastjson.JSON;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.*;

/**
 * @Author: Wei.Jun
 * @Date: 2018/4/14 20:52
 */
public class PagePipelineDemo implements Pipeline {


    public void process(ResultItems resultItems, Task task) {

        Map<String, Object> resultsMap = resultItems.getAll();
        String jsonString = JSON.toJSONString(resultsMap);
        if(!("{}".equals(jsonString))){
            toMongoDB(jsonString);
        }

        Iterator<Entry<String, Object>> iter = resultsMap.entrySet().iterator();
        Map.Entry<String, Object> entry;
        // 输出到控制台
        while (iter.hasNext()) {
            entry = iter.next();
            System.out.println(entry.getKey() + "：" + entry.getValue());
        }
    }

    private static void toMongoDB(String jsonString){
        ServerAddress serverAddress = new ServerAddress("localhost:27017");
        MongoClient mongoClient = new MongoClient(serverAddress);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("tutor");
        MongoCollection<DBObject> collection = mongoDatabase.getCollection("test", DBObject.class);
        DBObject bson = (DBObject)com.mongodb.util.JSON.parse(jsonString);

        collection.insertOne(bson);
    }

}
