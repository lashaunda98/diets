package es.victorgf87.diets.classes.exerciseactivities;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by Víctor on 26/09/2015.
 */
@Root(name="activity", strict = false)
public class ExerciseActivity
{
    @Attribute
    private Integer id;
    @Attribute
    private String name;
    @Attribute(name="loss")
    private Integer calsLoss;

    public ExerciseActivity() {
        this.id=null;
        this.name=null;
        this.calsLoss=null;
    }

    public ExerciseActivity(Integer calsLoss, String name) {
        this.calsLoss = calsLoss;
        this.name = name;
    }

    public ExerciseActivity(Integer id, String name, Integer calsLoss) {
        this.calsLoss = calsLoss;
        this.id = id;
        this.name = name;
    }

    public Integer getCalsLoss() {
        return calsLoss;
    }

    public void setCalsLoss(Integer calsLoss) {
        this.calsLoss = calsLoss;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
