package es.victorgf87.diets.classes.recipes;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by Víctor on 27/09/2015.
 */
@Root(name="Ingredient", strict = false)
public class Ingredient
{
    @Attribute(name="id")
    private Integer id;

    @Attribute(name="weight",required=false)
    private Integer weight;

    @Attribute(name="name",required=false)
    private String name;

    public Ingredient()
    {

    }

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
