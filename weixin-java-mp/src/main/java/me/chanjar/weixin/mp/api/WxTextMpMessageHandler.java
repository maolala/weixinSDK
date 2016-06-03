package me.chanjar.weixin.mp.api;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.bean.*;

import java.util.Map;

/**
 * Created by Ray.Fu on 2016/4/26.
 */
public class WxTextMpMessageHandler implements WxMpMessageHandler {
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WxMpXmlOutTextMessage m = null;
        if (wxMessage.getMap().get("valid") == null) {
            String pattern = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{5,6}$";
            if(wxMessage.getContent() != null && wxMessage.getContent().matches(pattern)) {
                WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
                templateMessage.setToUser(wxMessage.getFromUserName());
                templateMessage.setTemplateId("p9ZGWqeFEYbLPkMKOdd5KUKc_QiqfXaJgu1ZNmezIUo");
                templateMessage.setUrl("null");
                templateMessage.getDatas().add(new WxMpTemplateData("keyword1", wxMessage.getMap().get("shopName") == null ? "" : wxMessage.getMap().get("shopName").toString(), "#c37160"));
                templateMessage.getDatas().add(new WxMpTemplateData("keyword2", wxMessage.getMap().get("tokenNum").toString(), "#36b2cc"));
                templateMessage.getDatas().add(new WxMpTemplateData("keyword3", wxMessage.getMap().get("waitedTable").toString(), "#ff9900"));
                templateMessage.getDatas().add(new WxMpTemplateData("keyword4", wxMessage.getMap().get("predictTime").toString(), "#053eea"));
                templateMessage.getDatas().add(new WxMpTemplateData("keyword5", wxMessage.getMap().get("queueStatus")==null? "":wxMessage.getMap().get("queueStatus").toString(), "#ea05b1"));
                templateMessage.getDatas().add(new WxMpTemplateData("remark", wxMessage.getMap().get("queryTime").toString(), "#4a5de8"));
                wxMpService.templateSend(templateMessage);
                return null;
            }
        } else {
            m = WxMpXmlOutMessage.TEXT().content(wxMessage.getMap().get("valid").toString()).fromUser(wxMessage.getToUserName()).toUser(wxMessage.getFromUserName()).build();
        }
        return m;
    }
}
