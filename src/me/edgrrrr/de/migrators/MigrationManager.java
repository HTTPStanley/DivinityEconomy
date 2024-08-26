package me.edgrrrr.de.migrators;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import me.edgrrrr.de.migrators.migrations.Migrate343;
import me.edgrrrr.de.utils.VersionComparator;

public class MigrationManager extends DivinityModule  {
    private final Migration[] migrations = new Migration[] {
            new Migrate343(this.getMain()),
    };


    public MigrationManager(DEPlugin main) {
        super(main);
    }


    @Override
    public void init() {
        String currentVersion = getMain().getConfMan().getLoadedVersion();
        boolean migrationStarted = false;

        for (Migration migration : this.migrations) {
            // Start migration if we reach the current version
            if (!migrationStarted && VersionComparator.isVersionLowerOrEqual(currentVersion, migration.getVersion())) {
                migrationStarted = true;
            }

            // If migration has started, execute the migration
            if (migrationStarted) {
                this.getConsole().migrate("Migrating from %s to %s", migration.getVersion(), migration.getNextVersion());
                migration.migrate();
                currentVersion = migration.getNextVersion();
                this.getConsole().migrate("Migration for version %s complete", migration.getVersion());
            }
        }
    }



    @Override
    protected void deinit() {

    }
}
