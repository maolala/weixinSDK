package me.chanjar.weixin.mp.api;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.api.WxErrorExceptionHandler;
import me.chanjar.weixin.common.api.WxMessageDuplicateChecker;
import me.chanjar.weixin.common.api.WxMessageInMemoryDuplicateChecker;
import me.chanjar.weixin.common.session.InternalSession;
import me.chanjar.weixin.common.session.InternalSessionManager;
import me.chanjar.weixin.common.session.StandardSessionManager;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.common.util.LogExceptionHandler;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <pre>
 * 微信消息路由器，通过代码化的配置，把来自微信的消息交给handler处理
 * 
 * 说明：
 * 1. 配置路由规则时要按照从细到粗的原则，否则可能消息可能会被提前处理
 * 2. 默认情况下消息只会被处理一次，除非使用 {@link WxMpMessageRouterRule#next()}
 * 3. 规则的结束必须用{@link WxMpMessageRouterRule#end()}或者{@link WxMpMessageRouterRule#next()}，否则不会生效
 * 
 * 使用方法：
 * WxMpMessageRouter router = new WxMpMessageRouter();
 * router
 *   .rule()
 *       .msgType("MSG_TYPE").event("EVENT").eventKey("EVENT_KEY").content("CONTENT")
 *       .interceptor(interceptor, ...).handler(handler, ...)
 *   .end()
 *   .rule()
 *       // 另外一个匹配规则
 *   .end()
 * ;
 * 
 * // 将WxXmlMessage交给消息路由器
 * router.route(message);
 * 
 * </pre>
 * @author Daniel Qian
 *
 */
public class WxMpMessageRouter {

  protected final Logger log = LoggerFactory.getLogger(WxMpMessageRouter.class);

  private static final int DEFAULT_THREAD_POOL_SIZE = 100;

  private final List<WxMpMessageRouterRule> rules = new ArrayList<WxMpMessageRouterRule>();

  private final WxMpService wxMpService;

  private ExecutorService executorService;

  private WxMessageDuplicateChecker messageDuplicateChecker;

  private WxSessionManager sessionManager;

  private WxErrorExceptionHandler exceptionHandler;

  public WxMpMessageRouter(WxMpService wxMpService) {
    this.wxMpService = wxMpService;
    this.executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
    this.messageDuplicateChecker = new WxMessageInMemoryDuplicateChecker();
    this.sessionManager = new StandardSessionManager();
    this.exceptionHandler = new LogExceptionHandler();
//    rule().async(false).msgType(WxConsts.XML_MSG_TEXT).content("1")
//            .handler(new WxFirstMpMessageHandler()).end();
//    rule().async(false).msgType(WxConsts.XML_MSG_TEXT).content("2")
//            .handler(new WxSecondMpMessageHandler()).end();
//    rule().async(false).msgType(WxConsts.XML_MSG_TEXT).content("3")
//            .handler(new WxNewsMpMessageHandler()).end();
//    rule().async(false).msgType(WxConsts.XML_MSG_EVENT).event(WxConsts.EVT_CLICK)
//            .eventKey("V1001_QUERY_INPUT").handler(new WxMsgMpMessageHandler()).end();
    //点击按钮
//    rule().async(true).msgType(WxConsts.XML_MSG_EVENT).event(WxConsts.EVT_CLICK)
//            .eventKey("V1001_QUERY_INPUT").handler(new WxMsgMpMessageHandler()).end();
//    //关注
//    rule().async(true).msgType(WxConsts.XML_MSG_EVENT).event(WxConsts.EVT_SUBSCRIBE)
//            .handler(new WxFocusOnMpMessageHandler()).end();
//    //扫描
//    rule().async(true).msgType(WxConsts.XML_MSG_EVENT).event(WxConsts.EVT_SCAN)
//            .handler(new WxScanMpMessageHandler()).end();
//    //内部扫描
//    rule().async(true).msgType(WxConsts.XML_MSG_EVENT).event(WxConsts.BUTTON_SCANCODE_WAITMSG)
//            .handler(new WxScanMpMessageHandler()).end();
//    //推送文本
//    rule().async(true).msgType(WxConsts.XML_MSG_TEXT).handler(new WxTextMpMessageHandler()).end();

    //扫描二维码
//    rule().async(false).msgType(WxConsts.XML_MSG_EVENT).event(WxConsts.EVT_SCANCODE_PUSH)
//            .handler(new WxScanCodeMpMessageHandler()).end();
    this.rule().async(false).msgType(WxConsts.XML_MSG_EVENT).event(WxConsts.EVT_CLICK).eventKey("V1001_QUERY_INPUT").handler(new WxMsgMpMessageHandler()).end();
    this.rule().async(false).msgType(WxConsts.XML_MSG_EVENT).event(WxConsts.EVT_SUBSCRIBE).handler(new WxFocusOnMpMessageHandler()).end();
    this.rule().async(false).msgType(WxConsts.XML_MSG_EVENT).event(WxConsts.EVT_SCAN).handler(new WxScanMpMessageHandler()).end();
    this.rule().async(false).msgType(WxConsts.XML_MSG_EVENT).event(WxConsts.EVT_SCANCODE_WAITMSG).eventKey("V1001_QUERY_SCAN").handler(new WxScanMpMessageHandler()).end();
    this.rule().async(true).msgType(WxConsts.XML_MSG_TEXT).handler(new WxTextMpMessageHandler()).end();

  }

  /**
   * <pre>
   * 设置自定义的 {@link ExecutorService}
   * 如果不调用该方法，默认使用 Executors.newFixedThreadPool(100)
   * </pre>
   * @param executorService
   */
  public void setExecutorService(ExecutorService executorService) {
    this.executorService = executorService;
  }

  /**
   * <pre>
   * 设置自定义的 {@link me.chanjar.weixin.common.api.WxMessageDuplicateChecker}
   * 如果不调用该方法，默认使用 {@link me.chanjar.weixin.common.api.WxMessageInMemoryDuplicateChecker}
   * </pre>
   * @param messageDuplicateChecker
   */
  public void setMessageDuplicateChecker(WxMessageDuplicateChecker messageDuplicateChecker) {
    this.messageDuplicateChecker = messageDuplicateChecker;
  }

  /**
   * <pre>
   * 设置自定义的{@link me.chanjar.weixin.common.session.WxSessionManager}
   * 如果不调用该方法，默认使用 {@link me.chanjar.weixin.common.session.StandardSessionManager}
   * </pre>
   * @param sessionManager
   */
  public void setSessionManager(WxSessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }

  /**
   * <pre>
   * 设置自定义的{@link me.chanjar.weixin.common.api.WxErrorExceptionHandler}
   * 如果不调用该方法，默认使用 {@link me.chanjar.weixin.common.util.LogExceptionHandler}
   * </pre>
   * @param exceptionHandler
   */
  public void setExceptionHandler(WxErrorExceptionHandler exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
  }

  List<WxMpMessageRouterRule> getRules() {
    return this.rules;
  }

  /**
   * 开始一个新的Route规则
   * @return
   */
  public WxMpMessageRouterRule rule() {
    return new WxMpMessageRouterRule(this);
  }

  /**
   * 处理微信消息
   * @param wxMessage
   */
  public WxMpXmlOutMessage route(final WxMpXmlMessage wxMessage) {
    if (isDuplicateMessage(wxMessage)) {
      // 如果是重复消息，那么就不做处理
      return null;
    }

    final List<WxMpMessageRouterRule> matchRules = new ArrayList<WxMpMessageRouterRule>();
    // 收集匹配的规则
    for (final WxMpMessageRouterRule rule : rules) {
      if (rule.test(wxMessage)) {
        matchRules.add(rule);
        if(!rule.isReEnter()) {
          break;
        }
      }
    }

    if (matchRules.size() == 0) {
      return null;
    }

    WxMpXmlOutMessage res = null;
    final List<Future> futures = new ArrayList<Future>();
    for (final WxMpMessageRouterRule rule : matchRules) {
      // 返回最后一个非异步的rule的执行结果
      if(rule.isAsync()) {
        futures.add(
            executorService.submit(new Runnable() {
              public void run() {
                rule.service(wxMessage, wxMpService, sessionManager, exceptionHandler);
              }
            })
        );
      } else {
        res = rule.service(wxMessage, wxMpService, sessionManager, exceptionHandler);
        // 在同步操作结束，session访问结束
        log.debug("End session access: async=false, sessionId={}", wxMessage.getFromUserName());
        sessionEndAccess(wxMessage);
      }
    }

    if (futures.size() > 0) {
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          for (Future future : futures) {
            try {
              future.get();
              log.debug("End session access: async=true, sessionId={}", wxMessage.getFromUserName());
              // 异步操作结束，session访问结束
              sessionEndAccess(wxMessage);
            } catch (InterruptedException e) {
              log.error("Error happened when wait task finish", e);
            } catch (ExecutionException e) {
              log.error("Error happened when wait task finish", e);
            }
          }
        }
      });
    }
    return res;
  }

  protected boolean isDuplicateMessage(WxMpXmlMessage wxMessage) {

    String messageId = "";
    if (wxMessage.getMsgId() == null) {
      messageId = String.valueOf(wxMessage.getCreateTime())
          + "-" + wxMessage.getFromUserName()
          + "-" + String.valueOf(wxMessage.getEventKey() == null ? "" : wxMessage.getEventKey())
          + "-" + String.valueOf(wxMessage.getEvent() == null ? "" : wxMessage.getEvent())
      ;
    } else {
      messageId = String.valueOf(wxMessage.getMsgId());
    }

    if (messageDuplicateChecker.isDuplicate(messageId)) {
      return true;
    }
    return false;

  }

  /**
   * 对session的访问结束
   * @param wxMessage
   */
  protected void sessionEndAccess(WxMpXmlMessage wxMessage) {

    InternalSession session = ((InternalSessionManager)sessionManager).findSession(wxMessage.getFromUserName());
    if (session != null) {
      session.endAccess();
    }

  }
}
