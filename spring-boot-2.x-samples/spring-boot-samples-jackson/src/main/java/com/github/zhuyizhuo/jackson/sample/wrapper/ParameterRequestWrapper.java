package com.github.zhuyizhuo.jackson.sample.wrapper;

import com.github.zhuyizhuo.jackson.sample.constants.BaseConstant;
import com.github.zhuyizhuo.jackson.sample.io.PostServletInputStream;
import com.github.zhuyizhuo.jackson.sample.util.HashIdUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * 请求参数处理, 封装 id 的解密
 * @author zhuo
 */
public class ParameterRequestWrapper extends HttpServletRequestWrapper {

    private String bodyParams;

    private Map<String, String[]> params;

    public ParameterRequestWrapper(HttpServletRequest request) {
        super(request);
        initParameterMap(request);
        initInputStream(request);
    }

    @Override
    public String getParameter(String name) {
        String result;
        Object v = params.get(name);
        if (v == null) {
            result = null;
        } else if (v instanceof String[]) {
            String[] strArr = (String[]) v;
            if (strArr.length > 0) {
                result = strArr[0];
            } else {
                result = null;
            }
        } else if (v instanceof String) {
            result = (String) v;
        } else {
            result = v.toString();
        }
        return result;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return new Vector<>(params.keySet()).elements();
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] result;
        Object v = params.get(name);
        if (v == null) {
            result = null;
        } else if (v instanceof String[]) {
            result = (String[]) v;
        } else if (v instanceof String) {
            result = new String[]{(String) v};
        } else {
            result = new String[]{v.toString()};
        }
        return result;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException{
        return new PostServletInputStream(bodyParams);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    /**
     * 读取 输入流
     */
    private void initInputStream(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        bodyParams = stringBuilder.toString();
    }

    /**
     * 初始化参数 Map 处理 key1=value1&key2=value2 类型的传参
     */
    private void initParameterMap(HttpServletRequest req) {
        this.params = new HashMap<>(req.getParameterMap());
        String hashId = req.getParameter(BaseConstant.ENCODE_KEY);
        if (hashId != null && hashId.length() > 0){
            params.put(BaseConstant.DECODE_KEY, new String[]{HashIdUtils.decode(hashId)});
        }

        String queryString = req.getQueryString();
        if (queryString != null && queryString.trim().length() > 0) {
            String[] params = queryString.split("&");
            for (int i = 0; i < params.length; i++) {
                int splitIndex = params[i].indexOf("=");
                if (splitIndex == -1) {
                    continue;
                }
                String key = params[i].substring(0, splitIndex);
                if (!this.params.containsKey(key)) {
                    if (splitIndex < params[i].length()) {
                        String value = params[i].substring(splitIndex + 1);
                        this.params.put(key, new String[]{value});
                    }
                }
            }
        }
    }

}
