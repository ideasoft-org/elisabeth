package org.elisabeth.Plugin;

import java.util.*;

public class PluginContaier {

    private Map<String, Class> plugins = new HashMap<>();

    private List<Plugin> instances = Collections.synchronizedList(new LinkedList<>());


    public void register(Class plugin) {
        String yamlId = Plugin.getYamlId(plugin);
        plugins.put(yamlId, plugin);
    }

    public Plugin instantiate (String yamlId,
                               Map<String, Object> yamlData,
                               Map<String, String> globalVars) throws Exception {

            Class plugin = plugins.get(yamlId);

            if (plugin == null) {
                System.out.printf("Plugin %s not found\n", yamlId);
                return null;
            }

            System.out.printf("Creating %s\n", yamlId);

            Plugin instance = (Plugin) plugin.newInstance();
            instance.setYamlData(yamlData);
            instance.setGlobalVars(globalVars);
            instance.setPlugins(this.plugins);
            instances.add(instance);
            return instance;
    }

    public void evalAll() {
        instances.forEach(Plugin::evaluate);
    }

    public Map<String, Class> getPlugins() {
        return plugins;
    }

    public void setPlugins(Map<String, Class> plugins) {
        this.plugins = plugins;
    }
}
