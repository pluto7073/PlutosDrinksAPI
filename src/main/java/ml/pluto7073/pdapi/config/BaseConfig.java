package ml.pluto7073.pdapi.config;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public abstract class BaseConfig {

    protected Properties properties;
    protected String modid, name;
    protected Logger logger;

    public BaseConfig(String modid, String name, Logger logger) {
        this.modid = modid;
        this.name = name;
        this.logger = logger;
        properties = new Properties();
        initConfig();
        load();
    }

    public abstract void initConfig();

    public void load() {
        File configDir = new File(FabricLoader.getInstance().getGameDir().toFile(), "config");
        File configFile = new File(configDir, modid + "_" + name + ".properties");
        if (!configFile.exists()) return;
        try (FileReader reader = new FileReader(configFile)) {
            properties.load(reader);
        } catch (IOException e) {
            logger.error("Couldn't load {} config for {}", modid, name, e);
        }
    }

    public void save() {
        File configDir = new File(FabricLoader.getInstance().getGameDir().toFile(), "config");
        File configFile = new File(configDir, modid + "_" + name + ".properties");
        if (!configFile.exists()) {
            try {
                if (!configFile.createNewFile()) throw new IOException("File already exists");
            } catch (IOException e) {
                logger.error("Couldn't create {} config file for {}", modid, name, e);
            }
        }
        try (FileWriter writer = new FileWriter(configFile)){
            properties.store(writer, "");
        } catch (IOException e) {
            logger.error("Couldn't save {} config for {}", modid, name, e);
        }
    }

    protected void setInt(String key, int i) {
        properties.setProperty(key, String.valueOf(i));
    }

    protected void setFloat(String key, float f) {
        properties.setProperty(key, String.valueOf(f));
    }

    protected void setBoolean(String key, boolean b) {
        properties.setProperty(key, String.valueOf(b));
    }

    protected <T extends Enum<T>> void setEnum(String key, T value) {
        properties.setProperty(key, value.name());
    }

    protected int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    protected float getFloat(String key) {
        return Float.parseFloat(properties.getProperty(key));
    }

    protected boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    protected <T extends Enum<T>> T getEnum(String key, Class<T> enumClass) {
        return Enum.valueOf(enumClass, properties.getProperty(key));
    }

}

