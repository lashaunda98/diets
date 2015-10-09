package es.victorgf87.diets.classes.recipes;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by Victor on 09/10/2015 - 19:37.
 */
@Root
public class EquivalentIngredient
{
    @Attribute
    private Integer id;
    @Attribute
    private Integer id1;
    @Attribute
    private Integer id2;

    public EquivalentIngredient() {
    }

    public EquivalentIngredient(Integer id1, Integer id2) {
        this.id1=id1;
        this.id2=id2;
    }

    public Integer getId1() {
        return id1;
    }

    public Integer getId2() {
        return id2;
    }

    public Integer getId() {
        return id;
    }
}
