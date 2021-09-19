package com.rezdy.lunch.service;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Ingredient {

    @Id
    private String title;

    private LocalDate bestBefore;

    private LocalDate useBy;

    public Ingredient(){}

    public Ingredient(String title, LocalDate bestBefore, LocalDate useBy) {
        this.title = title;
        this.bestBefore = bestBefore;
        this.useBy = useBy;
    }

    public String getTitle() {
        return title;
    }

    public Ingredient setTitle(String title) {
        this.title = title;
        return this;
    }

    public LocalDate getBestBefore() {
        return bestBefore;
    }

    public Ingredient setBestBefore(LocalDate bestBefore) {
        this.bestBefore = bestBefore;
        return this;
    }

    public LocalDate getUseBy() {
        return useBy;
    }

    public Ingredient setUseBy(LocalDate useBy) {
        this.useBy = useBy;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ingredient that = (Ingredient) o;

        if (!title.equals(that.title)) return false;
        if (!bestBefore.equals(that.bestBefore)) return false;
        return useBy.equals(that.useBy);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + bestBefore.hashCode();
        result = 31 * result + useBy.hashCode();
        return result;
    }
}
