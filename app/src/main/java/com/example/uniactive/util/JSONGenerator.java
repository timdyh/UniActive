package com.example.uniactive.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class JSONGenerator {

    private static RequestQueue rq = null;
    private static final String VOLLEY_TAG = "19260817";
    private static final String REMIND = "naive";

    private static String md5Encode32(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    content.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static void cancelAllRequests() {
        if (rq != null) {
            rq.cancelAll(VOLLEY_TAG);
        }
    }

    public static void stopReminding() {
        if (rq != null) {
            rq.cancelAll(REMIND);
        }
    }

    private static void communicate(JSONObject jo, Context appContext,
                                    Response.Listener<JSONObject> listener,
                                    Response.ErrorListener errorListener) {
        final String url = "http://114.115.134.188:12345";
        if (rq == null) {
            rq = Volley.newRequestQueue(appContext);
        }
        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, url,
                jo, listener, errorListener);
        jr.setTag(VOLLEY_TAG);
        rq.add(jr);
    }

    public static void login(String email, String password, Context appContext,
                             Response.Listener<JSONObject> listener,
                             Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        password = md5Encode32(password);
        try {
            jo.put("request", "login");
            jo.put("email", email);
            jo.put("password", password);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void register(String email, String password, int gender, String nickname,
                                String label1, String label2, String label3, String label4, String label5,
                                String verifyCode, Context appContext,
                                Response.Listener<JSONObject> listener,
                                Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        password = md5Encode32(password);
        try {
            jo.put("request", "register");
            jo.put("email", email);
            jo.put("password", password);
            jo.put("gender", gender);
            jo.put("nickname", nickname);
            jo.put("label1", label1);
            jo.put("label2", label2);
            jo.put("label3", label3);
            jo.put("label4", label4);
            jo.put("label5", label5);
            jo.put("verify_code", verifyCode);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void modifyInfo(String email, String newNickname, int gender, String img,
                                  Context appContext,
                                  Response.Listener<JSONObject> listener,
                                  Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "modify_info");
            jo.put("email", email);
            jo.put("gender", gender);
            jo.put("nickname", newNickname);
            jo.put("img", img);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void queryJoinedActs(String email, Context appContext,
                                       Response.Listener<JSONObject> listener,
                                       Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "query_joined");
            jo.put("email", email);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void join(String email, int act_id, Context appContext,
                            Response.Listener<JSONObject> listener,
                            Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "join");
            jo.put("email", email);
            jo.put("act_id", act_id);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void quit(String email, int act_id, Context appContext,
                            Response.Listener<JSONObject> listener,
                            Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "quit");
            jo.put("email", email);
            jo.put("act_id", act_id);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void comment(String email, int act_id, int score, String com,
                               Context appContext,
                               Response.Listener<JSONObject> listener,
                               Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "comment");
            jo.put("email", email);
            jo.put("act_id", act_id);
            jo.put("score", score);
            jo.put("comment", com);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void delComment(String email, int act_id, Context appContext,
                                  Response.Listener<JSONObject> listener,
                                  Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "user_del_comment");
            jo.put("email", email);
            jo.put("act_id", act_id);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void hold(String email, String name, long start_time,
                            long end_time, int max_num, String introduction, String place,
                            String label1, String label2, String label3, String img1,
                            Context appContext,
                            Response.Listener<JSONObject> listener,
                            Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "hold");
            jo.put("email", email);
            jo.put("name", name);
            jo.put("start_time", start_time);
            jo.put("end_time", end_time);
            jo.put("max_num", max_num);
            jo.put("introduction", introduction);
            jo.put("place", place);
            jo.put("label1", label1);
            jo.put("label2", label2);
            jo.put("label3", label3);
            jo.put("img1", img1);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void modify(String email, int act_id, String name, long start_time,
                              long end_time, int max_num, String introduction, String place,
                              String label1, String label2, String label3, String img1,
                              Context appContext,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "modify");
            jo.put("email", email);
            jo.put("act_id", act_id);
            jo.put("name", name);
            jo.put("start_time", start_time);
            jo.put("end_time", end_time);
            jo.put("max_num", max_num);
            jo.put("introduction", introduction);
            jo.put("place", place);
            jo.put("label1", label1);
            jo.put("label2", label2);
            jo.put("label3", label3);
            jo.put("img1", img1);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void cancel(int act_id, Context appContext,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "cancel");
            jo.put("act_id", act_id);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void queryHeld(String email, Context appContext,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "query_held");
            jo.put("email", email);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getDetail(String email, int act_id, Context appContext,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "get_detail");
            jo.put("act_id", act_id);
            jo.put("email", email);
            System.out.println(jo.toString());
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void logoff(String email, String password, Context appContext,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        password = md5Encode32(password);
        try {
            jo.put("request", "logoff");
            jo.put("email", email);
            jo.put("password", password);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void changepsw(String email, String old_psw, String new_psw,
                                Context appContext,
                                Response.Listener<JSONObject> listener,
                                Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        old_psw = md5Encode32(old_psw);
        new_psw = md5Encode32(new_psw);
        try {
            jo.put("request", "changepsw");
            jo.put("email", email);
            jo.put("old_psw", old_psw);
            jo.put("new_psw", new_psw);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void searchByKeyword(String keyword, Context appContext,
                                       Response.Listener<JSONObject> listener,
                                       Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "search");
            jo.put("keyword", keyword);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void if_user_join(String email, int act_id, Context appContext,
                                    Response.Listener<JSONObject> listener,
                                    Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "if_user_join");
            jo.put("act_id", act_id);
            jo.put("email", email);
            System.out.println(jo.toString());
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void queryRemindActs(String email, Context appContext,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "query_remind");
            jo.put("email", email);
            final String url = "http://114.115.134.188:12345";
            if (rq == null) {
                rq = Volley.newRequestQueue(appContext);
            }
            JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, url,
                    jo, listener, errorListener);
            jr.setTag(REMIND);
            rq.add(jr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getAll(String email, Context appContext,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("email", email);
            jo.put("request", "all_activity");
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void searchByLabel(String label, Context appContext,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "search_label");
            jo.put("label", label);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getActComments(int act_id, Context appContext,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "get_act_comments");
            jo.put("act_id", act_id);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void addDiscuss(String email, int act_id, String content,
                                  Context appContext,
                                  Response.Listener<JSONObject> listener,
                                  Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "add_discuss");
            jo.put("email", email);
            jo.put("act_id", act_id);
            jo.put("content", content);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void modifyDiscuss(int disc_id, String content,
                                     Context appContext,
                                     Response.Listener<JSONObject> listener,
                                     Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "modify_discuss");
            jo.put("disc_id", disc_id);
            jo.put("content", content);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void delDiscuss(int disc_id, Context appContext,
                                  Response.Listener<JSONObject> listener,
                                  Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "del_discuss");
            jo.put("disc_id", disc_id);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void queryDiscuss(int act_id, Context appContext,
                                    Response.Listener<JSONObject> listener,
                                    Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "query_discuss");
            jo.put("act_id", act_id);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void addFav(String email, int act_id, Context appContext,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "add_fav");
            jo.put("email", email);
            jo.put("act_id", act_id);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void cancelFav(String email, int act_id, Context appContext,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "cancel_fav");
            jo.put("email", email);
            jo.put("act_id", act_id);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void queryFav(String email, Context appContext,
                                Response.Listener<JSONObject> listener,
                                Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "query_fav");
            jo.put("email", email);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void checkFav(String email, int act_id, Context appContext,
                                Response.Listener<JSONObject> listener,
                                Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "check_fav");
            jo.put("email", email);
            jo.put("act_id", act_id);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void feedback(String email, String opinion, Context appContext,
                                Response.Listener<JSONObject> listener,
                                Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "feedback");
            jo.put("provider", email);
            jo.put("opinion", opinion);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void sendVerifyCode(String email, Context appContext,
                                      Response.Listener<JSONObject> listener,
                                      Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "send_verify_code");
            jo.put("email", email);
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置密码
     * status: <br>
     * 1 -- 成功；<br>
     * 3 -- 未发送验证码或验证码过期；<br>
     * 2 -- 验证码错误；<br>
     * -1 -- 系统错误
     */
    public static void resetPassword(String email, String verifyCode,
                                     String newPassword, Context appContext,
                                     Response.Listener<JSONObject> listener,
                                     Response.ErrorListener errorListener) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "reset_password");
            jo.put("email", email);
            jo.put("verify_code", verifyCode);
            jo.put("new_psw", md5Encode32(newPassword));
            communicate(jo, appContext, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
