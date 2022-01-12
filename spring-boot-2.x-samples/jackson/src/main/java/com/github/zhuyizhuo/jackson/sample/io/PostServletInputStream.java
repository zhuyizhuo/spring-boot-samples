package com.github.zhuyizhuo.jackson.sample.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zhuyizhuo.jackson.sample.constants.BaseConstant;
import com.github.zhuyizhuo.jackson.sample.util.HashIdUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * 处理 post 请求参数流
 * @author zhuo
 */
public class PostServletInputStream  extends ServletInputStream {
    private InputStream inputStream;
    /** 解析json之后的文本 */
    private String body ;

    public PostServletInputStream(String body) throws IOException {
        this.body=body;
        inputStream = null;
    }

    private InputStream acquireInputStream() throws IOException {
        if(inputStream == null) {
            if (body != null && body.contains(BaseConstant.ENCODE_KEY)){
                ObjectMapper mapper = new ObjectMapper();
                HashMap hashMap = mapper.readValue(body, HashMap.class);
                Object o = hashMap.get(BaseConstant.ENCODE_KEY);
                if (o != null){
                    try{
                        String decode = HashIdUtils.decode(o.toString());
                        hashMap.put(BaseConstant.DECODE_KEY, decode);
                    } catch (Exception e){
                        //ignore this exception
                    }
                }
                body = mapper.writeValueAsString(hashMap);
            }
            //通过解析之后传入的文本生成inputStream以便后面control调用
            inputStream = new ByteArrayInputStream(body.getBytes());
        }
        return inputStream;
    }

    @Override
    public void close() throws IOException {
        try {
            if(inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            throw e;
        } finally {
            inputStream = null;
        }
    }

    @Override
    public int read() throws IOException {
        return acquireInputStream().read();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public synchronized void mark(int i) {
        throw new UnsupportedOperationException("mark not supported");
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new IOException(new UnsupportedOperationException("reset not supported"));
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener listener) {
        throw new UnsupportedOperationException();
    }
}
