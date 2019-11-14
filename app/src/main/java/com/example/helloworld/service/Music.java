package com.example.helloworld.service;

import android.media.MediaPlayer;

import com.example.helloworld.utils.GetHttp;
import com.example.helloworld.utils.GetHttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Music {
    /**
     * http://aqqmusic.tc.qq.com/amobile.music.tc.qq.com/C400002EsKmq1Mtjpi.m4a?guid=5653945352&vkey=EDF7BE4293DE7B497E9452A53270DA91D39EFEDC12CD4C761541E911F9F09718432FF8C44B5A66D4DC57F26523D43A1F434D6E4D7EF867C4&uin=0&fromtag=38
     * http://aqqmusic.tc.qq.com/amobile.music.tc.qq.com/C400000QwTVo0YHdcP.m4a?guid=7587371973&vkey=61838B3DEB33BAF17BE43D2478895A65E4FA423492C219095853AF735EE51B3ED0D6D6B7BEEC1E26C04C3528BC743BF2A36377AE3C1E091B&uin=0&fromtag=38
     * https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?format=json205361747&platform=yqq&cid=205361747&songmid=003lghpv0jfFXG&filename=C400003lghpv0jfFXG.m4a&guid=126548448
     * https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=1160855065&&loginUin=247990761&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8¬ice=0&platform=yqq&needNewCode=0&cid=205361747&&uin=247990761&songmid={0}&filename=C400{1}.m4a&guid=6964837424
     * https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=1160855065&&loginUin=247990761&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8¬ice=0&platform=yqq&needNewCode=0&cid=205361747&&uin=247990761&songmid=000qeAhg2Lj8sH&filename=C400000qeAhg2Lj8sH.m4a&guid=6964837424
     * http://ws.stream.qqmusic.qq.com/C100000qeAhg2Lj8sH.m4a?fromtag=0&guid=126548448
     * http://dl.stream.qqmusic.qq.com/C400{0}.m4a?vkey={1}guid=6964837424&uin=247990761&fromtag=66
     * http://dl.stream.qqmusic.qq.com/C400000QwTVo0YHdcP.m4a?vkey=61838B3DEB33BAF17BE43D2478895A65E4FA423492C219095853AF735EE51B3ED0D6D6B7BEEC1E26C04C3528BC743BF2A36377AE3C1E091B&guid=6964837424&uin=247990761&fromtag=66
     */

//    String songmid = "000X8RgD0FO49o";
//    String vkey = "";
//    String vkeyUri = "https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=1160855065&&loginUin=247990761&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8¬ice=0&platform=yqq&needNewCode=0&cid=205361747&&uin=247990761&songmid=" + songmid + "&filename=C400" + songmid + ".m4a&guid=6964837424";
//    String musicUri = "http://aqqmusic.tc.qq.com/amobile.music.tc.qq.com/C400" + songmid + ".m4a?guid=6964837424&vkey=" + vkey + "&uin=0&fromtag=38";
    public String getVkey() {

        return null;
    }

    public static void getMusicInfo(final MediaPlayer mMediaPlayer, final String musicName) {
        final String uri = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?aggr=1&cr=1&flag_qc=0&p=1&n=30&w=";
        final GetHttp getHttp = new GetHttp();
        getHttp.setResultHttpGet(new GetHttpRequest.ResultHttpGet() {
            @Override
            public void result(String resultStr) {
                String songmidStr = resultStr.substring(resultStr.indexOf("songmid\":\""), resultStr.indexOf("\",\"songname"));
                final String songmid = songmidStr.split("songmid\":\"")[1];
                System.out.println("====================================" + songmid);
                String vkeyUri = "https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=1160855065&&loginUin=247990761&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8¬ice=0&platform=yqq&needNewCode=0&cid=205361747&&uin=247990761&songmid=" + songmid + "&filename=C400" + songmid + ".m4a&guid=6964837424";
//                GetHttp getHttp = new GetHttp();
                getHttp.setResultHttpGet(new GetHttpRequest.ResultHttpGet() {
                    @Override
                    public void result(String resultStr) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultStr);
                            JSONObject dataJson = jsonObject.getJSONObject("data");
                            JSONArray itemsArray = dataJson.getJSONArray("items");
                            JSONObject itemsJson = itemsArray.getJSONObject(0);
                            String vkey = itemsJson.getString("vkey");

                            System.out.println("====================================" + vkey);
                            String musicUri = "http://aqqmusic.tc.qq.com/amobile.music.tc.qq.com/C400" + songmid + ".m4a?guid=6964837424&vkey=" + vkey + "&uin=0&fromtag=38";
                            System.out.println("====================================" + musicUri);

                            mMediaPlayer.setDataSource(musicUri);
                            mMediaPlayer.prepare();
                            mMediaPlayer.start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                getHttp.get(vkeyUri);
            }
        });
        getHttp.get(uri + musicName);
    }

    public void musicSearch() {

    }
}
