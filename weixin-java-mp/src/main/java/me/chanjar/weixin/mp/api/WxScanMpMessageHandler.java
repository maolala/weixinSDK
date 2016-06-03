package me.chanjar.weixin.mp.api;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.bean.*;

import java.util.Map;

/**
 * Created by Ray.Fu on 2016/4/26.
 */
public class WxScanMpMessageHandler implements WxMpMessageHandler {
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WxMpXmlOutTextMessage m = null;
        System.out.println("-----shopName-----");
        if (wxMessage.getMap().get("valid") == null) {
            WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
            templateMessage.setToUser(wxMessage.getFromUserName());
            templateMessage.setTemplateId("p9ZGWqeFEYbLPkMKOdd5KUKc_QiqfXaJgu1ZNmezIUo");
            templateMessage.setUrl("null");
            templateMessage.getDatas().add(new WxMpTemplateData("keyword1", wxMessage.getMap().get("dish1").toString() , "#c37160"));
            templateMessage.getDatas().add(new WxMpTemplateData("keyword2", wxMessage.getMap().get("dish2").toString(), "#36b2cc"));
            templateMessage.getDatas().add(new WxMpTemplateData("keyword3", wxMessage.getMap().get("dish3").toString(), "#ff9900"));
            templateMessage.getDatas().add(new WxMpTemplateData("keyword4", "测试1", "#053eea"));
            templateMessage.getDatas().add(new WxMpTemplateData("keyword5", "测试2", "#ea05b1"));
            templateMessage.getDatas().add(new WxMpTemplateData("remark", "测试3", "#4a5de8"));
            wxMpService.templateSend(templateMessage);
            return null;
        } else {
            m = WxMpXmlOutMessage.TEXT().content(wxMessage.getMap().get("valid").toString()).fromUser(wxMessage.getToUserName()).toUser(wxMessage.getFromUserName()).build();
        }
        return m;
    }
}
