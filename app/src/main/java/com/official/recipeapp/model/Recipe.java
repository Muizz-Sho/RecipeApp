package com.official.recipeapp.model;

public class Recipe {

    String recipeID;
    String recipeName;
    String recipeImageURL;
    String recipeType;
    String recipeIngredient;
    String recipeSteps;

    public Recipe() {
    }

    public Recipe(String recipeID, String recipeName, String recipeImageURL, String recipeType, String recipeIngredient, String recipeSteps) {
        this.recipeID = recipeID;
        this.recipeName = recipeName;
        this.recipeImageURL = recipeImageURL;
        this.recipeType = recipeType;
        this.recipeIngredient = recipeIngredient;
        this.recipeSteps = recipeSteps;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeImageURL() {
        return recipeImageURL;
    }

    public void setRecipeImageURL(String recipeImageURL) {
        this.recipeImageURL = recipeImageURL;
    }

    public String getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(String recipeType) {
        this.recipeType = recipeType;
    }

    public String getRecipeIngredient() {
        return recipeIngredient;
    }

    public void setRecipeIngredient(String recipeIngredient) {
        this.recipeIngredient = recipeIngredient;
    }

    public String getRecipeSteps() {
        return recipeSteps;
    }

    public void setRecipeSteps(String recipeSteps) {
        this.recipeSteps = recipeSteps;
    }
}
