package me.chanjar.weixin.mp.api;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.bean.*;
import me.chanjar.weixin.mp.bean.outxmlbuilder.TextBuilder;

import java.util.Map;

/**
 * Created by Ray.Fu on 2016/4/25.
 */
public class WxMsgMpMessageHandler implements WxMpMessageHandler {
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        WxMpXmlOutMessage m = null;
        try {
            m = ((TextBuilder)((TextBuilder)WxMpXmlOutMessage.TEXT().content(wxMessage.getMap().get("dishList").toString()).fromUser(wxMessage.getToUserName())).toUser(wxMessage.getFromUserName())).build();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return m;
        }
    }
}
