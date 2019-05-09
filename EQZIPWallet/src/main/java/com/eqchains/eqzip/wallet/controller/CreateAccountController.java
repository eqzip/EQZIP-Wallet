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

import com.eqchains.eqzip.wallet.EQZIPWallet;
import com.eqchains.eqzip.wallet.util.GuiUtils;
import com.eqzip.eqcoin.keystore.Keystore;
import com.eqzip.eqcoin.keystore.UserAccount;
import com.eqzip.eqcoin.keystore.Keystore.ECCTYPE;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

/**
 * @author Xun Wang
 * @date Jan 14, 2019
 * @email 10509759@qq.com
 */
public class CreateAccountController {
	@FXML
	private TextField accountTextField;
	@FXML
	private PasswordField passwordTextField1;
	@FXML
	private PasswordField passwordTextField2;
	@FXML
	private RadioButton p256;
	@FXML
	private RadioButton p521;
	
	private void hide(ActionEvent  actionEvent) {
		((Node)(actionEvent.getSource())).getScene().getWindow().hide();
	}
	
	@FXML
	private void onOk(ActionEvent  actionEvent) {
		if(accountTextField.getText().isEmpty()) {
			GuiUtils.informationalAlert("�û�������Ϊ�գ�", "�������û�����");
			accountTextField.requestFocus();
			return;
		}
		if((passwordTextField1.getText().length() < 8) || (passwordTextField1.getText().length() > 20)) {
			GuiUtils.informationalAlert("���볤�ȴ���", "���볤��Ӧ���ڵ���8С�ڵ���20��");
			passwordTextField1.requestFocus();
			return;
		}
		if((passwordTextField2.getText().length() < 8) || (passwordTextField2.getText().length() > 20)) {
			GuiUtils.informationalAlert("���볤�ȴ���", "���볤��Ӧ���ڵ���8С�ڵ���20��");
			passwordTextField2.requestFocus();
			return;
		}
		if(!passwordTextField1.getText().equals(passwordTextField2.getText())) {
			GuiUtils.informationalAlert("������������벻�����", "�������������룡");
			passwordTextField1.requestFocus();
			return;
		}
		ECCTYPE type = ECCTYPE.P256;
		if(p521.isSelected()) {
			type = ECCTYPE.P521;
		}
		UserAccount userAccount = Keystore.getInstance().createUserAccount(accountTextField.getText(), passwordTextField1.getText(), type);
		EQZIPWallet.constroller.setAddress(userAccount.getAddress());
		hide(actionEvent);
	}
	
	@FXML
	private void onCancel(ActionEvent actionEvent) {
		hide(actionEvent);
	}
	
}
