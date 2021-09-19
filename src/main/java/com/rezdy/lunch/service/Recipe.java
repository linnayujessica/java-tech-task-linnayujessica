package com.rezdy.lunch.service;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Recipe {

    @Id
    private String title;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "RECIPE_INGREDIENT",
            joinColumns = @JoinColumn(name = "RECIPE"),
            inverseJoinColumns = @JoinColumn(name = "INGREDIENT"))
    private List<Ingredient> ingredients;

    public String getTitle() {
        return title;
    }

    public Recipe setTitle(String title) {
        this.title = title;
        return this;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public Recipe setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Recipe recipe = (Recipe) o;

        if (!title.equals(recipe.title)) return false;
        return ingredients.equals(recipe.ingredients);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + ingredients.hashCode();
        return result;
    }
}
