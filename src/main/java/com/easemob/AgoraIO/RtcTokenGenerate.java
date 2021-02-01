package com.easemob.AgoraIO;

public class RtcTokenGenerate {
    static int expirationTimeInSeconds = 3600;
    public static String generateToken(String appId,String appCertificate,String channelName,String userAccount){
        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int)(System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
        String result = token.buildTokenWithUserAccount(appId, appCertificate,
                channelName, userAccount, RtcTokenBuilder.Role.Role_Publisher, timestamp);
        System.out.println(result);
        return result;
    }
}
