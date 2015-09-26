package es.victorgf87.diets.storers;

import java.util.List;

import es.victorgf87.diets.classes.ExerciseActivity;
import es.victorgf87.diets.classes.WeightRegister;

/**
 * Created by Víctor on 26/09/2015.
 */
public interface StorerInterface
{
    public void storeWeight(WeightRegister weight);
    public List<WeightRegister> getAllWeights();
    public List<ExerciseActivity>getActivitiesList();
}
