package com.xsd.safecardapp.javabean;

public class HXJson {
	


    /**
     * code : 0
     * result : {"type":1,"hxid":"teacher306"}
     */

    private int code;
    /**
     * type : 1
     * hxid : teacher306
     */

    private ResultEntity result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ResultEntity getResult() {
        return result;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public static class ResultEntity {
        private int type;
        private String hxid;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getHxid() {
            return hxid;
        }

        public void setHxid(String hxid) {
            this.hxid = hxid;
        }
    }

}
