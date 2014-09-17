package com.upscapp.pratap.upsc;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MyActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        System.out.println(mTitle);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));




        new getUrl().execute();



    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.my, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MyActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }



    protected class getUrl extends AsyncTask
    {

        ArrayAdapter ad ;

        public getUrl() {
            super();

        }

        @Override
        protected void onPreExecute() {
        }


        @Override
        public String doInBackground(Object[] objects) {

            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpGet httppost = new HttpGet("http://upsc.herokuapp.com/api/v1/books.json?category=CSAT&page=1");

            InputStream inputStream = null;
            String result = null;
            try {
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                result = sb.toString();

                return result;
            } catch (Exception e) {
                // Oops
            }
            finally
            {
                try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
            }

            return result;
        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            System.out.println(o);
            JSONArray jArray=null;

            try {
                JSONObject jsonObj =  new JSONObject((String)o);
                jArray = jsonObj.getJSONArray("books");
                System.out.println(jArray.get(0));



            } catch (JSONException e) {
                e.printStackTrace();
            }

            final ArrayList<String> arrayList = new ArrayList<String>();

            final ArrayList<String> arrayUrl = new ArrayList<String>();


            for(int i =0 ; i < jArray.length();i++)
            {
                String str = null;
                try {
                    str = (String) jArray.get(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String fileName = str.substring( str.lastIndexOf('/')+1, str.length() );
                String fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'));
                arrayList.add(fileNameWithoutExtn);

                arrayUrl.add(str);
                System.out.print(str);

            }




            ListView l = (ListView)findViewById(R.id.ListView);

            System.out.print(arrayList);
            ad = new ArrayAdapter<String>(MyActivity.this,android.R.layout.simple_list_item_1,android.R.id.text1,arrayList);
            l.setAdapter(ad);



            l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    // Show Alert
                    Toast.makeText(MyActivity.this,
                            "Downloading..." + arrayUrl.get(i), Toast.LENGTH_LONG)
                            .show();


                    System.out.print(arrayUrl.get(i).getClass());


                    String url = arrayUrl.get(i);
                    String fileName = arrayList.get(i);


                    String [] paramers = new String[0];
                    paramers = new String[]
                            {
                                    url,fileName,"A"

                            };

                    new downloadTask().execute(paramers);

                }
            });

        }


        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);



        }

    }


    public class downloadTask extends AsyncTask<String,String,String>
    {

        @Override
        protected String doInBackground(String... strings) {



            for (int i=0;i<strings.length;i++)
            {
                System.out.println(strings[i]);
            }
            HttpClient httpClient = new DefaultHttpClient();



            HttpGet httpGet = null;
            httpGet = new HttpGet(strings[0]);
            HttpResponse response = null;

            try {
                response = httpClient.execute(httpGet);
                HttpEntity entity = (HttpEntity) response.getEntity();
                File outputFile = null;
                if(entity!=null)
                {



                    System.out.println(Environment.getExternalStorageDirectory()+"/"+ R.string.foldername);

                    File folder = new File(Environment.getExternalStorageDirectory() + "/"+ "Sample_pdfs");
                    boolean success = true;
                    if (!folder.exists()) {
                        success = folder.mkdir();

                        System.out.println("Folder not  Exist");
                    }
                    else
                    {
                        System.out.println("Folder Exist");
                    }


                    //create folder
                    if (success) {

                        outputFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ "Sample_pdfs" + "/" + strings[1] + ".pdf");
                        if (outputFile.exists()) {
                            outputFile.createNewFile();
                            System.out.println("File Exist");

                        }
                        else
                        {
                            System.out.println("File not Exist");


                            InputStream inputStream = entity.getContent();
                            FileOutputStream fileOutputStream;
                            fileOutputStream = new FileOutputStream(outputFile);
                            int read = 0;
                            byte[] bytes = new byte[1024];
                            while ((read = inputStream.read(bytes)) != -1) {
                                fileOutputStream.write(bytes, 0x0, read);
                            }
                            fileOutputStream.close();
                            System.out.println("Downloded " + outputFile.length() + " bytes. " + entity.getContentType());

                        }

                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"Sample_pdfs"+"/"+ strings[1]+".pdf" );

                        System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"Sample_pdfs"+"/"+ strings[1]+".pdf");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);

                    }
Object foldername;
                }


                return null;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String o) {

            super.onPostExecute((String) o);

            Toast.makeText(MyActivity.this,
                    "Downloded", Toast.LENGTH_LONG)
                    .show();


        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
