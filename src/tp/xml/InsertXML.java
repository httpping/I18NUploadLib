package tp.xml;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.util.ExceptionUtil;
import okhttp3.*;
import tp.xml.utils.DateUtils;
import tp.xml.utils.FileParse;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.rmi.server.RemoteRef;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class InsertXML extends AnAction {
    private Project project;
    private OkHttpClient okHttpClient;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        System.out.println("start ...");
        String filepath = System.getProperty("user.dir") ;
        filepath = project.getBasePath();
        System.out.println(filepath);

        upload(filepath);

    }



    public void upload(String rootPath) {
        System.out.println("upload");
        //及分析路徑
        try {
            HashMap<String, String> map = FileParse.parse(rootPath);

            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String path = map.get(key);
                sendRequest(key, path);
            }
            MessageDialogBuilder.yesNo("提示","所有数据已传输后台，请确保上传前已经rebuild").show();
        } catch (Exception e) {
            MessageDialogBuilder.yesNo("错误提醒", ExceptionUtil.getThrowableText(e)).show();
        }

    }

    public void sendRequest(String lang, String path) {
        MultipartBody body = createMulipart(path, lang);

        String HOST = "http://10.32.2.253:8090/string/upload";
        Request request = new Request.Builder()
                .url(HOST)//请求接口。如果需要传参拼接到接口后面。
                .post(body)
                .build();//创建Request 对象

        okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {

            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("eeerror");
                MessageDialogBuilder.yesNo("错误提醒", ExceptionUtil.getThrowableText(e)).show();
            }

            public void onResponse(Call call, Response response) throws IOException {
                String text = response.body().string();
                System.out.println(text);
            }
        });//得到Response 对象
    }

    public MultipartBody createMulipart(String path, String lang) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        builder.addFormDataPart("domain", "rg");
        builder.addFormDataPart("appVersion", DateUtils.format(new Date()));
        builder.addFormDataPart("lang", lang);

        String contentType = "application/xml";
        builder.addFormDataPart("file", "file.xml", RequestBody.create(MediaType.parse(contentType), new File(path)));
        MultipartBody requestBody = builder.build();
        return requestBody;
    }
}
