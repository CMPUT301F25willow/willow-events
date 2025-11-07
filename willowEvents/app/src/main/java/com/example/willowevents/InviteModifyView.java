package com.example.willowevents;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InviteModifyView extends AppCompatActivity {

    Button acceptButton;
    Button declineWithdrawButton;
    TextView inviteMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_content);

        acceptButton = findViewById(R.id.accept_button);
        declineWithdrawButton = findViewById(R.id.decline_withdraw_button);
        inviteMessage = findViewById(R.id.invite_message);
    }
}
