package es.victorgf87.diets.classes.recipes;

import org.simpleframework.xml.Attribute;

/**
 * Created by Víctor on 27/09/2015.
 */
public class Ingredient
{
    @Attribute
    private Integer id;

    @Attribute
    private Integer weight;

    @Attribute
    private String name;


    public Ingredient(String name, Integer weight) {
        this.weight = weight;
        this.name = name;
    }

    public Ingredient(Integer id, String name, Integer weight)
    {
        this.id = id;
        this.name = name;
        this.weight = weight;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getWeight() {
        return weight;
    }
}
