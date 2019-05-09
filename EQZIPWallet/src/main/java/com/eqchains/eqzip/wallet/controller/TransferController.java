/**
 * EQZIPWallet - EQchains Foundation's EQZIPWallet
 * @copyright 2018 EQCOIN Foundation Inc.  All rights reserved...
 * https://www.EQCOIN Foundation.com
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
package com.eqchains.eqzip.wallet.controller;

import java.math.BigInteger;
import java.util.Arrays;

import com.eqchains.eqzip.wallet.EQZIPWallet;
import com.eqchains.eqzip.wallet.util.GuiUtils;
import com.eqzip.eqcoin.blockchain.Address;
import com.eqzip.eqcoin.blockchain.Transaction;
import com.eqzip.eqcoin.blockchain.TxIn;
import com.eqzip.eqcoin.blockchain.TxOut;
import com.eqzip.eqcoin.keystore.Keystore;
import com.eqzip.eqcoin.keystore.UserAccount;
import com.eqzip.eqcoin.keystore.Keystore.ECCTYPE;
import com.eqzip.eqcoin.persistence.h2.EQCBlockChainH2;
import com.eqzip.eqcoin.util.Log;
import com.eqzip.eqcoin.util.SerialNumber;
import com.eqzip.eqcoin.util.Util;
import com.eqzip.eqcoin.util.Util.AddressShape;
import com.eqzip.eqcoin.util.Util.TXFEE_RATE;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

/**
 * @author Xun Wang
 * @date Jan 20, 2019
 * @email 10509759@qq.com
 */
public class TransferController {
	@FXML
	private TextField accountTextField;
	@FXML
	private TextField value;
	@FXML
	private PasswordField password;
	@FXML
	private RadioButton one;
	@FXML
	private RadioButton twenty;
	@FXML
	private RadioButton fourty;
	@FXML
	private RadioButton sixty;
	@FXML
	private Label txFee;
	
	private void hide(ActionEvent  actionEvent) {
		((Node)(actionEvent.getSource())).getScene().getWindow().hide();
	}
	
	@FXML
	private void onOk(ActionEvent  actionEvent) {
		if(accountTextField.getText().length() == 0) {
			GuiUtils.informationalAlert("�տ��ַ����Ϊ�գ�", "�������տ��ַ��");
			accountTextField.requestFocus();
			return;
		}
		if(!Util.isAddressFormatValid(accountTextField.getText())) {
			GuiUtils.informationalAlert("�տ��ַ��ʽ����", "��������ȷ��ʽ���տ��ַ��");
			accountTextField.requestFocus();
			return;
		}
		if(!Util.AddressTool.verifyAddress(accountTextField.getText())) {
			GuiUtils.informationalAlert("�տ��ַУ��ʹ���", "��������ȷ��ʽ���տ��ַ��");
			accountTextField.requestFocus();
			return;
		}
//		if(Util.isTXValueValid(value.getText())) {
//			GuiUtils.informationalAlert("ת�˽���ʽ����", "��������ȷ��ʽ��ת�˽�");
//			value.requestFocus();
//			return;
//		}
		if(Double.valueOf(value.getText()) * Util.ABC > Util.getBalance(Keystore.getInstance().getUserAccounts().get(0).getAddress())) {
			GuiUtils.informationalAlert("���㣡", "������С������ת�˽�");
			value.requestFocus();
			return;
		}
		if(!Arrays.equals(Util.EQCCHA_MULTIPLE(password.getText().getBytes(), Util.HUNDRED, true), Keystore.getInstance().getUserAccounts().get(0).getPwdHash())) {
			GuiUtils.informationalAlert("�������", "��������ȷ�����룡");
			value.requestFocus();
			return;
		}
		createTransaction();
		hide(actionEvent);
	}
	
	@FXML
	private void onCancel(ActionEvent actionEvent) {
		hide(actionEvent);
	}
	
	private void createTransaction() {
		try {
		Transaction transaction;
		TxIn txIn;
		TxOut txOut;
		Address address;
		transaction = new Transaction();
		txIn = new TxIn();
		address = new Address();
		address.setAddress(Keystore.getInstance().getUserAccounts().get(0).getAddress());
		txIn.setAddress(address);
		
		txOut = new TxOut();
		address = new Address();
		address.setAddress(accountTextField.getText());
		txOut.setAddress(address);
		txOut.setValue((long)Double.parseDouble(value.getText())*Util.ABC);
		transaction.setTxIn(txIn);
		transaction.addTxOut(txOut);
		
		byte[] privateKey = Util.AESDecrypt(Keystore.getInstance().getUserAccounts().get(0).getPrivateKey(), password.getText());
		byte[] publickey =  Util.AESDecrypt(Keystore.getInstance().getUserAccounts().get(0).getPublicKey(), password.getText());
		
		com.eqzip.eqcoin.blockchain.PublicKey publicKey1 = new com.eqzip.eqcoin.blockchain.PublicKey();
		publicKey1.setPublicKey(publickey);
		transaction.setPublickey(publicKey1);
		publicKey1.setSerialNumber(EQCBlockChainH2.getInstance().getAddressSerialNumber(txIn.getAddress()));
		TXFEE_RATE txFeeRate =  TXFEE_RATE.POSTPONE0;
		if(one.isSelected()) {
			txFeeRate =  TXFEE_RATE.POSTPONE0;
		}
		else if(twenty.isSelected()) {
			txFeeRate =  TXFEE_RATE.POSTPONE20;
		}
		else if(fourty.isSelected()) {
			txFeeRate =  TXFEE_RATE.POSTPONE40;
		}
		else if(sixty.isSelected()) {
			txFeeRate =  TXFEE_RATE.POSTPONE60;
		}
		
		// TxFee
		txIn.setValue(txOut.getValue()
				+ Util.getBillingFee(transaction, txFeeRate, EQCBlockChainH2.getInstance().getBlockTailHeight()));
		Log.info("TxIn value:" + txIn.getValue());
		Log.info("TxFee: "
				+ Util.getBillingFee(transaction, txFeeRate, EQCBlockChainH2.getInstance().getBlockTailHeight()));
		
		if(Transaction.isValid(transaction.getBytes(AddressShape.ADDRESS), AddressShape.ADDRESS)) {
			Log.info("Right format");
		}
		else {
			Log.info("Bad format");
		}
		
//		byte[] sign = Util.signTransaction(privateKey, transaction, new byte[16]);
//		transaction.setSignature(sign);
		
		if(transaction.verify()) {
			Log.info("Passed");
		}
		Log.info(transaction.toString());
//		EQCBlockChainH2.getInstance().addTransactionInPool(transaction, transaction.getMaxBillingSize(), 0, System.currentTimeMillis());
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
