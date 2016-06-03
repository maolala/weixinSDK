package me.chanjar.weixin.mp.api;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.bean.*;

import java.util.Map;

/**
 * Created by Ray.Fu on 2016/4/25.
 */
public class WxMsgMpMessageHandler implements WxMpMessageHandler {
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WxMpXmlOutMessage m = null;
        try {
            m = WxMpXmlOutMessage.TEXT().content("aaaa").fromUser(wxMessage.getToUserName()).toUser(wxMessage.getFromUserName()).build();
            System.out.println("m.toXml-> "+m.toXml());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return m;
        }
    }
}
