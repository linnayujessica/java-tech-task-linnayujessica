package com.rezdy.lunch.controller;

import com.rezdy.lunch.exception.LunchExceptionHandler;
import com.rezdy.lunch.service.Ingredient;
import com.rezdy.lunch.service.LunchService;
import com.rezdy.lunch.service.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
public class LunchController {

    private final LunchService lunchService;

    @Autowired
    public LunchController(LunchService lunchService) {
        this.lunchService = lunchService;
    }

    @GetMapping("/lunch")
    public List<Recipe> getRecipes(@RequestParam(value = "date") String date) {
        return lunchService.getNonExpiredRecipesOnDate(LocalDate.parse(date));
    }

    @GetMapping("/recipe")
    public Recipe getRecipe(@RequestParam(value = "title") String title) {
        try {
            return lunchService.getRecipeByTitle(title);
        } catch(Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found", ex);
        }
    }

    @PostMapping("/exclude")
    public List<Recipe> excludeRecipes(@RequestBody List<Ingredient> ingredients) {
        return lunchService.filterRecipes(ingredients);
    }

}
