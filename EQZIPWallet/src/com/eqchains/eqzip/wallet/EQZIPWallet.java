package com.eqchains.eqzip.wallet;
	
import java.awt.desktop.ScreenSleepEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.eqchains.eqzip.wallet.controller.EQZIPWalletController;
import com.eqchains.eqzip.wallet.util.GuiUtils;
import com.eqchains.eqzip.wallet.util.WalletUtil;
import com.eqzip.eqcoin.keystore.Keystore;
import com.eqzip.eqcoin.persistence.h2.EQCBlockChainH2;
import com.eqzip.eqcoin.rpc.avro.MinerNetwork;
import com.eqzip.eqcoin.rpc.avro.TransactionNetwork;
import com.eqzip.eqcoin.service.MinerNetworkService;
import com.eqzip.eqcoin.service.SyncblockNetworkService;
import com.eqzip.eqcoin.service.TransactionNetworkService;
import com.eqzip.eqcoin.util.Log;
import com.eqzip.eqcoin.util.Util;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class EQZIPWallet extends Application {
	public static EQZIPWalletController constroller;
	@Override
	public void start(Stage primaryStage) {
//			GuiUtils.informationalAlert("exit", System.getProperty("user.dir"));
//		boolean boolIsSuccessful = true;
//		File file = new File(System.getProperty("user.dir"));
////		if (!file.isDirectory()) {
//			boolIsSuccessful = file.mkdir();
//			GuiUtils.informationalAlert("exit", "" + boolIsSuccessful);
////		}
			Util.init();
			if(!Util.isNetworkAvailable()) {
				GuiUtils.informationalAlert("���粻����!", "������������!");
				return;
			}
			if(!Util.isTimeCorrect()) {
				GuiUtils.informationalAlert("ʱ�䲻׼ȷ!", "���������ʱ��!");
				return;
			}
			BorderPane root = null;
			 FXMLLoader loader = null;
			try {
				loader =  new FXMLLoader(getClass().getResource("/com/EQCOIN Foundation/eqcoin/wallet/resource/fxml/EQZIPWallet.fxml"));
				root = loader.load();
//				root = (BorderPane)FXMLLoader.load(getClass().getResource("/com/EQCOIN Foundation/eqcoin/wallet/resource/fxml/EQZIPWallet.fxml"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Scene scene = new Scene(root,600,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			constroller = loader.getController();
			if(Keystore.getInstance().getUserAccounts().size() != 0) {
				constroller.setAddress(Keystore.getInstance().getUserAccounts().get(0).getAddress());
			}
			primaryStage.setResizable(false);
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(WalletUtil.LOGO)));
			primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
				// Hide the Window
				primaryStage.hide();
				// Begin release H2's resource and shutdown synchronization relevant Class
				EQCBlockChainH2.getInstance().close();
				MinerNetworkService.getInstance().close();
				SyncblockNetworkService.getInstance().close();
				TransactionNetworkService.getInstance().close();
		    });
			primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
