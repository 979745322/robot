package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.helloworld.utils.GetHttp;
import com.example.helloworld.utils.GetHttpRequest;
import com.example.helloworld.utils.MarketUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class yuyinChat extends AppCompatActivity implements View.OnClickListener {
    private String userName;
    private int currentVolume;
    // 获取系统的Audio管理者
    private AudioManager mAudioManager;
    // 用于播放音乐
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    // 正在播放的音乐名称
    private String songName = "";
    // 随机播放
    private boolean randomMusic = false;
    private static String TAG = YuTestActivity.class.getSimpleName();
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private EditText et_content;
    // private Button bt_speech;
    private SpeechSynthesizer mTts; // 语音合成
    private SpeechRecognizer mIat; // 语音听写
    private Button bt_write;
    private RecognizerDialog iatDialog; //听写动画
    // 0 小燕 青年女声 中英文（普通话） xiaoyan
    // 1 默认 小宇 青年男声 中英文（普通话） xiaoyu
    // 2 凯瑟琳 青年女声 英文 catherine
    // 3 亨利 青年男声 英文 henry
    // 4 玛丽 青年女声 英文 vimary
    // 5 小研 青年女声 中英文（普通话） vixy
    // 6 小琪 青年女声 中英文（普通话） vixq xiaoqi
    // 7 小峰 青年男声 中英文（普通话） vixf
    // 8 小梅 青年女声 中英文（粤语） vixm xiaomei
    // 9 小莉 青年女声 中英文（台湾普通话） vixl xiaolin
    // 10 小蓉 青年女声 汉语（四川话） vixr xiaorong
    // 11 小芸 青年女声 汉语（东北话） vixyun xiaoqian
    // 12 小坤 青年男声 汉语（河南话） vixk xiaokun
    // 13 小强 青年男声 汉语（湖南话） vixqa xiaoqiang
    // 14 小莹 青年女声 汉语（陕西话） vixying
    // 15 小新 童年男声 汉语（普通话） vixx xiaoxin
    // 16 楠楠 童年女声 汉语（普通话） vinn nannan
    // 17 老孙 老年男声 汉语（普通话）
    private String[] voiceName = {"xiaoyan", "xiaoyu", "catherine", "henry",
            "vimary", "vixy", "xiaoqi", "vixf", "xiaomei", "xiaolin",
            "xiaorong", "xiaoqian", "xiaokun", "xiaoqiang", "vixying",
            "xiaoxin", "nannan", "vils"};
    // 随机播放歌单
    private String[] randomMusicName = {"哑巴", "消愁", "空空如也", "最美的期待",
            "追光者", "走马", "像我这样的人", "贝加尔湖畔", "答案", "可不可以",
            "凉凉", "卡路里", "讲真的", "拥抱你离去", "学猫叫",
            "体面", "说散就散", "起风了", "不仅仅是喜欢", "离人愁",
            "广东爱情故事", "成都", "远走高飞", "纸短情长", "逆流成河",
            "红昭愿", "醉赤壁", "回忆总想哭", "带你去旅行", "刚好遇见你",
            "最后我们没有在一起", "告白气球", "半壶纱", "123我爱你", "风筝误",
            "魔鬼中的天使", "烟火里的尘埃", "佛系少女", "过客", "光年之外",
            "演员", "男孩", "等你下课", "丑八怪", "认真的雪",
            "你还要我怎样", "绅士", "暧昧", "天后", "方圆几里",
            "像风一样", "我好像在哪见过你", "一半", "意外", "肆无忌惮",
            "动物世界", "遗憾", "摩天大楼", "刚刚好", "天下",
            "背过手", "未完成的歌", "病变", "白羊", "岁月神偷",
            "小半", "可乐", "再也没有", "春风十里", "易燃易爆炸",
            "七月上", "盗将行", "奇妙能力歌", "年少有为", "不将就",
            "默", "大鱼", "模特", "山外小楼夜听雨", "阿楚姑娘",
            "浪人琵琶", "往后余生", "再见只是陌生人", "38度6", "云烟成雨",
            "你就不要想起我", "我要你", "夜空中最亮的星", "九张机", "生如夏花",
            "流浪记", "其实都没有", "从前慢", "匆匆那年", "我们不一样",
            "安和桥", "可惜没如果", "走在冷风中", "我管你", "阿刁"};
    GetHttpRequest getHR = new GetHttpRequest();
    //    TextView txv;
    RecyclerView txv2;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setContentView(R.layout.activity_yuyin_chat);
        et_content = (EditText) findViewById(R.id.et_content);
//        bt_speech = (Button) findViewById(R.id.bt_speech);
        bt_write = (Button) findViewById(R.id.bt_write);
//        bt_speech.setOnClickListener(this);
        bt_write.setOnClickListener(this);
        // 初始化即创建语音配置对象，只有初始化后才可以使用MSC的各项服务
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5d315593");
        // 语音合成 1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        mTts = SpeechSynthesizer.createSynthesizer(yuyinChat.this,
                mTtsInitListener);
        // 语音听写1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mIat = SpeechRecognizer.createRecognizer(this, mTtsInitListener);
        // 1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        iatDialog = new RecognizerDialog(this,
                mTtsInitListener);

        txv2 = findViewById(R.id.chat);  // 根据ID找到对应的text对象
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setReverseLayout(true);
        txv2.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter(new ArrayList<Map<String, String>>(), this, txv2);
        txv2.setAdapter(chatAdapter);
        userName = getIntent().getStringExtra("USERNAME");
