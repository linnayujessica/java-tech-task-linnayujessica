package com.rezdy.lunch.controller;

import com.rezdy.lunch.service.Ingredient;
import com.rezdy.lunch.service.LunchService;
import com.rezdy.lunch.service.Recipe;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(LunchController.class)
class LunchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LunchService lunchService;

    private String ingredients =
                    "       [\n" +
                    "            {\"title\": \"Eggs\", \"bestBefore\": \"2022-12-31\", \"useBy\": \"2021-12-31\"},\n" +
                    "            {\"title\": \"Plain flour\", \"bestBefore\": \"2021-12-31\", \"useBy\": \"2021-12-31\"},\n" +
                    "            {\"title\": \"Butter\", \"bestBefore\": \"2022-12-31\", \"useBy\": \"2021-12-31\"},\n" +
                    "            {\"title\": \"Milk\", \"bestBefore\": \"2022-12-31\", \"useBy\": \"2021-12-31\"},\n" +
                    "            {\"title\": \"Brown sugar\", \"bestBefore\": \"2022-12-31\", \"useBy\": \"2021-12-31\"}\n" +
                    "       ]";
    private static List<Recipe> recipes;

    @BeforeAll
    private static void setUp() {
        // Recipe 1:
        Recipe cookies = new Recipe();
        cookies.setTitle("Cookies");
        cookies.setIngredients(Arrays.asList(
                new Ingredient("Eggs", LocalDate.parse("2022-12-31"), LocalDate.parse("2021-12-31")),
                new Ingredient("Plain flour", LocalDate.parse("2021-12-31"), LocalDate.parse("2021-12-31")),
                new Ingredient("Butter", LocalDate.parse("2022-12-31"), LocalDate.parse("2021-12-31")),
                new Ingredient("Milk", LocalDate.parse("2022-12-31"), LocalDate.parse("2021-12-31")),
                new Ingredient("Brown sugar", LocalDate.parse("2022-12-31"), LocalDate.parse("2021-12-31"))
        ));
        // Recipe 2:
        Recipe meatPie = new Recipe();
        meatPie.setTitle("Meat pie");
        meatPie.setIngredients(Arrays.asList(
                new Ingredient("Onion", LocalDate.parse("2021-12-31"), LocalDate.parse("2022-12-31")),
                new Ingredient("Tomato source", LocalDate.parse("2021-12-31"), LocalDate.parse("2022-12-31")),
                new Ingredient("Beef mince", LocalDate.parse("2021-12-31"), LocalDate.parse("2022-12-31")),
                new Ingredient("Puff Pastry", LocalDate.parse("2022-12-31"), LocalDate.parse("2022-12-31")),
                new Ingredient("Cornflour", LocalDate.parse("2021-12-31"), LocalDate.parse("2022-12-31")),
                new Ingredient("Olive oil", LocalDate.parse("2021-12-31"), LocalDate.parse("2022-12-31"))
        ));
        // Recipe 3:
        Recipe pizza = new Recipe();
        pizza.setTitle("Pizza");
        pizza.setIngredients(Arrays.asList(
                new Ingredient("Bread flour", LocalDate.parse("2002-12-31"), LocalDate.parse("2022-12-31")),
                new Ingredient("Pepperoni", LocalDate.parse("2000-12-31"), LocalDate.parse("2022-12-31")),
                new Ingredient("Mozzarella cheese", LocalDate.parse("2021-12-31"), LocalDate.parse("2022-12-31")),
                new Ingredient("Garlic", LocalDate.parse("2022-12-31"), LocalDate.parse("2022-12-31")),
                new Ingredient("Fresh basil", LocalDate.parse("2021-12-31"), LocalDate.parse("2022-12-31")),
                new Ingredient("Tomato paste", LocalDate.parse("2021-12-31"), LocalDate.parse("2022-12-31"))
        ));
        recipes = Arrays.asList(cookies, meatPie, pizza);
    }

    @Test
    void getRecipes_success() throws Exception {
        given(lunchService.getNonExpiredRecipesOnDate(LocalDate.parse("2021-09-01"))).willReturn(recipes);

        mockMvc.perform(get("/lunch?date=2021-09-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(recipes.size())));
    }

    @Test
    void getRecipe_success() throws Exception {
        given(lunchService.getRecipeByTitle("Cookies")).willReturn(recipes.get(0));

        mockMvc.perform(get("/recipe?title=Cookies")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredients[0].title").value(recipes.get(0).getIngredients().get(0).getTitle()));
    }

    @Test
    public void getRecipe_error() throws Exception {
        given(lunchService.getRecipeByTitle("Lasagna")).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

        this.mockMvc.perform(get("/recipe?title=Lasagna")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Recipe not found", result.getResponse().getErrorMessage()));
    }

    @Test
    void excludeRecipes_success() throws Exception {
        given(lunchService.filterRecipes(recipes.get(0).getIngredients())).willReturn(recipes.subList(1,3));

        mockMvc.perform(post("/exclude")
                .content(ingredients)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value(recipes.get(1).getTitle()));

    }
}