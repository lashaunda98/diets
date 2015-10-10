package es.victorgf87.diets;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.dacer.androidcharts.LineView;
import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.victorgf87.diets.classes.DrankWaterGlass;
import es.victorgf87.diets.classes.exerciseactivities.ExerciseActivity;
import es.victorgf87.diets.storers.StorerFactory;

public class MainActivity extends AppCompatActivity
{
    @Bind(R.id.main_activity_btnAddGlass)Button btnAddGlass;
    @Bind(R.id.main_activity_glasses_linear_chart)LineView lineViewGlassesChart;

    @OnClick(R.id.main_activity_btnAddGlass)
    void addGlassClick()
    {
        DrankWaterGlass newGlass=new DrankWaterGlass();
        StorerFactory.create(MainActivity.this).addDrankWaterGlass(newGlass);

        paintGlassesChart();
    }

    private void paintGlassesChart()
    {

        List<DrankWaterGlass>glassesCollection=StorerFactory.create(MainActivity.this).getAllGlasses();
        if(glassesCollection.size()==0)return;

        ArrayList<String>bottomTexts=new ArrayList<>();
        ArrayList<ArrayList<Integer>> dataList=new ArrayList<>();

        String textBefore=getDayMonthString(glassesCollection.get(0).getDate());

        int count=0;
        for(DrankWaterGlass glass:glassesCollection)
        {
            String currentText=getDayMonthString(glass.getDate());
            if(currentText.equals(textBefore))
            {
                count++;
            }
            else {
                bottomTexts.add(textBefore);
                ArrayList<Integer>newNumber=new ArrayList<>();
                newNumber.add(count);
                dataList.add(newNumber);
                count=0;
            }
            textBefore=currentText;
        }

        bottomTexts.add(textBefore);
        ArrayList<Integer>newNumber=new ArrayList<>();
        newNumber.add(count);
        dataList.add(newNumber);



        lineViewGlassesChart.setDrawDotLine(false); //optional
        lineViewGlassesChart.setShowPopup(LineView.SHOW_POPUPS_All); //optional
        lineViewGlassesChart.setBottomTextList(bottomTexts);
        lineViewGlassesChart.setDataList(dataList);




    }

    @Override
    protected void onResume() {
        super.onResume();
        paintGlassesChart();

    }




    private String getDayMonthString(Date date)
    {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        String day=""+calendar.get(Calendar.DAY_OF_MONTH);
        String month=""+(calendar.get(Calendar.MONTH)+1);
        return day+"/"+month;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        List<ExerciseActivity> activities= StorerFactory.create(this).getActivitiesList();

        

    }

    private void wolfram()
    {


        HandlerThread thread=new HandlerThread("name");
        thread.start();
        Handler handler=new Handler(thread.getLooper());
        handler.post(new Runnable(){

            @Override
            public void run() {
                String wolfram_api_key=BuildConfig.WOLFRAM_API_KEY;
                String input = "falafel";
                // The WAEngine is a factory for creating WAQuery objects,
                // and it also used to perform those queries. You can set properties of
                // the WAEngine (such as the desired API output format types) that will
                // be inherited by all WAQuery objects created from it. Most applications
                // will only need to crete one WAEngine object, which is used throughout
                // the life of the application.
                WAEngine engine = new WAEngine();

                // These properties will be set in all the WAQuery objects created from this WAEngine.
                engine.setAppID(wolfram_api_key);
                //engine.addFormat("plaintext");


                // Create the query.
                WAQuery query = engine.createQuery();

                // Set properties of the query.
                query.setInput(input);

                try {
                    // For educational purposes, print out the URL we are about to send:
                    System.out.println("Query URL:");
                    System.out.println(engine.toURL(query));
                    System.out.println("");

                    // This sends the URL to the Wolfram|Alpha server, gets the XML result
                    // and parses it into an object hierarchy held by the WAQueryResult object.
                    WAQueryResult queryResult = engine.performQuery(query);

                    if (queryResult.isError()) {
                        System.out.println("Query error");
                        System.out.println("  error code: " + queryResult.getErrorCode());
                        System.out.println("  error message: " + queryResult.getErrorMessage());
                    } else if (!queryResult.isSuccess()) {
                        System.out.println("Query was not understood; no results available.");
                    } else {
                        // Got a result.
                        System.out.println("Successful query. Pods follow:\n");
                        for (WAPod pod : queryResult.getPods()) {
                            if (!pod.isError()) {
                                System.out.println(pod.getTitle());
                                System.out.println("------------");
                                for (WASubpod subpod : pod.getSubpods()) {
                                    for (Object element : subpod.getContents()) {
                                        if (element instanceof WAPlainText) {
                                            System.out.println(((WAPlainText) element).getText());
                                            System.out.println("");
                                        }
                                    }
                                }
                                System.out.println("");
                            }
                        }
                        // We ignored many other types of Wolfram|Alpha output, such as warnings, assumptions, etc.
                        // These can be obtained by methods of WAQueryResult or objects deeper in the hierarchy.
                    }
                } catch (WAException e) {
                    e.printStackTrace();
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
        switch(id)
        {
            case R.id.menu_main_action_go_weights:
                changeActivity(WeightsActivity.class);
                return true;

            case R.id.menu_main_action_go_recipeList:
                changeActivity(ShowRecipesActivity.class);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void changeActivity(Class klass)
    {
        Intent inte=new Intent();
        inte.setClass(this,klass);
        startActivity(inte);
    }
}
