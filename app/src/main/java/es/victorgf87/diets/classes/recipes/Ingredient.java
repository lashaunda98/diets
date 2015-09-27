package es.victorgf87.diets.classes.recipes;

/**
 * Created by Víctor on 27/09/2015.
 */
public class Ingredient
{
    private Integer id;
    private Integer weight;
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
