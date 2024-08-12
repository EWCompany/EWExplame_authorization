package explame_authorization.explame_authorization;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Explame_authourization extends JavaPlugin {

    private static final String LICENSE_SERVER_URL = "http://your_ip_server/d.php?key=%s";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        String licenseKey = getConfig().getString("licenseKey");
        if (licenseKey == null || licenseKey.isEmpty()) {
            getLogger().severe(translator.translate("&cКлюч для лицензии не найден. Отключение!"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new LicenseCheckTask(licenseKey).runTaskAsynchronously(this);
    }

    private class LicenseCheckTask extends BukkitRunnable {
        private final String licenseKey;

        public LicenseCheckTask(String licenseKey) {
            this.licenseKey = licenseKey;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(String.format(LICENSE_SERVER_URL, licenseKey));
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = in.readLine()) != null) {
                        response.append(responseLine);
                    }
                    in.close();

                    String jsonResponse = response.toString();
                    if (jsonResponse.contains("\"status\":\"success\"") && jsonResponse.contains("\"message\":\"valid\"")) {
                        getLogger().info(translator.translate("&aЛицензия проверена успешно. Приятной игры!"));
                    } else {
                        getLogger().severe("Invalid or missing license!");
                        getServer().getPluginManager().disablePlugin(Explame_authourization.this);
                    }
                } else {
                    getLogger().severe("Error checking license: " + conn.getResponseCode());
                    getServer().getPluginManager().disablePlugin(Explame_authourization.this);
                }
            } catch (Exception e) {
                getLogger().severe("An error occurred while checking the license: " + e.toString());
                e.printStackTrace(); // Печать стека вызовов
                getServer().getPluginManager().disablePlugin(Explame_authourization.this);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("LicenseCheckPlugin disabled.");
    }
}
