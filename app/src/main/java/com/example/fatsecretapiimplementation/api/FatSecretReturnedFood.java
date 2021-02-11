package com.example.fatsecretapiimplementation.api;

public  class FatSecretReturnedFood {
    String name;
    long id;
    boolean isChecked;


    public FatSecretReturnedFood(String name, long id, boolean isChecked) {
        this.name = name;
        this.id = id;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
