package com.xsl.wechat.util;

import com.xsl.wechat.enumeration.MessageTypeEnum;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 固定消息回复工具类
 */
public class SpecificMessageHandlerUtil {

    /**
     * @param request 封装了请求信息的HttpServletRequest对象
     * @return 解析结果
     * @throws IOException
     * @throws DocumentException
     */
    public static Map<String, String> parseXml(HttpServletRequest request) throws IOException, DocumentException {
        // 将解析结果存在map中
        HashMap<String, String> map = new HashMap<>();
        // 从request中获取输入流
        InputStream inputStream = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element rootElement = document.getRootElement();
        // 得到跟元素的所有子节点
        List<Element> elementList = rootElement.elements();
        // 遍历所有子节点
        for (Element e : elementList) {
            System.out.println("元素名：" + e.getName() + "值：" + e.getText());
            map.put(e.getName(), e.getText());
        }
        // 释放资源
        inputStream.close();
        return map;
    }

    /**
     * 根据消息类型构造返回消息
     *
     * @param map 封装了解析结果的map
     * @return responseMessage(响应消息)
     */
    public static String buildResponseMessage(Map<String, String> map) {
        // 响应信息
        String responseMessage = null;
        // 得到消息类型
        String msgType = map.get("MsgType").toString();
        System.out.println("消息类型" + msgType);
        // 消息类型
        MessageTypeEnum msgTypeEnum = MessageTypeEnum.valueOf(MessageTypeEnum.class, msgType.toUpperCase());
        switch (msgTypeEnum) {
            case TEXT:
                // 处理文本消息
                responseMessage = handleTextMessage(map);
                break;
            case IMAGE:
                // 处理图片消息
                responseMessage = handleImageMessage(map);
                break;
            case VOICE:
                // 处理语音消息
                responseMessage = handleVoiceMessage(map);
                break;
            case VIDEO:
                // 处理视频消息
                responseMessage = handleVideoMessage(map);
                break;
            case SHORTVIDEO:
                // 处理小视频消息
                responseMessage = handleSmallVideoMessage(map);
                break;
            case LOCATION:
                // 处理位置消息
                responseMessage = handleLocationMessage(map);
                break;
            case LINK:
                // 处理链接消息
                responseMessage = handleLinkMessage(map);
                break;
            case EVENT:
                // 处理时间消息，用户在关注或取消关注时，微信会向我们发送事件消息，开发者接收到事件消息之后可以给用户下发欢迎消息
                responseMessage = handleEventMessage(map);
                break;
            default:
                break;
        }
        // 返回响应消息
        return responseMessage;
    }

    /**
     * 接收文本消息后处理
     * @param map 封装了解析结果的map
     * @return responseMessage(响应消息)
     */
    private static String handleTextMessage(Map<String, String> map) {
        // 响应消息
        String responseMessage = null;
        // 消息内容
        String content = map.get("Content");
        switch (content) {
            case "文本":
                String msgText = "<a href=\"http://lenglianjia.top\">终于等到你啦，欢迎访问我的个人博客</a>";
                responseMessage = buildTextMessage(map, msgText);
                break;
            case "图片":
//                responseMessage = buildTextMessage(map, "攻城狮正在开发中。。。");

                String imgMediaId = "dO2vohwnd2GMuXbPeO1QDQgtpngRWuJJ__83vZjXID-n65H3wNyvqCx5lE_Kn3q2"; // TODO  需素材管理接口上传图片是得到 media_id
                responseMessage = buildImageMessage(map, imgMediaId);

                break;
            case "语音":
//                responseMessage = buildTextMessage(map, "攻城狮正在开发中。。。");

                String voiceMediaId = "kvQSVlM_05SYQhcZfLgD1zYvBSZ11BWtAir4YMA2rrx1uGY2OFMhe_1zSavIyolq"; // TODO 需素材管理接口上传语音文件得到 media_id
                responseMessage = buildVoiceMessage(map, voiceMediaId);

                break;
            case "图文":
                responseMessage = buildNewsMessage(map);
                break;
            case "音乐":
                Music music = new Music();
                music.title = "赵丽颖、许志安 - 乱世俱灭";
                music.description = "电视剧《蜀山战纪》插曲";
                music.musicUrl = "http://36b92c46.cpolar.io/media/music/music.mp3";
                music.hqMusicUrl = "http://36b92c46.cpolar.io/media/music/music.mp3";
                responseMessage = buildMusicMessage(map, music);
                break;
            case "视频":
//                responseMessage = buildTextMessage(map, "攻城狮正在开发中。。。");

                Video video = new Video();
                video.mediaId = "xmCa7o-QvKzkDRxFWY1L0bpHuavn9IVNncfgYasMb0KXsQX1BZ39BeX730RfbOE_"; // TODO 需素材管理接口上传语音文件得到 media_id
                video.title = "小苹果";
                video.description = "小苹果搞笑视频";
                responseMessage = buildVideoMessage(map, video);

                break;
            default:
                responseMessage = buildWelcomeTextMessage(map);
                break;
        }
        return responseMessage;
    }

