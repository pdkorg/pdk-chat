package com.pdk.chat.action;

import com.pdk.chat.util.CommonUtil;
import com.pdk.chat.util.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hubo on 2015/9/2
 */
@Controller
@RequestMapping("/img")
public class ImageUploadAction {

    private static Logger log = LoggerFactory.getLogger(ImageUploadAction.class);

    public static final String baseUploadImagePath = "/chat/img";

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> upload(@RequestParam(value = "imageFile", required = false) MultipartFile file, String userId) {

        Map<String, Object> result = new HashMap<>();

        String path = CommonUtil.getRealPath(baseUploadImagePath) + "/" + userId;

        String origFileName = file.getOriginalFilename();
        String ext = origFileName.substring(origFileName.lastIndexOf("."), origFileName.length());
        String fileName = UUIDGenerator.generateUUID() + ext;

        File targetFile = new File(path, fileName);

        if(!targetFile.exists()) {
            targetFile.mkdirs();
        }

        //保存
        try {
            file.transferTo(targetFile);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        result.put("imgURI", baseUploadImagePath + "/" + userId + "/" + targetFile.getName());

        return result;
    }
}
