package com.xsd.safecardapp.javabean;

public class SwitchJson {

    /**
     * code : 0
     * result : {"st":1}
     */

    private int code;
    /**
     * st : 1
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
        private int st;

        public int getSt() {
            return st;
        }

        public void setSt(int st) {
            this.st = st;
        }
    }
}
