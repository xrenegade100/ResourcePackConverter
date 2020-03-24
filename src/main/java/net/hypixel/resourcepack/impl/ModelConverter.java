package net.hypixel.resourcepack.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.MalformedJsonException;
import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;
import net.hypixel.resourcepack.pack.Pack;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

public class ModelConverter extends Converter {

    private double version;
    protected String light;
    private boolean verbose;

    public ModelConverter(PackConverter packConverter, String lightIn, String versionIn, boolean verbose) {
        super(packConverter);
        light = lightIn;
        version = Double.parseDouble(versionIn);
        this.verbose = verbose;
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path models = pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" +File.separator + "models");
        findFiles(models);
        //remapModelJson(models.resolve("item"));
        //remapModelJson(models.resolve("block"));
    }

    protected void findFiles(Path path) throws IOException {
        File directory = new File(path.toString());
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isDirectory()) {
                remapModelJson(Paths.get(file.getPath()));
                findFiles(Paths.get(file.getPath()));

            }
        }
    }
    protected void remapModelJson(Path path) throws IOException {

    if (!path.toFile().exists()) return;

    Files.list(path)
            .filter(path1 -> path1.toString().endsWith(".json"))
            .forEach(model -> {
                try {
                    if(this.verbose) System.out.println("      Processing: " + model.getParent() + model.getFileName());
                    JsonObject jsonObject = Util.readJson(packConverter.getGson(), model);

                    //GUI light system for 1.15.2
                    if (!light.equals("none") && (light.equals("front") || light.equals("side"))) jsonObject.addProperty("gui_light", light);
                    // minify the json so we can replace spaces in paths easily
                    // TODO Improvement: handle this in a cleaner way?
                    String content = jsonObject.toString();
                    content = content.replaceAll("items/", "item/");
                    content = content.replaceAll("blocks/", "block/");
                    content = content.replaceAll(" ", "_");

                    Files.write(model, Collections.singleton(content), Charset.forName("UTF-8"));

                    // handle the remapping of textures, for models that use default texture names
                    jsonObject = Util.readJson(packConverter.getGson(), model);
                    if (jsonObject.has("textures")) {
                        NameConverter nameConverter = packConverter.getConverter(NameConverter.class);

                        JsonObject textureObject = null;
                        try {
                            //Check to avoid crash on malformed jsons
                            textureObject = jsonObject.getAsJsonObject("textures");
                        } catch (ClassCastException e1) {
                            System.out.println("      Error on model: " + model.getFileName());
                            System.out.println("      `textures` is malformed, skipping this model");
                            return;
                        }

                        for (Map.Entry<String, JsonElement> entry : textureObject.entrySet()) {
                            String value = entry.getValue().getAsString();
                            if (version == 1.13) {
                                if (value.startsWith("block/")) {
                                    textureObject.addProperty(entry.getKey(), "block/" + nameConverter.getBlockMapping().remap(value.substring("block/".length())));
                                } else if (value.startsWith("item/")) {
                                    textureObject.addProperty(entry.getKey(), "item/" + nameConverter.getItemMapping().remap(value.substring("item/".length())));
                                }
                            }
                            if (version > 1.13) {
                                if (value.startsWith("block/")) {
                                    textureObject.addProperty(entry.getKey(), "block/" + nameConverter.getNewBlockMapping().remap(value.substring("block/".length())));
                                }
                            }
                        }


                    }
                    if (jsonObject.has("parent")) {
                        for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                            if(entry.getKey().equals("parent")) jsonObject.addProperty(entry.getKey(), entry.getValue().getAsString().toLowerCase());
                        }

                    }

                    Files.write(model, Collections.singleton(packConverter.getGson().toJson(jsonObject)), Charset.forName("UTF-8"));
                } catch (IOException e) {
                    throw Util.propagate(e);
                }
            });
}
}
