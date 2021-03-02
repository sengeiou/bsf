package com.weking.controller.system;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.weking.controller.out.OutControllerBase;
import com.weking.core.IMCode;
import com.weking.core.PushMsg;
import com.weking.service.shop.GoodsService;
import com.weking.service.user.UserService;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@Controller
@RequestMapping({"/test","/live/test"})
public class TestController extends OutControllerBase {

    protected static Logger log = Logger.getLogger("error");
    @Resource
    private GoodsService goodsService;
    @Resource
    private UserService userService;

    @RequestMapping("test")
    public void test(HttpServletRequest request, HttpServletResponse response) {
        String key1 = getParameter(request, "key1");
        String key2 = getParameter(request, "key2");
        log.info("key1:" + key1 + "---key2:" + key2);
        System.out.println("key1:" + key1 + "---key2:" + key2);
//		String apple_receipt = getParameter(request,"data");
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("receipt-data", apple_receipt);
//		String url = "https://buy.itunes.apple.com/verifyReceipt";
//		String post = HttpXmlUtils.httpsRequest(url, "POST", jsonObject.toString());
        out(response, "0",1.2);
    }

    @RequestMapping("demo")
    public void demo(HttpServletRequest request, HttpServletResponse response) {
        String goods_info = getParameter(request, "goods_info");

        System.out.println("demo:" + goodsService.updateGoodsNumBatch(goods_info));
    }

    @RequestMapping("/pushPost")
    public void pushPost(HttpServletRequest request, HttpServletResponse response) {
        String account = getParameter(request, "account");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String cid = userService.getUserFieldByAccount(account,"c_id");
        JSONObject CNObject = PushMsg.getPushJSONObject(IMCode.post_push, "zh_CN", "post.push.title", "post.push.content");
//        PushMsg.pushSingleMsg("feLrcI_61oM:APA91bE9TJJd2DouSf791V3SEWIMcwSwujyIFV4lVv5tpf4h-qfwpbUQ9ZQMJ6z5bBqfoB6BEsjyrVa_9hZ4Wt5OErYtH2Vp83sqSiI7rq1WB6dCxwAiyJwgDaP7Q3tXoHC_cVbDWhXs", CNObject);
//        PushMsg.pushSingleMsg("13d32a375136b6cb47d2c21a2f626cf3", CNObject);
        Boolean flag = PushMsg.pushSingleMsg(cid, CNObject);
        out(response, LibSysUtils.toString(flag),api_version);
    }


    @RequestMapping("paypal")
    public void paypal(HttpServletResponse response) {
        BraintreeGateway gateway = new BraintreeGateway("access_token$sandbox$dgk9w8kn75jfhb2p$30dea7199b1d84b10e1ba97d9f00c93a");

        TransactionRequest request = new TransactionRequest().
                amount(new BigDecimal("100")).
                merchantAccountId("USD").
                paymentMethodNonce("paymentMethodNonce").
                orderId("Mapped to PayPal Invoice Number").
                descriptor().
                name("Descriptor displayed in customer CC statements. 22 char max").
                done()
                .shippingAddress().
                        firstName("Jen")
                .lastName("Smith")
                .company("Braintree")
                .streetAddress("1 E 1st St")
                .extendedAddress("Suite 403")
                .locality("Bartlett")
                .region("IL")
                .postalCode("60103")
                .countryCodeAlpha2("US")
                .done()
                .options().
                        paypal().
                        customField("PayPal custom field").
                        description("Description for PayPal email receipt").
                        done()
                .storeInVaultOnSuccess(true).
                        done();

        Result<Transaction> result = gateway.transaction().sale(request);

        if (result.isSuccess()) {
            Transaction transaction = result.getTarget();
            System.out.println("Success ID: " + transaction.getId());
        } else {
            System.out.println("Message: " + result.getMessage());
        }
    }
}
