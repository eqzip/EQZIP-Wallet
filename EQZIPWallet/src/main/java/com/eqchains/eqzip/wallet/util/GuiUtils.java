/**
 * EQZIPWallet - EQchains Foundation's EQZIPWallet
 * @copyright 2018-present EQchains Foundation All rights reserved...
 * Copyright of all works released by EQchains Foundation or jointly released by
 * EQchains Foundation with cooperative partners are owned by EQchains Foundation
 * and entitled to protection available from copyright law by country as well as
 * international conventions.
 * Attribution — You must give appropriate credit, provide a link to the license.
 * Non Commercial — You may not use the material for commercial purposes.
 * No Derivatives — If you remix, transform, or build upon the material, you may
 * not distribute the modified material.
 * For any use of above stated content of copyright beyond the scope of fair use
 * or without prior written permission, EQchains Foundation reserves all rights to
 * take any legal action and pursue any right or remedy available under applicable
 * law.
 * https://www.eqchains.com
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.eqchains.eqzip.wallet.util;

import com.eqchains.eqzip.wallet.controller.AlertWindowController;
import com.google.common.base.Throwables;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.function.BiConsumer;

import static com.google.common.base.Preconditions.checkState;

public class GuiUtils {
    public static void runAlert(BiConsumer<Stage, AlertWindowController> setup) {
        try {
            // JavaFX2 doesn't actually have a standard alert template. Instead the Scene Builder app will create FXML
            // files for an alert window for you, and then you customise it as you see fit. I guess it makes sense in
            // an odd sort of way.
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader loader = new FXMLLoader(GuiUtils.class.getResource("/com/eqchains/eqzip/wallet/resource/fxml/alert.fxml"));
            Pane pane = loader.load();
            AlertWindowController controller = loader.getController();
            setup.accept(dialogStage, controller);
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.getIcons().add(new Image(setup.getClass().getResourceAsStream(WalletUtil.LOGO)));
            dialogStage.showAndWait();
        } catch (IOException e) {
            // We crashed whilst trying to show the alert dialog (this should never happen). Give up!
            throw new RuntimeException(e);
        }
    }

    public static void crashAlert(Throwable t) {
        t.printStackTrace();
        Throwable rootCause = Throwables.getRootCause(t);
        Runnable r = () -> {
            runAlert((stage, controller) -> controller.crashAlert(stage, rootCause.toString()));
            Platform.exit();
        };
        if (Platform.isFxApplicationThread())
            r.run();
        else
            Platform.runLater(r);
    }

    /** Show a GUI alert box for any unhandled exceptions that propagate out of this thread. */
    public static void handleCrashesOnThisThread() {
        Thread.currentThread().setUncaughtExceptionHandler((thread, exception) -> {
            GuiUtils.crashAlert(Throwables.getRootCause(exception));
        });
    }

    public static void informationalAlert(String message, String details, Object... args) {
        String formattedDetails = String.format(details, args);
        Runnable r = () -> runAlert((stage, controller) -> controller.informational(stage, message, formattedDetails));
        if (Platform.isFxApplicationThread())
            r.run();
        else
            Platform.runLater(r);
    }

    public static final int UI_ANIMATION_TIME_MSEC = 600;
    public static final Duration UI_ANIMATION_TIME = Duration.millis(UI_ANIMATION_TIME_MSEC);

    public static Animation fadeIn(Node ui) {
        return fadeIn(ui, 0);
    }

    public static Animation fadeIn(Node ui, int delayMillis) {
        ui.setCache(true);
        FadeTransition ft = new FadeTransition(Duration.millis(UI_ANIMATION_TIME_MSEC), ui);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setOnFinished(ev -> ui.setCache(false));
        ft.setDelay(Duration.millis(delayMillis));
        ft.play();
        return ft;
    }

    public static Animation fadeOut(Node ui) {
        FadeTransition ft = new FadeTransition(Duration.millis(UI_ANIMATION_TIME_MSEC), ui);
        ft.setFromValue(ui.getOpacity());
        ft.setToValue(0.0);
        ft.play();
        return ft;
    }

    public static Animation fadeOutAndRemove(Pane parentPane, Node... nodes) {
        Animation animation = fadeOut(nodes[0]);
        animation.setOnFinished(actionEvent -> parentPane.getChildren().removeAll(nodes));
        return animation;
    }

    public static Animation fadeOutAndRemove(Duration duration, Pane parentPane, Node... nodes) {
        nodes[0].setCache(true);
        FadeTransition ft = new FadeTransition(duration, nodes[0]);
        ft.setFromValue(nodes[0].getOpacity());
        ft.setToValue(0.0);
        ft.setOnFinished(actionEvent -> parentPane.getChildren().removeAll(nodes));
        ft.play();
        return ft;
    }

    public static void blurOut(Node node) {
        GaussianBlur blur = new GaussianBlur(0.0);
        node.setEffect(blur);
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(blur.radiusProperty(), 10.0);
        KeyFrame kf = new KeyFrame(Duration.millis(UI_ANIMATION_TIME_MSEC), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    public static void blurIn(Node node) {
        GaussianBlur blur = (GaussianBlur) node.getEffect();
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(blur.radiusProperty(), 0.0);
        KeyFrame kf = new KeyFrame(Duration.millis(UI_ANIMATION_TIME_MSEC), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(actionEvent -> node.setEffect(null));
        timeline.play();
    }

    public static ScaleTransition zoomIn(Node node) {
        return zoomIn(node, 0);
    }

    public static ScaleTransition zoomIn(Node node, int delayMillis) {
        return scaleFromTo(node, 0.95, 1.0, delayMillis);
    }

    public static ScaleTransition explodeOut(Node node) {
        return scaleFromTo(node, 1.0, 1.05, 0);
    }

    private static ScaleTransition scaleFromTo(Node node, double from, double to, int delayMillis) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(UI_ANIMATION_TIME_MSEC / 2), node);
        scale.setFromX(from);
        scale.setFromY(from);
        scale.setToX(to);
        scale.setToY(to);
        scale.setDelay(Duration.millis(delayMillis));
        scale.play();
        return scale;
    }

    public static void checkGuiThread() {
        checkState(Platform.isFxApplicationThread());
    }
}
