package es.victorgf87.diets.classes;

import java.util.Date;

/**
 * Created by Víctor on 26/09/2015.
 */
public class WeightRegister implements Comparable<WeightRegister>
{
    private double weight;
    private Date date;
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WeightRegister(double weight)
    {
        this.weight = weight;
        date=new Date(System.currentTimeMillis());
    }

    public WeightRegister(Integer id, double weight, long timeStamp) {
        this.date=new Date(timeStamp);
        this.id = id;
        this.weight = weight;
    }

    public Date getDate() {
        return date;
    }

    public long getDateTimeStamp()
    {
        return date.getTime();
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(WeightRegister another)
    {
        return date.compareTo(another.date);
    }
}
