package irie.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import irie.Main;
import irie.models.DiffieHellman;
import irie.models.SQLStatements;
import irie.services.TransformEngine;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.TrayNotification;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private TextArea ta_plaintext;

    @FXML
    private TextArea ta_cipher_text;

    @FXML
    private TextArea ta_dec_plaintext;

    @FXML
    private JFXTextField enc_txt_entry;

    @FXML
    private JFXTextField dec_txt_entry;

    @FXML
    private Spinner <Integer> enc_secret_key;

    @FXML
    private Spinner <Integer> dec_secret_key;

    @FXML
    private CheckBox cb_allow;

    @FXML
    private JFXButton btn_edit;

    @FXML
    private JFXButton btn_delete;

    @FXML
    private JFXButton btn_encrypt;

    @FXML
    private JFXButton btn_reset;

    @FXML
    private Tab tab_encrypt;

    @FXML
    private JFXTabPane tabpane_main;

    @FXML
    private JFXButton btn_update;

    private TransformEngine transformEngine = new TransformEngine();
    private byte[] coded_text, secret_key;
    private int secret_dh;
    private String decoded_message;
    private Image image = new Image("irie/views/assets/encrypted.png");
    private Image dec_image = new Image("irie/views/assets/decrypted.png");
    private Image up_image = new Image("irie/views/assets/updated.png");
    private Image delete_image = new Image("irie/views/assets/deleted.png");

    private DiffieHellman diffieHellman = new DiffieHellman();
    private int shared_key = diffieHellman.generateSharedSecret();
    private String shared_secret = String.valueOf(shared_key);

    SignUpController signUpController = new SignUpController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        SQLStatements sqlStatements = new SQLStatements();

        // Initializing spinner values for secret key on both encryption and decryption tabs
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99999);
        SpinnerValueFactory<Integer> dec_valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99999);
        enc_secret_key.setValueFactory(valueFactory);
        dec_secret_key.setValueFactory(dec_valueFactory);

        String table_query = "CREATE TABLE IF NOT EXISTS Entries (\n" +
                " Username String NOT NULL, \n" +
                " Entry_Title String NOT NULL, \n" +
                " Secret_Key Blob NOT NULL, \n" +
                " Encrypted_Message Blob NOT NULL, \n" +
                " Shared_Secret int NOT NULL);";

        // Creates the Credentials Table
        try{
            PreparedStatement preparedStatement;
            try{
                preparedStatement = SQLStatements.connection.prepareStatement(table_query);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void encryptPlainText() throws Exception {

        // Inserting into the Entries table
        if(!(enc_txt_entry.getText().isEmpty() || ta_plaintext.getText().isEmpty())){

            // Encrypting plain text of message title, secret key and message itself
            coded_text = transformEngine.encryptMessage(shared_secret, ta_plaintext.getText());
            secret_key = transformEngine.encryptMessage(shared_secret, String.valueOf(enc_secret_key.getValue()));

            ta_cipher_text.setText(String.valueOf(coded_text));

            TrayNotification trayNotification = new TrayNotification();
            trayNotification.setMessage("Entry text content and secret key encrypted!");
            trayNotification.setAnimationType(AnimationType.POPUP);
            trayNotification.setRectangleFill(Paint.valueOf("#00e676"));
            trayNotification.setTitle("Encrypted Successfully");
            trayNotification.setImage(image);
            trayNotification.showAndDismiss(Duration.seconds(3));

            try{
                PreparedStatement preparedStatement = null;

                String query = "INSERT INTO Entries (Username, Entry_Title, Secret_Key, Encrypted_Message, Shared_Secret)" +
                        " " +
                        "VALUES (?, ?, ?, ?, ?);";

                preparedStatement = SQLStatements.connection.prepareStatement(query);
                preparedStatement.setString(1, LoginController.username);
                preparedStatement.setString(2, enc_txt_entry.getText().toLowerCase());
                preparedStatement.setBytes(3, secret_key);
                preparedStatement.setBytes(4, coded_text);
                preparedStatement.setInt(5, Integer.valueOf(shared_secret));

                preparedStatement.executeUpdate();
            }
            catch (Exception e){ }
        }

        else {
            TrayNotification trayNotification = new TrayNotification();
            trayNotification.setMessage("Entry content could not be encrypted. Fill in input fields");
            trayNotification.setAnimationType(AnimationType.POPUP);
            trayNotification.setRectangleFill(Paint.valueOf("#d50000"));
            trayNotification.setTitle("Error Encrypting...");
            trayNotification.setImage(LoginController.error_image);
            trayNotification.showAndDismiss(Duration.seconds(3));
        }
    }

    @FXML
    void decryptCodedText() {

        if(!(dec_txt_entry.getText().isEmpty())) {
            // Selecting cipher text based on entry title
            try {
                String query = "SELECT * FROM Entries WHERE Username=? AND Entry_Title=?";
                PreparedStatement preparedStatement = SQLStatements.connection.prepareStatement(query);
                preparedStatement.setString(1, LoginController.username);
                preparedStatement.setString(2, dec_txt_entry.getText().toLowerCase());

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()){
                    coded_text = resultSet.getBytes("Encrypted_Message");
                    secret_key = resultSet.getBytes("Secret_Key");
                    secret_dh = resultSet.getInt("Shared_Secret");

                /*
                Need to decrypt secret key first then
                convert to integer and compare to see if keys are right before decrypting
                */
                    String decoded_key = transformEngine.decryptMessage(String.valueOf(secret_dh), secret_key);

                    if(Integer.valueOf(decoded_key).equals(dec_secret_key.getValue())){
                        decoded_message = transformEngine.decryptMessage(String.valueOf(secret_dh), coded_text);
                        ta_dec_plaintext.setText(decoded_message);
                        cb_allow.setDisable(false);

                        TrayNotification trayNotification = new TrayNotification();
                        trayNotification.setMessage("Plaintext restored!");
                        trayNotification.setAnimationType(AnimationType.POPUP);
                        trayNotification.setRectangleFill(Paint.valueOf("#00e676"));
                        trayNotification.setTitle("Decrypted Successfully");
                        trayNotification.setImage(dec_image);
                        trayNotification.showAndDismiss(Duration.seconds(3));
                    }

                }

                preparedStatement.close();
                resultSet.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        else {
            TrayNotification trayNotification = new TrayNotification();
            trayNotification.setMessage("Entry content could not be decrypted. Check secret key or title");
            trayNotification.setAnimationType(AnimationType.POPUP);
            trayNotification.setRectangleFill(Paint.valueOf("#d50000"));
            trayNotification.setTitle("Error Decrypting...");
            trayNotification.setImage(LoginController.error_image);
            trayNotification.showAndDismiss(Duration.seconds(3));
        }
    }

    @FXML
    private void allowModification(){
        if(cb_allow.isSelected()){
            btn_edit.setDisable(false);
            btn_delete.setDisable(false);
        }
        else{
            btn_delete.setDisable(true);
            btn_edit.setDisable(true);
        }
    }

    @FXML
    private void allowModification2(KeyEvent event){
        if(event.getCode() == (KeyCode.ENTER) && cb_allow.isSelected()){
            btn_edit.setDisable(false);
            btn_delete.setDisable(false);
        }
        else{
            btn_edit.setDisable(true);
            btn_delete.setDisable(true);
        }
    }

    @FXML
    private void editMessage() {
        /*
        Function called when edit button on Decrypt Tab is pressed
        Tab pane's focus is shifted to Encrypt Tab
         */
        ta_plaintext.setText(decoded_message);
        enc_txt_entry.setText("");
        tabpane_main.getSelectionModel().select(tab_encrypt);
        enc_txt_entry.setText(dec_txt_entry.getText());
        enc_secret_key.getValueFactory().setValue(dec_secret_key.getValue());

        // After making editable, disable secret key and entry title fields
        enc_txt_entry.setEditable(false);
        enc_secret_key.setDisable(true);
        btn_encrypt.setDisable(true);
        btn_update.setDisable(false);
        btn_reset.setDisable(false);

        // Resetting Decrypt Message Tab back to normal
        reset2();
    }

    @FXML
    private void updateEntry() {
        try {
            coded_text = transformEngine.encryptMessage(String.valueOf(secret_dh), ta_plaintext.getText());
            ta_cipher_text.setText(String.valueOf(coded_text));

            String query = "UPDATE Entries SET Encrypted_Message = ? WHERE Username = ? AND (Entry_Title = ? AND " +
                    "Secret_Key = ?)";
            PreparedStatement preparedStatement = SQLStatements.connection.prepareStatement(query);
            preparedStatement.setBytes(1, coded_text);
            preparedStatement.setString(2, LoginController.username);
            preparedStatement.setString(3, enc_txt_entry.getText().toLowerCase());
            preparedStatement.setBytes(4, secret_key);

            preparedStatement.executeUpdate();

            TrayNotification trayNotification = new TrayNotification();
            trayNotification.setMessage("Entry content updated!!!");
            trayNotification.setAnimationType(AnimationType.POPUP);
            trayNotification.setRectangleFill(Paint.valueOf("#00e676"));
            trayNotification.setTitle("Update Successful");
            trayNotification.setImage(up_image);
            trayNotification.showAndDismiss(Duration.seconds(3));

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteEntry() {
        try {
            String query = "DELETE FROM Entries WHERE Username = ? AND (Entry_Title = ? AND Secret_Key = ?)";
            PreparedStatement preparedStatement = SQLStatements.connection.prepareStatement(query);
            preparedStatement.setString(1, LoginController.username);
            preparedStatement.setString(2, dec_txt_entry.getText().toLowerCase());
            preparedStatement.setBytes(3, secret_key);

            preparedStatement.executeUpdate();

            TrayNotification trayNotification = new TrayNotification();
            trayNotification.setMessage("Entry deleted. It can not be retrieved!!!");
            trayNotification.setAnimationType(AnimationType.POPUP);
            trayNotification.setRectangleFill(Paint.valueOf("#37474f"));
            trayNotification.setTitle("Delete Successful");
            trayNotification.setImage(delete_image);
            trayNotification.showAndDismiss(Duration.seconds(3));

            reset2();
        }
        catch (Exception e) {

        }
    }

    @FXML
    private void reset(){
        enc_secret_key.getValueFactory().setValue(1);
        enc_secret_key.setDisable(false);
        enc_secret_key.setEditable(true);
        enc_txt_entry.setText("");
        enc_txt_entry.setEditable(true);
        ta_plaintext.setText("");
        ta_cipher_text.setText("");

        btn_encrypt.setDisable(false);
        btn_update.setDisable(true);
        btn_reset.setDisable(true);
    }

    @FXML
    private void reset2(){
        dec_txt_entry.setText("");
        dec_secret_key.getValueFactory().setValue(1);
        ta_dec_plaintext.setText("");
        cb_allow.setSelected(false);
        cb_allow.setDisable(true);

        if(!(cb_allow.isSelected())) {
            btn_edit.setDisable(true);
            btn_delete.setDisable(true);
        }
    }

    @FXML
    private void signOut() {
        try{
            Main.displaySignOut();
            Main.home_stage.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
