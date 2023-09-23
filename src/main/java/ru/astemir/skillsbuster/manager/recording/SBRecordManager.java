package ru.astemir.skillsbuster.manager.recording;


import com.google.gson.JsonObject;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.compress.utils.FileNameUtils;
import ru.astemir.skillsbuster.SkillsBuster;
import ru.astemir.skillsbuster.common.io.ModFile;
import ru.astemir.skillsbuster.common.io.json.SBJson;
import ru.astemir.skillsbuster.manager.SBManager;
import ru.astemir.skillsbuster.manager.config.ConfigType;
import ru.astemir.skillsbuster.manager.config.SBConfig;
import java.io.File;
import java.util.List;



@Mod.EventBusSubscriber(modid = SkillsBuster.MODID)
public class SBRecordManager extends SBManager.Configurable<RecordedScene> {
    private static SBRecordManager instance;
    private File folder;
    private RecordedScene currentScene;

    public SBRecordManager() {
        super(ConfigType.RECORD);
        instance = this;
    }

    @Override
    protected void onLoadConfiguration(List<SBConfig> configurations) {
        for (SBConfig configuration : configurations) {
            JsonObject cameraConfigJson = configuration.getFile().json();
            String path = SBJson.getString(cameraConfigJson,"records");
            folder = FMLPaths.GAMEDIR.get().resolve(path).toFile();
            if (!folder.exists()) {
                folder.mkdir();
            }
            for (String fileName : folder.list()) {
                ModFile recordFile = new ModFile(path + "/" + fileName, false, false);
                RecordedScene recordedScene = SBJson.as(recordFile.json(), RecordedScene.class);
                add(FileNameUtils.getBaseName(fileName), recordedScene);
            }
        }
    }


    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent e){
        if (e.phase == TickEvent.Phase.START) {

        }
    }

    public File getFolder() {
        return folder;
    }

    public static SBRecordManager getInstance() {
        return instance;
    }
}
