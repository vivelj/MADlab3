package com.example.mlr3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class task1 extends AppCompatActivity {
    private ImageView picture;
    private Button btn1;
    private EditText answr1;
    boolean firstPress = false;
    SharedPreferences sharedPreferences;
    private String myStars = "https://aisazuk.github.io/stars/index.html";
    private String myHost = "https://aisazuk.github.io";
    private ArrayList<String> lastNames = new ArrayList<>();
    private ArrayList<String> urls = new ArrayList<>();
    private int numberOfImage = 0;
    private String context = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task1);
 //     getSupportActionBar().setTitle("Угадай кто?");
        picture = (ImageView) findViewById(R.id.Who);
        btn1 = (Button) findViewById(R.id.check);
        answr1 = (EditText) findViewById(R.id.answer);
        sharedPreferences = getSharedPreferences("com.example.mlr3",MODE_PRIVATE);

        //если информация не была скачана и сохарнена в память телефона,
        // то скачиваем, иначе достаем из памяти телефона
        Log.i("Not From SHared","Preferences");
        DownloadTask task = new DownloadTask();
        try
        {
            context = task.execute(myStars).get();
            getResources(context);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.i("Error download","MYERROR error download image");
        }
    }

    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_up,R.anim.slide_in);
    }

    public void onClick (View view)
    {
        try
        {   //первое нажатие на кнопку: выводит картинку и поле для ввода фамилии
            if (!firstPress)
            {
                playGame();
                btn1.setText("Ответ");
                answr1.setVisibility(View.VISIBLE);
                firstPress = true;
            }
            //второе и последющие нажатия на кнопку проверяют
            // введенную фамилию и выводят новую картинку
            else
            {
                String answer = answr1.getText().toString().toUpperCase();
                Log.i("Answr",answer + "        " + lastNames.get(numberOfImage));

                if (!(answer.equals(""))) {
                    if (answer.equals(lastNames.get(numberOfImage).toUpperCase())) {
                        Toast toast = Toast.makeText(this, "Правильный ответ!", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(this, "Неправильный ответ", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    playGame();
                    answr1.setText("");
                }
                else
                {
                    Toast toast = Toast.makeText(this, "Пустое поле", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void getResourcesMem()
    {//из памяти
        int col = 0;
        while(sharedPreferences.contains("Image"+col))
        {
            urls.add(sharedPreferences.getString("Image"+col,"empty"));
            lastNames.add(sharedPreferences.getString("LastName"+col,"empty"));
            Log.i("Memory",urls.get(col) + " " + lastNames.get(col));
            col++;
        }
    }

    protected  void getResources(String cont)
    {//Ищем
//
        //ищем ссылки на изображение
        String start = " src=";
        String finish = "width=150 height=150>";

        Pattern pattern = Pattern.compile(start + "(.*?)" + finish);
        Matcher matcher = pattern.matcher(cont);
        while(matcher.find())
        {
            String[] splitContent2 = matcher.group(1).split(" ");
            urls.add(splitContent2[0]);
        }
        for(int i=0;i<urls.size();i++){
            sharedPreferences.edit().putString("Image"+i,urls.get(i)).apply();
        }
        //ищем фамилии
        start = "<h1>";
        finish = "</h1>";

        pattern = Pattern.compile(start + "(.*?)" + finish);
        matcher = pattern.matcher(cont);
        while(matcher.find())
        {
            String[] splitContent2 = matcher.group(1).split(" ");
            lastNames.add(splitContent2[0]);
        }
        for(int i=0;i<lastNames.size();i++){
            sharedPreferences.edit().putString("LastName"+i,lastNames.get(i)).apply();
        }
    }

    private void playGame()
    {
        try
        {//в случайном порядке изображения будут появляться
            numberOfImage = (int) (Math.random() * urls.size());

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            ImageRequest imageRequest = new ImageRequest(
                    urls.get(numberOfImage), // Image URL
                    new Response.Listener<Bitmap>()
                    { // Bitmap listener
                        @Override
                        public void onResponse(Bitmap response)
                        {
                            // Do something with response
                            picture.setImageBitmap(response);
                        }
                    },
                    0, // Image width
                    0, // Image height
                    ImageView.ScaleType.CENTER_CROP, // Image scale type
                    Bitmap.Config.RGB_565, //Image decode configuration
                    new Response.ErrorListener()
                    { // Error listener
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            // Do something with error response
                            Log.i("JSON error:", error.getMessage());
                            error.printStackTrace();
                        }
                    }
            );
            // Add ImageRequest to the RequestQueue
            requestQueue.add(imageRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static class DownloadTask extends AsyncTask<String,Void, String>
    {
        @Override
        protected String doInBackground(String... strings)
        {
            StringBuilder result = new StringBuilder();
            URL url = null;
            HttpsURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = bufferedReader.readLine();
                while (line!=null)
                {
                    result.append(line);
                    line = bufferedReader.readLine();
                }

            } catch (IOException e)
            {
                e.printStackTrace();
            } finally
            {
                if (urlConnection!=null)
                    urlConnection.disconnect();
            }
            return  result.toString();
        }
    }
}