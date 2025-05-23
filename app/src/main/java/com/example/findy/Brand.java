package com.example.findy;

public class Brand {
    private int id;
    private String name;
    private String category;
    private boolean vegetarianOption;
    private boolean veganOption;
    private boolean glutenFreeOption;
    private boolean meatOption;

    public Brand() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isVegetarianOption() { return vegetarianOption; }
    public void setVegetarianOption(boolean vegetarianOption) { this.vegetarianOption = vegetarianOption; }

    public boolean isVeganOption() { return veganOption; }
    public void setVeganOption(boolean veganOption) { this.veganOption = veganOption; }

    public boolean isGlutenFreeOption() { return glutenFreeOption; }
    public void setGlutenFreeOption(boolean glutenFreeOption) { this.glutenFreeOption = glutenFreeOption; }
    public boolean isMeatOption() { return meatOption; }
    public void setMeatOption(boolean vegetarianOption) { this.meatOption = vegetarianOption; }

    @Override
    public String toString() {
        return name;
    }
}