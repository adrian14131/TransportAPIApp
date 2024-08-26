package pl.ozog.transportapiapp.connectiontasks;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.annotation.Documented;

import pl.ozog.transportapiapp.generator.Generator;

public class PageTask extends AsyncTask<String, Void, Document> {

    private static final int MB = 10*1024*1024;


    @Override
    protected Document doInBackground(String... strings) {
        Document doc = null;
        if(strings.length>0){
            try {
                Connection.Response response = Jsoup.connect(strings[0])
                        .method(Connection.Method.POST)
                        .maxBodySize(MB)
                        .execute();
                Generator.downloadedSize += response.bodyAsBytes().length;
                Generator.numberOfLoadingPages++;
                doc = Jsoup.parse(response.body());

                return response.parse();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(doc==null){
            doc = Jsoup.parse("<h1></h1>");
        }

        return doc;
    }
}
