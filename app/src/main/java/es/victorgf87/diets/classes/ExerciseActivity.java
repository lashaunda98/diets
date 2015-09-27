package es.victorgf87.diets.classes;

/**
 * Created by Víctor on 26/09/2015.
 */
public class ExerciseActivity
{
    private Integer id;
    private String name;
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
