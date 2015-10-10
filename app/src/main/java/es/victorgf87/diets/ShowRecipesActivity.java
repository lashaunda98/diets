package es.victorgf87.diets;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.victorgf87.diets.classes.recipes.Recipe;
import es.victorgf87.diets.storers.StorerFactory;

public class ShowRecipesActivity extends AppCompatActivity
{
    @Bind(R.id.activity_show_recipes_listViewRecipes)ListView list;


    private class Adapt extends BaseAdapter
    {
        private List<Recipe>recipes;
        public Adapt(List<Recipe>recipes)
        {
            this.recipes=recipes;
        }

        @Override
        public int getCount() {
            return recipes.size();
        }

        @Override
        public Recipe getItem(int position)
        {
            return recipes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LinearLayout ll=new LinearLayout(ShowRecipesActivity.this);
            ll.setPadding(0, 50, 0, 50);

            TextView txt=new TextView(ShowRecipesActivity.this);
            txt.setText(recipes.get(position).getName());
            ll.addView(txt);

            return ll;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipes);
        ButterKnife.bind(this);
        List<Recipe>recipes=StorerFactory.create(this).getAllRecipes();
        Collections.sort(recipes);
        list.setAdapter(new Adapt(recipes));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Recipe recipe=((Adapt)list.getAdapter()).getItem(position);
                Dialog dialog=new Dialog(ShowRecipesActivity.this);
                dialog.setContentView(R.layout.detailed_recipe_layout);
                TextView txtDialogTitle=ButterKnife.findById(dialog, R.id.detailed_recipe_layout_txtTitle);
                TextView txtDialogElaboration=ButterKnife.findById(dialog,R.id.detailed_recipe_layout_txtElaboration);
                ListView listViewDialogIngredients=ButterKnife.findById(dialog,R.id.detailed_recipe_layout_ingredientsList);
                List<String>ingredientsList=recipe.ingredientsToStringList();
                //TextView txtDialogIngredients=ButterKnife.findById(dialog,R.id.detailed_recipe_layout_txtIngredients);
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(ShowRecipesActivity.this,android.R.layout.simple_list_item_1,ingredientsList);

                txtDialogTitle.setText(recipe.getName());
                txtDialogElaboration.setText(recipe.getElaboration());
                listViewDialogIngredients.setAdapter(arrayAdapter);



                dialog.show();
                int a=3;
                int b=a;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_recipes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id=item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id==R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
