package com.xsd.safecardapp.javabean;

import java.util.List;

public class LoginResult {
	



    /**
     * code : 0
     * result : [{"imei":"865583010258571","PhoneNumber":"","UserName":"杨木子","UserId":"14951","ClassId":"1223"}]
     */

    private String code;
    /**
     * imei : 865583010258571
     * PhoneNumber : 
     * UserName : 杨木子
     * UserId : 14951
     * ClassId : 1223
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
        private String imei;
        private String PhoneNumber;
        private String UserName;
        private String UserId;
        private String ClassId;

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getPhoneNumber() {
            return PhoneNumber;
        }

        public void setPhoneNumber(String PhoneNumber) {
            this.PhoneNumber = PhoneNumber;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public String getClassId() {
            return ClassId;
        }

        public void setClassId(String ClassId) {
            this.ClassId = ClassId;
        }
    }

}
