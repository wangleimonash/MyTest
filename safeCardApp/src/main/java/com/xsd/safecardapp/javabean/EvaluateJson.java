package com.xsd.safecardapp.javabean;

public class EvaluateJson {
	



    /**
     * code : 0
     * result : {"teacherid":1,"techaer":"张老师","userid":1,"username":"小明","num":5,"rmark":"今天课堂表现不错"}
     */

    private int code;
    /**
     * teacherid : 1
     * techaer : 张老师
     * userid : 1
     * username : 小明
     * num : 5
     * rmark : 今天课堂表现不错
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
        private int teacherid;
        private String techaer;
        private int userid;
        private String username;
        private int num;
        private String rmark;

        public int getTeacherid() {
            return teacherid;
        }

        public void setTeacherid(int teacherid) {
            this.teacherid = teacherid;
        }

        public String getTechaer() {
            return techaer;
        }

        public void setTechaer(String techaer) {
            this.techaer = techaer;
        }

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
            this.userid = userid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getRmark() {
            return rmark;
        }

        public void setRmark(String rmark) {
            this.rmark = rmark;
        }
    }

}
