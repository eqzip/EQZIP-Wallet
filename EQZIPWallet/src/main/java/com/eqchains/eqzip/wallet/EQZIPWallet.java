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
//			if(!Util.isNetworkAvailable()) {
//				GuiUtils.informationalAlert("���粻����!", "������������!");
//				return;
//			}
//			if(!Util.isTimeCorrect()) {
//				GuiUtils.informationalAlert("ʱ�䲻׼ȷ!", "���������ʱ��!");
//				return;
//			}
			BorderPane root = null;
			 FXMLLoader loader = null;
			try {
				loader =  new FXMLLoader(getClass().getResource("/com/eqchains/eqzip/wallet/resource/fxml/EQZIPWallet.fxml"));
				root = loader.load();
//				root = (BorderPane)FXMLLoader.load(getClass().getResource("/com/eqchains/eqzip/wallet/resource/fxml/EQZIPWallet.fxml"));
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
