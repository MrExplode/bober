package me.sunstorm.bober;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public Gson GSON = new GsonBuilder().setPrettyPrinting().create();
}