//        userName = "您好，";
        Map<String, String> map = new HashMap<>();
        map.put("type", "1");
        map.put("data", userName + "\n现有功能为：" +
                "\n1.聊天。" +
                "\n2.点歌-例子(播放林俊杰的曹操)或(播放曹操)，操作有：暂停、继续播放、单曲循环、关闭单曲循环" +
                "\n如果您没有想听的歌不妨试试对我说\"随机播放\",如果您不想听当前随机播放歌曲，可以对我说\"下一首\"" +
                "\n3.了解关于扇子的相关功能，在开头加“扇子”命令词。 ");
        chatAdapter.addData(map);

        String dataResult = MarketUtil.containsStr("扇子吆喝");
        Map<String, String> map2 = new HashMap<>();
        map2.put("type", "1");
        map2.put("data", dataResult);
        chatAdapter.addData(map2);
        starSpeech(dataResult);
    }

    public static Intent newIntent(Context packageContext, String userName) {
        Intent intent = new Intent(packageContext, yuyinChat.class);
        intent.putExtra("USERNAME", userName);
        return intent;
    }

    public void reBack(View view) {
        Intent intent = new Intent(yuyinChat.this, CameraFaceActivity.class);
        startActivity(intent);
        finish();
    }

    public void bigger(View view) {     // 按钮对应的 onclick 响应
        if (String.valueOf(et_content.getText()).contains("暂停")) {
            if (!songName.equals("")) {
                mMediaPlayer.pause();
                et_content.setText("");
                Map<String, String> map = new HashMap<>();
                map.put("type", "1");
                map.put("data", "暂停播放《" + songName + "》");
                chatAdapter.addData(map);
            } else {
                noMusic();
            }
        } else if (String.valueOf(et_content.getText()).contains("继续播放")) {
            if (!songName.equals("")) {
                mMediaPlayer.start();
                et_content.setText("");
                Map<String, String> map = new HashMap<>();
                map.put("type", "1");
                map.put("data", "继续播放《" + songName + "》");
                chatAdapter.addData(map);
            } else {
                noMusic();
            }
        } else if (String.valueOf(et_content.getText()).contains("关闭单曲循环")) {
            mMediaPlayer.setLooping(false);
            et_content.setText("");
            Map<String, String> map = new HashMap<>();
            map.put("type", "1");
            map.put("data", "关闭单曲循环播放模式");
            chatAdapter.addData(map);
        } else if (String.valueOf(et_content.getText()).contains("单曲循环")) {
            mMediaPlayer.setLooping(true);
            randomMusic = false;
            et_content.setText("");
            Map<String, String> map = new HashMap<>();
            map.put("type", "1");
            map.put("data", "开启单曲循环播放模式");
            chatAdapter.addData(map);
        } else if (String.valueOf(et_content.getText()).contains("下一首")) {
            randomMusic = true;
            mMediaPlayer.setLooping(false);
            et_content.setText("");
            et_content.setText("随机播放");
            bigger(null);
        } else if (String.valueOf(et_content.getText()).contains("随机播放")) {
            randomMusic = true;
            mMediaPlayer.setLooping(false);
            mMediaPlayer.reset();
            final String uri = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?aggr=1&cr=1&flag_qc=0&p=1&n=30&w=";
            final GetHttp getHttp = new GetHttp();
            getHttp.setResultHttpGet(new GetHttpRequest.ResultHttpGet() {
                @Override
                public void result(final String resultStr) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String songStr = resultStr.substring(9, resultStr.length() - 1);
                            try {
                                JSONObject songJson = new JSONObject(songStr);
                                JSONObject dataJson = songJson.getJSONObject("data");
                                JSONObject songJ = dataJson.getJSONObject("song");
                                JSONArray listArr = songJ.getJSONArray("list");
                                JSONObject song0 = listArr.getJSONObject(0);
                                final String songmid = song0.getString("songmid");
                                final String songname = song0.getString("songname");
                                System.out.println("====================================" + songmid);
                                String vkeyUri = "https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=1160855065&&loginUin=247990761&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8¬ice=0&platform=yqq&needNewCode=0&cid=205361747&&uin=247990761&songmid=" + songmid + "&filename=C400" + songmid + ".m4a&guid=6964837424";
                                getHttp.setResultHttpGet(new GetHttpRequest.ResultHttpGet() {
                                    @Override
                                    public void result(final String resultStr) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(resultStr);
                                                    JSONObject dataJson = jsonObject.getJSONObject("data");
                                                    JSONArray itemsArray = dataJson.getJSONArray("items");
                                                    JSONObject itemsJson = itemsArray.getJSONObject(0);
                                                    String vkey = itemsJson.getString("vkey");
                                                    if (vkey.equals("")) {
                                                        et_content.setText("随机播放");
                                                        bigger(null);
                                                    } else {
                                                        System.out.println("====================================" + vkey);
                                                        String musicUri = "http://aqqmusic.tc.qq.com/amobile.music.tc.qq.com/C400" + songmid + ".m4a?guid=6964837424&vkey=" + vkey + "&uin=0&fromtag=38";
                                                        System.out.println("====================================" + musicUri);
                                                        Map<String, String> map = new HashMap<>();
                                                        map.put("type", "1");
                                                        map.put("data", "正在随机播放《" + songname + "》");
                                                        starSpeech("正在随机播放" + songname);
                                                        songName = songname;
                                                        chatAdapter.addData(map);
                                                        mMediaPlayer.setDataSource(musicUri);
                                                        mMediaPlayer.prepare();
//                                                        mMediaPlayer.prepareAsync();
                                                        mMediaPlayer.start();
                                                        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                            @Override
                                                            public void onCompletion(MediaPlayer mp) {
                                                                Log.d("tag", "播放完毕");
                                                                et_content.setText("随机播放");
                                                                bigger(null);
                                                            }
                                                        });
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    }
                                });
                                getHttp.get(vkeyUri);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            Random random = new Random();
            int num = random.nextInt(100); // 0-99
            getHttp.get(uri + randomMusicName[num]);
            et_content.setText("");
        } else if (String.valueOf(et_content.getText()).contains("播放")) {
            randomMusic = false;
            mMediaPlayer.reset();
            final String musicName = String.valueOf(et_content.getText()).split("播放")[1];
            final String uri = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?aggr=1&cr=1&flag_qc=0&p=1&n=30&w=";
            final GetHttp getHttp = new GetHttp();
            getHttp.setResultHttpGet(new GetHttpRequest.ResultHttpGet() {
                @Override
                public void result(final String resultStr) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String songStr = resultStr.substring(9, resultStr.length() - 1);
                            try {
                                JSONObject songJson = new JSONObject(songStr);
                                JSONObject dataJson = songJson.getJSONObject("data");
                                JSONObject songJ = dataJson.getJSONObject("song");
                                JSONArray listArr = songJ.getJSONArray("list");
                                JSONObject song0 = listArr.getJSONObject(0);
                                final String songmid = song0.getString("songmid");
                                final String songname = song0.getString("songname");
                                System.out.println("====================================" + songmid);
                                String vkeyUri = "https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=1160855065&&loginUin=247990761&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8¬ice=0&platform=yqq&needNewCode=0&cid=205361747&&uin=247990761&songmid=" + songmid + "&filename=C400" + songmid + ".m4a&guid=6964837424";
                                getHttp.setResultHttpGet(new GetHttpRequest.ResultHttpGet() {
                                    @Override
                                    public void result(final String resultStr) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(resultStr);
                                                    JSONObject dataJson = jsonObject.getJSONObject("data");
                                                    JSONArray itemsArray = dataJson.getJSONArray("items");
                                                    JSONObject itemsJson = itemsArray.getJSONObject(0);
                                                    String vkey = itemsJson.getString("vkey");
                                                    if (vkey.equals("")) {
                                                        Map<String, String> map = new HashMap<>();
                                                        map.put("type", "1");
                                                        map.put("data", "没有找到《" + musicName + "》您换首歌试试");
                                                        chatAdapter.addData(map);
                                                        starSpeech("没有找到《" + musicName + "》您换首歌试试");
                                                    } else {
                                                        System.out.println("====================================" + vkey);
                                                        String musicUri = "http://aqqmusic.tc.qq.com/amobile.music.tc.qq.com/C400" + songmid + ".m4a?guid=6964837424&vkey=" + vkey + "&uin=0&fromtag=38";
                                                        System.out.println("====================================" + musicUri);
                                                        Map<String, String> map = new HashMap<>();
                                                        map.put("type", "1");
                                                        map.put("data", "正在播放《" + songname + "》");
                                                        starSpeech("正在播放" + songname);
                                                        songName = songname;
                                                        chatAdapter.addData(map);
                                                        mMediaPlayer.setDataSource(musicUri);
                                                        mMediaPlayer.prepare();
//                                                        mMediaPlayer.prepareAsync();
                                                        mMediaPlayer.start();

                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    }
                                });
                                getHttp.get(vkeyUri);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            getHttp.get(uri + musicName);
            Map<String, String> map = new HashMap<>();
            map.put("type", "0");
            map.put("data", String.valueOf(et_content.getText()));
            chatAdapter.addData(map);
            et_content.setText("");

        } else if (MarketUtil.containsStr(String.valueOf(et_content.getText())) != null) {
            Map<String, String> map = new HashMap<>();
            map.put("type", "0");
            map.put("data", String.valueOf(et_content.getText()));
            chatAdapter.addData(map);
            String dataResult = MarketUtil.containsStr(String.valueOf(et_content.getText()));
            Map<String, String> map2 = new HashMap<>();
            map2.put("type", "1");
            map2.put("data", dataResult);
            et_content.setText("");
            starSpeech(dataResult);
            chatAdapter.addData(map2);
//        }else if (String.valueOf(et_content.getText()).contains("菲菲")||String.valueOf(et_content.getText()).contains("飞飞")) {
        } else {
            getHR.setResultHttpGet(new GetHttpRequest.ResultHttpGet() {
                @Override
                public void result(final String resultStr) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject obj = new JSONObject(resultStr); // 最外层的JSONObject对象
                                String result = obj.getString("content"); // 通过content字段获取其所包含的字符串
                                Map<String, String> map = new HashMap<>();
                                map.put("type", "1");
                                map.put("data", result);
                                chatAdapter.addData(map);
                                starSpeech(result);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            getHR.get(String.valueOf(et_content.getText()));
            Map<String, String> map = new HashMap<>();
            map.put("type", "0");
            map.put("data", String.valueOf(et_content.getText()));
            chatAdapter.addData(map);
            et_content.setText("");
        } /*else{
            Map<String, String> map = new HashMap<>();
            map.put("type", "0");
            map.put("data", String.valueOf(et_content.getText()));
            chatAdapter.addData(map);
            String dataResult = MarketUtil.containsStr("无关");
            Map<String, String> map2 = new HashMap<>();
            map2.put("type", "1");
            map2.put("data", dataResult);
            et_content.setText("");
            starSpeech(dataResult);
            chatAdapter.addData(map2);
        }*/

    }

    public void noMusic() {
        et_content.setText("");
        Map<String, String> map = new HashMap<>();
        map.put("type", "1");
        map.put("data", "您当前没有播放音乐");
        chatAdapter.addData(map);
        starSpeech("您当前没有播放音乐");
    }
//    public void send(View v) {     // 按钮对应的 onclick 响应
//        if (String.valueOf(et_content.getText()).contains("暂停")) {
//            mMediaPlayer.reset();
//            et_content.setText("");
//        }
//        getHR.setResultHttpGet(new GetHttpRequest.ResultHttpGet() {
//            @Override
//            public void result(final String resultStr) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            JSONObject obj = new JSONObject(resultStr); // 最外层的JSONObject对象
//                            String result = obj.getString("content"); // 通过content字段获取其所包含的字符串
//                            Map<String, String> map = new HashMap<>();
//                            map.put("type", "1");
//                            map.put("data", result);
//                            chatAdapter.addData(map);
//                            starSpeech(result);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
//        if (!String.valueOf(et_content.getText()).equals("")) {
//            getHR.get(String.valueOf(et_content.getText()));
//            Map<String, String> map = new HashMap<>();
//            map.put("type", "0");
//            map.put("data", String.valueOf(et_content.getText()));
//            chatAdapter.addData(map);
//            et_content.setText("");
//        }
//    }

    /**
     * 初始化语音合成相关数据
     *
     * @Description:
     */
    public void starSpeech(String textVoice) {
//        String content = et_content.getText().toString().trim();

        // 2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, voiceName[0]);// 设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");// 设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");// 设置音量，范围0~100
//        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL); // 设置本地
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); // 设置云端
        //设置发音人资源路径
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
        // 设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        // 保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        // 如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, getFilesDir().getAbsolutePath() + "iflytek.pcm");
        // 3.开始合成
        mTts.startSpeaking(textVoice, mSynListener);
        // 合成监听器
        //

    }

    //获取发音人资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + voiceName[0] + ".jet"));
        return tempBuffer.toString();
    }

    /**
     * 初始化参数开始听写
     *
     * @Description:
     */
    private void starWrite() {
        // 2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        // 语音识别应用领域（：iat，search，video，poi，music）
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        // 接收语言中文
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 接受的语言是普通话
        mIat.setParameter(SpeechConstant.ACCENT, "mand4arin ");
        // 设置听写引擎（云端）
//        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL); // 本地
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); // 在线
        mIat.setParameter(ResourceUtil.ASR_RES_PATH, getResourceVoicePath());
        iatDialog.setListener(mRecognizerDialogListener);
//        iatDialog.show();
        Toast.makeText(getApplication(), "请开始说话…", Toast.LENGTH_SHORT).show();
        // 3.开始听写
        mIat.startListening(mRecoListener);
        // 听写监听器
    }

    // 语音识别资源
    private String getResourceVoicePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "iat/common.jet"));
        tempBuffer.append(";");
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "iat/sms_16k.jet"));
        //识别8k资源-使用8k的时候请解开注释
        return tempBuffer.toString();
    }

    /**
     * 语音听写监听
     */
    private RecognizerListener mRecoListener = new RecognizerListener() {
        // 听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
        // 一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        // 关于解析Json的代码可参见MscDemo中JsonParser类；
        // isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            printResult(results);
        }

        // 会话发生错误回调接口
        public void onError(SpeechError error) {
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            if (error.getErrorCode() == 10118) {
                Toast.makeText(getApplicationContext(), "你好像没有说话哦",
                        Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(), error.getPlainDescription(true),
                    Toast.LENGTH_SHORT).show();

        }// 获取错误码描述}

        // 开始录音
        public void onBeginOfSpeech() {
            Log.d(TAG, "开始说话");
            Toast.makeText(getApplicationContext(), "开始说话",
                    Toast.LENGTH_SHORT).show();
        }

        // 结束录音
        public void onEndOfSpeech() {
            Log.d(TAG, "说话结束");
            Toast.makeText(getApplicationContext(), "说话结束",
                    Toast.LENGTH_SHORT).show();
        }

        // 扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

        //音量
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            // TODO Auto-generated method stub
            Log.d(TAG, "当前说话音量大小" + volume);

        }

    };

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            Toast.makeText(getApplication(), error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
        }

    };
    /**
     * 语音合成监听
     */
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        // 会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
            if (error != null) {
                Log.d("code:", error.getErrorCode() + "");
            } else {
                Log.d("code:", "0");
            }
        }

        // 缓冲进度回调
        // percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
        }

        // 开始播放
        public void onSpeakBegin() {
        }

        // 暂停播放
        public void onSpeakPaused() {
        }

        // 播放进度回调
        // percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        // 恢复播放回调接口
        public void onSpeakResumed() {
        }

        // 会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };

    /**
     * 初始化语音合成监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @SuppressLint("ShowToast")
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                // showTip("初始化失败,错误码：" + code);
                Toast.makeText(getApplicationContext(), "初始化失败,错误码：" + code,
                        Toast.LENGTH_SHORT).show();
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_speech:
//                starSpeech();
                break;
            case R.id.bt_write:
                et_content.setText("");
                mIatResults.clear();
                starWrite();
                //当前音量
                currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                break;
            default:
                break;
        }

    }

    private void printResult(RecognizerResult results) {
        int intSn = 0;
        try {
            JSONObject resultText = new JSONObject(results.getResultString());
            intSn = resultText.getInt("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (intSn == 1) {
            String sn = null;
            StringBuilder text = new StringBuilder();
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(results.getResultString());
                JSONArray wsa = resultJson.getJSONArray("ws");
                for (int i = 0; i < wsa.length(); i++) {
                    JSONObject ws = wsa.getJSONObject(i);
                    JSONArray cwa = ws.getJSONArray("cw");
                    JSONObject cw = cwa.getJSONObject(0);
                    text.append(cw.getString("w"));
                }
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults.put(sn, text.toString());

            StringBuilder resultBuffer = new StringBuilder();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }
            et_content.setText(resultBuffer.toString());
            et_content.setSelection(et_content.length());
            // 发送
            bigger(null);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        }
    }
}
