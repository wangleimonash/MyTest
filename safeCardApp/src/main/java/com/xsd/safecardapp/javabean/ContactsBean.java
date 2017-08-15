package com.xsd.safecardapp.javabean;

import java.util.List;

public class ContactsBean {
	



    /**
     * code : 0
     * result : [{"cname":"一（1）班","tname":"吴福建","thxid":"teacher290","group":[]}]
     */

    private String code;
    /**
     * cname : 一（1）班
     * tname : 吴福建
     * thxid : teacher290
     * group : []
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
        private String cname;
        private String tname;
        private String thxid;
        private List<?> group;

        public String getCname() {
            return cname;
        }

        public void setCname(String cname) {
            this.cname = cname;
        }

        public String getTname() {
            return tname;
        }

        public void setTname(String tname) {
            this.tname = tname;
        }

        public String getThxid() {
            return thxid;
        }

        public void setThxid(String thxid) {
            this.thxid = thxid;
        }

        public List<?> getGroup() {
            return group;
        }

        public void setGroup(List<?> group) {
            this.group = group;
        }
    }

}
