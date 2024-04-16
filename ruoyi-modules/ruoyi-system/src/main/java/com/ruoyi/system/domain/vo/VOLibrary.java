package com.ruoyi.system.domain.vo;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * @author Chen Zhenyang
 * @Data 2024/4/5
 * @apiNote
 */
public class VOLibrary {

    @Autowired
    ISysConfigService configService;

    private JSONObject body;

    public VOLibrary() throws IOException {
        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        String name = "/com/ruoyi/system/domain/vo/Library.txt";
        path =path + name;
        System.out.println(path);
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String content = "";
        String line = reader.readLine();
        while (line != null) {
            content += line;
            line = reader.readLine();
        }
        reader.close();
//        String content = configService.selectConfigByKey("sys.form.auth");
        // 转成对象
        JSONObject w =JSONObject.parseObject(content);
        this.body = w;
    }

    public JSONObject getBody() {
        return body;
    }

    public void setBody(JSONObject body) {
        this.body = body;
    }

}
