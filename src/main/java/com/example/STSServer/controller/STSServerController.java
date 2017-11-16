package com.example.STSServer.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class STSServerController {

    public static final String REGION_CN_HANGZHOU = "cn-hangzhou";
    // 当前 STS API 版本
    public static final String STS_API_VERSION = "2015-04-01";

    static AssumeRoleResponse assumeRole(String accessKeyId, String accessKeySecret,
                                         String roleArn, String roleSessionName, String policy,
                                         ProtocolType protocolType) throws ClientException {
        try {
            // 创建一个 Aliyun Acs Client, 用于发起 OpenAPI 请求
            IClientProfile profile = DefaultProfile.getProfile(REGION_CN_HANGZHOU, accessKeyId, accessKeySecret);
            DefaultAcsClient client = new DefaultAcsClient(profile);
            // 创建一个 AssumeRoleRequest 并设置请求参数
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setVersion(STS_API_VERSION);
            request.setMethod(MethodType.POST);
            request.setProtocol(protocolType);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy);
            // 发起请求，并得到response
            final AssumeRoleResponse response = client.getAcsResponse(request);
            return response;
        } catch (ClientException e) {
            throw e;
        }
    }

    String accessKeyId = "LTAIaZvOZheNJGT3";//  LTAIaZvOZheNJGT3  LTAIbjYjxK78QDvi
    String accessKeySecret = "6bTw9HfbbNlegKCNuXFacmOKMLHKga";//  6bTw9HfbbNlegKCNuXFacmOKMLHKga  oCoMFltxWegrS8mXJRWGW8EHxRlbCL
    String roleArn = "acs:ram::1489696239152108:role/jiayibilin";//  1489696239152108 jiayibilin  1477550317375259  sunmenosswrite
    String roleSessionName = "alice";
    String policy = "{\n" +
            "    \"Version\": \"1\", \n" +
            "    \"Statement\": [\n" +
            "        {\n" +
            "            \"Action\": [\n" +
            "                \"oss:*\"\n" +
            "            ], \n" +
            "            \"Resource\": [\n" +
            "                \"acs:oss:*:*:jybl-photo\",\n" + //  jybl-photo  sunmen-oss
            "                \"acs:oss:*:*:jybl-photo/*\"\n" + //  jybl-photo  sunmen-oss
            "            ], \n" +
            "            \"Effect\": \"Allow\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";
    ProtocolType protocolType = ProtocolType.HTTPS;

    @CrossOrigin(origins="*")
    @RequestMapping("/requestSTS")
    public ResponseEntity<String> requestSTS(HttpServletRequest request, HttpServletResponse response) {

        JSONObject jo = new JSONObject();

        try {
            final AssumeRoleResponse stsResponse = assumeRole(accessKeyId, accessKeySecret,
                    roleArn, roleSessionName, policy, protocolType);
            jo.put("code", 0);
            jo.put("AccessKeyId", stsResponse.getCredentials().getAccessKeyId());
            jo.put("AccessKeySecret", stsResponse.getCredentials().getAccessKeySecret());
            jo.put("SecurityToken", stsResponse.getCredentials().getSecurityToken());
            jo.put("Expiration", stsResponse.getCredentials().getExpiration());
        } catch (ClientException e) {
            System.out.println(e.getErrorType());
            jo.put("code", -1);
            jo.put("errMsg", e.getErrMsg());
        }

        response.setHeader("Access-Control-Allow-Origin","*");
        return new ResponseEntity<String>(JSON.toJSONString(jo), HttpStatus.OK);
    }

}
