package es.victorgf87.diets;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.dacer.androidcharts.LineView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.victorgf87.diets.classes.WeightRegister;
import es.victorgf87.diets.storers.StorerFactory;

public class WeightsActivity extends AppCompatActivity
{
    @Bind(R.id.activity_weights_line_view) LineView lineView;
    @Bind(R.id.activity_weights_line_chart_scroll)HorizontalScrollView linearChartScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weights);
        ButterKnife.bind(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weights, menu);
        return true;
    }


    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for your activity to start interacting with the user.
     * This is a good place to begin animations, open exclusive-access devices
     * (such as the camera), etc.
     * <p/>
     * <p>Keep in mind that onResume is not the best indicator that your activity
     * is visible to the user; a system window such as the keyguard may be in
     * front.  Use {@link #onWindowFocusChanged} to know for certain that your
     * activity is visible to the user (for example, to resume a game).
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestoreInstanceState
     * @see #onRestart
     * @see #onPostResume
     * @see #onPause
     */
    @Override
    protected void onResume() {
        super.onResume();
        paintLineChart();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        paintLineChart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.menu_weights_add_weight:
                showNewWeightDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void showNewWeightDialog()
    {
        final Dialog dial=new Dialog(WeightsActivity.this);
        dial.setContentView(R.layout.dialog_new_weight_layout);
        dial.show();


        final EditText dialogTxtWeight=ButterKnife.findById(dial,R.id.dialog_new_weight_txtWeight);
        Button dialogBtnOk=ButterKnife.findById(dial, R.id.dialog_new_weight_btnOk);

        dialogBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeightRegister newRegister=new WeightRegister(Double.valueOf(dialogTxtWeight.getText().toString()));
                StorerFactory.create(WeightsActivity.this).storeWeight(newRegister);
                Toast.makeText(WeightsActivity.this,"Hecho",Toast.LENGTH_LONG).show();
                dial.dismiss();
            }
        });

        dial.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                paintLineChart();
            }
        });


    }

    private void paintLineChart()
    {
        List<WeightRegister> weightList=StorerFactory.create(this).getAllWeights();
        if(weightList.size()==0)
            return;

        ArrayList<ArrayList<Integer>>dataInts=new ArrayList<ArrayList<Integer>>();
        ArrayList<String>bottomStrings=new ArrayList<String>();

        ArrayList<Integer> actualWeights=new ArrayList<>();
        dataInts.add(actualWeights);

        int i=0;
        for(WeightRegister reg: weightList)
        {
            //ArrayList<Integer>newInt=new ArrayList<>();
            actualWeights.add((int) (reg.getWeight() * 1000));


            String newstring = new SimpleDateFormat("dd/MM HH:mm").format(reg.getDate());
            bottomStrings.add(newstring);
        }

        lineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a=3;
                int b=a;
            }
        });



        lineView.setDrawDotLine(true); //optional
        lineView.setShowPopup(LineView.SHOW_POPUPS_All); //optional

        lineView.setBottomTextList(bottomStrings);
        lineView.setDataList(dataInts);

        linearChartScroll.post(new Runnable() {
            @Override
            public void run() {
                linearChartScroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });

    }
}
