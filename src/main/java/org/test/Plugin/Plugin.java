package org.test.Plugin;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

public abstract class Plugin {

    private Map<String, String> globalVars = null;

    private PluginContaier pluginContaier = new PluginContaier();

    private Map<String, Class> plugins = new HashMap<>();

    private Map<String, Object> yamlData = null;

    public abstract Object evaluate();

    public Map<String, Class> getPlugins() {
        return plugins;
    }

    public void setPlugins(Map<String, Class> plugins) {
        this.plugins = plugins;
        pluginContaier.setPlugins(this.plugins);
    }

    public static String getYamlId(Class c) {
        return "$" + c.getSimpleName().replace("Plugin", "").toLowerCase();
    }


    public Map<String, Object> getYamlData() {
        return yamlData;
    }

    public void setYamlData(Map<String, Object> yamlData) {
        this.yamlData = yamlData;
    }

    public Map<String, String> getGlobalVars() {
        return globalVars;
    }

    public void setGlobalVars(Map<String, String> globalVars) {
        this.globalVars = globalVars;
    }

    public PluginContaier getPluginContaier() {
        return pluginContaier;
    }
}
