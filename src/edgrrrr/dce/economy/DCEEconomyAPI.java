package edgrrrr.dce.economy;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.vea.economy.EconomyAPI;

public class DCEEconomyAPI extends EconomyAPI {
    private final DCEPlugin app;

    public DCEEconomyAPI(DCEPlugin app) {
        super(app, app.getConfigManager(), app.getConsole(), app.getPlayerManager(), app.getConfigManager().getInt(Setting.ECONOMY_ACCURACY_DIGITS_INTEGER), "coins", "coin");
        this.app = app;
    }
}
