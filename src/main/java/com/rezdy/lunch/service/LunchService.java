package com.rezdy.lunch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LunchService {

    @Autowired
    private EntityManager entityManager;

    private List<Recipe> recipesSorted;
    private LocalDate inputDate;

    public List<Recipe> getNonExpiredRecipesOnDate(LocalDate date) {
        inputDate = date;
        List<Recipe> recipes = loadRecipes(date);
        sortRecipes(recipes);
        return recipesSorted;
    }

    private void sortRecipes(List<Recipe> recipes) {
        recipesSorted = new ArrayList<>();
        for(Recipe i : recipes) recipesSorted.add(i);
        // check any ingredients in recipe are past bestBefore
        for(Recipe recipe : recipes) {
            List<Ingredient> ingredients = recipe.getIngredients();
            long count  = ingredients.stream().filter(s -> s.getBestBefore().isBefore(inputDate)).count();
            if(count!=0) {
                recipesSorted.remove(recipe);
                recipesSorted.add(recipe);
            }
        }
    }

    private List<Recipe> loadRecipes(LocalDate date) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Recipe> criteriaQuery = cb.createQuery(Recipe.class);
        Root<Recipe> recipeRoot = criteriaQuery.from(Recipe.class);

        CriteriaQuery<Recipe> query = criteriaQuery.select(recipeRoot);

        Subquery<Recipe> expiredIngredientSubquery = query.subquery(Recipe.class);
        Root<Recipe> expiredIngredient = expiredIngredientSubquery.from(Recipe.class);
        expiredIngredientSubquery.select(expiredIngredient);

        Predicate matchingRecipe = cb.equal(expiredIngredient.get("title"), recipeRoot.get("title"));
        Predicate expiredRecipeIngredient = cb.lessThan(expiredIngredient.join("ingredients").get("useBy"), date);

        Predicate allExpiredIngredients = cb.exists(expiredIngredientSubquery.where(matchingRecipe, expiredRecipeIngredient));
        List expiredRecipes = entityManager.createQuery(query.where(allExpiredIngredients)).getResultList();
        List nonExpiredRecipes = allRecipes();
        nonExpiredRecipes.removeAll(expiredRecipes);
        return nonExpiredRecipes;
    }

    public Recipe getRecipeByTitle(String title) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Recipe> criteriaQuery = cb.createQuery(Recipe.class);
        Root<Recipe> recipeRoot = criteriaQuery.from(Recipe.class);
        CriteriaQuery<Recipe> query = criteriaQuery.select(recipeRoot);
        return entityManager.createQuery(query.where(cb.equal(recipeRoot.get("title"), title))).getSingleResult();
    }

    public List<Recipe> filterRecipes(List<Ingredient> ingredients) {
        Recipe recipeFiltered = new Recipe();
        List<Recipe> recipes = allRecipes();
        for(Recipe recipe : recipes) {
            if(ingredients.equals(recipe.getIngredients())) {
                recipeFiltered = recipe;
            }
        }
        recipes.remove(recipeFiltered);
        return recipes;
    }

    public List<Recipe> allRecipes() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Recipe> criteriaQuery = cb.createQuery(Recipe.class);
        Root<Recipe> recipeRoot = criteriaQuery.from(Recipe.class);

        criteriaQuery.select(recipeRoot);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
