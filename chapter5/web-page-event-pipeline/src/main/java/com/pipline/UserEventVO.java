package com.pipline;

public class UserEventVO {

    public UserEventVO(String timestamp, String userAgent, String colorName, String userName){
        this.timestamp = timestamp;
        this.userAgent = userAgent;
        this.colorName = colorName;
        this.userName = userName;
    }

//    데이터 생성 시점을 저장 
//    적재 순서가 순차적이지 않더라고 해당 값으로 정렬 가능
    private String timestamp;

//    유저 사용 브라우저
    private String userAgent;

//    컬러명
    private String colorName;

//    유저명
    private String userName;

}
