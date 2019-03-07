package com.example.user.test;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.inaka.galgo.Galgo;
import com.inaka.galgo.GalgoOptions;

import org.quietmodem.Quiet.FrameReceiver;
import org.quietmodem.Quiet.FrameReceiverConfig;
import org.quietmodem.Quiet.FrameTransmitter;
import org.quietmodem.Quiet.FrameTransmitterConfig;
import org.quietmodem.Quiet.ModemException;
import org.w3c.dom.Text;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class RequestProof extends AppCompatActivity {

    private String plusCode;
    private FrameTransmitter transmitter; // Transmitter object
    private FrameReceiver receiver;
    private Subscription frameSubscription = Subscriptions.empty();
    private TextView textView; // The plus code
    private TextView receivedText; // The message received
    private TextView numberToSpeak; // The status of the reception

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private Protocol protocolInstance;

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_proof);

        checkPermission();
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        plusCode = extras.getString("EXTRA_PLUS_CODE");

        textView = findViewById(R.id.plusCodeText);
        receivedText = findViewById(R.id.receivedText);
        numberToSpeak = findViewById(R.id.numberToSpeak);

        textView.setText(plusCode);


        // proverProtocol = new ProverProtocol(this, plusCode); // Instantiate prover protocol
//        setupTransmitter();
//        setupReceiver();
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() { // Find the transmit button
            @Override
            public void onClick(View v) {  // What happens when you click the button
                sendMessage();
            }
        });

        findViewById(R.id.receiveButton).setOnClickListener(new View.OnClickListener() { // Find the receive button
            @Override
            public void onClick(View v) {  // What happens when you click the button
                handleReceiveClick();
            }
        });
        findViewById(R.id.caButton).setOnClickListener(new View.OnClickListener() { // Find the receive button
            @Override
            public void onClick(View v) {  // What happens when you click the button
                handleCaButton();
            }
        });
        findViewById(R.id.lpButton).setOnClickListener(new View.OnClickListener() { // Find the receive button
            @Override
            public void onClick(View v) {  // What happens when you click the button
                handleLpButton();
            }
        });
        findViewById(R.id.verifierButton).setOnClickListener(new View.OnClickListener() { // Find the receive button
            @Override
            public void onClick(View v) {  // What happens when you click the button
                handleVerifierButton();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     // subscribeToFrames();
                } else {
                    showMissingAudioPermissionToast();
                    finish();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                checkPermission();
            } else {
                // Do as per your logic
            }

        }

    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    /*
    private void setupTransmitter() {
        FrameTransmitterConfig transmitterConfig;
        try {
            transmitterConfig = new FrameTransmitterConfig(
                    this, "ultrasonic-experimental");
            transmitter = new FrameTransmitter(transmitterConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ModemException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupReceiver() {
        if (hasRecordAudioPersmission()) {
            subscribeToFrames();
        } else {
            requestPermission();
        }
    }

    private void subscribeToFrames() {
        frameSubscription.unsubscribe();
        frameSubscription = FrameReceiverObservable.create(this, "ultrasonic-experimental").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(buf -> {
            receivedText.setText(new String(buf, Charset.forName("UTF-8")));
            Long time = System.currentTimeMillis() / 1000;
            String timestamp = time.toString();
            receivedStatus.setText("Received " + buf.length + " @" + timestamp);
        }, error -> {
            receivedStatus.setText("error " + error.toString());
        });
    }
    */

    private void sendMessage(){

        plusCode = "8G95WPQ94M";
        protocolInstance = new ProverProtocol(this, plusCode);
        protocolInstance.runProtocol();
    }


    /*
    private void handleSendClick() {
        if (transmitter == null) {
            setupTransmitter();
        }
        sendMessage();
    }
    */
    private void handleReceiveClick() {
        plusCode = "8G95WPQ94M";
        protocolInstance = new WitnessProtocol(this, plusCode);
        protocolInstance.runProtocol();
    }

    private void handleCaButton() {
        CertificateAuthority ca = new CertificateAuthority();
        ca.run();
    }

    private void handleLpButton() {
        String verifierPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC1y55aLcoLLaVhQbLvnInfqBPvy13+hWq0V1RrhtoLt4ibhEtb5wRSd9CPGymm4N2Sr2u1mijaF7ql8ge6GFr+QjEZFWPwVy+nb9O6ukaw48t0FsgldWj8RdqPEhF6H+VhdRsf4y6b+O6GHwkq6UZ0pYN7nnr8pezWf3/Zgw3nywIDAQAB";
        String verifierSegment = "{\"commitment\":\"CA identity\",\"type\":3,\"data\":\"WfLN30FHo10cprJ80dk9Oj+BgmHx/iVUwZyUGFLXqvgiJm2twPfjvoro3fY35+/Knsw41H7xjVzv\\nHHrTwJR9BrUJI0LzdFoQ1CtfV51qLTJtBNrgKKxr25c2T0YYOIYQq5D3ND6p0BaE510U8HfumeI/\\nwF+MW5HhBiLIboNowXQoFNU9ZKac/DQVw/N4vOh/4NMDmxZCaZkknnSNqY1v4BSu+xoteOQqWHm+\\nnXHrHxAE6MnLYrOnye5KxYWnvKFHL8UowWk3rHCTy6NjAqTiTXSo0eFn6O41bXXwZCA4ex9Hwxcs\\nCnxFxcE3ngqZEyBM0rJUM293d98/30xRK0uGZ2Fj8LoWUjf2BS2otQyKlTcRlMd7egRJDxy/TR9y\\nuGhSrNy0XtIWrF7E1ohA3gNWgw==\\n\",\"ivSpec\":\"wFNy9UjC/xMuHorEGy4fwg==\\n\",\"key\":\"MupOqKPyuORk1SgcAWYklX67F2Z8OqDS5qU1CfGWs+644uacYDTlamB4yu1IvMsM0n80kCA1iWI5\\nzynvHIqSGSnpgKyUsjtpktv8+O3tiftoYyuOZ0f1xL7QiBl9uLK1wKxhHbnxN+r8LxiSc8sIW8xT\\nzbBby6QqFgKb0TD2omQ=\\n\",\"sign\":\"byaljBwT1wGZq6QDGqvPvT0WS5yK+LpO9g5dQ8qhVB295lvKzS1pyYGqw+29JnvVPNU5zdZ4/vFy\\n8mZKjpevQwZcNrCQLKFbxpz3ZhPJJSlYIQHjR0lat/UESrB3LBvuoqt2qkHlWTkN3APemAY3NqjJ\\nXFBZH9XWRKNc5TFSi0o=\\n\"}";
        String auxInfoJSON = "{\"committedIdentity\":\"4cdfbbad15da3269fafc33122d881279b34e2a440fe247d408365d38e892675\",\"kchain\":[\"e3f70e9abddddab222c11a35eafa02546ad257f79877ddb868893e6fff57eb\",\"41f6938d18d480bdfab3bac9276eacb4ba4a94475fff66f78685787389b1d2c6\",\"ba14c986ecadb4d0bbfd3862c05ef0674f3a97d7f29a817b5c0a1eafee384\",\"b468d36ca5d436d9f9d7eacff24ad487a35d1d3e1d89b3ac3d128f0db1e1c\",\"24faf6ac2062b47c6973b564641f8424719937038e717ebd5ec442a7bd910\",\"b098ab9dde532a1bd715a451cbc1758fe1c5c6dd84dcd182cdb17a8ea1a85f\"],\"location\":\"8G95WPQ94M\"}";
        LpGenerator lpGen = new LpGenerator(auxInfoJSON);
        String lpJSON = lpGen.generateLp(verifierSegment, 2);
        lpGen.encryptLp(lpJSON, verifierPublicKey);

    }

    private void handleVerifierButton() {
        // String encryptedLp = "{\"commitment\":\"4cdfbbad15da3269fafc33122d881279b34e2a440fe247d408365d38e892675\",\"type\":4,\"data\":\"NpNd9s+EEoSA3q34smEjBJn3D07BZyY7HhGK5Jr+glaWwN8A5fEYCCOIZAYdy6UT/UdzovJxc0VJ\\nibsqYLGkfQCQcyPm/5t2ciePrHthcY4xcJgQ2hI7hb5gCp5v7t3WbF/l5HQ6eWtF/0M3pprngk6t\\n4Q+X/5bOFsSfYgASgggGPMo1i9EnuibWzeANlMCXm7G3hZHkW7I+3YGapHNGHsrglDOfzbo9iZcB\\nck3AaRdrGKth0qYEL6R8uxP26jqVm95LUKRIJ7nABj4unnwW9Jvum57BDge2Q14lobCIF8SBXN0j\\nZb8EJ8EYB78hEzy3R3tv2LF7A5CBjv7JVCzXyDeYwK6ORSpDmVxyveT9Svtu8ituem95YmE+9Uuj\\nF9XBpL7D1YUu4pZwOvkni6xM5m7YrnALAJuuc3oPpEd9UhZjfsm4AbQwMUoJgfG1KeYW/pRXOmKB\\n8eOoF7wwOGwJ2BylmYvtpCMAwYFkCuu64WPPir3Duqshu9hnxtS113/LX988kBA5P/dB0x/0Drh6\\n5aGL/bJuDvTkcksehDwH3Av0LNXs49RMmywqYIU3Gb+cYot5l5VbO5Dz1ivHamvOfrbQDIDB7O9O\\nPbOAVN0UgK9imayKEhCmTnazOSAtn/SuP5qFiEzRYiDqmEeSjOVfdF2t6TFmzPrbocDXkhCRuKGh\\nt6DLPxGNiMee45L1cE6BLAl7/cD1QlN91xP64lHQJQjMKCFSY2G8P6dZxARqvoD7wiibJInK/Rpi\\nbuPoIoXpEAnbXzfs/8IL+1WyBdFXScl21wulC3d9FmzgY3m36z4ZJ/RJfvvmmv+kl7f+kRlKiqWe\\nPnZki+/3qCyJqd5Mg0nnuh9Wp/pXkQYCuXGcHeahBz/zvpSCHXpEhiNgJZ7Np9FeU26H6x/isdba\\nCFacRkkJ8PGlEiPqJxbeUaqDSkl3gjoTJ4Ja6TiHU9nVKNfF5C+mXNlUTt+3fYc2fes3z2lNonHm\\nWWvxCrcVxPTg6J9Hr0vIq5Z/q5GFiHL7yUL6CajLaE1gBCVBsRSJfDVe9pZekElh5fN6NwfLUrM5\\noFL09Q2X7K3mlU4oAA4ZZq+sslgF4RZA/KF4Z/SS8fu7vfCNJ6Y+FB+iKBkoGXGMJPl5Jzgr4ndt\\nSOoNPoCKCI7lsPcQLFSl/2cY6agj2/uweU7Oqe1rnNk2kuXcN90UOAlscDP/tjgNilpJBsn59Z6N\\n0/xts/Ak41ku77hLfM9IPots6+nOV6gYFSwWyIOQ62m2S5fSu23Mv8HZtQ3HfvogxKZ3lGL9eOyt\\nZfvNu/3KvUEFrux1P9nX2eO3VNB3BWqzwiiqz28VEc2Sae+MCA4wIOrJzZY7AZnpzaHYK9bNJ4AN\\nz5Vnku6pJ5H6Sn14Y/c6tIaIAYBSkEYrW/LR0nkc\\n\",\"ivSpec\":\"atBDFEQmbp7ziforRFXuDA==\\n\",\"key\":\"MpBA4944kO/Pkkfv9uGfvFe0qI/UBAWvqiNF5bCngV0iPRuhg8sZNgXpu4yzv1I61YS7L5lCd94y\\neAvDOYIhpxOeSxN3Ai5HDIT92jH4mUxj25lSre8WyAeX+iQK9WRqdan6JugkP0hxyQn2zNtBxBmK\\nNzrZK7QdvbFsNYawJ7I=\\n\",\"sign\":\"A3N1SXDF3EKRvt80Uz49VJweuc4+A2g8pZ6lelToQWs9cNZ6D0iPCj+Xjm7RrEfuoWgeNvnfDxv8\\nCYH7LRoeSYSez4KHqGFDkeZncLEBr0rVMecHeJOOlJPQiG/1HX7wVoDgq8FG5iZWrWjlCKn7G45+\\nkD0GT1fMqXobBFq3G2U=\\n\"}";
        String encryptedLp = "{\"commitment\":\"4cdfbbad15da3269fafc33122d881279b34e2a440fe247d408365d38e892675\",\"type\":4,\"data\":\"IXzEbnIYlbycQv0mtUR66O1sXosV6JF6ZmI0wicoBV6CQLZ43MwK6EopXhjuSEAGGxtcSENQU/0o\\n+nFv8HWXeuVWfNoSpRA8Et6vMz9vBw5WVtfHxvXhM8Gav1qaTxGnlRvJqzxPpDpxUVhsMTrNo+9o\\nZa46tBeS/sbUNTODhCdJGSPcFAY5oXAP0O1BKVQ0KytVTgVdhpEAfqGuDPFGx73Bm6IL3DBjQk4d\\nqKRFgkW+6CtmyqvbmeYPvGFQxK4AITDckjW8/D9aNOfXKy6AIqTgkopEL/o8B6JHbLu7/XTic7dJ\\nfxZnyd3hp97NamHA6hWiPDkKgWGwykcO+C0fzdk4LxVixmNJAo5oIGreoZJQ15b967AKolLxgKuc\\nNDJq5ohlS1lg2T96DT6SIbmenJkqKywhD1iOYtGVyHbjs5qjv65+qrYMxmwSD2aNZ1wd5cZA47ur\\nNgRw3nkB0ajNasJCOUa1CVffcRjGCWnHHlR5Os46p3ZVwbRQFCmSEXM5pqGZGkFzcvM+kmjHA7/G\\ncOqyED+RI2yAF9wYhXGBJSzkdy2bbXA8uxzES9xAX1MWSaTx+sJQvECA5cBdu7Rh3EZkGjjSob2r\\nsdaacwjBFAhfCMAp5U3LCSLb/rHLub9Ihv2RkOHJNZ4Dj+TNwT6O9S/i7Nlx4fz39yCXkEPuxNo0\\nn+shPLUwJn4s5kKSPFC4yiCAKoBrA/8SgMgJspRGWK6KylryMxZLZEi+gmPqVw2AkCpHN+QsCsaS\\nG0AkRluHLJZ92PLRD4pCOu30CycZ3Zfzck1ErKLwplcI2Fk8bLeBOjukQW3B98IUC9eRYFJzdhpv\\nGpgASKMXQL1b9Jlumt6oEAzuCOlWaZz7CK7VRlzIIbQs4tqHihs/V28zpwN/7LgvfS6u0NQMewmX\\nxu0F4IzJfPVYRueNTr5YJLFdRP0Mjjo8kF61h9nUEgm8Mggigl2qXWn1iIFk69xFSKHfgf9UMiCu\\nB7m1RnW1KQr11Wz6qJABmngpcLpE6S01rCUEM3Nw9zaTu6tQHHNhbnmd3ZnWZ8VA70RDQ8VC8s/M\\n+d8IHODQdfy6PhWqIsfqavwnQKKanEfuaGTKqYzzRHTmHIiFyvS++RZySR5zidS4lA+b+Ol9XZx3\\nnpUr6BlhHD1e3BH587omYbHKh4DSKB4psFz8MfTVdfuJICCe2kOP9Ixrw/VJXlcga+9rMVHFwoFY\\ngLrI3NZLJpPchH0P02/1le0im9KQpgorixgZoYsK8132XTEg8USiF7oV0rKFbyt0etFFSJi2XMhf\\nnIKTOkWeHoXTO075JyVtLQ7wMKKyCDsR5/fkuVTzdULp4/ZkXZ/J5xu+Mpq/YKnsVgcPS2T+jzgW\\n0ChAPC3rbxOduYOj/L6wIlblY/k4i0fh0C8LAwyM\\n\",\"ivSpec\":\"QN8M6gu9mFSYrm/ebUhgHA==\\n\",\"key\":\"tNIjAFDVaRCSPuvJCkFABzCRC9H5WQlZbXW00Ezxd+4FkUkAKA/gBdD/bxbaDbJATPCYjZY9d1X3\\nsRz0/79NMdHQqEsxPQ4lSmadsb9V1wCgRublFNKpvO6/fm3+ReONMV3NUSAL9k+k1uHYyYNAWdXW\\nCO/jei3SqxsHyglMEbI=\\n\",\"sign\":\"Lp0OmZ3YUFXifgNffPjuYu1FsbzDy+kQgQppM0hkksVzvftnnNPWN1py+jYgtikhU9k5PHENWNxl\\nuvKZ9mLYqD+jyK+c8pd3uP7L9UMC5uzQfxlgpOR4Ca+6z5eRMtLgNmfcfXmI6GHerH5Z86WC1eq3\\nlJs8LUn9BMmCsYVfM4U=\\n\"}";
        Verifier verifier = new Verifier();
        verifier.verify(encryptedLp);
    }

    public void setNumberToSpeak(int number){
        numberToSpeak.setText(Integer.toString(number));
    }


    // Record audio permission methods
    boolean hasRecordAudioPersmission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
    }

    private void showMissingAudioPermissionToast() {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, R.string.missing_audio_permission, duration);
        toast.show();
    }


}
