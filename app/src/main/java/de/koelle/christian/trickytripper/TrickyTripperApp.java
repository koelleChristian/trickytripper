package de.koelle.christian.trickytripper;

import android.app.Application;
import de.koelle.christian.common.utils.FileUtils;
import de.koelle.christian.trickytripper.apputils.PrefAccessor;
import de.koelle.christian.trickytripper.controller.ExchangeRateController;
import de.koelle.christian.trickytripper.controller.ExportController;
import de.koelle.christian.trickytripper.controller.MiscController;
import de.koelle.christian.trickytripper.controller.TripController;
import de.koelle.christian.trickytripper.controller.ViewController;
import de.koelle.christian.trickytripper.controller.impl.ExchangeRateControllerImpl;
import de.koelle.christian.trickytripper.controller.impl.ExportControllerImpl;
import de.koelle.christian.trickytripper.controller.impl.MiscControllerImpl;
import de.koelle.christian.trickytripper.controller.impl.TripControllerImpl;
import de.koelle.christian.trickytripper.controller.impl.ViewControllerImpl;
import de.koelle.christian.trickytripper.dataaccess.DataManager;
import de.koelle.christian.trickytripper.dataaccess.impl.DataManagerImpl;
import de.koelle.christian.trickytripper.decoupling.PrefsResolver;

public class TrickyTripperApp extends Application {

    private DataManager dataManager;
    private ExchangeRateController exchangeRateController;
    private ExportController exportController;
    private MiscController miscController;
    private ViewController viewController;
    private TripController tripController;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        shutdown();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        shutdown();
    }

    private void shutdown() {
        closeDatabase();
        if (tripController != null) {
            tripController.safeLoadedTripIdToPrefs();
        }
        FileUtils.deleteAllFiles(this);
    }

    public void init() {

        FileUtils.deleteAllFiles(this);

        PrefsResolver prefsResolver = new PrefAccessor(this);
        dataManager = new DataManagerImpl(getBaseContext());

        exchangeRateController = new ExchangeRateControllerImpl(dataManager, prefsResolver);
        miscController = new MiscControllerImpl(dataManager, this, prefsResolver);
        viewController = new ViewControllerImpl(this);
        TripControllerImpl tripControllerImpl = new TripControllerImpl(this, dataManager, prefsResolver, miscController);
        tripController = tripControllerImpl;
        exportController = new ExportControllerImpl(this, prefsResolver, tripControllerImpl);

    }

    public void closeDatabase() {
        if (dataManager != null) {
            dataManager.close();
        }
    }

    public ViewController getViewController() {
        return viewController;
    }

    public TripController getTripController() {
        return tripController;
    }

    public MiscController getMiscController() {
        return miscController;
    }

    public ExchangeRateController getExchangeRateController() {
        return exchangeRateController;
    }

    public ExportController getExportController() {
        return exportController;

    }

}
