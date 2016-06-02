package com.bloocheeze.android;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Window;

public class LoginDialog extends DialogFragment {

	public static LoginDialog newInstance(String userId) {
		LoginDialog fragment = new LoginDialog();
		Bundle args = new Bundle();
		args.putString("objectId", userId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.ui_user_sign_in);

		return dialog;
	}

}
