package org.geelato.core.util;

import com.alibaba.fastjson2.JSON;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;

/**
 * @author diabl
 * @description: TODO
 * @date 2023/7/21 15:25
 */
public class AliMobileUtils {
    private static final Logger logger = LoggerFactory.getLogger(AliMobileUtils.class);
    private static final String ALIBABA_CLOUD_ACCESS_KEY_ID = "LTAI5tDdAKSKAnDEkSRT3RUV";
    private static final String ALIBABA_CLOUD_ACCESS_KEY_SECRET = "LzMUDHRye41hKJhPkBkWJEk7w9msec";
    private static final String ALIBABA_CLOUD_SECURITY_TOKEN = "";
    private static final String SIGN_NAME = "阿里云短信测试";
    private static final String TEMPLATE_CODE = "SMS_154950909";
    private static final int SEND_SMS_RESPONSE_STATUS_CODE = 200;
    private static final String SEND_SMS_RESPONSE_BODY_CODE = "OK";

    /**
     * 使用AK&SK初始化账号Client
     *
     * @throws Exception
     */
    public static com.aliyun.dysmsapi20170525.Client createClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(ALIBABA_CLOUD_ACCESS_KEY_ID)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(ALIBABA_CLOUD_ACCESS_KEY_SECRET);
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    /**
     * 使用STS鉴权方式初始化账号Client，推荐此方式。
     *
     * @throws Exception
     */
    public static com.aliyun.dysmsapi20170525.Client createClientWithSTS() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(ALIBABA_CLOUD_ACCESS_KEY_ID)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(ALIBABA_CLOUD_ACCESS_KEY_SECRET)
                // 必填，您的 Security Token
                .setSecurityToken(ALIBABA_CLOUD_SECURITY_TOKEN)
                // 必填，表明使用 STS 方式
                .setType("sts");
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    /**
     * 发送信息
     *
     * @param phoneNumbers  电话号码
     * @param templateParam 模板参数
     * @return
     * @throws Exception
     */
    public static boolean sendMobile(String phoneNumbers, Map<String, Object> templateParam) throws Exception {
        // 请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID 和 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
        // com.aliyun.dysmsapi20170525.Client client = AliMobileUtils.createClient(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"), System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"));
        com.aliyun.dysmsapi20170525.Client client = AliMobileUtils.createClient();
        com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setSignName(AliMobileUtils.SIGN_NAME)
                .setTemplateCode(AliMobileUtils.TEMPLATE_CODE)
                .setPhoneNumbers(phoneNumbers)
                .setTemplateParam(JSON.toJSONString(templateParam));
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        com.aliyun.dysmsapi20170525.models.SendSmsResponse resp = client.sendSmsWithOptions(sendSmsRequest, runtime);
        com.aliyun.teaconsole.Client.log(com.aliyun.teautil.Common.toJSONString(resp));
        if (resp.getStatusCode() == SEND_SMS_RESPONSE_STATUS_CODE) {
            if (Strings.isNotBlank(resp.getBody().getCode()) && SEND_SMS_RESPONSE_BODY_CODE.equals(resp.getBody().getCode().toUpperCase(Locale.ENGLISH))) {
                logger.info("短信发送成功！" + phoneNumbers);
                return true;
            }
        }
        return false;
    }
}
