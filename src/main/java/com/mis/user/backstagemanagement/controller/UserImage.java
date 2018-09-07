package com.mis.user.backstagemanagement.controller;

import com.alibaba.fastjson.JSON;
import com.mis.user.commom.canstants.Canstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class UserImage {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    Map map = new HashMap<String,String>();
    @Value("${file.path}")
    private String filePath;
    @Value("${default.path}")
    private String defaultPath;

    /**
     * 头像上传
     *
     * @param file
     * @param session
     *
     * */
    @RequestMapping(value = "/userImg",method = POST)
    public Object userImg(@RequestParam(value="file", required=false)MultipartFile file, HttpSession session) throws Exception {
        map.clear();
        try{
            if(session.getAttribute("userId")!= null) {
                String extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                File file1 = new File(filePath + File.separator + session.getAttribute("user"));
                //判断文件夹是否存在，不存在的话增加后缀，添加自己的文件夹，防止无限添加
                if(!file1.exists()){
                    //创建文件夹，用于存放个人数据
                    file1.mkdirs();
                }
                filePath = filePath + session.getAttribute("userId")+File.separator;
                String fileName = session.getAttribute("userId")+extName;
                //判断是否已有头像，若有，进行删除，防止找不到相应的头像
                File file2 = new File(filePath+fileName);
                if(file2.exists()){
                    file2.delete();
                }
                //若无，直接进行储存
                else {
                    map.clear();
                    FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(new File(filePath+ fileName)));
                    map.put("userImg",Canstants.SUCCESS);
                }
            }
            else{
                map.clear();
                map.put("userImg",Canstants.INFO_NULL);//2，上传失败
            }
            filePath = filePath.replace((CharSequence) session.getAttribute("user"),"");
        }catch ( Exception e){
            logger.error(e.getClass()+"{}",e);
            map.clear();
            map.put("userImg",Canstants.FAIL);

        }
        return JSON.toJSON(map);
    }


    /**
     * 当前登录人的头像
     *
     * @param session
     * */
    @RequestMapping(value = "/userPhoto",method = POST)
    public Object userPhoto(HttpSession session) {
        String path = "SSOUser"+ File.separator + session.getAttribute("userId") + File.separator + session.getAttribute("user") +".jpg";
        try {
            if (session.getAttribute("userId") != null) {
                File file = new File(filePath + session.getAttribute("userId") + File.separator + session.getAttribute("user") + ".jpg");
                if (!file.exists()) {
                    map.clear();
                    map.put("userPhoto", defaultPath);//默认头像,该用户目前无头像
                } else {
                    map.clear();
                    map.put("userPhoto", path);
                }
            } else {
                map.clear();
                map.put("userPhoto", defaultPath);
            }

        } catch (Exception e) {
            logger.error(e.getClass()+"{}",e);
            map.clear();
            map.put("userImg",defaultPath);
        }
        return JSON.toJSON(map);
    }



    /**
     * 查询用户的头像
     *
     * @param userId
     * */
    @RequestMapping(value = "/userP",method = POST)
    public Object userP(String userId){
        String path = "SSOUser"+ File.separator + userId + File.separator + userId +".jpg";
        try {
            if (userId != null){
                File file = new File( filePath + userId + File.separator + userId + ".jpg");
                if(!file.exists()){
                    map.clear();
                    map.put("userP",defaultPath);//默认头像,该用户目前无头像
                }
                else{
                    map.clear();
                    map.put("userP",path);
                }
            } else {
                map.clear();
                map.put("userP", defaultPath);
            }
        } catch (Exception e) {
            logger.error(e.getClass()+"{}",e);
            map.clear();
            map.put("userImg",defaultPath);
        }
        return JSON.toJSON(map);
    }
}
