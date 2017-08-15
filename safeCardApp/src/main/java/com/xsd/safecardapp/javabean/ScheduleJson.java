package com.xsd.safecardapp.javabean;

import java.util.List;

public class ScheduleJson {


    /**
     * code : 0
     * result : [{"monday":"语文,数学,语文,体育,品德,音乐,,"},{"tuesday":"语文,数学,美术,体育,语文,书法,,"},{"wednesday":"语文,数学,体育,音乐,英语,校本,,"},{"thursday":"语文,数学,体育,班级,美术,语文,,"},{"friday":"语文,数学,语文,英语,品德,数学,,"},{"saturday":",,,,,,,"},{"sunday":",,,,,,,"}]
     */

    private String code;
    /**
     * monday : 语文,数学,语文,体育,品德,音乐,,
     */

    private List<ResultEntity> result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ResultEntity> getResult() {
        return result;
    }

    public void setResult(List<ResultEntity> result) {
        this.result = result;
    }

    public static class ResultEntity {
        private String monday;

        private String tuesday;

        private String wednesday;

        private String thursday;

        private String friday;

        public String getTuesday() {
            return tuesday;
        }

        public void setTuesday(String tuesday) {
            this.tuesday = tuesday;
        }

        public String getWednesday() {
            return wednesday;
        }

        public void setWednesday(String wednesday) {
            this.wednesday = wednesday;
        }

        public String getThursday() {
            return thursday;
        }

        public void setThursday(String thursday) {
            this.thursday = thursday;
        }

        public String getFriday() {
            return friday;
        }

        public void setFriday(String friday) {
            this.friday = friday;
        }

        public String getMonday() {
            return monday;
        }

        public void setMonday(String monday) {
            this.monday = monday;
        }
    }
}
