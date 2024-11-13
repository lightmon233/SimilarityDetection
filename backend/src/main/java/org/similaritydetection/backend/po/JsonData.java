package org.similaritydetection.backend.po;

import lombok.Data;

@Data
public class JsonData {
    private String message;
    private Object data;
    private Integer error=0;
    public static  JsonData ok(Object data){
        JsonData tmp=new JsonData();
        tmp.setData(data);
        return tmp;
    }
    public static  JsonData error(Integer errorNo,String message){
        JsonData tmp=new JsonData();
        tmp.setError(errorNo);
        tmp.setMessage(message);
        return tmp;
    }



}