    /**
     * 构造文本消息
     *
     * @param map     封装了解析结果的map
     * @param content 文本消息内容
     * @return 文本消息XML字符串
     */
    private static String buildTextMessage(Map<String, String> map, String content) {
        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");
        /*
            xml 数据格式：
            <xml>
                <ToUserName>< ![CDATA[toUser] ]></ToUserName>
                <FromUserName>< ![CDATA[fromUser] ]></FromUserName>
                <CreateTime>12345678</CreateTime>
                <MsgType>< ![CDATA[text] ]></MsgType>
                <Content>< ![CDATA[你好] ]></Content>
            </xml>
         */
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[text]]></MsgType>" +
                        "<Content><![CDATA[%s]]></Content>" +
                        "</xml>",
                fromUserName, toUserName, getMessageCreateTime(), content);
    }

    /**
     * 构造图片消息
     *
     * @param map     封装了解析结果的map
     * @param mediaId 通过素材管理接口上传多媒体文件得到的id
     * @return 图片消息XML字符串
     */
    private static String buildImageMessage(Map<String, String> map, String mediaId) {
        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");
        /*
            图片消息格式：
            <xml>
                <ToUserName>< ![CDATA[toUser] ]></ToUserName>
                <FromUserName>< ![CDATA[fromUser] ]></FromUserName>
                <CreateTime>12345678</CreateTime>
                <MsgType>< ![CDATA[image] ]></MsgType>
                <Image>
                    <MediaId>< ![CDATA[media_id] ]></MediaId>
                </Image>
            </xml>
         */
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[image]]></MsgType>" +
                        "<Image>" +
                        "   <MediaId><![CDATA[%s]]></MediaId>" +
                        "</Image>" +
                    "</xml>",
                fromUserName, toUserName, getMessageCreateTime(), mediaId);
    }

    /**
     * 构造语音消息
     *
     * @param map     封装了解析结果的map
     * @param mediaId 通过素材管理接口上传多媒体文件得到的mediaId
     * @return 语音消息XML字符串
     */
    private static String buildVoiceMessage(Map<String, String> map, String mediaId) {
        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");
        /*
           语音xml格式：
           <xml>
                <ToUserName>< ![CDATA[toUser] ]></ToUserName>
                <FromUserName>< ![CDATA[fromUser] ]></FromUserName>
                <CreateTime>12345678</CreateTime>
                <MsgType>< ![CDATA[voice] ]></MsgType>
                <Voice>
                    <MediaId>< ![CDATA[media_id] ]></MediaId>
                </Voice>
            </xml>
         */
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[voice]]></MsgType>" +
                        "<Voice>" +
                        "   <MediaId><![CDATA[%s]]></MediaId>" +
                        "</Voice>" +
                        "</xml>",
                fromUserName, toUserName, getMessageCreateTime(), mediaId);
    }

    private static String buildNewsMessage(Map<String, String> map) {
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        NewsItem item = new NewsItem();
        item.Title = "微信开发学习总结（一）——微信开发环境搭建";
        item.Description = "工欲善其事，必先利其器。要做微信公众号开发，那么要先准备好两样必不可少的东西：\n" +
                "\n" +
                "　　1、要有一个用来测试的公众号。\n" +
                "\n" +
                "　　2、用来调式代码的开发环境";
        item.PicUrl = "http://images2015.cnblogs.com/blog/289233/201601/289233-20160121164317343-2145023644.png";
        item.Url = "http://www.cnblogs.com/xdp-gacl/p/5149171.html";
        String itemContent1 = buildSingleItem(item);

        NewsItem item2 = new NewsItem();
        item2.Title = "微信开发学习总结（二）——微信开发入门";
        item2.Description = "微信服务器就相当于一个转发服务器，终端（手机、Pad等）发起请求至微信服务器，微信服务器然后将请求转发给我们的应用服务器。应用服务器处理完毕后，将响应数据回发给微信服务器，微信服务器再将具体响应信息回复到微信App终端。";
        item2.PicUrl = "";
        item2.Url = "http://www.cnblogs.com/xdp-gacl/p/5151857.html";
        String itemContent2 = buildSingleItem(item2);


        String content = String.format(
                "<xml>\n" +
                    "<ToUserName><![CDATA[%s]]></ToUserName>\n" +
                    "<FromUserName><![CDATA[%s]]></FromUserName>\n" +
                    "<CreateTime>%s</CreateTime>\n" +
                    "<MsgType><![CDATA[news]]></MsgType>\n" +
                    "<ArticleCount>%s</ArticleCount>\n" +
                    "<Articles>\n" + "%s" +
                    "</Articles>\n" +
                "</xml> ", fromUserName, toUserName, getMessageCreateTime(), 2, itemContent1 + itemContent2);
        return content;

    }

    /**
     * 生成图文消息的一条记录
     *
     * @param item
     * @return
     */
    private static String buildSingleItem(NewsItem item) {
        String itemContent = String.format(
                "<item>\n" +
                        "<Title><![CDATA[%s]]></Title> \n" +
                        "<Description><![CDATA[%s]]></Description>\n" +
                        "<PicUrl><![CDATA[%s]]></PicUrl>\n" +
                        "<Url><![CDATA[%s]]></Url>\n" +
                "</item>", item.Title, item.Description, item.PicUrl, item.Url);
        return itemContent;
    }

    /**
     * 构造音乐消息
     * @param map 封装了解析结果的Map
     * @param music 封装好的音乐消息内容
     * @return 音乐消息XML字符串
     */
    private static String buildMusicMessage(Map<String, String> map, Music music) {
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        /**
         * 音乐消息XML数据格式
         *<xml>
         *     <ToUserName>< ![CDATA[toUser] ]></ToUserName>
         *     <FromUserName>< ![CDATA[fromUser] ]></FromUserName>
         *     <CreateTime>12345678</CreateTime>
         *     <MsgType>< ![CDATA[music] ]></MsgType>
         *     <Music>
         *         <Title>< ![CDATA[TITLE] ]></Title>
         *         <Description>< ![CDATA[DESCRIPTION] ]></Description>
         *         <MusicUrl>< ![CDATA[MUSIC_Url] ]></MusicUrl>
         *         <HQMusicUrl>< ![CDATA[HQ_MUSIC_Url] ]></HQMusicUrl>
         *         <ThumbMediaId>< ![CDATA[media_id] ]></ThumbMediaId>
         *     </Music>
         *</xml>
         */
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[music]]></MsgType>" +
                        "<Music>" +
                        "   <Title><![CDATA[%s]]></Title>" +
                        "   <Description><![CDATA[%s]]></Description>" +
                        "   <MusicUrl><![CDATA[%s]]></MusicUrl>" +
                        "   <HQMusicUrl><![CDATA[%s]]></HQMusicUrl>" +
                        "</Music>" +
                        "</xml>",
                fromUserName, toUserName, getMessageCreateTime(), music.title, music.description, music.musicUrl, music.hqMusicUrl);
    }

    /**
     * 构造视频消息
     * @param map 封装了解析结果的Map
     * @param video 封装好的视频消息内容
     * @return 视频消息XML字符串
     */
    private static String buildVideoMessage(Map<String, String> map, Video video) {
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        /**
         * 音乐消息XML数据格式
         *<xml>
         <ToUserName><![CDATA[toUser]]></ToUserName>
         <FromUserName><![CDATA[fromUser]]></FromUserName>
         <CreateTime>12345678</CreateTime>
         <MsgType><![CDATA[video]]></MsgType>
         <Video>
         <MediaId><![CDATA[media_id]]></MediaId>
         <Title><![CDATA[title]]></Title>
         <Description><![CDATA[description]]></Description>
         </Video>
         </xml>
         */
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[video]]></MsgType>" +
                        "<Video>" +
                        "   <MediaId><![CDATA[%s]]></MediaId>" +
                        "   <Title><![CDATA[%s]]></Title>" +
                        "   <Description><![CDATA[%s]]></Description>" +
                        "</Video>" +
                        "</xml>",
                fromUserName, toUserName, getMessageCreateTime(), video.mediaId, video.title, video.description);
    }


    /**
     * 处理接收到图片消息
     * @param map
     * @return
     */
    private static String handleImageMessage(Map<String, String> map) {
        String picUrl = map.get("PicUrl");
        String mediaId = map.get("MediaId");
        System.out.print("picUrl:" + picUrl);
        System.out.print("mediaId:" + mediaId);
        String result = String.format("已收到您发来的图片，图片Url为：%s\n图片素材Id为：%s", picUrl, mediaId);
        return buildTextMessage(map, result);
    }

    /**
     * 处理接收到语音消息
     * @param map
     * @return
     */
    private static String handleVoiceMessage(Map<String, String> map) {
        String format = map.get("Format");
        String mediaId = map.get("MediaId");
        System.out.print("format:" + format);
        System.out.print("mediaId:" + mediaId);
        String result = String.format("已收到您发来的语音，语音格式为：%s\n语音素材Id为：%s", format, mediaId);
        return buildTextMessage(map, result);
    }

    /**
     * 处理接收到的视频消息
     * @param map
     * @return
     */
    private static String handleVideoMessage(Map<String, String> map) {
        String thumbMediaId = map.get("ThumbMediaId");
        String mediaId = map.get("MediaId");
        System.out.print("thumbMediaId:" + thumbMediaId);
        System.out.print("mediaId:" + mediaId);
        String result = String.format("已收到您发来的视频，视频中的素材ID为：%s\n视频Id为：%s", thumbMediaId, mediaId);
        return buildTextMessage(map, result);
    }

    /**
     * 处理接收到的小视频消息
     * @param map
     * @return
     */
    private static String handleSmallVideoMessage(Map<String, String> map) {
        String thumbMediaId = map.get("ThumbMediaId");
        String mediaId = map.get("MediaId");
        System.out.print("thumbMediaId:" + thumbMediaId);
        System.out.print("mediaId:" + mediaId);
        String result = String.format("已收到您发来的小视频，小视频中素材ID为：%s,\n小视频Id为：%s", thumbMediaId, mediaId);
        return buildTextMessage(map, result);
    }

    /**
     * 处理接收到的地理位置消息
     * @param map
     * @return
     */
    private static String handleLocationMessage(Map<String, String> map) {
        String latitude = map.get("Location_X");  //纬度
        String longitude = map.get("Location_Y");  //经度
        String label = map.get("Label");  //地理位置精度
        String result = String.format("纬度：%s\n经度：%s\n地理位置：%s", latitude, longitude, label);
        return buildTextMessage(map, result);
    }

    /**
     * 处理接收到的链接消息
     * @param map
     * @return
     */
    private static String handleLinkMessage(Map<String, String> map) {
        String title = map.get("Title");
        String description = map.get("Description");
        String url = map.get("Url");
        String result = String.format("已收到您发来的链接，链接标题为：%s,\n描述为：%s\n,链接地址为：%s", title, description, url);
        return buildTextMessage(map, result);
    }


    /**
     * 处理事件消息
     * @param map 封装了解析结果的map
     * @return
     */
    private static String handleEventMessage(Map<String, String> map) {
        String responseMessage = buildWelcomeTextMessage(map);
        return responseMessage;
    }

    /**
     * 构建提示消息
     * @param map 封装了解析结果的map
     * @return responseMessageXml
     */
    private static String buildWelcomeTextMessage(Map<String, String> map) {
        String responseMessageXml;
        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");
        responseMessageXml = String
                .format("<xml>" +
                                "<ToUserName><![CDATA[%s]]></ToUserName>" +
                                "<FromUserName><![CDATA[%s]]></FromUserName>" +
                                "<CreateTime>%s</CreateTime>" +
                                "<MsgType><![CDATA[text]]></MsgType>" +
                                "<Content><![CDATA[%s]]></Content>" +
                                "</xml>",
                        fromUserName, toUserName, getMessageCreateTime(),
                        "感谢您关注点联公众号，请回复如下关键词来使用公众号提供的服务：\n1.文本\n2.图片\n3.语音\n4.视频\n5.音乐\n6.图文");
        return responseMessageXml;
    }


    /**
     * 生成消息创建时间(整型)
     * @return 消息创建时间
     */
    private static String getMessageCreateTime() {
        return new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
    }
}

/**
 * 图文消息
 */
class NewsItem {
    public String Title;
    public String Description;
    public String PicUrl;
    public String Url;
}

/**
 * 音乐消息
 */
class Music {
    public String title;
    public String description;
    public String musicUrl;
    public String hqMusicUrl;
}

/**
 * 视频消息
 */
class Video {
    public String title;
    public String description;
    public String mediaId;
}
