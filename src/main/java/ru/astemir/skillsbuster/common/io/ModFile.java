package ru.astemir.skillsbuster.common.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.StringUtils;
import org.astemir.api.io.FileUtils;
import org.astemir.api.io.json.JsonWrap;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.common.io.json.SBJson;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModFile {

    private String name;
    private String path;
    private String content = "";
    private Path fullPath;

    public ModFile(String path,boolean preload,boolean force) {
        this(getPath(path),getName(path));
        if (preload){
            preloadResource(force);
        }
    }

    public ModFile(String path, String name) {
        this.path = path;
        this.name = name;
        Path gameDir = FMLPaths.GAMEDIR.get();
        Path pathDir = null;
        if (path.isEmpty()) {
            this.fullPath = gameDir.resolve(name);
        }else{
            pathDir = gameDir.resolve(path);
            this.fullPath = pathDir.resolve(name);
        }
        try {
            if (pathDir != null) {
                if (!Files.exists(pathDir)) {
                    Files.createDirectories(pathDir);
                }
            }
            if (!Files.exists(fullPath)){
                Files.createFile(fullPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        load();
    }

    public void preloadResource(boolean force){
        if (content.isEmpty() || force) {
            InputStream inputStream = FileUtils.getResource(SkillsBuster.class, path + "/" + name);
            if (inputStream != null) {
                content = FileUtils.readText(inputStream);
                save();
            }
        }
    }


    public JsonElement get(String path){
        if (path.contains("/")) {
            String[] members = path.split("/");
            if (members.length > 0) {
                JsonElement result = json().get(members[0]);
                for (int i = 1; i < members.length; i++) {
                    if (result.isJsonObject()) {
                        result = result.getAsJsonObject().get(members[i]);
                    }
                }
                return result;
            }
        }
        return json().get(path);
    }

    public <T> T getAs(String path,Class<T> className){
        return SBJson.as(get(path),className);
    }

    public JsonArray getJsonArray(String path){
        return get(path).getAsJsonArray();
    }

    public JsonObject getJsonObject(String path){
        return get(path).getAsJsonObject();
    }

    public JsonObject json(){
        return SBJson.GSON.fromJson(content,JsonObject.class);
    }

    public void load(){
        content = FileUtils.readText(inputStream());
    }

    public void save(){
        FileUtils.writeText(getFile(), Charset.defaultCharset(),content);
    }

    public InputStream inputStream(){
        try {
            return new FileInputStream(fullPath.toFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getName() {
        return name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public JsonWrap getJson(){
        return new JsonWrap(getContent());
    }

    public String getContent() {
        return content;
    }

    public File getFile(){
        return fullPath.toFile();
    }

    public static String getPath(String path){
        if (path.contains("/")) {
            return StringUtils.substringBeforeLast(path, "/");
        }
        return "";
    }


    public static String getName(String path){
        if (path.contains("/")) {
            return StringUtils.substringAfterLast(path, "/");
        }
        return path;
    }
}
