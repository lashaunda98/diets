package es.victorgf87.diets.classes.recipes;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Víctor on 27/09/2015.
 */
@Root(name="Recipe", strict = false)
public class Recipe
{
    private Integer id;
    private String name, elaboration;
    private Integer people;

    @ElementList(entry="Ingredient",inline = true)
    private List<Ingredient> ingredients;

    public Recipe(String elaboration, Integer id, String name, Integer people) {
        ingredients=new ArrayList<>();
        this.elaboration = elaboration;
        this.id = id;
        this.name = name;
        this.people = people;
    }

    public Recipe(String elaboration, String name, Integer people) {
        this.elaboration = elaboration;
        this.name = name;
        this.people = people;
        ingredients=new ArrayList<>();
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Ingredient> getIngredients()
    {
        return ingredients;
    }

    public List<Integer> getIngredientIds()
    {
        List<Integer> ret=new ArrayList<Integer>();
        for(Ingredient ig: ingredients)
        {
            ret.add(ig.getId());
        }
        return ret;
    }

    public void addIngredient(Ingredient ingredient)
    {
        ingredients.add(ingredient);

    }

    public String getElaboration() {
        return elaboration;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPeople() {
        return people;
    }
}



