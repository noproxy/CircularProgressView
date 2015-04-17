package me.toxz.circularprogressview.sample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import me.toxz.circularprogressview.library.CircularProgressView;


public class MainActivity extends Activity {
    static CircularProgressView mCircularProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final DownLoadSigTask task = new DownLoadSigTask();

        mCircularProgressView = (CircularProgressView) findViewById(R.id.circularProgressView);
        mCircularProgressView.setOnStateListener(new CircularProgressView.OnStatusListener() {
            @Override
            public void onStatus(CircularProgressView.Status status) {
                switch (status) {
                    case START:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Starting recording", Toast.LENGTH_SHORT).show();
                            }
                        });
                        task.execute();
                        break;
                    case PROGRESS:
                        task.cancel(true);
                        mCircularProgressView.reset();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "recording stopped", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case END:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "recording complete", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static class DownLoadSigTask extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected String doInBackground(final String... args) {

            //Creating dummy task and updating progress

            for (int i = 0; i <= 100; i++) {
                try {
                    Thread.sleep(50);

                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                publishProgress(i);

            }


            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {

            //publishing progress to progress arc

            mCircularProgressView.setProgress(progress[0]);
        }


    }
}
