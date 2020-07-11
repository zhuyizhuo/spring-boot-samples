package com.github.zhuyizhuo.jackson.sample.util;

import com.github.zhuyizhuo.jackson.sample.constants.BaseConstant;

/**
 * 仅供演示原理使用,并非真实编解码
 * ID 编解码工具
 * @author zhuo
 */
public class HashIdUtils {

    public static String encode(Long id){
        return BaseConstant.PREFIX + id;
    }

    public static String decode(String hashId){
        if (!hashId.contains(BaseConstant.PREFIX)){
            return hashId;
        }
        return hashId.replace(BaseConstant.PREFIX, "");
    }

    public static Long decode2Long(String hashId){
        if (!hashId.contains(BaseConstant.PREFIX)){
            try {
                return Long.valueOf(hashId);
            } catch (Exception e){
                return null;
            }
        }
        return Long.valueOf(hashId.replace(BaseConstant.PREFIX, ""));
    }
}
