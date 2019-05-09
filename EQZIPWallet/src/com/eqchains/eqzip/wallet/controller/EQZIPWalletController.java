package com.eqchains.eqzip.wallet.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.DecimalFormat;

import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.util.Utf8;

import com.eqchains.eqzip.wallet.util.GuiUtils;
import com.eqchains.eqzip.wallet.util.WalletUtil;
import com.eqzip.eqcoin.blockchain.Address;
import com.eqzip.eqcoin.keystore.Keystore;
import com.eqzip.eqcoin.persistence.h2.EQCBlockChainH2;
import com.eqzip.eqcoin.rpc.avro.Cookie;
import com.eqzip.eqcoin.rpc.avro.SyncblockNetwork;
import com.eqzip.eqcoin.service.MinerService;
import com.eqzip.eqcoin.service.SyncblockNetworkService;
import com.eqzip.eqcoin.util.Log;
import com.eqzip.eqcoin.util.Util;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EQZIPWalletController {
	@FXML
	private MenuItem createAccount;
	@FXML
	private Hyperlink address;
	@FXML
	private Hyperlink balance;
	
	@FXML
	private void onCreateAccount(ActionEvent  actionEvent) {
//		FileChooser fileChooser = new FileChooser();
//		fileChooser.showOpenDialog(borderPane.getScene().getWindow());
//		GuiUtils.informationalAlert("1", "/com/EQCOIN Foundation/eqcoin/wallet/resource/image/e.png\")")
//		createAccount.setDisable(true);
		 Stage dialogStage = new Stage();
         dialogStage.initModality(Modality.APPLICATION_MODAL);
         FXMLLoader loader = new FXMLLoader(EQZIPWalletController.class.getResource("/com/EQCOIN Foundation/eqcoin/wallet/resource/fxml/CreateAccount.fxml"));
         GridPane pane = null;
		try {
			pane = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         CreateAccountController controller = loader.getController();
//         setup.accept(dialogStage, controller);
         dialogStage.setScene(new Scene(pane));
         dialogStage.setResizable(false);
         dialogStage.getIcons().add(new Image(getClass().getResourceAsStream(WalletUtil.LOGO)));
         dialogStage.showAndWait();
	}
	
	@FXML
	private void onShowing(ActionEvent actionEvent) {
//		createAccount.setDisable(true);
	}
	
	public void setAddress(String address) {
		this.address.setText(address);
	}
	
	@FXML
	private void onMinering(ActionEvent actionEvent) {
//		GuiUtils.informationalAlert("ok", "well");
		MinerService.getInstance().start();
	}
	
	private void testPing() {
		long time = System.currentTimeMillis();
    	NettyTransceiver client = null;
    	try {
    		System.out.println("Begin link remote: " + time);
    		client = new NettyTransceiver(new InetSocketAddress(InetAddress.getByName(Util.getCookie().getIp().toString()), 7997), 3000l);
    		System.out.println("End link remote: " + (System.currentTimeMillis() - time));
    		 // client code - attach to the server and send a message
    		SyncblockNetwork proxy = (SyncblockNetwork) SpecificRequestor.getClient(SyncblockNetwork.class, client);
            System.out.println("Client built, got proxy");
            Cookie cookie = new Cookie();
            cookie.setIp(Util.getCookie().getIp().toString());
            cookie.setVersion("0.01");
            System.out.println("Calling proxy.send with message:  " + cookie.toString());
            System.out.println("Result: " + proxy.ping(cookie));

//            // cleanup
//            client.close();
    	}
    	catch (Exception e) {
			// TODO: handle exception
    		System.out.println("Exception occur during link remote: " + (System.currentTimeMillis() - time));
    		System.out.println(e.getMessage());
		}
    	finally {
			if(client != null) {
				client.close();
			}
		}
	}
	
	@FXML
	private void onTransfer(ActionEvent  actionEvent) {
		testPing();
//		 Stage dialogStage = new Stage();
//         dialogStage.initModality(Modality.APPLICATION_MODAL);
//         FXMLLoader loader = new FXMLLoader(EQZIPWalletController.class.getResource("/com/EQCOIN Foundation/eqcoin/wallet/resource/fxml/Transfer.fxml"));
//         GridPane pane = null;
//		try {
//			pane = loader.load();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//         TransferController controller = loader.getController();
////         setup.accept(dialogStage, controller);
//         dialogStage.setScene(new Scene(pane));
//         dialogStage.setResizable(false);
//         dialogStage.getIcons().add(new Image(getClass().getResourceAsStream(WalletUtil.LOGO)));
//         dialogStage.showAndWait();
	}
	
	public void setBalance(String address) {
		this.balance.setText(address);
	}
	
	@FXML
	public void onBalance(ActionEvent  actionEvent) {
		SyncblockNetworkService.getInstance().start();
		Address address = new Address();
		address.setAddress(Keystore.getInstance().getUserAccounts().get(0).getAddress());
		address.setSerialNumber(EQCBlockChainH2.getInstance().getAddressSerialNumber(address));
		double balance = ((double)EQCBlockChainH2.getInstance().getBalance(address)) /  (double)Util.ABC;
		balance = ((double)12345678) / (double)Util.ABC;
		Log.info("balance: " + balance);
//		this.balance.setText(""+balance);
		this.balance.setText(new DecimalFormat("##.0000").format(balance));
	}
	
}
