package de.rpgframework.shadowrun6.updater;

import java.awt.Taskbar;
import java.awt.Taskbar.Feature;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import org.update4j.service.Delegate;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JavaFxDelegate extends Application implements Delegate {

	private final static Logger logger = System.getLogger("commlink6.updater");

	@Override
	public long version() {
		return 0;
	}

	@Override
	public void main(List<String> args) throws Throwable {
		launch();
	}

	// for testing purposes only
	public static void main(String[] args) {
		launch();
	}

	public static List<Image> images;
	public static Image inverted;

	@Override
	public void init() {
		System.setProperty("update4j.suppress.warning", "false");

//		logger.log(Level.DEBUG, "class loader 1 = "+ClassLoader.getSystemClassLoader());
//		logger.log(Level.DEBUG, "class loader 2 = "+System.getProperty("java.class.path"));
//
//		for (String elem : System.getProperty("java.class.path").split(":")) {
//			logger.log(Level.DEBUG, "  "+elem);
//		}

//		Parameters params = this.getParameters();
//		for (String elem : params.getRaw()) {
//			logger.log(Level.DEBUG, "Param  "+elem);
//		}

		logger.log(Level.DEBUG, "Process "+ProcessHandle.current().info().command());
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
//		primaryStage.setMinWidth(650);
//		primaryStage.setMinHeight(500);
		primaryStage.initStyle(StageStyle.TRANSPARENT);

		StartupView startup = new StartupView(primaryStage);
		startup.releaseTypeProperty().addListener( (ov,o,n) -> {});

		Scene scene = new Scene(startup, Color.TRANSPARENT);
//		scene.getStylesheets().add(getClass().getResource("root.css").toExternalForm());

		primaryStage.setScene(scene);

		primaryStage.setTitle("Commlink 6 Updater");
		primaryStage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("Commlink6.png")));

		//Set icon on the taskbar/dock
        if (Taskbar.isTaskbarSupported()) {
            var taskbar = Taskbar.getTaskbar();

            if (taskbar.isSupported(Feature.ICON_IMAGE)) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
				var dockIcon = defaultToolkit.getImage(ClassLoader.getSystemResource("Commlink6.png"));
                taskbar.setIconImage(dockIcon);
            }

        }
		Platform.setImplicitExit(true);
		primaryStage.show();
	}

}